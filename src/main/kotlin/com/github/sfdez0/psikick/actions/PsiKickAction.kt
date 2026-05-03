package com.github.sfdez0.psikick.actions

import com.github.sfdez0.psikick.settings.PsiKickSettings
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.platform.ide.progress.withBackgroundProgress
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.markup.EffectType
import com.intellij.openapi.editor.markup.HighlighterLayer
import com.intellij.openapi.editor.markup.HighlighterTargetArea
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.ui.JBColor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration
import kotlin.math.abs

/**
 * Represents a code smell detected by Gemini.
 *
 * @property line the line number where the code smell begins.
 * @property code the code fragment causing the code smell.
 * @property message the message describing the code smell.
 */
data class CodeSmell(val line: Int, val code: String, val message: String)

/**
 * Represents the request body for the Gemini API.
 *
 * @property contents the content of the request.
 * @property generationConfig the generation configuration.
 */
data class GeminiRequest(val contents: List<GeminiContent>, val generationConfig: GenerationConfig)

/**
 * Represents the content of the request body.
 *
 * @property parts the parts of the content.
 */
data class GeminiContent(val parts: List<GeminiPart>)

/**
 * Represents a part of the content.
 *
 * @property text the text of the part.
 */
data class GeminiPart(val text: String)

/**
 * Represents the generation configuration.
 *
 * @property responseMimeType the MIME type of the response.
 */
data class GenerationConfig(val responseMimeType: String = "application/json")

/**
 * Action that uses a Gemini API to analyze Kotlin code and detect code smells.
 */
class PsiKickAction : AnAction() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val log = Logger.getInstance(PsiKickAction::class.java)
    private val prompt = """
            You are an expert Kotlin static code analyzer (linter).
            Analyze the Kotlin code attached and detect the following "code smells":
            - Double Bang (!! operator)
            - Deep Nesting (more than 3 levels of nested blocks)
            - Empty catch blocks
            - Lack of immutability (var variables when their value is never reasigned)
            Return ONLY a valid JSON array. Do not use markdown code blocks (```json)
            or any conversational text.
            Each object in the JSON array must have the following structure:
            - "line": (Int) The exact line number where the code smell begins (starting from 1).
            - "code": (String) The exact code fragment causing the code smell.
            - "message": (String) A human-readable short description of the code smell.
            Return a maximum of 5 code smells prioritizing the most cricital ones. Keep
            the "message" under 15 words
            
            Code to analyze:
        """.trimIndent()

    /**
     * The action is updated in the background thread.
     */
    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }

    /**
     * Method called to check if the action should be enabled or not.
     * * The action is enabled if there is a project and an editor open with a Kotlin file.
     *
     * @param e The event that triggered the action.
     */
    override fun update(e: AnActionEvent) {
        val project = e.project
        val editor = e.getData(CommonDataKeys.EDITOR)
        val virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE)

        val isKotlinFile = virtualFile?.extension == "kt"

        e.presentation.isEnabledAndVisible = project != null && editor != null && isKotlinFile
    }

    /**
     * Method called when the action is triggered.
     * * The action is executed in the background thread.
     * * The action is updated in the EDT.
     *
     * @param e The event that triggered the action.
     */
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        val psiFile = e.getData(CommonDataKeys.PSI_FILE) ?: return

        val fileContent = psiFile.text
        val fileName = psiFile.name

        scope.launch {
            try {
                // Perform the analysis in background (with progress bar)
                val smells = withBackgroundProgress(project, "PsiKick: Analyzing File...") {
                    analyzeCode(fileContent, fileName)
                }

                // Update UI on the main thread
                withContext(Dispatchers.Main) {
                    if (!editor.isDisposed) {
                        applyHighlights(editor, smells)
                    }
                }
            } catch (ex: Exception) {
                log.error("PsiKick: Error in coroutine execution: ${ex.message}")
            }
        }
    }

    /**
     * Analyzes the collected information using an AI API and returns a list of code smells.
     *
     * @param codeText The text (code) to analyze.
     * @param fileName The name of the file being analyzed.
     * @return [List] of [CodeSmell].
     */
    private fun analyzeCode(codeText: String, fileName: String): List<CodeSmell> {
        log.info("PsiKick: Analyzing file $fileName using ${PsiKickSettings.selectedModel.displayName}")

        val token = PsiKickSettings.apiToken
        if (token.isNullOrBlank()) {
            log.info("PsiKick: Analysis aborted, API token not found")
            return emptyList()
        }

        val codeSmells = mutableListOf<CodeSmell>()

        // Build the prompt with the code text
        val finalPrompt = buildString {
            append(prompt)
            append(codeText)
        }

        // Build the request body
        val requestBody = GeminiRequest(
            contents = listOf(GeminiContent(listOf(GeminiPart(text = finalPrompt)))),
            generationConfig = GenerationConfig()
        )

        // Build the HTTP request including the API token
        val request = HttpRequest.newBuilder()
            .uri(URI.create("https://generativelanguage.googleapis.com/v1beta/models/${PsiKickSettings.selectedModel.apiId}:generateContent"))
            .header("Content-Type", "application/json")
            .header("x-goog-api-key", token)
            .timeout(Duration.ofSeconds(120))
            .POST(HttpRequest.BodyPublishers.ofString(Gson().toJson(requestBody)))
            .build()

        // Build the HTTP client
        val client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build()

        try {
            val response = client.send(request, HttpResponse.BodyHandlers.ofString())

            if (response.statusCode() == 200) {
                log.info("PsiKick: Analysis completed")

                // Get the JSON content from the response
                val jsonResponse = Gson().fromJson(response.body(), JsonObject::class.java)
                val textResponse = jsonResponse
                    .getAsJsonArray("candidates")[0].asJsonObject
                    .getAsJsonObject("content")
                    .getAsJsonArray("parts")[0].asJsonObject
                    .get("text").asString

                // Get the code smells array
                val smellsArray = Gson().fromJson(textResponse, JsonArray::class.java)
                for (smell in smellsArray) {
                    val obj = smell.asJsonObject

                    // Add the code smell to the response list
                    codeSmells.add(
                        CodeSmell(
                            obj.get("line").asInt,
                            obj.get("code").asString,
                            "PsiKick: " + obj.get("message").asString
                        )
                    )
                }
            } else {
                log.warn("PsiKick: Error analyzing file $fileName - Request status Code: ${response.statusCode()}")
            }
        } catch (e: Exception) {
            log.error("PsiKick: Error analyzing file $fileName - Exception: ${e.localizedMessage}")
        }

        return codeSmells
    }

    private fun applyHighlights(editor: Editor, annotationResult: List<CodeSmell>) {
        val markupModel = editor.markupModel

        for (smell in annotationResult) {
            // Get target line (AI -1) forcing min-max range
            val targetLine = (smell.line - 1).coerceIn(0, maxOf(0, editor.document.lineCount - 1))

            // Get the theoretical code smell line start position
            val theoreticalOffset = editor.document.getLineStartOffset(targetLine)

            // Create Regex including any number of whitespaces and non-breaking spaces
            val parts = smell.code.trim().split(Regex("[\\s\\u00A0]+"))
            val regexPattern = parts.joinToString("[\\s\\u00A0]+") { Regex.escape(it) }
            val regex = Regex(regexPattern)

            // Find matches in the file
            val matches = regex.findAll(editor.document.text)

            // Find the closest match to the reported line (in case AI output is off)
            val bestMatch = matches.minByOrNull { abs(it.range.first - theoreticalOffset) }

            if (bestMatch != null) {
                val textAttributes = TextAttributes().apply {
                    effectType = EffectType.WAVE_UNDERSCORE
                    effectColor = JBColor.ORANGE
                    errorStripeColor = JBColor.ORANGE
                }

                val highlighter = markupModel.addRangeHighlighter(
                    bestMatch.range.first,
                    bestMatch.range.last + 1,
                    HighlighterLayer.WARNING,
                    textAttributes,
                    HighlighterTargetArea.EXACT_RANGE
                )

                highlighter.errorStripeTooltip = smell.message
            } else {
                log.warn("PsiKick: Could not find match for code smell: ${smell.message}")
            }
        }
    }
}
