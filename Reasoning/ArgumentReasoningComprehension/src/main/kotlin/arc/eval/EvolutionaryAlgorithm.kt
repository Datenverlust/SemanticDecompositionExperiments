@file:Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package arc.eval

import arc.ArcConfig
import arc.ArcLabel
import arc.ArcSolver
import arc.ArcTask
import arc.CoroutineArcSolver
import arc.dataset.Dataset
import arc.dataset.readDataset
import arc.util.printProgress
import arc.util.userHome
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.coroutines.runBlocking
import java.io.File
import kotlin.math.roundToInt
import kotlin.random.Random

val dataSet = readDataset(Dataset.ADVERSARIAL_TRAIN)!!
private val solver = ArcSolver().also { it.initialFillRamCache(dataSet) }
private val parallelArcSolver = CoroutineArcSolver(8, solver)
private const val generationSize = 100
private const val numElites = 1
private const val crossoverFraction = 0.6
private const val mutationFraction = 0.2
private const val numParams = 14
private const val numRepeatedFitnessTests = 1
private const val mutateProb = 0.05
private const val mutateInfluence = 0.2

private fun fitness(config: ArcConfig, dataSet: List<ArcTask>) = runBlocking {
    (1..numRepeatedFitnessTests).map {
        parallelArcSolver.startAsync(dataSet, config)
    }.let { results ->
        val numCorrect = results.map { line -> line.filter { it.correctLabel == it.foundLabel }.count() }
        val numWrong = results.map { line -> line.filter { it.correctLabel != it.foundLabel && it.foundLabel != ArcLabel.UNKNOWN }.count() }
        val numUnknown = results.map { line -> line.filter { it.foundLabel == ArcLabel.UNKNOWN }.count() }
        Fitness(
            correct = numCorrect,
            wrong = numWrong,
            unknown = numUnknown
        )
    }
}

private fun buildRandomGeneration(size: Int) = (1..size).map {
    ArcConfig(
        threshold = randomThreshold(),
        synonymLinkWeight = randomEdgeWeight(),
        antonymLinkWeight = randomNegEdgeWeight(),
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

private fun List<Genotype>.crossover(): List<Genotype> = shuffled().asSequence()
    .chunked(2)
    .filter { it.size == 2 }
    .map { (first, second) ->
        val crossDef = (1..numParams).map { Random.nextBoolean() }
        val invertedCrossDef = crossDef.map { it.not() }
        listOf(
            (first.dna to second.dna).uniformCrossover(crossDef.iterator()),
            (first.dna to second.dna).uniformCrossover(invertedCrossDef.iterator())
        )
    }
    .flatten()
    .map { Genotype(dna = it) }
    .toList()

private fun Pair<ArcConfig, ArcConfig>.uniformCrossover(crossDefIterator: Iterator<Boolean>): ArcConfig =
    ArcConfig(
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
private fun randomNegEdgeWeight() = Random.nextDouble(-1.0, 1.0)
private fun randomThreshold() = Random.nextDouble(0.1, 1.0)

private fun Double.mutateBy(randomNumber: Double) = (1.0 - mutateInfluence) * this + mutateInfluence * randomNumber

private fun ArcConfig.mutate(): ArcConfig = (1..numParams).map { Random.nextDouble(0.0, 1.0) < mutateProb }.iterator()
    .let { iterator ->
        ArcConfig(
            threshold = if (iterator.next()) threshold.mutateBy(randomThreshold()) else threshold,
            synonymLinkWeight = if (iterator.next()) synonymLinkWeight.mutateBy(randomEdgeWeight()) else synonymLinkWeight,
            antonymLinkWeight = if (iterator.next()) antonymLinkWeight.mutateBy(randomNegEdgeWeight()) else antonymLinkWeight,
            definitionLinkWeight = if (iterator.next()) definitionLinkWeight.mutateBy(randomEdgeWeight()) else definitionLinkWeight,
            definitionOfLinkWeight = if (iterator.next()) definitionOfLinkWeight.mutateBy(randomEdgeWeight()) else definitionOfLinkWeight,
            hyponymLinkWeight = if (iterator.next()) hyponymLinkWeight.mutateBy(randomEdgeWeight()) else hyponymLinkWeight,
            hypernymLinkWeight = if (iterator.next()) hypernymLinkWeight.mutateBy(randomEdgeWeight()) else hypernymLinkWeight,
            meronymLinkWeight = if (iterator.next()) meronymLinkWeight.mutateBy(randomEdgeWeight()) else meronymLinkWeight,
            holonymLinkWeight = if (iterator.next()) holonymLinkWeight.mutateBy(randomEdgeWeight()) else holonymLinkWeight,
            syntaxLinkWeight = if (iterator.next()) syntaxLinkWeight.mutateBy(randomEdgeWeight()) else syntaxLinkWeight,
            namedEntityLinkWeight = if (iterator.next()) namedEntityLinkWeight.mutateBy(randomEdgeWeight()) else namedEntityLinkWeight,
            nameOfLinkWeight = if (iterator.next()) nameOfLinkWeight.mutateBy(randomEdgeWeight()) else nameOfLinkWeight,
            semanticRoleLinkWeight = if (iterator.next()) semanticRoleLinkWeight.mutateBy(randomEdgeWeight()) else semanticRoleLinkWeight,
            semanticRoleOfLinkWeight = if (iterator.next()) semanticRoleOfLinkWeight.mutateBy(randomEdgeWeight()) else semanticRoleOfLinkWeight
        )
    }

private fun List<Genotype>.evolution(dataSet: List<ArcTask>): List<Genotype> {
    val results = asSequence().printProgress(1, size)
        .map { if (it.fitness == Fitness()) it.copy(fitness = fitness(it.dna, dataSet)) else it }
        .sortedWith(
            compareByDescending<Genotype> { (it.fitness.correct.average() - it.fitness.wrong.average()) }
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
    val numParents = (results.size.toDouble() * crossoverFraction).roundToInt()
    val parents = results.take(numParents)
    val numMutations = (results.size.toDouble() * mutationFraction).roundToInt()
    val mutations = results.take(numMutations).map { Genotype(dna = it.dna.mutate()) }
    val numRandoms = generationSize - numElites -
        ((numParents / 2) * 2) - numMutations
    return elite.plus(parents.crossover())
        .plus(mutations)
        .plus(buildRandomGeneration(numRandoms))
}

fun main() {
    val mapper = ObjectMapper().registerModule(KotlinModule())
    val resultDir = File(userHome("Dokumente"), "generations_arc").also { it.mkdirs() }
    val timestamp = System.currentTimeMillis()
    repeat(10) { index ->
        println("ITERATION#${index + 1}")
        var generation = resultDir.listFiles().asSequence()
            .take(generationSize)
            .map {
                mapper.readValue<Genotype>(it.readBytes())
            }
            .toList()
            .drop(100)
            .let { it.plus(buildRandomGeneration(generationSize - it.size)) }

        var bestResult = 0
        var evolutionCount = 0

        //do the evolution
        var unchangedBestCount = 0
        while (unchangedBestCount < 10) {
            evolutionCount = evolutionCount.inc()
            generation = generation.evolution(dataSet)
            val eliteResult = generation.first().fitness.correct.first()
            if (bestResult == eliteResult) {
                unchangedBestCount = unchangedBestCount.inc()
            } else {
                bestResult = eliteResult
                unchangedBestCount = 0
                File(resultDir, "${timestamp}_iter-${index + 1}_evo-${evolutionCount}.json")
                    .writeText(mapper.writeValueAsString(generation.first()))
            }
        }
    }
}