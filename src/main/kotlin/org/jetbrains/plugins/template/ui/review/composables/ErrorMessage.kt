package org.jetbrains.plugins.template.ui.review.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.plugins.template.ui.theme.ChatAppColors

@Composable
fun ErrorMessage(
    message: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .border(1.dp, ChatAppColors.Error.foreground, RoundedCornerShape(4.dp))
            .background(ChatAppColors.Error.background, RoundedCornerShape(4.dp))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "⚠️",
                fontSize = 16.sp
            )

            Text(
                text = message,
                style = JewelTheme.defaultTextStyle.copy(
                    color = ChatAppColors.Error.foreground,
                    fontSize = 12.sp
                ),
                modifier = Modifier.weight(1f)
            )
        }
    }
}
