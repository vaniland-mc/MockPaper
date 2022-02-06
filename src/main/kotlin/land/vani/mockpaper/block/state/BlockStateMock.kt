package land.vani.mockpaper.block.state

import land.vani.mockpaper.UnimplementedOperationException
import land.vani.mockpaper.block.BlockMock
import land.vani.mockpaper.metadata.MetadataHolder
import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.block.BlockState
import org.bukkit.block.data.BlockData
import org.bukkit.metadata.Metadatable

open class BlockStateMock(
    private var material: Material,
    private var block: Block? = null,
) : BlockState, Metadatable by MetadataHolder() {
    constructor(block: Block) : this(block.type, block)

    constructor(state: BlockStateMock) : this(state.material, state.block)

    override fun getBlock(): Block = block
        ?: error("block is not placed")

    @Suppress("DEPRECATION")
    override fun getData(): org.bukkit.material.MaterialData =
        org.bukkit.material.MaterialData(material)

    override fun getBlockData(): BlockData {
        throw UnimplementedOperationException()
    }

    override fun getType(): Material = material

    override fun getLightLevel(): Byte = block?.lightLevel ?: 0

    override fun getWorld(): World = block?.world ?: error("block is not placed")

    override fun getX(): Int = block?.x ?: error("block is not placed")

    override fun getY(): Int = block?.y ?: error("block is not placed")

    override fun getZ(): Int = block?.z ?: error("block is not placed")

    override fun getLocation(): Location = block?.location
        ?: Location(null, 0.0, 0.0, 0.0)

    override fun getLocation(loc: Location?): Location? =
        loc?.apply {
            x = this@BlockStateMock.location.x
            y = this@BlockStateMock.location.y
            z = this@BlockStateMock.location.z
        }

    override fun getChunk(): Chunk = block?.chunk ?: error("block is not placed")

    @Suppress("DEPRECATION")
    override fun setData(data: org.bukkit.material.MaterialData) {
        material = data.itemType
    }

    override fun setBlockData(data: BlockData) {
        throw UnimplementedOperationException()
    }

    override fun setType(type: Material) {
        material = type
    }

    override fun update(): Boolean = update(false)

    override fun update(force: Boolean): Boolean = update(force, true)

    override fun update(force: Boolean, applyPhysics: Boolean): Boolean {
        if (!isPlaced) {
            return true
        }
        val b = block

        return if (b is BlockMock && (force || b.type == material)) {
            b.state = this
            true
        } else {
            false
        }
    }

    override fun getRawData(): Byte {
        throw UnimplementedOperationException()
    }

    override fun setRawData(data: Byte) {
        throw UnimplementedOperationException()
    }

    override fun isPlaced(): Boolean = block != null

    override fun isCollidable(): Boolean = material.isCollidable

    companion object {
        fun mock(block: Block): BlockStateMock =
            when (block.type) {
                Material.LECTERN -> LecternMock(block)
                Material.HOPPER -> HopperMock(block)
                Material.BARREL -> BarrelMock(block)
                Material.DISPENSER -> DispenserMock(block)
                Material.DROPPER -> DropperMock(block)
                Material.CHEST,
                Material.TRAPPED_CHEST,
                -> ChestMock(block)
                Material.ENDER_CHEST -> EnderChestMock(block)
                Material.ACACIA_SIGN,
                Material.ACACIA_WALL_SIGN,
                Material.BIRCH_SIGN,
                Material.BIRCH_WALL_SIGN,
                Material.CRIMSON_SIGN,
                Material.CRIMSON_WALL_SIGN,
                Material.DARK_OAK_SIGN,
                Material.DARK_OAK_WALL_SIGN,
                Material.JUNGLE_SIGN,
                Material.JUNGLE_WALL_SIGN,
                Material.OAK_SIGN,
                Material.OAK_WALL_SIGN,
                Material.SPRUCE_SIGN,
                Material.SPRUCE_WALL_SIGN,
                Material.WARPED_SIGN,
                Material.WARPED_WALL_SIGN,
                -> SignMock(block)
                Material.SHULKER_BOX,
                Material.WHITE_SHULKER_BOX,
                Material.ORANGE_SHULKER_BOX,
                Material.MAGENTA_SHULKER_BOX,
                Material.LIGHT_BLUE_SHULKER_BOX,
                Material.YELLOW_SHULKER_BOX,
                Material.LIME_SHULKER_BOX,
                Material.PINK_SHULKER_BOX,
                Material.GRAY_SHULKER_BOX,
                Material.LIGHT_GRAY_SHULKER_BOX,
                Material.CYAN_SHULKER_BOX,
                Material.PURPLE_SHULKER_BOX,
                Material.BLUE_SHULKER_BOX,
                Material.BROWN_SHULKER_BOX,
                Material.GREEN_SHULKER_BOX,
                Material.RED_SHULKER_BOX,
                Material.BLACK_SHULKER_BOX,
                -> ShulkerBoxMock(block)
                else -> BlockStateMock(block)
            }
    }
}
