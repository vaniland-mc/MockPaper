package land.vani.mockpaper.inventory.meta

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import land.vani.mockpaper.MockPaper
import org.bukkit.Color
import org.bukkit.FireworkEffect

class FireworkEffectMetaMockTest : ShouldSpec({
    lateinit var meta: FireworkEffectMetaMock

    beforeTest {
        MockPaper.mock()
        meta = FireworkEffectMetaMock()
    }

    should("hasEffect is default false") {
        meta.hasEffect() shouldBe false
    }

    should("setEffect") {
        val effect = FireworkEffect.builder().withColor(Color.BLUE)
            .with(FireworkEffect.Type.BALL_LARGE)
            .build()
        meta.effect = effect

        meta.hasEffect() shouldBe true
        meta.effect shouldBe effect
    }

    should("clone") {
        val meta2 = meta.clone()
        meta shouldBe meta2
        meta2 shouldBe meta
    }
})
