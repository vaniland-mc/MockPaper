package land.vani.mockpaper.inventory.meta

import land.vani.mockpaper.ServerMock
import org.bukkit.Color
import org.bukkit.inventory.meta.LeatherArmorMeta

class LeatherArmorMetaMock : ItemMetaMock, LeatherArmorMeta {
    private var color: Color? = null
    private val server: ServerMock

    constructor(server: ServerMock) : super() {
        this.server = server
    }

    constructor(server: ServerMock, meta: LeatherArmorMeta) : super(meta) {
        color = meta.color
        this.server = server
    }

    override fun getColor(): Color =
        color ?: server.itemFactory.defaultLeatherColor

    override fun setColor(color: Color?) {
        this.color = color
    }

    override fun clone(): LeatherArmorMetaMock = (super.clone() as LeatherArmorMetaMock)
        .apply {
            color = this@LeatherArmorMetaMock.color
        }
}
