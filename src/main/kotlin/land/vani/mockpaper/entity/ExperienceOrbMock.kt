package land.vani.mockpaper.entity

import land.vani.mockpaper.ServerMock
import land.vani.mockpaper.UnimplementedOperationException
import org.bukkit.entity.EntityType
import org.bukkit.entity.ExperienceOrb
import java.util.UUID

/**
 * This is a simple mock of the [ExperienceOrb].
 *
 * @author TheBusyBiscuit
 */
class ExperienceOrbMock(
    server: ServerMock,
    uuid: UUID,
    private var experience: Int,
) : EntityMock(server, uuid), ExperienceOrb {
    constructor(server: ServerMock, uuid: UUID) : this(server, uuid, 0)

    override fun getType(): EntityType = EntityType.EXPERIENCE_ORB

    override fun getExperience(): Int = experience

    override fun setExperience(value: Int) {
        experience = value
    }

    override fun getTriggerEntityId(): UUID? {
        throw UnimplementedOperationException()
    }

    override fun getSourceEntityId(): UUID? {
        throw UnimplementedOperationException()
    }

    override fun getSpawnReason(): ExperienceOrb.SpawnReason {
        throw UnimplementedOperationException()
    }
}
