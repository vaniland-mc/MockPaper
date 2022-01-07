package land.vani.mockpaper.world

import land.vani.mockpaper.UnimplementedOperationException
import land.vani.mockpaper.persistence.PersistentDataContainerMock
import org.bukkit.Chunk
import org.bukkit.ChunkSnapshot
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.block.BlockState
import org.bukkit.block.data.BlockData
import org.bukkit.entity.Entity
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.plugin.Plugin
import java.util.function.Predicate

class ChunkMock(
    private val world: World,
    private val x: Int,
    private val z: Int,
) : Chunk {
    private var isLoaded: Boolean = true

    private val persistentDataContainer: PersistentDataContainer = PersistentDataContainerMock()

    override fun getX(): Int = x

    override fun getZ(): Int = z

    override fun getWorld(): World = world

    override fun getBlock(x: Int, y: Int, z: Int): Block {
        require(x in 0..15) { "x is out of range (expected 0 to 15)" }
        require(y in -64..319) { "y is out of range (expected -64 to 319)" }
        require(z in 0..15) { "z is out of range (expected 0 to 15)" }
        return world.getBlockAt((this.x shl 4) + x, y, (this.z shl 4) + z)
    }

    override fun getChunkSnapshot(): ChunkSnapshot {
        throw UnimplementedOperationException()
    }

    override fun getChunkSnapshot(
        includeMaxblocky: Boolean,
        includeBiome: Boolean,
        includeBiomeTempRain: Boolean,
    ): ChunkSnapshot {
        throw UnimplementedOperationException()
    }

    override fun isEntitiesLoaded(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun getEntities(): Array<Entity> {
        throw UnimplementedOperationException()
    }

    override fun getTileEntities(): Array<BlockState> {
        throw UnimplementedOperationException()
    }

    override fun getTileEntities(useSnapshot: Boolean): Array<BlockState> {
        throw UnimplementedOperationException()
    }

    override fun getTileEntities(
        blockPredicate: Predicate<Block>,
        useSnapshot: Boolean,
    ): Collection<BlockState> {
        throw UnimplementedOperationException()
    }

    override fun isLoaded(): Boolean = isLoaded

    override fun load(generate: Boolean): Boolean = load()

    override fun load(): Boolean {
        isLoaded = true
        return true
    }

    override fun unload(save: Boolean): Boolean = unload()

    override fun unload(): Boolean {
        isLoaded = false
        return true
    }

    override fun isSlimeChunk(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun isForceLoaded(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun setForceLoaded(forced: Boolean) {
        throw UnimplementedOperationException()
    }

    override fun addPluginChunkTicket(plugin: Plugin): Boolean {
        throw UnimplementedOperationException()
    }

    override fun removePluginChunkTicket(plugin: Plugin): Boolean {
        throw UnimplementedOperationException()
    }

    override fun getPluginChunkTickets(): Collection<Plugin> {
        throw UnimplementedOperationException()
    }

    override fun getInhabitedTime(): Long {
        throw UnimplementedOperationException()
    }

    override fun setInhabitedTime(ticks: Long) {
        throw UnimplementedOperationException()
    }

    override fun contains(block: BlockData): Boolean {
        throw UnimplementedOperationException()
    }

    override fun getPersistentDataContainer(): PersistentDataContainer =
        persistentDataContainer
}
