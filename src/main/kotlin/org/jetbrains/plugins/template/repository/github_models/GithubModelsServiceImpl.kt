package org.jetbrains.plugins.template.repository.github_models

import org.jetbrains.plugins.template.repository.settings.SettingsServiceImpl
import com.intellij.openapi.components.service
import dev.langchain4j.model.github.GitHubModelsChatModel

class GithubModelsServiceImpl : GithubModelsService {

    private val settingsService
        get() = SettingsServiceImpl.getInstance()

    companion object {
        fun getInstance(): GithubModelsService = service<GithubModelsService>()
    }

    override fun isConfigured(): Boolean = settingsService.hasApiKey()

    override suspend fun sendMessage(userMessage: String): Result<String> {
        val prompt = userMessage.trim()
        if (prompt.isBlank()) {
            return Result.failure(IllegalArgumentException("Message cannot be empty"))
        }
        if (!isConfigured()) {
            return Result.failure(IllegalStateException("API key not configured"))
        }

        return runCatching { generateResponse(prompt) }
    }

    private fun generateResponse(prompt: String): String = buildChatModel()
        .chat(prompt)

    private fun buildChatModel(): GitHubModelsChatModel =
        GitHubModelsChatModel.builder()
            .gitHubToken(settingsService.getApiKey())
            .modelName(settingsService.getSelectedModel().modelId)
            .maxTokens(2048)
            .build()
}
