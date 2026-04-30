package com.github.sfdez0.psikick.startup

import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity

class PsiKickActivity : ProjectActivity {

    override suspend fun execute(project: Project) {
        thisLogger().info("PsiKickActivity: ${project.name}")
    }
}