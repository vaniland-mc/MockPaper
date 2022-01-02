package land.vani.mockpaper.inventory.meta

import com.destroystokyo.paper.profile.PlayerProfile
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.inventory.meta.SkullMeta

class SkullMetaMock : ItemMetaMock, SkullMeta {
    private var owningPlayer: OfflinePlayer? = null
    private var profile: PlayerProfile? = null

    constructor() : super()

    constructor(meta: SkullMeta) : super(meta) {
        owningPlayer = meta.owningPlayer
    }

    override fun getOwner(): String? = owningPlayer?.name

    override fun hasOwner(): Boolean = owningPlayer != null

    override fun setOwner(owner: String?): Boolean {
        owningPlayer = owner?.let {
            @Suppress("DEPRECATION")
            Bukkit.getOfflinePlayer(it)
        }
        return true
    }

    override fun getPlayerProfile(): PlayerProfile? = profile

    override fun setPlayerProfile(profile: PlayerProfile?) {
        this.profile = profile
    }

    override fun getOwningPlayer(): OfflinePlayer? = owningPlayer

    override fun setOwningPlayer(owner: OfflinePlayer?): Boolean {
        owningPlayer = owner
        return true
    }

    override fun clone(): SkullMetaMock = (super.clone() as SkullMetaMock)
        .apply {
            owner = this@SkullMetaMock.owner
            playerProfile = this@SkullMetaMock.playerProfile
            owningPlayer = this@SkullMetaMock.owningPlayer
        }
}