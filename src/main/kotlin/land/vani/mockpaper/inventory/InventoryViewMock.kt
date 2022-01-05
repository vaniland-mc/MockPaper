package land.vani.mockpaper.inventory

import org.bukkit.entity.HumanEntity
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryView
import org.jetbrains.annotations.VisibleForTesting

open class InventoryViewMock(
    private var player: HumanEntity,
    private var name: String,
    private var topInventory: Inventory,
    private var bottomInventory: Inventory,
    private var type: InventoryType,
) : InventoryView() {
    override fun getTopInventory(): Inventory = topInventory

    /**
     * Sets the top [inventory].
     */
    @VisibleForTesting
    fun setTopInventory(inventory: Inventory) {
        topInventory = inventory
    }

    override fun getBottomInventory(): Inventory = bottomInventory

    /**
     * Sets the bottom [inventory].
     */
    @VisibleForTesting
    fun setBottomInventory(inventory: Inventory) {
        bottomInventory = inventory
    }

    override fun getPlayer(): HumanEntity = player

    /**
     * Sets the [player] viewing.
     */
    @VisibleForTesting
    fun setPlayer(player: HumanEntity) {
        this.player = player
    }

    override fun getType(): InventoryType = type

    /**
     * Sets the [type] of inventory view.
     */
    @VisibleForTesting
    fun setType(type: InventoryType) {
        this.type = type
    }

    override fun getTitle(): String = name
}
