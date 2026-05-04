package org.jetbrains.plugins.template.settings

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBPasswordField
import com.intellij.util.ui.FormBuilder
import org.jetbrains.plugins.template.ComposeTemplateBundle
import org.jetbrains.plugins.template.model.LLMModel
import org.jetbrains.plugins.template.repository.settings.SettingsService
import org.jetbrains.plugins.template.repository.settings.SettingsServiceImpl
import javax.swing.JComponent
import javax.swing.JPanel

class SettingsConfigurable : Configurable {

    private var apiKeyField: JBPasswordField? = null
    private var modelComboBox: ComboBox<LLMModel>? = null

    private val settingsService: SettingsService
        get() = SettingsServiceImpl.getInstance()

    override fun getDisplayName(): String = ComposeTemplateBundle.message("settings.display.name")

    override fun createComponent(): JComponent {
        val apiKey = createApiKeyField()
        val modelBox = createModelComboBox()

        return FormBuilder.createFormBuilder()
            .addLabeledComponent(JBLabel(ComposeTemplateBundle.message("settings.api_key.label")), apiKey)
            .addLabeledComponent(JBLabel(ComposeTemplateBundle.message("settings.model.label")), modelBox)
            .addComponentFillVertically(JPanel(), 0)
            .panel
    }

    override fun isModified(): Boolean {
        val currentApiKey = String(apiKeyField?.password ?: charArrayOf())
        val currentModel = (modelComboBox?.selectedItem as? LLMModel) ?: ""
        return currentApiKey != settingsService.getApiKey() ||
                currentModel != settingsService.getSelectedModel()
    }

    override fun apply() {
        val apiKey = String(apiKeyField?.password ?: charArrayOf())
        val model = (modelComboBox?.selectedItem as? LLMModel) ?: LLMModel.GPT_4_1

        settingsService.setApiKey(apiKey)
        settingsService.setModel(model)
    }

    override fun reset() {
        apiKeyField?.text = settingsService.getApiKey()
        modelComboBox?.selectedItem = settingsService.getSelectedModel()
    }

    override fun disposeUIResources() {
        apiKeyField = null
        modelComboBox = null
    }

    private fun createApiKeyField(): JBPasswordField =
        JBPasswordField().also { field ->
            field.text = settingsService.getApiKey()
            field.columns = 40
            apiKeyField = field
        }

    private fun createModelComboBox(): ComboBox<LLMModel> =
        ComboBox(LLMModel.entries.toTypedArray()).also { box ->
            box.selectedItem = settingsService.getSelectedModel()
            modelComboBox = box
        }
}
