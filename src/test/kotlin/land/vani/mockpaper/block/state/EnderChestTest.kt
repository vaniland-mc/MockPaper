package land.vani.mockpaper.block.state

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import land.vani.mockpaper.UnimplementedOperationException
import org.bukkit.Material

class EnderChestTest : ShouldSpec({
    lateinit var enderChest: EnderChestMock

    beforeTest {
        enderChest = EnderChestMock(Material.ENDER_CHEST)
    }

    should("getSnapshot") {
        enderChest.getSnapshot().material shouldBe enderChest.material
    }

    should("open is not implemented yet") {
        shouldThrow<UnimplementedOperationException> {
            enderChest.open()
        }
    }

    should("isOpen is not implemented yet") {
        shouldThrow<UnimplementedOperationException> {
            enderChest.isOpen
        }
    }

    should("close is not implmeented yet") {
        shouldThrow<UnimplementedOperationException> {
            enderChest.close()
        }
    }
})
