package land.vani.mockpaper.inventory.meta

import org.bukkit.FireworkEffect
import org.bukkit.inventory.meta.FireworkEffectMeta

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
}
