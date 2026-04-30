package com.github.sfdez0.psikick.services

import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project

@Service(Service.Level.PROJECT)
class PsiKickService(project: Project) {

    fun processChatMessage(message: String): String {
        return "Message received: '$message'.\n"
    }
}
