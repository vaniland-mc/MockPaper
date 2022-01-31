package land.vani.mockpaper.world

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.matchers.shouldBe
import land.vani.mockpaper.randomCoordinate

class CoordinateTest : ShouldSpec({
    lateinit var coordinate: Coordinate

    beforeTest {
        coordinate = randomCoordinate()
    }

    should("default constructor") {
        coordinate = Coordinate()

        coordinate.x shouldBeExactly 0
        coordinate.y shouldBeExactly 0
        coordinate.z shouldBeExactly 0
    }

    should("constructor value set") {
        coordinate = Coordinate(1, 2, 3)

        coordinate.x shouldBeExactly 1
        coordinate.y shouldBeExactly 2
        coordinate.z shouldBeExactly 3
    }

    should("toChunkCoordinate") {
        coordinate.toChunkCoordinate() shouldBe ChunkCoordinate(
            coordinate.x shr 4,
            coordinate.z shr 4,
        )
    }
})
