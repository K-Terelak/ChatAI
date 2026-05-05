package org.jetbrains.plugins.template.ui.review.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.plugins.template.model.CodeReviewIssue
import org.jetbrains.plugins.template.ui.common.HorizontalDivider
import org.jetbrains.plugins.template.ui.theme.ChatAppColors

@Composable
fun CodeReviewIssueCard(
    issue: CodeReviewIssue,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, ChatAppColors.Text.disabled, RoundedCornerShape(6.dp))
            .background(ChatAppColors.Panel.background, RoundedCornerShape(6.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = issue.title)
                SeverityBadge(severity = issue.severity)
            }

            HorizontalDivider()

            Text(text = issue.description)

            if (issue.proposedFix.isNotBlank() || issue.gitDiff.isNotBlank()) {
                ExpandableSection(
                    headerLabel = "💡 Suggested fix",
                    initiallyExpanded = true
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {

                        if (issue.proposedFix.isNotBlank()) {
                            Text(text = issue.proposedFix)
                        }

                        if (issue.gitDiff.isNotBlank()) {
                            DiffView(
                                diff = issue.gitDiff,
                                filePath = issue.filePath,
                            )
                        }
                    }
                }
            }

            Text(
                text = issue.formattedTime(),
                style = JewelTheme.defaultTextStyle.copy(
                    color = ChatAppColors.Text.disabled,
                    fontSize = 10.sp
                ),
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/**
 * Renders a git diff with colored lines.
 * Shows [filePath] as a header instead of the @@ hunk line.
 * Lines starting with '+' → green, '-' → red, rest → neutral.
 */
@Composable
private fun DiffView(diff: String, filePath: String) {
    val addedBg = Color(0x2200C853)
    val removedBg = Color(0x22F44336)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, ChatAppColors.Text.disabled, RoundedCornerShape(4.dp))
            .background(ChatAppColors.Panel.background, RoundedCornerShape(4.dp))
    ) {
        if (filePath.isNotBlank()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        ChatAppColors.Text.disabled.copy(alpha = 0.08f),
                        RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = filePath,
                    style = JewelTheme.defaultTextStyle.copy(
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = ChatAppColors.Text.disabled
                    )
                )
            }
        }

        Column(modifier = Modifier.padding(8.dp)) {
            diff.lines()
                .filter { !it.startsWith("@@") }
                .forEach { line ->
                    val bg = when {
                        line.startsWith("+") -> addedBg
                        line.startsWith("-") -> removedBg
                        else -> Color.Transparent
                    }
                    val textColor = when {
                        line.startsWith("+") -> Color(0xFF4CAF50)
                        line.startsWith("-") -> Color(0xFFF44336)
                        else -> JewelTheme.defaultTextStyle.color
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(bg)
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = line,
                            style = JewelTheme.defaultTextStyle.copy(
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = textColor
                            )
                        )
                    }
                }
        }
    }
}

@Composable
private fun ExpandableSection(
    headerLabel: String,
    initiallyExpanded: Boolean = true,
    content: @Composable () -> Unit
) {
    var expanded by remember { mutableStateOf(initiallyExpanded) }

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(vertical = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = headerLabel,
                style = JewelTheme.defaultTextStyle.copy(
                    fontSize = 11.sp,
                    color = ChatAppColors.Text.disabled
                )
            )
            Text(
                text = if (expanded) "▲" else "▼",
                style = JewelTheme.defaultTextStyle.copy(
                    fontSize = 9.sp,
                    color = ChatAppColors.Text.disabled
                )
            )
        }

        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            content()
        }
    }
}
