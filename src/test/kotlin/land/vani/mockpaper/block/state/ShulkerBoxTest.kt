package land.vani.mockpaper.block.state

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.property.Arb
import io.kotest.property.Exhaustive
import io.kotest.property.arbitrary.enum
import io.kotest.property.arbitrary.filter
import io.kotest.property.checkAll
import io.kotest.property.exhaustive.collection
import land.vani.mockpaper.block.BlockMock
import land.vani.mockpaper.inventory.ShulkerBoxInventoryMock
import org.bukkit.Material

private val shulkerBoxes = enumValues<Material>()
    .filter { it.name.endsWith("SHULKER_BOX") }
    .filterNot { it.name.startsWith("LEGACY") }

private fun Exhaustive.Companion.shulkerBox(): Exhaustive<Material> = Exhaustive.collection(shulkerBoxes)

fun Arb.Companion.notShulkerBox(): Arb<Material> = Arb.enum<Material>()
    .filter { it !in shulkerBoxes }

class ShulkerBoxTest : ShouldSpec({
    lateinit var shulkerBox: ShulkerBoxMock

    beforeTest {
        shulkerBox = ShulkerBoxMock(Material.SHULKER_BOX)
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
})
