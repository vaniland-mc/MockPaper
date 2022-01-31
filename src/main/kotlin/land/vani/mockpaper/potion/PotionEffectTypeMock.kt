package land.vani.mockpaper.potion

import org.bukkit.Color
import org.bukkit.NamespacedKey
import org.bukkit.potion.PotionEffectType

/**
 * This mocks an actual [PotionEffectType] by tacking an id, a name, wheather it is
 * instant and RGB [Color] variable.
 *
 * @author TheBusyBiscuit
 */
class PotionEffectTypeMock(
    key: NamespacedKey,
    id: Int,
    private val name: String,
    private val instant: Boolean,
    private val color: Color,
) : PotionEffectType(id, key) {
    override fun getDurationModifier(): Double = 1.0

    override fun getName(): String = name

    override fun isInstant(): Boolean = instant

    override fun getColor(): Color = color
}

fun registerPotionEffectTypes() {
    mapOf(
        1 to "speed",
        2 to "slowness",
        3 to "haste",
        4 to "mining_fatigue",
        5 to "strength",
        6 to "instant_health",
        7 to "instant_damage",
        8 to "jump_boost",
        9 to "nausea",
        10 to "regeneration",
        11 to "resistance",
        12 to "fire_resistance",
        13 to "water_breathing",
        14 to "invisibility",
        15 to "blindness",
        16 to "night_vision",
        17 to "hunger",
        18 to "weakness",
        19 to "poison",
        20 to "wither",
        21 to "health_boost",
        22 to "absorption",
        23 to "saturation",
        24 to "glowing",
        25 to "levitation",
        26 to "luck",
        27 to "unluck",
        28 to "slow_falling",
        29 to "conduit_power",
        30 to "dolphins_grace",
        31 to "bad_omen",
        32 to "hero_of_the_village",
    ).forEach { (id, name) ->
        register(id, name)
    }
}

private fun register(id: Int, name: String) {
    val key = NamespacedKey.minecraft(name)
    if (PotionEffectType.getByKey(key) == null) {
        PotionEffectType.registerPotionEffectType(
            PotionEffectTypeMock(
                key,
                id,
                name,
                false,
                Color.AQUA
            )
        )
    }
}
