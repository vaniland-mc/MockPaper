package land.vani.mockpaper.inventory.meta

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.matchers.shouldBe
import land.vani.mockpaper.MockPaper
import org.bukkit.Color
import org.bukkit.FireworkEffect

class FireworkMetaMockTest : ShouldSpec({
    lateinit var meta: FireworkMetaMock

    beforeTest {
        MockPaper.mock()
        meta = FireworkMetaMock()
    }

    should("addEffect") {
        val effect = FireworkEffect.builder()
            .withColor(Color.BLUE)
            .with(FireworkEffect.Type.BALL_LARGE)
            .build()
        meta.addEffect(effect)

        meta.hasEffects() shouldBe true
        meta.effects shouldContainExactly listOf(effect)
    }

    should("addEffects(FireworkEffect...)") {
        val effect1 = FireworkEffect.builder()
            .withColor(Color.BLUE)
            .with(FireworkEffect.Type.BALL_LARGE)
            .build()
        val effect2 = FireworkEffect.builder()
            .withColor(Color.FUCHSIA)
            .with(FireworkEffect.Type.BURST)
            .build()
        meta.addEffects(effect1, effect2)

        meta.hasEffects() shouldBe true
        meta.effects shouldContainExactly listOf(effect1, effect2)
    }

    should("addEffects(Iterable<FireworkEffect>)") {
        val effect1 = FireworkEffect.builder()
            .withColor(Color.BLUE)
            .with(FireworkEffect.Type.BALL_LARGE)
            .build()
        val effect2 = FireworkEffect.builder()
            .withColor(Color.FUCHSIA)
            .with(FireworkEffect.Type.BURST)
            .build()
        meta.addEffects(listOf(effect1, effect2))

        meta.hasEffects() shouldBe true
        meta.effects shouldContainExactly listOf(effect1, effect2)
    }

    should("getEffectsSize") {
        val effect1 = FireworkEffect.builder()
            .withColor(Color.BLUE)
            .with(FireworkEffect.Type.BALL_LARGE)
            .build()
        val effect2 = FireworkEffect.builder()
            .withColor(Color.FUCHSIA)
            .with(FireworkEffect.Type.BURST)
            .build()
        meta.addEffects(effect1, effect2)

        meta.effectsSize shouldBeExactly 2
    }

    should("removeEffect") {
        val effect1 = FireworkEffect.builder()
            .withColor(Color.BLUE)
            .with(FireworkEffect.Type.BALL_LARGE)
            .build()
        val effect2 = FireworkEffect.builder()
            .withColor(Color.FUCHSIA)
            .with(FireworkEffect.Type.BURST)
            .build()
        meta.addEffects(effect1, effect2)
        meta.removeEffect(1)

        meta.effects shouldHaveSize 1
        meta.effects shouldContainExactly listOf(effect1)
    }

    should("clearEffects") {
        val effect1 = FireworkEffect.builder()
            .withColor(Color.BLUE)
            .with(FireworkEffect.Type.BALL_LARGE)
            .build()
        val effect2 = FireworkEffect.builder()
            .withColor(Color.FUCHSIA)
            .with(FireworkEffect.Type.BURST)
            .build()
        meta.addEffects(effect1, effect2)
        meta.clearEffects()

        meta.hasEffects() shouldBe false
    }

    should("power is default 0") {
        meta.power shouldBeExactly 0
    }

    should("set power") {
        meta.power = 10

        meta.power shouldBeExactly 10
    }

    should("clone") {
        val meta2 = meta.clone()

        meta shouldBe meta2
        meta2 shouldBe meta
    }
})
