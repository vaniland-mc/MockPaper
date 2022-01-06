package land.vani.mockpaper.inventory

import land.vani.mockpaper.internal.fallbackNull
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder

class ShulkerBoxInventoryMock(holder: InventoryHolder?) : InventoryMock(holder, 27, InventoryType.SHULKER_BOX) {
    override fun getSnapshot(): Inventory = ShulkerBoxInventoryMock(holder).apply {
        setContents(contents.fallbackNull())
    }
}
