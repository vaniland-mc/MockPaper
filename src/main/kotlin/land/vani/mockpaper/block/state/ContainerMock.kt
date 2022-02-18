package land.vani.mockpaper.block.state

import land.vani.mockpaper.NameableHolder
import land.vani.mockpaper.inventory.InventoryMock
import org.bukkit.Material
import org.bukkit.Nameable
import org.bukkit.block.Block
import org.bukkit.block.Container
import org.bukkit.inventory.Inventory

/**
 * The [ContainerMock] is an extension of a [TileStateMock] which can also hold an
 * [Inventory].
 *
 * @author TheBusyBiscuit
 */
abstract class ContainerMock(material: Material, block: Block? = null) :
    TileStateMock(material, block),
    Container,
    Nameable by NameableHolder() {
    protected constructor(block: Block) : this(block.type, block)

    protected constructor(state: ContainerMock) : this(state.material, state.block)

    private val inventory: InventoryMock by lazy {
        createInventory()
    }
    private var lock = ""

    protected abstract fun createInventory(): InventoryMock

    abstract override fun getSnapshot(): BlockStateMock

    override fun isLocked(): Boolean = lock.isNotEmpty()

    override fun setLock(key: String?) {
        lock = key ?: ""
    }

    override fun getLock(): String = lock

    override fun getInventory(): Inventory = inventory

    override fun getSnapshotInventory(): Inventory = inventory.getSnapshot()
}
