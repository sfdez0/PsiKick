package com.github.sfdez0.psikick.settings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.options.Configurable
import com.intellij.ui.components.JBPasswordField
import com.intellij.ui.dsl.builder.panel
import javax.swing.JComponent
import javax.swing.SwingUtilities

class PsiKickConfigurable : Configurable {
    /**
     * The password field for the API token.
     */
    private val tokenField = JBPasswordField()

    /**
     * The saved API token
     */
    private var savedToken: String = ""


    /**
     * Returns the display name of the plugin.
     *
     * @return [String] display name of the plugin
     */
    override fun getDisplayName(): String = "PsiKick"


    /**
     * Checks if the settings have been modified.
     *
     * @return [Boolean] true if the settings have been modified, false otherwise
     */
    override fun isModified(): Boolean {
        val currentToken = String(tokenField.password)
        return currentToken != savedToken
    }

    /**
     * Creates the settings panel.
     *
     * @return [JComponent] the settings panel
     */
    override fun createComponent(): JComponent {
        return panel {
            row("Gemini API Token:") {
                cell(tokenField)
                    .comment("Get your token from Google AI Studio")
            }
        }
    }

    /**
     * Applies the settings.
     */
    override fun apply() {
        val newToken = String(tokenField.password)
        PsiKickSettings.apiToken = newToken
        savedToken = newToken
    }

    /**
     * Loads the settings from the persistent storage and updates the UI.
     */
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
    }
}
