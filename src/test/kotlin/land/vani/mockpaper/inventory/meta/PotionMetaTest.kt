package land.vani.mockpaper.inventory.meta

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldNotContainExactly
import io.kotest.matchers.shouldBe
import land.vani.mockpaper.MockPaper
import org.bukkit.Color
import org.bukkit.potion.PotionData
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.potion.PotionType

class PotionMetaTest : ShouldSpec({
    lateinit var meta: PotionMetaMock

    beforeTest {
        MockPaper.mock()
        meta = PotionMetaMock()
    }

    context("basePotionData") {
        should("basePotionData is default uncraftable") {
            meta.basePotionData.type shouldBe PotionType.UNCRAFTABLE
        }

        should("set basePotionData") {
            val data = PotionData(PotionType.INSTANT_HEAL, false, true)
            meta.basePotionData = data

            meta.basePotionData shouldBe data
        }
    }

    context("customEffects") {
        should("hasCustomEffects is default false") {
            meta.hasCustomEffects() shouldBe false
        }

        should("hasCustomEffect(PotionEffectType) is default all false") {
            PotionEffectType.values().forEach {
                meta.hasCustomEffect(it) shouldBe false
            }
        }

        context("addCustomEffect") {
            should("normal") {
                val effect = PotionEffect(PotionEffectType.SPEED, 40, 1)
                meta.addCustomEffect(effect, false)

                meta.hasCustomEffects() shouldBe true
                meta.hasCustomEffect(PotionEffectType.SPEED) shouldBe true
                meta.customEffects shouldContain effect
            }

            should("overwrite") {
                val effect = PotionEffect(PotionEffectType.SPEED, 40, 1)
                val effect2 = PotionEffect(PotionEffectType.SPEED, 60, 2)
                val effect3 = PotionEffect(PotionEffectType.SPEED, 60, 1)

                meta.addCustomEffect(effect, true)
                meta.customEffects shouldContainExactly listOf(effect)

                meta.addCustomEffect(effect2, false)
                meta.customEffects shouldNotContainExactly listOf(effect2)

                meta.addCustomEffect(effect2, true)
                meta.customEffects shouldContainExactly listOf(effect2)

                meta.addCustomEffect(effect3, true)
                meta.customEffects shouldNotContainExactly listOf(effect3)
            }
        }

        should("removeCustomEffect") {
            val effect = PotionEffect(PotionEffectType.SPEED, 40, 1)
            meta.addCustomEffect(effect, false)
            meta.removeCustomEffect(PotionEffectType.SPEED)

            meta.hasCustomEffect(PotionEffectType.SPEED) shouldBe false
        }

        should("clearCustomEffects") {
            val effect = PotionEffect(PotionEffectType.SPEED, 40, 1)
            meta.addCustomEffect(effect, false)
            meta.clearCustomEffects()

            meta.hasCustomEffects() shouldBe false
        }
    }

    context("color") {
        should("hasColor is default false") {
            meta.hasColor() shouldBe false
        }

        should("set color") {
            meta.color = Color.AQUA

            meta.hasColor() shouldBe true
            meta.color shouldBe Color.AQUA
        }

        should("set color to null") {
            meta.color = Color.AQUA
            meta.color = null

            meta.hasColor() shouldBe false
        }
    }

    should("clone") {
        val meta2 = meta.clone()

        meta shouldBe meta2
        meta2 shouldBe meta
    }
})
