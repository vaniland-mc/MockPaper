package land.vani.mockpaper.scheduler

import org.bukkit.plugin.Plugin

class RepeatedTaskMock(
    id: Int,
    plugin: Plugin,
    isSync: Boolean,
    scheduledTick: Long,
    val period: Long,
    runnable: Runnable,
) : ScheduledTaskMock(id, plugin, isSync, scheduledTick, runnable) {
    fun updateScheduledTick() {
        scheduledTick += period
    }
}
