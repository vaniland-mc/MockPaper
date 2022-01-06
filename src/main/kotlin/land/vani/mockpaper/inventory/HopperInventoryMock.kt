package land.vani.mockpaper.inventory

import land.vani.mockpaper.internal.fallbackNull
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder

class HopperInventoryMock(holder: InventoryHolder?) : InventoryMock(holder, 9, InventoryType.HOPPER) {
    override fun getSnapshot(): Inventory = HopperInventoryMock(holder).apply {
        setContents(contents.fallbackNull())
    }
}
