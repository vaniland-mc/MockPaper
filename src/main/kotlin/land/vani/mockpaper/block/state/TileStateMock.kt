package land.vani.mockpaper.block.state

import land.vani.mockpaper.persistence.PersistentDataContainerMock
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.TileState
import org.bukkit.persistence.PersistentDataContainer

/**
 * This [BlockStateMock] represents a [TileState] which is capable of storing persistent
 * data using a [PersistentDataContainerMock].
 *
 * @author TheBusyBiscuit
 */
abstract class TileStateMock(material: Material, block: Block? = null) :
    BlockStateMock(material, block), TileState {
    private val container = PersistentDataContainerMock()

    override fun getPersistentDataContainer(): PersistentDataContainer = container

    abstract fun getSnapshot(): BlockStateMock
}
