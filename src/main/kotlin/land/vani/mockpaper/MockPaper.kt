package land.vani.mockpaper

import org.bukkit.Bukkit
import java.util.logging.Level

object MockPaper {
    @JvmStatic
    var mock: ServerMock?
        get() = Bukkit.getServer() as ServerMock?
        private set(value) {
            if (value != null) {
                val defaultLevel = value.logger.level
                value.logger.level = Level.WARNING
                Bukkit.setServer(value)
                value.logger.level = defaultLevel
            } else {
                val serverProperty = Bukkit::class.java.getDeclaredField("server").apply {
                    isAccessible = true
                }
                serverProperty[null] = null
            }
        }

    @JvmStatic
    fun mock(): ServerMock = mock(ServerMock())

    @JvmStatic
    fun <T : ServerMock> mock(implementation: T): T {
        @Suppress("SENSELESS_COMPARISON")
        check(Bukkit.getServer() == null && mock == null) {
            "ServerMock is already initialized." +
                " If you want to re-mock with another implementation, please unmock first."
        }

        return implementation.also {
            mock = it
        }
    }

    @JvmStatic
    fun unmock() {
        val mock = this.mock ?: run {
            println("unmock is requested but mock is null")
            return
        }

        mock.pluginManager.disablePlugins()

        try {
            mock.scheduler.shutdown()
        } finally {
            mock.pluginManager.deleteTemporaryPaths()
        }

        this.mock = null
    }
}
