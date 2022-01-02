package land.vani.mockpaper.block.data

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import org.bukkit.Material

class BlockDataTest : ShouldSpec({
    should("same data will matches") {
        val blockData1 = BlockDataMock(Material.STONE)
        val blockData2 = BlockDataMock(Material.STONE)
        blockData1.matches(blockData2) shouldBe true
    }
})
