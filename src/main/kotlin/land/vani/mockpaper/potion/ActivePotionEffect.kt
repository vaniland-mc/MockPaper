package land.vani.mockpaper.potion

import org.bukkit.potion.PotionEffect
import java.util.concurrent.TimeUnit

class ActivePotionEffect(
    val effect: PotionEffect,
) {
    private val startedTime = System.currentTimeMillis()

    val isExpired: Boolean
        get() {
            val tick = effect.duration * 20L
            return tick < 1 || startedTime + TimeUnit.SECONDS.toMillis(tick) < System.currentTimeMillis()
        }
}
