package land.vani.mockpaper.block.state

import land.vani.mockpaper.UnimplementedOperationException
import land.vani.mockpaper.inventory.BarrelInventoryMock
import land.vani.mockpaper.inventory.InventoryMock
import org.bukkit.Material
import org.bukkit.block.Barrel
import org.bukkit.block.Block
import org.bukkit.block.BlockState
import org.bukkit.loot.LootTable

/**
 * This [ContainerMock] represents a [Barrel].
 *
 * @author TheBusyBiscuit
 */
class BarrelMock : ContainerMock, Barrel {
    constructor(block: Block? = null, material: Material) : super(block, material)

    constructor(block: Block) : super(block)

    constructor(state: BarrelMock) : super(state)

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

    override fun createInventory(): InventoryMock = BarrelInventoryMock(this)

    override fun getSnapshot(): BlockState = BarrelMock(this)
}
