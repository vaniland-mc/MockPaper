package land.vani.mockpaper.inventory.meta

import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.inventory.meta.LeatherArmorMeta

class LeatherArmorMetaMock : ItemMetaMock, LeatherArmorMeta {
    private var color: Color? = null

    constructor() : super()

    constructor(meta: LeatherArmorMeta) : super(meta) {
        color = meta.color
    }

    override fun getColor(): Color =
        color ?: Bukkit.getServer().itemFactory.defaultLeatherColor

    override fun setColor(color: Color?) {
        this.color = color
    }

    override fun clone(): LeatherArmorMetaMock = (super.clone() as LeatherArmorMetaMock)
        .apply {
            color = this@LeatherArmorMetaMock.color
        }
}
