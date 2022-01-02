package land.vani.mockpaper.entity

import land.vani.mockpaper.ServerMock
import land.vani.mockpaper.UnimplementedOperationException
import org.bukkit.entity.EntityType
import java.util.UUID

open class SimpleMobMock(server: ServerMock, uuid: UUID) : EntityMock(server, uuid) {
    constructor(server: ServerMock) : this(server, UUID.randomUUID())

    override fun getType(): EntityType {
        throw UnimplementedOperationException()
    }
}
