package com.github.sfdez0.psikick.toolWindow

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory

class PsiKickFactory : ToolWindowFactory {
    /**
     * This method is called when the user opens the tool window.
     *
     * @param project The project the tool window belongs to.
     * @param toolWindow The tool window instance.
     */
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        // Instantiate UI
        val chatWindow = ChatWindow(project)

        // Get the content factory instance
        val contentFactory = ContentFactory.getInstance()

        // Create content and add it to the tool window
        val content = contentFactory.createContent(chatWindow.content, "", false)

        // Set content
        toolWindow.contentManager.addContent(content)
    }
}
