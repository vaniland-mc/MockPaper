package land.vani.mockpaper.scheduler

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.matchers.longs.shouldBeExactly
import io.kotest.matchers.shouldBe
import land.vani.mockpaper.MockPaper
import land.vani.mockpaper.MockPlugin
import land.vani.mockpaper.ServerMock
import java.util.concurrent.CancellationException

class ScheduledTaskTest : ShouldSpec({
    lateinit var server: ServerMock
    lateinit var plugin: MockPlugin

    beforeTest {
        server = MockPaper.mock()
        plugin = server.pluginManager.createMockPlugin()
    }

    should("scheduled tick") {
        val task = ScheduledTaskMock(0, plugin, true, 5) {}
        task.scheduledTick shouldBeExactly 5
    }

    should("runnable") {
        val runnable = Runnable { }
        val task = ScheduledTaskMock(0, plugin, true, 0, runnable)

        task.runnable shouldBe runnable
    }

    should("task id") {
        val task = ScheduledTaskMock(5, plugin, true, 0) {}

        task.taskId shouldBeExactly 5
    }

    context("isSync") {
        should("isSync is true") {
            val task = ScheduledTaskMock(0, plugin, true, 0) {}

            task.isSync shouldBe true
        }

        should("isSync is false") {
            val task = ScheduledTaskMock(0, plugin, false, 0) {}

            task.isSync shouldBe false
        }
    }

    should("setScheduledTick") {
        val task = ScheduledTaskMock(0, plugin, true, 5) {}

        task.scheduledTick shouldBeExactly 5

        task.scheduledTick = 20
        task.scheduledTick shouldBeExactly 20
    }

    context("cancel") {
        should("isCanceled is default false") {
            val task = ScheduledTaskMock(0, plugin, true, 0) {}

            task.isCancelled shouldBe false
        }

        should("cancel") {
            val task = ScheduledTaskMock(0, plugin, true, 0) {}
            task.cancel()

            task.isCancelled shouldBe true
        }
    }

    context("run") {
        should("not cancelled") {
            var executed = false
            val task = ScheduledTaskMock(0, plugin, true, 0) {
                executed = true
            }
            task.run()

            executed shouldBe true
        }

        should("cancelled") {
            val task = ScheduledTaskMock(0, plugin, true, 0) {}
            task.cancel()

            shouldThrow<CancellationException> {
                task.run()
            }
        }
    }

    context("onCancel") {
        should("cancelled") {
            var executed = false
            val task = ScheduledTaskMock(0, plugin, true, 0) {}
            task.onCancel {
                executed = true
            }
            task.cancel()

            executed shouldBe true
        }

        should("not executed") {
            var executed = false
            val task = ScheduledTaskMock(0, plugin, true, 0) {}
            task.onCancel {
                executed = true
            }

            executed shouldBe false
        }
    }
})
