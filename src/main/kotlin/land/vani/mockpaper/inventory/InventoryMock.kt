package land.vani.mockpaper.inventory

import land.vani.mockpaper.UnimplementedOperationException
import land.vani.mockpaper.internal.fallbackNull
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.HumanEntity
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack
import org.jetbrains.annotations.VisibleForTesting
import java.util.Objects
import kotlin.math.min

/**
 * @constructor Creates a new inventory with [size].
 */
open class InventoryMock(
    private val holder: InventoryHolder?,
    private val size: Int,
    private val type: InventoryType,
) : Inventory {
    object Crafting : InventoryMock(null, InventoryType.CRAFTING)

    /**
     * Creates a new inventory with size inherited from [InventoryType.getDefaultSize].
     */
    constructor(holder: InventoryHolder?, type: InventoryType) : this(holder, type.defaultSize, type)

    /**
     * Creates a very simple inventory with no holder, as name `Inventory`,
     * a size of 9, and an inventory type of [InventoryType.CHEST].
     */
    constructor() : this(null, 9, InventoryType.CHEST)

    private val items: Array<ItemStack?>

    init {
        if (size != type.defaultSize) {
            require(size in 9..54 && size % 9 == 0) {
                "Size for custom inventory must be a multiple of 9 between 9 and 54 slots (got $size)"
            }
        }
        items = arrayOfNulls(size)
    }

    /**
     * Get the number of times a certain item is in the inventory.
     */
    @VisibleForTesting
    fun getNumberOfItems(item: ItemStack?): Int = items.fold(0) { count, itemStack ->
        if (item?.isSimilar(itemStack) == true) {
            count + (itemStack?.amount ?: 0)
        } else {
            count
        }
    }

    override fun getSize(): Int = items.size

    override fun getMaxStackSize(): Int {
        throw UnimplementedOperationException()
    }

    override fun setMaxStackSize(size: Int) {
        throw UnimplementedOperationException()
    }

    override fun getItem(index: Int): ItemStack? = items[index]

    override fun setItem(index: Int, item: ItemStack?) {
        items[index] = item?.clone()
    }

    /**
     * Adds a single [item] to the inventory.
     *
     * Returns whatever item it couldn't add or null if that is empty.
     */
    fun addItem(item: ItemStack): ItemStack? {
        val copiedItem = item.clone()
        for (i in items.indices) {
            val oItem = items[i]
            if (oItem == null) {
                val toAdd = min(item.amount, item.maxStackSize)
                items[i] = copiedItem.clone().apply {
                    amount = toAdd
                }
                copiedItem.amount -= toAdd
            } else if (item.isSimilar(oItem) && oItem.amount < oItem.maxStackSize) {
                val toAdd = min(copiedItem.amount, copiedItem.maxStackSize - oItem.amount)
                oItem.amount += toAdd
                copiedItem.amount -= toAdd
            }

            if (copiedItem.amount == 0) {
                return null
            }
        }
        return copiedItem
    }

    override fun addItem(vararg items: ItemStack): HashMap<Int, ItemStack> =
        hashMapOf<Int, ItemStack>().apply {
            for (i in items.indices) {
                val item = items[i]
                val left = addItem(item)
                if (left != null) put(i, left)
            }
        }

    override fun removeItem(vararg items: ItemStack): HashMap<Int, ItemStack> {
        throw UnimplementedOperationException()
    }

    override fun removeItemAnySlot(vararg items: ItemStack): HashMap<Int, ItemStack> {
        throw UnimplementedOperationException()
    }

    override fun getContents(): Array<ItemStack?> = items

    override fun setContents(items: Array<out ItemStack>) {
        for (i in this.items.indices) {
            if (i < items.size) {
                this.items[i] = items[i].clone()
                    .takeUnless { it.type == Material.AIR }
            } else {
                this.items[i] = null
            }
        }
    }

    override fun getStorageContents(): Array<ItemStack> = contents.fallbackNull()

    override fun setStorageContents(items: Array<out ItemStack>) {
        setContents(items)
    }

    override fun contains(material: Material): Boolean =
        items.any { it?.type == material }

    override fun contains(item: ItemStack?): Boolean = contains(item!!.type)

    override fun contains(material: Material, amount: Int): Boolean =
        amount < 1 || getNumberOfItems(ItemStack(material)) == amount

    override fun contains(item: ItemStack?, amount: Int): Boolean =
        getNumberOfItems(item) == amount

    override fun containsAtLeast(item: ItemStack?, amount: Int): Boolean =
        getNumberOfItems(item) >= amount

    override fun all(item: ItemStack?): HashMap<Int, out ItemStack> {
        throw UnimplementedOperationException()
    }

    override fun all(material: Material): HashMap<Int, out ItemStack> {
        throw UnimplementedOperationException()
    }

    override fun first(item: ItemStack): Int {
        throw UnimplementedOperationException()
    }

    override fun first(material: Material): Int {
        throw UnimplementedOperationException()
    }

    override fun firstEmpty(): Int =
        items.indexOfFirst { it == null || it.type == Material.AIR }

    override fun isEmpty(): Boolean = items.isEmpty()

    override fun remove(item: ItemStack) {
        throw UnimplementedOperationException()
    }

    override fun remove(material: Material) {
        throw UnimplementedOperationException()
    }

    override fun clear(index: Int) {
        items[index] = null
    }

    override fun clear() {
        items.fill(null)
    }

    override fun close(): Int {
        throw UnimplementedOperationException()
    }

    override fun getViewers(): MutableList<HumanEntity> {
        throw UnimplementedOperationException()
    }

    override fun getType(): InventoryType = type

    override fun getHolder(): InventoryHolder? = holder

    override fun getHolder(useSnapshot: Boolean): InventoryHolder? =
        if (useSnapshot) {
            InventoryHolder {
                InventoryMock(holder, size, type).apply {
                    setContents(contents.fallbackNull())
                }
            }
        } else {
            holder
        }

    override fun iterator(): MutableListIterator<ItemStack> = items.filterNotNull().toMutableList().listIterator()

    override fun iterator(index: Int): MutableListIterator<ItemStack> {
        throw UnimplementedOperationException()
    }

    override fun getLocation(): Location? {
        throw UnimplementedOperationException()
    }

    open fun getSnapshot(): Inventory = InventoryMock(holder, size, type)
        .apply {
            setContents(contents.fallbackNull())
        }

    override fun equals(other: Any?): Boolean {
        if (other !is InventoryMock) return false

        if (type != other.type) return false
        if (holder != other.holder) return false
        if (size != other.size) return false
        if (!contents.contentEquals(other.contents)) return false

        return true
    }

    override fun hashCode(): Int = Objects.hash(
        type,
        holder,
        size,
        contents
    )
}
