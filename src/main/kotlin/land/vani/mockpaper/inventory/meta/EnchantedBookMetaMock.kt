package land.vani.mockpaper.inventory.meta

import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.meta.EnchantmentStorageMeta

class EnchantedBookMetaMock : ItemMetaMock, EnchantmentStorageMeta {
    private val storedEnchantments: MutableMap<Enchantment, Int> = mutableMapOf()

    constructor() : super()

    constructor(meta: EnchantmentStorageMeta) : super(meta) {
        storedEnchantments += meta.storedEnchants
    }

    override fun hasStoredEnchants(): Boolean = storedEnchantments.isNotEmpty()

    override fun hasStoredEnchant(ench: Enchantment): Boolean = ench in storedEnchantments

    override fun getStoredEnchantLevel(ench: Enchantment): Int = storedEnchantments[ench] ?: 0

    override fun getStoredEnchants(): Map<Enchantment, Int> = storedEnchantments.toMap()

    override fun addStoredEnchant(ench: Enchantment, level: Int, ignoreLevelRestriction: Boolean): Boolean {
        if (!ignoreLevelRestriction && level !in ench.startLevel..ench.maxLevel) {
            return false
        }
        val prev = storedEnchantments.put(ench, level)
        return prev == null || prev != level
    }

    override fun removeStoredEnchant(ench: Enchantment): Boolean =
        storedEnchantments.remove(ench) != null

    override fun hasConflictingStoredEnchant(ench: Enchantment): Boolean =
        storedEnchantments.keys.any { it.conflictsWith(ench) }

    override fun clone(): EnchantedBookMetaMock = (super.clone() as EnchantedBookMetaMock)
        .apply {
            storedEnchantments += this@EnchantedBookMetaMock.storedEnchantments
        }
}
