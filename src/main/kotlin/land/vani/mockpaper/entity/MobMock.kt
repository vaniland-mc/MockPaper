package land.vani.mockpaper.entity

import land.vani.mockpaper.ServerMock
import land.vani.mockpaper.UnimplementedOperationException
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Mob
import org.bukkit.loot.LootTable
import java.util.UUID

abstract class MobMock(
    server: ServerMock,
    uuid: UUID,
) : LivingEntityMock(server, uuid), Mob {
    override fun isInDaylight(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun lookAt(entity: Entity) {
        throw UnimplementedOperationException()
    }

    override fun lookAt(location: Location) {
        throw UnimplementedOperationException()
    }

    override fun lookAt(entity: Entity, headRotationSpeed: Float, maxHeadPitch: Float) {
        throw UnimplementedOperationException()
    }

    override fun lookAt(location: Location, headRotationSpeed: Float, maxHeadPitch: Float) {
        throw UnimplementedOperationException()
    }

    override fun lookAt(x: Double, y: Double, z: Double) {
        throw UnimplementedOperationException()
    }

    override fun lookAt(x: Double, y: Double, z: Double, headRotationSpeed: Float, maxHeadPitch: Float) {
        throw UnimplementedOperationException()
    }

    override fun getHeadRotationSpeed(): Int {
        throw UnimplementedOperationException()
    }

    override fun getMaxHeadPitch(): Int {
        throw UnimplementedOperationException()
    }

    override fun getTarget(): LivingEntity? {
        throw UnimplementedOperationException()
    }

    override fun setTarget(target: LivingEntity?) {
        throw UnimplementedOperationException()
    }

    override fun getLootTable(): LootTable? {
        throw UnimplementedOperationException()
    }

    override fun setLootTable(table: LootTable?) {
        throw UnimplementedOperationException()
    }

    override fun getSeed(): Long {
        throw UnimplementedOperationException()
    }

    override fun setSeed(seed: Long) {
        throw UnimplementedOperationException()
    }

    override fun isSleeping(): Boolean {
        throw UnimplementedOperationException()
    }
}
