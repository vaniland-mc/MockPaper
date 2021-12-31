package land.vani.mockpaper.sound

import org.bukkit.Location
import org.bukkit.Sound
import org.bukkit.SoundCategory

sealed class AudioExperience {
    class BukkitAudioExperience(
        val sound: String,
        val category: SoundCategory,
        val location: Location,
        val volume: Float,
        val pitch: Float,
    ) : AudioExperience() {
        constructor(
            sound: Sound,
            category: SoundCategory,
            location: Location,
            volume: Float,
            pitch: Float,
        ) : this(sound.key.key, category, location, volume, pitch)
    }

    class KyoriAudioExperience(
        val sound: net.kyori.adventure.sound.Sound,
        val x: Double,
        val y: Double,
        val z: Double,
    ) : AudioExperience()

    class KyoriEmitterAudioExperience(
        val sound: net.kyori.adventure.sound.Sound,
        val emitter: net.kyori.adventure.sound.Sound.Emitter,
    ) : AudioExperience()
}
