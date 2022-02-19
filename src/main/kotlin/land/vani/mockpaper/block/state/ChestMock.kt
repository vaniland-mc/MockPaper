package land.vani.mockpaper.block.state

import land.vani.mockpaper.UnimplementedOperationException
import land.vani.mockpaper.inventory.ChestInventoryMock
import land.vani.mockpaper.inventory.InventoryMock
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.Chest
import org.bukkit.inventory.Inventory
import org.bukkit.loot.LootTable
import java.util.UUID

/**
 * This [ContainerMock] represents a [Chest].
 *
 * @author TheBusyBiscuit
 */
class ChestMock(material: Material, block: Block? = null) : ContainerMock(material, block), Chest {
    constructor(block: Block) : this(block.type, block)

    constructor(state: ChestMock) : this(
        state.material,
        if (state.isPlaced) {
            state.block
        } else {
            null
        }
    )

    override fun getLootTable(): LootTable? {
        throw UnimplementedOperationException()
    }

    override fun setLootTable(table: LootTable?) {
        throw UnimplementedOperationException()
    }

    override fun getSeed(): Long {
        throw UnimplementedOperationException()
    }

    override fun setSeed(seed: Long) {
        throw UnimplementedOperationException()
    }

    override fun open() {
        throw UnimplementedOperationException()
    }

    override fun isOpen(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun close() {
        throw UnimplementedOperationException()
    }

    override fun getBlockInventory(): Inventory = inventory

    override fun createInventory(): InventoryMock = ChestInventoryMock(this, 27)

    override fun getSnapshot(): BlockStateMock = ChestMock(this)

    override fun isRefillEnabled(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun hasBeenFilled(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun hasPlayerLooted(player: UUID): Boolean {
        throw UnimplementedOperationException()
    }

    override fun getLastLooted(player: UUID): Long? {
        throw UnimplementedOperationException()
    }

    override fun setHasPlayerLooted(player: UUID, looted: Boolean): Boolean {
        throw UnimplementedOperationException()
    }

    override fun getLastFilled(): Long {
        throw UnimplementedOperationException()
    }

    override fun hasPendingRefill(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun getNextRefill(): Long {
        throw UnimplementedOperationException()
    }

    override fun setNextRefill(refillAt: Long): Long {
        throw UnimplementedOperationException()
    }
}
