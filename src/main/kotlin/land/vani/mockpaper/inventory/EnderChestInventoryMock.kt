package land.vani.mockpaper.inventory

import land.vani.mockpaper.internal.fallbackNull
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder

class EnderChestInventoryMock(holder: InventoryHolder?) : InventoryMock(holder, 27, InventoryType.ENDER_CHEST) {
    override fun getSnapshot(): Inventory = EnderChestInventoryMock(holder).apply {
        setContents(contents.fallbackNull())
    }
}
