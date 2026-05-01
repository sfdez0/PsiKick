package com.github.sfdez0.psikick.models

/**
 * Represents the Gemini models available for use in the plugin.
 */
enum class GeminiModel(val apiId: String, val displayName: String) {
    GEMINI_3_1_FLASH_LITE_PREVIEW("gemini-3.1-flash-lite-preview", "Gemini 3.1 Flash Lite Preview"),
    GEMINI_3_FLASH_PREVIEW("gemini-3-flash-preview", "Gemini 3.0 Flash Preview");

    override fun toString(): String = displayName
}