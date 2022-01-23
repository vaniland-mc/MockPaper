package land.vani.mockpaper.inventory

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import land.vani.mockpaper.MockPaper
import land.vani.mockpaper.ServerMock
import land.vani.mockpaper.inventory.meta.BookMetaMock
import land.vani.mockpaper.inventory.meta.EnchantedBookMetaMock
import land.vani.mockpaper.inventory.meta.FireworkEffectMetaMock
import land.vani.mockpaper.inventory.meta.FireworkMetaMock
import land.vani.mockpaper.inventory.meta.ItemMetaMock
import land.vani.mockpaper.inventory.meta.KnowledgeBookMetaMock
import land.vani.mockpaper.inventory.meta.LeatherArmorMetaMock
import land.vani.mockpaper.inventory.meta.PotionMetaMock
import land.vani.mockpaper.inventory.meta.SkullMetaMock
import land.vani.mockpaper.inventory.meta.SuspiciousStewMetaMock
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class ItemFactoryTest : ShouldSpec({
    lateinit var server: ServerMock
    lateinit var factory: ItemFactoryMock

    beforeTest {
        server = MockPaper.mock()
        factory = server.itemFactory as ItemFactoryMock
    }

    should("getItemMeta") {
        factory.getItemMeta(Material.DIRT).shouldBeInstanceOf<ItemMetaMock>()
        factory.getItemMeta(Material.PLAYER_HEAD).shouldBeInstanceOf<SkullMetaMock>()

        factory.getItemMeta(Material.WRITABLE_BOOK).shouldBeInstanceOf<BookMetaMock>()
        factory.getItemMeta(Material.WRITTEN_BOOK).shouldBeInstanceOf<BookMetaMock>()
        factory.getItemMeta(Material.ENCHANTED_BOOK).shouldBeInstanceOf<EnchantedBookMetaMock>()
        factory.getItemMeta(Material.KNOWLEDGE_BOOK).shouldBeInstanceOf<KnowledgeBookMetaMock>()

        factory.getItemMeta(Material.FIREWORK_STAR).shouldBeInstanceOf<FireworkEffectMetaMock>()
        factory.getItemMeta(Material.FIREWORK_ROCKET).shouldBeInstanceOf<FireworkMetaMock>()

        factory.getItemMeta(Material.SUSPICIOUS_STEW).shouldBeInstanceOf<SuspiciousStewMetaMock>()
        factory.getItemMeta(Material.POTION).shouldBeInstanceOf<PotionMetaMock>()
        factory.getItemMeta(Material.LEATHER_CHESTPLATE).shouldBeInstanceOf<LeatherArmorMetaMock>()
    }

    context("isApplicable") {
        should("correct itemStack") {
            val itemStack = ItemStack(Material.DIRT)
            val meta = factory.getItemMeta(Material.DIRT)

            factory.isApplicable(meta, itemStack)
        }

        should("correct type") {
            val meta = factory.getItemMeta(Material.DIRT)

            factory.isApplicable(meta, Material.DIRT) shouldBe true
        }
    }

    context("equals") {
        should("(null, null) should be true") {
            factory.equals(null, null) shouldBe true
        }

        should("(meta, null) should be false") {
            val meta = factory.getItemMeta(Material.DIRT)

            factory.equals(meta, null) shouldBe false
        }

        should("(null, meta) should be false") {
            val meta = factory.getItemMeta(Material.DIRT)

            factory.equals(null, meta) shouldBe false
        }

        should("compatible metas should be true") {
            val meta1 = factory.getItemMeta(Material.DIRT)
            val meta2 = factory.getItemMeta(Material.DIRT)

            factory.equals(meta1, meta2) shouldBe true
        }

        should("incompatible metas should be false") {
            val meta1 = factory.getItemMeta(Material.DIRT)
            val meta2 = factory.getItemMeta(Material.POTION)

            factory.equals(meta1, meta2) shouldBe false
        }
    }

    context("asMetaFor") {
        should("dirt itemMeta on dirt itemStack") {
            val meta = factory.getItemMeta(Material.DIRT)
            meta.displayName(Component.text("my piece of dirt"))
            val newMeta = factory.asMetaFor(meta, ItemStack(Material.DIRT))

            meta shouldBe newMeta
        }

        should("dirt itemMeta on dirt material") {
            val meta = factory.getItemMeta(Material.DIRT)
            meta.displayName(Component.text("my piece of dirt"))
            val newMeta = factory.asMetaFor(meta, Material.DIRT)

            meta shouldBe newMeta
        }
    }
})
