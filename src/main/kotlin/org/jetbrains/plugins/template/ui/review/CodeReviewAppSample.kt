package org.jetbrains.plugins.template.ui.review

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import org.jetbrains.plugins.template.model.CodeReviewIssue
import org.jetbrains.plugins.template.ui.common.HorizontalDivider
import org.jetbrains.plugins.template.ui.review.composables.CodeReviewActionsBar
import org.jetbrains.plugins.template.ui.review.composables.CodeReviewIssuesList
import org.jetbrains.plugins.template.ui.theme.ChatAppColors

/**
 * Main composable for the Code Review UI.
 * Displays a list of code review issues with search functionality.
 *
 * @param viewModel The CodeReviewViewModel managing the state and operations
 */
@Composable
fun CodeReviewAppSample(viewModel: CodeReviewViewModel) {
    val issues by viewModel.issuesFlow.collectAsState(emptyList<CodeReviewIssue>())
    val reviewInputState by viewModel.reviewInputState.collectAsState(ReviewInputState.Idle)
    val isLoading by viewModel.isLoading.collectAsState(false)

    val listState = rememberLazyListState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ChatAppColors.Panel.background)
    ) {
        CodeReviewIssuesList(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            issues = issues,
            listState = listState,
        )

        HorizontalDivider()

        CodeReviewActionsBar(
            isLoading = isLoading,
            reviewInputState = reviewInputState,
            onSendForReview = { viewModel.onSendForReview() },
            onAbortReview = { viewModel.onAbortReview() }
        )
    }
}
