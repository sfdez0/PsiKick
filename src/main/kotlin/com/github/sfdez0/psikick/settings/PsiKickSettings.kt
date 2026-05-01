package com.github.sfdez0.psikick.settings

import com.github.sfdez0.psikick.models.GeminiModel
import com.intellij.credentialStore.CredentialAttributes
import com.intellij.credentialStore.generateServiceName
import com.intellij.ide.passwordSafe.PasswordSafe

object PsiKickSettings {
    /**
     * Stores credential attributes used for managing the Gemini API token securely.
     */
    private val credentials = CredentialAttributes(
        generateServiceName("PsiKick", "GeminiApiToken")
    )

    /**
     * The chosen API token used to authenticate with the Gemini API.
     */
    var apiToken: String?
        get() = PasswordSafe.instance.getPassword(credentials)
        set(value) {
            PasswordSafe.instance.setPassword(credentials, value)
        }

    /**
     * The selected model for the Gemini API.
     */
    var selectedModel: GeminiModel
        get() = PsiKickState.instance.selectedModel
        set(value) {
            PsiKickState.instance.selectedModel = value
        }
}