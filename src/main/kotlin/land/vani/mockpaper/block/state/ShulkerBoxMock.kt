package land.vani.mockpaper.block.state

import land.vani.mockpaper.UnimplementedOperationException
import land.vani.mockpaper.inventory.InventoryMock
import land.vani.mockpaper.inventory.ShulkerBoxInventoryMock
import org.bukkit.DyeColor
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.ShulkerBox
import org.bukkit.loot.LootTable
import java.util.UUID

/**
 * This [ContainerMock] represents a [ShulkerBox].
 *
 * @author TheBusyBiscuit
 */
class ShulkerBoxMock(material: Material, block: Block? = null) : ContainerMock(material, block), ShulkerBox {
    private var color: DyeColor?

    init {
        color = getColorFromMaterial(material)
    }

    constructor(block: Block) : this(block.type, block)

    constructor(state: ShulkerBoxMock) : this(state.material, state.block) {
        color = state.color
    }

    private fun getColorFromMaterial(type: Material): DyeColor? =
        when (type) {
            Material.SHULKER_BOX -> null
            Material.WHITE_SHULKER_BOX -> DyeColor.WHITE
            Material.ORANGE_SHULKER_BOX -> DyeColor.ORANGE
            Material.MAGENTA_SHULKER_BOX -> DyeColor.MAGENTA
            Material.LIGHT_BLUE_SHULKER_BOX -> DyeColor.LIGHT_BLUE
            Material.YELLOW_SHULKER_BOX -> DyeColor.YELLOW
            Material.LIME_SHULKER_BOX -> DyeColor.LIME
            Material.PINK_SHULKER_BOX -> DyeColor.PINK
            Material.GRAY_SHULKER_BOX -> DyeColor.GRAY
            Material.LIGHT_GRAY_SHULKER_BOX -> DyeColor.LIGHT_GRAY
            Material.CYAN_SHULKER_BOX -> DyeColor.CYAN
            Material.PURPLE_SHULKER_BOX -> DyeColor.PURPLE
            Material.BLUE_SHULKER_BOX -> DyeColor.BLUE
            Material.BROWN_SHULKER_BOX -> DyeColor.BROWN
            Material.GREEN_SHULKER_BOX -> DyeColor.GREEN
            Material.RED_SHULKER_BOX -> DyeColor.RED
            Material.BLACK_SHULKER_BOX -> DyeColor.BLACK
            else -> error("${type.name} is not a shulker box!")
        }

    override fun createInventory(): InventoryMock = ShulkerBoxInventoryMock(this)

    override fun getSnapshot(): BlockStateMock = ShulkerBoxMock(this)

    override fun setLootTable(table: LootTable?) {
        throw UnimplementedOperationException()
    }

    override fun getLootTable(): LootTable? {
        throw UnimplementedOperationException()
    }

    override fun setSeed(seed: Long) {
        throw UnimplementedOperationException()
    }

    override fun getSeed(): Long {
        throw UnimplementedOperationException()
    }

    override fun isRefillEnabled(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun hasBeenFilled(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun hasPlayerLooted(player: UUID): Boolean {
        throw UnimplementedOperationException()
    }

    override fun getLastLooted(player: UUID): Long? {
        throw UnimplementedOperationException()
    }

    override fun setHasPlayerLooted(player: UUID, looted: Boolean): Boolean {
        throw UnimplementedOperationException()
    }

    override fun hasPendingRefill(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun getLastFilled(): Long {
        throw UnimplementedOperationException()
    }

    override fun getNextRefill(): Long {
        throw UnimplementedOperationException()
    }

    override fun setNextRefill(refillAt: Long): Long {
        throw UnimplementedOperationException()
    }

    override fun open() {
        throw UnimplementedOperationException()
    }

    override fun close() {
        throw UnimplementedOperationException()
    }

    override fun isOpen(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun getColor(): DyeColor? = color
}
