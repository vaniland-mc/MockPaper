package land.vani.mockpaper.inventory.meta

import land.vani.mockpaper.internal.asUnmodifiable
import org.bukkit.inventory.meta.SuspiciousStewMeta
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class SuspiciousStewMetaMock : ItemMetaMock, SuspiciousStewMeta {
    private val effects: MutableList<PotionEffect> = mutableListOf()

    constructor() : super()
    constructor(meta: SuspiciousStewMeta) : super(meta) {
        effects += meta.customEffects
    }

    override fun hasCustomEffects(): Boolean = effects.isNotEmpty()

    override fun getCustomEffects(): List<PotionEffect> = effects.asUnmodifiable()

    override fun addCustomEffect(effect: PotionEffect, overwrite: Boolean): Boolean {
        val index = indexOf(effect.type)

        if (index == -1) {
            effects += effect
            return true
        }

        if (!overwrite) return false

        val prev = effects[index]
        if (prev.duration == effect.duration) return false
        effects[index] = effect
        return true
    }

    override fun removeCustomEffect(type: PotionEffectType): Boolean =
        effects.removeAll { it.type == type }

    override fun hasCustomEffect(type: PotionEffectType): Boolean = indexOf(type) != -1

    override fun clearCustomEffects(): Boolean {
        effects.clear()
        return true
    }

    private fun indexOf(type: PotionEffectType): Int {
        for (i in effects.indices) {
            if (effects[i].type == type) return i
        }
        return -1
    }

    override fun clone(): SuspiciousStewMetaMock = (super.clone() as SuspiciousStewMetaMock)
        .apply {
            effects += this@SuspiciousStewMetaMock.effects
        }
}
