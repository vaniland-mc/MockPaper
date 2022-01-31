package land.vani.mockpaper.internal

import org.bukkit.Material
import org.bukkit.inventory.ItemStack

internal fun Array<ItemStack?>.fallbackNull(fallback: Material = Material.AIR): Array<ItemStack> =
    map { it ?: ItemStack(fallback) }.toTypedArray()
