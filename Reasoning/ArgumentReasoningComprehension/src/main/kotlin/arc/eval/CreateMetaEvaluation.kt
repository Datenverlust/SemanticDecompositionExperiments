@file:Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package arc.eval

import arc.ArcResult
import arc.toDirName
import arc.util.resultsDir
import arc.util.userHome
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import java.io.File

data class Evaluation(
    val maxNodes: Int,
    val minNodes: Int,
    val medianNodes: Double,
    val avgNodes: Double,
    val maxEdges: Int,
    val minEdges: Int,
    val medianEdges: Double,
    val avgEdges: Double,
    val propConnected: Double,
    val avgPropDefinitionEdges: Double,
    val avgPropSynonymEdges: Double,
    val avgPropAntonymEdges: Double,
    val avgPropHyperHyponymEdges: Double,
    val avgPropMeroHolonymEdges: Double,
    val avgPropSyntaxEdges: Double,
    val avgPropNamedEntityEdges: Double,
    val avgPropSemanticRoleEdges: Double,
)

fun median(l: List<Int>) = l.ifEmpty { null }?.sorted()?.let { (it[it.size / 2].toDouble() + it[(it.size - 1) / 2].toDouble()) / 2 }

fun main() {
    val mapper = ObjectMapper().registerModule(KotlinModule())
    listOf(1, 2).forEach { depth ->
        listOf(false, true).forEach { useWsd ->
            listOf(false, true).forEach { useNeg ->
                val config = getLatestConfig()
                    .copy(
                        depth = depth,
                        useWsd = useWsd,
                        useNeg = useNeg
                    )
                val meta = File(resultsDir, config.toDirName())
                    .also { it.mkdirs() }
                    .listFiles()
                    .map { mapper.readValue<ArcResult>(it.readBytes()) }
                    .map { listOf(it.resultW0.graphMeta, it.resultW1.graphMeta) }
                    .flatten()
                val nodes = meta.map { it.numNodes }
                val edges = meta.map { it.numTotalEdges }

                val eval = Evaluation(
                    maxNodes = nodes.maxOrNull() ?: 0,
                    minNodes = nodes.minOrNull() ?: 0,
                    medianNodes = median(nodes) ?: 0.0,
                    avgNodes = nodes.average(),
                    maxEdges = edges.maxOrNull() ?: 0,
                    minEdges = edges.minOrNull() ?: 0,
                    medianEdges = median(edges) ?: 0.0,
                    avgEdges = edges.average(),
                    propConnected = meta.filter { it.connected }.size.toDouble() / meta.size,
                    avgPropDefinitionEdges = meta.map { it.propDefinitionEdges }.average(),
                    avgPropSynonymEdges = meta.map { it.propSynonymEdges }.average(),
                    avgPropAntonymEdges = meta.map { it.propAntonymEdges }.average(),
                    avgPropHyperHyponymEdges = meta.map { it.propHyperHyponymEdges }.average(),
                    avgPropMeroHolonymEdges = meta.map { it.propMeroHolonymEdges }.average(),
                    avgPropSyntaxEdges = meta.map { it.propSyntaxEdges }.average(),
                    avgPropNamedEntityEdges = meta.map { it.propNamedEntityEdges }.average(),
                    avgPropSemanticRoleEdges = meta.map { it.propSemanticRoleEdges }.average(),
                )
                if (meta.size == 2 * 888) {
                    println("Write evaluation file for ${config.toDirName()}")
                    mapper.writeValueAsString(eval).let {
                        File(userHome("Dokumente"), "evaluation${config.toDirName()}.json").writeText(it)
                    }
                }
            }
        }
    }
}