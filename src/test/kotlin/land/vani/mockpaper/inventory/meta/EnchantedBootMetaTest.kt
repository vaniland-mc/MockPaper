package land.vani.mockpaper.inventory.meta

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.matchers.maps.shouldContainExactly
import io.kotest.matchers.shouldBe
import land.vani.mockpaper.ServerMock
import org.bukkit.enchantments.Enchantment

class EnchantedBootMetaTest : ShouldSpec({
    lateinit var enchantment1: Enchantment
    lateinit var enchantment2: Enchantment

    lateinit var meta: EnchantedBookMetaMock

    beforeTest {
        ServerMock()
        meta = EnchantedBookMetaMock()
        enchantment1 = Enchantment.DURABILITY
        enchantment2 = Enchantment.DAMAGE_ALL
    }

    context("enchants") {
        should("hasStoredEnchants is default false") {
            meta.hasStoredEnchants() shouldBe false
        }

        should("hasEnchant is default all false") {
            Enchantment.values().forEach {
                meta.hasEnchant(it) shouldBe false
            }
        }

        should("getStoredEnchantLevel") {
            meta.addStoredEnchant(enchantment1, 1, false) shouldBe true
            meta.getStoredEnchantLevel(enchantment1) shouldBeExactly 1
        }

        context("addStoredEnchant") {
            should("addStoredEnchant safety") {
                meta.addStoredEnchant(enchantment1, 1, false) shouldBe true
                meta.addStoredEnchant(enchantment2, 1, false) shouldBe true

                meta.storedEnchants shouldContainExactly mapOf(enchantment1 to 1, enchantment2 to 1)
            }

            should("addStoredEnchant unsafely") {
                meta.addStoredEnchant(enchantment1, 100, true) shouldBe true
                meta.addStoredEnchant(enchantment2, 200, true) shouldBe true

                meta.storedEnchants shouldContainExactly mapOf(enchantment1 to 100, enchantment2 to 200)
            }

            should("addStoredEnchant already added") {
                meta.addStoredEnchant(enchantment1, 1, false) shouldBe true

                meta.addStoredEnchant(enchantment1, 2, false) shouldBe false
            }
        }
    }
})
