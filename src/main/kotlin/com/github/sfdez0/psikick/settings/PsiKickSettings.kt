package com.github.sfdez0.psikick.settings

import com.intellij.credentialStore.CredentialAttributes
import com.intellij.credentialStore.generateServiceName
import com.intellij.ide.passwordSafe.PasswordSafe

object PsiKickSettings {
    /**
     * The credentials used to authenticate with the Gemini API.
     */
    private val credentials = CredentialAttributes(
        generateServiceName("PsiKick", "GeminiApiToken")
    )

    /**
     * The API token used to authenticate with the Gemini API
     */
    var apiToken: String?
        get() = PasswordSafe.instance.getPassword(credentials)
        set(value) {
            PasswordSafe.instance.setPassword(credentials, value)
        }
}