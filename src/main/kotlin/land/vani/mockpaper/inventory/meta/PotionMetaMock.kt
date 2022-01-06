package land.vani.mockpaper.inventory.meta

import land.vani.mockpaper.UnimplementedOperationException
import land.vani.mockpaper.internal.asUnmodifiable
import org.bukkit.Color
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.potion.PotionData
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.potion.PotionType

class PotionMetaMock : ItemMetaMock, PotionMeta {
    private val effects: MutableList<PotionEffect> = mutableListOf()
    private var basePotionData: PotionData = PotionData(PotionType.UNCRAFTABLE)
    private var color: Color? = null

    constructor() : super()

    constructor(meta: PotionMeta) : super(meta) {
        effects += meta.customEffects
        basePotionData = meta.basePotionData
        color = meta.color
    }

    override fun setBasePotionData(data: PotionData) {
        this.basePotionData = data
    }

    override fun getBasePotionData(): PotionData = basePotionData

    override fun hasCustomEffects(): Boolean = effects.isNotEmpty()

    override fun getCustomEffects(): List<PotionEffect> =
        effects.asUnmodifiable()

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

    override fun hasCustomEffect(type: PotionEffectType): Boolean =
        indexOf(type) != -1

    private fun indexOf(type: PotionEffectType): Int {
        for (i in effects.indices) {
            if (effects[i].type == type) {
                return i
            }
        }
        return -1
    }

    override fun setMainEffect(type: PotionEffectType): Boolean {
        throw UnimplementedOperationException()
    }

    override fun clearCustomEffects(): Boolean {
        effects.clear()
        return true
    }

    override fun hasColor(): Boolean = color != null

    override fun getColor(): Color? = color

    override fun setColor(color: Color?) {
        this.color = color
    }

    override fun clone(): PotionMetaMock = (super.clone() as PotionMetaMock)
        .apply {
            effects += this@PotionMetaMock.effects
            basePotionData = this@PotionMetaMock.basePotionData
            color = this@PotionMetaMock.color
        }
}
