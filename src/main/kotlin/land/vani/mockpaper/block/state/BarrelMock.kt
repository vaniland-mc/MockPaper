package land.vani.mockpaper.block.state

import land.vani.mockpaper.UnimplementedOperationException
import land.vani.mockpaper.inventory.BarrelInventoryMock
import land.vani.mockpaper.inventory.InventoryMock
import org.bukkit.Material
import org.bukkit.block.Barrel
import org.bukkit.block.Block
import org.bukkit.loot.LootTable

/**
 * This [ContainerMock] represents a [Barrel].
 *
 * @author TheBusyBiscuit
 */
class BarrelMock(material: Material, block: Block? = null) : ContainerMock(material, block), Barrel {
    constructor(block: Block) : this(block.type, block)

    constructor(state: BarrelMock) : this(state.material, state.block)

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

    override fun getSnapshot(): BlockStateMock = BarrelMock(this)
}
