package land.vani.mockpaper.command

import kotlin.test.assertEquals
import kotlin.test.fail

interface MessageTarget {
    fun nextMessage(): String?

    infix fun assertSaid(expected: String) {
        val message = nextMessage() ?: fail("No more messages were sent.")
        assertEquals(expected, message)
    }

    fun assertNoMoreSaid() {
        if (nextMessage() != null) {
            fail("More messages were available.")
        }
    }
}
