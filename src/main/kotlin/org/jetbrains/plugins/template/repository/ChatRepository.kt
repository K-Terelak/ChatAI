package org.jetbrains.plugins.template.repository

import com.intellij.openapi.components.Service
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import org.jetbrains.plugins.template.chatApp.model.ChatMessage
import org.jetbrains.plugins.template.repository.github_models.GithubModelsServiceImpl
import java.time.LocalDateTime

/**
 * Interface defining the contract for managing chat messages and interactions within a chat system.
 * Provides access to the flow of messages and supports operations for sending and editing chat messages.
 */
interface ChatRepositoryApi {
    /**
     * Flow that emits a list of chat messages.
     * Updates with new messages as they are received or edited.
     */
    val messagesFlow: StateFlow<List<ChatMessage>>

    /**
     * Sends a message with the provided content.
     *
     * @param messageContent The content of the message to be sent.
     */
    suspend fun sendMessage(messageContent: String)
}

@Service
class ChatRepository : ChatRepositoryApi {

    private val chatMessageFactory = ChatMessageFactory("AI Buddy", "Me")
    private val githubModelsService = GithubModelsServiceImpl.getInstance()
    private val _messages = MutableStateFlow(
        listOf(
            chatMessageFactory.createAIMessage(
                content = "Hello! I'm your AI Buddy. I'm here to help and chat with you about anything you'd like to discuss. How are you doing today?",
                timestamp = LocalDateTime.now(),
            ),
        )
    )

    override val messagesFlow: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    override suspend fun sendMessage(messageContent: String) {
        withContext(Dispatchers.IO) {
            try {
                _messages.value += chatMessageFactory.createUserMessage(messageContent)
                generateResponse(messageContent)
            } catch (e: Exception) {
                if (e is CancellationException) {
                    // In case the message sending is canceled before a response is generated,
                    // we remove a loading placeholder message
                    _messages.value = _messages.value.filter { !it.isAIThinkingMessage() }
                    throw e
                }
                e.printStackTrace()
            }
        }
    }

    private suspend fun generateResponse(message: String) {
        val aiThinkingMessage = chatMessageFactory.createAIThinkingMessage("Hm, let me think about that...")
        _messages.value += aiThinkingMessage

        val response: String = githubModelsService.sendMessage(message).fold(
            onSuccess = { response ->
                response
            },
            onFailure = { failure ->
                failure.printStackTrace()
                failure.message ?: "Something went wrong"
            }
        )
        val responseMessage = chatMessageFactory.createAIMessage(content = response)

        _messages.value = _messages.value.map { message ->
            if (message.id == aiThinkingMessage.id) responseMessage else message
        }
    }
}
