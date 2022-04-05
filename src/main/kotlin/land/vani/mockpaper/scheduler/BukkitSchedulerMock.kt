package land.vani.mockpaper.scheduler

import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import land.vani.mockpaper.ServerMock
import land.vani.mockpaper.UnimplementedOperationException
import org.bukkit.event.Event
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitScheduler
import org.bukkit.scheduler.BukkitTask
import org.bukkit.scheduler.BukkitWorker
import org.jetbrains.annotations.VisibleForTesting
import java.util.concurrent.Callable
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.SynchronousQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.function.Consumer
import java.util.logging.Logger
import kotlin.math.max

class BukkitSchedulerMock(
    private val server: ServerMock,
) : BukkitScheduler {
    companion object {
        private val LOGGER: Logger = Logger.getLogger("BukkitSchedulerMock")
    }

    private val mainThreadExecutor = ThreadPoolExecutor(
        0,
        Integer.MAX_VALUE,
        60L,
        TimeUnit.SECONDS,
        SynchronousQueue(),
    )
    private val asyncEventExecutor = Executors.newCachedThreadPool()

    private val tasks = TaskList()

    private var lastId: Int = 0
    var currentTick: Long = 0
        private set

    @VisibleForTesting
    var executorTimeout: Long = 60000
        set(value) {
            require(value > 0)
            field = value
        }

    override fun scheduleSyncDelayedTask(plugin: Plugin, task: Runnable, delay: Long): Int =
        runTaskLater(plugin, task, delay).taskId.also {
            LOGGER.warning("Consider using runTaskLater instead of scheduleSyncDelayTask")
        }

    @Deprecated("use BukkitRunnable#runTaskLater", ReplaceWith("task.runTaskLater(plugin, delay)"))
    override fun scheduleSyncDelayedTask(plugin: Plugin, task: BukkitRunnable, delay: Long): Int =
        runTaskLater(plugin, task as Runnable, delay).taskId.also {
            LOGGER.warning("Consider using runTaskLater instead of scheduleSyncDelayTask")
        }

    override fun scheduleSyncDelayedTask(plugin: Plugin, task: Runnable): Int =
        runTask(plugin, task).taskId.also {
            LOGGER.warning("Consider using runTask instead of scheduleSyncDelayedTask")
        }

    @Deprecated("use BukkitRunnable#runTask", ReplaceWith("task.runTask(plugin)"))
    override fun scheduleSyncDelayedTask(plugin: Plugin, task: BukkitRunnable): Int =
        runTask(plugin, task as Runnable).taskId.also {
            LOGGER.warning("Consider using runTask instead of scheduleSyncDelayedTask")
        }

    override fun scheduleSyncRepeatingTask(plugin: Plugin, task: Runnable, delay: Long, period: Long): Int =
        runTaskTimer(plugin, task, delay, period).taskId.also {
            LOGGER.warning("Consider using runTaskTimer instead of scheduleSyncRepeatingTask")
        }

    @Deprecated("use BukkitRunnable#runTaskTimer", ReplaceWith("task.runTaskTimer(plugin, delay, period)"))
    override fun scheduleSyncRepeatingTask(plugin: Plugin, task: BukkitRunnable, delay: Long, period: Long): Int =
        runTaskTimer(plugin, task as Runnable, delay, period).taskId.also {
            LOGGER.warning("Consider using runTaskTimer instead of scheduleSyncRepeatingTask")
        }

    @Deprecated(
        "This name is misleading, as it does not schedule \"a sync\" task," +
            " but rather, \"an async\" task"
    )
    override fun scheduleAsyncDelayedTask(plugin: Plugin, task: Runnable, delay: Long): Int =
        runTaskLaterAsynchronously(plugin, task, delay).taskId.also {
            LOGGER.warning("Consider using runTaskLaterAsynchronously instead of scheduleAsyncDelayedTask")
        }

    @Deprecated(
        "This name is misleading, as it does not schedule \"a sync\" task," +
            " but rather, \"an async\" task"
    )
    override fun scheduleAsyncDelayedTask(plugin: Plugin, task: Runnable): Int =
        runTaskAsynchronously(plugin, task).taskId.also {
            LOGGER.warning("Consider using runTaskAsynchronously instead of scheduleAsyncDelayedTask")
        }

    @Deprecated(
        "This name is misleading, as it does not schedule \"a sync\" task," +
            " but rather, \"an async\" task"
    )
    override fun scheduleAsyncRepeatingTask(plugin: Plugin, task: Runnable, delay: Long, period: Long): Int =
        runTaskTimerAsynchronously(plugin, task, delay, period).taskId.also {
            LOGGER.warning("Consider using runTaskTimerAsynchronously instead of scheduleAsyncRepeatingTask")
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

    @Deprecated("use BukkitRunnable#runTask", ReplaceWith("task.runTask(plugin)"))
    override fun runTask(plugin: Plugin, task: BukkitRunnable): BukkitTask =
        runTask(plugin, task as Runnable)

    override fun runTaskAsynchronously(plugin: Plugin, task: Runnable): BukkitTask =
        ScheduledTaskMock(lastId++, plugin, false, currentTick, task).also {
            mainThreadExecutor.execute(wrapTask(it))
        }

    override fun runTaskAsynchronously(plugin: Plugin, task: Consumer<BukkitTask>) {
        throw UnimplementedOperationException()
    }

    @Deprecated("use BukkitRunnable#runTaskAsynchronously", ReplaceWith("task.runTaskAsynchronously(plugin)"))
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

    @Deprecated("use BukkitRunnable#runTaskLater", ReplaceWith("task.runTaskLater(plugin)"))
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

    @Deprecated("use BukkitRunnable#runTaskAsynchronously", ReplaceWith("task.RunTaskLaterAsynchronously(plugin"))
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

    @Deprecated("use BukkitRunnable#runTaskTimer", ReplaceWith("task.runTaskTimer(plugin, delay, period)"))
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

    @Deprecated(
        "use BukkitRunnable#runTaskTimerAsynchronously",
        ReplaceWith("task.runTaskTimerAsynchronously(plugin, delay, period)")
    )
    override fun runTaskTimerAsynchronously(
        plugin: Plugin,
        task: BukkitRunnable,
        delay: Long,
        period: Long,
    ): BukkitTask = runTaskTimerAsynchronously(plugin, task as Runnable, delay, period)

    override fun getMainThreadExecutor(plugin: Plugin): Executor = mainThreadExecutor

    private fun wrapTask(task: ScheduledTaskMock): () -> Unit = {
        task.isRunning = true
        task.run()
        task.isRunning = false
    }

    /**
     * Perform one tick on the server.
     */
    @VisibleForTesting
    fun performTicks(tick: Int = 1) {
        repeat(tick) {
            currentTick++
            val oldTasks = tasks.currentTasks

            oldTasks.filter { it.scheduledTick == currentTick && !it.isCancelled }.forEach { task ->
                if (task.isSync) {
                    wrapTask(task)()
                } else {
                    mainThreadExecutor.submit(wrapTask(task))
                }

                if (task is RepeatedTaskMock && !task.isCancelled) {
                    task.updateScheduledTick()
                    tasks.addTask(task)
                }
            }
        }
    }

    @get:VisibleForTesting
    val queuedAsyncTaskCount: Int
        get() = tasks.currentTasks
            .filterNot { it.isSync || it.isCancelled || it.isRunning }
            .count()

    /**
     * Call [event] from async thread.
     */
    @VisibleForTesting
    fun callEventAsync(event: Event): Future<*> = asyncEventExecutor.submit {
        server.pluginManager.callEvent(event)
    }

    @VisibleForTesting
    @JvmSynthetic
    suspend fun callEventAsyncSuspend(event: Event) {
        withContext(asyncEventExecutor.asCoroutineDispatcher()) {
            server.pluginManager.callEvent(event)
        }
    }

    private suspend fun waitAsyncTasks(waiter: suspend (Long) -> Unit) {
        while (tasks.currentTasks.isNotEmpty()) {
            performTicks()
        }

        val systemTime = System.currentTimeMillis()
        while (mainThreadExecutor.activeCount > 0) {
            waiter(10)

            if (systemTime + executorTimeout > System.currentTimeMillis()) {
                continue
            }

            tasks.currentTasks.forEach { task ->
                if (!task.isRunning) {
                    return@forEach
                }

                task.cancel()
                throw AsyncTaskForceCancellationException(
                    "Forced cancellation of task owned by ${task.owner.name}"
                )
            }
            mainThreadExecutor.shutdownNow()
        }
    }

    /**
     * Waits until all asynchronous tasks have finished executing. If you have an asynchronous task that runs
     * indefinitely, this function will never return.
     */
    @Suppress("BlockingMethodInNonBlockingContext")
    @VisibleForTesting
    fun waitAsyncTaskFinished() {
        runBlocking {
            waitAsyncTasks {
                try {
                    Thread.sleep(it)
                } catch (ex: InterruptedException) {
                    Thread.currentThread().interrupt()
                    return@waitAsyncTasks
                }
            }
        }
    }

    /**
     * Waits until all asynchronous tasks have finished executing. If you have an asynchronous task that runs
     * indefinitely, this function will never return.
     */
    @VisibleForTesting
    @JvmSynthetic
    suspend fun waitAsyncTaskFinishedSuspend() {
        waitAsyncTasks {
            delay(it)
        }
    }

    /**
     * Shut the scheduler down.
     */
    @VisibleForTesting
    fun shutdown() {
        mainThreadExecutor.shutdown()
        asyncEventExecutor.shutdownNow()
    }
}
