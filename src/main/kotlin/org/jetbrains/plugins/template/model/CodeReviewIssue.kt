package org.jetbrains.plugins.template.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.KSerializer
import java.time.LocalDateTime
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import java.time.format.DateTimeFormatter
import java.util.*

private val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

/**
 * Represents a code review issue with title, description, proposed fix, and git diff.
 *
 * @property id Unique identifier for the issue
 * @property title Short title of the issue
 * @property description Detailed description of the issue
 * @property proposedFix Short explanation of how to fix it
 * @property gitDiff Hunks-only diff for colored display (no diff --git / index / --- / +++ headers)
 * @property severity Severity level of the issue (ERROR, WARNING, INFO)
 * @property filePath Path to the file where the issue was found
 * @property timestamp When the issue was created
 */
@Serializable
data class CodeReviewIssue(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String = "",
    val proposedFix: String = "",
    @Serializable(with = FlexibleStringSerializer::class)
    val gitDiff: String = "",
    @Serializable(with = SeveritySerializer::class)
    val severity: IssueSeverity = IssueSeverity.WARNING,
    val filePath: String = "",
    @Transient
    @Contextual
    val timestamp: LocalDateTime = LocalDateTime.now()
) : Searchable {

    enum class IssueSeverity {
        ERROR,
        WARNING,
        INFO
    }

    @JvmOverloads
    fun formattedTime(dateTimeFormatter: DateTimeFormatter = timeFormatter): String =
        timestamp.format(dateTimeFormatter)

    override fun matches(query: String): Boolean {
        if (query.isBlank()) return false
        return title.contains(query, ignoreCase = true) ||
                description.contains(query, ignoreCase = true) ||
                proposedFix.contains(query, ignoreCase = true) ||
                filePath.contains(query, ignoreCase = true)
    }
}

private object FlexibleStringSerializer : KSerializer<String> {
    override val descriptor = PrimitiveSerialDescriptor("FlexibleString", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): String {
        val jsonDecoder = decoder as? JsonDecoder
            ?: return decoder.decodeString()
        return when (val el = jsonDecoder.decodeJsonElement()) {
            is JsonPrimitive -> el.contentOrNull.orEmpty()
            is JsonArray -> el.mapNotNull { (it as? JsonPrimitive)?.contentOrNull }.joinToString("\n")
            else -> ""
        }
    }

    override fun serialize(encoder: Encoder, value: String) = encoder.encodeString(value)
}

private object SeveritySerializer : KSerializer<CodeReviewIssue.IssueSeverity> {
    override val descriptor = PrimitiveSerialDescriptor("Severity", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): CodeReviewIssue.IssueSeverity =
        when (decoder.decodeString().uppercase()) {
            "ERROR", "CRITICAL" -> CodeReviewIssue.IssueSeverity.ERROR
            "WARNING", "WARN" -> CodeReviewIssue.IssueSeverity.WARNING
            else -> CodeReviewIssue.IssueSeverity.INFO
        }

    override fun serialize(encoder: Encoder, value: CodeReviewIssue.IssueSeverity) =
        encoder.encodeString(value.name)
}
