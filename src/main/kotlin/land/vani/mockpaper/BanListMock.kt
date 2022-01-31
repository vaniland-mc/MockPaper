package land.vani.mockpaper

import land.vani.mockpaper.internal.asUnmodifiable
import org.bukkit.BanEntry
import org.bukkit.BanList
import java.util.Date
import java.util.Objects

/**
 * A simple mock of [BanList].
 */
class BanListMock : BanList {
    private val bans = mutableMapOf<String, BanEntry>()

    override fun addBan(target: String, reason: String?, expires: Date?, source: String?): BanEntry =
        BanEntryMock(target, expires, reason, source ?: "Server").also {
            bans[target] = it
        }

    override fun getBanEntry(target: String): BanEntry? = bans[target]

    override fun getBanEntries(): Set<BanEntry> = bans.values.toSet().asUnmodifiable()

    override fun isBanned(target: String): Boolean = target in bans

    override fun pardon(target: String) {
        bans -= target
    }

    /**
     * A simple mock of [BanEntry].
     */
    class BanEntryMock(
        private val target: String,
        private var expires: Date?,
        private var reason: String?,
        private var source: String,
    ) : BanEntry {
        private var created = Date()

        override fun getTarget(): String = target

        override fun getCreated(): Date = created

        override fun setCreated(created: Date) {
            this.created = created
        }

        override fun getSource(): String = source

        override fun setSource(source: String) {
            this.source = source
        }

        override fun getExpiration(): Date? = expires

        override fun setExpiration(expiration: Date?) {
            expires = expiration
        }

        override fun getReason(): String? = reason

        override fun setReason(reason: String?) {
            this.reason = reason
        }

        override fun save() {
            throw UnimplementedOperationException()
        }

        override fun hashCode(): Int = Objects.hash(
            target,
            expiration,
            reason,
            source,
            created,
        )

        override fun equals(other: Any?): Boolean {
            if (this === other) return true

            if (other !is BanEntry) return false
            return (target == other.target) &&
                (expiration == other.expiration) &&
                (reason == other.reason) &&
                (source == other.source) &&
                (created == other.created)
        }
    }
}
