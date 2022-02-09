package land.vani.mockpaper.entity

import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.doubles.shouldBeExactly
import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.matchers.types.shouldNotBeSameInstanceAs
import land.vani.mockpaper.MockPaper
import land.vani.mockpaper.ServerMock
import land.vani.mockpaper.randomLocation
import land.vani.mockpaper.world.WorldMock
import net.kyori.adventure.text.Component
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.permissions.Permission
import org.bukkit.permissions.PermissionDefault
import org.opentest4j.AssertionFailedError
import java.util.UUID

class EntityMockTest : ShouldSpec({
    lateinit var server: ServerMock
    lateinit var world: WorldMock
    lateinit var entity: EntityMock

    beforeTest {
        server = MockPaper.mock()
        world = server.addSimpleWorld("world")
        entity = SimpleEntityMock(server)
    }

    should("server") {
        entity.server shouldBe server
        entity.server.shouldBeInstanceOf<ServerMock>()
    }

    should("get the location is equal") {
        val location1 = entity.location
        val location2 = entity.location
        location1 shouldBe location2
        location1 shouldNotBeSameInstanceAs location2
    }

    should("getLocation(Location)") {
        val location = Location(world, 0.0, 0.0, 0.0)
        val location1 = entity.location

        location shouldNotBe location1
        entity.getLocation(location) shouldBe location1
        location shouldBe location1
    }

    context("assertLocation") {
        should("with correct location") {
            val location = entity.location
            location.add(0.0, 10.0, 0.0)
            entity.location = location

            shouldNotThrow<AssertionFailedError> {
                entity.assertLocation(location, 5.0)
            }
        }

        should("with wrong location") {
            val location = entity.location
            location.add(0.0, 10.0, 0.0)

            shouldThrow<AssertionFailedError> {
                entity.assertLocation(location, 5.0)
            }
        }
    }

    context("assertTeleport") {
        should("with teleport") {
            val location = entity.location
            entity.teleport(location)

            shouldNotThrow<AssertionFailedError> {
                entity.assertTeleported(location, 5.0)
            }
            entity.teleportCause shouldBe TeleportCause.PLUGIN
        }

        should("without teleport") {
            val location = entity.location

            shouldThrow<AssertionFailedError> {
                entity.assertTeleported(location, 5.0)
            }
        }
    }

    context("assertNotTeleported") {
        should("without teleport") {
            shouldNotThrow<AssertionFailedError> {
                entity.assertNotTeleported()
            }
        }

        should("with teleport") {
            val location = entity.location
            entity.teleport(location)

            shouldThrow<AssertionFailedError> {
                entity.assertNotTeleported()
            }
        }

        should("after assertion") {
            val location = entity.location
            entity.teleport(location)
            entity.assertTeleported(location, 0.0)

            shouldNotThrow<AssertionFailedError> {
                entity.assertNotTeleported()
            }
        }
    }

    context("teleport") {
        should("with cause") {
            val location = entity.location
            location.add(0.0, 10.0, 0.0)
            entity.teleport(location, TeleportCause.CHORUS_FRUIT)

            entity.assertTeleported(location, 0.0)
            entity.teleportCause shouldBe TeleportCause.CHORUS_FRUIT
        }

        should("to other entity") {
            val entity2 = SimpleEntityMock(server)
            val location = entity2.location
            location.add(0.0, 5.0, 0.0)

            entity2.teleport(location)
            entity.teleport(entity2)

            entity.assertTeleported(location, 0.0)
        }
    }

    should("hasTeleported with teleport") {
        entity.isTeleported shouldBe false
        entity.teleport(entity.location)
        entity.isTeleported shouldBe true
    }

    should("clearTeleported") {
        entity.isTeleported shouldBe false

        entity.teleport(entity.location)
        entity.isTeleported shouldBe true

        entity.clearTeleported()
        entity.isTeleported shouldBe false
    }

    context("name") {
        should("default name") {
            entity.name() shouldBe Component.text("entity")
        }

        should("custom name") {
            entity.customName(Component.text("Custom name"))

            entity.name() shouldBe Component.text("entity")
            entity.customName() shouldBe Component.text("Custom name")
            entity.customName shouldBe "Custom name"
        }
    }

    should("constructor with uuid is correct") {
        val uuid = UUID.randomUUID()
        entity = SimpleEntityMock(server, uuid)

        entity.uniqueId shouldBe uuid
    }

    should("sendMessage") {
        entity.sendMessage("hello")
        entity.sendMessage("my", "world")

        entity.assertSaid("hello")
        entity.assertSaid("my")
        entity.assertSaid("world")
        entity.assertNoMoreSaid()
    }

    context("equals") {
        should("with same uuid") {
            val entity2 = SimpleEntityMock(server, entity.uniqueId)
            entity shouldBe entity2
        }

        should("with different uuid") {
            val entity2 = SimpleEntityMock(server, UUID.randomUUID())
            entity shouldNotBe entity2
        }

        should("with different object") {
            entity shouldNotBe Any()
        }

        should("with null") {
            entity shouldNotBe null
        }
    }

    should("metadata") {
        val plugin = server.pluginManager.createMockPlugin()
        entity.hasMetadata("metadata") shouldBe false
        entity.setMetadata("metadata", FixedMetadataValue(plugin, "value"))
        entity.hasMetadata("metadata") shouldBe true
        entity.getMetadata("metadata") shouldHaveSize 1
    }

    context("hasPermission") {
        should("permission with PermissionDefault.FALSE") {
            val permission = Permission("mockpaper.perm", PermissionDefault.FALSE)
            server.pluginManager.addPermission(permission)

            entity.hasPermission("mockpaper.perm") shouldBe false
        }

        should("permission with PermissionDefault.TRUE") {
            val permission = Permission("mockpaper.perm", PermissionDefault.TRUE)
            server.pluginManager.addPermission(permission)

            entity.hasPermission("mockpaper.perm") shouldBe true
        }

        should("permission attachment with PermissionDefault.FALSE") {
            val plugin = server.pluginManager.createMockPlugin()
            val permission = Permission("mockpaper.perm", PermissionDefault.FALSE)
            server.pluginManager.addPermission(permission)
            val attachment = entity.addAttachment(plugin)
            attachment.setPermission(permission, true)

            entity.hasPermission("mockpaper.perm") shouldBe true
        }

        should("permission attachment with PermissionDefault.TRUE") {
            val plugin = server.pluginManager.createMockPlugin()
            val permission = Permission("mockpaper.perm", PermissionDefault.TRUE)
            server.pluginManager.addPermission(permission)
            val attachment = entity.addAttachment(plugin)
            attachment.setPermission(permission, true)

            entity.hasPermission("mockpaper.perm") shouldBe true
        }

        should("permission attachment with permission string") {
            val plugin = server.pluginManager.createMockPlugin()
            val permission = Permission("mockpaper.perm", PermissionDefault.TRUE)
            server.pluginManager.addPermission(permission)
            val attachment = entity.addAttachment(plugin)
            attachment.setPermission(permission.name, true)

            entity.hasPermission("mockpaper.perm") shouldBe true
        }
    }

    should("permissionList") {
        val plugin = server.pluginManager.createMockPlugin()
        entity.apply {
            addAttachment(plugin, "mockpaper.perm1", true)
            addAttachment(plugin, "mockpaper.perm2", true)
            addAttachment(plugin, "mockpaper.perm3", false)
        }

        val effectivePermissions = entity.effectivePermissions
        effectivePermissions shouldHaveSize 3

        val permissions = effectivePermissions.map { it.permission }.toSet()
        permissions shouldContainAll listOf("mockpaper.perm1", "mockpaper.perm2", "mockpaper.perm3")

        val first = effectivePermissions.find { it.permission == "mockpaper.perm3" }
        first.shouldNotBeNull()
        first.value shouldBe false
    }

    should("removeAttachment") {
        val plugin = server.pluginManager.createMockPlugin()
        val permission = Permission("mockpaper.perm")
        server.pluginManager.addPermission(permission)
        val attachment = entity.addAttachment(plugin)
        attachment.setPermission(permission, true)
        entity.hasPermission(permission) shouldBe true

        entity.removeAttachment(attachment)
        entity.hasPermission(permission) shouldBe false
    }

    should("damage by player") {
        val zombie = world.spawnEntity(randomLocation(world), EntityType.ZOMBIE) as LivingEntity
        val player = server.addPlayer()
        val initialHealth = zombie.health
        zombie.damage(4.0, player)
        val finalHealth = zombie.health

        initialHealth - finalHealth shouldBeExactly 4.0
    }

    should("invulnerable") {
        entity.isInvulnerable shouldBe false
        entity.isInvulnerable = true
        entity.isInvulnerable shouldBe true
    }

    should("damage by invulnerable") {
        val zombie = world.spawnEntity(randomLocation(world), EntityType.ZOMBIE) as LivingEntity
        val player = server.addPlayer()
        val initialHealth = zombie.health
        zombie.isInvulnerable = true
        zombie.damage(4.0, player)
        val finalDamage = zombie.health

        initialHealth shouldBeExactly finalDamage
    }

    should("creative damage by invulnerable") {
        val zombie = world.spawnEntity(randomLocation(world), EntityType.ZOMBIE) as LivingEntity
        val player = server.addPlayer()
        player.gameMode = GameMode.CREATIVE
        zombie.isInvulnerable = true
        val initialHealth = zombie.health
        zombie.damage(4.0, player)
        val finalHealth = zombie.health

        initialHealth - finalHealth shouldBeExactly 4.0
    }

    should("damage event is fired") {
        val zombie = world.spawnEntity(randomLocation(world), EntityType.ZOMBIE) as LivingEntity
        val player = server.addPlayer()
        zombie.damage(4.0, player)
        server.pluginManager.assertEventFired<EntityDamageByEntityEvent>()
    }

    should("velocity") {
        val velocity = entity.velocity
        velocity.y = 2.0
        entity.velocity = velocity

        entity.velocity shouldBe velocity
    }

    should("fireTicks") {
        entity.fireTicks = 10
        entity.fireTicks shouldBeExactly 10
    }
})
