package com.github.sfdez0.psikick.settings

import com.github.sfdez0.psikick.models.GeminiModel
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil

@State(
    name = "PsiKickConfig",
    storages = [Storage("PsiKickConfig.xml")]
)
class PsiKickState : PersistentStateComponent<PsiKickState> {
    /**
     * The saved model for the Gemini API.
     */
    var selectedModel: GeminiModel = GeminiModel.GEMINI_3_1_FLASH_LITE_PREVIEW

    override fun getState(): PsiKickState = this

    override fun loadState(state: PsiKickState) {
        XmlSerializerUtil.copyBean(state, this)
    }

    companion object {
        val instance: PsiKickState
            get() = ApplicationManager.getApplication().getService(PsiKickState::class.java)
    }
}