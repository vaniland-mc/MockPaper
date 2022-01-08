package land.vani.mockpaper.command

import land.vani.mockpaper.UnimplementedOperationException
import net.kyori.adventure.text.Component
import org.bukkit.Server
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.conversations.Conversation
import org.bukkit.conversations.ConversationAbandonedEvent
import org.bukkit.permissions.Permission
import org.bukkit.permissions.PermissionAttachment
import org.bukkit.permissions.PermissionAttachmentInfo
import org.bukkit.plugin.Plugin
import java.util.Queue
import java.util.UUID
import java.util.concurrent.LinkedTransferQueue

class ConsoleCommandSenderMock : ConsoleCommandSender, MessageTarget {
    private val messages: Queue<String> = LinkedTransferQueue()

    override fun sendMessage(vararg messages: String) {
        messages.forEach { sendMessage(it) }
    }

    override fun sendMessage(message: String) {
        messages.offer(message)
    }

    override fun sendMessage(sender: UUID?, vararg messages: String) {
        messages.forEach { sendMessage(it) }
    }

    override fun sendMessage(sender: UUID?, message: String) {
        messages.offer(message)
    }

    override fun getServer(): Server {
        throw UnimplementedOperationException()
    }

    override fun getName(): String = "CONSOLE"

    override fun name(): Component = Component.text(name)

    override fun isPermissionSet(name: String): Boolean {
        throw UnimplementedOperationException()
    }

    override fun isPermissionSet(perm: Permission): Boolean {
        throw UnimplementedOperationException()
    }

    override fun hasPermission(perm: Permission): Boolean {
        throw UnimplementedOperationException()
    }

    override fun hasPermission(name: String): Boolean {
        throw UnimplementedOperationException()
    }

    override fun addAttachment(plugin: Plugin): PermissionAttachment {
        throw UnimplementedOperationException()
    }

    override fun addAttachment(plugin: Plugin, ticks: Int): PermissionAttachment? {
        throw UnimplementedOperationException()
    }

    override fun addAttachment(plugin: Plugin, name: String, value: Boolean): PermissionAttachment {
        throw UnsupportedOperationException()
    }

    override fun addAttachment(plugin: Plugin, name: String, value: Boolean, ticks: Int): PermissionAttachment? {
        throw UnsupportedOperationException()
    }

    override fun removeAttachment(attachment: PermissionAttachment) {
        throw UnsupportedOperationException()
    }

    override fun recalculatePermissions() {
        throw UnimplementedOperationException()
    }

    override fun getEffectivePermissions(): Set<PermissionAttachmentInfo> {
        throw UnimplementedOperationException()
    }

    override fun isOp(): Boolean = true

    override fun setOp(value: Boolean) {
        throw UnimplementedOperationException("Console is op and its status cannot be changed")
    }

    override fun isConversing(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun acceptConversationInput(input: String) {
        throw UnimplementedOperationException()
    }

    override fun beginConversation(conversation: Conversation): Boolean {
        throw UnsupportedOperationException()
    }

    override fun abandonConversation(conversation: Conversation) {
        throw UnimplementedOperationException()
    }

    override fun abandonConversation(conversation: Conversation, details: ConversationAbandonedEvent) {
        throw UnsupportedOperationException()
    }

    override fun sendRawMessage(message: String) {
        sendRawMessage(null, message)
    }

    override fun sendRawMessage(sender: UUID?, message: String) {
        messages.offer(message)
    }

    override fun spigot(): CommandSender.Spigot {
        throw UnimplementedOperationException()
    }

    override fun nextMessage(): String? = messages.poll()
}
