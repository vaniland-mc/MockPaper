package land.vani.mockpaper.player

import com.destroystokyo.paper.ClientOption
import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent
import com.destroystokyo.paper.profile.PlayerProfile
import com.google.common.collect.ImmutableSet
import land.vani.mockpaper.ServerMock
import land.vani.mockpaper.UnimplementedOperationException
import land.vani.mockpaper.entity.LivingEntityMock
import land.vani.mockpaper.internal.toComponent
import land.vani.mockpaper.internal.toLegacyString
import land.vani.mockpaper.inventory.InventoryViewMock
import land.vani.mockpaper.inventory.PlayerInventoryMock
import land.vani.mockpaper.inventory.PlayerInventoryViewMock
import land.vani.mockpaper.sound.AudioExperience
import land.vani.mockpaper.sound.SoundReceiver
import land.vani.mockpaper.statistic.StatisticsMock
import net.kyori.adventure.sound.SoundStop
import net.kyori.adventure.text.Component
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.BaseComponent
import org.bukkit.BanList
import org.bukkit.DyeColor
import org.bukkit.Effect
import org.bukkit.GameMode
import org.bukkit.Instrument
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Note
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.SoundCategory
import org.bukkit.Statistic
import org.bukkit.WeatherType
import org.bukkit.advancement.Advancement
import org.bukkit.advancement.AdvancementProgress
import org.bukkit.attribute.Attribute
import org.bukkit.block.Block
import org.bukkit.block.Sign
import org.bukkit.block.data.BlockData
import org.bukkit.conversations.Conversation
import org.bukkit.conversations.ConversationAbandonedEvent
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.Firework
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.Villager
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockDamageEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.PlayerKickEvent
import org.bukkit.event.player.PlayerLevelChangeEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerResourcePackStatusEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.event.player.PlayerToggleFlightEvent
import org.bukkit.event.player.PlayerToggleSneakEvent
import org.bukkit.event.player.PlayerToggleSprintEvent
import org.bukkit.inventory.EntityEquipment
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.MainHand
import org.bukkit.inventory.Merchant
import org.bukkit.map.MapView
import org.bukkit.plugin.Plugin
import org.bukkit.scoreboard.Scoreboard
import org.jetbrains.annotations.VisibleForTesting
import java.net.InetSocketAddress
import java.util.Locale
import java.util.Queue
import java.util.UUID
import java.util.concurrent.LinkedTransferQueue
import kotlin.math.max
import kotlin.math.min

class PlayerMock(server: ServerMock, name: String, uuid: UUID) :
    LivingEntityMock(server, uuid),
    Player,
    SoundReceiver {
    override val heardSounds: MutableList<AudioExperience> = mutableListOf()

    private val statistics = StatisticsMock()

    private var isOnline: Boolean
    private var gameMode: GameMode = GameMode.SURVIVAL

    private var displayName: Component = Component.text(name)
    private var playerListName: Component = displayName
    private var playerListHeader: Component? = null
    private var playerListFooter: Component? = null

    private var compassTarget: Location

    private var isSneaking: Boolean = false
    private var isSprinting: Boolean = false

    private var isSleepingIgnored: Boolean = false
    private var bedSpawnLocation: Location? = null

    private var isWhiteListed: Boolean = false

    private val titles: Queue<String> = LinkedTransferQueue()
    private val subTitles: Queue<String> = LinkedTransferQueue()

    private var exp: Float = 0.0F
    private var expTotal: Int = 0
    private var expLevel: Int = 0

    private var isAllowFlight: Boolean = false

    private val hiddenPlayers = mutableMapOf<UUID, MutableSet<Plugin>>()
    private val hiddenPlayersDeprecated = mutableSetOf<UUID>()

    private var isFlying: Boolean = false

    private var foodLevel: Int = 20
    private var saturation: Float = 5.0F

    private var cursorItem: ItemStack? = null

    private val inventory: PlayerInventoryMock =
        server.createInventory(this, InventoryType.PLAYER) as PlayerInventoryMock
    private var inventoryView: InventoryViewMock = InventoryViewMock(
        this,
        null,
        inventory,
        InventoryType.CRAFTING,
    )

    init {
        isOnline = true
        this.name = name
        displayName(name.toComponent())

        // TODO add world when server has no world
        // TODO set location

        compassTarget = location
    }

    constructor(server: ServerMock, name: String) : this(
        server,
        name,
        UUID.nameUUIDFromBytes("OfflinePlayer:$name".toByteArray())
    ) {
        isOnline = false
    }

    override fun getType(): EntityType = EntityType.PLAYER

    override fun displayName(): Component = displayName

    override fun displayName(displayName: Component?) {
        this.displayName = displayName ?: Component.text(name)
    }

    override fun getDisplayName(): String = displayName().toLegacyString()

    override fun setDisplayName(name: String?) {
        displayName(name?.toComponent())
    }

    override fun playerListName(): Component = playerListName

    override fun playerListName(name: Component?) {
        playerListName = name ?: displayName()
    }

    override fun playerListHeader(): Component? = playerListHeader

    override fun playerListFooter(): Component? = playerListFooter

    override fun getPlayerListName(): String = playerListName().toLegacyString()

    override fun setPlayerListName(name: String?) {
        playerListName(name?.toComponent())
    }

    override fun getPlayerListHeader(): String? = playerListHeader()?.toLegacyString()

    override fun setPlayerListHeader(header: String?) {
        sendPlayerListHeader(header?.toComponent() ?: Component.empty())
    }

    override fun getPlayerListFooter(): String? = playerListFooter()?.toLegacyString()

    override fun setPlayerListFooter(footer: String?) {
        sendPlayerListFooter(footer?.toComponent() ?: Component.empty())
    }

    override fun setPlayerListHeaderFooter(header: String?, footer: String?) {
        playerListHeader = header?.toComponent()
        playerListFooter = footer?.toComponent()
    }

    override fun getCompassTarget(): Location = compassTarget

    override fun setCompassTarget(loc: Location) {
        compassTarget = loc
    }

    /**
     * This method simulates the [Player] respawning and also fires the [PlayerRespawnEvent] and
     * [PlayerPostRespawnEvent].
     *
     * If [Player] is not dead (when [isDead] returns false) then this throws an [UnsupportedOperationException].
     * Otherwise, the [location] will be set to [bedSpawnLocation] or [org.bukkit.World.getSpawnLocation].
     * Lastly the health of this player will be restored and set to the max health.
     */
    @VisibleForTesting
    fun respawn() {
        // TODO: Respawn anchors are not supported yet in Spigot API.
        val isBedSpawn = bedSpawnLocation != null
        val isAnchorSpawn = false
        val respawnLocation = bedSpawnLocation ?: location.world.spawnLocation

        if (!isDead) throw UnsupportedOperationException("Player is not dead")

        val event = PlayerRespawnEvent(
            this,
            respawnLocation,
            isBedSpawn,
            isAnchorSpawn,
            ImmutableSet.builder()
        )
        server.pluginManager.callEvent(event)

        health = getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.value
        location = event.respawnLocation.clone()

        val postEvent = PlayerPostRespawnEvent(this, event.respawnLocation, isBedSpawn)
        server.pluginManager.callEvent(postEvent)
    }

    /**
     * This method moves player to [moveLocation] instantly with respect to [PlayerMoveEvent].
     */
    @VisibleForTesting
    fun simulatePlayerMove(moveLocation: Location): PlayerMoveEvent {
        val event = PlayerMoveEvent(this, location, moveLocation)
        location = event.to
        server.pluginManager.callEvent(event)
        if (event.isCancelled) {
            location = event.from
        }
        return event
    }

    override fun getGameMode(): GameMode = gameMode

    override fun setGameMode(mode: GameMode) {
        gameMode = mode
    }

    override fun isOnline(): Boolean = isOnline

    override fun isBanned(): Boolean = server.getBanList(BanList.Type.NAME).isBanned(name)

    override fun isWhitelisted(): Boolean = isWhiteListed

    override fun setWhitelisted(value: Boolean) {
        isWhiteListed = value
    }

    override fun getPlayer(): Player? = this.takeIf { isOnline }

    override fun getFirstPlayed(): Long {
        throw UnimplementedOperationException()
    }

    override fun getLastPlayed(): Long {
        throw UnimplementedOperationException()
    }

    override fun hasPlayedBefore(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun getLastLogin(): Long {
        throw UnimplementedOperationException()
    }

    override fun getLastSeen(): Long {
        throw UnimplementedOperationException()
    }

    override fun attack(target: Entity) {
        throw UnimplementedOperationException()
    }

    override fun getEquipment(): EntityEquipment {
        // TODO: implement inventory
        throw UnimplementedOperationException()
    }

    override fun getInventory(): PlayerInventoryMock = inventory

    override fun getEnderChest(): Inventory {
        // TODO: implement inventory
        throw UnimplementedOperationException()
    }

    override fun getMainHand(): MainHand {
        // TODO: implement inventory
        throw UnimplementedOperationException()
    }

    override fun setWindowProperty(prop: InventoryView.Property, value: Int): Boolean {
        throw UnimplementedOperationException()
    }

    override fun getOpenInventory(): InventoryView = inventoryView

    override fun openInventory(inventory: Inventory): InventoryView {
        closeInventory()
        return PlayerInventoryViewMock(this, inventory).also {
            inventoryView = it
        }
    }

    override fun openWorkbench(location: Location?, force: Boolean): InventoryView? {
        // TODO: implement inventory
        throw UnimplementedOperationException()
    }

    override fun openEnchanting(location: Location?, force: Boolean): InventoryView? {
        // TODO: implement inventory
        throw UnimplementedOperationException()
    }

    override fun openInventory(inventory: InventoryView) {
        // TODO: implement inventory
        throw UnimplementedOperationException()
    }

    override fun openMerchant(merchant: Merchant, force: Boolean): InventoryView? {
        // TODO: implement inventory
        throw UnimplementedOperationException()
    }

    override fun openMerchant(trader: Villager, force: Boolean): InventoryView? {
        // TODO: implement inventory
        throw UnimplementedOperationException()
    }

    override fun openAnvil(location: Location?, force: Boolean): InventoryView? {
        // TODO: implement inventory
        throw UnimplementedOperationException()
    }

    override fun openCartographyTable(location: Location?, force: Boolean): InventoryView? {
        // TODO: implement inventory
        throw UnimplementedOperationException()
    }

    override fun openGrindstone(location: Location?, force: Boolean): InventoryView? {
        // TODO: implement inventory
        throw UnimplementedOperationException()
    }

    override fun openLoom(location: Location?, force: Boolean): InventoryView? {
        // TODO: implement inventory
        throw UnimplementedOperationException()
    }

    override fun openSmithingTable(location: Location?, force: Boolean): InventoryView? {
        // TODO: implement inventory
        throw UnimplementedOperationException()
    }

    override fun openStonecutter(location: Location?, force: Boolean): InventoryView? {
        // TODO: implement inventory
        throw UnimplementedOperationException()
    }

    override fun closeInventory() {
        closeInventory(InventoryCloseEvent.Reason.PLUGIN)
    }

    override fun closeInventory(reason: InventoryCloseEvent.Reason) {
        if (inventoryView is PlayerInventoryViewMock) {
            val event = InventoryCloseEvent(inventoryView)
            server.pluginManager.callEvent(event)
        }

        cursorItem = null
        inventoryView = InventoryViewMock(this, null, inventory, InventoryType.CRAFTING)
    }

    override fun getItemInHand(): ItemStack = inventory.itemInMainHand

    override fun setItemInHand(item: ItemStack?) {
        inventory.setItemInMainHand(item)
    }

    override fun getItemOnCursor(): ItemStack = cursorItem?.clone() ?: ItemStack(Material.AIR, 0)

    override fun setItemOnCursor(item: ItemStack?) {
        cursorItem = item?.clone()
    }

    override fun hasCooldown(material: Material): Boolean {
        throw UnimplementedOperationException()
    }

    override fun getCooldown(material: Material): Int {
        throw UnimplementedOperationException()
    }

    override fun setCooldown(material: Material, ticks: Int) {
        throw UnimplementedOperationException()
    }

    override fun isSleeping(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun getSleepTicks(): Int {
        throw UnimplementedOperationException()
    }

    override fun isDeeplySleeping(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun getPotentialBedLocation(): Location? {
        throw UnimplementedOperationException()
    }

    override fun sleep(location: Location, force: Boolean): Boolean {
        throw UnimplementedOperationException()
    }

    override fun wakeup(setSpawnLocation: Boolean) {
        throw UnimplementedOperationException()
    }

    override fun getBedLocation(): Location {
        throw UnimplementedOperationException()
    }

    override fun incrementStatistic(statistic: Statistic) {
        statistics.incrementStatistic(statistic, 1)
    }

    override fun decrementStatistic(statistic: Statistic) {
        statistics.decrementStatistic(statistic, 1)
    }

    override fun incrementStatistic(statistic: Statistic, amount: Int) {
        statistics.incrementStatistic(statistic, amount)
    }

    override fun decrementStatistic(statistic: Statistic, amount: Int) {
        statistics.decrementStatistic(statistic, amount)
    }

    override fun incrementStatistic(statistic: Statistic, entityType: EntityType) {
        statistics.incrementStatistic(statistic, entityType, 1)
    }

    override fun decrementStatistic(statistic: Statistic, entityType: EntityType) {
        statistics.decrementStatistic(statistic, entityType, 1)
    }

    override fun incrementStatistic(statistic: Statistic, entityType: EntityType, amount: Int) {
        statistics.incrementStatistic(statistic, entityType, amount)
    }

    override fun decrementStatistic(statistic: Statistic, entityType: EntityType, amount: Int) {
        statistics.decrementStatistic(statistic, entityType, amount)
    }

    override fun incrementStatistic(statistic: Statistic, material: Material) {
        statistics.incrementStatistic(statistic, material, 1)
    }

    override fun decrementStatistic(statistic: Statistic, material: Material) {
        statistics.decrementStatistic(statistic, material, 1)
    }

    override fun incrementStatistic(statistic: Statistic, material: Material, amount: Int) {
        statistics.incrementStatistic(statistic, material, amount)
    }

    override fun decrementStatistic(statistic: Statistic, material: Material, amount: Int) {
        statistics.decrementStatistic(statistic, material, amount)
    }

    override fun getStatistic(statistic: Statistic): Int = statistics.getStatistic(statistic)

    override fun getStatistic(statistic: Statistic, entityType: EntityType): Int =
        statistics.getStatistic(statistic, entityType)

    override fun getStatistic(statistic: Statistic, material: Material): Int =
        statistics.getStatistic(statistic, material)

    override fun setStatistic(statistic: Statistic, newValue: Int) {
        statistics.setStatistic(statistic, newValue)
    }

    override fun setStatistic(statistic: Statistic, entityType: EntityType, newValue: Int) {
        statistics.setStatistic(statistic, entityType, newValue)
    }

    override fun setStatistic(statistic: Statistic, material: Material, newValue: Int) {
        statistics.setStatistic(statistic, material, newValue)
    }

    override fun getAddress(): InetSocketAddress {
        throw UnimplementedOperationException()
    }

    override fun kickPlayer(message: String?) {
        kick(message?.toComponent())
    }

    override fun kick(message: Component?) {
        throw UnimplementedOperationException()
    }

    override fun kick(message: Component?, cause: PlayerKickEvent.Cause) {
        throw UnimplementedOperationException()
    }

    override fun chat(msg: String) {
        messages.offer(msg)
    }

    override fun performCommand(command: String): Boolean = server.dispatchCommand(this, command)

    override fun isSneaking(): Boolean = isSneaking

    override fun setSneaking(sneak: Boolean) {
        isSneaking = sneak
    }

    /**
     * Simulates the player sneaking with [PlayerToggleSneakEvent].
     */
    @VisibleForTesting
    fun simulateSneak(sneak: Boolean): PlayerToggleSneakEvent {
        val event = PlayerToggleSneakEvent(this, sneak)
        server.pluginManager.callEvent(event)
        if (!event.isCancelled) {
            isSneaking = event.isSneaking
        }
        return event
    }

    override fun isSprinting(): Boolean = isSprinting

    override fun setSprinting(sprinting: Boolean) {
        isSprinting = sprinting
    }

    /**
     * Simulates the player sprinting with [PlayerToggleSprintEvent].
     */
    @VisibleForTesting
    fun simulateSprinting(sprinting: Boolean): PlayerToggleSprintEvent {
        val event = PlayerToggleSprintEvent(this, sprinting)
        server.pluginManager.callEvent(event)
        if (!event.isCancelled) {
            isSprinting = event.isSprinting
        }
        return event
    }

    override fun loadData() {
        throw UnimplementedOperationException()
    }

    override fun saveData() {
        throw UnimplementedOperationException()
    }

    override fun isSleepingIgnored(): Boolean = isSleepingIgnored

    override fun setSleepingIgnored(isSleeping: Boolean) {
        isSleepingIgnored = isSleeping
    }

    override fun getBedSpawnLocation(): Location? = bedSpawnLocation

    override fun setBedSpawnLocation(location: Location?) {
        setBedSpawnLocation(location, false)
    }

    override fun setBedSpawnLocation(location: Location?, force: Boolean) {
        if (force || location == null || location.block.type.name.endsWith("_BED")) {
            bedSpawnLocation = location
        }
    }

    override fun playNote(loc: Location, instrument: Instrument, note: Note) {
        throw UnimplementedOperationException()
    }

    override fun playNote(loc: Location, instrument: Byte, note: Byte) {
        throw UnimplementedOperationException()
    }

    override fun playSound(location: Location, sound: Sound, volume: Float, pitch: Float) {
        playSound(location, sound, SoundCategory.MASTER, volume, pitch)
    }

    override fun playSound(location: Location, sound: String, volume: Float, pitch: Float) {
        playSound(location, sound, SoundCategory.MASTER, volume, pitch)
    }

    override fun playSound(location: Location, sound: Sound, category: SoundCategory, volume: Float, pitch: Float) {
        heardSounds += AudioExperience.BukkitAudioExperience(sound, category, location, volume, pitch)
    }

    override fun playSound(location: Location, sound: String, category: SoundCategory, volume: Float, pitch: Float) {
        heardSounds += AudioExperience.BukkitAudioExperience(sound, category, location, volume, pitch)
    }

    override fun playSound(sound: net.kyori.adventure.sound.Sound) {
        heardSounds += AudioExperience.KyoriAudioExperience(sound, location.x, location.y, location.z)
    }

    override fun playSound(sound: net.kyori.adventure.sound.Sound, emitter: net.kyori.adventure.sound.Sound.Emitter) {
        heardSounds += AudioExperience.KyoriEmitterAudioExperience(sound, emitter)
    }

    override fun playSound(sound: net.kyori.adventure.sound.Sound, x: Double, y: Double, z: Double) {
        heardSounds += AudioExperience.KyoriAudioExperience(sound, x, y, z)
    }

    override fun stopSound(sound: Sound) {
        stopSound(sound, SoundCategory.MASTER)
    }

    override fun stopSound(sound: String) {
        stopSound(sound, SoundCategory.MASTER)
    }

    override fun stopSound(sound: Sound, category: SoundCategory?) {
        // We just pretend the Sound has stopped
    }

    override fun stopSound(sound: String, category: SoundCategory?) {
        // We just pretend the Sound has stopped
    }

    override fun stopSound(sound: net.kyori.adventure.sound.Sound) {
        // We just pretend the Sound has stopped
    }

    override fun stopSound(stop: SoundStop) {
        // We just pretend the Sound has stopped
    }

    override fun stopAllSounds() {
        // We just pretend the Sound has stopped
    }

    override fun playEffect(loc: Location, effect: Effect, data: Int) {
        throw UnimplementedOperationException()
    }

    override fun <T : Any?> playEffect(loc: Location, effect: Effect, data: T?) {
        throw UnimplementedOperationException()
    }

    override fun breakBlock(block: Block): Boolean {
        throw UnimplementedOperationException()
    }

    override fun sendBlockChange(loc: Location, material: Material, data: Byte) {
        throw UnimplementedOperationException()
    }

    override fun sendBlockChange(loc: Location, block: BlockData) {
        throw UnimplementedOperationException()
    }

    override fun sendBlockDamage(loc: Location, progress: Float) {
        throw UnimplementedOperationException()
    }

    private fun simulateBlockDamagePure(block: Block): BlockDamageEvent =
        BlockDamageEvent(this, block, inventory.itemInMainHand, false).also {
            server.pluginManager.callEvent(it)
        }

    /**
     * Simulates the player damaging a [block].
     *
     * Note that this method does not anything unless the player is in survival mode.
     * if [BlockDamageEvent.instaBreak] is set to `true` by an event handler, a [BlockBreakEvent] is immediately fired.
     * The result will then still be whether the [BlockDamageEvent] was cancelled or not,
     * not the later [BlockBreakEvent].
     */
    @VisibleForTesting
    fun simulateBlocKDamage(block: Block): BlockDamageEvent? {
        if (gameMode != GameMode.SURVIVAL) return null

        val event = simulateBlockDamagePure(block)
        if (!event.instaBreak) return event

        val blockBreakEvent = BlockBreakEvent(block, this)
        server.pluginManager.callEvent(blockBreakEvent)
        if (!blockBreakEvent.isCancelled) block.type = Material.AIR

        return event
    }

    /**
     * Simulates the player breaking a [block].
     *
     * This method will not break the block if the player is in adventure or spectator mode.
     * If the player is in survival mode, the player will first damage the block.
     */
    @VisibleForTesting
    fun simulateBlockBreak(block: Block): BlockBreakEvent? {
        if (gameMode == GameMode.SPECTATOR || gameMode == GameMode.ADVENTURE) return null
        if (gameMode == GameMode.SURVIVAL && simulateBlockDamagePure(block).isCancelled) return null

        val event = BlockBreakEvent(block, this)
        server.pluginManager.callEvent(event)
        if (!event.isCancelled) {
            block.type = Material.AIR
        }
        return event
    }

    /**
     * Simulates the player placing a block with [material] to [location].
     *
     * This method will not place the block if the player is in adventure or spectator mode.
     */
    @VisibleForTesting
    fun simulateBlockPlace(material: Material, location: Location): BlockPlaceEvent? {
        if (gameMode == GameMode.ADVENTURE || gameMode == GameMode.SPECTATOR) return null

        val block = location.block
        val event = BlockPlaceEvent(
            block,
            block.state,
            block,
            ItemStack(Material.AIR),
            this,
            true,
            EquipmentSlot.HAND,
        )
        if (!event.isCancelled) {
            block.type = material
        }
        return event
    }

    override fun sendEquipmentChange(entity: LivingEntity, slot: EquipmentSlot, item: ItemStack) {
        throw UnimplementedOperationException()
    }

    override fun sendSignChange(
        loc: Location,
        lines: List<Component>?,
        dyeColor: DyeColor,
        hasGlowingText: Boolean,
    ) {
        throw UnimplementedOperationException()
    }

    override fun sendSignChange(loc: Location, lines: Array<out String?>?) {
        throw UnimplementedOperationException()
    }

    override fun sendSignChange(loc: Location, lines: Array<out String?>?, dyeColor: DyeColor) {
        throw UnimplementedOperationException()
    }

    override fun sendSignChange(
        loc: Location,
        lines: Array<out String?>?,
        dyeColor: DyeColor,
        hasGlowingText: Boolean,
    ) {
        throw UnimplementedOperationException()
    }

    override fun sendMap(map: MapView) {
        throw UnimplementedOperationException()
    }

    override fun sendPluginMessage(source: Plugin, channel: String, message: ByteArray) {
        throw UnimplementedOperationException()
    }

    override fun sendActionBar(message: String) {
        throw UnimplementedOperationException()
    }

    override fun sendActionBar(message: Component) {
        throw UnimplementedOperationException()
    }

    override fun sendActionBar(vararg message: BaseComponent) {
        throw UnimplementedOperationException()
    }

    override fun sendActionBar(alternateChar: Char, message: String) {
        throw UnimplementedOperationException()
    }

    override fun setPlayerListHeaderFooter(header: BaseComponent?, footer: BaseComponent?) {
        throw UnimplementedOperationException()
    }

    override fun setPlayerListHeaderFooter(header: Array<out BaseComponent?>?, footer: Array<out BaseComponent?>?) {
        throw UnimplementedOperationException()
    }

    override fun setTitleTimes(fadeInTicks: Int, stayTicks: Int, fadeOutTicks: Int) {
        throw UnimplementedOperationException()
    }

    override fun setSubtitle(subtitle: BaseComponent?) {
        subTitles.offer(subtitle?.toLegacyText())
    }

    override fun setSubtitle(subtitle: Array<out BaseComponent?>?) {
        subtitle?.forEach {
            @Suppress("DEPRECATION")
            setSubtitle(it)
        }
    }

    override fun showTitle(title: BaseComponent?) {
        titles.offer(title?.toLegacyText())
    }

    override fun showTitle(title: Array<out BaseComponent?>?) {
        title?.forEach { titles.offer(it?.toLegacyText()) }
    }

    @Suppress("DEPRECATION")
    override fun showTitle(
        title: BaseComponent?,
        subtitle: BaseComponent?,
        fadeInTicks: Int,
        stayTicks: Int,
        fadeOutTicks: Int,
    ) {
        showTitle(title)
        setSubtitle(subtitle)
    }

    @Suppress("DEPRECATION")
    override fun showTitle(
        title: Array<out BaseComponent?>?,
        subtitle: Array<out BaseComponent?>?,
        fadeInTicks: Int,
        stayTicks: Int,
        fadeOutTicks: Int,
    ) {
        showTitle(title)
        setSubtitle(subtitle)
    }

    @Suppress("DEPRECATION")
    override fun sendTitle(title: com.destroystokyo.paper.Title) {
        showTitle(title.title)
        setSubtitle(title.subtitle)
    }

    @Suppress("DEPRECATION")
    override fun updateTitle(title: com.destroystokyo.paper.Title) {
        showTitle(title.title)
        setSubtitle(title.subtitle)
    }

    override fun hideTitle() {
        throw UnimplementedOperationException()
    }

    override fun updateInventory() {
        throw UnimplementedOperationException()
    }

    override fun getPlayerTime(): Long {
        throw UnimplementedOperationException()
    }

    override fun setPlayerTime(time: Long, relative: Boolean) {
        throw UnimplementedOperationException()
    }

    override fun getPlayerTimeOffset(): Long {
        throw UnimplementedOperationException()
    }

    override fun isPlayerTimeRelative(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun resetPlayerTime() {
        throw UnimplementedOperationException()
    }

    override fun setPlayerWeather(type: WeatherType) {
        throw UnimplementedOperationException()
    }

    override fun getPlayerWeather(): WeatherType? {
        throw UnimplementedOperationException()
    }

    override fun resetPlayerWeather() {
        throw UnimplementedOperationException()
    }

    override fun giveExp(amount: Int, applyMending: Boolean) {
        exp += amount.toFloat() / expToLevel.toFloat()
        totalExperience = expTotal + amount
        while (exp < 0.0) {
            val total = exp * expToLevel

            val shouldContinue = expLevel > 0
            giveExpLevels(-1)
            if (shouldContinue) {
                exp = 1.0F + (total / expToLevel)
            }
        }

        while (exp >= 1.0) {
            exp = (exp - 1.0F) * expToLevel
            giveExpLevels(1)
            exp /= expToLevel
        }
    }

    override fun applyMending(amount: Int): Int {
        throw UnimplementedOperationException()
    }

    override fun giveExpLevels(amount: Int) {
        val oldLevel = expLevel
        expLevel += amount
        if (expLevel < 0) {
            expLevel = 0
            exp = 0.0F
        }
        if (oldLevel != expLevel) {
            val event = PlayerLevelChangeEvent(this, oldLevel, expLevel)
            server.pluginManager.callEvent(event)
        }
    }

    override fun getExp(): Float = exp

    override fun setExp(exp: Float) {
        require(exp in 0.0..1.0) { "Experience progress must be between 0.0..1.0" }
        this.exp = exp
    }

    override fun getLevel(): Int = expLevel

    override fun setLevel(level: Int) {
        expLevel = level
    }

    override fun getTotalExperience(): Int = expTotal

    override fun setTotalExperience(exp: Int) {
        expTotal = max(0, exp)
    }

    override fun sendExperienceChange(progress: Float) {
        throw UnimplementedOperationException()
    }

    override fun sendExperienceChange(progress: Float, level: Int) {
        throw UnimplementedOperationException()
    }

    override fun getAllowFlight(): Boolean = isAllowFlight

    override fun setAllowFlight(flight: Boolean) {
        isAllowFlight = flight
    }

    override fun hidePlayer(player: Player) {
        hiddenPlayersDeprecated += player.uniqueId
    }

    override fun hidePlayer(plugin: Plugin, player: Player) {
        hiddenPlayers.getOrPut(player.uniqueId) { mutableSetOf() }
            .add(plugin)
    }

    override fun showPlayer(player: Player) {
        hiddenPlayersDeprecated -= player.uniqueId
    }

    override fun showPlayer(plugin: Plugin, player: Player) {
        hiddenPlayers[player.uniqueId]?.remove(plugin)
        if (hiddenPlayers[player.uniqueId]?.isEmpty() == true) {
            hiddenPlayers -= player.uniqueId
        }
    }

    override fun canSee(player: Player): Boolean =
        player.uniqueId !in hiddenPlayers &&
            player.uniqueId !in hiddenPlayersDeprecated

    override fun hideEntity(plugin: Plugin, entity: Entity) {
        throw UnimplementedOperationException()
    }

    override fun showEntity(plugin: Plugin, entity: Entity) {
        throw UnimplementedOperationException()
    }

    override fun canSee(entity: Entity): Boolean {
        throw UnimplementedOperationException()
    }

    override fun isFlying(): Boolean = isFlying

    override fun setFlying(value: Boolean) {
        isFlying = value
    }

    /**
     * Simulates the player flight to [fly].
     */
    @VisibleForTesting
    fun simulateToggleFlight(fly: Boolean): PlayerToggleFlightEvent {
        val event = PlayerToggleFlightEvent(this, fly)
        server.pluginManager.callEvent(event)
        if (!event.isCancelled) {
            isFlying = event.isFlying
        }
        return event
    }

    override fun getFlySpeed(): Float {
        throw UnimplementedOperationException()
    }

    override fun setFlySpeed(value: Float) {
        throw UnimplementedOperationException()
    }

    override fun getWalkSpeed(): Float {
        throw UnimplementedOperationException()
    }

    override fun setWalkSpeed(value: Float) {
        throw UnimplementedOperationException()
    }

    override fun setTexturePack(url: String) {
        throw UnimplementedOperationException()
    }

    override fun setResourcePack(url: String, hash: ByteArray?) {
        throw UnimplementedOperationException()
    }

    override fun setResourcePack(url: String, hash: String) {
        throw UnimplementedOperationException()
    }

    override fun setResourcePack(url: String, hash: ByteArray?, prompt: String?) {
        throw UnimplementedOperationException()
    }

    override fun setResourcePack(url: String, hash: ByteArray?, force: Boolean) {
        throw UnimplementedOperationException()
    }

    override fun setResourcePack(url: String, hash: ByteArray?, prompt: Component?, force: Boolean) {
        throw UnimplementedOperationException()
    }

    override fun setResourcePack(url: String, hash: ByteArray?, prompt: String?, force: Boolean) {
        throw UnimplementedOperationException()
    }

    override fun setResourcePack(url: String, hash: String, required: Boolean) {
        throw UnimplementedOperationException()
    }

    override fun setResourcePack(url: String, hash: String, required: Boolean, resourcePackPrompt: Component?) {
        throw UnimplementedOperationException()
    }

    override fun setResourcePack(url: String) {
        throw UnimplementedOperationException()
    }

    override fun getScoreboard(): Scoreboard {
        throw UnimplementedOperationException()
    }

    override fun setScoreboard(scoreboard: Scoreboard) {
        throw UnimplementedOperationException()
    }

    override fun isHealthScaled(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun setHealthScaled(scale: Boolean) {
        throw UnimplementedOperationException()
    }

    override fun getHealthScale(): Double {
        throw UnimplementedOperationException()
    }

    override fun setHealthScale(scale: Double) {
        throw UnimplementedOperationException()
    }

    override fun sendHealthUpdate() {
        throw UnimplementedOperationException()
    }

    override fun sendHealthUpdate(health: Double, foodLevel: Int, saturationLevel: Float) {
        throw UnimplementedOperationException()
    }

    override fun getSpectatorTarget(): Entity? {
        throw UnimplementedOperationException()
    }

    override fun setSpectatorTarget(entity: Entity?) {
        throw UnimplementedOperationException()
    }

    override fun sendTitle(title: String?, subtitle: String?) {
        titles.offer(title)
        subTitles.offer(subtitle)
    }

    override fun sendTitle(title: String?, subtitle: String?, fadeIn: Int, stay: Int, fadeOut: Int) {
        @Suppress("DEPRECATION")
        sendTitle(title, subtitle)
    }

    override fun resetTitle() {
        throw UnimplementedOperationException()
    }

    /**
     * Returns the next title that was sent to the player.
     */
    @VisibleForTesting
    fun nextTitle(): String = titles.poll()

    /**
     * Returns the next subtitle that was sent to the player.
     */
    @VisibleForTesting
    fun nextSubTitle(): String = subTitles.poll()

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
    ) {
        throw UnimplementedOperationException()
    }

    override fun getAdvancementProgress(advancement: Advancement): AdvancementProgress {
        throw UnimplementedOperationException()
    }

    override fun getClientViewDistance(): Int {
        throw UnimplementedOperationException()
    }

    override fun locale(): Locale {
        throw UnimplementedOperationException()
    }

    override fun getPing(): Int {
        throw UnimplementedOperationException()
    }

    override fun getLocale(): String {
        throw UnimplementedOperationException()
    }

    override fun getAffectsSpawning(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun setAffectsSpawning(affects: Boolean) {
        throw UnimplementedOperationException()
    }

    override fun getViewDistance(): Int {
        throw UnimplementedOperationException()
    }

    override fun setViewDistance(viewDistance: Int) {
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

    override fun updateCommands() {
        throw UnimplementedOperationException()
    }

    override fun openBook(book: ItemStack) {
        throw UnimplementedOperationException()
    }

    override fun getResourcePackHash(): String? {
        throw UnimplementedOperationException()
    }

    override fun hasResourcePack(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun getResourcePackStatus(): PlayerResourcePackStatusEvent.Status? {
        throw UnimplementedOperationException()
    }

    override fun getPlayerProfile(): PlayerProfile {
        throw UnimplementedOperationException()
    }

    override fun setPlayerProfile(profile: PlayerProfile) {
        throw UnimplementedOperationException()
    }

    override fun getCooledAttackStrength(adjustTicks: Float): Float {
        throw UnimplementedOperationException()
    }

    override fun resetCooldown() {
        throw UnimplementedOperationException()
    }

    override fun getCooldownPeriod(): Float {
        throw UnimplementedOperationException()
    }

    override fun <T : Any?> getClientOption(option: ClientOption<T>): T {
        throw UnimplementedOperationException()
    }

    override fun boostElytra(firework: ItemStack): Firework? {
        throw UnimplementedOperationException()
    }

    override fun sendOpLevel(level: Byte) {
        throw UnimplementedOperationException()
    }

    override fun registerAttribute(attribute: Attribute) {
        throw UnimplementedOperationException()
    }

    override fun isConversing(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun acceptConversationInput(input: String) {
        throw UnimplementedOperationException()
    }

    override fun beginConversation(conversation: Conversation): Boolean {
        throw UnimplementedOperationException()
    }

    override fun abandonConversation(conversation: Conversation) {
        throw UnimplementedOperationException()
    }

    override fun abandonConversation(conversation: Conversation, details: ConversationAbandonedEvent) {
        throw UnimplementedOperationException()
    }

    override fun sendRawMessage(message: String) {
        throw UnimplementedOperationException()
    }

    override fun sendRawMessage(sender: UUID?, message: String) {
        throw UnimplementedOperationException()
    }

    override fun isBlocking(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun isHandRaised(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun getItemInUse(): ItemStack? {
        throw UnimplementedOperationException()
    }

    override fun getExpToLevel(): Int {
        if (expLevel >= 31)
            return (9 * expLevel) - 158
        if (expLevel >= 16)
            return (5 * expLevel) - 38
        return (2 * expLevel) + 7
    }

    override fun releaseLeftShoulderEntity(): Entity? {
        throw UnimplementedOperationException()
    }

    override fun releaseRightShoulderEntity(): Entity? {
        throw UnimplementedOperationException()
    }

    override fun getAttackCooldown(): Float {
        throw UnimplementedOperationException()
    }

    override fun discoverRecipe(recipe: NamespacedKey): Boolean {
        throw UnimplementedOperationException()
    }

    override fun discoverRecipes(recipes: Collection<NamespacedKey>): Int {
        throw UnimplementedOperationException()
    }

    override fun getDiscoveredRecipes(): Set<NamespacedKey> {
        throw UnimplementedOperationException()
    }

    override fun hasDiscoveredRecipe(recipe: NamespacedKey): Boolean {
        throw UnimplementedOperationException()
    }

    override fun undiscoverRecipe(recipe: NamespacedKey): Boolean {
        throw UnimplementedOperationException()
    }

    override fun undiscoverRecipes(recipes: Collection<NamespacedKey>): Int {
        throw UnimplementedOperationException()
    }

    override fun getShoulderEntityLeft(): Entity? {
        throw UnimplementedOperationException()
    }

    override fun getShoulderEntityRight(): Entity? {
        throw UnimplementedOperationException()
    }

    override fun setShoulderEntityLeft(entity: Entity?) {
        throw UnimplementedOperationException()
    }

    override fun setShoulderEntityRight(entity: Entity?) {
        throw UnimplementedOperationException()
    }

    override fun openSign(sign: Sign) {
        throw UnimplementedOperationException()
    }

    override fun showDemoScreen() {
        throw UnimplementedOperationException()
    }

    override fun isAllowingServerListings(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun dropItem(dropAll: Boolean): Boolean {
        throw UnimplementedOperationException()
    }

    override fun getExhaustion(): Float {
        throw UnimplementedOperationException()
    }

    override fun setExhaustion(value: Float) {
        throw UnimplementedOperationException()
    }

    override fun getSaturation(): Float = saturation

    override fun setSaturation(value: Float) {
        saturation = min(foodLevel.toFloat(), value)
    }

    override fun getFoodLevel(): Int = foodLevel

    override fun setFoodLevel(value: Int) {
        foodLevel = value
    }

    override fun sendMessage(vararg components: BaseComponent) {
        components.forEach {
            @Suppress("DEPRECATION")
            sendMessage(it)
        }
    }

    override fun sendMessage(component: BaseComponent) {
        messages.offer(component.toLegacyText())
    }

    override fun getSaturatedRegenRate(): Int {
        throw UnimplementedOperationException()
    }

    override fun setSaturatedRegenRate(ticks: Int) {
        throw UnimplementedOperationException()
    }

    override fun getUnsaturatedRegenRate(): Int {
        throw UnimplementedOperationException()
    }

    override fun setUnsaturatedRegenRate(ticks: Int) {
        throw UnimplementedOperationException()
    }

    override fun getStarvationRate(): Int {
        throw UnimplementedOperationException()
    }

    override fun setStarvationRate(ticks: Int) {
        throw UnimplementedOperationException()
    }

    override fun getListeningPluginChannels(): MutableSet<String> {
        throw UnimplementedOperationException()
    }

    override fun getProtocolVersion(): Int {
        throw UnimplementedOperationException()
    }

    override fun getVirtualHost(): InetSocketAddress? {
        throw UnimplementedOperationException()
    }

    override fun getClientBrandName(): String? {
        throw UnimplementedOperationException()
    }

    override fun serialize(): Map<String, Any> {
        throw UnimplementedOperationException()
    }

    override fun spigot(): Player.Spigot = PlayerSpigotMock()

    inner class PlayerSpigotMock : Player.Spigot() {
        override fun sendMessage(vararg components: BaseComponent) {
            components.forEach {
                @Suppress("DEPRECATION")
                sendMessage(it)
            }
        }

        override fun sendMessage(component: BaseComponent) {
            @Suppress("DEPRECATION")
            sendMessage(ChatMessageType.CHAT, component)
        }

        override fun sendMessage(position: ChatMessageType, component: BaseComponent) {
            this@PlayerMock.sendMessage(component.toLegacyText())
        }
    }
}
