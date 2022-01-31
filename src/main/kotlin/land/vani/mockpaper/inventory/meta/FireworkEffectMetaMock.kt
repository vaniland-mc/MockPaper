package land.vani.mockpaper.inventory.meta

import org.bukkit.FireworkEffect
import org.bukkit.inventory.meta.FireworkEffectMeta
import java.util.Objects

class FireworkEffectMetaMock : ItemMetaMock, FireworkEffectMeta {
    private var effect: FireworkEffect? = null

    constructor() : super()

    constructor(meta: FireworkEffectMeta) : super(meta) {
        effect = meta.effect
    }

    override fun hasEffect(): Boolean = effect != null

    override fun getEffect(): FireworkEffect? = effect

    override fun setEffect(effect: FireworkEffect?) {
        this.effect = effect
    }

    override fun clone(): FireworkEffectMetaMock = (super.clone() as FireworkEffectMetaMock)
        .apply {
            effect = this@FireworkEffectMetaMock.effect
        }

    override fun hashCode(): Int = super.hashCode() + Objects.hash(effect)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (!super.equals(other)) return false
        if (other !is FireworkEffectMetaMock) return false

        if (effect != other.effect) return false

        return true
    }
}
