package land.vani.mockpaper.inventory.meta

import land.vani.mockpaper.internal.asUnmodifiable
import org.bukkit.NamespacedKey
import org.bukkit.inventory.meta.KnowledgeBookMeta

class KnowledgeBookMetaMock : ItemMetaMock, KnowledgeBookMeta {
    companion object {
        private const val MAX_RECIPES = 32767
    }

    private val recipes: MutableList<NamespacedKey> = mutableListOf()

    constructor() : super()

    constructor(meta: KnowledgeBookMeta) : super(meta) {
        recipes += meta.recipes
    }

    override fun hasRecipes(): Boolean = recipes.isNotEmpty()

    override fun getRecipes(): MutableList<NamespacedKey> =
        recipes.asUnmodifiable()

    override fun setRecipes(recipes: MutableList<NamespacedKey>) {
        recipes.clear()
        addRecipe(*recipes.toTypedArray())
    }

    override fun addRecipe(vararg recipes: NamespacedKey) {
        recipes.forEach { recipe ->
            if (this.recipes.size >= MAX_RECIPES) return
            this.recipes += recipe
        }
    }

    override fun clone(): KnowledgeBookMetaMock = (super.clone() as KnowledgeBookMetaMock)
        .apply {
            recipes += this@KnowledgeBookMetaMock.recipes
        }
}
