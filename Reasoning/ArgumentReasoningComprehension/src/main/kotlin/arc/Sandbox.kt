package arc

import arc.dataset.Dataset
import arc.dataset.allElements
import arc.dataset.readDataset
import arc.util.asAnnotatedCoreDocument
import arc.util.merge
import de.kimanufaktur.nsm.decomposition.Concept
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.Collections

fun main() {
    val arcTask = ArcTask(
        id = "9309004_185_AE861G0AY5RGT",
        warrant0 = "most waiters do work hard",
        warrant1 = "most waiters do no work hard",
        correctLabelW0orW1 = ArcLabel.W0,
        reason = "Tipping rewards entrepreneurial spirit and hard work.",
        claim = "To tip",
        debateTitle = "To Tip or Not to Tip",
        debateInfo = "Should restaurants do away with tipping?"
    )

//    val wsdSentence = "I am visiting my mother today. The Mediterranean was mother to many cultures and languages."
    //    val sentence = arcTask.allElements().map { it.toGraphComponent().graph}.merge()
//    val init = arcTask.warrant0.toGraphComponent()
//    graphCache.clear()
//
//
    val startTimeParallel = System.currentTimeMillis()
    val graphBuilder = ParallelGraphBuilder(3)
    val parallelGraph = graphBuilder.startAsync(arcTask.allElements()).map { it.graph }.merge()
    val endTimeParallel = System.currentTimeMillis()
    val durationParallel = endTimeParallel - startTimeParallel
    println("parallel duration: $durationParallel ms")
    graphCache.clear()

    val startTime = System.currentTimeMillis()
    val con = arcTask.allElements().map { it.toGraphComponent().graph}.merge()
    val endTime = System.currentTimeMillis()
    val duration = endTime - startTime
    println("duration: $duration ms")

    println("debug")

}

class ParallelGraphBuilder(
    val numThreads: Int
) {
    fun startAsync(components: List<String>):List<GraphComponent> = runBlocking {
        val componentChannel = Channel<String>()
        val graphs = Collections.synchronizedList(ArrayList<GraphComponent>(components.size))
        val runners = (1..numThreads).map {
            async(Dispatchers.IO) {
                buildGraphs(componentChannel, graphs)
            }
        }
        components.forEach { componentChannel.send(it) }
        componentChannel.close()
        runners.forEach { it.await() }
        return@runBlocking graphs
    }

    internal suspend fun buildGraphs(componentChannel: Channel<String>, graphs: MutableList<GraphComponent>) {
        for (component in componentChannel) {
            val graph = component.toGraphComponent()
            graphs.add(graph)
        }
    }
}


/*
    fun startAsync(inScope: CoroutineScope) = inScope.launch {
        while (inScope.isActive) {
            val bulkProcessingTimer = summaryBulkProcessingTime.startTimer()
            val timer = TimerUtil()
            val urlChannel = Channel<String>()
            val docs = Collections.synchronizedList(ArrayList<Website>(updateBulkSize))
            val runners = (1..numThreads).map {
                async(Dispatchers.IO) {
                    loadWebtechs(urlChannel, docs)
                }
            }
            webtechIndex.getOldestTimestamp().let { gaugeOldestUrlInBulk.set(it.time.toDouble()) }
            webtechIndex.getNOldestUrls(updateBulkSize).forEach { url ->
                urlChannel.send(url)
            }
            urlChannel.close()
            runners.forEach { it.await() }
            webtechIndex.putWebsites(docs)
            LOGGER.info {
                "Processing %s urls took %s - Most recent website: %s".format(
                    updateBulkSize,
                    timer.formattedDuration,
                    DateFormatHelper.germanDateFormat(docs.maxBy { it.timestamp }!!.timestamp)
                )
            }
            delay(1_500)
            bulkProcessingTimer.observeDuration()
        }
    }

    internal suspend fun loadWebtechs(urlChannel: Channel<String>, docs: MutableList<Website>) {
        var webtechtorRunner = webtechtorRunnerFactory.invoke()
        for (url in urlChannel) {
            val urlProcessingTimer = summaryUrlProcessingTime.startTimer()
            try {
                val apps = webtechtorRunner
                    .invoke(url)
                    .map {
                        App(
                            appName = it.appName,
                            version = it.version,
                            categoryName = it.categoryName,
                            icon = it.icon,
                            website = it.website
                        )
                    }
                docs.add(Website(url, Date(), apps))
                summarySizeWebtechsFound.observe(apps.size.toDouble())
            } catch (e: UnhandledAlertException) {
                LOGGER.info { "UnhandledAlertException: $url could not be loaded." }
                docs.add(Website(url, Date(), listOf()))
                webtechtorRunner = webtechtorRunnerFactory.invoke()
            } catch (e: WebDriverException) {
                //relevant selenium bug: https://github.com/SeleniumHQ/selenium/issues/7359
                if (e.message?.contains("""bad\sinspector\smessage""".toRegex()) ?: false) {
                    LOGGER.info { "WebDriverException: Bad inspector message at $url." }
                    docs.add(Website(url, Date(), listOf()))
                    webtechtorRunner = webtechtorRunnerFactory.invoke()
                } else {
                    LOGGER.info { "Unhandled Exception at $url will kill webtechtor." }
                    throw e
                }
            } catch (e: Exception) {
                LOGGER.info { "Unhandled Exception at $url will kill webtechtor." }
                throw e
            }
            counterProcessedUrls.inc()
            urlProcessingTimer.observeDuration()
        }
    }
 */