package com.github.sfdez0.psikick.inspections

import com.github.sfdez0.psikick.settings.PsiKickSettings
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.ExternalAnnotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiFile
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration

/**
 * Represents the file to be analyzed.
 *
 * @property code the code to be analyzed.
 * @property fileName the name of the file.
 */
data class File(val code: String, val fileName: String)

/**
 * Represents a code smell detected by Gemini.
 *
 * @property startOffset the start offset of the code smell.
 * @property endOffset the end offset of the code smell.
 * @property message the message describing the code smell.
 */
data class CodeSmell(val startOffset: Int, val endOffset: Int, val message: String)

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
 * External annotator that uses a Gemini API to analyze Kotlin code and detect code smells.
 */
class PsiKickCodeSmellAnnotator : ExternalAnnotator<File, List<CodeSmell>>() {
    companion object {
        private const val PROMPT = """
            You are an expert Kotlin static code analyzer (linter).
            Analyze the Kotlin code attached and detect the following "code smells":
            - Double Bang (!! operator)
            - Deep Nesting (more than 3 levels of nested blocks)
            - Empty catch blocks
            - Lack of immutability (var variables when their value is never reasigned)
            Return ONLY a valid JSON array. Do not use markdown code blocks (```json)
            or any conversational text.
            Each object in the JSON array must have the following structure:
            - "start": (Int) The exact character index where the code smell begins (based on the provided code).
            - "end": (Int) The exact character index where the code smell ends.
            - "message": (String) A human-readable short description of the code smell.
            Return a maximum of 5 code smells prioritizing the most cricital ones. Keep
            the "message" under 15 words
            
            Code to analyze:
        """

        private val LOG = Logger.getInstance(PsiKickCodeSmellAnnotator::class.java)
    }

    /**
     * Collects information about the file to be analyzed.
     *
     * @param file the file to be analyzed.
     * @return [File] the collected information.
     */
    override fun collectInformation(file: PsiFile): File {
        return File(file.text, file.name)
    }

    /**
     * Analyzes the collected information using an AI API and returns a list of code smells.
     *
     * @param collectedInfo the collected information.
     * @return [List] of [CodeSmell].
     */
    override fun doAnnotate(collectedInfo: File): List<CodeSmell> {
        LOG.info("PsiKick: Analyzing file ${collectedInfo.fileName}")
        val aiResponse = mutableListOf<CodeSmell>()

        // Get the API token from the settings
        val token = PsiKickSettings.apiToken
        if (token.isNullOrBlank()) {
            LOG.info("PsiKick: Analysis aborted, API token not found")
            return aiResponse
        }

        // Build the prompt with the code to analyze
        val finalPrompt = buildString {
            append(PROMPT)
            append(collectedInfo.code)
        }

        // Build the request body with the prompt and API key
        val requestBody = GeminiRequest(
            contents = listOf(GeminiContent(listOf(GeminiPart(text = finalPrompt)))),
            generationConfig = GenerationConfig()
        )

        // Build the HTTP request including the token
        val request = HttpRequest.newBuilder()
            .uri(URI.create("https://generativelanguage.googleapis.com/v1beta/models/gemini-3-flash-preview:generateContent"))
            .header("Content-Type", "application/json")
            .header("x-goog-api-key", token) // Google token header
            .timeout(Duration.ofSeconds(120)) // TODO set proper timeout
            .POST(HttpRequest.BodyPublishers.ofString(Gson().toJson(requestBody)))
            .build()

        // Build the HTTP client
        val client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build()

        try {
            val response = client.send(request, HttpResponse.BodyHandlers.ofString())

            if (response.statusCode() == 200) {
                LOG.info("PsiKick: Analysis completed")

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
                    aiResponse.add(
                        CodeSmell(
                            obj.get("start").asInt,
                            obj.get("end").asInt,
                            "PsiKick: " + obj.get("message").asString
                        )
                    )
                }
            } else {
                LOG.warn("PsiKick: Error analyzing file ${collectedInfo.fileName} - Request status Code: ${response.statusCode()}")
            }

        } catch (e: Exception) {
            LOG.error("PsiKick: Error analyzing file ${collectedInfo.fileName} - Exception: ${e.localizedMessage}")
        }

        return aiResponse
    }

    /**
     * Applies the code smell annotations to the file.
     *
     * @param file the file to be annotated.
     * @param annotationResult the list of code smells.
     * @param holder the annotation holder.
     */
    override fun apply(file: PsiFile, annotationResult: List<CodeSmell>, holder: AnnotationHolder) {
        // Create annotations for each code smell
        for (smell in annotationResult) {
            // Range of the code smell
            val range = TextRange(smell.startOffset, smell.endOffset)

            holder.newAnnotation(HighlightSeverity.WARNING, smell.message).range(range).create()
        }
    }
}
