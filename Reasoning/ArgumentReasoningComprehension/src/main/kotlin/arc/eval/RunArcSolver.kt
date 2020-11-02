@file:Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package arc.eval

import arc.ArcConfig
import arc.ArcResult
import arc.ArcSolver
import arc.CoroutineArcSolver
import arc.dataset.Dataset
import arc.dataset.readDataset
import arc.toDirName
import arc.util.getIdsOfDoneTasks
import arc.util.print
import arc.util.resultsDir
import arc.util.saveResult
import arc.util.userHome
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import java.io.File

fun getLatestConfig(): ArcConfig = File(userHome("Dokumente"), "generations_arc")
    .listFiles()
    .maxByOrNull { it.lastModified() }!!
    .let { ObjectMapper().registerModule(KotlinModule()).readValue<Genotype>(it.readBytes()).dna }

fun main() {
    val bulkSize = 100
    val solver = ArcSolver()
    val parallelArcSolver = CoroutineArcSolver(12, solver)
    val configs = listOf(1, 2).map { depth ->
        listOf(false, true).map { useWsd ->
            listOf(false, true).map { useNeg ->
                getLatestConfig().copy(
                    depth = depth,
                    useNeg = useNeg,
                    useWsd = useWsd
                )
            }
        }
    }
        .flatten()
        .flatten()
    configs.forEach { config ->
        println(config)
        val dirName = config.toDirName()
        val mapper = ObjectMapper().registerModule(KotlinModule())
        readDataset(Dataset.ADVERSARIAL_TEST)?.let { dataSet ->
            val tasksDone = getIdsOfDoneTasks(dirName)
            println("Done Tasks: ${tasksDone.size}")
            File(resultsDir, dirName).also { it.mkdirs() }.listFiles().map { mapper.readValue<ArcResult>(it.readBytes()) }.print()
            dataSet.asSequence()
                .filterNot { it.id in tasksDone }
                .chunked(bulkSize)
                .forEach { bulkSet ->
                    val results = parallelArcSolver.startAsync(bulkSet, config)
                    results.forEach { result ->
                        saveResult(result, dirName)
                    }
                    File(resultsDir, dirName).also { it.mkdirs() }.listFiles().map { mapper.readValue<ArcResult>(it.readBytes()) }.print()
                }
        }
    }
}