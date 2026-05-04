package org.jetbrains.plugins.template.model

enum class LLMModel(val modelId: String, val displayName: String) {
    GPT_4_1(modelId = "gpt-4.1", displayName = "GPT 4.1");

    // default comboBox label
    override fun toString(): String = displayName
}