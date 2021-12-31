package land.vani.mockpaper.entity

import land.vani.mockpaper.ServerMock
import land.vani.mockpaper.UnimplementedOperationException
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Entity
import org.bukkit.entity.Monster
import java.util.UUID

abstract class MonsterMock(server: ServerMock, uuid: UUID) : CreatureMock(server, uuid), Monster {
    override fun isAware(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun setAware(aware: Boolean) {
        throw UnimplementedOperationException()
    }

    override fun attack(target: Entity) {
        throw UnimplementedOperationException()
    }

    override fun registerAttribute(attribute: Attribute) {
        throw UnimplementedOperationException()
    }
}
