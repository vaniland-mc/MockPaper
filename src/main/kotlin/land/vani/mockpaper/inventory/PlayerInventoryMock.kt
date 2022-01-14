package land.vani.mockpaper.inventory

import land.vani.mockpaper.UnimplementedOperationException
import land.vani.mockpaper.internal.fallbackNull
import org.bukkit.Material
import org.bukkit.entity.HumanEntity
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory

class PlayerInventoryMock(holder: HumanEntity?) : InventoryMock(holder, InventoryType.PLAYER), PlayerInventory {
    companion object {
        const val SLOT_BAR = 9
        const val BOOTS = 36
        const val LEGGINGS = 37
        const val CHEST_PLATE = 38
        const val HELMET = 39
        const val OFF_HAND = 40
    }

    private var mainHandSlot = 0

    override fun getHolder(): HumanEntity? = super.getHolder() as HumanEntity?

    override fun getStorageContents(): Array<ItemStack> =
        contents.copyOfRange(0, 36).fallbackNull()

    override fun setStorageContents(items: Array<out ItemStack>) {
        throw UnimplementedOperationException()
    }

    override fun getArmorContents(): Array<ItemStack> =
        contents.copyOfRange(BOOTS, BOOTS + 4).fallbackNull()

    override fun getExtraContents(): Array<ItemStack> =
        contents.copyOfRange(OFF_HAND, OFF_HAND + 1).fallbackNull()

    override fun getHelmet(): ItemStack? = getItem(HELMET)

    override fun getChestplate(): ItemStack? = getItem(CHEST_PLATE)

    override fun getLeggings(): ItemStack? = getItem(LEGGINGS)

    override fun getBoots(): ItemStack? = getItem(BOOTS)

    override fun setItem(slot: EquipmentSlot, item: ItemStack?) {
        when (slot) {
            EquipmentSlot.HAND -> setItemInMainHand(item)
            EquipmentSlot.OFF_HAND -> setItemInOffHand(item)
            EquipmentSlot.FEET -> boots = item
            EquipmentSlot.LEGS -> leggings = item
            EquipmentSlot.CHEST -> chestplate = item
            EquipmentSlot.HEAD -> helmet = item
            else -> throw IllegalArgumentException("Unknown EquipmentType")
        }
    }

    override fun getItem(slot: EquipmentSlot): ItemStack? =
        when (slot) {
            EquipmentSlot.HAND -> itemInMainHand
            EquipmentSlot.OFF_HAND -> itemInOffHand
            EquipmentSlot.FEET -> boots
            EquipmentSlot.LEGS -> leggings
            EquipmentSlot.CHEST -> chestplate
            EquipmentSlot.HEAD -> helmet
            else -> throw IllegalArgumentException("Unknown EquipmentType")
        }

    override fun setArmorContents(items: Array<ItemStack?>?) {
        requireNotNull(items) { "ItemStack array must not be null" }
        require(items.size <= 4) { "ItemStack array is too large (max: 4, but was: ${items.size})" }
        val copied = if (items.size == 4) items else items.copyOf(4)
        boots = copied[0]
        leggings = copied[1]
        chestplate = copied[2]
        helmet = copied[3]
    }

    override fun setExtraContents(items: Array<ItemStack?>?) {
        requireNotNull(items) { "ItemStack array must not be null" }
        require(items.size == 1) { "ItemStack array too large (max: 1, but was: ${items.size})" }
        val copied = if (items.size == 1) items else items.copyOf(1)
        setItemInOffHand(copied[0])
    }

    override fun setHelmet(helmet: ItemStack?) {
        setItem(HELMET, helmet)
    }

    override fun setChestplate(chestplate: ItemStack?) {
        setItem(CHEST_PLATE, chestplate)
    }

    override fun setLeggings(leggings: ItemStack?) {
        setItem(LEGGINGS, leggings)
    }

    override fun setBoots(boots: ItemStack?) {
        setItem(BOOTS, boots)
    }

    override fun getItemInMainHand(): ItemStack =
        getItem(SLOT_BAR + mainHandSlot) ?: ItemStack(Material.AIR)

    override fun setItemInMainHand(item: ItemStack?) {
        setItem(SLOT_BAR + mainHandSlot, item)
    }

    override fun getItemInOffHand(): ItemStack =
        getItem(OFF_HAND) ?: ItemStack(Material.AIR)

    override fun setItemInOffHand(item: ItemStack?) {
        setItem(OFF_HAND, item)
    }

    override fun getItemInHand(): ItemStack = itemInMainHand

    override fun setItemInHand(stack: ItemStack?) = setItemInMainHand(stack)

    override fun getHeldItemSlot(): Int = mainHandSlot

    override fun setHeldItemSlot(slot: Int) {
        require(slot in 0..8) { "Slot should be within [0-8] (was: $slot)" }
        mainHandSlot = slot
    }
}
