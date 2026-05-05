package org.jetbrains.plugins.template.ui.review.composables

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.jewel.ui.component.CircularProgressIndicator
import org.jetbrains.jewel.ui.component.DefaultButton
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.plugins.template.ui.review.ReviewInputState

@Composable
fun CodeReviewActionsBar(
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    reviewInputState: ReviewInputState,
    onSendForReview: () -> Unit,
    onAbortReview: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (reviewInputState is ReviewInputState.ReviewFailed) {
            ErrorMessage(
                message = reviewInputState.errorMessage,
                modifier = Modifier.fillMaxWidth()
            )
        } else if (isLoading) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Reviewing your code...")
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DefaultButton(
                onClick = { onSendForReview() },
                enabled = !isLoading,
                modifier = Modifier.weight(1f)
            ) {
                Text(if (isLoading) "Reviewing..." else "Send for Review")
            }

            if (isLoading) {
                DefaultButton(
                    onClick = onAbortReview,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Stop")
                }
            }
        }
    }
}
