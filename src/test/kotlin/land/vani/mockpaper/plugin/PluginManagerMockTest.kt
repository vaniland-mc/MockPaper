package land.vani.mockpaper.plugin

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import land.vani.mockpaper.MockPaper
import land.vani.mockpaper.MockPlugin
import land.vani.mockpaper.ServerMock
import org.bukkit.block.BlockFace
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.player.PlayerInteractEvent

class PluginManagerMockTest : ShouldSpec({
    lateinit var server: ServerMock
    lateinit var pluginManager: PluginManagerMock
    lateinit var plugin: MockPlugin

    beforeTest {
        server = MockPaper.mock()
        pluginManager = server.pluginManager
        plugin = pluginManager.createMockPlugin("MockPlugin")
    }

    context("getPlugin(String)") {
        should("valid") {
            pluginManager.getPlugin("MockPlugin") shouldBe plugin
        }

        should("unknown name") {
            pluginManager.getPlugin("UnknownPlugin").shouldBeNull()
        }
    }

    should("getPlugins()") {
        pluginManager.plugins shouldContainExactly arrayOf(plugin)
    }

    context("isPluginEnabled(String)") {
        should("valid") {
            pluginManager.isPluginEnabled("MockPlugin") shouldBe true
        }

        should("unknown name") {
            pluginManager.isPluginEnabled("UnknownPlugin") shouldBe false
        }

        should("disabled plugin") {
            val plugin2 = pluginManager.createMockPlugin("plugin2")
            pluginManager.disablePlugin(plugin2)

            pluginManager.isPluginEnabled("plugin2") shouldBe false
        }
    }

    context("isPluginEnabled(Plugin)") {
        should("valid") {
            pluginManager.isPluginEnabled(plugin) shouldBe true
        }

        should("disabled plugin") {
            val plugin2 = pluginManager.createMockPlugin("plugin2")
            pluginManager.disablePlugin(plugin2)

            pluginManager.isPluginEnabled(plugin2) shouldBe false
        }
    }

    should("disablePlugins") {
        plugin.isEnabled shouldBe true

        pluginManager.disablePlugins()
        plugin.isEnabled shouldBe false
    }

    should("clearPlugins") {
        plugin.isEnabled shouldBe true

        pluginManager.clearPlugins()

        pluginManager.plugins.shouldBeEmpty()
        plugin.isEnabled shouldBe false
    }

    should("clearEvents") {
        val player = server.addPlayer()
        val event = PlayerInteractEvent(
            player,
            Action.LEFT_CLICK_AIR,
            null,
            null,
            BlockFace.UP
        )
        pluginManager.callEvent(event)

        pluginManager.assertEventFired<PlayerInteractEvent>()
        pluginManager.clearEvents()

        shouldThrow<AssertionError> {
            pluginManager.assertEventFired<PlayerInteractEvent>()
        }
        pluginManager.assertEventNotFired<PlayerInteractEvent>()
    }

    should("callEvent") {
        val player = server.addPlayer()
        var unannotatedPlayerInteractEventExecuted = false
        var annotatedBlockBreakEventExecuted = false
        var annotatedPlayerInteractEventExecuted = false
        pluginManager.registerEvents(
            object : Listener {
                @Suppress("UNUSED")
                fun unannotatedPlayerInteractEventHandler(
                    @Suppress("UNUSED_PARAMETER")
                    event: PlayerInteractEvent,
                ) {
                    unannotatedPlayerInteractEventExecuted = true
                }

                @EventHandler
                fun annotatedBlockBreakEventHandler(
                    @Suppress("UNUSED_PARAMETER")
                    event: BlockBreakEvent,
                ) {
                    annotatedBlockBreakEventExecuted = true
                }

                @EventHandler
                fun annotatedPlayerInteractEventHandler(
                    @Suppress("UNUSED_PARAMETER")
                    event: PlayerInteractEvent,
                ) {
                    annotatedPlayerInteractEventExecuted = true
                }
            },
            plugin
        )
        val event = PlayerInteractEvent(
            player,
            Action.LEFT_CLICK_AIR,
            null,
            null,
            BlockFace.UP
        )
        pluginManager.callEvent(event)

        unannotatedPlayerInteractEventExecuted shouldBe false
        annotatedBlockBreakEventExecuted shouldBe false
        annotatedPlayerInteractEventExecuted shouldBe true
    }
})
