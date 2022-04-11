package land.vani.mockpaper.block.state

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowUnit
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.property.Arb
import io.kotest.property.Exhaustive
import io.kotest.property.arbitrary.enum
import io.kotest.property.arbitrary.filter
import io.kotest.property.checkAll
import io.kotest.property.exhaustive.collection
import land.vani.mockpaper.MockPaper
import land.vani.mockpaper.UnimplementedOperationException
import land.vani.mockpaper.block.BlockMock
import land.vani.mockpaper.inventory.ShulkerBoxInventoryMock
import org.bukkit.DyeColor
import org.bukkit.Material
import org.bukkit.loot.LootTables
import java.util.UUID

private val shulkerBoxes = enumValues<Material>()
    .filter { it.name.endsWith("SHULKER_BOX") }
    .filterNot { it.name.startsWith("LEGACY") }

private fun Exhaustive.Companion.shulkerBox(): Exhaustive<Material> = Exhaustive.collection(shulkerBoxes)

private fun Arb.Companion.notShulkerBox(): Arb<Material> = Arb.enum<Material>()
    .filter { it !in shulkerBoxes }

class ShulkerBoxTest : ShouldSpec({
    lateinit var shulkerBox: ShulkerBoxMock

    beforeEach {
        MockPaper.mock()
        shulkerBox = ShulkerBoxMock(Material.SHULKER_BOX)
    }

    afterEach {
        MockPaper.unmock()
    }

    should("material block state") {
        checkAll(Exhaustive.shulkerBox()) { material ->
            val block = BlockMock(material)
            block.state.shouldBeInstanceOf<ShulkerBoxMock>()
        }
    }

    should("not shulker box cannot create instance") {
        checkAll(Arb.notShulkerBox()) { material ->
            shouldThrow<IllegalArgumentException> {
                ShulkerBoxMock(material)
            }
        }
    }

    should("inventory is ShulkerBoxInventoryMock") {
        shulkerBox.inventory.shouldBeInstanceOf<ShulkerBoxInventoryMock>()
    }

    should("getSnapshot") {
        shulkerBox.getSnapshot().shouldBeInstanceOf<ShulkerBoxMock>()
    }

    should("getLootTable is not implemented yet") {
        shouldThrow<UnimplementedOperationException> {
            shulkerBox.lootTable
        }
    }

    should("setLootTable is not implemented yet") {
        shouldThrowUnit<UnimplementedOperationException> {
            shulkerBox.lootTable = LootTables.SPAWN_BONUS_CHEST.lootTable
        }
    }

    should("getSeed is not implemented yet") {
        shouldThrow<UnimplementedOperationException> {
            shulkerBox.seed
        }
    }

    should("sedSeed is not implemented yet") {
        shouldThrowUnit<UnimplementedOperationException> {
            shulkerBox.seed = 10
        }
    }

    should("isRefillEnabled is not implemented yet") {
        shouldThrow<UnimplementedOperationException> {
            shulkerBox.isRefillEnabled
        }
    }

    should("hasBeenFilled is not implemented yet") {
        shouldThrow<UnimplementedOperationException> {
            shulkerBox.hasBeenFilled()
        }
    }

    should("hasPlayerLooted is not implemented yet") {
        shouldThrow<UnimplementedOperationException> {
            shulkerBox.hasPlayerLooted(UUID.randomUUID())
        }
    }

    should("getLastLooted is not implemented yet") {
        shouldThrow<UnimplementedOperationException> {
            shulkerBox.getLastLooted(UUID.randomUUID())
        }
    }

    should("setHasPlayerLooted is not implemented yet") {
        shouldThrow<UnimplementedOperationException> {
            shulkerBox.setHasPlayerLooted(UUID.randomUUID(), true)
        }
    }

    should("hasPendingRefill is not implemented yet") {
        shouldThrow<UnimplementedOperationException> {
            shulkerBox.hasPendingRefill()
        }
    }

    should("getLastFilled is not implemented yet") {
        shouldThrow<UnimplementedOperationException> {
            shulkerBox.lastFilled
        }
    }

    should("getNextRefill is not implemented yet") {
        shouldThrow<UnimplementedOperationException> {
            shulkerBox.nextRefill
        }
    }

    should("setNextRefill is not implemented yet") {
        shouldThrowUnit<UnimplementedOperationException> {
            shulkerBox.nextRefill = 10
        }
    }

    should("open is not implemented yet") {
        shouldThrow<UnimplementedOperationException> {
            shulkerBox.open()
        }
    }

    should("close is not implemented yet") {
        shouldThrow<UnimplementedOperationException> {
            shulkerBox.close()
        }
    }

    should("isOpen is not implemented yet") {
        shouldThrow<UnimplementedOperationException> {
            shulkerBox.isOpen
        }
    }

    context("getColor") {
        should("getColor is default null") {
            shulkerBox.color.shouldBeNull()
        }

        should("getColor with white shulker box is white") {
            shulkerBox = ShulkerBoxMock(Material.WHITE_SHULKER_BOX)

            shulkerBox.color shouldBe DyeColor.WHITE
        }
    }
})
