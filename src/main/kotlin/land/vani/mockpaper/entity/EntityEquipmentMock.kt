package land.vani.mockpaper.entity

import land.vani.mockpaper.UnimplementedOperationException
import org.bukkit.Material
import org.bukkit.entity.Entity
import org.bukkit.inventory.EntityEquipment
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack

/**
 * This mocks the [EntityEquipment] of a [LivingEntityMock].
 *
 * Note that not every [org.bukkit.entity.LivingEntity] has [EntityEquipment],
 * So only implement this where necessary.
 *
 * @author TheBusyBiscuit
 */
class EntityEquipmentMock(
    private val holder: LivingEntityMock,
) : EntityEquipment {
    private var itemInMainHand: ItemStack = ItemStack(Material.AIR)
    private var itemInOffHand: ItemStack = ItemStack(Material.AIR)
    private var helmet: ItemStack? = null
    private var chestPlate: ItemStack? = null
    private var leggings: ItemStack? = null
    private var boots: ItemStack? = null

    override fun setItem(slot: EquipmentSlot, item: ItemStack?) {
        setItem(slot, item, false)
    }

    override fun setItem(slot: EquipmentSlot, item: ItemStack?, silent: Boolean) {
        when (slot) {
            EquipmentSlot.HEAD -> setHelmet(item, silent)
            EquipmentSlot.CHEST -> setChestplate(item, silent)
            EquipmentSlot.LEGS -> setLeggings(item, silent)
            EquipmentSlot.FEET -> setBoots(item, silent)
            EquipmentSlot.HAND -> setItemInMainHand(item, silent)
            EquipmentSlot.OFF_HAND -> setItemInOffHand(item, silent)
            else -> throw UnimplementedOperationException("EquipmentSlot '$slot' has no implementation!")
        }
    }

    override fun getItem(slot: EquipmentSlot): ItemStack =
        when (slot) {
            EquipmentSlot.HEAD -> helmet
            EquipmentSlot.CHEST -> chestplate
            EquipmentSlot.LEGS -> leggings
            EquipmentSlot.FEET -> boots
            EquipmentSlot.HAND -> itemInMainHand
            EquipmentSlot.OFF_HAND -> itemInOffHand
        } ?: ItemStack(Material.AIR)

    override fun getItemInMainHand(): ItemStack = itemInMainHand

    override fun setItemInMainHand(item: ItemStack?) {
        setItemInMainHand(item, false)
    }

    override fun setItemInMainHand(item: ItemStack?, silent: Boolean) {
        itemInMainHand = item ?: ItemStack(Material.AIR)
    }

    override fun getItemInOffHand(): ItemStack = itemInOffHand

    override fun setItemInOffHand(item: ItemStack?) {
        setItemInOffHand(item, false)
    }

    override fun setItemInOffHand(item: ItemStack?, silent: Boolean) {
        itemInOffHand = item ?: ItemStack(Material.AIR)
    }

    override fun getHelmet(): ItemStack? = helmet

    override fun setHelmet(helmet: ItemStack?) {
        setHelmet(helmet, false)
    }

    override fun setHelmet(helmet: ItemStack?, silent: Boolean) {
        this.helmet = helmet
    }

    override fun getChestplate(): ItemStack? = chestPlate

    override fun setChestplate(chestplate: ItemStack?) {
        setChestplate(chestplate, false)
    }

    override fun setChestplate(chestplate: ItemStack?, silent: Boolean) {
        this.chestPlate = chestplate
    }

    override fun getLeggings(): ItemStack? = leggings

    override fun setLeggings(leggings: ItemStack?) {
        setLeggings(leggings, false)
    }

    override fun setLeggings(leggings: ItemStack?, silent: Boolean) {
        this.leggings = leggings
    }

    override fun getBoots(): ItemStack? = boots

    override fun setBoots(boots: ItemStack?) {
        setBoots(boots, false)
    }

    override fun setBoots(boots: ItemStack?, silent: Boolean) {
        this.boots = boots
    }

    @Suppress("UNCHECKED_CAST")
    // Maybe return type is `Array<ItemStack?>` but Java code
    // `@NotNull ItemStack[]` is converted to Kotlin as `Array<ItemStack>`
    override fun getArmorContents(): Array<ItemStack> =
        arrayOf(boots, leggings, chestPlate, helmet) as Array<ItemStack>

    override fun setArmorContents(items: Array<out ItemStack>) {
        boots = if (items.isNotEmpty()) items[0] else null
        leggings = if (items.size >= 2) items[1] else null
        chestPlate = if (items.size >= 3) items[2] else null
        helmet = if (items.size >= 4) items[3] else null
    }

    override fun clear() {
        itemInMainHand = ItemStack(Material.AIR)
        itemInOffHand = ItemStack(Material.AIR)

        helmet = null
        chestPlate = null
        leggings = null
        boots = null
    }

    override fun getItemInHand(): ItemStack = itemInMainHand

    override fun setItemInHand(stack: ItemStack?) {
        setItemInMainHand(stack)
    }

    override fun getItemInHandDropChance(): Float {
        throw UnimplementedOperationException()
    }

    override fun setItemInHandDropChance(chance: Float) {
        throw UnimplementedOperationException()
    }

    override fun getItemInMainHandDropChance(): Float {
        throw UnimplementedOperationException()
    }

    override fun setItemInMainHandDropChance(chance: Float) {
        throw UnimplementedOperationException()
    }

    override fun getItemInOffHandDropChance(): Float {
        throw UnimplementedOperationException()
    }

    override fun setItemInOffHandDropChance(chance: Float) {
        throw UnimplementedOperationException()
    }

    override fun getHelmetDropChance(): Float {
        throw UnimplementedOperationException()
    }

    override fun setHelmetDropChance(chance: Float) {
        throw UnimplementedOperationException()
    }

    override fun getChestplateDropChance(): Float {
        throw UnimplementedOperationException()
    }

    override fun setChestplateDropChance(chance: Float) {
        throw UnimplementedOperationException()
    }

    override fun getLeggingsDropChance(): Float {
        throw UnimplementedOperationException()
    }

    override fun setLeggingsDropChance(chance: Float) {
        throw UnimplementedOperationException()
    }

    override fun getBootsDropChance(): Float {
        throw UnimplementedOperationException()
    }

    override fun setBootsDropChance(chance: Float) {
        throw UnimplementedOperationException()
    }

    override fun getDropChance(slot: EquipmentSlot): Float {
        throw UnimplementedOperationException()
    }

    override fun setDropChance(slot: EquipmentSlot, chance: Float) {
        throw UnimplementedOperationException()
    }

    override fun getHolder(): Entity = holder
}
