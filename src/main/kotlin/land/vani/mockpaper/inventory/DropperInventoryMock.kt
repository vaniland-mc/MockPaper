package land.vani.mockpaper.inventory

import land.vani.mockpaper.internal.fallbackNull
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder

class DropperInventoryMock(holder: InventoryHolder?) : InventoryMock(holder, 9, InventoryType.DROPPER) {
    override fun getSnapshot(): Inventory = DropperInventoryMock(holder).apply {
        setContents(contents.fallbackNull())
    }
}
