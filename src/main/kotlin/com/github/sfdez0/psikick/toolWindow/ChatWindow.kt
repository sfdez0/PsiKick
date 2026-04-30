package com.github.sfdez0.psikick.toolWindow

import com.github.sfdez0.psikick.services.PsiKickService
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTextArea
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.AlignY
import com.intellij.ui.dsl.builder.panel
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import javax.swing.JButton
import javax.swing.JPanel

class ChatWindow(private val project: Project) {
    /**
     * Chat history
     */
    private val chatHistory = JBTextArea().apply {
        isEditable = false
        lineWrap = true
        wrapStyleWord = true
        text = "PsiKick AI: Hi!.\n"
    }

    /**
     * Chat input
     */
    private val inputField = JBTextArea(3, 20).apply {
        lineWrap = true
        wrapStyleWord = true

        // Send with ctlr+enter
        addKeyListener(object: KeyAdapter() {
            override fun keyPressed(e: KeyEvent) {
                if (e.isControlDown && e.keyCode == KeyEvent.VK_ENTER) {
                    sendMessage()
                }
            }
        })
    }

    /**
     * Send button
     */
    private val sendButton = JButton("Send").apply {
        addActionListener {
            sendMessage()
        }
    }

    /**
     * UI Form
     */
    val content: JPanel = panel {
        row {
            cell(JBScrollPane(chatHistory))
                .align(Align.FILL)
        }.resizableRow()

        row {
            cell(JBScrollPane(inputField))
                .align(Align.FILL)
            cell(sendButton)
                .align(AlignY.BOTTOM)
        }
    }

    /**
     * Send message
     */
    private fun sendMessage() {
        val message = inputField.text
        if (message.isNotBlank()) {
            chatHistory.append("\nUser: $message\n")
            inputField.text = ""

            val psiKickService = project.service<PsiKickService>()
            val response = psiKickService.processChatMessage(message)

            chatHistory.append("PsiKick AI: $response\n")

            chatHistory.caretPosition = chatHistory.document.length
        }
    }
}