package org.jetbrains.plugins.template.repository.settings

import org.jetbrains.plugins.template.model.LLMModel

interface SettingsService {
    fun getApiKey(): String
    fun getSelectedModel(): LLMModel
    fun setApiKey(apiKey: String)
    fun setModel(model: LLMModel)
    fun hasApiKey(): Boolean
}

