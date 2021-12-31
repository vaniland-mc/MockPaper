package land.vani.mockpaper.entity

import land.vani.mockpaper.ServerMock
import land.vani.mockpaper.UnimplementedOperationException
import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.inventory.EntityEquipment
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.util.EulerAngle
import java.util.UUID

class ArmorStandMock(server: ServerMock, uuid: UUID) : LivingEntityMock(server, uuid), ArmorStand {
    private val equipment = EntityEquipmentMock(this)

    private var hasArms: Boolean = false
    private var isSmall: Boolean = false
    private var isMarker: Boolean = false
    private var hasBasePlate: Boolean = false
    private var isVisible: Boolean = false

    override fun getType(): EntityType = EntityType.ARMOR_STAND

    override fun getEquipment(): EntityEquipment = equipment

    override fun getItemInHand(): ItemStack = equipment.itemInHand

    override fun setItemInHand(item: ItemStack?) {
        @Suppress("DEPRECATION")
        equipment.setItemInHand(item)
    }

    override fun getBoots(): ItemStack = equipment.boots ?: ItemStack(Material.AIR)

    override fun setBoots(item: ItemStack?) {
        equipment.boots = item
    }

    override fun getLeggings(): ItemStack = equipment.leggings ?: ItemStack(Material.AIR)

    override fun setLeggings(item: ItemStack?) {
        equipment.leggings = item
    }

    override fun getChestplate(): ItemStack = equipment.chestplate ?: ItemStack(Material.AIR)

    override fun setChestplate(item: ItemStack?) {
        equipment.chestplate = item
    }

    override fun getHelmet(): ItemStack = equipment.helmet ?: ItemStack(Material.AIR)

    override fun setHelmet(item: ItemStack?) {
        equipment.helmet = item
    }

    override fun getBodyPose(): EulerAngle {
        throw UnimplementedOperationException()
    }

    override fun setBodyPose(pose: EulerAngle) {
        throw UnimplementedOperationException()
    }

    override fun getLeftArmPose(): EulerAngle {
        throw UnimplementedOperationException()
    }

    override fun setLeftArmPose(pose: EulerAngle) {
        throw UnimplementedOperationException()
    }

    override fun getRightArmPose(): EulerAngle {
        throw UnimplementedOperationException()
    }

    override fun setRightArmPose(pose: EulerAngle) {
        throw UnimplementedOperationException()
    }

    override fun getLeftLegPose(): EulerAngle {
        throw UnimplementedOperationException()
    }

    override fun setLeftLegPose(pose: EulerAngle) {
        throw UnimplementedOperationException()
    }

    override fun getRightLegPose(): EulerAngle {
        throw UnimplementedOperationException()
    }

    override fun setRightLegPose(pose: EulerAngle) {
        throw UnimplementedOperationException()
    }

    override fun getHeadPose(): EulerAngle {
        throw UnimplementedOperationException()
    }

    override fun setHeadPose(pose: EulerAngle) {
        throw UnimplementedOperationException()
    }

    override fun hasBasePlate(): Boolean = hasBasePlate

    override fun setBasePlate(basePlate: Boolean) {
        hasBasePlate = basePlate
    }

    override fun isVisible(): Boolean = isVisible

    override fun setVisible(visible: Boolean) {
        isVisible = visible
    }

    override fun hasArms(): Boolean = hasArms

    override fun setArms(arms: Boolean) {
        hasArms = arms
    }

    override fun isSmall(): Boolean = isSmall

    override fun setSmall(small: Boolean) {
        isSmall = small
    }

    override fun isMarker(): Boolean = isMarker

    override fun setMarker(marker: Boolean) {
        isMarker = marker
    }

    override fun addEquipmentLock(slot: EquipmentSlot, lockType: ArmorStand.LockType) {
        throw UnimplementedOperationException()
    }

    override fun removeEquipmentLock(slot: EquipmentSlot, lockType: ArmorStand.LockType) {
        throw UnimplementedOperationException()
    }

    override fun hasEquipmentLock(slot: EquipmentSlot, lockType: ArmorStand.LockType): Boolean {
        throw UnimplementedOperationException()
    }

    override fun canMove(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun setCanMove(move: Boolean) {
        throw UnimplementedOperationException()
    }

    override fun canTick(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun setCanTick(tick: Boolean) {
        throw UnimplementedOperationException()
    }

    override fun getItem(slot: EquipmentSlot): ItemStack = equipment.getItem(slot)

    override fun setItem(slot: EquipmentSlot, item: ItemStack?) {
        equipment.setItem(slot, item)
    }

    override fun getDisabledSlots(): Set<EquipmentSlot> {
        throw UnimplementedOperationException()
    }

    override fun setDisabledSlots(vararg slots: EquipmentSlot) {
        throw UnimplementedOperationException()
    }

    override fun addDisabledSlots(vararg slots: EquipmentSlot) {
        throw UnimplementedOperationException()
    }

    override fun removeDisabledSlots(vararg slots: EquipmentSlot) {
        throw UnimplementedOperationException()
    }

    override fun isSlotDisabled(slot: EquipmentSlot): Boolean {
        throw UnimplementedOperationException()
    }

    override fun registerAttribute(attribute: Attribute) {
        throw UnimplementedOperationException()
    }

    override fun isSleeping(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun attack(target: Entity) {
        throw UnimplementedOperationException()
    }
}
