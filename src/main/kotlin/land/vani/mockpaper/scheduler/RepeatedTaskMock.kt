package land.vani.mockpaper.scheduler

import org.bukkit.plugin.Plugin
import org.jetbrains.annotations.VisibleForTesting

class RepeatedTaskMock(
    id: Int,
    plugin: Plugin,
    isSync: Boolean,
    scheduledTick: Long,
    @VisibleForTesting
    val period: Long,
    runnable: Runnable,
) : ScheduledTaskMock(id, plugin, isSync, scheduledTick, runnable) {
    @VisibleForTesting
    fun updateScheduledTick() {
        scheduledTick += period
    }
}
