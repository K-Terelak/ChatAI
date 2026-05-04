package org.jetbrains.plugins.template.ui.theme

import com.intellij.DynamicBundle
import org.jetbrains.annotations.Nls
import org.jetbrains.annotations.PropertyKey

private const val BUNDLE = "messages.ComposeTemplateBundle"

object ComposeTemplateBundle {
    private val instance = DynamicBundle(ComposeTemplateBundle::class.java, BUNDLE)

    @JvmStatic
    fun message(key: @PropertyKey(resourceBundle = BUNDLE) String, vararg params: Any?): @Nls String {
        return instance.getMessage(key, *params)
    }
}
