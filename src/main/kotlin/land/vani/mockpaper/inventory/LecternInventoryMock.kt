package land.vani.mockpaper.inventory

import land.vani.mockpaper.internal.fallbackNull
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder

class LecternInventoryMock(holder: InventoryHolder?) : InventoryMock(holder, InventoryType.LECTERN) {
    override fun getSnapshot(): Inventory = LecternInventoryMock(holder).apply {
        setContents(contents.fallbackNull())
    }
}
