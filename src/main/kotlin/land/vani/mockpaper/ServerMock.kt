package land.vani.mockpaper

import com.destroystokyo.paper.entity.ai.MobGoals
import com.destroystokyo.paper.profile.PlayerProfile
import io.papermc.paper.datapack.DatapackManager
import land.vani.mockpaper.boss.BossBarMock
import land.vani.mockpaper.boss.KeyedBossBarMock
import land.vani.mockpaper.command.CommandMapMock
import land.vani.mockpaper.command.ConsoleCommandSenderMock
import land.vani.mockpaper.enchantments.registerDefaultEnchantments
import land.vani.mockpaper.entity.EntityMock
import land.vani.mockpaper.help.HelpMapMock
import land.vani.mockpaper.internal.asUnmodifiable
import land.vani.mockpaper.inventory.BarrelInventoryMock
import land.vani.mockpaper.inventory.ChestInventoryMock
import land.vani.mockpaper.inventory.DispenserInventoryMock
import land.vani.mockpaper.inventory.DropperInventoryMock
import land.vani.mockpaper.inventory.EnderChestInventoryMock
import land.vani.mockpaper.inventory.HopperInventoryMock
import land.vani.mockpaper.inventory.InventoryMock
import land.vani.mockpaper.inventory.ItemFactoryMock
import land.vani.mockpaper.inventory.LecternInventoryMock
import land.vani.mockpaper.inventory.PlayerInventoryMock
import land.vani.mockpaper.inventory.ShulkerBoxInventoryMock
import land.vani.mockpaper.inventory.meta.ItemMetaMock
import land.vani.mockpaper.player.OfflinePlayerMock
import land.vani.mockpaper.player.PlayerMock
import land.vani.mockpaper.player.randomPlayerName
import land.vani.mockpaper.plugin.PluginManagerMock
import land.vani.mockpaper.potion.registerPotionEffectTypes
import land.vani.mockpaper.scheduler.BukkitSchedulerMock
import land.vani.mockpaper.world.WorldMock
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.md_5.bungee.api.chat.BaseComponent
import org.bukkit.BanList
import org.bukkit.GameMode
import org.bukkit.Keyed
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.OfflinePlayer
import org.bukkit.Server
import org.bukkit.StructureType
import org.bukkit.Tag
import org.bukkit.Warning
import org.bukkit.World
import org.bukkit.WorldCreator
import org.bukkit.advancement.Advancement
import org.bukkit.block.data.BlockData
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarFlag
import org.bukkit.boss.BarStyle
import org.bukkit.boss.BossBar
import org.bukkit.boss.KeyedBossBar
import org.bukkit.command.CommandMap
import org.bukkit.command.CommandSender
import org.bukkit.command.PluginCommand
import org.bukkit.configuration.serialization.ConfigurationSerialization
import org.bukkit.entity.Entity
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.generator.ChunkGenerator
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemFactory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.Merchant
import org.bukkit.inventory.Recipe
import org.bukkit.loot.LootTable
import org.bukkit.map.MapView
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.ServicesManager
import org.bukkit.plugin.messaging.Messenger
import org.bukkit.scoreboard.ScoreboardManager
import org.bukkit.structure.StructureManager
import org.bukkit.util.CachedServerIcon
import org.jetbrains.annotations.VisibleForTesting
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import java.util.UUID
import java.util.function.Consumer
import java.util.logging.Level
import java.util.logging.LogManager
import java.util.logging.Logger

open class ServerMock : Server, Server.Spigot() {
    companion object {
        private const val BUKKIT_VERSION = "1.18.1"
        private const val JOIN_MESSAGE = "%s has joined the server."
        private const val MOTD = "A Minecraft Server"
    }

    private val mainThread: Thread = Thread.currentThread()
    private val _logger = Logger.getLogger("MockPaper")

    private val _entities: MutableSet<EntityMock> = mutableSetOf()
    private val playerList: PlayerListMock by lazy {
        PlayerListMock(this)
    }

    private val pluginManagerMock: PluginManagerMock by lazy {
        PluginManagerMock(this)
    }
    private val unsafeValues = UnsafeValuesMock()
    private val worlds: MutableSet<WorldMock> = mutableSetOf()
    private val recipes: MutableSet<Recipe> = mutableSetOf()

    private var spawnRadius: Int = 16

    private val consoleCommandSender = ConsoleCommandSenderMock()
    private val helpMap = HelpMapMock()

    private val itemFactory = ItemFactoryMock()
    private val bossBars: MutableMap<NamespacedKey, KeyedBossBarMock> = mutableMapOf()
    private val commandMap: CommandMapMock by lazy {
        CommandMapMock(this)
    }
    private val schedulerMock: BukkitSchedulerMock by lazy {
        BukkitSchedulerMock(this)
    }

    init {
        registerSerializables()
    }

    private fun registerSerializables() {
        ConfigurationSerialization.registerClass(ItemMetaMock::class.java)
        // TODO: load tag

        registerDefaultEnchantments()
        registerPotionEffectTypes()

        try {
            ClassLoader.getSystemResourceAsStream("logger.properties").use {
                LogManager.getLogManager().readConfiguration(it)
            }
        } catch (ex: IOException) {
            logger.log(Level.WARNING, "Could not load file logger.properties", ex)
        }
        logger.level = Level.ALL
    }

    override fun sendPluginMessage(source: Plugin, channel: String, message: ByteArray) {
        throw UnimplementedOperationException()
    }

    override fun getListeningPluginChannels(): MutableSet<String> {
        throw UnimplementedOperationException()
    }

    override fun audiences(): Iterable<Audience> = onlinePlayers

    override fun getName(): String = "ServerMock"

    override fun getVersion(): String = "MockPaper (MC: $BUKKIT_VERSION)"

    override fun getBukkitVersion(): String = BUKKIT_VERSION

    override fun getMinecraftVersion(): String = BUKKIT_VERSION

    override fun getOnlinePlayers(): Collection<Player> = playerList.onlinePlayers

    override fun getMaxPlayers(): Int = playerList.maxPlayers

    override fun setMaxPlayers(maxPlayers: Int) {
        playerList.maxPlayers = maxPlayers
    }

    override fun getPort(): Int {
        throw UnimplementedOperationException()
    }

    override fun getViewDistance(): Int {
        throw UnimplementedOperationException()
    }

    override fun getSimulationDistance(): Int {
        throw UnimplementedOperationException()
    }

    override fun getIp(): String {
        throw UnimplementedOperationException()
    }

    override fun getWorldType(): String {
        throw UnimplementedOperationException()
    }

    override fun getGenerateStructures(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun getMaxWorldSize(): Int {
        throw UnimplementedOperationException()
    }

    override fun getAllowEnd(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun getAllowNether(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun getResourcePack(): String {
        throw UnimplementedOperationException()
    }

    override fun getResourcePackHash(): String {
        throw UnimplementedOperationException()
    }

    override fun getResourcePackPrompt(): String {
        throw UnimplementedOperationException()
    }

    override fun isResourcePackRequired(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun hasWhitelist(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun setWhitelist(value: Boolean) {
        throw UnimplementedOperationException()
    }

    override fun isWhitelistEnforced(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun setWhitelistEnforced(value: Boolean) {
        throw UnimplementedOperationException()
    }

    override fun getWhitelistedPlayers(): Set<OfflinePlayer> {
        throw UnimplementedOperationException()
    }

    override fun reloadWhitelist() {
        throw UnimplementedOperationException()
    }

    override fun broadcastMessage(message: String): Int =
        onlinePlayers.onEach {
            it.sendMessage(message)
        }.count()

    override fun broadcast(vararg components: BaseComponent) {
        onlinePlayers.forEach {
            @Suppress("DEPRECATION")
            it.sendMessage(*components)
        }
    }

    override fun broadcast(message: String, permission: String): Int =
        onlinePlayers.filter {
            it.hasPermission(permission)
        }.onEach {
            it.sendMessage(message)
        }.count()

    override fun broadcast(message: Component): Int =
        onlinePlayers.onEach {
            it.sendMessage(message)
        }.count()

    override fun broadcast(message: Component, permission: String): Int =
        onlinePlayers.filter {
            it.hasPermission(permission)
        }.onEach {
            it.sendMessage(message)
        }.count()

    override fun getUpdateFolder(): String {
        throw UnimplementedOperationException()
    }

    override fun getUpdateFolderFile(): File {
        throw UnimplementedOperationException()
    }

    override fun getConnectionThrottle(): Long {
        throw UnimplementedOperationException()
    }

    override fun getTicksPerAnimalSpawns(): Int {
        throw UnimplementedOperationException()
    }

    override fun getTicksPerMonsterSpawns(): Int {
        throw UnimplementedOperationException()
    }

    override fun getTicksPerWaterSpawns(): Int {
        throw UnimplementedOperationException()
    }

    override fun getTicksPerWaterAmbientSpawns(): Int {
        throw UnimplementedOperationException()
    }

    override fun getTicksPerWaterUndergroundCreatureSpawns(): Int {
        throw UnimplementedOperationException()
    }

    override fun getTicksPerAmbientSpawns(): Int {
        throw UnimplementedOperationException()
    }

    override fun getPlayer(name: String): Player? = playerList.getPlayer(name)

    override fun getPlayer(id: UUID): Player? = playerList.getPlayer(id)

    override fun getPlayerExact(name: String): Player? = playerList.getPlayerExact(name)

    override fun matchPlayer(name: String): List<Player> = playerList.matchPlayers(name)

    override fun getPlayerUniqueId(playerName: String): UUID = playerList.getOfflinePlayer(playerName).uniqueId

    override fun getPluginManager(): PluginManagerMock = pluginManagerMock

    override fun getScheduler(): BukkitSchedulerMock = schedulerMock

    override fun getServicesManager(): ServicesManager {
        throw UnimplementedOperationException()
    }

    override fun getWorlds(): List<World> = worlds.toList()

    override fun createWorld(creator: WorldCreator): WorldMock = WorldMock(this, creator)
        .also {
            assertMainThread()
            worlds += it
        }

    override fun unloadWorld(name: String, save: Boolean): Boolean {
        throw UnimplementedOperationException()
    }

    override fun unloadWorld(world: World, save: Boolean): Boolean {
        throw UnimplementedOperationException()
    }

    override fun getWorld(name: String): WorldMock? = worlds.find { it.name == name }

    override fun getWorld(uid: UUID): WorldMock? = worlds.find { it.uid == uid }

    override fun getWorld(worldKey: NamespacedKey): WorldMock? = worlds.find { it.key == worldKey }

    override fun getMap(id: Int): MapView? {
        throw UnimplementedOperationException()
    }

    override fun createMap(world: World): MapView {
        throw UnimplementedOperationException()
    }

    override fun createExplorerMap(world: World, location: Location, structureType: StructureType): ItemStack {
        throw UnimplementedOperationException()
    }

    override fun createExplorerMap(
        world: World,
        location: Location,
        structureType: StructureType,
        radius: Int,
        findUnexplored: Boolean,
    ): ItemStack {
        throw UnimplementedOperationException()
    }

    override fun reload() {
        throw UnimplementedOperationException()
    }

    override fun reloadData() {
        throw UnimplementedOperationException()
    }

    override fun getLogger(): Logger = _logger

    override fun getPluginCommand(name: String): PluginCommand? {
        assertMainThread()
        return commandMap.getCommand(name) as? PluginCommand?
    }

    override fun savePlayers() {
        throw UnimplementedOperationException()
    }

    override fun dispatchCommand(sender: CommandSender, commandLine: String): Boolean {
        assertMainThread()
        val commands = commandLine.split(" ").toTypedArray()
        val label = commands[0]
        val args = commands.copyOfRange(1, commands.size)
        val command = commandMap.getCommand(label) ?: return false

        return command.execute(sender, label, args)
    }

    override fun addRecipe(recipe: Recipe?): Boolean {
        assertMainThread()
        recipe?.let {
            recipes += it
            return true
        } ?: return false
    }

    override fun getRecipesFor(result: ItemStack): List<Recipe> {
        assertMainThread()

        return recipes.filter {
            it.result.type == result.type &&
                it.result.itemMeta == result.itemMeta
        }
    }

    override fun getRecipe(recipeKey: NamespacedKey): Recipe? {
        assertMainThread()
        return recipes.find { it is Keyed && it.key == recipeKey }
    }

    override fun getCraftingRecipe(craftingMatrix: Array<out ItemStack>, world: World): Recipe? {
        throw UnimplementedOperationException()
    }

    override fun craftItem(craftingMatrix: Array<out ItemStack>, world: World, player: Player): ItemStack {
        throw UnimplementedOperationException()
    }

    override fun recipeIterator(): MutableIterator<Recipe> {
        assertMainThread()
        return recipes.iterator()
    }

    override fun clearRecipes() {
        assertMainThread()
        recipes.clear()
    }

    override fun resetRecipes() {
        throw UnimplementedOperationException()
    }

    override fun removeRecipe(key: NamespacedKey): Boolean {
        assertMainThread()
        return recipes.removeAll {
            it is Keyed && it.key == key
        }
    }

    override fun getCommandAliases(): Map<String, Array<String>> {
        throw UnimplementedOperationException()
    }

    override fun getSpawnRadius(): Int = spawnRadius

    override fun setSpawnRadius(value: Int) {
        spawnRadius = value
    }

    override fun getHideOnlinePlayers(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun getOnlineMode(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun getAllowFlight(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun isHardcore(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun shutdown() {
        throw UnimplementedOperationException()
    }

    override fun getOfflinePlayer(name: String): OfflinePlayer = playerList.getOfflinePlayer(name)

    override fun getOfflinePlayer(id: UUID): OfflinePlayer = playerList.getOfflinePlayer(id)

    override fun getOfflinePlayerIfCached(name: String): OfflinePlayer {
        throw UnimplementedOperationException()
    }

    override fun getIPBans(): Set<String> = playerList.ipBans
        .banEntries
        .map { it.target }
        .toSet()

    override fun banIP(address: String) {
        assertMainThread()
        playerList.ipBans.addBan(address, null, null, null)
    }

    override fun unbanIP(address: String) {
        assertMainThread()
        playerList.ipBans.pardon(address)
    }

    override fun getBannedPlayers(): Set<OfflinePlayer> = playerList.profileBans
        .banEntries
        .map {
            @Suppress("DEPRECATION")
            getOfflinePlayer(it.target)
        }
        .toSet()

    override fun getBanList(type: BanList.Type): BanList =
        when (type) {
            BanList.Type.NAME -> playerList.profileBans
            BanList.Type.IP -> playerList.ipBans
        }

    override fun getOperators(): Set<OfflinePlayer> = playerList.offlinePlayers
        .filter { it.isOp }
        .toSet()

    override fun getDefaultGameMode(): GameMode {
        throw UnimplementedOperationException()
    }

    override fun setDefaultGameMode(mode: GameMode) {
        throw UnimplementedOperationException()
    }

    override fun getConsoleSender(): ConsoleCommandSenderMock = consoleCommandSender

    override fun getWorldContainer(): File {
        throw UnimplementedOperationException()
    }

    override fun getOfflinePlayers(): Array<OfflinePlayer> = playerList.offlinePlayers.toTypedArray()

    override fun getMessenger(): Messenger {
        throw UnimplementedOperationException()
    }

    override fun getHelpMap(): HelpMapMock = helpMap

    @Suppress("DEPRECATION")
    override fun createInventory(owner: InventoryHolder?, type: InventoryType): Inventory =
        createInventory(owner, type, "inventory")

    override fun createInventory(owner: InventoryHolder?, type: InventoryType, title: Component): Inventory =
        createInventory(owner, type, -1)

    override fun createInventory(owner: InventoryHolder?, type: InventoryType, title: String): Inventory =
        createInventory(owner, type, -1)

    @Suppress("DEPRECATION")
    override fun createInventory(owner: InventoryHolder?, size: Int): Inventory =
        createInventory(owner, size, "inventory")

    override fun createInventory(owner: InventoryHolder?, size: Int, title: Component): Inventory =
        createInventory(owner, InventoryType.CHEST, size)

    override fun createInventory(owner: InventoryHolder?, size: Int, title: String): Inventory =
        createInventory(owner, InventoryType.CHEST, size)

    @VisibleForTesting
    fun createInventory(owner: InventoryHolder?, type: InventoryType, size: Int): InventoryMock {
        assertMainThread()

        if (!type.isCreatable) {
            throw IllegalArgumentException("Inventory type is not creatable")
        }
        return when (type) {
            InventoryType.CHEST -> ChestInventoryMock(owner, if (size > 0) size else 9 * 3)
            InventoryType.DISPENSER -> DispenserInventoryMock(owner)
            InventoryType.DROPPER -> DropperInventoryMock(owner)
            InventoryType.PLAYER -> {
                if (owner is HumanEntity) {
                    PlayerInventoryMock(owner)
                } else {
                    throw IllegalArgumentException("Cannot create a Player inventory for: $owner")
                }
            }
            InventoryType.ENDER_CHEST -> EnderChestInventoryMock(owner)
            InventoryType.HOPPER -> HopperInventoryMock(owner)
            InventoryType.SHULKER_BOX -> ShulkerBoxInventoryMock(owner)
            InventoryType.BARREL -> BarrelInventoryMock(owner)
            InventoryType.LECTERN -> LecternInventoryMock(owner)
            else -> throw UnimplementedOperationException("Inventory type $type is not supported yet")
        }
    }

    override fun createMerchant(title: Component?): Merchant {
        throw UnimplementedOperationException()
    }

    override fun createMerchant(title: String?): Merchant {
        throw UnimplementedOperationException()
    }

    override fun getMonsterSpawnLimit(): Int {
        throw UnimplementedOperationException()
    }

    override fun getAnimalSpawnLimit(): Int {
        throw UnimplementedOperationException()
    }

    override fun getWaterAnimalSpawnLimit(): Int {
        throw UnimplementedOperationException()
    }

    override fun getWaterAmbientSpawnLimit(): Int {
        throw UnimplementedOperationException()
    }

    override fun getWaterUndergroundCreatureSpawnLimit(): Int {
        throw UnimplementedOperationException()
    }

    override fun getAmbientSpawnLimit(): Int {
        throw UnimplementedOperationException()
    }

    override fun isPrimaryThread(): Boolean = isOnMainThread

    override fun motd(): Component = Component.text(MOTD)

    override fun getMotd(): String = MOTD

    override fun shutdownMessage(): Component? {
        throw UnimplementedOperationException()
    }

    override fun getShutdownMessage(): String? {
        throw UnimplementedOperationException()
    }

    override fun getWarningState(): Warning.WarningState {
        throw UnimplementedOperationException()
    }

    override fun getItemFactory(): ItemFactory = itemFactory

    override fun getScoreboardManager(): ScoreboardManager {
        throw UnimplementedOperationException()
    }

    override fun getServerIcon(): CachedServerIcon? {
        throw UnimplementedOperationException()
    }

    override fun loadServerIcon(file: File): CachedServerIcon {
        throw UnimplementedOperationException()
    }

    override fun loadServerIcon(image: BufferedImage): CachedServerIcon {
        throw UnimplementedOperationException()
    }

    override fun setIdleTimeout(threshold: Int) {
        throw UnimplementedOperationException()
    }

    override fun getIdleTimeout(): Int {
        throw UnimplementedOperationException()
    }

    override fun createChunkData(world: World): ChunkGenerator.ChunkData {
        throw UnimplementedOperationException()
    }

    override fun createVanillaChunkData(world: World, x: Int, z: Int): ChunkGenerator.ChunkData {
        throw UnimplementedOperationException()
    }

    override fun createBossBar(title: String?, color: BarColor, style: BarStyle, vararg flags: BarFlag): BossBar =
        BossBarMock(title, color, style, *flags)

    override fun createBossBar(
        key: NamespacedKey,
        title: String?,
        color: BarColor,
        style: BarStyle,
        vararg flags: BarFlag,
    ): KeyedBossBar = KeyedBossBarMock(key, title, color, style, *flags).also {
        bossBars[key] = it
    }

    override fun getBossBars(): Iterator<KeyedBossBar> = bossBars.values.iterator()

    override fun getBossBar(key: NamespacedKey): KeyedBossBar? = bossBars[key]

    override fun removeBossBar(key: NamespacedKey): Boolean = bossBars.remove(key) != null

    override fun getEntity(uuid: UUID): Entity? = entities.find { it.uniqueId == uuid }

    override fun getTPS(): DoubleArray {
        throw UnimplementedOperationException()
    }

    override fun getTickTimes(): LongArray {
        throw UnimplementedOperationException()
    }

    override fun getAverageTickTime(): Double {
        throw UnimplementedOperationException()
    }

    override fun getCommandMap(): CommandMap = commandMap

    override fun getAdvancement(key: NamespacedKey): Advancement? {
        throw UnimplementedOperationException()
    }

    override fun advancementIterator(): MutableIterator<Advancement> {
        throw UnimplementedOperationException()
    }

    override fun createBlockData(material: Material): BlockData {
        throw UnimplementedOperationException()
    }

    override fun createBlockData(material: Material, consumer: Consumer<BlockData>?): BlockData {
        throw UnimplementedOperationException()
    }

    override fun createBlockData(data: String): BlockData {
        throw UnimplementedOperationException()
    }

    override fun createBlockData(material: Material?, data: String?): BlockData {
        throw UnimplementedOperationException()
    }

    override fun <T : Keyed?> getTag(registry: String, tag: NamespacedKey, clazz: Class<T>): Tag<T>? {
        throw UnimplementedOperationException()
    }

    override fun <T : Keyed?> getTags(registry: String, clazz: Class<T>): MutableIterable<Tag<T>> {
        throw UnimplementedOperationException()
    }

    override fun getLootTable(key: NamespacedKey): LootTable? {
        throw UnimplementedOperationException()
    }

    override fun selectEntities(sender: CommandSender, selector: String): MutableList<Entity> {
        throw UnimplementedOperationException()
    }

    override fun getStructureManager(): StructureManager {
        throw UnimplementedOperationException()
    }

    @Suppress("DEPRECATION")
    override fun getUnsafe(): org.bukkit.UnsafeValues = unsafeValues

    override fun spigot(): Server.Spigot {
        throw UnimplementedOperationException()
    }

    override fun reloadPermissions() {
        throw UnimplementedOperationException()
    }

    override fun reloadCommandAliases(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun suggestPlayerNamesWhenNullTabCompletions(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun getPermissionMessage(): String {
        throw UnimplementedOperationException()
    }

    override fun createProfile(uuid: UUID): PlayerProfile {
        throw UnimplementedOperationException()
    }

    override fun createProfile(name: String): PlayerProfile {
        throw UnimplementedOperationException()
    }

    override fun createProfile(uuid: UUID?, name: String?): PlayerProfile {
        throw UnimplementedOperationException()
    }

    override fun getCurrentTick(): Int {
        throw UnimplementedOperationException()
    }

    override fun getPluginsFolder(): File {
        throw UnimplementedOperationException()
    }

    override fun isStopping(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun getMobGoals(): MobGoals {
        throw UnimplementedOperationException()
    }

    override fun getDatapackManager(): DatapackManager {
        throw UnimplementedOperationException()
    }

    override fun broadcast(component: BaseComponent) {
        onlinePlayers.forEach {
            @Suppress("DEPRECATION")
            it.sendMessage(component)
        }
    }

    open val currentServerTime: Long
        get() = System.currentTimeMillis()

    val isOnMainThread: Boolean
        get() = mainThread == Thread.currentThread()

    /**
     * Checks if we are running a method on the main thread.
     *
     * @throws IllegalThreadStateException If calling this method with non-main thread.
     */
    @Throws(IllegalThreadStateException::class)
    fun assertMainThread() {
        if (!isOnMainThread) {
            throw IllegalThreadStateException()
        }
    }

    /**
     * Returns a set of entities that exist on the server instance.
     */
    val entities: Set<Entity>
        get() = _entities.asUnmodifiable()

    fun registerEntity(entity: EntityMock): EntityMock = entity.also {
        assertMainThread()
        _entities += it
    }

    /**
     * Adds the given mocked [world] to this server.
     */
    fun addWorld(world: WorldMock): WorldMock = world.also {
        assertMainThread()
        worlds += it
    }

    /**
     * Adds a very simple super flat world with a given [name].
     */
    fun addSimpleWorld(name: String): WorldMock {
        assertMainThread()
        val world = WorldMock(this)
        world.name = name
        worlds += world
        return world
    }

    /**
     * Add a specific [player] to the set.
     */
    @VisibleForTesting
    fun addPlayer(player: PlayerMock): PlayerMock {
        assertMainThread()
        playerList.addPlayer(player)
        player.lastPlayed = currentServerTime
        @Suppress("DEPRECATION")
        val event = PlayerJoinEvent(
            player,
            JOIN_MESSAGE.format(player.displayName)
        )
        pluginManager.callEvent(event)
        registerEntity(player)

        return player
    }

    /**
     * Add a random player and adds it.
     */
    @VisibleForTesting
    fun addPlayer(): PlayerMock {
        assertMainThread()
        var playerName: String = randomPlayerName()
        while (playerName in offlinePlayers.map { it.name }) {
            playerName = randomPlayerName()
        }
        val player = PlayerMock(this, playerName, UUID.randomUUID())
        return addPlayer(player)
    }

    /**
     * Add a player with a given [name] and adds it.
     */
    @VisibleForTesting
    fun addPlayer(name: String): PlayerMock {
        assertMainThread()
        val player = PlayerMock(this, name)
        return addPlayer(player)
    }

    /**
     * Set the [amount] of mock players that are on this server.
     *
     * Note that it will remove all players that are already on this server.
     */
    @VisibleForTesting
    fun setPlayers(amount: Int) {
        require(amount >= 0) { "player amount is more or equals than 0" }
        assertMainThread()
        playerList.clearOnlinePlayers()
        repeat(amount) {
            addPlayer()
        }
    }

    /**
     * Add a specific offline [player] to the set.
     */
    @VisibleForTesting
    fun addOfflinePlayer(player: OfflinePlayerMock): OfflinePlayerMock {
        assertMainThread()
        playerList.addOfflinePlayer(player)
        return player
    }

    /**
     * Add a random player and adds it.
     */
    @VisibleForTesting
    fun addOfflinePlayer(): OfflinePlayerMock {
        assertMainThread()
        var playerName: String = randomPlayerName()
        while (playerName in offlinePlayers.map { it.name }) {
            playerName = randomPlayerName()
        }
        val player = OfflinePlayerMock(this, playerName, UUID.randomUUID())
        addOfflinePlayer(player)
        return player
    }

    /**
     * Add a player with a given [name] and adds it.
     */
    @VisibleForTesting
    fun addOfflinePlayer(name: String): OfflinePlayerMock {
        assertMainThread()
        val player = OfflinePlayerMock(this, name)
        addOfflinePlayer(player)
        return player
    }

    /**
     * Set the [amount] of mock offline players that are on this server.
     *
     * Note that even players that are online are also
     * considered offline player because an [OfflinePlayer] really just refers to anyone that has at some point
     * in time played on the server.
     */
    @VisibleForTesting
    fun setOfflinePlayers(amount: Int) {
        require(amount >= 0) { "player amount is more or equals than 0" }
        assertMainThread()
        playerList.clearOfflinePlayers()
        playerList.onlinePlayers.forEach {
            playerList.addPlayer(it)
        }
        repeat(amount) {
            addOfflinePlayer()
        }
    }
}
