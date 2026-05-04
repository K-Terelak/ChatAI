package org.jetbrains.plugins.template.repository.github_models

interface GithubModelsService {
    suspend fun sendMessage(userMessage: String): Result<String>
    fun isConfigured(): Boolean
}
