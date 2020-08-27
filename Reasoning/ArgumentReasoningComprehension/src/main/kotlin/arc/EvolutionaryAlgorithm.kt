package arc

import arc.dataset.Dataset
import arc.dataset.readDataset
import arc.util.userHome
import java.io.File
import kotlin.random.Random

private val solver = ArcSolver()
private const val generationSize = 10

private fun ArcConfig.fitness(dataSet: List<ArcTask>) = dataSet.asSequence()
    .map { solver.invoke(it, this) }
    .filter { it.correctLabel == it.foundLabel }
    .count()

private fun buildRandomGeneration(size: Int) = (1..size).map {
    ArcConfig(
        startActivation = Random.nextDouble(0.0, 100.0),
        threshold = Random.nextDouble(0.0, 1.0),
        synonymLinkWeight = Random.nextDouble(0.0, 1.0),
        antonymLinkWeight = Random.nextDouble(-1.0, 1.0),
        definitionLinkWeight = Random.nextDouble(0.0, 1.0),
        hyponymLinkWeight = Random.nextDouble(0.0, 1.0),
        hypernymLinkWeight = Random.nextDouble(0.0, 1.0),
        meronymLinkWeight = Random.nextDouble(0.0, 1.0),
        syntaxLinkWeight = Random.nextDouble(0.0, 1.0),
        namedEntityLinkWeight = Random.nextDouble(0.0, 1.0),
        semanticRoleLinkWeight = Random.nextDouble(0.0, 1.0)
    )
}

private fun List<ArcConfig>.mix(): List<ArcConfig> = elementPairs(this).toList()
    .map { (first, second) -> first.mix(second).mutate(0.2) }

private fun <T> elementPairs(arr: List<T>): Sequence<Pair<T, T>> = sequence {
    for (i in 0 until arr.size - 1)
        for (j in i + 1 until arr.size)
            yield(arr[i] to arr[j])
}

private fun ArcConfig.mix(config: ArcConfig) = ArcConfig(
    startActivation = if (Random.nextBoolean()) config.startActivation else startActivation,
    threshold = if (Random.nextBoolean()) config.threshold else threshold,
    synonymLinkWeight = if (Random.nextBoolean()) config.synonymLinkWeight else synonymLinkWeight,
    antonymLinkWeight = if (Random.nextBoolean()) config.antonymLinkWeight else antonymLinkWeight,
    definitionLinkWeight = if (Random.nextBoolean()) config.definitionLinkWeight else definitionLinkWeight,
    hyponymLinkWeight = if (Random.nextBoolean()) config.hyponymLinkWeight else hyponymLinkWeight,
    hypernymLinkWeight = if (Random.nextBoolean()) config.hypernymLinkWeight else hypernymLinkWeight,
    meronymLinkWeight = if (Random.nextBoolean()) config.meronymLinkWeight else meronymLinkWeight,
    syntaxLinkWeight = if (Random.nextBoolean()) config.syntaxLinkWeight else syntaxLinkWeight,
    namedEntityLinkWeight = if (Random.nextBoolean()) config.namedEntityLinkWeight else namedEntityLinkWeight,
    semanticRoleLinkWeight = if (Random.nextBoolean()) config.namedEntityLinkWeight else semanticRoleLinkWeight
)

private fun ArcConfig.mutate(mutateProb: Double): ArcConfig = ArcConfig(
    startActivation = if (Random.nextDouble(0.0, 1.0) < mutateProb) Random.nextDouble(0.0, 100.0) else startActivation,
    threshold = if (Random.nextDouble(0.0, 1.0) < mutateProb) Random.nextDouble(0.0, 1.0) else threshold,
    synonymLinkWeight = if (Random.nextDouble(0.0, 1.0) < mutateProb) Random.nextDouble(0.0, 1.0) else synonymLinkWeight,
    antonymLinkWeight = if (Random.nextDouble(0.0, 1.0) < mutateProb) Random.nextDouble(-1.0, 1.0) else antonymLinkWeight,
    definitionLinkWeight = if (Random.nextDouble(0.0, 1.0) < mutateProb) Random.nextDouble(0.0, 1.0) else definitionLinkWeight,
    hyponymLinkWeight = if (Random.nextDouble(0.0, 1.0) < mutateProb) Random.nextDouble(0.0, 1.0) else hyponymLinkWeight,
    hypernymLinkWeight = if (Random.nextDouble(0.0, 1.0) < mutateProb) Random.nextDouble(0.0, 1.0) else hypernymLinkWeight,
    meronymLinkWeight = if (Random.nextDouble(0.0, 1.0) < mutateProb) Random.nextDouble(0.0, 1.0) else meronymLinkWeight,
    syntaxLinkWeight = if (Random.nextDouble(0.0, 1.0) < mutateProb) Random.nextDouble(0.0, 1.0) else syntaxLinkWeight,
    namedEntityLinkWeight = if (Random.nextDouble(0.0, 1.0) < mutateProb) Random.nextDouble(0.0, 1.0) else namedEntityLinkWeight,
    semanticRoleLinkWeight = if (Random.nextDouble(0.0, 1.0) < mutateProb) Random.nextDouble(0.0, 1.0) else semanticRoleLinkWeight
)

private fun List<ArcConfig>.evolution(dataSet: List<ArcTask>): List<ArcConfig> {
    val results = map { it to it.fitness(dataSet) }
        .sortedByDescending { it.second }
        .also { resultPairs -> println(resultPairs.map { it.second }) }
    val elite = results.first().first
    val parents = results.take(4).map { it.first }
    return listOf(elite)
        .plus(parents.mix())
        .plus(buildRandomGeneration(3))
}

fun main() {
    val resultDir = File(userHome("Dokumente"), "generations_arc").also { it.mkdirs() }
    var generation = buildRandomGeneration(generationSize)
    val fullDataSet = readDataset(Dataset.ADVERSIAL_TRAIN)!!.asSequence()
    repeat(10) { shuffleIndex ->
        fullDataSet.shuffled().chunked(200).filter { it.size == 200 }.forEachIndexed { chunkIndex, dataSet ->
            //initial test last elite
            dataSet.asSequence().map { task -> solver.invoke(task, generation.first()) }
                .filter { it.correctLabel == it.foundLabel }
                .count()
                .let { println("Score of elite from last chunk: $it") }

            //do the evolution
            repeat(20) { generation = generation.evolution(dataSet) }
            File(resultDir, "shuffle${shuffleIndex + 1}_chunk${chunkIndex + 1}.txt").writeText(generation.first().toString())
            solver.clearCaches()
        }
    }
}
