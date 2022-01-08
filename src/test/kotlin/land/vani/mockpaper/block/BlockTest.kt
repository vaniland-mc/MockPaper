package land.vani.mockpaper.block

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import land.vani.mockpaper.ServerMock
import land.vani.mockpaper.block.data.BlockDataMock
import land.vani.mockpaper.randomLocation
import land.vani.mockpaper.world.WorldMock
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.BlockFace

class BlockTest : ShouldSpec({
    lateinit var server: ServerMock
    lateinit var block: BlockMock

    beforeEach {
        server = ServerMock()
        val world = WorldMock(server)
        block = BlockMock(randomLocation(world))
    }

    should("type default is air") {
        block.type shouldBe Material.AIR
    }

    should("set type to STONE and type is STONE") {
        block.type = Material.STONE
        block.type shouldBe Material.STONE
    }

    should("default location is Location(null, 0, 0, 0)") {
        BlockMock().location shouldBe Location(null, 0.0, 0.0, 0.0)
    }

    should("custom location on constructor and location is same") {
        val world = WorldMock(server)
        val location = randomLocation(world)
        block = BlockMock(Material.AIR, location)
        block.location shouldBe location
    }

    should("chunk is matches") {
        val world = WorldMock(server)
        val location = randomLocation(world)
        val worldBlock = world.getBlockAt(location)
        val chunk = worldBlock.chunk
        val localX = location.blockX - (chunk.x shl 4)
        val localZ = location.blockZ - (chunk.z shl 4)
        val chunkBlock = chunk.getBlock(localX, location.blockY, localZ)

        worldBlock shouldBe chunkBlock
    }

    should("world matches location.world") {
        block.world shouldBe block.location.world
    }

    should("x,y,z matches location") {
        val location = randomLocation(null)
        block = BlockMock(location)
        block.x shouldBeExactly location.blockX
        block.y shouldBeExactly location.blockY
        block.z shouldBeExactly location.blockZ
    }

    should("getRelative is correct") {
        val relative = block.getRelative(BlockFace.UP)
        relative.x shouldBeExactly block.x
        relative.y shouldBeExactly block.y + 1
        relative.z shouldBeExactly block.z
    }

    should("getRelative with distance is correct") {
        val relative = block.getRelative(BlockFace.UP, 4)
        relative.x shouldBeExactly block.x
        relative.y shouldBeExactly block.y + 4
        relative.z shouldBeExactly block.z
    }

    should("getRelative with coordinates") {
        val relative = block.getRelative(2, 6, 0)
        relative.x shouldBeExactly block.x + 2
        relative.y shouldBeExactly block.y + 6
        relative.z shouldBeExactly block.z
    }

    should("blockData.material is block.type") {
        block.blockData.material shouldBe block.type
    }

    should("set blockData") {
        val blockData = BlockDataMock(Material.DIRT)
        block.blockData = blockData

        block.blockData shouldBe blockData
        block.type shouldBe blockData.material
    }

    context("liquid") {
        should("water is liquid") {
            block.type = Material.WATER
            block.isLiquid shouldBe true
        }
        should("lava is liquid") {
            block.type = Material.LAVA
            block.isLiquid shouldBe true
        }
        should("bubble column is liquid") {
            block.type = Material.BUBBLE_COLUMN
            block.isLiquid shouldBe true
        }

        should("stone is not liquid") {
            block.type = Material.STONE
            block.isLiquid shouldNotBe true
        }
    }

    context("empty") {
        should("air is empty") {
            block.type = Material.AIR
            block.isEmpty shouldBe true
        }

        should("stone is not empty") {
            block.type = Material.STONE
            block.isEmpty shouldNotBe true
        }
    }

    should("after breakNaturally block is empty") {
        block.type = Material.STONE
        block.breakNaturally()
        block.isEmpty shouldBe true
    }
})
