package land.vani.mockpaper.inventory

import land.vani.mockpaper.internal.fallbackNull
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder

class DispenserInventoryMock(holder: InventoryHolder?) : InventoryMock(holder, 9, InventoryType.DISPENSER) {
    override fun getSnapshot(): Inventory = DispenserInventoryMock(holder).apply {
        setContents(contents.fallbackNull())
    }
}
