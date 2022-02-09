package land.vani.mockpaper.scheduler

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.matchers.longs.shouldBeExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import land.vani.mockpaper.MockPaper
import land.vani.mockpaper.MockPlugin
import land.vani.mockpaper.ServerMock
import kotlin.time.Duration.Companion.seconds

class BukkitSchedulerMockTest : ShouldSpec({
    lateinit var server: ServerMock
    lateinit var scheduler: BukkitSchedulerMock
    lateinit var plugin: MockPlugin

    beforeTest {
        server = MockPaper.mock()
        scheduler = BukkitSchedulerMock(server)
        plugin = server.pluginManager.createMockPlugin()
    }

    should("currentTick") {
        scheduler.currentTick shouldBeExactly 0
        scheduler.performTicks()

        scheduler.currentTick shouldBeExactly 1
        scheduler.performTicks(2)

        scheduler.currentTick shouldBeExactly 3
    }

    should("runTask") {
        var executed = false
        val task = { executed = true }
        scheduler.runTask(plugin, task)

        executed shouldBe false
        scheduler.performTicks()

        executed shouldBe true
    }

    should("runTaskLater") {
        var executed = false
        val callback = { executed = true }
        scheduler.runTaskLater(plugin, callback, 20)

        executed shouldBe false
        scheduler.performTicks(10)

        executed shouldBe false
        scheduler.performTicks(20)

        executed shouldBe true
    }

    should("runTaskTimer") {
        var count = 0
        val callback = { count += 1 }
        val task = scheduler.runTaskTimer(plugin, callback, 10, 2)

        scheduler.performTicks(9)
        count shouldBeExactly 0

        scheduler.performTicks()
        count shouldBeExactly 1

        scheduler.performTicks()
        count shouldBeExactly 1

        scheduler.performTicks()
        count shouldBeExactly 2

        task.cancel()
        scheduler.performTicks(2)

        count shouldBeExactly 2
    }

    xshould("runTaskTimer with self cancelling") {
        var count = 0
        scheduler.runTaskTimer(plugin, { it ->
            count++
            if (count == 2) {
                it.cancel()
            }
        }, 1, 1)

        count shouldBeExactly 0
        scheduler.performTicks()

        count shouldBeExactly 1
        scheduler.performTicks()

        count shouldBeExactly 2
        scheduler.performTicks()

        count shouldBeExactly 2
    }

    should("runTaskTimer with zero delay") {
        var count = 0
        val callback = { count += 1 }
        scheduler.runTaskTimer(plugin, callback, 0, 2)

        count shouldBeExactly 0
        scheduler.performTicks()

        count shouldBeExactly 1
    }

    xshould("runTaskTimerAsynchronously executed on separate thread") {
        val mainThread = Thread.currentThread()
        var count = 0

        scheduler.runTaskTimerAsynchronously(
            plugin,
            { it ->
                count++
                Thread.currentThread() shouldNotBe mainThread
                if (count == 2) {
                    it.cancel()
                }
                runBlocking {
                    delay(3.seconds)
                }
            },
            2,
            1
        )

        count shouldBeExactly 0

        scheduler.performTicks()
        count shouldBeExactly 0

        scheduler.performTicks()
        delay(3.seconds)
        count shouldBeExactly 1

        scheduler.performTicks()
        delay(3.seconds)
        count shouldBeExactly 2

        scheduler.performTicks()
        delay(3.seconds)
        count shouldBeExactly 2
    }

    should("cancelling all task by plugin") {
        scheduler.queuedAsyncTaskCount shouldBeExactly 0

        scheduler.runTaskLaterAsynchronously(
            plugin,
            Runnable { },
            5
        )
        scheduler.runTaskLaterAsynchronously(
            plugin,
            Runnable { },
            10
        )
        scheduler.runTaskLaterAsynchronously(
            plugin,
            Runnable { },
            5
        )

        scheduler.queuedAsyncTaskCount shouldBeExactly 3

        scheduler.cancelTasks(plugin)
        scheduler.queuedAsyncTaskCount shouldBeExactly 0
    }
})
