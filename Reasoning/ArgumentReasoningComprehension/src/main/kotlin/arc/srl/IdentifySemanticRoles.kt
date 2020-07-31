package arc.srl

import arc.util.userHome
import de.kimanufaktur.nsm.decompostion.Dictionaries.DictUtil
import edu.stanford.nlp.ling.CoreLabel
import edu.stanford.nlp.pipeline.CoreDocument
import se.lth.cs.srl.CompletePipeline
import se.lth.cs.srl.options.CompletePipelineCMDLineOptions
import java.io.File

private const val language = "eng"
private const val source = "http://dainas.dai-labor.de/~faehndrich@dai/NLP/Models/"
private val lemmaPath = downloadModel("lemmatizer-eng-4M-v36.mdl")
private val taggerPath = downloadModel("tagger-eng-4M-v36.mdl")
private val parserPath = downloadModel("parser-eng-12M-v36.mdl")
private val srlPath = downloadModel("CoNLL2009-ST-English-ALL.anna-3.3.srl-4.1.srl.model")
private val srlPipeline = getPipeline(
    arrayOf(
        language,
        "-lemma",
        lemmaPath,
        "-tagger",
        taggerPath,
        "-parser",
        parserPath,
        "-srl",
        srlPath
    )
)

private fun downloadModel(fileName: String): String {
    File(userHome(".decomposition"), "Models")
        .also { it.mkdirs() }
        .let { modelsDir ->
            File(modelsDir, fileName).let { model ->
                if (!model.exists()) DictUtil.downloadFileParalell(source + fileName, model.path)
                return model.path
            }
        }
}

private fun getPipeline(pipelineOptions: Array<String>): CompletePipeline {
//    val defaultOutputStream = System.out
//    System.setOut(PrintStream(OutputStream.nullOutputStream()))
    val srlPipeline = CompletePipelineCMDLineOptions()
        .also { pipeline -> pipeline.parseCmdLineArgs(pipelineOptions) }
//    } catch (e:NoClassDefFoundError){
//        CompletePipelineCMDLineOptions()
//            .also { it.parseCmdLineArgs(pipelineOptions) }
//            .let { CompletePipeline.getCompletePipeline(it) }
//    }
//    System.setOut(defaultOutputStream)
    return srlPipeline.let { pipeline -> CompletePipeline.getCompletePipeline(pipeline) }
}

fun identifySemanticRoles(coreDocument: CoreDocument): Map<CoreLabel, List<String>> = coreDocument.sentences().map { sentence ->
    //add a pseudo element to the tokens at index 0... mate tools srl desires it
    srlPipeline.parse(listOf("").plus(sentence.tokensAsStrings()))
        .predicates.mapNotNull { predicate ->
            getRoleSet(predicate.lemma, predicate.sense)
                ?.let { roleSet ->
                    predicate.argMap.mapNotNull { (word, argName) ->
                        argName.removePrefix("A").toIntOrNull()?.let { roleSet.getOrNull(it) }
                            ?.let { role ->
                                sentence.tokens()[word.idx - 1] to role
                            }
                    }
                }
        }
        .flatten()
}
    .asSequence()
    .flatten()
    .groupBy({ (coreLabel, _) -> coreLabel }, { (_, roleName) -> roleName })