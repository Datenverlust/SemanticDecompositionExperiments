package arc

import arc.dataset.Dataset
import arc.dataset.readDataset
import arc.util.userHome
import java.io.File
import kotlin.random.Random

private val solver = ArcSolver()
private const val generationSize = 20
private const val numElites = 3
private const val numParents = 10

private const val paramNum = 11

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
        .let { Genotype(dna = it) }
}

data class Genotype(
    val dna: ArcConfig,
    val fitness: Int = 0
)

private fun List<Genotype>.nextGeneration(): List<Genotype> = shuffled()
    .asSequence()
    .chunked(2)
    .filter { it.size == 2 }
    .map { parents ->
        val crossDef = (1..paramNum).map { Random.nextBoolean() }
        val invertedCrossDef = crossDef.map { it.not() }
        listOf(
            (parents[0].dna to parents[1].dna).uniformCrossover(crossDef),
            (parents[0].dna to parents[1].dna).uniformCrossover(invertedCrossDef)
        )
    }
    .flatten()
    .map { Genotype(dna = it.mutate()) }
    .toList()

private fun Pair<ArcConfig, ArcConfig>.uniformCrossover(crossDef: List<Boolean>): ArcConfig = if (crossDef.size != paramNum)
    throw RuntimeException("random list size does not match params")
else
    ArcConfig(
        startActivation = if (crossDef[0]) first.startActivation else second.startActivation,
        threshold = if (crossDef[1]) first.threshold else second.threshold,
        synonymLinkWeight = if (crossDef[2]) first.synonymLinkWeight else second.synonymLinkWeight,
        antonymLinkWeight = if (crossDef[3]) first.antonymLinkWeight else second.antonymLinkWeight,
        definitionLinkWeight = if (crossDef[4]) first.definitionLinkWeight else second.definitionLinkWeight,
        hyponymLinkWeight = if (crossDef[5]) first.hyponymLinkWeight else second.hyponymLinkWeight,
        hypernymLinkWeight = if (crossDef[6]) first.hypernymLinkWeight else second.hypernymLinkWeight,
        meronymLinkWeight = if (crossDef[7]) first.meronymLinkWeight else second.meronymLinkWeight,
        syntaxLinkWeight = if (crossDef[8]) first.syntaxLinkWeight else second.syntaxLinkWeight,
        namedEntityLinkWeight = if (crossDef[9]) first.namedEntityLinkWeight else second.namedEntityLinkWeight,
        semanticRoleLinkWeight = if (crossDef[10]) first.semanticRoleLinkWeight else second.semanticRoleLinkWeight
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
    val results = map { it.copy(fitness = it.dna.fitness(dataSet)) }
        .sortedByDescending { it.fitness }
        .also { genotypes -> println(genotypes.map { it.fitness }) }
    val elite = results.take(numElites)
    val parents = results.take(numParents)
    val nextGeneration = parents.nextGeneration()
    val numRandoms = generationSize - numElites - (numParents / 2*2)
    return elite.plus(nextGeneration)
        .plus(buildRandomGeneration(numRandoms))
}

fun main() {
    val resultDir = File(userHome("Dokumente"), "generations_arc").also { it.mkdirs() }
    var generation = buildRandomGeneration(generationSize)
    var bestGenotype: Genotype = generation.first()

    val fullDataSet = readDataset(Dataset.ADVERSIAL_TEST)!!.asSequence()
    repeat(10) { shuffleIndex ->
        val chunkSize = 10
        fullDataSet.shuffled()
            .chunked(chunkSize)
            .filter { it.size == chunkSize }
            .forEachIndexed { chunkIndex, dataSet ->
                //initial test last time's best genotype and fill the cache
                dataSet.asSequence().map { task -> solver.invoke(task, bestGenotype.dna) }
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
