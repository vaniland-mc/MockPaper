package land.vani.mockpaper.block.state

import land.vani.mockpaper.NameableHolder
import land.vani.mockpaper.inventory.InventoryMock
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockState
import org.bukkit.block.Container
import org.bukkit.inventory.Inventory

/**
 * The [ContainerMock] is an extension of a [TileStateMock] which can also hold an
 * [Inventory].
 *
 * @author TheBusyBiscuit
 */
abstract class ContainerMock : TileStateMock, Container {
    constructor(material: Material, block: Block? = null) : super(material, block)

    protected constructor(block: Block) : this(block.type, block)

    protected constructor(state: ContainerMock) : super(state)

    private val inventory: InventoryMock by lazy {
        createInventory()
    }
    private var lock = ""
    private val nameable = NameableHolder()

    protected abstract fun createInventory(): InventoryMock

    abstract override fun getSnapshot(): BlockState

    override fun isLocked(): Boolean = lock.isNotEmpty()

    override fun setLock(key: String?) {
        lock = key ?: ""
    }

    override fun getLock(): String = lock

    override fun getInventory(): Inventory = inventory

    override fun getSnapshotInventory(): Inventory = inventory.getSnapshot()

    override fun customName(): Component? = nameable.customName()

    override fun customName(customName: Component?) {
        nameable.customName(customName)
    }

    override fun getCustomName(): String? = nameable.customName

    override fun setCustomName(name: String?) {
        nameable.customName = name
    }
}
