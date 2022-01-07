package land.vani.mockpaper

import com.destroystokyo.paper.entity.ai.MobGoals
import com.destroystokyo.paper.profile.PlayerProfile
import io.papermc.paper.datapack.DatapackManager
import land.vani.mockpaper.entity.EntityMock
import land.vani.mockpaper.internal.asUnmodifiable
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
import org.bukkit.UnsafeValues
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
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.command.PluginCommand
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryType
import org.bukkit.generator.ChunkGenerator
import org.bukkit.help.HelpMap
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemFactory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.Merchant
import org.bukkit.inventory.Recipe
import org.bukkit.loot.LootTable
import org.bukkit.map.MapView
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.PluginManager
import org.bukkit.plugin.ServicesManager
import org.bukkit.plugin.messaging.Messenger
import org.bukkit.scheduler.BukkitScheduler
import org.bukkit.scoreboard.ScoreboardManager
import org.bukkit.structure.StructureManager
import org.bukkit.util.CachedServerIcon
import java.awt.image.BufferedImage
import java.io.File
import java.util.UUID
import java.util.function.Consumer
import java.util.logging.Logger

class ServerMock : Server, Server.Spigot() {
    private val mainThread: Thread = Thread.currentThread()

    private val _entities: MutableSet<EntityMock> = mutableSetOf()

    override fun sendPluginMessage(source: Plugin, channel: String, message: ByteArray) {
        throw UnimplementedOperationException()
    }

    override fun getListeningPluginChannels(): MutableSet<String> {
        throw UnimplementedOperationException()
    }

    override fun audiences(): MutableIterable<Audience> {
        throw UnimplementedOperationException()
    }

    override fun getName(): String {
        throw UnimplementedOperationException()
    }

    override fun getVersion(): String {
        throw UnimplementedOperationException()
    }

    override fun getBukkitVersion(): String {
        throw UnimplementedOperationException()
    }

    override fun getMinecraftVersion(): String {
        throw UnimplementedOperationException()
    }

    override fun getOnlinePlayers(): MutableCollection<out Player> {
        throw UnimplementedOperationException()
    }

    override fun getMaxPlayers(): Int {
        throw UnimplementedOperationException()
    }

    override fun setMaxPlayers(maxPlayers: Int) {
        throw UnimplementedOperationException()
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

    override fun getWhitelistedPlayers(): MutableSet<OfflinePlayer> {
        throw UnimplementedOperationException()
    }

    override fun reloadWhitelist() {
        throw UnimplementedOperationException()
    }

    override fun broadcastMessage(message: String): Int {
        throw UnimplementedOperationException()
    }

    override fun broadcast(vararg components: BaseComponent) {
        throw UnimplementedOperationException()
    }

    override fun broadcast(message: String, permission: String): Int {
        throw UnimplementedOperationException()
    }

    override fun broadcast(message: Component): Int {
        throw UnimplementedOperationException()
    }

    override fun broadcast(message: Component, permission: String): Int {
        throw UnimplementedOperationException()
    }

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

    override fun getPlayer(name: String): Player? {
        throw UnimplementedOperationException()
    }

    override fun getPlayer(id: UUID): Player? {
        throw UnimplementedOperationException()
    }

    override fun getPlayerExact(name: String): Player? {
        throw UnimplementedOperationException()
    }

    override fun matchPlayer(name: String): MutableList<Player> {
        throw UnimplementedOperationException()
    }

    override fun getPlayerUniqueId(playerName: String): UUID? {
        throw UnimplementedOperationException()
    }

    override fun getPluginManager(): PluginManager {
        throw UnimplementedOperationException()
    }

    override fun getScheduler(): BukkitScheduler {
        throw UnimplementedOperationException()
    }

    override fun getServicesManager(): ServicesManager {
        throw UnimplementedOperationException()
    }

    override fun getWorlds(): MutableList<World> {
        throw UnimplementedOperationException()
    }

    override fun createWorld(creator: WorldCreator): World? {
        throw UnimplementedOperationException()
    }

    override fun unloadWorld(name: String, save: Boolean): Boolean {
        throw UnimplementedOperationException()
    }

    override fun unloadWorld(world: World, save: Boolean): Boolean {
        throw UnimplementedOperationException()
    }

    override fun getWorld(name: String): World? {
        throw UnimplementedOperationException()
    }

    override fun getWorld(uid: UUID): World? {
        throw UnimplementedOperationException()
    }

    override fun getWorld(worldKey: NamespacedKey): World? {
        throw UnimplementedOperationException()
    }

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

    private val _logger = Logger.getLogger("MockPaper")
    override fun getLogger(): Logger = _logger

    override fun getPluginCommand(name: String): PluginCommand? {
        throw UnimplementedOperationException()
    }

    override fun savePlayers() {
        throw UnimplementedOperationException()
    }

    override fun dispatchCommand(sender: CommandSender, commandLine: String): Boolean {
        throw UnimplementedOperationException()
    }

    override fun addRecipe(recipe: Recipe?): Boolean {
        throw UnimplementedOperationException()
    }

    override fun getRecipesFor(result: ItemStack): MutableList<Recipe> {
        throw UnimplementedOperationException()
    }

    override fun getRecipe(recipeKey: NamespacedKey): Recipe? {
        throw UnimplementedOperationException()
    }

    override fun getCraftingRecipe(craftingMatrix: Array<out ItemStack>, world: World): Recipe? {
        throw UnimplementedOperationException()
    }

    override fun craftItem(craftingMatrix: Array<out ItemStack>, world: World, player: Player): ItemStack {
        throw UnimplementedOperationException()
    }

    override fun recipeIterator(): MutableIterator<Recipe> {
        throw UnimplementedOperationException()
    }

    override fun clearRecipes() {
        throw UnimplementedOperationException()
    }

    override fun resetRecipes() {
        throw UnimplementedOperationException()
    }

    override fun removeRecipe(key: NamespacedKey): Boolean {
        throw UnimplementedOperationException()
    }

    override fun getCommandAliases(): MutableMap<String, Array<String>> {
        throw UnimplementedOperationException()
    }

    override fun getSpawnRadius(): Int {
        throw UnimplementedOperationException()
    }

    override fun setSpawnRadius(value: Int) {
        throw UnimplementedOperationException()
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

    override fun getOfflinePlayer(name: String): OfflinePlayer {
        throw UnimplementedOperationException()
    }

    override fun getOfflinePlayer(id: UUID): OfflinePlayer {
        throw UnimplementedOperationException()
    }

    override fun getOfflinePlayerIfCached(name: String): OfflinePlayer? {
        throw UnimplementedOperationException()
    }

    override fun getIPBans(): MutableSet<String> {
        throw UnimplementedOperationException()
    }

    override fun banIP(address: String) {
        throw UnimplementedOperationException()
    }

    override fun unbanIP(address: String) {
        throw UnimplementedOperationException()
    }

    override fun getBannedPlayers(): MutableSet<OfflinePlayer> {
        throw UnimplementedOperationException()
    }

    override fun getBanList(type: BanList.Type): BanList {
        throw UnimplementedOperationException()
    }

    override fun getOperators(): MutableSet<OfflinePlayer> {
        throw UnimplementedOperationException()
    }

    override fun getDefaultGameMode(): GameMode {
        throw UnimplementedOperationException()
    }

    override fun setDefaultGameMode(mode: GameMode) {
        throw UnimplementedOperationException()
    }

    override fun getConsoleSender(): ConsoleCommandSender {
        throw UnimplementedOperationException()
    }

    override fun getWorldContainer(): File {
        throw UnimplementedOperationException()
    }

    override fun getOfflinePlayers(): Array<OfflinePlayer> {
        throw UnimplementedOperationException()
    }

    override fun getMessenger(): Messenger {
        throw UnimplementedOperationException()
    }

    override fun getHelpMap(): HelpMap {
        throw UnimplementedOperationException()
    }

    override fun createInventory(owner: InventoryHolder?, type: InventoryType): Inventory {
        throw UnimplementedOperationException()
    }

    override fun createInventory(owner: InventoryHolder?, type: InventoryType, title: Component): Inventory {
        throw UnimplementedOperationException()
    }

    override fun createInventory(owner: InventoryHolder?, type: InventoryType, title: String): Inventory {
        throw UnimplementedOperationException()
    }

    override fun createInventory(owner: InventoryHolder?, size: Int): Inventory {
        throw UnimplementedOperationException()
    }

    override fun createInventory(owner: InventoryHolder?, size: Int, title: Component): Inventory {
        throw UnimplementedOperationException()
    }

    override fun createInventory(owner: InventoryHolder?, size: Int, title: String): Inventory {
        throw UnimplementedOperationException()
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

    override fun isPrimaryThread(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun motd(): Component {
        throw UnimplementedOperationException()
    }

    override fun getMotd(): String {
        throw UnimplementedOperationException()
    }

    override fun shutdownMessage(): Component? {
        throw UnimplementedOperationException()
    }

    override fun getShutdownMessage(): String? {
        throw UnimplementedOperationException()
    }

    override fun getWarningState(): Warning.WarningState {
        throw UnimplementedOperationException()
    }

    override fun getItemFactory(): ItemFactory {
        throw UnimplementedOperationException()
    }

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

    override fun createBossBar(title: String?, color: BarColor, style: BarStyle, vararg flags: BarFlag): BossBar {
        throw UnimplementedOperationException()
    }

    override fun createBossBar(
        key: NamespacedKey,
        title: String?,
        color: BarColor,
        style: BarStyle,
        vararg flags: BarFlag,
    ): KeyedBossBar {
        throw UnimplementedOperationException()
    }

    override fun getBossBars(): MutableIterator<KeyedBossBar> {
        throw UnimplementedOperationException()
    }

    override fun getBossBar(key: NamespacedKey): KeyedBossBar? {
        throw UnimplementedOperationException()
    }

    override fun removeBossBar(key: NamespacedKey): Boolean {
        throw UnimplementedOperationException()
    }

    override fun getEntity(uuid: UUID): Entity? {
        throw UnimplementedOperationException()
    }

    override fun getTPS(): DoubleArray {
        throw UnimplementedOperationException()
    }

    override fun getTickTimes(): LongArray {
        throw UnimplementedOperationException()
    }

    override fun getAverageTickTime(): Double {
        throw UnimplementedOperationException()
    }

    override fun getCommandMap(): CommandMap {
        throw UnimplementedOperationException()
    }

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

    override fun <T : Keyed?> getTag(registry: String, tag: NamespacedKey, clazz: Class<T>): Tag<T> {
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

    override fun getUnsafe(): UnsafeValues {
        throw UnimplementedOperationException()
    }

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
        throw UnimplementedOperationException()
    }

    /**
     * Checks if we are running a method on the main thread.
     *
     * @throws IllegalThreadStateException If calling this method with non-main thread.
     */
    @Throws(IllegalThreadStateException::class)
    fun assertMainThread() {
        if (mainThread != Thread.currentThread()) {
            throw IllegalThreadStateException()
        }
    }

    val entities: Set<Entity>
        get() = _entities.asUnmodifiable()

    fun registerEntity(entity: EntityMock) {
        assertMainThread()
        _entities += entity
    }
}
