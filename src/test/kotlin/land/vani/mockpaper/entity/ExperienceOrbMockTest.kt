package land.vani.mockpaper.entity

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.matchers.shouldBe
import land.vani.mockpaper.MockPaper
import land.vani.mockpaper.ServerMock
import land.vani.mockpaper.randomLocation
import land.vani.mockpaper.world.WorldMock
import org.bukkit.entity.EntityType
import java.util.UUID

class ExperienceOrbMockTest : ShouldSpec({
    lateinit var server: ServerMock
    lateinit var world: WorldMock
    lateinit var orb: ExperienceOrbMock

    beforeTest {
        server = MockPaper.mock()
        world = server.addSimpleWorld("world")
        orb = world.spawn(randomLocation(world))
    }

    should("entityType") {
        orb.type shouldBe EntityType.EXPERIENCE_ORB
    }

    should("spawning") {
        world.entities shouldContain orb
        orb.experience shouldBeExactly 0
    }

    should("second constructor") {
        orb = ExperienceOrbMock(server, UUID.randomUUID(), 10)
        orb.experience shouldBeExactly 10
    }

    should("set experience") {
        orb.experience shouldBeExactly 0
        orb.experience = 10
        orb.experience shouldBeExactly 10
    }
})
