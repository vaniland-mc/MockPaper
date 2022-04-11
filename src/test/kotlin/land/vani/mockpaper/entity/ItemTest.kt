package land.vani.mockpaper.entity

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import land.vani.mockpaper.MockPaper
import land.vani.mockpaper.ServerMock
import land.vani.mockpaper.randomLocation
import land.vani.mockpaper.world.WorldMock
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.Item
import org.bukkit.inventory.ItemStack

class ItemTest : ShouldSpec({
    lateinit var server: ServerMock
    lateinit var world: WorldMock
    var item = ItemStack(Material.EMERALD)
    lateinit var entity: Item

    beforeEach {
        server = MockPaper.mock()
        world = server.addSimpleWorld("world")
    }

    afterEach {
        MockPaper.unmock()
    }

    should("entityType") {
        entity = world.dropItem(randomLocation(world), item)
        entity.type shouldBe EntityType.DROPPED_ITEM
    }

    should("dropItemNaturally") {
        val location = randomLocation(world)
        entity = world.dropItemNaturally(location, item)
        entity.itemStack shouldBe item
        world.entities shouldContain entity
        entity.location shouldNotBe location
    }

    should("dropItem with consumer") {
        val location = randomLocation(world)
        entity = world.dropItem(location, item) { n ->
            world.entities shouldNotContain n
        }
        entity.itemStack shouldBe item
        world.entities shouldContain entity
    }

    should("item cannot spawn by World#spawn(...)") {
        shouldThrow<IllegalArgumentException> {
            world.spawn<Item>(randomLocation(world))
        }
        shouldThrow<IllegalArgumentException> {
            world.spawnEntity(randomLocation(world), EntityType.DROPPED_ITEM)
        }
    }

    should("pickupDelay") {
        entity.pickupDelay shouldBeExactly 10
        entity.pickupDelay = 50
        entity.pickupDelay shouldBeExactly 50
    }

    should("pickupDelay max") {
        val max = 32767
        entity.pickupDelay = 100000000
        entity.pickupDelay shouldBeExactly max
    }

    should("set itemStack") {
        item = ItemStack(Material.QUARTZ)
        entity.itemStack = item
        entity.itemStack shouldBe item
    }
})
