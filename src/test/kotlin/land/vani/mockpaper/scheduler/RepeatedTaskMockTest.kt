package land.vani.mockpaper.scheduler

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.longs.shouldBeExactly
import land.vani.mockpaper.MockPaper
import land.vani.mockpaper.MockPlugin
import land.vani.mockpaper.ServerMock

class RepeatedTaskMockTest : ShouldSpec({
    lateinit var server: ServerMock
    lateinit var plugin: MockPlugin

    beforeTest {
        server = MockPaper.mock()
        plugin = server.pluginManager.createMockPlugin()
    }

    should("scheduledTick on start is delay") {
        val task = RepeatedTaskMock(0, plugin, true, 10, 20) {}

        task.scheduledTick shouldBeExactly 10
    }

    should("scheduledTick after updateScheduledTick") {
        val task = RepeatedTaskMock(0, plugin, true, 10, 20) {}
        task.updateScheduledTick()

        task.scheduledTick shouldBeExactly 10 + 20 * 1
    }

    should("period") {
        val task = RepeatedTaskMock(0, plugin, true, 10, 20) {}

        task.period shouldBeExactly 20
    }
})
