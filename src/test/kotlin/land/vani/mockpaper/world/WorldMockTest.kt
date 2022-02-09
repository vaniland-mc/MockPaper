package land.vani.mockpaper.world

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldNotContainExactly
import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.matchers.longs.shouldBeExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import land.vani.mockpaper.MockPaper
import land.vani.mockpaper.ServerMock
import land.vani.mockpaper.randomLocation
import org.bukkit.GameRule
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.Zombie
import org.bukkit.event.world.TimeSkipEvent
import org.bukkit.inventory.ItemStack

class WorldMockTest : ShouldSpec({
    lateinit var server: ServerMock
    lateinit var world: WorldMock

    beforeTest {
        server = MockPaper.mock()
        world = server.addWorld(WorldMock(server))
    }

    context("name") {
        should("name is default 'World'") {
            world.name shouldBe "World"
        }

        should("setName") {
            world.name = "SomeWorld"

            world.name shouldBe "SomeWorld"
        }
    }

    should("getPlayerCount") {
        world.playerCount shouldBeExactly 0

        val player = server.addPlayer()
        player.location = randomLocation(world)

        world.playerCount shouldBeExactly 1
    }

    context("getBlockAt") {
        should("standard world") {
            world = server.addWorld(WorldMock(server, Material.DIRT, 3))

            world.getBlockAt(0, -64, 0).type shouldBe Material.BEDROCK
            world.getBlockAt(0, 1, 0).type shouldBe Material.DIRT
            world.getBlockAt(0, 2, 0).type shouldBe Material.DIRT
            world.getBlockAt(0, 3, 0).type shouldBe Material.DIRT
            world.getBlockAt(0, 4, 0).type shouldBe Material.AIR
        }

        should("block changed") {
            world.getBlockAt(0, 10, 0).type shouldBe Material.AIR
            world.getBlockAt(0, 10, 0).type = Material.BIRCH_WOOD

            world.getBlockAt(0, 10, 0).type shouldBe Material.BIRCH_WOOD
        }

        should("location") {
            val location = randomLocation(world)
            val block = world.getBlockAt(location)

            block.location shouldBe location.toBlockLocation()
        }
    }

    context("getChunkAt") {
        should("difference location") {
            val chunk1 = world.getChunkAt(0, 0)
            val chunk2 = world.getChunkAt(1, 0)

            chunk1 shouldNotBe chunk2
        }

        should("same location") {
            val chunk1 = world.getChunkAt(0, 0)
            val chunk2 = world.getChunkAt(0, 0)

            chunk1 shouldBe chunk2
        }
    }

    should("getLoadedChunks empty world has no loaded chunks") {
        world.loadedChunks.shouldBeEmpty()
    }

    context("isChunkLoaded") {
        should("unloaded chunk") {
            world.isChunkLoaded(0, 0) shouldBe false
        }

        should("loaded chunk") {
            val block = world.getBlockAt(64, 64, 64)
            val chunk = block.chunk

            world.isChunkLoaded(chunk) shouldBe true
        }
    }

    context("dropItem") {
        should("air") {
            val location = randomLocation(world)
            val item = ItemStack(Material.AIR)

            shouldThrow<IllegalArgumentException> {
                world.dropItem(location, item)
            }
        }

        should("valid") {
            val location = randomLocation(world)
            val itemStack = ItemStack(Material.DIRT)
            val entity = world.dropItem(location, itemStack)

            world.entities shouldContainExactly listOf(entity)
            entity.type shouldBe EntityType.DROPPED_ITEM
            entity.itemStack shouldBe itemStack
        }
    }

    context("getEntities") {
        should("default is empty") {
            world.entities.shouldBeEmpty()
        }

        should("one player in world") {
            val player = server.addPlayer()
            player.teleport(world.spawnLocation)

            world.entities shouldContainExactly listOf(player)
        }

        should("one player in other world") {
            val player = server.addPlayer()
            val world2 = server.addSimpleWorld("world2")
            player.teleport(world2.spawnLocation)

            world.entities shouldNotContainExactly listOf(player)
        }
    }

    context("getPlayers") {
        should("one player in world") {
            val player = server.addPlayer()
            player.teleport(world.spawnLocation)

            world.players shouldContainExactly listOf(player)
        }

        should("one player in other world") {
            val player = server.addPlayer()
            val world2 = server.addSimpleWorld("world2")
            player.teleport(world2.spawnLocation)

            world.players shouldNotContainExactly listOf(player)
        }
    }

    context("spawnLocation") {
        should("default") {
            val location = world.spawnLocation.clone()

            world.getBlockAt(location).type shouldBe Material.AIR
            world.getBlockAt(location.add(0.0, -1.0, 0.0)).type shouldBe Material.GRASS
        }

        should("set new location") {
            val location = world.spawnLocation.clone()
            world.spawnLocation = location.add(10.0, 10.0, 10.0)

            world.spawnLocation shouldBe location
        }
    }

    context("time") {
        should("default zero") {
            world.fullTime shouldBeExactly 0
            world.time shouldBeExactly 0
        }

        should("set time") {
            world.time = 20

            world.time shouldBeExactly 20
            world.fullTime shouldBeExactly 20
        }

        should("set fullTime") {
            world.fullTime = 3 * 24000 + 20

            world.time shouldBeExactly 20
            world.fullTime shouldBeExactly 3 * 24000 + 20
        }

        should("set fullTime adjusted with day time") {
            world.fullTime = 3 * 24000 + 20
            world.time = 12000

            world.time = 12000
            world.fullTime = 3 * 24000 + 12000
        }

        should("event triggered") {
            world.time = 6000
            world.time = 10000

            server.pluginManager.assertEventFired<TimeSkipEvent> {
                it.skipAmount == 4000L && it.skipReason == TimeSkipEvent.SkipReason.CUSTOM
            }
        }
    }

    context("storm") {
        should("storm is default false") {
            world.hasStorm() shouldBe false
        }

        should("set storm") {
            world.setStorm(true)

            world.hasStorm() shouldBe true
        }
    }

    context("thundering") {
        should("thundering is default false") {
            world.isThundering shouldBe false
        }

        should("set thundering") {
            world.isThundering = true

            world.isThundering shouldBe true
        }
    }

    context("thunderDuration") {
        should("thunderDuration is default 0") {
            world.thunderDuration shouldBeExactly 0
        }

        should("set thunderDuration") {
            world.thunderDuration = 20

            world.thunderDuration shouldBeExactly 20
            world.isThundering shouldBe true
        }
    }

    context("gameRule") {
        should("set") {
            world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true) shouldBe true
            world.getGameRuleValue(GameRule.DO_DAYLIGHT_CYCLE) shouldBe true

            world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false) shouldBe true
            world.getGameRuleValue(GameRule.DO_DAYLIGHT_CYCLE) shouldBe false
        }
    }

    should("spawn") {
        val location = randomLocation(world)
        val zombie = world.spawn<Zombie>(location)

        zombie.location shouldBe location
        zombie.type shouldBe EntityType.ZOMBIE
        world.entities shouldContainExactly listOf(zombie)
    }
})
