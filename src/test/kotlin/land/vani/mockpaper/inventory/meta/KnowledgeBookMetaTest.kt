package land.vani.mockpaper.inventory.meta

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import land.vani.mockpaper.MockPaper
import org.bukkit.Material

class KnowledgeBookMetaTest : ShouldSpec({
    lateinit var meta: KnowledgeBookMetaMock

    beforeEach {
        MockPaper.mock()
        meta = KnowledgeBookMetaMock()
    }

    afterEach {
        MockPaper.unmock()
    }

    should("hasRecipes is default false") {
        meta.hasRecipes() shouldBe false
    }

    should("setRecipes") {
        val recipes = listOf(
            Material.BUCKET.key,
            Material.DIAMOND_HELMET.key,
        )
        meta.recipes = recipes

        meta.hasRecipes() shouldBe true
        meta.recipes shouldContainExactly recipes
    }

    should("addRecipes") {
        val recipes = listOf(
            Material.BUCKET.key,
            Material.DIAMOND_HELMET.key,
        )
        meta.addRecipe(*recipes.toTypedArray())

        meta.hasRecipes() shouldBe true
        meta.recipes shouldContainExactly recipes
    }

    should("clone") {
        val meta2 = meta.clone()

        meta shouldBe meta2
        meta2 shouldBe meta
    }
})
