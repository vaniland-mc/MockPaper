package land.vani.mockpaper.entity

import land.vani.mockpaper.ServerMock
import org.bukkit.entity.Projectile
import org.bukkit.projectiles.ProjectileSource
import java.util.UUID

/**
 * [EntityMock] that representing a generic [Projectile].
 *
 * @author TheBusyBiscuit
 */
abstract class ProjectileMock(server: ServerMock, uuid: UUID) : EntityMock(server, uuid), Projectile {
    private var source: ProjectileSource? = null
    private var isBounce: Boolean = false

    override fun getShooter(): ProjectileSource? = source

    override fun setShooter(source: ProjectileSource?) {
        this.source = source
    }

    override fun doesBounce(): Boolean = isBounce

    override fun setBounce(doesBounce: Boolean) {
        isBounce = doesBounce
    }
}
