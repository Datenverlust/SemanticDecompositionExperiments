package arc

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import java.util.Collections

class ParallelArcSolver(
    val numThreads: Int,
    val arcSolverFactory: () -> ((ArcTask) -> ArcLabel)
) {
    fun startAsync(tasks: List<ArcTask>): Map<String, ArcLabel> = runBlocking {
        val componentChannel = Channel<ArcTask>()
        val labels = Collections.synchronizedMap(HashMap<String, ArcLabel>(tasks.size))
        val runners = (1..numThreads).map {
            async(Dispatchers.IO) {
                buildGraphs(componentChannel, labels)
            }
        }
        tasks.forEach { componentChannel.send(it) }
        componentChannel.close()
        runners.forEach { it.await() }
        return@runBlocking labels
    }

    internal suspend fun buildGraphs(taskChannel: Channel<ArcTask>, labels: MutableMap<String, ArcLabel>) {
        val arcSolver = arcSolverFactory.invoke()
        for (task in taskChannel) {
            val label = arcSolver.invoke(task)
            labels[task.id] = label
        }
    }
}