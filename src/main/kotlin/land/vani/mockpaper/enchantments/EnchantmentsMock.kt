package land.vani.mockpaper.enchantments

import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment

fun registerDefaultEnchantments() {
    listOf(
        "protection",
        "fire_protection",
        "feather_falling",
        "blast_protection",
        "respiration",
        "projectile_protection",
        "aqua_affinity",
        "thorns",
        "depth_strider",
        "frost_walker",
        "binding_curse",
        "sharpness",
        "smite",
        "bane_of_arthropods",
        "knockback",
        "fire_aspect",
        "looting",
        "sweeping",
        "efficiency",
        "silk_touch",
        "unbreaking",
        "fortune",
        "power",
        "punch",
        "flame",
        "infinity",
        "luck_of_the_sea",
        "lure",
        "loyalty",
        "impaling",
        "riptide",
        "channeling",
        "multishot",
        "quick_charge",
        "piercing",
        "mending",
        "vanishing_curse",
    ).forEach {
        register(it)
    }
}

private fun register(name: String) {
    val key = NamespacedKey.minecraft(name)
    if (Enchantment.getByKey(key) == null) {
        Enchantment.registerEnchantment(EnchantmentMock(key, name))
    }
}
