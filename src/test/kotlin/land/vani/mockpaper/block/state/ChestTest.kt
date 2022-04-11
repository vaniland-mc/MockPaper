package land.vani.mockpaper.block.state

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowUnit
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldBeEmpty
import io.kotest.matchers.types.shouldBeInstanceOf
import land.vani.mockpaper.MockPaper
import land.vani.mockpaper.UnimplementedOperationException
import land.vani.mockpaper.inventory.ChestInventoryMock
import land.vani.mockpaper.persistence.PersistentDataContainerMock
import org.bukkit.Material
import org.bukkit.event.inventory.InventoryType
import org.bukkit.loot.LootTables
import java.util.UUID

class ChestTest : ShouldSpec({
    lateinit var chest: ChestMock

    beforeEach {
        MockPaper.mock()
        chest = ChestMock(Material.CHEST)
    }

    afterEach {
        MockPaper.unmock()
    }

    context("setLock") {
        should("valid key") {
            chest.setLock("key")

            chest.isLocked shouldBe true
            chest.lock shouldBe "key"
        }

        should("null key") {
            chest.setLock(null)

            chest.isLocked shouldBe false
            chest.lock.shouldBeEmpty()
        }
    }

    should("getLock is default empty string") {
        chest.lock.shouldBeEmpty()
    }

    should("getInventory") {
        chest.inventory.shouldBeInstanceOf<ChestInventoryMock>()
        chest.inventory.holder shouldBe chest
        chest.inventory.type shouldBe InventoryType.CHEST
    }

    should("getSnapshotInventory") {
        chest.snapshotInventory shouldBe chest.inventory
    }

    should("getPersistentDataContainer") {
        chest.persistentDataContainer.shouldBeInstanceOf<PersistentDataContainerMock>()
    }

    should("getLootTable is not implemented yet") {
        shouldThrow<UnimplementedOperationException> {
            chest.lootTable
        }
    }

    should("setLootTable is not implemented yet") {
        shouldThrowUnit<UnimplementedOperationException> {
            chest.lootTable = LootTables.SPAWN_BONUS_CHEST.lootTable
        }
    }

    should("getSeed is not implemented yet") {
        shouldThrow<UnimplementedOperationException> {
            chest.seed
        }
    }

    should("setSeed is not implemented yet") {
        shouldThrowUnit<UnimplementedOperationException> {
            chest.seed = 10
        }
    }

    should("open is not implemented yet") {
        shouldThrow<UnimplementedOperationException> {
            chest.open()
        }
    }

    should("isOpen is not implemented yet") {
        shouldThrow<UnimplementedOperationException> {
            chest.isOpen
        }
    }

    should("close is not implemented yet") {
        shouldThrow<UnimplementedOperationException> {
            chest.close()
        }
    }

    should("getBlockInventory is same to inventory") {
        chest.blockInventory shouldBe chest.inventory
    }

    should("getSnapshot") {
        val snapshot = chest.getSnapshot()
        snapshot.material shouldBe chest.material
    }

    should("isRefillEnabled is not implemented yet") {
        shouldThrow<UnimplementedOperationException> {
            chest.isRefillEnabled
        }
    }

    should("hasBeenFilled is not implemented yet") {
        shouldThrow<UnimplementedOperationException> {
            chest.hasBeenFilled()
        }
    }

    should("hasPlayerLooted is not implemented yet") {
        shouldThrow<UnimplementedOperationException> {
            chest.hasPlayerLooted(UUID.randomUUID())
        }
    }

    should("getLastLooted is not implemented yet") {
        shouldThrow<UnimplementedOperationException> {
            chest.getLastLooted(UUID.randomUUID())
        }
    }

    should("setHasPlayerLooted is not implemented yet") {
        shouldThrow<UnimplementedOperationException> {
            chest.setHasPlayerLooted(UUID.randomUUID(), true)
        }
    }

    should("getLastFilled is not implemented yet") {
        shouldThrow<UnimplementedOperationException> {
            chest.lastFilled
        }
    }

    should("hasPendingRefill is not implemented yet") {
        shouldThrow<UnimplementedOperationException> {
            chest.hasPendingRefill()
        }
    }

    should("getNextRefill is not implemented yet") {
        shouldThrow<UnimplementedOperationException> {
            chest.nextRefill
        }
    }

    should("setNextRefill is not implemented yet") {
        shouldThrowUnit<UnimplementedOperationException> {
            chest.nextRefill = 10
        }
    }
})
