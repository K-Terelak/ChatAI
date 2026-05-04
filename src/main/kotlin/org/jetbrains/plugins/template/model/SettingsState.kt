package org.jetbrains.plugins.template.model

data class SettingsState(
    val apiKey: String = "",
    val selectedModel: LLMModel = LLMModel.GPT_4_1,
)