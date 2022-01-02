package land.vani.mockpaper.entity

import com.destroystokyo.paper.entity.Pathfinder
import land.vani.mockpaper.ServerMock
import land.vani.mockpaper.UnimplementedOperationException
import org.bukkit.entity.EntityType
import org.bukkit.entity.Monster
import org.bukkit.inventory.EntityEquipment
import java.util.UUID

open class SimpleMonsterMock(server: ServerMock, uuid: UUID) : MonsterMock(server, uuid), Monster {
    constructor(server: ServerMock) : this(server, UUID.randomUUID())

    override fun getType(): EntityType {
        throw UnimplementedOperationException()
    }

    override fun getEquipment(): EntityEquipment {
        throw UnimplementedOperationException()
    }

    override fun getPathfinder(): Pathfinder {
        throw UnimplementedOperationException()
    }

    override fun isLeftHanded(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun setLeftHanded(leftHanded: Boolean) {
        throw UnimplementedOperationException()
    }
}
