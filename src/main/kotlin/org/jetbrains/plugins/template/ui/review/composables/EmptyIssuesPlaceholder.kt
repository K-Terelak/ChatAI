package org.jetbrains.plugins.template.ui.review.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.plugins.template.ui.theme.ChatAppColors

@Composable
fun EmptyIssuesPlaceholder(
    placeholderText: String = "No code review issues yet. Send your changes for review!",
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = placeholderText,
            style = JewelTheme.defaultTextStyle.copy(
                color = ChatAppColors.Text.disabled,
                fontSize = 16.sp
            )
        )
    }
}
