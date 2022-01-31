package land.vani.mockpaper

import org.bukkit.Bukkit

object MockPaper {
    @JvmStatic
    @JvmOverloads
    fun mock(implementation: ServerMock = ServerMock()): ServerMock {
        @Suppress("SENSELESS_COMPARISON")
        if (Bukkit.getServer() == null) {
            Bukkit.setServer(implementation)
        }
        return implementation
    }
}
