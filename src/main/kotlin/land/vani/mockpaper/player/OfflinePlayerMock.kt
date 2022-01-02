package land.vani.mockpaper.player

import land.vani.mockpaper.ServerMock
import land.vani.mockpaper.UnimplementedOperationException
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.Statistic
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import java.util.UUID

class OfflinePlayerMock(
    private val server: ServerMock,
    private val uuid: UUID,
    private var name: String,
) : OfflinePlayer {
    constructor(server: ServerMock, name: String) : this(
        server,
        UUID.nameUUIDFromBytes("OfflinePlayer:$name".toByteArray()),
        name
    )

    fun join(server: ServerMock): PlayerMock {
        return PlayerMock(server, name, uuid).also {
            // TODO: Add a player mock to server
        }
    }

    override fun isOnline(): Boolean = false

    override fun getName(): String = name

    override fun getUniqueId(): UUID = uuid

    override fun isBanned(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun isWhitelisted(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun setWhitelisted(value: Boolean) {
        throw UnimplementedOperationException()
    }

    override fun getPlayer(): Player? = server.getPlayer(name)

    override fun getFirstPlayed(): Long {
        throw UnimplementedOperationException()
    }

    override fun hasPlayedBefore(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun getLastPlayed(): Long {
        throw UnimplementedOperationException()
    }

    override fun getBedSpawnLocation(): Location? {
        throw UnimplementedOperationException()
    }

    override fun getLastLogin(): Long {
        throw UnimplementedOperationException()
    }

    override fun getLastSeen(): Long {
        throw UnimplementedOperationException()
    }

    override fun incrementStatistic(statistic: Statistic) {
        throw UnimplementedOperationException()
    }

    override fun incrementStatistic(statistic: Statistic, material: Material) {
        throw UnimplementedOperationException()
    }

    override fun incrementStatistic(statistic: Statistic, entityType: EntityType) {
        throw UnimplementedOperationException()
    }

    override fun incrementStatistic(statistic: Statistic, amount: Int) {
        throw UnimplementedOperationException()
    }

    override fun incrementStatistic(statistic: Statistic, material: Material, amount: Int) {
        throw UnimplementedOperationException()
    }

    override fun incrementStatistic(statistic: Statistic, entityType: EntityType, amount: Int) {
        throw UnimplementedOperationException()
    }

    override fun decrementStatistic(statistic: Statistic) {
        throw UnimplementedOperationException()
    }

    override fun decrementStatistic(statistic: Statistic, material: Material) {
        throw UnimplementedOperationException()
    }

    override fun decrementStatistic(statistic: Statistic, entityType: EntityType) {
        throw UnimplementedOperationException()
    }

    override fun decrementStatistic(statistic: Statistic, amount: Int) {
        throw UnimplementedOperationException()
    }

    override fun decrementStatistic(statistic: Statistic, material: Material, amount: Int) {
        throw UnimplementedOperationException()
    }

    override fun decrementStatistic(statistic: Statistic, entityType: EntityType, amount: Int) {
        throw UnimplementedOperationException()
    }

    override fun getStatistic(statistic: Statistic): Int {
        throw UnimplementedOperationException()
    }

    override fun getStatistic(statistic: Statistic, entityType: EntityType): Int {
        throw UnimplementedOperationException()
    }

    override fun getStatistic(statistic: Statistic, material: Material): Int {
        throw UnimplementedOperationException()
    }

    override fun setStatistic(statistic: Statistic, newValue: Int) {
        throw UnimplementedOperationException()
    }

    override fun setStatistic(statistic: Statistic, material: Material, newValue: Int) {
        throw UnimplementedOperationException()
    }

    override fun setStatistic(statistic: Statistic, entityType: EntityType, newValue: Int) {
        throw UnimplementedOperationException()
    }

    override fun isOp(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun setOp(value: Boolean) {
        throw UnimplementedOperationException()
    }

    override fun serialize(): MutableMap<String, Any> {
        throw UnimplementedOperationException()
    }
}
