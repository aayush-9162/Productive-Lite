package com.example.notemoon.settings.data

import com.example.notemoon.calendar.data.local.EventDao
import com.example.notemoon.calendar.data.local.EventEntity
import com.example.notemoon.notes.data.local.NoteDao
import com.example.notemoon.notes.data.local.NoteEntity
import com.example.notemoon.tasks.data.local.TaskDao
import com.example.notemoon.tasks.data.local.TaskEntity
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject

/** Counts of records moved during a backup operation. */
data class BackupSummary(val notes: Int, val tasks: Int, val events: Int)

/**
 * Exports all app data (notes, tasks and calendar events) to a single JSON
 * document, and imports such a document back. Used by the Settings backup
 * feature with the Storage Access Framework for the actual file I/O.
 */
class BackupManager @Inject constructor(
    private val noteDao: NoteDao,
    private val taskDao: TaskDao,
    private val eventDao: EventDao
) {

    suspend fun exportToJson(): String {
        val root = JSONObject()
        root.put("version", BACKUP_VERSION)
        root.put("notes", JSONArray().apply { noteDao.getAllNotesList().forEach { put(it.toJson()) } })
        root.put("tasks", JSONArray().apply { taskDao.getAllTasksList().forEach { put(it.toJson()) } })
        root.put("events", JSONArray().apply { eventDao.getAllEventsList().forEach { put(it.toJson()) } })
        return root.toString(2)
    }

    suspend fun importFromJson(json: String): BackupSummary {
        val root = JSONObject(json)

        val notes = root.optJSONArray("notes") ?: JSONArray()
        for (i in 0 until notes.length()) noteDao.upsertNote(notes.getJSONObject(i).toNoteEntity())

        val tasks = root.optJSONArray("tasks") ?: JSONArray()
        for (i in 0 until tasks.length()) taskDao.upsertTask(tasks.getJSONObject(i).toTaskEntity())

        val events = root.optJSONArray("events") ?: JSONArray()
        for (i in 0 until events.length()) eventDao.upsertEvent(events.getJSONObject(i).toEventEntity())

        return BackupSummary(notes.length(), tasks.length(), events.length())
    }

    // ---- Notes ----
    private fun NoteEntity.toJson() = JSONObject().apply {
        put("id", id); put("title", title); put("content", content)
        put("isPinned", isPinned); put("isFavorite", isFavorite); put("isArchived", isArchived)
        put("createdAt", createdAt); put("updatedAt", updatedAt)
    }

    private fun JSONObject.toNoteEntity() = NoteEntity(
        id = getLong("id"),
        title = optString("title"),
        content = optString("content"),
        isPinned = optBoolean("isPinned"),
        isFavorite = optBoolean("isFavorite"),
        isArchived = optBoolean("isArchived"),
        createdAt = optLong("createdAt"),
        updatedAt = optLong("updatedAt")
    )

    // ---- Tasks ----
    private fun TaskEntity.toJson() = JSONObject().apply {
        put("id", id); put("title", title); put("description", description)
        put("priority", priority); put("category", category)
        put("dueDate", dueDate); put("dueTime", dueTime)
        put("isCompleted", isCompleted); put("reminderEnabled", reminderEnabled)
        put("repeatType", repeatType); put("createdAt", createdAt); put("updatedAt", updatedAt)
    }

    private fun JSONObject.toTaskEntity() = TaskEntity(
        id = getLong("id"),
        title = optString("title"),
        description = optString("description"),
        priority = optString("priority"),
        category = optString("category"),
        dueDate = optLong("dueDate"),
        dueTime = optLong("dueTime"),
        isCompleted = optBoolean("isCompleted"),
        reminderEnabled = optBoolean("reminderEnabled"),
        repeatType = optString("repeatType"),
        createdAt = optLong("createdAt"),
        updatedAt = optLong("updatedAt")
    )

    // ---- Events ----
    private fun EventEntity.toJson() = JSONObject().apply {
        put("id", id); put("title", title); put("description", description)
        put("category", category); put("date", date)
        put("startTime", startTime); put("endTime", endTime); put("location", location)
        put("createdAt", createdAt); put("updatedAt", updatedAt)
    }

    private fun JSONObject.toEventEntity() = EventEntity(
        id = getLong("id"),
        title = optString("title"),
        description = optString("description"),
        category = optString("category"),
        date = optLong("date"),
        startTime = optLong("startTime"),
        endTime = optLong("endTime"),
        location = optString("location"),
        createdAt = optLong("createdAt"),
        updatedAt = optLong("updatedAt")
    )

    companion object {
        private const val BACKUP_VERSION = 1
    }
}
