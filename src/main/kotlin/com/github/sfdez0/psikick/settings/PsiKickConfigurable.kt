package com.github.sfdez0.psikick.settings

import com.github.sfdez0.psikick.models.GeminiModel
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.SimpleListCellRenderer
import com.intellij.ui.components.JBPasswordField
import com.intellij.ui.dsl.builder.panel
import javax.swing.JComponent
import javax.swing.SwingUtilities

class PsiKickConfigurable : Configurable {
    /**
     * The saved API token
     */
    private var savedToken: String = ""

    /**
     * The password field for the API token.
     */
    private val tokenField = JBPasswordField()

    /**
     * The combo box for selecting the model.
     */
    private val modelComboBox = ComboBox(GeminiModel.entries.toTypedArray()).apply {
        renderer = SimpleListCellRenderer.create("") { it.displayName }
    }

    override fun getDisplayName(): String = "PsiKick"

    override fun isModified(): Boolean {
        // Check and return if the token or model has been changed
        val currentToken = String(tokenField.password)
        val currentModel = modelComboBox.selectedItem as GeminiModel

        return currentToken != savedToken || currentModel != PsiKickSettings.selectedModel
    }

    override fun createComponent(): JComponent {
        return panel {
            row("Model:"){
                cell(modelComboBox)
                    .comment("Choose a model that fits your API quota and performance needs")
            }
            row("Gemini API key:") {
                cell(tokenField)
                    .comment("Get your API key from Google AI Studio")
            }
        }
    }

    override fun apply() {
        // Save the token and model
        val newToken = String(tokenField.password)
        PsiKickSettings.apiToken = newToken
        savedToken = newToken

        PsiKickSettings.selectedModel = modelComboBox.item
    }

    override fun reset() {
        tokenField.text = ""
        savedToken = ""

        // On a background thread, ask for the token
        ApplicationManager.getApplication().executeOnPooledThread {
            val loadedToken = PsiKickSettings.apiToken ?: ""

            // Update the UI on the main thread
            SwingUtilities.invokeLater {
                savedToken = loadedToken
                tokenField.text = loadedToken
            }
        }

        modelComboBox.item = PsiKickSettings.selectedModel
    }
}
