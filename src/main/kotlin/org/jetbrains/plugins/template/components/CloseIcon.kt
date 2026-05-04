package org.jetbrains.plugins.template.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.HoverInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.semantics.Role
import org.jetbrains.jewel.ui.component.Icon
import org.jetbrains.plugins.template.ComposeTemplateBundle
import org.jetbrains.plugins.template.chatApp.ChatAppIcons

@Composable
fun CloseIcon(onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    var hovered by remember { mutableStateOf(false) }

    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect {
            when (it) {
                is HoverInteraction.Enter -> hovered = true
                is HoverInteraction.Exit -> hovered = false
            }
        }
    }

    Icon(
        key = if (hovered) ChatAppIcons.CloseSmallHovered else ChatAppIcons.CloseSmall,
        contentDescription = ComposeTemplateBundle.message("weather.app.clear.button.content.description"),
        modifier = Modifier
            .pointerHoverIcon(PointerIcon.Default)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.Button,
            ) { onClick() },
    )
}