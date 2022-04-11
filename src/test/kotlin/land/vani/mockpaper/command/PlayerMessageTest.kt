package land.vani.mockpaper.command

import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import land.vani.mockpaper.MockPaper
import land.vani.mockpaper.ServerMock
import land.vani.mockpaper.player.PlayerMock
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.TextComponent
import org.opentest4j.AssertionFailedError

class PlayerMessageTest : ShouldSpec({
    lateinit var server: ServerMock
    lateinit var player: PlayerMock

    beforeEach {
        server = MockPaper.mock()
        player = server.addPlayer()
    }

    afterEach {
        MockPaper.unmock()
    }

    should("sendMessage(Component)") {
        player.sendMessage(
            Component.text("Component message").color(NamedTextColor.WHITE)
        )

        player.assertSaid("§fComponent message")
    }

    should("sendMessage(BaseComponent)") {
        @Suppress("DEPRECATION")
        player.sendMessage(
            TextComponent("Hello World").apply {
                color = ChatColor.YELLOW
            }
        )

        player.assertSaid("§eHello World")
    }

    should("spigot().sendMessage(BaseComponent)") {
        @Suppress("DEPRECATION")
        player.spigot().sendMessage(
            TextComponent("Hello World").apply {
                color = ChatColor.YELLOW
            }
        )

        player.assertSaid("§eHello World")
    }

    should("assertSaid failed with illegal message") {
        player.sendMessage("Test message")

        shouldThrow<AssertionFailedError> {
            player.assertSaid("illegal message")
        }
    }

    should("assertNoMoreSaid with no message") {
        shouldNotThrow<AssertionFailedError> {
            player.assertNoMoreSaid()
        }
    }

    should("assertNoMoreSaid failed with some message") {
        player.sendMessage("Test message")

        shouldThrow<AssertionFailedError> {
            player.assertNoMoreSaid()
        }
    }
})
