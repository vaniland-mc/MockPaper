package land.vani.mockpaper.help

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import land.vani.mockpaper.MockPaper
import land.vani.mockpaper.ServerMock
import org.bukkit.command.defaults.VersionCommand
import org.bukkit.help.HelpTopicFactory
import org.bukkit.help.IndexHelpTopic

class HelpMapTest : ShouldSpec({
    lateinit var server: ServerMock
    lateinit var helpMap: HelpMapMock

    beforeTest {
        server = MockPaper.mock()
        helpMap = server.helpMap
    }

    should("lookup") {
        val indexHelpTopic = IndexHelpTopic("test", "shortText", "mockpaper.perm", listOf())
        helpMap.addTopic(indexHelpTopic)

        helpMap.getHelpTopic("test") shouldBe indexHelpTopic
    }

    should("factory") {
        val factory = HelpTopicFactory { _: VersionCommand ->
            IndexHelpTopic("", "shortText", "mockpaper.perm", listOf())
        }
        helpMap.registerHelpTopicFactory<VersionCommand>(factory)

        helpMap.assertRegistered(factory)
    }

    should("factory with incorrect type") {
        shouldThrow<IllegalArgumentException> {
            helpMap.registerHelpTopicFactory<Any> {
                IndexHelpTopic("", "shortText", "mockpaper.perm", listOf())
            }
        }
    }
})
