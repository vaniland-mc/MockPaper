package land.vani.mockpaper.block.state

import land.vani.mockpaper.UnimplementedOperationException
import org.bukkit.block.Block
import org.bukkit.entity.Projectile
import org.bukkit.projectiles.BlockProjectileSource
import org.bukkit.util.Vector

class DispenserProjectileSourceMock(
    private val dispenser: DispenserMock,
) : BlockProjectileSource {
    override fun <T : Projectile?> launchProjectile(projectile: Class<out T>): T {
        throw UnimplementedOperationException()
    }

    override fun <T : Projectile?> launchProjectile(projectile: Class<out T>, velocity: Vector?): T {
        throw UnimplementedOperationException()
    }

    override fun getBlock(): Block = dispenser.block
}
