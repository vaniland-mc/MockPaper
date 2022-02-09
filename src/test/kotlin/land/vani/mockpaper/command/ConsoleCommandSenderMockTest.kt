package land.vani.mockpaper.command

import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowUnit
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import land.vani.mockpaper.UnimplementedOperationException
import org.opentest4j.AssertionFailedError

class ConsoleCommandSenderMockTest : ShouldSpec({
    lateinit var sender: ConsoleCommandSenderMock

    beforeEach {
        sender = ConsoleCommandSenderMock()
    }

    should("sendMessage(String)") {
        sender.sendMessage("TestMessage")

        sender.assertSaid("TestMessage")
        sender.assertNoMoreSaid()
    }

    should("sendMessage(vararg String)") {
        sender.sendMessage("TestA", "TestB")

        sender.assertSaid("TestA")
        sender.assertSaid("TestB")
        sender.assertNoMoreSaid()
    }

    should("nextMessage() with no message is null") {
        sender.nextMessage().shouldBeNull()
    }

    should("name is CONSOLE") {
        sender.name shouldBe "CONSOLE"
    }

    should("isOp is true") {
        sender.isOp shouldBe true
    }

    should("setOp is failed") {
        shouldThrowUnit<UnimplementedOperationException> {
            sender.isOp = false
        }
    }

    should("assertSaid with correct message") {
        sender.sendMessage("Correct message")

        shouldNotThrow<AssertionFailedError> {
            sender.assertSaid("Correct message")
        }
    }

    should("assertSaid with no message") {
        shouldThrow<AssertionFailedError> {
            sender.assertSaid("A message")
        }
    }

    should("assertNoMoreSaid with no message") {
        shouldNotThrow<AssertionFailedError> {
            sender.assertNoMoreSaid()
        }
    }

    should("assertNoMoreSaid with a message") {
        sender.sendMessage("A message")

        shouldThrow<AssertionFailedError> {
            sender.assertNoMoreSaid()
        }
    }
})
