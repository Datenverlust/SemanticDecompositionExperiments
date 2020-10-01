@file:Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package arc

import arc.dataset.Dataset
import arc.dataset.readDataset
import arc.util.printProgress
import arc.util.userHome
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.coroutines.runBlocking
import java.io.File
import kotlin.random.Random

private val solver = ArcSolver()
private val parallelArcSolver = CoroutineArcSolver(10, solver)
private const val generationSize = 100
private const val numElites = 1
private const val numParents = 25
private const val numParams = 16
private const val numRepeats = 1
private const val mutateProb = 0.02

private fun fitness(config: ArcConfig, dataSet: List<ArcTask>) = runBlocking {
    (1..numRepeats).map {
        parallelArcSolver.startAsync(dataSet, config)
    }.let { results ->
        Fitness(
            correct = results.map { line -> line.filter { it.correctLabel == it.foundLabel }.count() },
            wrong = results.map { line -> line.filter { it.correctLabel != it.foundLabel && it.foundLabel != ArcLabel.UNKNOWN }.count() },
            unknown = results.map { line -> line.filter { it.foundLabel == ArcLabel.UNKNOWN }.count() }
        )
    }
}

private fun buildRandomGeneration(size: Int) = (1..size).map {
    ArcConfig(
        pulseCount = randomPulseCount(),
        startActivation = randomStartActivation(),
        threshold = randomThreshold(),
        synonymLinkWeight = randomEdgeWeight(),
        antonymLinkWeight = randomEdgeWeight(),
        definitionLinkWeight = randomEdgeWeight(),
        definitionOfLinkWeight = randomEdgeWeight(),
        hyponymLinkWeight = randomEdgeWeight(),
        hypernymLinkWeight = randomEdgeWeight(),
        meronymLinkWeight = randomEdgeWeight(),
        holonymLinkWeight = randomEdgeWeight(),
        syntaxLinkWeight = randomEdgeWeight(),
        namedEntityLinkWeight = randomEdgeWeight(),
        nameOfLinkWeight = randomEdgeWeight(),
        semanticRoleLinkWeight = randomEdgeWeight(),
        semanticRoleOfLinkWeight = randomEdgeWeight()
    )
        .let { Genotype(dna = it) }
}

data class Genotype(
    val dna: ArcConfig,
    val fitness: Fitness = Fitness()
)

data class Fitness(
    val correct: List<Int> = listOf(),
    val wrong: List<Int> = listOf(),
    val unknown: List<Int> = listOf()
)

private fun List<Genotype>.nextGeneration(): List<Genotype> = shuffled().zip(shuffled())
    .asSequence()
    .map { (first, second) ->
        val crossDef = (1..numParams).map { Random.nextBoolean() }
        val invertedCrossDef = crossDef.map { it.not() }
        listOf(
            (first.dna to second.dna).uniformCrossover(crossDef.iterator()),
            (first.dna to second.dna).uniformCrossover(invertedCrossDef.iterator())
        )
    }
    .flatten()
    .map { Genotype(dna = it.mutate()) }
    .toList()

private fun Pair<ArcConfig, ArcConfig>.uniformCrossover(crossDefIterator: Iterator<Boolean>): ArcConfig =
    ArcConfig(
        pulseCount = if (crossDefIterator.next()) first.pulseCount else second.pulseCount,
        startActivation = if (crossDefIterator.next()) first.startActivation else second.startActivation,
        threshold = if (crossDefIterator.next()) first.threshold else second.threshold,
        synonymLinkWeight = if (crossDefIterator.next()) first.synonymLinkWeight else second.synonymLinkWeight,
        antonymLinkWeight = if (crossDefIterator.next()) first.antonymLinkWeight else second.antonymLinkWeight,
        definitionLinkWeight = if (crossDefIterator.next()) first.definitionLinkWeight else second.definitionLinkWeight,
        definitionOfLinkWeight = if (crossDefIterator.next()) first.definitionOfLinkWeight else second.definitionOfLinkWeight,
        hyponymLinkWeight = if (crossDefIterator.next()) first.hyponymLinkWeight else second.hyponymLinkWeight,
        hypernymLinkWeight = if (crossDefIterator.next()) first.hypernymLinkWeight else second.hypernymLinkWeight,
        meronymLinkWeight = if (crossDefIterator.next()) first.meronymLinkWeight else second.meronymLinkWeight,
        holonymLinkWeight = if (crossDefIterator.next()) first.holonymLinkWeight else second.holonymLinkWeight,
        syntaxLinkWeight = if (crossDefIterator.next()) first.syntaxLinkWeight else second.syntaxLinkWeight,
        namedEntityLinkWeight = if (crossDefIterator.next()) first.namedEntityLinkWeight else second.namedEntityLinkWeight,
        nameOfLinkWeight = if (crossDefIterator.next()) first.nameOfLinkWeight else second.nameOfLinkWeight,
        semanticRoleLinkWeight = if (crossDefIterator.next()) first.semanticRoleLinkWeight else second.semanticRoleLinkWeight,
        semanticRoleOfLinkWeight = if (crossDefIterator.next()) first.semanticRoleOfLinkWeight else second.semanticRoleOfLinkWeight
    )

private fun randomEdgeWeight() = Random.nextDouble(0.0, 1.0)
private fun randomThreshold() = Random.nextDouble(0.0, 1.0)
private fun randomStartActivation() = Random.nextDouble(0.0, 100.0)
private fun randomPulseCount() = Random.nextInt(0, 100)

private fun ArcConfig.mutate(): ArcConfig = (1..numParams).map { Random.nextDouble(0.0, 1.0) < mutateProb }.iterator()
    .let { iterator ->
        ArcConfig(
            pulseCount = if (iterator.next()) randomPulseCount() else pulseCount,
            startActivation = if (iterator.next()) randomStartActivation() else startActivation,
            threshold = if (iterator.next()) randomThreshold() else threshold,
            synonymLinkWeight = if (iterator.next()) randomEdgeWeight() else synonymLinkWeight,
            antonymLinkWeight = if (iterator.next()) randomEdgeWeight() else antonymLinkWeight,
            definitionLinkWeight = if (iterator.next()) randomEdgeWeight() else definitionLinkWeight,
            definitionOfLinkWeight = if (iterator.next()) randomEdgeWeight() else definitionOfLinkWeight,
            hyponymLinkWeight = if (iterator.next()) randomEdgeWeight() else hyponymLinkWeight,
            hypernymLinkWeight = if (iterator.next()) randomEdgeWeight() else hypernymLinkWeight,
            meronymLinkWeight = if (iterator.next()) randomEdgeWeight() else meronymLinkWeight,
            holonymLinkWeight = if (iterator.next()) randomEdgeWeight() else holonymLinkWeight,
            syntaxLinkWeight = if (iterator.next()) randomEdgeWeight() else syntaxLinkWeight,
            namedEntityLinkWeight = if (iterator.next()) randomEdgeWeight() else namedEntityLinkWeight,
            nameOfLinkWeight = if (iterator.next()) randomEdgeWeight() else nameOfLinkWeight,
            semanticRoleLinkWeight = if (iterator.next()) randomEdgeWeight() else semanticRoleLinkWeight,
            semanticRoleOfLinkWeight = if (iterator.next()) randomEdgeWeight() else semanticRoleOfLinkWeight
        )
    }

private fun List<Genotype>.evolution(dataSet: List<ArcTask>): List<Genotype> {
    val results = asSequence().printProgress(1, size)
        .map { if (it.fitness == Fitness()) it.copy(fitness = fitness(it.dna, dataSet)) else it }
        .sortedWith(
            compareByDescending<Genotype> { it.fitness.correct.average() - it.fitness.wrong.average() }
                .thenByDescending { it.fitness.correct.average() }
        )
        .toList()
        .also { genotypes ->
            genotypes.forEach {
                println("(correct: ${it.fitness.correct}, wrong: ${it.fitness.wrong}, unknown: ${it.fitness.unknown}) - ${it.dna.hashCode()}")
            }
        }
    results.first().let { println("(correct: ${it.fitness.correct}, wrong: ${it.fitness.wrong}, unknown: ${it.fitness.unknown})") }
    val elite = results.take(numElites)
    val parents = results.take(numParents)
    val numRandoms = generationSize - numElites - (numParents * 2)
    return elite.plus(parents.nextGeneration())
        .plus(buildRandomGeneration(numRandoms))
}

fun main() {
    val resultDir = File(userHome("Dokumente"), "generations_arc").also { it.mkdirs() }
    val dataSet = readDataset(Dataset.ADVERSIAL_TEST)!!
    println("fill caches")
    dataSet.asSequence().printProgress(1, dataSet.size).forEach { solver.invoke(it, ArcConfig()) }
    val timestamp = System.currentTimeMillis()
    repeat(10) { index ->
        var generation = resultDir.listFiles()
            .maxByOrNull { it.lastModified() }
            ?.let {
                listOf(
                    ObjectMapper().registerModule(KotlinModule()).readValue<Genotype>(it.readBytes())
                ).plus(buildRandomGeneration(generationSize - 1))
            } ?: buildRandomGeneration(generationSize)

        var bestGenotype: Genotype? = null
        var evolutionCount = 0

        //do the evolution
        var unchangedBestCount = 0
        while (unchangedBestCount < 10) {
            evolutionCount = evolutionCount.inc()
            generation = generation.evolution(dataSet)
            if (bestGenotype == generation.first()) {
                unchangedBestCount = unchangedBestCount.inc()
            } else {
                bestGenotype = generation.first()
                unchangedBestCount = 0
            }
            File(resultDir, "iter-${index + 1}_evo-${evolutionCount}_$timestamp.json")
                .writeText(ObjectMapper().writeValueAsString(generation.first()))
        }
    }
}