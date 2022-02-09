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
class EnderChestMock : TileStateMock, EnderChest {
    constructor(material: Material, block: Block? = null) : super(material, block)

    constructor(block: Block) : super(block)

    constructor(state: EnderChestMock) : super(state)

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