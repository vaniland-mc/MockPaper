package land.vani.mockpaper.block.state

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowUnit
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.nulls.shouldBeNull
import land.vani.mockpaper.MockPaper
import land.vani.mockpaper.UnimplementedOperationException
import org.bukkit.Material
import org.bukkit.loot.LootTables
import java.util.UUID

class DispenserTest : ShouldSpec({
    lateinit var dispenser: DispenserMock

    beforeEach {
        MockPaper.mock()
        dispenser = DispenserMock(Material.DISPENSER)
    }

    afterEach {
        MockPaper.unmock()
    }

    should("getLootTable is not implemented yet") {
        shouldThrow<UnimplementedOperationException> {
            dispenser.lootTable
        }
    }

    should("setLootTable is not implemented yet") {
        shouldThrowUnit<UnimplementedOperationException> {
            dispenser.lootTable = LootTables.SPAWN_BONUS_CHEST.lootTable
        }
    }

    should("getSeed") {
        shouldThrow<UnimplementedOperationException> {
            dispenser.seed
        }
    }

    should("isRefillEnabled is not implemented yet") {
        shouldThrow<UnimplementedOperationException> {
            dispenser.isRefillEnabled
        }
    }

    should("hasBeenFilled is not implemented yet") {
        shouldThrow<UnimplementedOperationException> {
            dispenser.hasBeenFilled()
        }
    }

    should("hasPlayerLooted is not implemented yet") {
        shouldThrow<UnimplementedOperationException> {
            dispenser.hasPlayerLooted(UUID.randomUUID())
        }
    }

    should("getLastLooted is not implemented yet") {
        shouldThrow<UnimplementedOperationException> {
            dispenser.getLastLooted(UUID.randomUUID())
        }
    }

    should("setHasPlayerLooted is not implemented yet") {
        shouldThrow<UnimplementedOperationException> {
            dispenser.setHasPlayerLooted(UUID.randomUUID(), true)
        }
    }

    should("hasPendingRefill is not implemented yet") {
        shouldThrow<UnimplementedOperationException> {
            dispenser.hasPendingRefill()
        }
    }

    should("getLastFilled is not implemented yet") {
        shouldThrow<UnimplementedOperationException> {
            dispenser.lastFilled
        }
    }

    should("getNextRefill is not implemented yet") {
        shouldThrow<UnimplementedOperationException> {
            dispenser.nextRefill
        }
    }

    should("setNextRefill is not implemented yet") {
        shouldThrowUnit<UnimplementedOperationException> {
            dispenser.nextRefill = 10
        }
    }

    should("setSeed is not implemented yet") {
        shouldThrowUnit<UnimplementedOperationException> {
            dispenser.seed = 10
        }
    }

    should("getBlockProjectileSource") {
        dispenser.blockProjectileSource.shouldBeNull()
    }

    should("dispense is not implemented yet") {
        shouldThrow<UnimplementedOperationException> {
            dispenser.dispense()
        }
    }
})
