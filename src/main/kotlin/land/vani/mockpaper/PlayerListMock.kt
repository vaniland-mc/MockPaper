package land.vani.mockpaper

import land.vani.mockpaper.internal.asSynchronized
import land.vani.mockpaper.internal.asUnmodifiable
import land.vani.mockpaper.player.OfflinePlayerMock
import land.vani.mockpaper.player.PlayerMock
import org.bukkit.OfflinePlayer
import java.util.UUID
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.math.abs

class PlayerListMock(
    private val server: ServerMock,
) {
    var maxPlayers = Int.MAX_VALUE

    private val _onlinePlayers: MutableList<PlayerMock> = CopyOnWriteArrayList()
    private val _offlinePlayers: MutableSet<OfflinePlayer> = mutableSetOf<OfflinePlayer>().asSynchronized()

    val ipBans = BanListMock()
    val profileBans = BanListMock()

    fun addPlayer(player: PlayerMock) {
        _onlinePlayers += player
        _offlinePlayers += player
    }

    fun addOfflinePlayer(player: OfflinePlayer) {
        _offlinePlayers += player
    }

    val operators: Set<OfflinePlayer>
        get() = ((_onlinePlayers + _offlinePlayers).toSet()).filter { it.isOp }
            .toSet()

    val onlinePlayers: Collection<PlayerMock>
        get() = _onlinePlayers.asUnmodifiable()

    val offlinePlayers: Collection<OfflinePlayer>
        get() = _offlinePlayers.asUnmodifiable()

    fun matchPlayers(name: String): List<PlayerMock> =
        onlinePlayers.filter { it.name.lowercase() == name.lowercase() }

    fun getPlayerExact(name: String): PlayerMock? =
        onlinePlayers.find { it.name.lowercase() == name.lowercase() }

    fun getPlayer(name: String): PlayerMock? {
        var player = getPlayerExact(name)
        if (player != null) return player

        val lowercase = name.lowercase()
        var delta = Int.MAX_VALUE

        onlinePlayers.forEach { namedPlayer ->
            if (namedPlayer.name.lowercase().startsWith(lowercase)) {
                val currentDelta = abs(namedPlayer.name.length - lowercase.length)
                if (currentDelta < delta) {
                    delta = currentDelta
                    player = namedPlayer
                }
            }
        }

        return player
    }

    fun getPlayer(uuid: UUID): PlayerMock? = onlinePlayers.find { it.uniqueId == uuid }

    fun getOfflinePlayer(name: String): OfflinePlayer =
        getPlayer(name)
            ?: offlinePlayers.find { it.name == name }
            ?: OfflinePlayerMock(server, name)

    fun getOfflinePlayer(uuid: UUID): OfflinePlayer? =
        getPlayer(uuid) ?: offlinePlayers.find { it.uniqueId == uuid }

    fun clearOnlinePlayers() {
        _onlinePlayers.clear()
    }

    fun clearOfflinePlayers() {
        _offlinePlayers.clear()
    }
}
