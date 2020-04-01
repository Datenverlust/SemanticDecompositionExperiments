package arc.srl

import arc.utils.userHome
import de.kimanufaktur.nsm.decompostion.Dictionaries.DictUtil
import java.io.File

class SemanticRoleLabeler() {
    internal val language = "eng"
    internal val source = "http://dainas.dai-labor.de/~faehndrich@dai/NLP/Models/"
    internal val lemmaPath = downloadModel("lemmatizer-eng-4M-v36.mdl")
    internal val taggerPath = downloadModel("tagger-eng-4M-v36.mdl")
    internal val parserPath = downloadModel("parser-eng-12M-v36.mdl")
    internal val srlPath = downloadModel("CoNLL2009-ST-English-ALL.anna-3.3.srl-4.1.srl.model")

    fun invoke() {

    }


    fun downloadModel(fileName: String): String {
        File(userHome(".decomposition"), "Models")
            .also { it.mkdirs() }
            .let { modelsDir ->
                File(modelsDir, fileName).let { model ->
                    if (!model.exists()) DictUtil.downloadFileParalell(source + fileName, model.path)
                    return model.path
                }
            }
    }
}