@file:Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package arc

import arc.dataset.Dataset
import arc.dataset.readDataset
import arc.util.userHome
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.coroutines.runBlocking
import java.io.File
import kotlin.random.Random

private val solver = ArcSolver()
private val parallelArcSolver = CoroutineArcSolver(10, solver)
private const val generationSize = 20
private const val numElites = 3
private const val numParents = 10
private const val paramNum = 16

private fun fitness(config: ArcConfig, dataSet: List<ArcTask>) =

    runBlocking {
//        dataSet.solveArcWithFlow(config, solver)
        parallelArcSolver.startAsync(dataSet, config)
            .let { results ->
                Fitness(
                    correct = results.filter { it.correctLabel == it.foundLabel }.count(),
                    wrong = results.filter { it.correctLabel != it.foundLabel && it.foundLabel != ArcLabel.UNKNOWN }.count(),
                    unknown = results.filter { it.foundLabel == ArcLabel.UNKNOWN }.count()
                )
            }
    }

private fun buildRandomGeneration(size: Int) = (1..size).map {
    ArcConfig(
        pulseCount = Random.nextInt(1, 100),
        startActivation = Random.nextDouble(0.0, 100.0),
        threshold = Random.nextDouble(0.0, 1.0),
        synonymLinkWeight = Random.nextDouble(0.0, 1.0),
        antonymLinkWeight = Random.nextDouble(-1.0, 1.0),
        definitionLinkWeight = Random.nextDouble(0.0, 1.0),
        definitionOfLinkWeight = Random.nextDouble(0.0, 1.0),
        hyponymLinkWeight = Random.nextDouble(0.0, 1.0),
        hypernymLinkWeight = Random.nextDouble(0.0, 1.0),
        meronymLinkWeight = Random.nextDouble(0.0, 1.0),
        holonymLinkWeight = Random.nextDouble(0.0, 1.0),
        syntaxLinkWeight = Random.nextDouble(0.0, 1.0),
        namedEntityLinkWeight = Random.nextDouble(0.0, 1.0),
        nameOfLinkWeight = Random.nextDouble(0.0, 1.0),
        semanticRoleLinkWeight = Random.nextDouble(0.0, 1.0),
        semanticRoleOfLinkWeight = Random.nextDouble(0.0, 1.0)
    )
        .let { Genotype(dna = it) }
}

data class Genotype(
    val dna: ArcConfig,
    val fitness: Fitness = Fitness()
)

data class Fitness(
    val correct: Int = 0,
    val wrong: Int = 0,
    val unknown: Int = 0
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
else {
    val iterator = crossDef.iterator()
    ArcConfig(
        pulseCount = if (iterator.next()) first.pulseCount else second.pulseCount,
        startActivation = if (iterator.next()) first.startActivation else second.startActivation,
        threshold = if (iterator.next()) first.threshold else second.threshold,
        synonymLinkWeight = if (iterator.next()) first.synonymLinkWeight else second.synonymLinkWeight,
        antonymLinkWeight = if (iterator.next()) first.antonymLinkWeight else second.antonymLinkWeight,
        definitionLinkWeight = if (iterator.next()) first.definitionLinkWeight else second.definitionLinkWeight,
        definitionOfLinkWeight = if (iterator.next()) first.definitionOfLinkWeight else second.definitionOfLinkWeight,
        hyponymLinkWeight = if (iterator.next()) first.hyponymLinkWeight else second.hyponymLinkWeight,
        hypernymLinkWeight = if (iterator.next()) first.hypernymLinkWeight else second.hypernymLinkWeight,
        meronymLinkWeight = if (iterator.next()) first.meronymLinkWeight else second.meronymLinkWeight,
        holonymLinkWeight = if (iterator.next()) first.holonymLinkWeight else second.holonymLinkWeight,
        syntaxLinkWeight = if (iterator.next()) first.syntaxLinkWeight else second.syntaxLinkWeight,
        namedEntityLinkWeight = if (iterator.next()) first.namedEntityLinkWeight else second.namedEntityLinkWeight,
        nameOfLinkWeight = if (iterator.next()) first.nameOfLinkWeight else second.nameOfLinkWeight,
        semanticRoleLinkWeight = if (iterator.next()) first.semanticRoleLinkWeight else second.semanticRoleLinkWeight,
        semanticRoleOfLinkWeight = if (iterator.next()) first.semanticRoleOfLinkWeight else second.semanticRoleOfLinkWeight
    )
}

private fun ArcConfig.mutate(mutateProb: Double = 0.1): ArcConfig = ArcConfig(
    pulseCount = if (Random.nextDouble(0.0, 1.0) < mutateProb) Random.nextInt(1, 100) else pulseCount,
    startActivation = if (Random.nextDouble(0.0, 1.0) < mutateProb) Random.nextDouble(0.0, 100.0) else startActivation,
    threshold = if (Random.nextDouble(0.0, 1.0) < mutateProb) Random.nextDouble(0.0, 1.0) else threshold,
    synonymLinkWeight = if (Random.nextDouble(0.0, 1.0) < mutateProb) Random.nextDouble(0.0, 1.0) else synonymLinkWeight,
    antonymLinkWeight = if (Random.nextDouble(0.0, 1.0) < mutateProb) Random.nextDouble(-1.0, 1.0) else antonymLinkWeight,
    definitionLinkWeight = if (Random.nextDouble(0.0, 1.0) < mutateProb) Random.nextDouble(0.0, 1.0) else definitionLinkWeight,
    definitionOfLinkWeight = if (Random.nextDouble(0.0, 1.0) < mutateProb) Random.nextDouble(0.0, 1.0) else definitionOfLinkWeight,
    hyponymLinkWeight = if (Random.nextDouble(0.0, 1.0) < mutateProb) Random.nextDouble(0.0, 1.0) else hyponymLinkWeight,
    hypernymLinkWeight = if (Random.nextDouble(0.0, 1.0) < mutateProb) Random.nextDouble(0.0, 1.0) else hypernymLinkWeight,
    meronymLinkWeight = if (Random.nextDouble(0.0, 1.0) < mutateProb) Random.nextDouble(0.0, 1.0) else meronymLinkWeight,
    holonymLinkWeight = if (Random.nextDouble(0.0, 1.0) < mutateProb) Random.nextDouble(0.0, 1.0) else holonymLinkWeight,
    syntaxLinkWeight = if (Random.nextDouble(0.0, 1.0) < mutateProb) Random.nextDouble(0.0, 1.0) else syntaxLinkWeight,
    namedEntityLinkWeight = if (Random.nextDouble(0.0, 1.0) < mutateProb) Random.nextDouble(0.0, 1.0) else namedEntityLinkWeight,
    nameOfLinkWeight = if (Random.nextDouble(0.0, 1.0) < mutateProb) Random.nextDouble(0.0, 1.0) else nameOfLinkWeight,
    semanticRoleLinkWeight = if (Random.nextDouble(0.0, 1.0) < mutateProb) Random.nextDouble(0.0, 1.0) else semanticRoleLinkWeight,
    semanticRoleOfLinkWeight = if (Random.nextDouble(0.0, 1.0) < mutateProb) Random.nextDouble(0.0, 1.0) else semanticRoleOfLinkWeight
)

private fun List<Genotype>.evolution(dataSet: List<ArcTask>): List<Genotype> {
    val results = map { if (it.fitness.correct > 0) it else it.copy(fitness = fitness(it.dna, dataSet)) }
        .sortedByDescending { it.fitness.correct }
        .also { genotypes -> println(genotypes.map { it.fitness.correct }) }
    results.first().let { println("(correct: ${it.fitness.correct}, wrong: ${it.fitness.wrong}, unknown: ${it.fitness.unknown})") }
    val elite = results.take(numElites)
    val parents = results.take(numParents)
    val nextGeneration = parents.nextGeneration()
    val numRandoms = generationSize - numElites - (numParents / 2 * 2)
    return elite.plus(nextGeneration)
        .plus(buildRandomGeneration(numRandoms))
}

fun main() {
    val resultDir = File(userHome("Dokumente"), "generations_arc").also { it.mkdirs() }
    var generation = resultDir.listFiles()
        .maxByOrNull { it.lastModified() }
        ?.let {
            listOf(
                ObjectMapper().registerModule(KotlinModule()).readValue<Genotype>(it.readBytes())
            ).plus(buildRandomGeneration(generationSize - 1))
        } ?: buildRandomGeneration(generationSize)

    var bestGenotype: Genotype = generation.first()

    val fullDataSet = readDataset(Dataset.ADVERSIAL_TEST)!!.asSequence()
    repeat(10) { shuffleIndex ->
        val chunkSize = 400
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

                //reset fitness values
                generation = generation.map { it.copy(fitness = Fitness()) }

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
                File(resultDir, "shuffle${shuffleIndex + 1}_chunk${chunkIndex + 1}.json")
                    .writeText(ObjectMapper().writeValueAsString(generation.first()))
                solver.clearCaches()
            }
    }
}
