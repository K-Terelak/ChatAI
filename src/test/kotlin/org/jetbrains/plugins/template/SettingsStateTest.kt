package org.jetbrains.plugins.template

import org.jetbrains.plugins.template.model.LLMModel
import org.jetbrains.plugins.template.model.SettingsState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SettingsStateTest {

    @Test
    fun defaults_useEmptyApiKeyAndSingleModel() {
        val state = SettingsState()

        assertTrue(state.apiKey.isEmpty())
        assertEquals(LLMModel.GPT_4_1, state.selectedModel)
    }

    @Test
    fun model_toStringUsesDisplayName() {
        assertEquals("GPT 4.1", LLMModel.GPT_4_1.toString())
    }
}

