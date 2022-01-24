package land.vani.mockpaper.world

import io.papermc.paper.world.MoonPhase
import land.vani.mockpaper.ServerMock
import land.vani.mockpaper.UnimplementedOperationException
import land.vani.mockpaper.block.BlockMock
import land.vani.mockpaper.entity.ArmorStandMock
import land.vani.mockpaper.entity.EntityMock
import land.vani.mockpaper.entity.ExperienceOrbMock
import land.vani.mockpaper.entity.FireworkMock
import land.vani.mockpaper.entity.ItemEntityMock
import land.vani.mockpaper.entity.ZombieMock
import land.vani.mockpaper.metadata.MetadataHolder
import land.vani.mockpaper.player.PlayerMock
import org.bukkit.BlockChangeDelegate
import org.bukkit.Chunk
import org.bukkit.ChunkSnapshot
import org.bukkit.Difficulty
import org.bukkit.Effect
import org.bukkit.FluidCollisionMode
import org.bukkit.GameEvent
import org.bukkit.GameRule
import org.bukkit.HeightMap
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Particle
import org.bukkit.Raid
import org.bukkit.Sound
import org.bukkit.SoundCategory
import org.bukkit.StructureType
import org.bukkit.TreeType
import org.bukkit.World
import org.bukkit.WorldBorder
import org.bukkit.WorldCreator
import org.bukkit.WorldType
import org.bukkit.block.Biome
import org.bukkit.block.Block
import org.bukkit.block.BlockState
import org.bukkit.block.TileState
import org.bukkit.block.data.BlockData
import org.bukkit.boss.DragonBattle
import org.bukkit.entity.AbstractArrow
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Arrow
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.ExperienceOrb
import org.bukkit.entity.FallingBlock
import org.bukkit.entity.Firework
import org.bukkit.entity.Item
import org.bukkit.entity.LightningStrike
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.Zombie
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.event.world.TimeSkipEvent
import org.bukkit.generator.BiomeProvider
import org.bukkit.generator.BlockPopulator
import org.bukkit.generator.ChunkGenerator
import org.bukkit.inventory.ItemStack
import org.bukkit.metadata.Metadatable
import org.bukkit.plugin.Plugin
import org.bukkit.util.BoundingBox
import org.bukkit.util.Consumer
import org.bukkit.util.RayTraceResult
import org.bukkit.util.Vector
import org.jetbrains.annotations.VisibleForTesting
import java.io.File
import java.util.Random
import java.util.UUID
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ThreadLocalRandom
import java.util.function.Predicate

class WorldMock(
    private val server: ServerMock,
    private val defaultBlock: Material,
    private val height: Int,
    private val grassHeight: Int,
) : World, Metadatable by MetadataHolder() {
    companion object {
        private const val MIN_WORLD_HEIGHT = -64
        private const val MAX_WORLD_HEIGHT = 319
    }

    private var name: String = "World"
    private val uuid: UUID = UUID.randomUUID()
    private var environment: World.Environment = World.Environment.NORMAL
    private var worldType: WorldType = WorldType.NORMAL
    private var seed: Long = 0

    private val gameRules: MutableMap<GameRule<*>, Any?> = mutableMapOf(
        GameRule.ANNOUNCE_ADVANCEMENTS to true,
        GameRule.COMMAND_BLOCK_OUTPUT to true,
        GameRule.DISABLE_ELYTRA_MOVEMENT_CHECK to false,
        GameRule.DO_DAYLIGHT_CYCLE to true,
        GameRule.DO_ENTITY_DROPS to true,
        GameRule.DO_FIRE_TICK to true,
        GameRule.DO_LIMITED_CRAFTING to true,
        GameRule.DO_MOB_LOOT to true,
        GameRule.DO_MOB_SPAWNING to true,
        GameRule.DO_TILE_DROPS to true,
        GameRule.DO_WEATHER_CYCLE to true,
        GameRule.KEEP_INVENTORY to false,
        GameRule.LOG_ADMIN_COMMANDS to true,
        GameRule.MAX_COMMAND_CHAIN_LENGTH to 65536,
        GameRule.MAX_ENTITY_CRAMMING to 24,
        GameRule.MOB_GRIEFING to true,
        GameRule.NATURAL_REGENERATION to true,
        GameRule.RANDOM_TICK_SPEED to 3,
        GameRule.REDUCED_DEBUG_INFO to false,
        GameRule.SEND_COMMAND_FEEDBACK to true,
        GameRule.SHOW_DEATH_MESSAGES to true,
        GameRule.SPAWN_RADIUS to 10,
        GameRule.SPECTATORS_GENERATE_CHUNKS to true,
    )

    private val blocks: MutableMap<Coordinate, BlockMock> = mutableMapOf()
    private val loadedChunks: MutableMap<ChunkCoordinate, ChunkMock> = mutableMapOf()

    private var spawnLocation: Location = Location(
        this,
        0.0,
        grassHeight + 1.0,
        0.0
    )
    private var fullTime: Long = 0
    private var hasStorm: Boolean = false
    private var thunderDuration: Int = 0

    constructor(server: ServerMock, creator: WorldCreator) : this(server) {
        name = creator.name()
        environment = creator.environment()
        worldType = creator.type()
        seed = creator.seed()
    }

    constructor(
        server: ServerMock,
        defaultBlock: Material,
        grassHeight: Int,
    ) : this(
        server,
        defaultBlock,
        319,
        grassHeight
    )

    constructor(server: ServerMock) : this(server, Material.GRASS, 4)

    override fun getName(): String = name

    /**
     * Sets the unique name of this world.
     */
    @VisibleForTesting
    fun setName(name: String) {
        this.name = name
    }

    override fun getUID(): UUID = uuid

    override fun getEnvironment(): World.Environment = environment

    override fun getSeed(): Long = seed

    override fun getMinHeight(): Int = MIN_WORLD_HEIGHT

    override fun getMaxHeight(): Int = MAX_WORLD_HEIGHT

    override fun getEntityCount(): Int = server.entities.count { it.world == this }

    override fun getTileEntityCount(): Int = blocks.count {
        it.value.state is TileState
    }

    override fun getTickableTileEntityCount(): Int {
        throw UnimplementedOperationException()
    }

    override fun getChunkCount(): Int {
        throw UnimplementedOperationException()
    }

    override fun getPlayerCount(): Int = server.onlinePlayers
        .count { it.world == this }

    override fun getMoonPhase(): MoonPhase {
        throw UnimplementedOperationException()
    }

    override fun lineOfSightExists(from: Location, to: Location): Boolean {
        throw UnimplementedOperationException()
    }

    private fun createBlock(coordinate: Coordinate): BlockMock {
        if (coordinate.y !in MIN_WORLD_HEIGHT..height) {
            throw IndexOutOfBoundsException("y is out of bounds ($MIN_WORLD_HEIGHT to $height)")
        }
        val location = Location(
            this,
            coordinate.x.toDouble(),
            coordinate.y.toDouble(),
            coordinate.z.toDouble()
        )
        return when {
            coordinate.y == MIN_WORLD_HEIGHT -> {
                BlockMock(Material.BEDROCK, location)
            }
            coordinate.y <= grassHeight -> {
                BlockMock(defaultBlock, location)
            }
            else -> {
                BlockMock(location)
            }
        }.also { blocks[coordinate] = it }
    }

    override fun getBlockAt(x: Int, y: Int, z: Int): Block {
        val coordinate = Coordinate(x, y, z)
        return blocks[coordinate] ?: createBlock(coordinate)
    }

    override fun getBlockAt(location: Location): Block =
        getBlockAt(location.blockX, location.blockY, location.blockZ)

    override fun getHighestBlockYAt(x: Int, z: Int): Int {
        throw UnimplementedOperationException()
    }

    override fun getHighestBlockYAt(location: Location): Int {
        throw UnimplementedOperationException()
    }

    override fun getHighestBlockAt(x: Int, z: Int): Block {
        throw UnimplementedOperationException()
    }

    override fun getHighestBlockAt(location: Location): Block {
        throw UnimplementedOperationException()
    }

    override fun getHighestBlockYAt(
        x: Int,
        z: Int,
        @Suppress("DEPRECATION")
        heightmap: com.destroystokyo.paper.HeightmapType,
    ): Int {
        throw UnimplementedOperationException()
    }

    override fun getHighestBlockYAt(
        location: Location,
        @Suppress("DEPRECATION")
        heightmap: com.destroystokyo.paper.HeightmapType,
    ): Int {
        throw UnimplementedOperationException()
    }

    override fun getHighestBlockAt(location: Location, heightMap: HeightMap): Block {
        throw UnimplementedOperationException()
    }

    override fun getHighestBlockAt(x: Int, z: Int, heightMap: HeightMap): Block {
        throw UnimplementedOperationException()
    }

    override fun getHighestBlockYAt(location: Location, heightMap: HeightMap): Int {
        throw UnimplementedOperationException()
    }

    override fun getHighestBlockYAt(x: Int, z: Int, heightMap: HeightMap): Int {
        throw UnimplementedOperationException()
    }

    override fun getHighestBlockAt(
        location: Location,
        @Suppress("DEPRECATION")
        heightmap: com.destroystokyo.paper.HeightmapType,
    ): Block {
        throw UnimplementedOperationException()
    }

    override fun getHighestBlockAt(
        x: Int,
        z: Int,
        @Suppress("DEPRECATION")
        heightmap: com.destroystokyo.paper.HeightmapType,
    ): Block {
        throw UnimplementedOperationException()
    }

    override fun getChunkAt(x: Int, z: Int): Chunk = getChunkAt(ChunkCoordinate(x, z))

    override fun getChunkAt(location: Location): Chunk = getChunkAt(
        location.blockX shr 4,
        location.blockZ shr 4
    )

    override fun getChunkAt(block: Block): Chunk = getChunkAt(block.location)

    private fun getChunkAt(coordinate: ChunkCoordinate): ChunkMock =
        (loadedChunks[coordinate] ?: ChunkMock(this, coordinate.x, coordinate.z))
            .also { loadedChunks[coordinate] = it }

    override fun isChunkLoaded(chunk: Chunk): Boolean = isChunkLoaded(chunk.x, chunk.z)

    override fun getLoadedChunks(): Array<Chunk> = loadedChunks.values.toTypedArray()

    override fun loadChunk(chunk: Chunk) {
        throw UnimplementedOperationException()
    }

    override fun isChunkLoaded(x: Int, z: Int): Boolean {
        val coordinate = ChunkCoordinate(x, z)
        return coordinate in loadedChunks
    }

    override fun isChunkGenerated(x: Int, z: Int): Boolean {
        throw UnimplementedOperationException()
    }

    override fun isChunkInUse(x: Int, z: Int): Boolean {
        throw UnimplementedOperationException()
    }

    override fun loadChunk(x: Int, z: Int) {
        throw UnimplementedOperationException()
    }

    override fun loadChunk(x: Int, z: Int, generate: Boolean): Boolean {
        throw UnimplementedOperationException()
    }

    override fun unloadChunk(chunk: Chunk): Boolean {
        throw UnimplementedOperationException()
    }

    override fun unloadChunk(x: Int, z: Int): Boolean {
        throw UnimplementedOperationException()
    }

    override fun unloadChunk(x: Int, z: Int, save: Boolean): Boolean {
        throw UnimplementedOperationException()
    }

    override fun unloadChunkRequest(x: Int, z: Int): Boolean {
        throw UnimplementedOperationException()
    }

    override fun regenerateChunk(x: Int, z: Int): Boolean {
        throw UnimplementedOperationException()
    }

    override fun refreshChunk(x: Int, z: Int): Boolean {
        throw UnimplementedOperationException()
    }

    override fun isChunkForceLoaded(x: Int, z: Int): Boolean {
        throw UnimplementedOperationException()
    }

    override fun setChunkForceLoaded(x: Int, z: Int, forced: Boolean) {
        throw UnimplementedOperationException()
    }

    override fun getForceLoadedChunks(): Collection<Chunk> {
        throw UnimplementedOperationException()
    }

    override fun addPluginChunkTicket(x: Int, z: Int, plugin: Plugin): Boolean {
        throw UnimplementedOperationException()
    }

    override fun removeMetadata(metadataKey: String, owningPlugin: Plugin) {
        throw UnimplementedOperationException()
    }

    override fun removePluginChunkTicket(x: Int, z: Int, plugin: Plugin): Boolean {
        throw UnimplementedOperationException()
    }

    override fun removePluginChunkTickets(plugin: Plugin) {
        throw UnimplementedOperationException()
    }

    override fun getPluginChunkTickets(x: Int, z: Int): MutableCollection<Plugin> {
        throw UnimplementedOperationException()
    }

    override fun getPluginChunkTickets(): Map<Plugin, MutableCollection<Chunk>> {
        throw UnimplementedOperationException()
    }

    override fun dropItem(location: Location, item: ItemStack): Item =
        dropItem(location, item) { }

    override fun dropItem(location: Location, item: ItemStack, function: Consumer<Item>?): Item {
        require(!item.type.isAir) { "Cannot drop air" }

        val entity = ItemEntityMock(server, UUID.randomUUID(), item)
        entity.location = location
        function?.accept(entity)

        server.registerEntity(entity)
        return entity
    }

    override fun dropItemNaturally(location: Location, item: ItemStack): Item =
        dropItemNaturally(location, item) { }

    override fun dropItemNaturally(location: Location, item: ItemStack, function: Consumer<Item>?): Item {
        val random = ThreadLocalRandom.current()

        val xs: Double = random.nextFloat() * 0.5f + 0.25
        val ys: Double = random.nextFloat() * 0.5f + 0.25
        val zs: Double = random.nextFloat() * 0.5f + 0.25

        val loc = location.clone().apply {
            x += xs
            y += ys
            z += zs
        }
        return dropItem(loc, item, function)
    }

    override fun spawnArrow(location: Location, direction: Vector, speed: Float, spread: Float): Arrow {
        throw UnimplementedOperationException()
    }

    override fun <T : AbstractArrow?> spawnArrow(
        location: Location,
        direction: Vector,
        speed: Float,
        spread: Float,
        clazz: Class<T>,
    ): T {
        throw UnimplementedOperationException()
    }

    override fun generateTree(location: Location, type: TreeType): Boolean {
        throw UnimplementedOperationException()
    }

    override fun generateTree(loc: Location, type: TreeType, delegate: BlockChangeDelegate): Boolean {
        throw UnimplementedOperationException()
    }

    override fun generateTree(location: Location, random: Random, type: TreeType): Boolean {
        throw UnimplementedOperationException()
    }

    override fun generateTree(
        location: Location,
        random: Random,
        type: TreeType,
        stateConsumer: Consumer<BlockState>?,
    ): Boolean {
        throw UnimplementedOperationException()
    }

    override fun strikeLightning(loc: Location): LightningStrike {
        throw UnimplementedOperationException()
    }

    override fun strikeLightningEffect(loc: Location): LightningStrike {
        throw UnimplementedOperationException()
    }

    override fun findLightningRod(location: Location): Location? {
        throw UnimplementedOperationException()
    }

    override fun findLightningTarget(location: Location): Location? {
        throw UnimplementedOperationException()
    }

    override fun getEntities(): List<Entity> = server.entities.filter { it.world == this }

    override fun getLivingEntities(): List<LivingEntity> = server.entities
        .filter { it.world == this }
        .filterIsInstance<LivingEntity>()

    override fun <T : Entity?> getEntitiesByClass(vararg classes: Class<T>): Collection<T> {
        throw UnimplementedOperationException()
    }

    override fun getEntitiesByClasses(vararg classes: Class<*>): Collection<Entity> {
        throw UnimplementedOperationException()
    }

    override fun <T : Entity?> getEntitiesByClass(cls: Class<T>): Collection<T> {
        throw UnimplementedOperationException()
    }

    override fun getChunkAtAsync(x: Int, z: Int, gen: Boolean, urgent: Boolean): CompletableFuture<Chunk> =
        CompletableFuture.completedFuture(getChunkAt(x, z))

    override fun getKey(): NamespacedKey = NamespacedKey.minecraft(name)

    override fun getPlayers(): List<Player> = server.onlinePlayers
        .filter { it.world == this }

    override fun getNearbyEntities(location: Location, x: Double, y: Double, z: Double): MutableCollection<Entity> {
        throw UnimplementedOperationException()
    }

    override fun getEntity(uuid: UUID): Entity? = entities.find { it.uniqueId == uuid }

    override fun getNearbyEntities(
        location: Location,
        x: Double,
        y: Double,
        z: Double,
        filter: Predicate<Entity>?,
    ): Collection<Entity> {
        throw UnimplementedOperationException()
    }

    override fun getNearbyEntities(boundingBox: BoundingBox): Collection<Entity> {
        throw UnimplementedOperationException()
    }

    override fun getNearbyEntities(boundingBox: BoundingBox, filter: Predicate<Entity>?): Collection<Entity> {
        throw UnimplementedOperationException()
    }

    override fun rayTraceEntities(start: Location, direction: Vector, maxDistance: Double): RayTraceResult? {
        throw UnimplementedOperationException()
    }

    override fun rayTraceEntities(
        start: Location,
        direction: Vector,
        maxDistance: Double,
        raySize: Double,
    ): RayTraceResult? {
        throw UnimplementedOperationException()
    }

    override fun rayTraceEntities(
        start: Location,
        direction: Vector,
        maxDistance: Double,
        filter: Predicate<Entity>?,
    ): RayTraceResult? {
        throw UnimplementedOperationException()
    }

    override fun rayTraceEntities(
        start: Location,
        direction: Vector,
        maxDistance: Double,
        raySize: Double,
        filter: Predicate<Entity>?,
    ): RayTraceResult? {
        throw UnimplementedOperationException()
    }

    override fun rayTraceBlocks(start: Location, direction: Vector, maxDistance: Double): RayTraceResult? {
        throw UnimplementedOperationException()
    }

    override fun rayTraceBlocks(
        start: Location,
        direction: Vector,
        maxDistance: Double,
        fluidCollisionMode: FluidCollisionMode,
    ): RayTraceResult? {
        throw UnimplementedOperationException()
    }

    override fun rayTraceBlocks(
        start: Location,
        direction: Vector,
        maxDistance: Double,
        fluidCollisionMode: FluidCollisionMode,
        ignorePassableBlocks: Boolean,
    ): RayTraceResult? {
        throw UnimplementedOperationException()
    }

    override fun rayTrace(
        start: Location,
        direction: Vector,
        maxDistance: Double,
        fluidCollisionMode: FluidCollisionMode,
        ignorePassableBlocks: Boolean,
        raySize: Double,
        filter: Predicate<Entity>?,
    ): RayTraceResult? {
        throw UnimplementedOperationException()
    }

    override fun getSpawnLocation(): Location = spawnLocation

    override fun setSpawnLocation(location: Location): Boolean =
        setSpawnLocation(location.blockX, location.blockY, location.blockZ)

    override fun setSpawnLocation(x: Int, y: Int, z: Int, angle: Float): Boolean =
        setSpawnLocation(x, y, z)

    override fun setSpawnLocation(x: Int, y: Int, z: Int): Boolean {
        spawnLocation.x = x.toDouble()
        spawnLocation.y = y.toDouble()
        spawnLocation.z = z.toDouble()
        return true
    }

    override fun getTime(): Long = fullTime % 24000

    override fun setTime(time: Long) {
        val base = fullTime - fullTime % 24000
        setFullTime(base + time % 24000)
    }

    override fun getFullTime(): Long = fullTime

    override fun setFullTime(time: Long) {
        val event = TimeSkipEvent(
            this,
            TimeSkipEvent.SkipReason.CUSTOM,
            time - fullTime
        )
        server.pluginManager.callEvent(event)
        if (!event.isCancelled) {
            fullTime += event.skipAmount
        }
    }

    override fun isDayTime(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun getGameTime(): Long {
        throw UnimplementedOperationException()
    }

    override fun hasStorm(): Boolean = hasStorm

    override fun setStorm(hasStorm: Boolean) {
        this.hasStorm = hasStorm
    }

    override fun getWeatherDuration(): Int {
        throw UnimplementedOperationException()
    }

    override fun setWeatherDuration(duration: Int) {
        throw UnimplementedOperationException()
    }

    override fun isThundering(): Boolean = thunderDuration > 0

    override fun setThundering(thundering: Boolean) {
        thunderDuration = if (thundering) 600 else 0
    }

    override fun getThunderDuration(): Int = thunderDuration

    override fun setThunderDuration(duration: Int) {
        thunderDuration = duration
    }

    override fun isClearWeather(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun getClearWeatherDuration(): Int {
        throw UnimplementedOperationException()
    }

    override fun setClearWeatherDuration(duration: Int) {
        throw UnimplementedOperationException()
    }

    override fun createExplosion(x: Double, y: Double, z: Double, power: Float): Boolean {
        throw UnimplementedOperationException()
    }

    override fun createExplosion(x: Double, y: Double, z: Double, power: Float, setFire: Boolean): Boolean {
        throw UnimplementedOperationException()
    }

    override fun createExplosion(
        x: Double,
        y: Double,
        z: Double,
        power: Float,
        setFire: Boolean,
        breakBlocks: Boolean,
    ): Boolean {
        throw UnimplementedOperationException()
    }

    override fun createExplosion(loc: Location, power: Float): Boolean {
        throw UnimplementedOperationException()
    }

    override fun createExplosion(loc: Location, power: Float, setFire: Boolean): Boolean {
        throw UnimplementedOperationException()
    }

    override fun createExplosion(loc: Location, power: Float, setFire: Boolean, breakBlocks: Boolean): Boolean {
        throw UnimplementedOperationException()
    }

    override fun createExplosion(
        loc: Location,
        power: Float,
        setFire: Boolean,
        breakBlocks: Boolean,
        source: Entity?,
    ): Boolean {
        throw UnimplementedOperationException()
    }

    override fun createExplosion(
        source: Entity?,
        loc: Location,
        power: Float,
        setFire: Boolean,
        breakBlocks: Boolean,
    ): Boolean {
        throw UnimplementedOperationException()
    }

    override fun createExplosion(
        x: Double,
        y: Double,
        z: Double,
        power: Float,
        setFire: Boolean,
        breakBlocks: Boolean,
        source: Entity?,
    ): Boolean {
        throw UnimplementedOperationException()
    }

    override fun getPVP(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun setPVP(pvp: Boolean) {
        throw UnimplementedOperationException()
    }

    override fun getGenerator(): ChunkGenerator? {
        throw UnimplementedOperationException()
    }

    override fun getBiomeProvider(): BiomeProvider? {
        throw UnimplementedOperationException()
    }

    override fun save() {
        throw UnimplementedOperationException()
    }

    override fun getPopulators(): MutableList<BlockPopulator> {
        throw UnimplementedOperationException()
    }

    override fun spawnFallingBlock(location: Location, data: BlockData): FallingBlock {
        throw UnimplementedOperationException()
    }

    override fun spawnFallingBlock(
        location: Location,
        @Suppress("DEPRECATION")
        data: org.bukkit.material.MaterialData,
    ): FallingBlock {
        throw UnimplementedOperationException()
    }

    override fun spawnFallingBlock(location: Location, material: Material, data: Byte): FallingBlock {
        throw UnimplementedOperationException()
    }

    override fun playEffect(location: Location, effect: Effect, data: Int) {
        throw UnimplementedOperationException()
    }

    override fun <T : Any?> playEffect(location: Location, effect: Effect, data: T?) {
        throw UnimplementedOperationException()
    }

    override fun <T : Any?> playEffect(location: Location, effect: Effect, data: T?, radius: Int) {
        throw UnimplementedOperationException()
    }

    override fun playEffect(location: Location, effect: Effect, data: Int, radius: Int) {
        throw UnimplementedOperationException()
    }

    override fun getEmptyChunkSnapshot(
        x: Int,
        z: Int,
        includeBiome: Boolean,
        includeBiomeTemp: Boolean,
    ): ChunkSnapshot {
        throw UnimplementedOperationException()
    }

    override fun setSpawnFlags(allowMonsters: Boolean, allowAnimals: Boolean) {
        throw UnimplementedOperationException()
    }

    override fun getAllowAnimals(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun getAllowMonsters(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun getBiome(x: Int, z: Int): Biome {
        throw UnimplementedOperationException()
    }

    override fun setBiome(x: Int, z: Int, bio: Biome) {
        throw UnimplementedOperationException()
    }

    override fun getTemperature(x: Int, z: Int): Double {
        throw UnimplementedOperationException()
    }

    override fun getTemperature(x: Int, y: Int, z: Int): Double {
        throw UnimplementedOperationException()
    }

    override fun getHumidity(x: Int, z: Int): Double {
        throw UnimplementedOperationException()
    }

    override fun getHumidity(x: Int, y: Int, z: Int): Double {
        throw UnimplementedOperationException()
    }

    override fun getLogicalHeight(): Int {
        throw UnimplementedOperationException()
    }

    override fun isNatural(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun isBedWorks(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun hasSkyLight(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun hasCeiling(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun isPiglinSafe(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun isRespawnAnchorWorks(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun hasRaids(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun isUltraWarm(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun getSeaLevel(): Int {
        throw UnimplementedOperationException()
    }

    override fun getKeepSpawnInMemory(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun setKeepSpawnInMemory(keepLoaded: Boolean) {
        throw UnimplementedOperationException()
    }

    override fun isAutoSave(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun setAutoSave(value: Boolean) {
        throw UnimplementedOperationException()
    }

    override fun getDifficulty(): Difficulty {
        throw UnimplementedOperationException()
    }

    override fun setDifficulty(difficulty: Difficulty) {
        throw UnimplementedOperationException()
    }

    override fun getWorldFolder(): File {
        throw UnimplementedOperationException()
    }

    override fun getWorldType(): WorldType = worldType

    override fun canGenerateStructures(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun isHardcore(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun setHardcore(hardcore: Boolean) {
        throw UnimplementedOperationException()
    }

    override fun getTicksPerAnimalSpawns(): Long {
        throw UnimplementedOperationException()
    }

    override fun setTicksPerAnimalSpawns(ticksPerAnimalSpawns: Int) {
        throw UnimplementedOperationException()
    }

    override fun getTicksPerMonsterSpawns(): Long {
        throw UnimplementedOperationException()
    }

    override fun setTicksPerMonsterSpawns(ticksPerMonsterSpawns: Int) {
        throw UnimplementedOperationException()
    }

    override fun getTicksPerWaterSpawns(): Long {
        throw UnimplementedOperationException()
    }

    override fun setTicksPerWaterSpawns(ticksPerWaterSpawns: Int) {
        throw UnimplementedOperationException()
    }

    override fun getTicksPerWaterAmbientSpawns(): Long {
        throw UnimplementedOperationException()
    }

    override fun setTicksPerWaterAmbientSpawns(ticksPerAmbientSpawns: Int) {
        throw UnimplementedOperationException()
    }

    override fun getTicksPerWaterUndergroundCreatureSpawns(): Long {
        throw UnimplementedOperationException()
    }

    override fun setTicksPerWaterUndergroundCreatureSpawns(ticksPerWaterUndergroundCreatureSpawns: Int) {
        throw UnimplementedOperationException()
    }

    override fun getTicksPerAmbientSpawns(): Long {
        throw UnimplementedOperationException()
    }

    override fun setTicksPerAmbientSpawns(ticksPerAmbientSpawns: Int) {
        throw UnimplementedOperationException()
    }

    override fun getMonsterSpawnLimit(): Int {
        throw UnimplementedOperationException()
    }

    override fun setMonsterSpawnLimit(limit: Int) {
        throw UnimplementedOperationException()
    }

    override fun getAnimalSpawnLimit(): Int {
        throw UnimplementedOperationException()
    }

    override fun setAnimalSpawnLimit(limit: Int) {
        throw UnimplementedOperationException()
    }

    override fun getWaterAnimalSpawnLimit(): Int {
        throw UnimplementedOperationException()
    }

    override fun setWaterAnimalSpawnLimit(limit: Int) {
        throw UnimplementedOperationException()
    }

    override fun getWaterUndergroundCreatureSpawnLimit(): Int {
        throw UnimplementedOperationException()
    }

    override fun setWaterUndergroundCreatureSpawnLimit(limit: Int) {
        throw UnimplementedOperationException()
    }

    override fun getWaterAmbientSpawnLimit(): Int {
        throw UnimplementedOperationException()
    }

    override fun setWaterAmbientSpawnLimit(limit: Int) {
        throw UnimplementedOperationException()
    }

    override fun getAmbientSpawnLimit(): Int {
        throw UnimplementedOperationException()
    }

    override fun setAmbientSpawnLimit(limit: Int) {
        throw UnimplementedOperationException()
    }

    override fun playSound(location: Location, sound: Sound, volume: Float, pitch: Float) {
        throw UnimplementedOperationException()
    }

    override fun playSound(location: Location, sound: String, volume: Float, pitch: Float) {
        throw UnimplementedOperationException()
    }

    override fun playSound(location: Location, sound: Sound, category: SoundCategory, volume: Float, pitch: Float) {
        throw UnimplementedOperationException()
    }

    override fun playSound(location: Location, sound: String, category: SoundCategory, volume: Float, pitch: Float) {
        throw UnimplementedOperationException()
    }

    override fun getGameRules(): Array<String> = gameRules.values
        .map { it.toString() }
        .toTypedArray()

    override fun getGameRuleValue(rule: String?): String? {
        if (rule == null) return null
        val gameRule = GameRule.getByName(rule) ?: return null
        return getGameRuleValue(gameRule)?.toString()
    }

    @Suppress("UNCHECKED_CAST")
    override fun setGameRuleValue(rule: String, value: String): Boolean {
        val gameRule = GameRule.getByName(rule) ?: return false
        return when (gameRule.type) {
            Boolean::class.java -> {
                val converted = value.toBooleanStrictOrNull() ?: return false
                setGameRule(gameRule as GameRule<Boolean>, converted)
            }
            Int::class.java -> {
                val converted = value.toIntOrNull() ?: return false
                setGameRule(gameRule as GameRule<Int>, converted)
            }
            else -> {
                false
            }
        }
    }

    override fun isGameRule(rule: String): Boolean = GameRule.getByName(rule) != null

    override fun <T : Any?> getGameRuleValue(rule: GameRule<T>): T? =
        rule.type.cast(gameRules[rule])

    override fun <T : Any?> getGameRuleDefault(rule: GameRule<T>): T? {
        throw UnimplementedOperationException()
    }

    override fun <T> setGameRule(rule: GameRule<T>, newValue: T): Boolean {
        gameRules[rule] = newValue
        return true
    }

    override fun getWorldBorder(): WorldBorder {
        throw UnimplementedOperationException()
    }

    override fun spawnParticle(particle: Particle, location: Location, count: Int) {
        throw UnimplementedOperationException()
    }

    override fun <T : Any?> spawnParticle(particle: Particle, location: Location, count: Int, data: T?) {
        throw UnimplementedOperationException()
    }

    override fun spawnParticle(particle: Particle, x: Double, y: Double, z: Double, count: Int) {
        throw UnimplementedOperationException()
    }

    override fun <T : Any?> spawnParticle(particle: Particle, x: Double, y: Double, z: Double, count: Int, data: T?) {
        throw UnimplementedOperationException()
    }

    override fun spawnParticle(
        particle: Particle,
        location: Location,
        count: Int,
        offsetX: Double,
        offsetY: Double,
        offsetZ: Double,
    ) {
        throw UnimplementedOperationException()
    }

    override fun <T : Any?> spawnParticle(
        particle: Particle,
        location: Location,
        count: Int,
        offsetX: Double,
        offsetY: Double,
        offsetZ: Double,
        data: T?,
    ) {
        throw UnimplementedOperationException()
    }

    override fun spawnParticle(
        particle: Particle,
        location: Location,
        count: Int,
        offsetX: Double,
        offsetY: Double,
        offsetZ: Double,
        extra: Double,
    ) {
        throw UnimplementedOperationException()
    }

    override fun <T : Any?> spawnParticle(
        particle: Particle,
        location: Location,
        count: Int,
        offsetX: Double,
        offsetY: Double,
        offsetZ: Double,
        extra: Double,
        data: T?,
    ) {
        throw UnimplementedOperationException()
    }

    override fun spawnParticle(
        particle: Particle,
        x: Double,
        y: Double,
        z: Double,
        count: Int,
        offsetX: Double,
        offsetY: Double,
        offsetZ: Double,
    ) {
        throw UnimplementedOperationException()
    }

    override fun <T : Any?> spawnParticle(
        particle: Particle,
        location: Location,
        count: Int,
        offsetX: Double,
        offsetY: Double,
        offsetZ: Double,
        extra: Double,
        data: T?,
        force: Boolean,
    ) {
        throw UnimplementedOperationException()
    }

    override fun <T : Any?> spawnParticle(
        particle: Particle,
        x: Double,
        y: Double,
        z: Double,
        count: Int,
        offsetX: Double,
        offsetY: Double,
        offsetZ: Double,
        data: T?,
    ) {
        throw UnimplementedOperationException()
    }

    override fun spawnParticle(
        particle: Particle,
        x: Double,
        y: Double,
        z: Double,
        count: Int,
        offsetX: Double,
        offsetY: Double,
        offsetZ: Double,
        extra: Double,
    ) {
        throw UnimplementedOperationException()
    }

    override fun <T : Any?> spawnParticle(
        particle: Particle,
        x: Double,
        y: Double,
        z: Double,
        count: Int,
        offsetX: Double,
        offsetY: Double,
        offsetZ: Double,
        extra: Double,
        data: T?,
        force: Boolean,
    ) {
        throw UnimplementedOperationException()
    }

    override fun <T : Any?> spawnParticle(
        particle: Particle,
        receivers: MutableList<Player>?,
        source: Player?,
        x: Double,
        y: Double,
        z: Double,
        count: Int,
        offsetX: Double,
        offsetY: Double,
        offsetZ: Double,
        extra: Double,
        data: T?,
        force: Boolean,
    ) {
        throw UnimplementedOperationException()
    }

    override fun locateNearestStructure(
        origin: Location,
        structureType: StructureType,
        radius: Int,
        findUnexplored: Boolean,
    ): Location? {
        throw UnimplementedOperationException()
    }

    override fun locateNearestBiome(origin: Location, biome: Biome, radius: Int): Location? {
        throw UnimplementedOperationException()
    }

    override fun locateNearestBiome(origin: Location, biome: Biome, radius: Int, step: Int): Location? {
        throw UnimplementedOperationException()
    }

    override fun locateNearestRaid(location: Location, radius: Int): Raid? {
        throw UnimplementedOperationException()
    }

    override fun isUltrawarm(): Boolean = isUltraWarm

    override fun getCoordinateScale(): Double {
        throw UnimplementedOperationException()
    }

    override fun hasSkylight(): Boolean = hasSkyLight()

    override fun hasBedrockCeiling(): Boolean = hasCeiling()

    override fun doesBedWork(): Boolean = isBedWorks

    override fun doesRespawnAnchorWork(): Boolean = isRespawnAnchorWorks

    override fun isFixedTime(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun getInfiniburn(): MutableCollection<Material> {
        throw UnimplementedOperationException()
    }

    override fun sendGameEvent(sourceEntity: Entity?, gameEvent: GameEvent, position: Vector) {
        throw UnimplementedOperationException()
    }

    override fun getViewDistance(): Int {
        throw UnimplementedOperationException()
    }

    override fun setViewDistance(viewDistance: Int) {
        throw UnimplementedOperationException()
    }

    override fun getSimulationDistance(): Int {
        throw UnimplementedOperationException()
    }

    override fun getNoTickViewDistance(): Int {
        throw UnimplementedOperationException()
    }

    override fun setNoTickViewDistance(viewDistance: Int) {
        throw UnimplementedOperationException()
    }

    override fun getSendViewDistance(): Int {
        throw UnimplementedOperationException()
    }

    override fun setSendViewDistance(viewDistance: Int) {
        throw UnimplementedOperationException()
    }

    override fun spigot(): World.Spigot {
        throw UnimplementedOperationException()
    }

    override fun getRaids(): List<Raid> {
        throw UnimplementedOperationException()
    }

    override fun getEnderDragonBattle(): DragonBattle? {
        throw UnimplementedOperationException()
    }

    override fun getBiome(location: Location): Biome {
        throw UnimplementedOperationException()
    }

    override fun getBiome(x: Int, y: Int, z: Int): Biome {
        throw UnimplementedOperationException()
    }

    override fun setBiome(location: Location, biome: Biome) {
        throw UnimplementedOperationException()
    }

    override fun setBiome(x: Int, y: Int, z: Int, biome: Biome) {
        throw UnimplementedOperationException()
    }

    override fun getBlockState(location: Location): BlockState {
        throw UnimplementedOperationException()
    }

    override fun getBlockState(x: Int, y: Int, z: Int): BlockState {
        throw UnimplementedOperationException()
    }

    override fun getBlockData(location: Location): BlockData {
        throw UnimplementedOperationException()
    }

    override fun getBlockData(x: Int, y: Int, z: Int): BlockData {
        throw UnimplementedOperationException()
    }

    override fun getType(location: Location): Material {
        throw UnimplementedOperationException()
    }

    override fun getType(x: Int, y: Int, z: Int): Material {
        throw UnimplementedOperationException()
    }

    override fun setBlockData(location: Location, blockData: BlockData) {
        throw UnimplementedOperationException()
    }

    override fun setBlockData(x: Int, y: Int, z: Int, blockData: BlockData) {
        throw UnimplementedOperationException()
    }

    override fun setType(location: Location, material: Material) {
        throw UnimplementedOperationException()
    }

    override fun setType(x: Int, y: Int, z: Int, material: Material) {
        throw UnimplementedOperationException()
    }

    override fun spawnEntity(location: Location, type: EntityType): Entity =
        spawn(location, type.entityClass!!)

    override fun spawnEntity(loc: Location, type: EntityType, randomizeData: Boolean): Entity {
        throw UnimplementedOperationException()
    }

    private fun <T : Entity?> mockEntity(clazz: Class<T>): EntityMock =
        when (clazz) {
            ArmorStand::class.java,
            ArmorStandMock::class.java,
            -> ArmorStandMock(server, UUID.randomUUID())
            Zombie::class.java,
            ZombieMock::class.java,
            -> ZombieMock(server, UUID.randomUUID())
            Firework::class.java,
            FireworkMock::class.java,
            -> FireworkMock(server, UUID.randomUUID())
            ExperienceOrb::class.java,
            ExperienceOrbMock::class.java,
            -> ExperienceOrbMock(server, UUID.randomUUID())
            Player::class.java,
            PlayerMock::class.java,
            -> throw IllegalArgumentException(
                "Player entities cannot be spawned, use ServerMock#addPlayer(...)"
            )
            Item::class.java,
            ItemEntityMock::class.java,
            -> throw IllegalArgumentException("Items must be spawned using World#dropItem(...)")
            else -> throw UnimplementedOperationException()
        }

    override fun <T : Entity?> spawn(location: Location, clazz: Class<T>): T {
        val entity = mockEntity(clazz)
        entity.location = location
        server.registerEntity(entity)
        return clazz.cast(entity)
    }

    override fun <T : Entity?> spawn(
        location: Location,
        clazz: Class<T>,
        function: Consumer<T>?,
        reason: CreatureSpawnEvent.SpawnReason,
    ): T {
        val entity = spawn(location, clazz)
        function?.accept(entity)
        return entity
    }

    override fun <T : Entity?> spawn(
        location: Location,
        clazz: Class<T>,
        randomizeData: Boolean,
        function: Consumer<T>?,
    ): T {
        throw UnimplementedOperationException()
    }

    /**
     * Creates a new entity at the given Location with the supplied function run
     * before the entity is added to the world.
     *
     * Note that when the function is run, the entity will not be actually in the world.
     * Any operation involving such as teleporting the entity is undefined until after this function returns.
     * The passed function however is run after the potential entity's spawn randomization and hence already allows
     * access to the values of the mob, whether those were randomized, such as attributes or the entity equipment.
     */
    @JvmSynthetic
    inline fun <reified T : Entity> spawn(location: Location): T = spawn(location, T::class.java)

    override fun sendPluginMessage(source: Plugin, channel: String, message: ByteArray) {
        throw UnimplementedOperationException()
    }

    override fun getListeningPluginChannels(): MutableSet<String> {
        throw UnimplementedOperationException()
    }
}
