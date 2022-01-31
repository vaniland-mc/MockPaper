package land.vani.mockpaper.inventory.meta

import land.vani.mockpaper.internal.asUnmodifiable
import org.bukkit.NamespacedKey
import org.bukkit.inventory.meta.KnowledgeBookMeta
import java.util.Objects

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

    override fun getRecipes(): List<NamespacedKey> =
        recipes.asUnmodifiable()

    override fun setRecipes(recipes: List<NamespacedKey>) {
        this.recipes.clear()
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

    override fun hashCode(): Int = super.hashCode() + Objects.hash(recipes)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (!super.equals(other)) return false
        if (other !is KnowledgeBookMetaMock) return false

        if (recipes != other.recipes) return false

        return true
    }
}
