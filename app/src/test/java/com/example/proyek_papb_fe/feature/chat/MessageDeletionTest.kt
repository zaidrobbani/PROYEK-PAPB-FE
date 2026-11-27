package com.example.proyek_papb_fe.feature.chat

import com.example.proyek_papb_fe.core.data.remote.ChatRealtimeSession
import com.example.proyek_papb_fe.core.data.repository.ChatRepository
import com.example.proyek_papb_fe.core.data.source.PickedChatFile
import com.example.proyek_papb_fe.core.domain.model.Conversation
import com.example.proyek_papb_fe.core.domain.model.DeleteForEveryoneWindowMillis
import com.example.proyek_papb_fe.core.domain.model.DeletedMessageLabel
import com.example.proyek_papb_fe.core.domain.model.Message
import com.example.proyek_papb_fe.core.domain.model.MessageAttachment
import com.example.proyek_papb_fe.core.domain.model.MessagePage
import com.example.proyek_papb_fe.core.domain.model.OutgoingAttachment
import com.example.proyek_papb_fe.feature.chat.viewmodel.MessageSelection
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

private class FakeChatRepository(private val failFor: Set<String> = emptySet()) : ChatRepository {
    val calls = mutableListOf<Triple<String, List<String>, Boolean>>()

    override suspend fun deleteMessages(conversationId: String, messageIds: List<String>, forEveryone: Boolean): Long {
        calls += Triple(conversationId, messageIds.sorted(), forEveryone)
        if (conversationId in failFor) throw IllegalStateException("Pesan tidak ditemukan")
        return 1_000L
    }

    override suspend fun conversations(): List<Conversation> = TODO()
    override suspend fun conversation(conversationId: String): Conversation = TODO()
    override suspend fun openConversation(counterpartUserId: String, projectId: String?): Conversation = TODO()
    override suspend fun messages(conversationId: String, beforeAt: String?, beforeId: String?): MessagePage = TODO()
    override suspend fun send(conversationId: String, text: String): Message = TODO()
    override suspend fun markRead(conversationId: String) = TODO()
    override suspend fun sendAttachment(conversationId: String, attachment: OutgoingAttachment): Message = TODO()
    override fun describeFile(uri: String): PickedChatFile = TODO()
    override fun realtime(conversationId: String): ChatRealtimeSession = TODO()
}

private fun message(id: String, sender: String = "me", sentAt: Long = 0L) =
    Message(id, "c1", sender, "Halo", "TEXT", sentAt, null)

class MessageDeletionTest {
    @Test
    fun selection_toggles_and_retains_visible_ids() {
        val selection = MessageSelection()
        selection.toggle("a")
        selection.toggle("b")
        selection.toggle("a")
        assertEquals(setOf("b"), selection.selected.value)
        selection.toggle("c")
        selection.retain(setOf("c"))
        assertEquals(setOf("c"), selection.selected.value)
    }

    @Test
    fun delete_groups_by_conversation_and_clears_selection() = runBlocking {
        val selection = MessageSelection()
        listOf("m1", "m2", "x1").forEach(selection::toggle)
        val repo = FakeChatRepository()
        val deleted = mutableListOf<Pair<String, List<String>>>()

        selection.delete(repo, mapOf("m1" to "c1", "m2" to "c1", "x1" to "c2"), forEveryone = true) { c, ids, _ ->
            deleted += c to ids.sorted()
        }

        assertEquals(
            listOf(Triple("c1", listOf("m1", "m2"), true), Triple("c2", listOf("x1"), true)),
            repo.calls.sortedBy { it.first }
        )
        assertEquals(listOf("c1" to listOf("m1", "m2"), "c2" to listOf("x1")), deleted.sortedBy { it.first })
        assertTrue(selection.selected.value.isEmpty())
        assertFalse(selection.isDeleting.value)
    }

    @Test
    fun failed_conversation_keeps_its_selection_and_rethrows() = runBlocking {
        val selection = MessageSelection()
        listOf("m1", "x1").forEach(selection::toggle)
        val repo = FakeChatRepository(failFor = setOf("c2"))
        try {
            selection.delete(repo, mapOf("m1" to "c1", "x1" to "c2"), forEveryone = false) { _, _, _ -> }
            fail("harus melempar error")
        } catch (e: IllegalStateException) {
            assertEquals("Pesan tidak ditemukan", e.message)
        }
        assertEquals(setOf("x1"), selection.selected.value)
        assertFalse(selection.isDeleting.value)
    }

    @Test
    fun delete_for_everyone_rule_matches_backend() {
        val now = 10 * DeleteForEveryoneWindowMillis
        assertTrue(message("a", sentAt = now - DeleteForEveryoneWindowMillis).canDeleteForEveryone("me", now))
        assertFalse(message("a", sentAt = now - DeleteForEveryoneWindowMillis - 1).canDeleteForEveryone("me", now))
        assertFalse(message("a", sender = "other", sentAt = now).canDeleteForEveryone("me", now))
        assertFalse(message("a", sentAt = now).asDeleted(now).canDeleteForEveryone("me", now))
    }

    @Test
    fun deleted_message_drops_content_and_shows_label() {
        val attachment = MessageAttachment("f", "a.pdf", "application/pdf", 1, "/x", null)
        val deleted = message("a").copy(attachments = listOf(attachment)).asDeleted(5L)
        assertTrue(deleted.isDeleted)
        assertEquals("", deleted.text)
        assertTrue(deleted.attachments.isEmpty())
        assertEquals(DeletedMessageLabel, deleted.previewText)
    }
}
