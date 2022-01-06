package land.vani.mockpaper.inventory

import land.vani.mockpaper.internal.fallbackNull
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder

class BarrelInventoryMock(holder: InventoryHolder?) : InventoryMock(holder, 27, InventoryType.BARREL) {
    override fun getSnapshot(): Inventory = BarrelInventoryMock(holder).apply {
        setContents(contents.fallbackNull())
    }
}
