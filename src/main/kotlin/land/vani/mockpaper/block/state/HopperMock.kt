package land.vani.mockpaper.block.state

import land.vani.mockpaper.UnimplementedOperationException
import land.vani.mockpaper.inventory.HopperInventoryMock
import land.vani.mockpaper.inventory.InventoryMock
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockState
import org.bukkit.block.Hopper
import org.bukkit.loot.LootTable
import java.util.UUID

/**
 * This [ContainerMock] represents a [Hopper].
 *
 * @author TheBusyBiscuit
 */
class HopperMock : ContainerMock, Hopper {
    constructor(block: Block? = null, material: Material) : super(block, material)

    constructor(block: Block) : super(block)

    constructor(state: HopperMock) : super(state)

    override fun createInventory(): InventoryMock = HopperInventoryMock(this)

    override fun getSnapshot(): BlockState = HopperMock(this)

    override fun setLootTable(table: LootTable?) {
        throw UnimplementedOperationException()
    }

    override fun getLootTable(): LootTable? {
        throw UnimplementedOperationException()
    }

    override fun setSeed(seed: Long) {
        throw UnimplementedOperationException()
    }

    override fun getSeed(): Long {
        throw UnimplementedOperationException()
    }

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

    override fun hasPendingRefill(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun getLastFilled(): Long {
        throw UnimplementedOperationException()
    }

    override fun getNextRefill(): Long {
        throw UnimplementedOperationException()
    }

    override fun setNextRefill(refillAt: Long): Long {
        throw UnimplementedOperationException()
    }
}
