package land.vani.mockpaper.scheduler

import land.vani.mockpaper.UnimplementedOperationException
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitScheduler
import org.bukkit.scheduler.BukkitTask
import org.bukkit.scheduler.BukkitWorker
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.Callable
import java.util.concurrent.Executor
import java.util.concurrent.Future
import java.util.function.Consumer
import kotlin.math.max

class BukkitSchedulerMock : BukkitScheduler {
    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger("BukkitSchedulerMock")
    }

    private val tasks = TaskList()

    private var lastId: Int = 0
    private var currentTick: Long = 0

    override fun scheduleSyncDelayedTask(plugin: Plugin, task: Runnable, delay: Long): Int =
        runTaskLater(plugin, task, delay).taskId.also {
            LOGGER.warn("Consider using runTaskLater instead of scheduleSyncDelayTask")
        }

    override fun scheduleSyncDelayedTask(plugin: Plugin, task: BukkitRunnable, delay: Long): Int =
        runTaskLater(plugin, task as Runnable, delay).taskId.also {
            LOGGER.warn("Consider using runTaskLater instead of scheduleSyncDelayTask")
        }

    override fun scheduleSyncDelayedTask(plugin: Plugin, task: Runnable): Int =
        runTask(plugin, task).taskId.also {
            LOGGER.warn("Consider using runTask instead of scheduleSyncDelayedTask")
        }

    override fun scheduleSyncDelayedTask(plugin: Plugin, task: BukkitRunnable): Int =
        runTask(plugin, task as Runnable).taskId.also {
            LOGGER.warn("Consider using runTask instead of scheduleSyncDelayedTask")
        }

    override fun scheduleSyncRepeatingTask(plugin: Plugin, task: Runnable, delay: Long, period: Long): Int =
        runTaskTimer(plugin, task, delay, period).taskId.also {
            LOGGER.warn("Consider using runTaskTimer instead of scheduleSyncRepeatingTask")
        }

    override fun scheduleSyncRepeatingTask(plugin: Plugin, task: BukkitRunnable, delay: Long, period: Long): Int =
        runTaskTimer(plugin, task as Runnable, delay, period).taskId.also {
            LOGGER.warn("Consider using runTaskTimer instead of scheduleSyncRepeatingTask")
        }

    override fun scheduleAsyncDelayedTask(plugin: Plugin, task: Runnable, delay: Long): Int =
        runTaskLaterAsynchronously(plugin, task, delay).taskId.also {
            LOGGER.warn("Consider using runTaskLaterAsynchronously instead of scheduleAsyncDelayedTask")
        }

    override fun scheduleAsyncDelayedTask(plugin: Plugin, task: Runnable): Int =
        runTaskAsynchronously(plugin, task).taskId.also {
            LOGGER.warn("Consider using runTaskAsynchronously instead of scheduleAsyncDelayedTask")
        }

    override fun scheduleAsyncRepeatingTask(plugin: Plugin, task: Runnable, delay: Long, period: Long): Int =
        runTaskTimerAsynchronously(plugin, task, delay, period).taskId.also {
            LOGGER.warn("Consider using runTaskTimerAsynchronously instead of scheduleAsyncRepeatingTask")
        }

    override fun <T : Any?> callSyncMethod(plugin: Plugin, task: Callable<T>): Future<T> {
        throw UnimplementedOperationException()
    }

    override fun cancelTask(taskId: Int) {
        tasks.cancelTask(taskId)
    }

    override fun cancelTasks(plugin: Plugin) {
        tasks.currentTasks.filter { it.owner == plugin }
            .forEach { it.cancel() }
    }

    override fun isCurrentlyRunning(taskId: Int): Boolean {
        throw UnimplementedOperationException()
    }

    override fun isQueued(taskId: Int): Boolean =
        tasks.currentTasks.any { it.taskId == taskId && !it.isCancelled }

    override fun getActiveWorkers(): List<BukkitWorker> {
        throw UnimplementedOperationException()
    }

    override fun getPendingTasks(): List<BukkitTask> {
        throw UnimplementedOperationException()
    }

    override fun runTask(plugin: Plugin, task: Runnable): BukkitTask =
        runTaskLater(plugin, task, 1)

    override fun runTask(plugin: Plugin, task: Consumer<BukkitTask>) {
        throw UnimplementedOperationException()
    }

    override fun runTask(plugin: Plugin, task: BukkitRunnable): BukkitTask =
        runTask(plugin, task as Runnable)

    override fun runTaskAsynchronously(plugin: Plugin, task: Runnable): BukkitTask =
        runTaskLaterAsynchronously(plugin, task, 1)

    override fun runTaskAsynchronously(plugin: Plugin, task: Consumer<BukkitTask>) {
        throw UnimplementedOperationException()
    }

    override fun runTaskAsynchronously(plugin: Plugin, task: BukkitRunnable): BukkitTask =
        runTaskAsynchronously(plugin, task as Runnable)

    override fun runTaskLater(plugin: Plugin, task: Runnable, delay: Long): BukkitTask {
        val fixedDelay = max(delay, 1)
        val scheduledTask = ScheduledTaskMock(
            lastId++,
            plugin,
            true,
            currentTick + fixedDelay,
            task
        )
        return scheduledTask.also { tasks.addTask(it) }
    }

    override fun runTaskLater(plugin: Plugin, task: Consumer<BukkitTask>, delay: Long) {
        throw UnimplementedOperationException()
    }

    override fun runTaskLater(plugin: Plugin, task: BukkitRunnable, delay: Long): BukkitTask =
        runTaskLater(plugin, task as Runnable, delay)

    override fun runTaskLaterAsynchronously(plugin: Plugin, task: Runnable, delay: Long): BukkitTask =
        ScheduledTaskMock(
            lastId++,
            plugin,
            false,
            currentTick + delay,
            task
        ).also { tasks.addTask(it) }

    override fun runTaskLaterAsynchronously(plugin: Plugin, task: Consumer<BukkitTask>, delay: Long) {
        throw UnimplementedOperationException()
    }

    override fun runTaskLaterAsynchronously(plugin: Plugin, task: BukkitRunnable, delay: Long): BukkitTask =
        runTaskLaterAsynchronously(plugin, task as Runnable, delay)

    override fun runTaskTimer(plugin: Plugin, task: Runnable, delay: Long, period: Long): BukkitTask {
        val fixedDelay = max(1, delay)
        return RepeatedTaskMock(
            lastId++,
            plugin,
            true,
            currentTick + fixedDelay,
            period,
            task
        ).also { tasks.addTask(it) }
    }

    override fun runTaskTimer(plugin: Plugin, task: Consumer<BukkitTask>, delay: Long, period: Long) {
        throw UnimplementedOperationException()
    }

    override fun runTaskTimer(plugin: Plugin, task: BukkitRunnable, delay: Long, period: Long): BukkitTask =
        runTaskTimer(plugin, task as Runnable, delay, period)

    override fun runTaskTimerAsynchronously(plugin: Plugin, task: Runnable, delay: Long, period: Long): BukkitTask =
        RepeatedTaskMock(
            lastId++,
            plugin,
            false,
            currentTick + delay,
            period,
            task
        ).also { tasks.addTask(it) }

    override fun runTaskTimerAsynchronously(plugin: Plugin, task: Consumer<BukkitTask>, delay: Long, period: Long) {
        throw UnimplementedOperationException()
    }

    override fun runTaskTimerAsynchronously(
        plugin: Plugin,
        task: BukkitRunnable,
        delay: Long,
        period: Long,
    ): BukkitTask = runTaskTimerAsynchronously(plugin, task as Runnable, delay, period)

    override fun getMainThreadExecutor(plugin: Plugin): Executor {
        throw UnimplementedOperationException()
    }
}
