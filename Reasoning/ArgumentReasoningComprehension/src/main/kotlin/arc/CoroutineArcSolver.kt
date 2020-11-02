package arc

import arc.util.printProgress
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import java.util.Collections

private val kLogger = KotlinLogging.logger { }

class CoroutineArcSolver(
    val numCoroutines: Int,
    val solver: ArcSolver
) {
    fun startAsync(tasks: List<ArcTask>, config: ArcConfig): List<ArcResult> = runBlocking {
        val componentChannel = Channel<ArcTask>()
        val results = Collections.synchronizedList(ArrayList<ArcResult>(tasks.size))
        val runners = (1..numCoroutines).map {
            async(Dispatchers.IO) {
                solveTasks(componentChannel, results, config)
            }
        }
        tasks.asSequence()
            .printProgress(100, tasks.size) { msg -> kLogger.info { msg } }
            .forEach { componentChannel.send(it) }
        componentChannel.close()
        runners.forEach { it.await() }
        return@runBlocking results
    }

    internal suspend fun solveTasks(
        taskChannel: Channel<ArcTask>,
        results: MutableList<ArcResult>,
        config: ArcConfig
    ) {
        for (task in taskChannel) {
            results.add(solver.invoke(task, config))
        }
    }
}