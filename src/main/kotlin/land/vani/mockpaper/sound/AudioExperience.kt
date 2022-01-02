package land.vani.mockpaper.sound

import org.bukkit.Location
import org.bukkit.Sound
import org.bukkit.SoundCategory

/**
 * This class represents a [Sound] or [net.kyori.adventure.sound.Sound] that was heard by
 * a [land.vani.mockpaper.sound.SoundReceiver].
 */
sealed class AudioExperience {
    /**
     * An [AudioExperience] implementation for Bukkit [Sound].
     *
     * @property sound Played [Sound] as string representation.
     * @property category [SoundCategory] with which sound played.
     * @property location [Location] at which the sound was played.
     * @property volume The volume of the sound.
     * @property pitch The pitch of the sound.
     */
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

    /**
     * An [AudioExperience] implementation for [net.kyori.adventure.sound.Sound].
     *
     * @property sound Played [net.kyori.adventure.sound.Sound].
     * @property x x coordinate
     * @property y y coordinate
     * @property z z coordinate
     */
    class KyoriAudioExperience(
        val sound: net.kyori.adventure.sound.Sound,
        val x: Double,
        val y: Double,
        val z: Double,
    ) : AudioExperience()

    /**
     * An [AudioExperience] implementation for [net.kyori.adventure.sound.Sound] and
     * [net.kyori.adventure.sound.Sound.Emitter].
     *
     * @property sound Played [net.kyori.adventure.sound.Sound].
     * @property emitter An emitter that the sound follows.
     */
    class KyoriEmitterAudioExperience(
        val sound: net.kyori.adventure.sound.Sound,
        val emitter: net.kyori.adventure.sound.Sound.Emitter,
    ) : AudioExperience()
}
