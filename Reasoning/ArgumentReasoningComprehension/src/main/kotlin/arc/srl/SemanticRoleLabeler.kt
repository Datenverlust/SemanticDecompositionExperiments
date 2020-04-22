package arc.srl

import arc.util.userHome
import de.kimanufaktur.nsm.decompostion.Dictionaries.DictUtil
import edu.stanford.nlp.ling.CoreLabel
import se.lth.cs.srl.CompletePipeline
import se.lth.cs.srl.options.CompletePipelineCMDLineOptions
import java.io.File

class SemanticRoleLabeler() {
    internal val language = "eng"
    internal val source = "http://dainas.dai-labor.de/~faehndrich@dai/NLP/Models/"
    internal val lemmaPath = downloadModel("lemmatizer-eng-4M-v36.mdl")
    internal val taggerPath = downloadModel("tagger-eng-4M-v36.mdl")
    internal val parserPath = downloadModel("parser-eng-12M-v36.mdl")
    internal val srlPath = downloadModel("CoNLL2009-ST-English-ALL.anna-3.3.srl-4.1.srl.model")
    internal val pipeline = getPipeline(
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

    internal fun downloadModel(fileName: String): String {
        File(userHome(".decomposition"), "Models")
            .also { it.mkdirs() }
            .let { modelsDir ->
                File(modelsDir, fileName).let { model ->
                    if (!model.exists()) DictUtil.downloadFileParalell(source + fileName, model.path)
                    return model.path
                }
            }
    }

    internal fun getPipeline(pipelineOptions: Array<String>) =
        CompletePipelineCMDLineOptions()
            .also { it.parseCmdLineArgs(pipelineOptions) }
            .let { CompletePipeline.getCompletePipeline(it) }

    fun invoke(tokenizedSentence: List<CoreLabel>) =
        pipeline.parse(
            tokenizedSentence.map { token ->
                token.originalText()
            }
        )
            .predicates.mapNotNull { predicate ->
                getRoleset(predicate.lemma, predicate.sense)
                    ?.let { roleset ->
                        predicate.argMap.mapNotNull { (word, argName) ->
                            roleset.getOrNull(argName.removePrefix("A").toInt().dec())
                                ?.let { role ->
                                    tokenizedSentence.get(word.idx) to role
                                }
                        }
                            .toMap()
                    }
            }
            .asSequence()
            .flatMap { it.asSequence() }
            .groupBy({ it.key }, { it.value })
}