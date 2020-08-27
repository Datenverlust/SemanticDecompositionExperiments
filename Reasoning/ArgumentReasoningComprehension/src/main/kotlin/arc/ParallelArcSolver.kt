package arc

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import java.util.Collections

class ParallelArcSolver(
    val numCoroutines: Int,
    val config :ArcConfig,
    val arcSolverFactory: () -> ((ArcTask, ArcConfig) -> ArcResult)
) {
    fun startAsync(tasks: List<ArcTask>): List<ArcResult> = runBlocking {
        val componentChannel = Channel<ArcTask>()
        val results = Collections.synchronizedList(ArrayList<ArcResult>(tasks.size))
        val runners = (1..numCoroutines).map {
            async(Dispatchers.IO) {
                buildGraphs(componentChannel, results)
            }
        }
        tasks.forEach { componentChannel.send(it) }
        componentChannel.close()
        runners.forEach { it.await() }
        return@runBlocking results
    }

    internal suspend fun buildGraphs(
        taskChannel: Channel<ArcTask>,
        results: MutableList<ArcResult>
    ) {
        val arcSolver = arcSolverFactory.invoke()
        for (task in taskChannel) {
            val result = arcSolver.invoke(task, config)
            results.add(result)
        }
    }
}