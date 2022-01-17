package land.vani.mockpaper.inventory.meta

import com.destroystokyo.paper.Namespaced
import com.google.common.collect.Multimap
import land.vani.mockpaper.UnimplementedOperationException
import land.vani.mockpaper.internal.asUnmodifiable
import land.vani.mockpaper.internal.toBungeeComponents
import land.vani.mockpaper.internal.toComponent
import land.vani.mockpaper.internal.toLegacyString
import land.vani.mockpaper.persistence.PersistentDataContainerMock
import net.kyori.adventure.text.Component
import net.md_5.bungee.api.chat.BaseComponent
import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.meta.Damageable
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.Repairable
import org.bukkit.persistence.PersistentDataContainer
import java.util.EnumSet
import java.util.Objects

open class ItemMetaMock : ItemMeta, Damageable, Repairable {
    private var displayName: Component? = null
    private var lore: MutableList<Component>? = null
    private var customModelData: Int = 0
    private var enchants: MutableMap<Enchantment, Int> = mutableMapOf()
    private var hideFlags: MutableSet<ItemFlag> = EnumSet.noneOf(ItemFlag::class.java)
    private var isUnbreakable: Boolean = false

    private var damage: Int = 0
    private var repairCost: Int = 0

    constructor()

    constructor(meta: ItemMeta) {
        isUnbreakable = meta.isUnbreakable
        enchants = meta.enchants
        customModelData = meta.customModelData
        hideFlags += meta.itemFlags
        displayName = meta.displayName()
        lore = meta.lore()
        if (meta is ItemMetaMock) {
            persistentDataContainer = meta.persistentDataContainer
        }
    }

    private var persistentDataContainer = PersistentDataContainerMock()

    override fun hasDisplayName(): Boolean = displayName != null

    override fun displayName(): Component? = displayName

    override fun displayName(displayName: Component?) {
        this.displayName = displayName
    }

    override fun getDisplayName(): String = displayName?.toLegacyString()
        ?: error("this item does not have display name")

    override fun getDisplayNameComponent(): Array<out BaseComponent> =
        displayName?.toBungeeComponents() ?: emptyArray()

    override fun setDisplayName(name: String?) {
        displayName = name?.toComponent()
    }

    override fun setDisplayNameComponent(component: Array<out BaseComponent?>?) {
        displayName = component?.filterNotNull()
            ?.toTypedArray()
            ?.toComponent()
    }

    override fun hasLocalizedName(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun getLocalizedName(): String {
        throw UnimplementedOperationException()
    }

    override fun setLocalizedName(name: String?) {
        throw UnimplementedOperationException()
    }

    override fun hasLore(): Boolean = lore != null

    override fun lore(): List<Component>? = lore

    override fun lore(lore: List<Component>?) {
        this.lore = lore?.toMutableList()
    }

    override fun getLore(): List<String>? = lore?.map { it.toLegacyString() }

    override fun getLoreComponents(): List<Array<out BaseComponent>>? =
        lore?.map { it.toBungeeComponents() }

    override fun setLore(lore: List<String>?) {
        this.lore = lore?.map { it.toComponent() }?.toMutableList()
    }

    override fun setLoreComponents(lore: List<Array<out BaseComponent>>?) {
        this.lore = lore?.map { it.toComponent() }?.toMutableList()
    }

    override fun hasCustomModelData(): Boolean = customModelData != 0

    override fun getCustomModelData(): Int = customModelData

    override fun setCustomModelData(data: Int?) {
        customModelData = data ?: 0
    }

    override fun hasEnchants(): Boolean = enchants.isNotEmpty()

    override fun hasEnchant(ench: Enchantment): Boolean = ench in enchants

    override fun getEnchantLevel(ench: Enchantment): Int = enchants[ench] ?: 0

    override fun getEnchants(): Map<Enchantment, Int> =
        enchants.asUnmodifiable()

    override fun addEnchant(ench: Enchantment, level: Int, ignoreLevelRestriction: Boolean): Boolean {
        val existsLevel = enchants[ench]
        if (existsLevel == level) {
            return false
        }
        if (ignoreLevelRestriction || (level in ench.startLevel..ench.maxLevel)) {
            enchants[ench] = level
            return true
        }
        return false
    }

    override fun removeEnchant(ench: Enchantment): Boolean =
        enchants.remove(ench) != null

    override fun hasConflictingEnchant(ench: Enchantment): Boolean =
        enchants.any { it.key.conflictsWith(ench) }

    override fun addItemFlags(vararg itemFlags: ItemFlag) {
        hideFlags += itemFlags
    }

    override fun removeItemFlags(vararg itemFlags: ItemFlag) {
        hideFlags -= itemFlags.toSet()
    }

    override fun getItemFlags(): Set<ItemFlag> = hideFlags.asUnmodifiable()

    override fun hasItemFlag(flag: ItemFlag): Boolean = flag in hideFlags

    override fun isUnbreakable(): Boolean = isUnbreakable

    override fun setUnbreakable(unbreakable: Boolean) {
        isUnbreakable = unbreakable
    }

    override fun hasAttributeModifiers(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun getAttributeModifiers(): Multimap<Attribute, AttributeModifier>? {
        throw UnimplementedOperationException()
    }

    override fun getAttributeModifiers(attribute: Attribute): MutableCollection<AttributeModifier>? {
        throw UnimplementedOperationException()
    }

    override fun getAttributeModifiers(slot: EquipmentSlot): Multimap<Attribute, AttributeModifier> {
        throw UnimplementedOperationException()
    }

    override fun addAttributeModifier(attribute: Attribute, modifier: AttributeModifier): Boolean {
        throw UnimplementedOperationException()
    }

    override fun setAttributeModifiers(attributeModifiers: Multimap<Attribute, AttributeModifier>?) {
        throw UnimplementedOperationException()
    }

    override fun removeAttributeModifier(attribute: Attribute): Boolean {
        throw UnimplementedOperationException()
    }

    override fun removeAttributeModifier(slot: EquipmentSlot): Boolean {
        throw UnimplementedOperationException()
    }

    override fun removeAttributeModifier(attribute: Attribute, modifier: AttributeModifier): Boolean {
        throw UnimplementedOperationException()
    }

    @Suppress("DEPRECATION")
    override fun getCustomTagContainer(): org.bukkit.inventory.meta.tags.CustomItemTagContainer {
        throw UnimplementedOperationException()
    }

    override fun setVersion(version: Int) {
        throw UnimplementedOperationException()
    }

    override fun clone(): ItemMetaMock = ItemMetaMock().let {
        it.displayName = displayName
        it.lore = lore
        it.isUnbreakable = isUnbreakable
        it.customModelData = customModelData
        it.enchants = enchants.toMutableMap()
        it.persistentDataContainer
        it.persistentDataContainer = PersistentDataContainerMock(persistentDataContainer)
        it.hideFlags = EnumSet.copyOf(hideFlags)
        it
    }

    override fun getCanDestroy(): Set<Material> {
        throw UnimplementedOperationException()
    }

    override fun setCanDestroy(canDestroy: Set<Material>?) {
        throw UnimplementedOperationException()
    }

    override fun getCanPlaceOn(): Set<Material> {
        throw UnimplementedOperationException()
    }

    override fun setCanPlaceOn(canPlaceOn: Set<Material>?) {
        throw UnimplementedOperationException()
    }

    override fun getDestroyableKeys(): Set<Namespaced> {
        throw UnimplementedOperationException()
    }

    override fun setDestroyableKeys(canDestroy: Collection<Namespaced>) {
        throw UnimplementedOperationException()
    }

    override fun getPlaceableKeys(): Set<Namespaced> {
        throw UnimplementedOperationException()
    }

    override fun setPlaceableKeys(canPlaceOn: Collection<Namespaced>) {
        throw UnimplementedOperationException()
    }

    override fun hasPlaceableKeys(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun hasDestroyableKeys(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun hasDamage(): Boolean = damage > 0

    override fun getDamage(): Int = damage

    override fun setDamage(damage: Int) {
        this.damage = damage
    }

    override fun hasRepairCost(): Boolean = repairCost > 0

    override fun getRepairCost(): Int = repairCost

    override fun setRepairCost(cost: Int) {
        this.repairCost = cost
    }

    override fun getPersistentDataContainer(): PersistentDataContainer =
        persistentDataContainer

    override fun serialize(): Map<String, Any> = buildMap {
        if (displayName != null) {
            put("displayName", displayName!!.toLegacyString())
        }
        if (lore != null) {
            put("lore", lore!!.map { it.toLegacyString() })
        }
        if (customModelData != 0) {
            put("customModelData", customModelData)
        }
        put("enchants", enchants)

        // Not implemented: attributeModifiers

        if (hasRepairCost()) {
            put("repairCost", repairCost)
        }

        put("itemFlags", hideFlags)
        put("unbreakable", isUnbreakable)

        if (hasDamage()) {
            put("damage", damage)
        }

        // Not implemented: customTagContainer

        put("persistentDataContainer", persistentDataContainer.serialize())
    }

    override fun hashCode(): Int = Objects.hash(
        displayName,
        lore,
        customModelData,
        enchants,
        hideFlags,
        isUnbreakable,
        damage,
        repairCost,
        persistentDataContainer,
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ItemMetaMock

        if (displayName != other.displayName) return false
        if (lore != other.lore) return false
        if (customModelData != other.customModelData) return false
        if (enchants != other.enchants) return false
        if (hideFlags != other.hideFlags) return false
        if (isUnbreakable != other.isUnbreakable) return false
        if (damage != other.damage) return false
        if (repairCost != other.repairCost) return false
        if (persistentDataContainer != other.persistentDataContainer) return false

        return true
    }

    companion object {
        @Suppress("UNCHECKED_CAST")
        @JvmStatic
        fun deserialize(args: Map<String, Any>): ItemMeta =
            ItemMetaMock().apply {
                displayName = (args["displayName"] as String?)?.toComponent()
                lore = (args["lore"] as List<String>?)?.map { it.toComponent() }
                    ?.toMutableList()
                enchants = args["enchants"] as MutableMap<Enchantment, Int>
                hideFlags = args["itemFlags"] as MutableSet<ItemFlag>
                isUnbreakable = args["unbreakable"] as Boolean
                damage = args["damage"] as Int? ?: 0
                repairCost = args["repairCost"] as Int? ?: 0

                // attributeModifier and customTagContainer are not supported

                customModelData = args["customModelData"] as? Int ?: 0
                persistentDataContainer = (args["persistentDataContainer"] as Map<String, Any>)
                    .let {
                        PersistentDataContainerMock.deserialize(it)
                    }
            }
    }
}
