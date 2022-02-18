package land.vani.mockpaper.block.state

import land.vani.mockpaper.UnimplementedOperationException
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.EnderChest

/**
 * This [ContainerMock] represents a [EnderChest].
 *
 * @author TheBusyBiscuit
 */
class EnderChestMock(material: Material, block: Block? = null) : TileStateMock(material, block), EnderChest {
    constructor(block: Block) : this(block.type, block)

    constructor(state: EnderChestMock) : this(state.material, state.block)

    override fun getSnapshot(): BlockStateMock = EnderChestMock(this)

    override fun open() {
        throw UnimplementedOperationException()
    }

    override fun close() {
        throw UnimplementedOperationException()
    }

    override fun isOpen(): Boolean {
        throw UnimplementedOperationException()
    }
}
