package land.vani.mockpaper.entity

import land.vani.mockpaper.NameableHolder
import land.vani.mockpaper.ServerMock
import land.vani.mockpaper.UnimplementedOperationException
import land.vani.mockpaper.command.MessageTarget
import land.vani.mockpaper.internal.toComponent
import land.vani.mockpaper.internal.toLegacyString
import land.vani.mockpaper.metadata.MetadataHolder
import land.vani.mockpaper.persistence.PersistentDataContainerMock
import net.kyori.adventure.text.Component
import net.md_5.bungee.api.chat.BaseComponent
import org.bukkit.Bukkit
import org.bukkit.EntityEffect
import org.bukkit.Location
import org.bukkit.Nameable
import org.bukkit.Server
import org.bukkit.World
import org.bukkit.block.BlockFace
import org.bukkit.block.PistonMoveReaction
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.entity.Pose
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause
import org.bukkit.metadata.Metadatable
import org.bukkit.permissions.Permission
import org.bukkit.permissions.PermissionAttachment
import org.bukkit.permissions.PermissionAttachmentInfo
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.plugin.Plugin
import org.bukkit.util.BoundingBox
import org.bukkit.util.Vector
import java.util.Queue
import java.util.UUID
import java.util.concurrent.LinkedTransferQueue
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

abstract class EntityMock(
    private val server: ServerMock,
    private val uuid: UUID,
) :
    Entity.Spigot(),
    Entity,
    Metadatable by MetadataHolder(),
    Nameable by NameableHolder(),
    MessageTarget {
    private var _location = Bukkit.getWorlds().firstOrNull()?.spawnLocation
        ?: Location(null, 0.0, 0.0, 0.0)

    private var isTeleported: Boolean = false
    private var teleportCause: TeleportCause? = null

    private val persistentDataContainer = PersistentDataContainerMock()

    private var _isOperator: Boolean = false

    private var name: Component = Component.text("entity")

    private val messages: Queue<String> = LinkedTransferQueue()

    private val permissionAttachments = mutableSetOf<PermissionAttachment>()

    private var _velocity: Vector = Vector(0, 0, 0)

    private var _fireTicks = -20
    private var _maxFireTicks = 20

    private var _fallDistance = 0.0f

    private var _isCustomNameVisible = true
    private var _isGlowing = false
    private var _isInvulnerable = false
    private var _isSilent = false

    override fun hashCode(): Int = uuid.hashCode()

    override fun equals(other: Any?): Boolean {
        if (other is EntityMock) {
            return uuid == other.uuid
        }
        return false
    }

    fun assertLocation(expectedLocation: Location, maximumDistance: Double) {
        val distance = location.distance(expectedLocation)
        assertEquals(expectedLocation.world, location.world)
        assertTrue(
            distance <= maximumDistance,
            "Distance was <%.3f> but should be less than or equals to <%.3f>".format(distance, maximumDistance)
        )
    }

    fun assertTeleported(expectedLocation: Location, maximumDistance: Double) {
        assertTrue(isTeleported, "Entity was expected to teleport, but actually it did not teleport.")
        assertLocation(expectedLocation, maximumDistance)
        isTeleported = false
    }

    fun assertNotTeleported() {
        assertFalse(isTeleported, "Entity was expected not to teleport, but actually it teleported.")
        isTeleported = false
    }

    fun hasTeleported() = isTeleported

    fun clearTeleported() {
        isTeleported = false
    }

    fun getTeleportCause(): TeleportCause? = teleportCause

    override fun getUniqueId(): UUID = uuid

    override fun getLocation(): Location = _location.clone()

    override fun getLocation(loc: Location?): Location? = loc?.apply {
        world = _location.world
        direction = _location.direction
        x = _location.x
        y = _location.y
        z = _location.z
        pitch = _location.pitch
        yaw = _location.yaw
    }

    fun setLocation(location: Location) {
        _location = location
    }

    override fun getWorld(): World = _location.world

    override fun getPersistentDataContainer(): PersistentDataContainer = persistentDataContainer

    override fun teleport(location: Location): Boolean = teleport(location, TeleportCause.PLUGIN)

    override fun teleport(location: Location, cause: TeleportCause): Boolean {
        _location = location
        isTeleported = true
        teleportCause = cause
        return true
    }

    override fun teleport(destination: Entity): Boolean = teleport(destination, TeleportCause.PLUGIN)

    override fun teleport(destination: Entity, cause: TeleportCause): Boolean =
        teleport(destination.location, cause)

    override fun isOp(): Boolean = _isOperator

    override fun setOp(value: Boolean) {
        _isOperator = value
    }

    override fun name(): Component = name

    override fun getName(): String = name.toLegacyString()

    fun setName(name: String) {
        this.name = name.toComponent()
    }

    override fun sendMessage(message: String) {
        sendMessage(null, message)
    }

    override fun sendMessage(vararg messages: String) {
        sendMessage(null, *messages)
    }

    override fun sendMessage(sender: UUID?, message: String) {
        messages += message
    }

    override fun sendMessage(sender: UUID?, vararg messages: String) {
        messages.forEach {
            sendMessage(it)
        }
    }

    override fun sendMessage(component: BaseComponent) {
        messages += component.toLegacyText()
    }

    @Suppress("DEPRECATION")
    override fun sendMessage(vararg components: BaseComponent) {
        components.forEach {
            sendMessage(it)
        }
    }

    override fun nextMessage(): String? = messages.poll()

    override fun isPermissionSet(name: String): Boolean =
        permissionAttachments.any { attachments ->
            val permissions = attachments.permissions
            name in permissions && permissions[name] == true
        }

    override fun isPermissionSet(perm: Permission): Boolean =
        isPermissionSet(perm.name.lowercase())

    override fun hasPermission(name: String): Boolean {
        if (isPermissionSet(name)) return true

        val perm = server.pluginManager.getPermission(name)
        return perm?.let { hasPermission(perm) } ?: Permission.DEFAULT_PERMISSION.getValue(isOp)
    }

    override fun hasPermission(perm: Permission): Boolean =
        isPermissionSet(perm) || perm.default.getValue(isOp)

    override fun addAttachment(plugin: Plugin, name: String, value: Boolean): PermissionAttachment =
        addAttachment(plugin).apply {
            setPermission(name, value)
        }

    override fun addAttachment(plugin: Plugin): PermissionAttachment =
        PermissionAttachment(plugin, this).also { permissionAttachments += it }

    override fun addAttachment(plugin: Plugin, ticks: Int): PermissionAttachment? {
        throw UnimplementedOperationException()
    }

    override fun addAttachment(plugin: Plugin, name: String, value: Boolean, ticks: Int): PermissionAttachment? {
        throw UnimplementedOperationException()
    }

    override fun removeAttachment(attachment: PermissionAttachment) {
        if (attachment in permissionAttachments) {
            permissionAttachments -= attachment

            attachment.removalCallback?.attachmentRemoved(attachment)

            recalculatePermissions()
        } else {
            throw IllegalArgumentException("Given attachment is not part of Permissible object $this")
        }
    }

    override fun recalculatePermissions() {
        // nothing to do
    }

    override fun getEffectivePermissions(): Set<PermissionAttachmentInfo> =
        permissionAttachments.flatMap { attachment ->
            attachment.permissions.map { (key, value) ->
                PermissionAttachmentInfo(this, key, attachment, value)
            }
        }.toSet()

    override fun getVelocity(): Vector = _velocity

    override fun setVelocity(velocity: Vector) {
        _velocity = velocity
    }

    override fun getHeight(): Double {
        throw UnimplementedOperationException()
    }

    override fun getWidth(): Double {
        throw UnimplementedOperationException()
    }

    override fun isOnGround(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun getNearbyEntities(x: Double, y: Double, z: Double): MutableList<Entity> {
        throw UnimplementedOperationException()
    }

    override fun getEntityId(): Int {
        throw UnimplementedOperationException()
    }

    override fun getFireTicks(): Int = _fireTicks

    override fun getMaxFireTicks(): Int = _maxFireTicks

    override fun setFireTicks(ticks: Int) {
        _fireTicks = ticks
    }

    override fun isVisualFire(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun setVisualFire(fire: Boolean) {
        throw UnimplementedOperationException()
    }

    override fun setFreezeTicks(ticks: Int) {
        throw UnimplementedOperationException()
    }

    override fun isFrozen(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun getFreezeTicks(): Int {
        throw UnimplementedOperationException()
    }

    override fun getMaxFreezeTicks(): Int {
        throw UnimplementedOperationException()
    }

    override fun remove() {
        throw UnimplementedOperationException()
    }

    override fun isDead(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun isValid(): Boolean = !isDead

    override fun getServer(): Server = server

    override fun getPassenger(): Entity? {
        throw UnimplementedOperationException()
    }

    override fun setPassenger(passenger: Entity): Boolean {
        throw UnimplementedOperationException()
    }

    override fun getPassengers(): List<Entity> {
        throw UnimplementedOperationException()
    }

    override fun addPassenger(passenger: Entity): Boolean {
        throw UnimplementedOperationException()
    }

    override fun removePassenger(passenger: Entity): Boolean {
        throw UnimplementedOperationException()
    }

    override fun isEmpty(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun eject(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun getFallDistance(): Float = _fallDistance

    override fun setFallDistance(distance: Float) {
        _fallDistance = distance
    }

    override fun getLastDamageCause(): EntityDamageEvent? {
        throw UnimplementedOperationException()
    }

    override fun setLastDamageCause(event: EntityDamageEvent?) {
        throw UnimplementedOperationException()
    }

    override fun getTicksLived(): Int {
        throw UnimplementedOperationException()
    }

    override fun setTicksLived(value: Int) {
        throw UnimplementedOperationException()
    }

    override fun playEffect(type: EntityEffect) {
        throw UnimplementedOperationException()
    }

    override fun isInsideVehicle(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun leaveVehicle(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun getVehicle(): Entity? {
        throw UnimplementedOperationException()
    }

    override fun isCustomNameVisible(): Boolean = _isCustomNameVisible

    override fun setCustomNameVisible(flag: Boolean) {
        _isCustomNameVisible = flag
    }

    override fun isGlowing(): Boolean = _isGlowing

    override fun setGlowing(flag: Boolean) {
        _isGlowing = flag
    }

    override fun isInvulnerable(): Boolean = _isInvulnerable

    override fun setInvulnerable(flag: Boolean) {
        _isInvulnerable = flag
    }

    override fun isSilent(): Boolean = _isSilent

    override fun setSilent(flag: Boolean) {
        _isSilent = flag
    }

    override fun hasGravity(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun setGravity(gravity: Boolean) {
        throw UnimplementedOperationException()
    }

    override fun getPortalCooldown(): Int {
        throw UnimplementedOperationException()
    }

    override fun setPortalCooldown(cooldown: Int) {
        throw UnimplementedOperationException()
    }

    override fun getScoreboardTags(): Set<String> {
        throw UnimplementedOperationException()
    }

    override fun addScoreboardTag(tag: String): Boolean {
        throw UnimplementedOperationException()
    }

    override fun removeScoreboardTag(tag: String): Boolean {
        throw UnimplementedOperationException()
    }

    override fun getPistonMoveReaction(): PistonMoveReaction {
        throw UnimplementedOperationException()
    }

    override fun setRotation(yaw: Float, pitch: Float) {
        throw UnimplementedOperationException()
    }

    override fun getBoundingBox(): BoundingBox {
        throw UnimplementedOperationException()
    }

    override fun isPersistent(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun setPersistent(persistent: Boolean) {
        throw UnimplementedOperationException()
    }

    override fun getFacing(): BlockFace {
        throw UnimplementedOperationException()
    }

    override fun getPose(): Pose {
        throw UnimplementedOperationException()
    }

    override fun teamDisplayName(): Component {
        throw UnimplementedOperationException()
    }

    override fun getOrigin(): Location? {
        throw UnimplementedOperationException()
    }

    override fun fromMobSpawner(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun getEntitySpawnReason(): CreatureSpawnEvent.SpawnReason {
        throw UnimplementedOperationException()
    }

    override fun isInRain(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun isInBubbleColumn(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun isInWaterOrRain(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun isInWaterOrBubbleColumn(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun isInWaterOrRainOrBubbleColumn(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun isInLava(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun isTicking(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun getTrackedPlayers(): MutableSet<Player> {
        throw UnimplementedOperationException()
    }

    override fun spawnAt(location: Location, reason: CreatureSpawnEvent.SpawnReason): Boolean {
        throw UnimplementedOperationException()
    }

    override fun isInWater(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun spigot(): Entity.Spigot = this
}
