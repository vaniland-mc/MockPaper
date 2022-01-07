package land.vani.mockpaper.scheduler

import land.vani.mockpaper.internal.asUnmodifiable
import java.util.concurrent.ConcurrentHashMap

internal class TaskList {
    private val tasks: MutableMap<Int, ScheduledTaskMock> = ConcurrentHashMap()

    fun addTask(task: ScheduledTaskMock) {
        tasks[task.taskId] = task
    }

    val currentTasks: List<ScheduledTaskMock>
        get() = tasks.values.toList().asUnmodifiable()

    fun cancelTask(taskId: Int): Boolean = tasks[taskId]?.cancel() != null
}
