package land.vani.mockpaper.inventory

import land.vani.mockpaper.internal.fallbackNull
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder

class ChestInventoryMock(holder: InventoryHolder?, size: Int) : InventoryMock(holder, size, InventoryType.CHEST) {
    override fun getSnapshot(): Inventory = ChestInventoryMock(holder, size).apply {
        setContents(contents.fallbackNull())
    }
}
