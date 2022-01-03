package land.vani.mockpaper.enchantments

import io.papermc.paper.enchantments.EnchantmentRarity
import land.vani.mockpaper.UnimplementedOperationException
import net.kyori.adventure.text.Component
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.enchantments.EnchantmentTarget
import org.bukkit.entity.EntityCategory
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.jetbrains.annotations.VisibleForTesting

class EnchantmentMock(key: NamespacedKey, private val name: String) : Enchantment(key) {
    private var maxLevel: Int = 1
    private var startLevel: Int = 1

    @Suppress("DEPRECATION")
    private var itemTarget: EnchantmentTarget = EnchantmentTarget.ALL
    private var isTreasure: Boolean = false
    private var isCursed: Boolean = false

    override fun getName(): String = name

    override fun getMaxLevel(): Int = maxLevel

    /**
     * Sets the maximum level that this Enchantment may become.
     */
    @VisibleForTesting
    fun setMaxLevel(maxLevel: Int) {
        this.maxLevel = maxLevel
    }

    override fun getStartLevel(): Int = startLevel

    /**
     * Sets the level that this Enchantment should start at
     */
    @VisibleForTesting
    fun setStartLevel(startLevel: Int) {
        this.startLevel = startLevel
    }

    override fun getItemTarget(): EnchantmentTarget = itemTarget

    /**
     * Sets the type of [org.bukkit.inventory.ItemStack] that may fit this Enchantment.
     */
    fun setItemTarget(itemTarget: EnchantmentTarget) {
        this.itemTarget = itemTarget
    }

    override fun isTreasure(): Boolean = isTreasure

    /**
     * Sets this enchantment is a treasure enchantment.
     *
     * Treasure enchantments can only be received via looting, trading, or fishing.
     */
    @VisibleForTesting
    fun setTreasure(isTreasure: Boolean) {
        this.isTreasure = isTreasure
    }

    override fun isCursed(): Boolean = isCursed

    /**
     * Sets this enchantment is a cursed enchantment.
     *
     * Cursed enchantments are found the same way treasure enchantments are.
     */
    @VisibleForTesting
    fun setCursed(isCursed: Boolean) {
        this.isCursed = isCursed
    }

    override fun conflictsWith(other: Enchantment): Boolean = false

    override fun canEnchantItem(item: ItemStack): Boolean = false

    override fun displayName(level: Int): Component {
        throw UnimplementedOperationException()
    }

    override fun isTradeable(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun isDiscoverable(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun getRarity(): EnchantmentRarity {
        throw UnimplementedOperationException()
    }

    override fun getDamageIncrease(level: Int, entityCategory: EntityCategory): Float {
        throw UnimplementedOperationException()
    }

    override fun getActiveSlots(): MutableSet<EquipmentSlot> {
        throw UnimplementedOperationException()
    }

    override fun translationKey(): String {
        throw UnimplementedOperationException()
    }
}
