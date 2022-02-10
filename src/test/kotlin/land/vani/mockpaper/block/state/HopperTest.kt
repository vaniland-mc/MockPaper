package land.vani.mockpaper.block.state

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowUnit
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldBeEmpty
import io.kotest.matchers.types.shouldBeInstanceOf
import land.vani.mockpaper.MockPaper
import land.vani.mockpaper.UnimplementedOperationException
import land.vani.mockpaper.inventory.HopperInventoryMock
import land.vani.mockpaper.persistence.PersistentDataContainerMock
import org.bukkit.Material
import org.bukkit.event.inventory.InventoryType
import org.bukkit.loot.LootTables
import java.util.UUID

class HopperTest : ShouldSpec({
    lateinit var hopper: HopperMock

    beforeTest {
        MockPaper.mock()
        hopper = HopperMock(Material.HOPPER)
    }

    context("setLock") {
        should("valid key") {
            hopper.setLock("key")

            hopper.isLocked shouldBe true
            hopper.lock shouldBe "key"
        }

        should("null key") {
            hopper.setLock(null)

            hopper.isLocked shouldBe false
            hopper.lock.shouldBeEmpty()
        }
    }

    should("getLock is default empty string") {
        hopper.lock.shouldBeEmpty()
    }

    should("getInventory") {
        hopper.inventory.shouldBeInstanceOf<HopperInventoryMock>()
        hopper.inventory.holder shouldBe hopper
        hopper.inventory.type shouldBe InventoryType.HOPPER
    }

    should("getSnapshotInventory") {
        hopper.snapshotInventory shouldBe hopper.inventory
    }

    should("getPersistentDataContainer") {
        hopper.persistentDataContainer.shouldBeInstanceOf<PersistentDataContainerMock>()
    }

    should("getLootTable is not implemented yet") {
        shouldThrow<UnimplementedOperationException> {
            hopper.lootTable
        }
    }

    should("setLootTable is not implemented yet") {
        shouldThrowUnit<UnimplementedOperationException> {
            hopper.lootTable = LootTables.SPAWN_BONUS_CHEST.lootTable
        }
    }

    should("getSeed is not implemented yet") {
        shouldThrow<UnimplementedOperationException> {
            hopper.seed
        }
    }

    should("setSeed is not implemented yet") {
        shouldThrowUnit<UnimplementedOperationException> {
            hopper.seed = 10
        }
    }

    should("getSnapshot") {
        val snapshot = hopper.getSnapshot()
        snapshot.material shouldBe hopper.material
    }

    should("isRefillEnabled is not implemented yet") {
        shouldThrow<UnimplementedOperationException> {
            hopper.isRefillEnabled
        }
    }

    should("hasBeenFilled is not implemented yet") {
        shouldThrow<UnimplementedOperationException> {
            hopper.hasBeenFilled()
        }
    }

    should("hasPlayerLooted is not implemented yet") {
        shouldThrow<UnimplementedOperationException> {
            hopper.hasPlayerLooted(UUID.randomUUID())
        }
    }

    should("getLastLooted is not implemented yet") {
        shouldThrow<UnimplementedOperationException> {
            hopper.getLastLooted(UUID.randomUUID())
        }
    }

    should("setHasPlayerLooted is not implemented yet") {
        shouldThrow<UnimplementedOperationException> {
            hopper.setHasPlayerLooted(UUID.randomUUID(), true)
        }
    }

    should("getLastFilled is not implemented yet") {
        shouldThrow<UnimplementedOperationException> {
            hopper.lastFilled
        }
    }

    should("hasPendingRefill is not implemented yet") {
        shouldThrow<UnimplementedOperationException> {
            hopper.hasPendingRefill()
        }
    }

    should("getNextRefill is not implemented yet") {
        shouldThrow<UnimplementedOperationException> {
            hopper.nextRefill
        }
    }

    should("setNextRefill is not implemented yet") {
        shouldThrowUnit<UnimplementedOperationException> {
            hopper.nextRefill = 10
        }
    }
})
