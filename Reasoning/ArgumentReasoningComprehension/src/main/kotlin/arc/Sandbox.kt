@file:Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package arc

import arc.dataset.Dataset
import arc.dataset.readDataset
import arc.eval.getLatestConfig
import arc.util.print
import arc.util.resultsDir
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import java.io.File

fun main() {
//    val arcTask = ArcTask(
//        id = "9309004_185_AE861G0AY5RGT",
//        warrant0 = "most waiters do work hard",
//        warrant1 = "most waiters do no work hard",
//        correctLabelW0orW1 = ArcLabel.W0,
//        reason = "Tipping rewards entrepreneurial spirit and hard work.",
//        claim = "To tip",
//        debateTitle = "To Tip or Not to Tip",
//        debateInfo = "Should restaurants do away with tipping?",
//        isAdversarial = false
//    )
    val mapper = ObjectMapper().registerModule(KotlinModule())
    val config = getLatestConfig()
        .copy(
            depth = 2,
            useWsd = true,
            useNeg = false
        )
    val dirName = config.toDirName()
    val adversarialMap = readDataset(Dataset.ADVERSARIAL_TEST)!!.map { it.id to it.isAdversarial }.toMap()
    println("TOTAL")
    val results = File(resultsDir, dirName)
        .also { it.mkdirs() }
        .listFiles()
        .map { mapper.readValue<ArcResult>(it.readBytes()) }
    results.print()
    println("ARC")
    results.filterNot { adversarialMap.getValue(it.id) }.print()
    println("ADV")
    results.filter { adversarialMap.getValue(it.id) }.print()
}