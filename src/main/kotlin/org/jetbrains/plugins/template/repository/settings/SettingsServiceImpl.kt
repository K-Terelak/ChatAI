package org.jetbrains.plugins.template.repository.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.service
import org.jetbrains.plugins.template.model.LLMModel
import org.jetbrains.plugins.template.model.SettingsState

@State(
    name = "ChatSettings",
    storages = [Storage("ChatSettings.xml")],
)
class SettingsServiceImpl : SettingsService, PersistentStateComponent<SettingsState> {

    private var state = SettingsState()

    companion object {
        fun getInstance(): SettingsService = service<SettingsService>()
    }

    override fun getState(): SettingsState = state

    override fun loadState(state: SettingsState) {
        this.state = state
    }

    override fun getApiKey(): String = state.apiKey

    override fun getSelectedModel(): LLMModel = state.selectedModel

    override fun setApiKey(apiKey: String) {
        state = state.copy(apiKey = apiKey.trim())
    }

    override fun setModel(model: LLMModel) {
        state = state.copy(selectedModel = model)
    }

    override fun hasApiKey(): Boolean = state.apiKey.isNotBlank()
}