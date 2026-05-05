package org.jetbrains.plugins.template.ui.review

sealed class ReviewInputState {
    object Idle : ReviewInputState()

    object Reviewing : ReviewInputState()

    data class ReviewFailed(val errorMessage: String) : ReviewInputState()
}
