package land.vani.mockpaper

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import org.bukkit.plugin.InvalidPluginException
import org.bukkit.plugin.PluginDescriptionFile

class UnsafeValuesMockTest : ShouldSpec({
    lateinit var server: ServerMock
    lateinit var unsafeValues: UnsafeValuesMock

    beforeTest {
        server = MockPaper.mock()
        unsafeValues = server.unsafe
    }

    val pluginInfoFormat = """
        name: VersionTest
        version: 1.0
        main: not.exists
        api-version: %s
    """.trimIndent()

    fun checkVersion(version: String) {
        val description = PluginDescriptionFile(
            pluginInfoFormat.format(version).reader()
        )
        unsafeValues.checkSupported(description)
    }

    should("checkSupported current server version") {
        var currentVersion = server.bukkitVersion
        if (currentVersion matches ".{1,3}\\..{1,3}\\..*".toRegex()) {
            currentVersion = currentVersion.split(".")
                .take(2)
                .joinToString(".")
        }

        checkVersion(currentVersion)
    }

    should("supported version") {
        checkVersion("1.13")
    }

    should("unsupported version") {
        shouldThrow<InvalidPluginException> {
            checkVersion("1.8")
        }
    }

    should("no specified version") {
        val description = PluginDescriptionFile(
            "VersionTest",
            "1.0",
            "not.exists"
        )
        shouldThrow<InvalidPluginException> {
            unsafeValues.checkSupported(description)
        }
    }
})
