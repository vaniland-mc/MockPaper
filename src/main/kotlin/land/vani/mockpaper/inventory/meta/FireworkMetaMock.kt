package land.vani.mockpaper.inventory.meta

import land.vani.mockpaper.internal.asUnmodifiable
import org.bukkit.FireworkEffect
import org.bukkit.inventory.meta.FireworkMeta
import java.util.Objects

class FireworkMetaMock : ItemMetaMock, FireworkMeta {
    private val effects: MutableList<FireworkEffect> = mutableListOf()
    private var power: Int = 0

    constructor() : super()

    constructor(meta: FireworkMeta) : super(meta) {
        effects += meta.effects
    }

    override fun addEffect(effect: FireworkEffect) {
        effects += effect
    }

    override fun addEffects(effects: Iterable<FireworkEffect>) {
        effects.forEach {
            addEffect(it)
        }
    }

    override fun addEffects(vararg effects: FireworkEffect) {
        effects.forEach {
            addEffect(it)
        }
    }

    override fun getEffects(): List<FireworkEffect> = effects.asUnmodifiable()

    override fun getEffectsSize(): Int = effects.size

    override fun removeEffect(index: Int) {
        effects.removeAt(index)
    }

    override fun clearEffects() {
        effects.clear()
    }

    override fun hasEffects(): Boolean = effects.isNotEmpty()

    override fun getPower(): Int = power

    override fun setPower(power: Int) {
        this.power = power
    }

    override fun clone(): FireworkMetaMock = (super.clone() as FireworkMetaMock)
        .apply {
            effects += this@FireworkMetaMock.effects
            power = this@FireworkMetaMock.power
        }

    override fun hashCode(): Int = super.hashCode() + Objects.hash(
        effects,
        power
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (!super.equals(other)) return false
        if (other !is FireworkMetaMock) return false

        if (effects != other.effects) return false
        if (power != other.power) return false

        return true
    }
}
