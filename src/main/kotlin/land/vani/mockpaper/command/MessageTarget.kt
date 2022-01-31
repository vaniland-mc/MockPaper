package land.vani.mockpaper.command

import java.util.Queue
import kotlin.test.assertEquals
import kotlin.test.fail

interface MessageTarget {
    /**
     * Messages that was sent to the target.
     */
    val messages: Queue<String>

    /**
     * Returns the next message that was sent to the target.
     */
    fun nextMessage(): String? = messages.poll()

    /**
     * Asserts that a specific message was not received next by the message target.
     */
    infix fun assertSaid(expected: String) {
        val message = nextMessage() ?: fail("No more messages were sent.")
        assertEquals(expected, message)
    }

    /**
     * Asserts that no more messages were received by the message target.
     */
    fun assertNoMoreSaid() {
        if (nextMessage() != null) {
            fail("More messages were available ($messages).")
        }
    }
}
