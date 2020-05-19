package arc.srl

import arc.util.userHome
import de.kimanufaktur.nsm.decompostion.Dictionaries.DictUtil
import edu.stanford.nlp.pipeline.CoreSentence
import se.lth.cs.srl.CompletePipeline
import se.lth.cs.srl.options.CompletePipelineCMDLineOptions
import java.io.File
import java.io.OutputStream
import java.io.PrintStream

class SemanticRoleLabeler {
    internal val language = "eng"
    internal val source = "http://dainas.dai-labor.de/~faehndrich@dai/NLP/Models/"
    private val lemmaPath = downloadModel("lemmatizer-eng-4M-v36.mdl")
    private val taggerPath = downloadModel("tagger-eng-4M-v36.mdl")
    private val parserPath = downloadModel("parser-eng-12M-v36.mdl")
    private val srlPath = downloadModel("CoNLL2009-ST-English-ALL.anna-3.3.srl-4.1.srl.model")
    private val pipeline = getPipeline(
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
        val defaultOutputStream = System.out
        System.setOut(PrintStream(OutputStream.nullOutputStream()))
        val pipeline = CompletePipelineCMDLineOptions()
            .also { it.parseCmdLineArgs(pipelineOptions) }
            .let { CompletePipeline.getCompletePipeline(it) }
        System.setOut(defaultOutputStream)
        return pipeline
    }

    fun invoke(sentence: CoreSentence) =
        pipeline.parse(
            sentence.tokens().map { token ->
                token.originalText()
            }
        )
            .predicates.mapNotNull { predicate ->
                getRoleset(predicate.lemma, predicate.sense)
                    ?.let { roleSet ->
                        predicate.argMap.mapNotNull { (word, argName) ->
                            roleSet.getOrNull(argName.removePrefix("A").toInt().dec())
                                ?.let { role ->
                                    sentence.tokens()[word.idx] to role
                                }
                        }
                            .toMap()
                    }
            }
            .asSequence()
            .flatMap { it.asSequence() }
            .groupBy({ it.key }, { it.value })
}