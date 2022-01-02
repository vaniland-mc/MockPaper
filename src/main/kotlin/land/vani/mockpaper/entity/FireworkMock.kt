package land.vani.mockpaper.entity

import land.vani.mockpaper.ServerMock
import land.vani.mockpaper.UnimplementedOperationException
import org.bukkit.entity.EntityType
import org.bukkit.entity.Firework
import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.meta.FireworkMeta
import java.util.UUID

/**
 * This is a simple mock of the [Firework].
 *
 * It takes a [FireworkMeta] to carry all properties.
 *
 * @author TheBusyBiscuit
 */
class FireworkMock(
    server: ServerMock,
    uuid: UUID,
    private var meta: FireworkMeta,
) : ProjectileMock(server, uuid), Firework {
    private var isShotAtAngle: Boolean = false

    override fun getType(): EntityType = EntityType.FIREWORK

    override fun getFireworkMeta(): FireworkMeta = meta

    override fun setFireworkMeta(meta: FireworkMeta) {
        this.meta = meta
    }

    override fun detonate() {
        throw UnimplementedOperationException()
    }

    override fun isShotAtAngle(): Boolean = isShotAtAngle

    override fun setShotAtAngle(shotAtAngle: Boolean) {
        isShotAtAngle = shotAtAngle
    }

    override fun getSpawningEntity(): UUID? {
        throw UnimplementedOperationException()
    }

    override fun getBoostedEntity(): LivingEntity? {
        throw UnimplementedOperationException()
    }
}
