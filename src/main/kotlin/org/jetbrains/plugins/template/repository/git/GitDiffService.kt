package org.jetbrains.plugins.template.repository.git

import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import java.io.File

/**
 * Service for collecting and managing git diffs from the current project.
 * Executes git commands to retrieve changes.
 */
interface GitDiffService {
    /**
     * Retrieves the current git diff for all uncommitted changes.
     *
     * @return Git diff string showing all changes, or empty string if no changes
     */
    suspend fun getCurrentDiff(): String
}

@Service(Service.Level.PROJECT)
class GitDiffServiceImpl(private val project: Project) : GitDiffService {

    override suspend fun getCurrentDiff(): String {
        return executeGitCommand("git diff --no-color")
    }

    private fun executeGitCommand(command: String): String {
        return try {
            val projectPath = project.basePath ?: return ""
            val process = ProcessBuilder("bash", "-c", command)
                .directory(File(projectPath))
                .redirectErrorStream(true)
                .start()

            val output = process.inputStream.bufferedReader().use { it.readText() }
            process.waitFor()
            output
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }
}
