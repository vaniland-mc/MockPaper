package land.vani.mockpaper.command

import org.jetbrains.annotations.VisibleForTesting

/**
 * A representation of the command execution result.
 *
 * @property isSuccess True when the command executed successfully
 */
class CommandResult(
    @VisibleForTesting
    val isSuccess: Boolean,
    private val sender: MessageTarget,
) {
    /**
     * Asserts if the given [message] was not the next message sent to the command sender.
     */
    @VisibleForTesting
    infix fun assertResponse(message: String) = sender.assertSaid(message)

    /**
     * Asserts if a given [format] with [args] message was not the next message sent to the command sender.
     */
    @VisibleForTesting
    fun assertResponse(format: String, vararg args: Any?) {
        assertResponse(format.format(args))
    }

    /**
     * Asserts if more messages have been sent to the command sender.
     */
    @VisibleForTesting
    fun assertNoResponse() = sender.assertNoMoreSaid()
}
