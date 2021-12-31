package land.vani.mockpaper.sound

import org.bukkit.Sound
import kotlin.test.assertTrue

interface SoundReceiver {
    val heardSounds: MutableList<AudioExperience>

    fun clearSounds() = heardSounds.clear()

    fun assertSoundHeard(
        message: String = "Sound Heard Assertion failed",
        sound: Sound,
        predicate: (AudioExperience.BukkitAudioExperience) -> Boolean = { true },
    ) = assertSoundHeard(message, sound.key.key, predicate)

    fun assertSoundHeard(
        message: String = "Sound Heard Assertion failed",
        sound: String,
        predicate: (AudioExperience.BukkitAudioExperience) -> Boolean = { true },
    ) {
        assertTrue(message) {
            heardSounds.filterIsInstance<AudioExperience.BukkitAudioExperience>()
                .any { it.sound == sound && predicate(it) }
        }
    }

    fun assertSoundHeard(
        message: String = "Sound Heard Assertion failed",
        sound: net.kyori.adventure.sound.Sound,
        predicate: (AudioExperience.KyoriAudioExperience) -> Boolean = { true },
    ) {
        assertTrue(message) {
            heardSounds.filterIsInstance<AudioExperience.KyoriAudioExperience>()
                .any { it.sound == sound && predicate(it) }
        }
    }

    fun assertSoundHeard(
        message: String = "Sound Heard Assertion failed",
        sound: net.kyori.adventure.sound.Sound,
        emitter: net.kyori.adventure.sound.Sound.Emitter,
        predicate: (AudioExperience.KyoriEmitterAudioExperience) -> Boolean = { true },
    ) {
        assertTrue(message) {
            heardSounds.filterIsInstance<AudioExperience.KyoriEmitterAudioExperience>()
                .any { it.sound == sound && it.emitter == emitter && predicate(it) }
        }
    }
}
