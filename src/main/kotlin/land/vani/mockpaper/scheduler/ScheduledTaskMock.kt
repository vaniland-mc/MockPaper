package land.vani.mockpaper.scheduler

import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitTask
import java.util.concurrent.CancellationException

open class ScheduledTaskMock(
    private val id: Int,
    private val plugin: Plugin,
    private val isSync: Boolean,
    var scheduledTick: Long,
    private val runnable: Runnable,
) : BukkitTask {
    var isRunning: Boolean = false
    private var isCancelled: Boolean = false

    private val cancelListeners: MutableList<() -> Unit> = mutableListOf()

    override fun getTaskId(): Int = id

    override fun getOwner(): Plugin = plugin

    override fun isSync(): Boolean = isSync

    override fun isCancelled(): Boolean = isCancelled

    override fun cancel() {
        isCancelled = true
        cancelListeners.forEach { it() }
    }

    fun run() {
        if (isCancelled) throw CancellationException("Task is cancelled")

        runnable.run()
    }

    /**
     * Adds a callback which is executed when the task is cancelled.
     */
    fun onCancel(block: () -> Unit) {
        cancelListeners += block
    }
}
