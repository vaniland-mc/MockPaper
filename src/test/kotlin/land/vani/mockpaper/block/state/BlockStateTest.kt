package land.vani.mockpaper.block.state

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import land.vani.mockpaper.MockPaper
import land.vani.mockpaper.ServerMock
import land.vani.mockpaper.block.BlockMock
import land.vani.mockpaper.randomLocation
import org.bukkit.Location
import org.bukkit.Material

class BlockStateTest : ShouldSpec({
    lateinit var server: ServerMock

    beforeTest {
        server = MockPaper.mock()
    }

    context("constructor") {
        should("constructor(state)") {
            val block = BlockMock(randomLocation(null))
            val state1 = BlockStateMock(Material.SAND, block)
            val state2 = BlockStateMock(state1)

            state1.material shouldBe state2.material
            state1.block shouldBe state2.block
        }

        should("constructor(block)") {
            val block = BlockMock(randomLocation(null))
            val state = BlockStateMock(block)

            state.block shouldBe block
            state.material shouldBe block.type
        }
    }

    context("on placed") {
        lateinit var location: Location
        lateinit var block: BlockMock
        lateinit var state: BlockStateMock
        beforeTest {
            location = randomLocation(server.addSimpleWorld("test"))
            block = BlockMock(Material.DIRT, location)
            state = block.state
        }

        should("placed is true") {
            state.isPlaced shouldBe true
        }
        should("block is same") {
            state.block shouldBe block
        }
        should("type is same") {
            state.type shouldBe block.type
        }
        should("location is same") {
            state.location shouldBe block.location
        }
        should("getLocation(Location)") {
            val location2 = randomLocation(state.world)
            state.getLocation(location2) shouldBe location
            location2 shouldBe location
        }
        should("chunk is same") {
            state.chunk shouldBe block.chunk
        }
        should("world is same") {
            state.world shouldBe block.world
        }
        should("x y z is same") {
            state.x shouldBeExactly block.x
            state.y shouldBeExactly block.y
            state.z shouldBeExactly block.z
        }
        should("update") {
            val s = BlockStateMock(block).also { block.state = it }

            state.update() shouldBe true
            block.state shouldNotBe s
        }
        should("update force") {
            val s = BlockStateMock(Material.SAND, block).also { block.state = it }

            state.update(true) shouldBe true
            block.state shouldNotBe s
        }
    }

    context("not placed") {
        lateinit var state: BlockStateMock
        beforeTest {
            state = BlockStateMock(Material.DIRT)
        }

        should("placed is false") {
            state.isPlaced shouldBe false
        }
        should("block should throws IllegalStateException") {
            shouldThrow<IllegalStateException> {
                state.block
            }
        }
        should("type is same") {
            state.type shouldBe Material.DIRT
        }
        should("world should throws IllegalStateException") {
            shouldThrow<IllegalStateException> {
                state.world
            }
        }
        should("x y z should throws IllegalStateException") {
            shouldThrow<IllegalStateException> {
                state.x
            }
            shouldThrow<IllegalStateException> {
                state.y
            }
            shouldThrow<IllegalStateException> {
                state.z
            }
        }
        should("location's world is null") {
            state.location.world.shouldBeNull()
        }
        should("chunk should throws IllegalStateException") {
            shouldThrow<IllegalStateException> {
                state.chunk
            }
        }
    }
})
