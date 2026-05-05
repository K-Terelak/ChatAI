package org.jetbrains.plugins.template.ui.review

import com.intellij.openapi.Disposable
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.jetbrains.plugins.template.model.CodeReviewIssue
import org.jetbrains.plugins.template.repository.CodeReviewRepository
import org.jetbrains.plugins.template.repository.CodeReviewRepositoryApi

interface CodeReviewViewModelApi : Disposable {
    val issuesFlow: StateFlow<List<CodeReviewIssue>>

    val reviewInputState: StateFlow<ReviewInputState>

    fun onSendForReview()

    fun onAbortReview()


    val isLoading: StateFlow<Boolean>
}

class CodeReviewViewModel(
    private val coroutineScope: CoroutineScope,
    private val repository: CodeReviewRepositoryApi
) : CodeReviewViewModelApi {

    private val _issuesFlow = MutableStateFlow(emptyList<CodeReviewIssue>())
    override val issuesFlow: StateFlow<List<CodeReviewIssue>> = _issuesFlow.asStateFlow()

    private val _reviewInputState = MutableStateFlow<ReviewInputState>(ReviewInputState.Idle)
    override val reviewInputState: StateFlow<ReviewInputState> = _reviewInputState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    override val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    /**
     * A nullable [Job] instance used to manage the coroutine responsible for sending code for review.
     * This property holds a reference to the currently active job related to the `onSendForReview`
     * operation in the [CodeReviewViewModel]. It enables tracking, cancellation, and lifecycle management
     * of the review process.
     */
    private var currentReviewJob: Job? = null

    init {
        repository
            .issuesFlow
            .onEach { issues -> _issuesFlow.value = issues }
            .launchIn(coroutineScope)
    }

    override fun onSendForReview() {
        currentReviewJob = coroutineScope.launch {
            try {
                emitReviewInputState(ReviewInputState.Reviewing)
                _isLoading.value = true

                repository.sendForReview()

                emitReviewInputState(ReviewInputState.Idle)
            } catch (e: Exception) {
                if (e is CancellationException) throw e

                emitReviewInputState(ReviewInputState.ReviewFailed(e.message ?: "Unknown error"))
            } finally {
                _isLoading.value = false
            }
        }
    }

    override fun onAbortReview() {
        currentReviewJob?.cancel()
        emitReviewInputState(ReviewInputState.Idle)
        _isLoading.value = false
    }

    override fun dispose() {
        coroutineScope.cancel()
    }

    private fun emitReviewInputState(state: ReviewInputState) {
        _reviewInputState.value = state
    }
}

@Service(Service.Level.PROJECT)
class CodeReviewViewModelFactory(
    private val project: Project,
    private val coroutineScope: CoroutineScope
) {
    fun create(): CodeReviewViewModel = CodeReviewViewModel(coroutineScope, project.service<CodeReviewRepository>())
}
