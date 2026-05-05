package org.jetbrains.plugins.template.ui.review.composables

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.jewel.ui.component.VerticallyScrollableContainer
import org.jetbrains.plugins.template.model.CodeReviewIssue

@Composable
fun CodeReviewIssuesList(
    modifier: Modifier = Modifier,
    issues: List<CodeReviewIssue>,
    listState: LazyListState,
) {
    Box(modifier = modifier) {
        if (issues.isEmpty()) {
            EmptyIssuesPlaceholder()
        } else {
            VerticallyScrollableContainer(
                modifier = Modifier.fillMaxWidth().safeContentPadding(),
                scrollState = listState,
            ) {
                SelectionContainer {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(issues, key = { it.id }) { issue ->
                            CodeReviewIssueCard(issue = issue)
                        }
                    }
                }
            }
        }
    }
}
