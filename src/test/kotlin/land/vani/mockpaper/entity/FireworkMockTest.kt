package land.vani.mockpaper.entity

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import land.vani.mockpaper.MockPaper
import land.vani.mockpaper.ServerMock
import land.vani.mockpaper.inventory.meta.FireworkMetaMock
import land.vani.mockpaper.randomLocation
import land.vani.mockpaper.world.WorldMock
import org.bukkit.Color
import org.bukkit.FireworkEffect
import org.bukkit.entity.EntityType
import java.util.UUID

class FireworkMockTest : ShouldSpec({
    lateinit var server: ServerMock
    lateinit var world: WorldMock
    lateinit var firework: FireworkMock

    beforeTest {
        server = MockPaper.mock()
        world = server.addSimpleWorld("world")
        firework = world.spawn(randomLocation(world))
    }

    should("entityType") {
        firework.type shouldBe EntityType.FIREWORK
    }

    should("spawning") {
        world.entities shouldContain firework
        firework.fireworkMeta.shouldNotBeNull()
    }

    should("second constructor") {
        val meta = FireworkMetaMock()
        meta.addEffect(
            FireworkEffect.builder()
                .flicker(true)
                .with(FireworkEffect.Type.BALL)
                .withColor(Color.BLUE)
                .build()
        )
        firework = FireworkMock(server, UUID.randomUUID(), meta)
        firework.fireworkMeta shouldBe meta
    }

    should("shotAtAngle") {
        firework.isShotAtAngle shouldBe false
        firework.isShotAtAngle = true
        firework.isShotAtAngle shouldBe true
    }

    should("set fireworkMeta") {
        val meta = FireworkMetaMock()
        meta.addEffect(
            FireworkEffect.builder()
                .flicker(true)
                .with(FireworkEffect.Type.BALL)
                .withColor(Color.BLUE)
                .build()
        )
        firework.fireworkMeta shouldNotBe meta
        firework.fireworkMeta = meta
        firework.fireworkMeta shouldBe meta
    }
})
