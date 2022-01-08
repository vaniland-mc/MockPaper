package land.vani.mockpaper.entity

import com.destroystokyo.paper.block.TargetBlockInfo
import com.destroystokyo.paper.entity.TargetEntityInfo
import com.google.common.base.Function
import land.vani.mockpaper.ServerMock
import land.vani.mockpaper.UnimplementedOperationException
import land.vani.mockpaper.attribute.AttributeInstanceMock
import land.vani.mockpaper.potion.ActivePotionEffect
import org.bukkit.Bukkit
import org.bukkit.FluidCollisionMode
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeInstance
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityCategory
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Item
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.entity.memory.MemoryKey
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.util.RayTraceResult
import org.bukkit.util.Vector
import java.util.EnumMap
import java.util.UUID
import kotlin.math.min

abstract class LivingEntityMock(
    server: ServerMock,
    uuid: UUID,
) : EntityMock(server, uuid), LivingEntity {
    companion object {
        private const val MAX_HEALTH = 20.0
    }

    private var health: Double = MAX_HEALTH

    private var maxAirTick = 300
    private var remainingAirTick = 300

    private var isAlive = true
    private val attributes: MutableMap<Attribute, AttributeInstanceMock> = EnumMap(Attribute::class.java)

    private val activeEffects = mutableListOf<ActivePotionEffect>()

    init {
        attributes[Attribute.GENERIC_MAX_HEALTH] = AttributeInstanceMock(Attribute.GENERIC_MAX_HEALTH, MAX_HEALTH)
    }

    override fun getHealth(): Double = health

    override fun isDead(): Boolean = !isAlive

    override fun setHealth(health: Double) {
        if (health > 0) {
            this.health = min(health, maxHealth)
            return
        }
        this.health = 0.0

        val event = EntityDeathEvent(this, mutableListOf(), 0)
        server.pluginManager.callEvent(event)

        isAlive = false
    }

    override fun getMaxHealth(): Double = getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.value

    override fun setMaxHealth(health: Double) {
        getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.baseValue = health
        if (health > health) {
            this.health = health
        }
    }

    override fun resetMaxHealth() {
        maxHealth = MAX_HEALTH
    }

    override fun damage(amount: Double) {
        damage(amount, null)
    }

    @Suppress("DEPRECATION")
    override fun damage(amount: Double, source: Entity?) {
        if (isInvulnerable) {
            if (source is HumanEntity && source.gameMode != GameMode.CREATIVE) {
                return
            }
        }

        val modifiers =
            EnumMap<EntityDamageEvent.DamageModifier, Double>(EntityDamageEvent.DamageModifier::class.java).apply {
                put(EntityDamageEvent.DamageModifier.BASE, 1.0)
            }
        val modifierFunctions =
            EnumMap<EntityDamageEvent.DamageModifier, Function<Double, Double>>(
                EntityDamageEvent.DamageModifier::class.java
            ).apply {
                put(EntityDamageEvent.DamageModifier.BASE) { damage -> damage }
            }

        val event = if (source != null) {
            EntityDamageByEntityEvent(
                source,
                this,
                EntityDamageEvent.DamageCause.ENTITY_ATTACK,
                modifiers,
                modifierFunctions
            )
        } else {
            EntityDamageEvent(this, EntityDamageEvent.DamageCause.CUSTOM, modifiers, modifierFunctions)
        }
        event.damage = amount
        Bukkit.getPluginManager().callEvent(event)
        if (event.isCancelled) return

        health -= amount
    }

    override fun getAbsorptionAmount(): Double {
        throw UnimplementedOperationException()
    }

    override fun setAbsorptionAmount(amount: Double) {
        throw UnimplementedOperationException()
    }

    override fun getAttribute(attribute: Attribute): AttributeInstance? {
        if (attribute in attributes) {
            return attributes[attribute]
        } else {
            throw UnimplementedOperationException()
        }
    }

    override fun <T : Projectile> launchProjectile(projectile: Class<out T>): T {
        throw UnimplementedOperationException()
    }

    override fun <T : Projectile> launchProjectile(projectile: Class<out T>, velocity: Vector?): T {
        val entity = launchProjectile(projectile)
        velocity?.let {
            entity.velocity = it
        }
        return entity
    }

    override fun getEyeHeight(): Double {
        throw UnimplementedOperationException()
    }

    override fun getEyeHeight(ignorePose: Boolean): Double {
        throw UnimplementedOperationException()
    }

    override fun getEyeLocation(): Location {
        throw UnimplementedOperationException()
    }

    override fun getLineOfSight(transparent: MutableSet<Material>?, maxDistance: Int): MutableList<Block> {
        throw UnimplementedOperationException()
    }

    override fun getTargetBlock(maxDistance: Int, fluidMode: TargetBlockInfo.FluidMode): Block? {
        throw UnimplementedOperationException()
    }

    override fun getTargetBlock(transparent: MutableSet<Material>?, maxDistance: Int): Block {
        throw UnimplementedOperationException()
    }

    override fun getLastTwoTargetBlocks(transparent: MutableSet<Material>?, maxDistance: Int): MutableList<Block> {
        throw UnimplementedOperationException()
    }

    override fun getTargetBlockExact(maxDistance: Int): Block? {
        throw UnimplementedOperationException()
    }

    override fun getTargetBlockExact(maxDistance: Int, fluidCollisionMode: FluidCollisionMode): Block? {
        throw UnimplementedOperationException()
    }

    override fun getTargetBlockFace(maxDistance: Int, fluidMode: TargetBlockInfo.FluidMode): BlockFace? {
        throw UnimplementedOperationException()
    }

    override fun getTargetBlockInfo(maxDistance: Int, fluidMode: TargetBlockInfo.FluidMode): TargetBlockInfo? {
        throw UnimplementedOperationException()
    }

    override fun getTargetEntity(maxDistance: Int, ignoreBlocks: Boolean): Entity? {
        throw UnimplementedOperationException()
    }

    override fun getTargetEntityInfo(maxDistance: Int, ignoreBlocks: Boolean): TargetEntityInfo? {
        throw UnimplementedOperationException()
    }

    override fun rayTraceBlocks(maxDistance: Double): RayTraceResult? {
        throw UnimplementedOperationException()
    }

    override fun rayTraceBlocks(maxDistance: Double, fluidCollisionMode: FluidCollisionMode): RayTraceResult? {
        throw UnimplementedOperationException()
    }

    override fun getRemainingAir(): Int = remainingAirTick

    override fun setRemainingAir(ticks: Int) {
        remainingAirTick = ticks
    }

    override fun getMaximumAir(): Int = maxAirTick

    override fun setMaximumAir(ticks: Int) {
        maxAirTick = ticks
    }

    override fun getMaximumNoDamageTicks(): Int {
        throw UnimplementedOperationException()
    }

    override fun setMaximumNoDamageTicks(ticks: Int) {
        throw UnimplementedOperationException()
    }

    override fun getLastDamage(): Double {
        throw UnimplementedOperationException()
    }

    override fun setLastDamage(damage: Double) {
        throw UnimplementedOperationException()
    }

    override fun getNoDamageTicks(): Int {
        throw UnimplementedOperationException()
    }

    override fun setNoDamageTicks(ticks: Int) {
        throw UnimplementedOperationException()
    }

    override fun getKiller(): Player? {
        throw UnimplementedOperationException()
    }

    override fun setKiller(killer: Player?) {
        throw UnimplementedOperationException()
    }

    @Suppress("DEPRECATION")
    override fun addPotionEffect(effect: PotionEffect): Boolean = addPotionEffect(effect, false)

    override fun addPotionEffect(effect: PotionEffect, force: Boolean): Boolean {
        activeEffects += ActivePotionEffect(effect)
        return true
    }

    override fun addPotionEffects(effects: Collection<PotionEffect>): Boolean =
        effects.all {
            addPotionEffect(it)
        }

    override fun hasPotionEffect(type: PotionEffectType): Boolean = getPotionEffect(type) != null

    override fun getPotionEffect(type: PotionEffectType): PotionEffect? =
        activePotionEffects.firstOrNull { it.type == type }

    override fun removePotionEffect(type: PotionEffectType) {
        activeEffects.removeIf { it.isExpired || it.effect.type == type }
    }

    override fun getActivePotionEffects(): Collection<PotionEffect> {
        activeEffects.removeAll { it.isExpired }
        return activeEffects
            .map { it.effect }
            .toSet()
    }

    override fun hasLineOfSight(location: Location): Boolean {
        throw UnimplementedOperationException()
    }

    override fun hasLineOfSight(other: Entity): Boolean {
        throw UnimplementedOperationException()
    }

    override fun getRemoveWhenFarAway(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun setRemoveWhenFarAway(remove: Boolean) {
        throw UnimplementedOperationException()
    }

    override fun setCanPickupItems(pickup: Boolean) {
        throw UnimplementedOperationException()
    }

    override fun getCanPickupItems(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun isLeashed(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun getLeashHolder(): Entity {
        throw UnimplementedOperationException()
    }

    override fun setLeashHolder(holder: Entity?): Boolean {
        throw UnimplementedOperationException()
    }

    override fun isGliding(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun setGliding(gliding: Boolean) {
        throw UnimplementedOperationException()
    }

    override fun isSwimming(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun setSwimming(swimming: Boolean) {
        throw UnimplementedOperationException()
    }

    override fun isRiptiding(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun isClimbing(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun hasAI(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun setAI(ai: Boolean) {
        throw UnimplementedOperationException()
    }

    override fun swingMainHand() {
        throw UnimplementedOperationException()
    }

    override fun swingOffHand() {
        throw UnimplementedOperationException()
    }

    override fun isCollidable(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun setCollidable(collidable: Boolean) {
        throw UnimplementedOperationException()
    }

    override fun getCollidableExemptions(): Set<UUID> {
        throw UnimplementedOperationException()
    }

    override fun <T : Any?> getMemory(memoryKey: MemoryKey<T>): T? {
        throw UnimplementedOperationException()
    }

    override fun <T : Any?> setMemory(memoryKey: MemoryKey<T>, memoryValue: T?) {
        throw UnimplementedOperationException()
    }

    override fun getCategory(): EntityCategory {
        throw UnimplementedOperationException()
    }

    override fun getArrowCooldown(): Int {
        throw UnimplementedOperationException()
    }

    override fun setArrowCooldown(ticks: Int) {
        throw UnimplementedOperationException()
    }

    override fun getArrowsInBody(): Int {
        throw UnimplementedOperationException()
    }

    override fun setArrowsInBody(count: Int) {
        throw UnimplementedOperationException()
    }

    override fun getBeeStingerCooldown(): Int {
        throw UnimplementedOperationException()
    }

    override fun setBeeStingerCooldown(ticks: Int) {
        throw UnimplementedOperationException()
    }

    override fun getBeeStingersInBody(): Int {
        throw UnimplementedOperationException()
    }

    override fun setBeeStingersInBody(count: Int) {
        throw UnimplementedOperationException()
    }

    override fun isInvisible(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun setInvisible(invisible: Boolean) {
        throw UnimplementedOperationException()
    }

    override fun getArrowsStuck(): Int {
        throw UnimplementedOperationException()
    }

    override fun setArrowsStuck(arrows: Int) {
        throw UnimplementedOperationException()
    }

    override fun getShieldBlockingDelay(): Int {
        throw UnimplementedOperationException()
    }

    override fun setShieldBlockingDelay(delay: Int) {
        throw UnimplementedOperationException()
    }

    override fun getActiveItem(): ItemStack? {
        throw UnimplementedOperationException()
    }

    override fun clearActiveItem() {
        throw UnimplementedOperationException()
    }

    override fun getItemUseRemainingTime(): Int {
        throw UnimplementedOperationException()
    }

    override fun getHandRaisedTime(): Int {
        throw UnimplementedOperationException()
    }

    override fun isHandRaised(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun getHandRaised(): EquipmentSlot {
        throw UnimplementedOperationException()
    }

    override fun isJumping(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun setJumping(jumping: Boolean) {
        throw UnimplementedOperationException()
    }

    override fun playPickupItemAnimation(item: Item, quantity: Int) {
        throw UnimplementedOperationException()
    }

    override fun getHurtDirection(): Float {
        throw UnimplementedOperationException()
    }

    override fun setHurtDirection(hurtDirection: Float) {
        throw UnimplementedOperationException()
    }
}
