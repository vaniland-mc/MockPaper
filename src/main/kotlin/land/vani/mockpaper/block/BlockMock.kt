package land.vani.mockpaper.block

import com.destroystokyo.paper.block.BlockSoundGroup
import land.vani.mockpaper.UnimplementedOperationException
import land.vani.mockpaper.block.data.BlockDataMock
import land.vani.mockpaper.block.state.BlockStateMock
import land.vani.mockpaper.metadata.MetadataHolder
import org.bukkit.Chunk
import org.bukkit.FluidCollisionMode
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.block.Biome
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.block.PistonMoveReaction
import org.bukkit.block.data.BlockData
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.metadata.Metadatable
import org.bukkit.util.BoundingBox
import org.bukkit.util.RayTraceResult
import org.bukkit.util.Vector
import org.bukkit.util.VoxelShape
import org.jetbrains.annotations.VisibleForTesting

/**
 * @constructor Creates a basic block with a given [material] that's also linked to a specific [location].
 */
class BlockMock(
    private var material: Material,
    private val location: Location = Location(null, 0.0, 0.0, 0.0),
) : Block, Metadatable by MetadataHolder() {
    private var data: Byte = 0

    private var blockData: BlockData = BlockDataMock(material)
    private var state: BlockStateMock = BlockStateMock.mock(this)

    /**
     * Creates a basic block made of air at a certain [location].
     */
    constructor(location: Location) : this(Material.AIR, location)

    /**
     * Creates a basic block made of air.
     */
    constructor() : this(Material.AIR, Location(null, 0.0, 0.0, 0.0))

    override fun getData(): Byte = data

    override fun getRelative(modX: Int, modY: Int, modZ: Int): Block {
        val x = location.blockX + modX
        val y = location.blockY + modY
        val z = location.blockZ + modZ
        return location.world.getBlockAt(x, y, z)
    }

    override fun getRelative(face: BlockFace): Block = getRelative(face, 1)

    override fun getRelative(face: BlockFace, distance: Int): Block =
        getRelative(face.modX * distance, face.modY * distance, face.modZ * distance)

    override fun getType(): Material = material

    override fun setType(type: Material) {
        material = type
    }

    override fun setType(type: Material, applyPhysics: Boolean) {
        setType(type)
    }

    override fun getLightLevel(): Byte {
        throw UnimplementedOperationException()
    }

    override fun getLightFromSky(): Byte {
        throw UnimplementedOperationException()
    }

    override fun getLightFromBlocks(): Byte {
        throw UnimplementedOperationException()
    }

    override fun getWorld(): World = location.world

    override fun getX(): Int = location.blockX

    override fun getY(): Int = location.blockY

    override fun getZ(): Int = location.blockZ

    override fun isValidTool(itemStack: ItemStack): Boolean {
        throw UnimplementedOperationException()
    }

    override fun getLocation(): Location = location

    override fun getLocation(loc: Location?): Location? = loc?.apply {
        x = location.x
        y = location.y
        z = location.z
    }

    override fun getChunk(): Chunk = location.world.getChunkAt(this)

    override fun getFace(block: Block): BlockFace? {
        throw UnimplementedOperationException()
    }

    override fun getState(): BlockStateMock = state

    override fun getState(useSnapshot: Boolean): BlockStateMock {
        throw UnimplementedOperationException()
    }

    @VisibleForTesting
    fun setState(state: BlockStateMock) {
        this.state = state
    }

    override fun getBiome(): Biome {
        throw UnimplementedOperationException()
    }

    override fun setBiome(bio: Biome) {
        throw UnimplementedOperationException()
    }

    override fun isBlockPowered(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun isBlockIndirectlyPowered(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun isBlockFacePowered(face: BlockFace): Boolean {
        throw UnimplementedOperationException()
    }

    override fun isBlockFaceIndirectlyPowered(face: BlockFace): Boolean {
        throw UnimplementedOperationException()
    }

    override fun getBlockPower(): Int {
        throw UnimplementedOperationException()
    }

    override fun getBlockPower(face: BlockFace): Int {
        throw UnimplementedOperationException()
    }

    override fun isEmpty(): Boolean = type.isEmpty

    override fun isLiquid(): Boolean = when (type) {
        Material.WATER,
        Material.LAVA,
        Material.BUBBLE_COLUMN,
        -> true
        else -> false
    }

    override fun isBuildable(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun isBurnable(): Boolean = type.isBurnable

    override fun isReplaceable(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun isSolid(): Boolean = type.isSolid

    override fun isCollidable(): Boolean = type.isCollidable

    override fun getTemperature(): Double {
        throw UnimplementedOperationException()
    }

    override fun getHumidity(): Double {
        throw UnimplementedOperationException()
    }

    override fun getPistonMoveReaction(): PistonMoveReaction {
        throw UnimplementedOperationException()
    }

    override fun breakNaturally(): Boolean {
        if (isEmpty) return false
        type = Material.AIR
        return true
    }

    override fun breakNaturally(tool: ItemStack?): Boolean = breakNaturally()

    override fun breakNaturally(tool: ItemStack, triggerEffect: Boolean): Boolean =
        breakNaturally()

    override fun breakNaturally(triggerEffect: Boolean): Boolean = breakNaturally()

    override fun applyBoneMeal(face: BlockFace): Boolean {
        throw UnimplementedOperationException()
    }

    override fun getDrops(): Collection<ItemStack> {
        throw UnimplementedOperationException()
    }

    override fun getDrops(tool: ItemStack?): Collection<ItemStack> {
        throw UnimplementedOperationException()
    }

    override fun getDrops(tool: ItemStack, entity: Entity?): Collection<ItemStack> {
        throw UnimplementedOperationException()
    }

    override fun isPreferredTool(tool: ItemStack): Boolean {
        throw UnimplementedOperationException()
    }

    override fun getBlockData(): BlockData = blockData

    override fun setBlockData(data: BlockData) {
        this.material = data.material
        this.blockData = data
    }

    override fun setBlockData(data: BlockData, applyPhysics: Boolean) {
        throw UnimplementedOperationException()
    }

    override fun isPassable(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun rayTrace(
        start: Location,
        direction: Vector,
        maxDistance: Double,
        fluidCollisionMode: FluidCollisionMode,
    ): RayTraceResult? {
        throw UnimplementedOperationException()
    }

    override fun getBoundingBox(): BoundingBox {
        throw UnimplementedOperationException()
    }

    override fun getCollisionShape(): VoxelShape {
        throw UnimplementedOperationException()
    }

    override fun canPlace(data: BlockData): Boolean {
        throw UnimplementedOperationException()
    }

    override fun getSoundGroup(): BlockSoundGroup {
        throw UnimplementedOperationException()
    }

    override fun getTranslationKey(): String = translationKey()

    override fun getDestroySpeed(itemStack: ItemStack, considerEnchants: Boolean): Float {
        throw UnimplementedOperationException()
    }

    override fun getBreakSpeed(player: Player): Float {
        throw UnimplementedOperationException()
    }

    override fun translationKey(): String {
        throw UnimplementedOperationException()
    }
}
