package land.vani.mockpaper.entity

import land.vani.mockpaper.ServerMock
import org.bukkit.entity.Mob
import java.util.UUID

abstract class CreatureMock(server: ServerMock, uuid: UUID) : MobMock(server, uuid), Mob
