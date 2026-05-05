package org.jetbrains.plugins.template.ui.review.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.plugins.template.model.CodeReviewIssue

@Composable
fun SeverityBadge(
    severity: CodeReviewIssue.IssueSeverity,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = severity.name,
            style = JewelTheme.defaultTextStyle.copy(fontSize = 9.sp)
        )
    }
}
