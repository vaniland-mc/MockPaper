package land.vani.mockpaper.inventory

import org.bukkit.entity.HumanEntity
import org.bukkit.inventory.Inventory

class PlayerInventoryViewMock(player: HumanEntity, topInventory: Inventory) :
    InventoryViewMock(player, "Inventory", topInventory, player.inventory, topInventory.type)
