package land.vani.mockpaper.command

import land.vani.mockpaper.ServerMock
import org.bukkit.command.CommandMap
import org.bukkit.command.SimpleCommandMap

class CommandMapMock(server: ServerMock) : SimpleCommandMap(server), CommandMap
