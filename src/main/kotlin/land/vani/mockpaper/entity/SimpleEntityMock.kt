package land.vani.mockpaper.entity

import land.vani.mockpaper.ServerMock
import land.vani.mockpaper.UnimplementedOperationException
import org.bukkit.entity.EntityType
import java.util.UUID

/**
 * A very simple class that allows one to create an instance of an entity when a specific type of entity
 * is not required.
 *
 * This should only be used for testing code that doesn't care what type of entity it is.
 */
class SimpleEntityMock(server: ServerMock, uuid: UUID) : EntityMock(server, uuid) {
    constructor(server: ServerMock) : this(server, UUID.randomUUID())

    override fun getType(): EntityType {
        throw UnimplementedOperationException()
    }
}
