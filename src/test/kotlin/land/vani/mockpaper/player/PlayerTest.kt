package land.vani.mockpaper.player

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent
import io.kotest.assertions.fail
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.doubles.shouldNotBeExactly
import io.kotest.matchers.floats.shouldBeExactly
import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.matchers.longs.shouldBeExactly
import io.kotest.matchers.longs.shouldBeGreaterThan
import io.kotest.matchers.longs.shouldNotBeExactly
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.property.Exhaustive
import io.kotest.property.checkAll
import io.kotest.property.exhaustive.enum
import io.kotest.property.exhaustive.exhaustive
import io.kotest.property.exhaustive.filterNot
import io.papermc.paper.event.player.AsyncChatEvent
import land.vani.mockpaper.MockPaper
import land.vani.mockpaper.MockPlugin
import land.vani.mockpaper.ServerMock
import land.vani.mockpaper.inventory.EnderChestInventoryMock
import land.vani.mockpaper.inventory.InventoryMock
import land.vani.mockpaper.inventory.InventoryViewMock
import land.vani.mockpaper.randomLocation
import net.kyori.adventure.text.Component
import org.bukkit.BanList
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.EntityType
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockDamageEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.PlayerExpChangeEvent
import org.bukkit.event.player.PlayerLevelChangeEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.event.player.PlayerToggleFlightEvent
import org.bukkit.event.player.PlayerToggleSneakEvent
import org.bukkit.event.player.PlayerToggleSprintEvent
import org.intellij.lang.annotations.Language
import java.util.UUID

class PlayerTest : ShouldSpec({
    lateinit var server: ServerMock
    lateinit var uuid: UUID
    lateinit var player: PlayerMock

    beforeTest {
        server = MockPaper.mock(
            object : ServerMock() {
                private var tick = 0

                override val currentServerTime: Long
                    get() = super.currentServerTime + tick++
            }
        )
        uuid = UUID.randomUUID()
        player = PlayerMock(server, randomPlayerName(), uuid)
    }

    should("entityType is Player") {
        player.type shouldBe EntityType.PLAYER
    }

    context("displayName") {
        should("constructor") {
            val name = randomPlayerName()
            player = PlayerMock(server, name, uuid)

            player.displayName() shouldBe Component.text(name)
        }

        should("displayName(Component)") {
            player.displayName(Component.text("some display name"))

            player.displayName() shouldBe Component.text("some display name")
        }

        should("displayName(null)") {
            val name = randomPlayerName()
            player = PlayerMock(server, name, uuid)
            player.displayName(Component.text("some display name"))
            player.displayName(null)

            player.displayName() shouldBe Component.text(name)
        }

        should("getDisplayName") {
            val name = randomPlayerName()
            player = PlayerMock(server, name, uuid)

            player.displayName shouldBe name
        }

        should("setDisplayName") {
            @Suppress("DEPRECATION")
            player.setDisplayName("some display name")

            player.displayName shouldBe "some display name"
            player.displayName() shouldBe Component.text("some display name")
        }
    }

    context("playerList") {
        context("name") {
            should("playerListName is default player.displayName()") {
                player.playerListName() shouldBe player.displayName()
            }

            should("set playerListName") {
                player.playerListName(Component.text("some player list name"))

                player.playerListName() shouldBe Component.text("some player list name")
            }
        }

        context("header") {
            should("playerListHeader is default null") {
                player.playerListHeader().shouldBeNull()
            }

            should("playerListHeader(Component))") {
                player.sendPlayerListHeader(Component.text("some player list header"))

                player.playerListHeader().shouldNotBeNull()
                player.playerListHeader() shouldBe Component.text("some player list header")
            }

            should("setPlayerListHeader(String)") {
                player.playerListHeader = "some player list header"

                player.playerListHeader.shouldNotBeNull()
                player.playerListHeader shouldBe "some player list header"
                player.playerListHeader() shouldBe Component.text("some player list header")
            }
        }

        context("footer") {
            should("playerListFooter is default null") {
                player.playerListFooter().shouldBeNull()
            }

            should("playerListFooter(Component)") {
                player.sendPlayerListFooter(Component.text("some player list footer"))

                player.playerListFooter().shouldNotBeNull()
                player.playerListFooter() shouldBe Component.text("some player list footer")
            }

            should("setPlayerListFooter(String)") {
                player.playerListFooter = "some player list footer"

                player.playerListFooter.shouldNotBeNull()
                player.playerListFooter shouldBe "some player list footer"
                player.playerListFooter() shouldBe Component.text("some player list footer")
            }
        }

        should("sendPlayerListHeaderAndFooter") {
            player.sendPlayerListHeaderAndFooter(
                Component.text("some player list header"),
                Component.text("some player list footer"),
            )

            player.playerListHeader() shouldBe Component.text("some player list header")
            player.playerListFooter() shouldBe Component.text("some player list footer")
        }
    }

    context("compassTarget") {
        should("compassTarget is default player location") {
            player.compassTarget shouldBe player.location
        }

        should("set compassTarget") {
            val location = randomLocation(player.world)
            player.compassTarget = location

            player.compassTarget shouldBe location
        }
    }

    should("respawn") {
        val location = randomLocation(player.world)
        player.setBedSpawnLocation(location, true)

        player.health = 0.0
        player.respawn()

        player.location shouldBe location
        player.health shouldNotBeExactly 0.0

        server.pluginManager.assertEventFired<PlayerRespawnEvent>()
        server.pluginManager.assertEventFired<PlayerPostRespawnEvent>()
        player.location shouldBe location
    }

    should("simulatePlayerMove") {
        val location = randomLocation(player.world)
        val event = player.simulatePlayerMove(location)

        event.isCancelled shouldBe false
        server.pluginManager.assertEventFired<PlayerMoveEvent> { it == event }
        event.to shouldBe location
    }

    context("gameMode") {
        should("gameMode is default survival") {
            player.gameMode shouldBe GameMode.SURVIVAL
        }

        should("set gameMode") {
            player.gameMode = GameMode.CREATIVE

            player.gameMode shouldBe GameMode.CREATIVE
        }
    }

    should("isOnline is default true") {
        player.isOnline shouldBe true
    }

    context("isBanned") {
        should("isBanned is default false") {
            player.isBanned shouldBe false
        }

        should("banned player") {
            server.getBanList(BanList.Type.NAME).addBan(player.name, null, null, null)

            player.isBanned shouldBe true
        }
    }

    context("isWhitelisted") {
        should("isWhitelisted is default false") {
            player.isWhitelisted shouldBe false
        }

        should("whitelist") {
            player.isWhitelisted = true

            player.isWhitelisted shouldBe true
        }
    }

    context("getPlayer") {
        should("getPlayer is default not null") {
            player.player.shouldNotBeNull()
        }
    }

    should("firstPlayed") {
        player = PlayerMock(server, randomPlayerName(), UUID.randomUUID())

        player.hasPlayedBefore() shouldBe false
        player.firstPlayed shouldBeExactly 0
        player.lastPlayed shouldBeExactly 0

        server.addPlayer(player)
        val firstPlayed = player.firstPlayed

        player.hasPlayedBefore() shouldBe true
        firstPlayed shouldBeGreaterThan 0
        firstPlayed shouldBeExactly player.lastPlayed

        // Player reconnects
        server.addPlayer(player)

        player.hasPlayedBefore() shouldBe true
        firstPlayed shouldBeExactly player.firstPlayed
        player.firstPlayed shouldNotBeExactly player.lastPlayed
    }

    context("inventory") {
        should("twice inventory are same") {
            player.inventory shouldBe player.inventory
        }

        should("enderChest") {
            player.enderChest.shouldBeInstanceOf<EnderChestInventoryMock>()
        }

        should("openInventory is default CRAFTING") {
            val view = player.openInventory

            view.shouldNotBeNull()
            view.type shouldBe InventoryType.CRAFTING
        }

        should("openInventory") {
            val inventory = InventoryViewMock(
                player,
                InventoryMock.Crafting,
                InventoryMock(),
                InventoryType.CHEST
            )
            player.openInventory(inventory)

            player.openInventory shouldBe inventory
        }

        should("closeInventory") {
            val inventory = InventoryViewMock(
                player,
                InventoryMock.Crafting,
                InventoryMock(),
                InventoryType.CHEST
            )
            player.openInventory(inventory)
            player.closeInventory()

            player.openInventory.type shouldBe InventoryType.CRAFTING
            server.pluginManager.assertEventFired<InventoryCloseEvent>()
        }
    }

    // TODO: async event call is not supported yet.
    xshould("chat") {
        player.chat("some message")

        player.assertSaid("some message")
        player.assertNoMoreSaid()
        @Suppress("DEPRECATION")
        server.pluginManager.assertEventFired<org.bukkit.event.player.PlayerChatEvent>()
        @Suppress("DEPRECATION")
        server.pluginManager.assertEventFired<org.bukkit.event.player.AsyncPlayerChatEvent>()
        server.pluginManager.assertEventFired<AsyncChatEvent>()
    }

    should("perform command") {
        @Language("yaml")
        val plugin = server.pluginManager.loadPlugin<MockPlugin>(
            """
                name: MockPlugin
                version: "1.0.0"
                main: ${MockPlugin::class.java.name}
                commands:
                  test: {}
            """.trimIndent()
        )
        var executed = false
        var executedSender: CommandSender? = null
        var executedArgs: Array<String>? = null

        plugin.getCommand("test")!!.setExecutor { sender, _, _, args ->
            executed = true
            executedSender = sender
            executedArgs = args
            true
        }

        player.performCommand("test foo bar") shouldBe true
        executed shouldBe true
        executedSender shouldBe player
        executedArgs shouldContainExactly arrayOf("foo", "bar")
    }

    context("sneak") {
        should("player is default not sneaking") {
            player.isSneaking shouldBe false
        }

        should("set sneaking") {
            player.isSneaking = true

            player.isSneaking shouldBe true
        }

        should("eyeHeight") {
            player.isSneaking = true
            player.eyeHeight shouldNotBeExactly player.getEyeHeight(true)
        }

        should("simulateSneak") {
            val event = player.simulateSneaking(true)

            player.isSneaking shouldBe true
            server.pluginManager.assertEventFired<PlayerToggleSneakEvent> {
                it == event
            }
            event.player shouldBe player
            event.isSneaking shouldBe true
        }
    }

    context("sprint") {
        should("player is default not sprinting") {
            player.isSprinting shouldBe false
        }

        should("set sprinting") {
            player.isSprinting = true

            player.isSprinting shouldBe true
        }

        should("simulateSprint") {
            val event = player.simulateSprinting(true)

            player.isSprinting shouldBe true
            server.pluginManager.assertEventFired<PlayerToggleSprintEvent> {
                it == event
            }
            event.player shouldBe player
            event.isSprinting shouldBe true
        }
    }

    context("bedSpawnLocation") {
        should("set bedSpawnLocation") {
            val location = randomLocation(player.world)
            location.block.type = Material.LIGHT_BLUE_BED

            player.bedSpawnLocation.shouldBeNull()

            player.bedSpawnLocation = location
            player.bedSpawnLocation shouldBe location

            player.bedSpawnLocation = null
            player.bedSpawnLocation.shouldBeNull()
        }

        should("set bedSpawnLocation force") {
            val location = randomLocation(player.world)

            player.bedSpawnLocation = location
            player.bedSpawnLocation.shouldBeNull()

            player.setBedSpawnLocation(location, true)
            player.bedSpawnLocation shouldBe location
        }
    }

    context("breakBlock") {
        should("success") {
            val location = randomLocation(player.world)
            location.block.type = Material.DIRT

            player.breakBlock(location.block) shouldBe true
            location.block.type shouldBe Material.AIR
        }

        should("failed") {
            val location = randomLocation(player.world)
            player.gameMode = GameMode.ADVENTURE

            player.breakBlock(location.block) shouldBe false
        }
    }

    context("simulateBlockDamage") {
        should("survival") {
            player.gameMode = GameMode.SURVIVAL
            val location = randomLocation(player.world)
            val event = player.simulateBlocKDamage(location.block)

            event.shouldNotBeNull()
            event.isCancelled shouldBe false
            event.player shouldBe player
            server.pluginManager.assertEventFired<BlockDamageEvent>()
            server.pluginManager.clearEvents()
        }

        should("without survival") {
            checkAll(
                Exhaustive.enum<GameMode>().filterNot { it == GameMode.SURVIVAL }
            ) { gameMode ->
                player.gameMode = gameMode
                val location = randomLocation(player.world)
                player.simulateBlocKDamage(location.block).shouldBeNull()
            }
        }

        should("not insta break") {
            val plugin = server.pluginManager.createMockPlugin()
            player.gameMode = GameMode.SURVIVAL
            var isBroken = false
            server.pluginManager.registerEvents(
                object : Listener {
                    @EventHandler
                    fun onBlockDamage(event: BlockDamageEvent) {
                        event.instaBreak = false
                    }

                    @EventHandler
                    fun onBlockBreak(@Suppress("UNUSED_PARAMETER") event: BlockBreakEvent) {
                        isBroken = true
                    }
                },
                plugin
            )
            val location = randomLocation(player.world)
            location.block.type = Material.STONE

            val event = player.simulateBlocKDamage(location.block)
            event.shouldNotBeNull()
            event.isCancelled shouldBe false
            isBroken shouldBe false
            location.block.type shouldBe Material.STONE
        }

        should("insta break") {
            val plugin = server.pluginManager.createMockPlugin()
            player.gameMode = GameMode.SURVIVAL
            var brokenCount = 0
            server.pluginManager.registerEvents(
                object : Listener {
                    @EventHandler
                    fun onBlockDamage(event: BlockDamageEvent) {
                        event.instaBreak = true
                    }

                    @EventHandler
                    fun onBlockBreak(@Suppress("UNUSED_PARAMETER") event: BlockBreakEvent) {
                        brokenCount++
                    }
                },
                plugin
            )
            val location = randomLocation(player.world)
            location.block.type = Material.STONE
            val event = player.simulateBlocKDamage(location.block)

            event.shouldNotBeNull()
            event.isCancelled shouldBe false
            brokenCount shouldBeExactly 1
            location.block.type shouldBe Material.AIR
        }
    }

    context("simulateBlockBreak") {
        should("insta break") {
            val plugin = server.pluginManager.createMockPlugin()
            player.gameMode = GameMode.SURVIVAL
            var brokenCount = 0
            server.pluginManager.registerEvents(
                object : Listener {
                    @EventHandler
                    fun onBlockDamage(event: BlockDamageEvent) {
                        event.instaBreak = true
                    }

                    @EventHandler
                    fun onBlockBreak(@Suppress("UNUSED_PARAMETER") event: BlockBreakEvent) {
                        brokenCount++
                    }
                },
                plugin
            )
            val location = randomLocation(player.world)
            location.block.type = Material.STONE
            val event = player.simulateBlockBreak(location.block)

            event.shouldNotBeNull()
            event.isCancelled shouldBe false
            brokenCount shouldBeExactly 1
            location.block.type shouldBe Material.AIR
        }
    }

    // TODO: block state is not supported yet.
    xcontext("simulateBlockPlace") {
        should("valid") {
            val location = randomLocation(player.world)
            player.gameMode = GameMode.SURVIVAL
            val event = player.simulateBlockPlace(Material.STONE, location)

            event.shouldNotBeNull()
            event.isCancelled shouldBe false
            server.pluginManager.assertEventFired<BlockPlaceEvent>()
            location.block.type shouldBe Material.STONE
        }

        should("invalid") {
            val location = randomLocation(player.world)
            player.gameMode = GameMode.ADVENTURE
            val event = player.simulateBlockPlace(Material.STONE, location)
            server.pluginManager.assertEventNotFired<BlockPlaceEvent>()
            event.shouldBeNull()
        }
    }

    context("giveExp") {
        val expRequired = intArrayOf(
            7,
            9,
            11,
            13,
            15,
            17,
            19,
            21,
            23,
            25,
            27,
            29,
            31,
            33,
            35,
            37,
            42,
            47,
            52,
            57,
            62,
            67,
            72,
            77,
            82,
            87,
            92,
            97,
            102,
            107,
            112,
            121,
            130,
            139,
            148,
            157,
            166,
            175,
            184,
            193
        )

        should("negative") {
            player.exp = 0.5F
            player.level = 1
            player.giveExpLevels(-100)

            player.exp shouldBeExactly 0.0F
            player.level shouldBeExactly 0
        }

        should("some exp increase level") {
            checkAll(expRequired.withIndex().toList().exhaustive()) { (index, level) ->
                player.exp shouldBeExactly 0.0F
                player.giveExp(level)
                player.level shouldBeExactly index + 1
            }
        }

        should("some exp increase multiple levels") {
            player.giveExp(expRequired[0] + expRequired[1] + expRequired[2])
            player.level shouldBeExactly 3
            player.totalExperience shouldBeExactly expRequired[0] + expRequired[1] + expRequired[2]
        }

        should("some exp decrease level") {
            player.giveExp(expRequired[0] + expRequired[1])
            player.giveExp(-expRequired[1])
            player.level shouldBeExactly 1
            player.totalExperience shouldBeExactly expRequired[0]
        }

        should("some exp decrease multiple levels") {
            player.giveExp(expRequired[0] + expRequired[1])
            player.giveExp(-(expRequired[0] + expRequired[1]))
            player.level shouldBeExactly 0
            player.totalExperience shouldBeExactly 0
        }

        should("level event is fired") {
            val plugin = server.pluginManager.createMockPlugin()
            var levelCount = 0
            server.pluginManager.registerEvents(
                object : Listener {
                    @EventHandler
                    fun onLevelChanged(@Suppress("UNUSED_PARAMETER") event: PlayerLevelChangeEvent) {
                        levelCount++
                    }

                    @EventHandler
                    fun onExpChanged(@Suppress("UNUSED_PARAMETER") event: PlayerExpChangeEvent) {
                        fail("PlayerExpChangeEvent should not be called")
                    }
                },
                plugin
            )
            player.giveExp(expRequired[0])
            levelCount shouldBeExactly 1
        }

        should("no exp event is fired") {
            val plugin = server.pluginManager.createMockPlugin()
            server.pluginManager.registerEvents(
                object : Listener {
                    @EventHandler
                    fun onLevelChangeEvent(@Suppress("UNUSED_PARAMETER") event: PlayerLevelChangeEvent) {
                        fail("PlayerLevelChangeEvent should not be called")
                    }

                    @EventHandler
                    fun onExpChangeEvent(@Suppress("UNUSED_PARAMETER") event: PlayerExpChangeEvent) {
                        fail("PlayerExpChangeEvent should not be called")
                    }
                },
                plugin
            )
            player.giveExp(0)
        }
    }

    context("allowFlight") {
        should("allowFlight is default false") {
            player.allowFlight shouldBe false
        }

        should("set allowFlight") {
            player.allowFlight = true
            player.allowFlight shouldBe true
        }
    }

    context("hidePlayer") {
        should("other player default can see") {
            val player2 = server.addPlayer()
            player.canSee(player2) shouldBe true
        }

        @Suppress("DEPRECATION")
        should("deprecated hidePlayer") {
            val player2 = server.addPlayer()
            player.hidePlayer(player2)

            player.canSee(player2) shouldBe false
            player.showPlayer(player2)

            player.canSee(player2) shouldBe true
        }

        should("new hidePlayer") {
            val plugin = server.pluginManager.createMockPlugin()
            val player2 = server.addPlayer()
            player.hidePlayer(plugin, player2)

            player.canSee(player2) shouldBe false
            player.showPlayer(plugin, player2)

            player.canSee(player2) shouldBe true
        }

        @Suppress("DEPRECATION")
        should("old and new") {
            val plugin = server.pluginManager.createMockPlugin()
            val player2 = server.addPlayer()
            player.hidePlayer(plugin, player2)

            player.canSee(player2) shouldBe false
            player.showPlayer(player2)

            player.canSee(player2) shouldBe false
            player.showPlayer(plugin, player2)

            player.canSee(player2) shouldBe true
        }

        @Suppress("DEPRECATION")
        should("eachOther") {
            val plugin1 = server.pluginManager.createMockPlugin("plugin1")
            val plugin2 = server.pluginManager.createMockPlugin("plugin2")
            val player2 = server.addPlayer()

            player.hidePlayer(plugin1, player2)
            player.canSee(player2) shouldBe false

            player.hidePlayer(plugin2, player2)
            player.canSee(player2) shouldBe false

            player.hidePlayer(player2)
            player.canSee(player2) shouldBe false

            player.showPlayer(player2)
            player.canSee(player2) shouldBe false

            player.showPlayer(plugin2, player2)
            player.canSee(player2) shouldBe false

            player.showPlayer(plugin1, player2)
            player.canSee(player2) shouldBe true
        }
    }

    context("flying") {
        should("flying is default false") {
            player.isFlying shouldBe false
        }

        should("set flying") {
            player.isFlying = true
            player.isFlying shouldBe true
        }
    }

    should("simulateToggleFlight") {
        val event = player.simulateToggleFlight(true)

        event.shouldNotBeNull()
        event.isFlying shouldBe true
        player.isFlying shouldBe true
        server.pluginManager.assertEventFired<PlayerToggleFlightEvent>()
    }

    @Suppress("DEPRECATION")
    should("sendTitle") {
        player.sendTitle("some title", "some subtitle")

        player.nextTitle() shouldBe "some title"
        player.nextSubTitle() shouldBe "some subtitle"
    }

    context("saturation") {
        should("saturation is default 5.0") {
            player.saturation shouldBeExactly 5.0F
        }

        should("set saturation") {
            player.foodLevel = 20
            player.saturation = 8.0F

            player.saturation shouldBeExactly 8.0F

            player.foodLevel = 20
            player.saturation = 1000000.0F

            player.saturation shouldBeExactly 20.0F
        }
    }
})
