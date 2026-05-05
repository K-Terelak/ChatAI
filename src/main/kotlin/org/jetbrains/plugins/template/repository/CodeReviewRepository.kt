package org.jetbrains.plugins.template.repository

import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.jetbrains.plugins.template.model.CodeReviewIssue
import org.jetbrains.plugins.template.repository.git.GitDiffService
import org.jetbrains.plugins.template.repository.git.GitDiffServiceImpl
import org.jetbrains.plugins.template.repository.github_models.GithubModelsServiceImpl
import java.time.LocalDateTime

interface CodeReviewRepositoryApi {
    val issuesFlow: StateFlow<List<CodeReviewIssue>>
    suspend fun sendForReview()
}

@Service(Service.Level.PROJECT)
class CodeReviewRepository(
    private val project: Project
) : CodeReviewRepositoryApi {

    private val _issues = MutableStateFlow<List<CodeReviewIssue>>(emptyList())
    override val issuesFlow: StateFlow<List<CodeReviewIssue>> = _issues.asStateFlow()

    private val githubModelsService = GithubModelsServiceImpl.getInstance()
    private val gitDiffService: GitDiffService by lazy { GitDiffServiceImpl(project) }
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        explicitNulls = false
    }

    override suspend fun sendForReview() {
        withContext(Dispatchers.IO) {
            _issues.value = emptyList()

            try {
                val gitPatch = gitDiffService.getCurrentDiff()
                if (gitPatch.isBlank()) {
                    _issues.value = listOf(
                        CodeReviewIssue(
                            title = "No changes",
                            description = "No git diff found. Make sure you have uncommitted changes.",
                            proposedFix = "",
                            severity = CodeReviewIssue.IssueSeverity.INFO
                        )
                    )
                    return@withContext
                }

                val loadingIssue = CodeReviewIssue(
                    title = "Analyzing code...",
                    description = "Your code is being reviewed by AI...",
                    proposedFix = "",
                    severity = CodeReviewIssue.IssueSeverity.INFO
                )
                _issues.value = listOf(loadingIssue)

                val reviewPrompt = """
                    Please review the following git diff and identify any issues, improvements, or bugs.
                    For each issue found, provide:
                    1. title: short name of the issue
                    2. description: what is wrong and why
                    3. proposedFix: short one-sentence explanation of how to fix it
                    4. severity: ERROR, WARNING or INFO
                    5. gitDiff: a minimal unified diff patch showing ONLY the fix for this specific issue.
                       Include ONLY the hunk lines (+/-/context). Do NOT include diff --git, index, ---, +++ headers.
                       Example:
                       -        Collections.srt(list);
                       +        Collections.sort(list);
                    6. filePath: the file path extracted from the diff where this issue occurs (e.g. src/Main.java)

                    Format your response as a JSON array with objects containing:
                    title, description, proposedFix, severity, gitDiff, filePath

                    Return ONLY the JSON array, no markdown, no explanation.

                    Git diff:
                    $gitPatch
                """.trimIndent()

                val response = githubModelsService.sendMessage(reviewPrompt)
                    .fold(
                        onSuccess = { it },
                        onFailure = {
                            it.printStackTrace()
                            "Error during review: ${it.message ?: "Unknown error"}"
                        }
                    )

                val reviewIssues = parseReviewResponse(response)
                _issues.value = reviewIssues

            } catch (e: Exception) {
                if (e is CancellationException) throw e
                e.printStackTrace()
                _issues.value = listOf(
                    CodeReviewIssue(
                        title = "Review failed",
                        description = e.message ?: "Unknown error occurred during code review",
                        proposedFix = "",
                        severity = CodeReviewIssue.IssueSeverity.ERROR
                    )
                )
            }
        }
    }

    private fun parseReviewResponse(response: String): List<CodeReviewIssue> {
        return try {
            val jsonText = response
                .replace(Regex("^```[a-zA-Z]*\\s*", RegexOption.MULTILINE), "")
                .replace(Regex("```\\s*$", RegexOption.MULTILINE), "")
                .trim()

            val jsonArray = extractJsonArray(jsonText) ?: return fallbackIssue(response)

            json.decodeFromString<List<CodeReviewIssue>>(jsonArray)
                .filter { it.title.isNotBlank() }
                .map { it.copy(timestamp = LocalDateTime.now()) }
                .ifEmpty { fallbackIssue(response) }

        } catch (e: Exception) {
            e.printStackTrace()
            listOf(
                CodeReviewIssue(
                    title = "Parsing error",
                    description = e.message.orEmpty(),
                    proposedFix = ""
                )
            )
        }
    }

    private fun fallbackIssue(response: String): List<CodeReviewIssue> = listOf(
        CodeReviewIssue(
            title = "Code Review Feedback",
            description = response,
            proposedFix = "",
            severity = CodeReviewIssue.IssueSeverity.INFO,
            timestamp = LocalDateTime.now()
        )
    )

    private fun extractJsonArray(jsonText: String): String? {
        val arrayStart = jsonText.indexOf('[')
        val arrayEnd = jsonText.lastIndexOf(']')
        if (arrayStart == -1 || arrayEnd == -1 || arrayEnd <= arrayStart) return null
        return jsonText.substring(arrayStart, arrayEnd + 1)
    }
}
