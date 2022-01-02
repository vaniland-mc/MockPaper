package land.vani.mockpaper.potion

import org.bukkit.potion.PotionEffect
import java.util.concurrent.TimeUnit

/**
 * This class represents an active [PotionEffect] which was applied to a [org.bukkit.entity.LivingEntity].
 *
 * @property effect Underlying [PotionEffect].
 *
 * @author TheBusyBiscuit
 * @see land.vani.mockpaper.entity.LivingEntityMock.addPotionEffect
 */
class ActivePotionEffect(
    val effect: PotionEffect,
) {
    private val startedTime = System.currentTimeMillis()

    /**
     * Returns [PotionEffect] has expired.
     */
    val isExpired: Boolean
        get() {
            val tick = effect.duration * 20L
            return tick < 1 || startedTime + TimeUnit.SECONDS.toMillis(tick) < System.currentTimeMillis()
        }
}
