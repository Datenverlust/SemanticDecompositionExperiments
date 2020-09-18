package arc

import arc.dataset.Dataset
import arc.dataset.readDataset
import arc.util.userHome
import java.io.File
import kotlin.random.Random

private val solver = ArcSolver()
private const val generationSize = 20

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
        .let {
            Genotype(
                dna = it to it,
                phenotype = it
            )
        }
}

data class Genotype(
    val dna: Pair<ArcConfig, ArcConfig>,
    val phenotype: ArcConfig,
    val fitness: Int = 0
)

private fun List<Genotype>.nextGeneration(): List<Genotype> = shuffled().zipWithNext().map { (first, second) ->
    first.dna.second.mixWith(second.dna.first).mutate() to first.dna.first.mixWith(second.dna.second).mutate()
}
    .map {
        Genotype(
            dna = it,
            phenotype = it.first.mixWith(it.second)
        )
    }

private fun ArcConfig.mixWith(config: ArcConfig) = ArcConfig(
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

private fun ArcConfig.mutate(mutateProb: Double = 0.1): ArcConfig = ArcConfig(
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

private fun List<Genotype>.evolution(dataSet: List<ArcTask>): List<Genotype> {
    val results = map { it.copy(fitness = it.phenotype.fitness(dataSet)) }
        .sortedByDescending { it.fitness }
        .also { genotypes -> println(genotypes.map { it.fitness }) }
    val elite = results.take(3)
    val parents = results.take(10)
    return elite.plus(parents.nextGeneration())
        .plus(buildRandomGeneration(7))
}

fun main() {
    val resultDir = File(userHome("Dokumente"), "generations_arc").also { it.mkdirs() }
    var generation = buildRandomGeneration(generationSize)
    var bestGenotype: Genotype = generation.first()

    val fullDataSet = readDataset(Dataset.ADVERSIAL_TEST)!!.asSequence()
    repeat(10) { shuffleIndex ->
        fullDataSet.shuffled().chunked(400).filter { it.size == 400 }.forEachIndexed { chunkIndex, dataSet ->
            println("size of next dataset is ${dataSet.size}")
            //initial test last time's best genotype
            dataSet.asSequence().map { task -> solver.invoke(task, bestGenotype.phenotype) }
                .filter { it.correctLabel == it.foundLabel }
                .count()
                .let { it.toDouble() / dataSet.size }
                .let { println("Score of best genotype from last chunk: $it") }
            var unchangedBestCount = 0
            //do the evolution
            while (unchangedBestCount < 10) {
                generation = generation.evolution(dataSet)
                if (bestGenotype == generation.first()) {
                    unchangedBestCount = unchangedBestCount.inc()
                } else {
                    bestGenotype = generation.first()
                    unchangedBestCount = 0
                }
            }
            File(resultDir, "shuffle${shuffleIndex + 1}_chunk${chunkIndex + 1}.txt").writeText(generation.first().toString())
            solver.clearCaches()
        }
    }
}
