package land.vani.mockpaper

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.matchers.longs.shouldBeExactly
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf
import land.vani.mockpaper.command.ConsoleCommandSenderMock
import land.vani.mockpaper.entity.SimpleEntityMock
import land.vani.mockpaper.internal.toBungeeComponents
import land.vani.mockpaper.player.OfflinePlayerMock
import land.vani.mockpaper.player.PlayerMock
import land.vani.mockpaper.world.WorldMock
import net.kyori.adventure.text.Component
import org.bukkit.BanList
import org.bukkit.Keyed
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.World
import org.bukkit.WorldCreator
import org.bukkit.WorldType
import org.bukkit.command.CommandSender
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.Recipe
import org.bukkit.permissions.Permission
import org.bukkit.permissions.PermissionDefault
import java.util.UUID

class ServerTest : ShouldSpec({
    lateinit var server: ServerMock

    beforeTest {
        server = MockPaper.mock()
    }

    context("getOnlinePlayers") {
        should("default empty") {
            server.onlinePlayers.shouldBeEmpty()
        }

        should("one player") {
            val player = server.addPlayer()

            server.onlinePlayers shouldHaveSize 1
            server.onlinePlayers shouldContainExactly listOf(player)
        }
    }

    should("maxPlayers") {
        should("default Int.MAX_VALUE") {
            server.maxPlayers shouldBeExactly Int.MAX_VALUE
        }

        should("set") {
            server.maxPlayers = 10

            server.maxPlayers shouldBeExactly 10
        }
    }

    should("broadcastMessage(String)") {
        val player1 = server.addPlayer()
        val player2 = server.addPlayer()
        @Suppress("DEPRECATION")
        server.broadcastMessage("hello world")

        assertSoftly(player1) {
            assertSaid("hello world")
            assertNoMoreSaid()
        }
        assertSoftly(player2) {
            assertSaid("hello world")
            assertNoMoreSaid()
        }
    }

    should("broadcast(BaseComponent...)") {
        val player1 = server.addPlayer()
        val player2 = server.addPlayer()
        @Suppress("DEPRECATION")
        server.broadcast(*Component.text("hello world").toBungeeComponents())

        assertSoftly(player1) {
            assertSaid("hello world")
            assertNoMoreSaid()
        }
        assertSoftly(player2) {
            assertSaid("hello world")
            assertNoMoreSaid()
        }
    }

    should("broadcast(String, String)") {
        val player1 = server.addPlayer()
        val player2 = server.addPlayer()
        val player3 = server.addPlayer()
        server.pluginManager.addPermission(
            Permission("mockpaper.test", PermissionDefault.FALSE)
        )
        val plugin = server.pluginManager.createMockPlugin()
        player2.addAttachment(plugin, "mockpaper.test", true)
        player3.addAttachment(plugin, "mockpaper.test", true)

        @Suppress("DEPRECATION")
        server.broadcast("hello world", "mockpaper.test") shouldBeExactly 2

        player1.assertNoMoreSaid()
        assertSoftly(player2) {
            assertSaid("hello world")
            assertNoMoreSaid()
        }
        assertSoftly(player3) {
            assertSaid("hello world")
            assertNoMoreSaid()
        }
    }

    should("broadcast(Component)") {
        val player1 = server.addPlayer()
        val player2 = server.addPlayer()
        server.broadcast(Component.text("hello world"))

        assertSoftly(player1) {
            assertSaid("hello world")
            assertNoMoreSaid()
        }
        assertSoftly(player2) {
            assertSaid("hello world")
            assertNoMoreSaid()
        }
    }

    should("broadcast(Component, String)") {
        val player1 = server.addPlayer()
        val player2 = server.addPlayer()
        val player3 = server.addPlayer()
        server.pluginManager.addPermission(
            Permission("mockpaper.test", PermissionDefault.FALSE)
        )
        val plugin = server.pluginManager.createMockPlugin()
        player2.addAttachment(plugin, "mockpaper.test", true)
        player3.addAttachment(plugin, "mockpaper.test", true)

        server.broadcast(Component.text("hello world"), "mockpaper.test") shouldBeExactly 2

        player1.assertNoMoreSaid()
        assertSoftly(player2) {
            assertSaid("hello world")
            assertNoMoreSaid()
        }
        assertSoftly(player3) {
            assertSaid("hello world")
            assertNoMoreSaid()
        }
    }

    context("getPlayer(String)") {
        should("found") {
            val player = server.addPlayer()
            val foundPlayer = server.getPlayer(player.name)

            foundPlayer.shouldNotBeNull()
            foundPlayer shouldBe player
        }

        should("not found") {
            val foundPlayer = server.getPlayer("invalidPlayer")

            foundPlayer.shouldBeNull()
        }
    }

    context("getPlayer(UUID)") {
        should("found") {
            val player = server.addPlayer()
            val foundPlayer = server.getPlayer(player.uniqueId)

            foundPlayer.shouldNotBeNull()
            foundPlayer shouldBe player
        }

        should("not found") {
            val foundPlayer = server.getPlayer(UUID.randomUUID())

            foundPlayer.shouldBeNull()
        }
    }

    context("getPlayerExact") {
        should("found") {
            val player = server.addPlayer()
            val foundPlayer = server.getPlayerExact(player.name)

            foundPlayer shouldBe player
        }

        should("not found") {
            val foundPlayer = server.getPlayerExact("invalidPlayer")

            foundPlayer.shouldBeNull()
        }

        should("case insensitive") {
            val player = server.addPlayer()
            val foundPlayer = server.getPlayerExact(player.name.uppercase())

            foundPlayer shouldBe player
        }
    }

    context("matchPlayer") {
        should("found") {
            val player = server.addPlayer()
            val foundPlayers = server.matchPlayer(player.name)

            foundPlayers shouldContainExactly listOf(player)
        }

        should("not found") {
            val foundPlayers = server.matchPlayer("invalidPlayer")

            foundPlayers.shouldBeEmpty()
        }
    }

    should("getPlayerUniqueId") {
        val player = server.addPlayer()
        val uniqueId = server.getPlayerUniqueId(player.name)

        uniqueId shouldBe player.uniqueId
    }

    context("getWorlds") {
        should("default empty") {
            server.worlds.shouldBeEmpty()
        }

        should("vailed") {
            val world = server.addSimpleWorld("world")

            server.worlds shouldHaveSize 1
            server.worlds shouldContainExactly listOf(world)
        }
    }

    should("createWorld") {
        val creator = WorldCreator("test")
            .seed(12345)
            .type(WorldType.FLAT)
            .environment(World.Environment.NORMAL)
        val world = server.createWorld(creator)

        server.worlds shouldHaveSize 1
        server.worlds shouldContainExactly listOf(world)
        world.seed shouldBeExactly 12345
        @Suppress("DEPRECATION")
        world.worldType shouldBe WorldType.FLAT
        world.environment shouldBe World.Environment.NORMAL
    }

    context("getWorld(String)") {
        should("valid") {
            val originalWorld = server.addSimpleWorld("test")
            val world = server.getWorld("test")

            world.shouldNotBeNull()
            world shouldBe originalWorld
        }

        should("invalid") {
            val world = server.getWorld("invalidWorld")

            world.shouldBeNull()
        }
    }

    context("getWorld(UUID)") {
        should("valid") {
            val originalWorld = server.addSimpleWorld("test")
            val world = server.getWorld(originalWorld.uid)

            world.shouldNotBeNull()
            world shouldBe originalWorld
        }

        should("invalid") {
            val world = server.getWorld(UUID.randomUUID())

            world.shouldBeNull()
        }
    }

    context("getWorld(NamespacedKey)") {
        should("valid") {
            val originalWorld = server.addSimpleWorld("test")
            val world = server.getWorld(originalWorld.key)

            world.shouldNotBeNull()
            world shouldBe originalWorld
        }

        should("invalid") {
            val world = server.getWorld(NamespacedKey.minecraft("invalid_world"))

            world.shouldBeNull()
        }
    }

    context("getPluginCommand") {
        should("valid") {
            val plugin = server.pluginManager.loadPlugin<MockPlugin>(
                """
                name: MockPlugin
                version: "1.0.0"
                main: ${MockPlugin::class.java.name}
                commands:
                  test: {}
                """.trimIndent()
            )
            val command = plugin.getCommand("test")
            val pluginCommand = server.getPluginCommand("test")

            pluginCommand.shouldNotBeNull()
            pluginCommand shouldBe command
        }

        should("invalid") {
            server.getPluginCommand("invalidCommand").shouldBeNull()
        }
    }

    context("dispatchCommand") {
        should("success") {
            val plugin = server.pluginManager.loadPlugin<MockPlugin>(
                """
                name: MockPlugin
                version: "1.0.0"
                main: ${MockPlugin::class.java.name}
                commands:
                  test: {}
                """.trimIndent()
            )
            var args: List<String> = listOf()
            plugin.getCommand("test")!!.setExecutor { _, _, _, commandArgs ->
                args = commandArgs.toList()
                true
            }
            val player = server.addPlayer()

            server.dispatchCommand(player, "test arg1 arg2") shouldBe true
            args.shouldNotBeEmpty()
            args shouldContainExactly listOf("arg1", "arg2")
        }

        should("failed") {
            val player = server.addPlayer()
            server.dispatchCommand(player, "invalidCommand") shouldBe false
        }
    }

    context("addRecipe") {
        should("success") {
            val recipe1 = TestRecipe()
            val recipe2 = TestRecipe()

            server.addRecipe(recipe1) shouldBe true
            server.addRecipe(recipe2) shouldBe true
            val iterator = server.recipeIterator()

            iterator.next() shouldBe recipe1
            iterator.next() shouldBe recipe2
            iterator.hasNext() shouldBe false
        }

        should("failed") {
            server.addRecipe(null) shouldBe false
        }
    }

    context("getRecipesFor(ItemStack)") {
        should("valid") {
            val recipe1 = TestRecipe(ItemStack(Material.STONE))
            val recipe2 = TestRecipe(ItemStack(Material.APPLE))
            server.addRecipe(recipe1)
            server.addRecipe(recipe2)
            val recipes = server.getRecipesFor(ItemStack(Material.APPLE))

            recipes shouldHaveSize 1
            recipes shouldContainExactly listOf(recipe2)
        }

        should("ignore amount") {
            val recipe = TestRecipe(ItemStack(Material.APPLE))
            server.addRecipe(recipe)
            val recipes1 = server.getRecipesFor(ItemStack(Material.APPLE, 1))
            val recipes2 = server.getRecipesFor(ItemStack(Material.APPLE, 10))

            recipes1 shouldContainExactly listOf(recipe)
            recipes1 shouldBe recipes2
        }

        should("invalid") {
            server.getRecipesFor(ItemStack(Material.APPLE)).shouldBeEmpty()
        }
    }

    context("getRecipe(NamespacedKey)") {
        should("valid") {
            val originalRecipe = TestRecipe(
                ItemStack(Material.APPLE),
                NamespacedKey.minecraft("test")
            )
            server.addRecipe(originalRecipe)
            val recipe = server.getRecipe(NamespacedKey.minecraft("test"))

            recipe.shouldNotBeNull()
            recipe shouldBe originalRecipe
        }
    }

    should("recipeIterator") {
        val recipe = TestRecipe(ItemStack(Material.APPLE))
        server.addRecipe(recipe)
        server.recipeIterator().hasNext() shouldBe true

        server.clearRecipes()
        server.recipeIterator().hasNext() shouldBe false
    }

    context("removeRecipe") {
        should("success") {
            val recipe = TestRecipe(ItemStack(Material.APPLE))
            server.addRecipe(recipe)

            server.removeRecipe(recipe.key) shouldBe true
        }

        should("failed") {
            @Suppress("DEPRECATION")
            server.removeRecipe(NamespacedKey.randomKey()) shouldBe false
        }
    }

    context("spawnRadius") {
        should("default 16") {
            server.spawnRadius shouldBeExactly 16
        }

        should("set") {
            server.spawnRadius = 200

            server.spawnRadius shouldBeExactly 200
        }
    }

    context("getOfflinePlayer(String)") {
        should("offline player") {
            val original = server.addOfflinePlayer()

            @Suppress("DEPRECATION")
            val player = server.getOfflinePlayer(original.name)

            player.uniqueId shouldBe original.uniqueId
            player.shouldBeInstanceOf<OfflinePlayerMock>()
            player shouldBe original
        }

        should("online player") {
            val original = server.addPlayer()

            @Suppress("DEPRECATION")
            val player = server.getOfflinePlayer(original.name)

            player.uniqueId shouldBe original.uniqueId
            player.shouldBeInstanceOf<PlayerMock>()
            player shouldBe original
        }
    }

    context("getOfflinePlayer(UUID)") {
        should("offline player") {
            val original = server.addOfflinePlayer()
            val player = server.getOfflinePlayer(original.uniqueId)

            player.uniqueId shouldBe original.uniqueId
            player.shouldBeInstanceOf<OfflinePlayerMock>()
            player shouldBe original
        }

        should("online player") {
            val original = server.addPlayer()
            val player = server.getOfflinePlayer(original.uniqueId)

            player.uniqueId shouldBe original.uniqueId
            player.shouldBeInstanceOf<PlayerMock>()
            player shouldBe original
        }
    }

    context("getIPBans") {
        should("default empty") {
            server.ipBans.shouldBeEmpty()
        }

        should("one bans") {
            server.banIP("192.168.1.1")

            server.ipBans shouldHaveSize 1
            server.ipBans shouldContainExactly setOf("192.168.1.1")
        }
    }

    should("banIP") {
        server.banIP("192.168.1.1")

        server.getBanList(BanList.Type.IP).banEntries shouldHaveSize 1
    }

    should("unbanIP") {
        server.banIP("192.168.1.1")
        server.unbanIP("192.168.1.1")

        server.getBanList(BanList.Type.IP).banEntries.shouldBeEmpty()
    }

    context("getBannedPlayers") {
        should("default empty") {
            server.bannedPlayers.shouldBeEmpty()
        }

        should("1 player banned") {
            val player = server.addOfflinePlayer()
            server.getBanList(BanList.Type.NAME).addBan(
                player.name,
                null,
                null,
                null
            )

            server.bannedPlayers shouldHaveSize 1
            server.bannedPlayers shouldContainExactly setOf(player)
        }
    }

    context("getBanList") {
        should("ip") {
            server.getBanList(BanList.Type.IP).shouldBeInstanceOf<BanListMock>()
        }

        should("profile") {
            server.getBanList(BanList.Type.NAME).shouldBeInstanceOf<BanListMock>()
        }
    }

    context("getOperators") {
        should("default empty") {
            server.operators.shouldBeEmpty()
        }

        should("1 operator") {
            val player = server.addPlayer()
            player.isOp = true

            server.operators shouldHaveSize 1
            server.operators shouldContainExactly setOf(player)
        }
    }

    context("consoleSender") {
        should("consoleSender is instance of ConsoleCommandSenderMock") {
            server.consoleSender.shouldBeInstanceOf<ConsoleCommandSenderMock>()
        }

        should("dispatch command") {
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
            var sender: CommandSender? = null
            var args = arrayOf<String>()
            plugin.getCommand("test")!!.setExecutor { commandSender, _, _, commandArgs ->
                executed = true
                sender = commandSender
                args = commandArgs
                true
            }

            server.dispatchCommand(server.consoleSender, "test aaa bbb") shouldBe true
            executed shouldBe true
            sender.shouldNotBeNull()
            sender.shouldBeInstanceOf<ConsoleCommandSenderMock>()
            args shouldHaveSize 2
            args shouldContainExactly arrayOf("aaa", "bbb")
        }
    }

    context("createInventory") {
        should("createInventory with size") {
            val player = server.addPlayer()

            @Suppress("DEPRECATION")
            val inventory = server.createInventory(player, 9, "title")

            inventory.size shouldBeExactly 9
            inventory.holder shouldBe player
        }

        should("createInventory with type") {
            val inventory = server.createInventory(null, InventoryType.CHEST)

            inventory.size shouldBeExactly 9 * 3
            inventory.type shouldBe InventoryType.CHEST
            inventory.holder.shouldBeNull()
        }
    }

    context("entities") {
        should("default empty") {
            server.entities.shouldBeEmpty()
        }

        should("entity registered") {
            val entity = SimpleEntityMock(server)
            server.registerEntity(entity)

            server.entities shouldHaveSize 1
            server.entities shouldContainExactly setOf(entity)
        }
    }

    context("worlds") {
        should("addWorld") {
            val world = WorldMock(server)

            server.addWorld(world) shouldBe world
            server.worlds shouldHaveSize 1
            server.worlds shouldContainExactly listOf(world)
        }

        should("addSimpleWorld") {
            val world = server.addSimpleWorld("TestWorld")

            world.name shouldBe "TestWorld"
            server.worlds shouldHaveSize 1
            server.worlds shouldContainExactly listOf(world)
        }
    }

    should("addPlayer makes a unique player") {
        val player1 = server.addPlayer()
        val player2 = server.addPlayer()

        player1 shouldNotBe player2
        player2 shouldNotBe player1
    }

    context("setPlayers") {
        should("success") {
            server.setPlayers(10)

            server.onlinePlayers shouldHaveSize 10
            server.offlinePlayers shouldHaveSize 10
        }

        should("failed") {
            shouldThrow<IllegalArgumentException> {
                server.setPlayers(-1)
            }
        }
    }

    should("addOfflinePlayer makes a unique player ") {
        val player1 = server.addOfflinePlayer()
        val player2 = server.addOfflinePlayer()

        player1 shouldNotBe player2
        player2 shouldNotBe player1
    }

    context("setOfflinePlayers") {
        should("success") {
            server.setOfflinePlayers(10)

            server.onlinePlayers.shouldBeEmpty()
            server.offlinePlayers shouldHaveSize 10
        }

        should("failed") {
            shouldThrow<IllegalArgumentException> {
                server.setOfflinePlayers(-1)
            }
        }
    }
})

private class TestRecipe(
    private val result: ItemStack,
    @Suppress("DEPRECATION")
    private val key: NamespacedKey = NamespacedKey.randomKey(),
) : Recipe, Keyed {
    constructor() : this(ItemStack(Material.AIR))

    override fun getResult(): ItemStack = result

    override fun getKey(): NamespacedKey = key
}
