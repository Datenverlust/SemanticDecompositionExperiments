package arc.srl

import arc.util.userHome
import de.kimanufaktur.nsm.decomposition.Concept
import de.kimanufaktur.nsm.decomposition.Decomposition
import de.kimanufaktur.nsm.decompostion.Dictionaries.DictUtil
import se.lth.cs.srl.CompletePipeline
import se.lth.cs.srl.corpus.Sentence
import se.lth.cs.srl.corpus.Word
import se.lth.cs.srl.options.CompletePipelineCMDLineOptions
import java.io.File
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

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
            "srl",
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

    fun invoke(text: List<List<String>>): MutableMap<List<Concept>, List<String>> {
        val roleMap: MutableMap<List<Concept>, List<String>> = HashMap()
        //Für jeden im Schema vorkommenden Satz
        for (i in text.indices) {
            val sentence = text[i]
            val newText: MutableList<String?> = ArrayList()
            newText.add("<root>")
            newText.addAll(sentence)
            var s: Sentence? = null
            try {
                s = pipeline.parse(newText)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            val roleList: Map<String, Map<List<Concept>, List<String>>> = HashMap()

            //Für jedes im Satz vorkommende Prädikat
            for (p in s!!.predicates) {

                //Hole Framset für Prädikat
                val newRoleSet = getRoleset(p.sense)
                if (newRoleSet != null && newRoleSet.size != 0) { //Wenn Verb in frames existiert

                    //Für jedes gefundene Argument von Prädikat
                    for (arg in p.argMap.keys) {
                        val argName = getLongName(p.argMap[arg])
                        var argList = newRoleSet[argName]
                        val thisRoles: MutableList<Concept> = ArrayList()
                        for (w in getFull(arg)) {
                            var con: Concept? = null
                            if (!Decomposition.getConcepts2Ignore().contains(w.lemma)) {
                                con = if (w.lemma == w.form.toLowerCase()) Concept(w.form) else Concept(w.lemma)
                            }
                            if (con != null) {
                                thisRoles.add(con)
                            }
                        }
                        if (argList == null) {
                            argList = ArrayList()
                            if (getModifier(argName) != null) getModifier(argName)?.let { argList.add(it) }
                        }
                        if (argList.size != 0) roleMap[thisRoles] = argList
                    }
                }
            }
        }
        return roleMap
    }

    private fun getFull(w: Word): Set<Word> {
        val span: MutableSet<Word> = TreeSet(Comparator { o1, o2 -> if (o1.idx < o2.idx) -1 else if (o1.idx > o2.idx) 1 else 0 })
        span.add(w)
        val children: MutableList<Word> = LinkedList()
        children.addAll(w.children)
        while (!children.isEmpty()) {
            val c = children.removeAt(0)
            if (span.contains(c)) continue
            span.add(c)
            children.addAll(c.children)
        }
        return span
    }

    private fun getLongName(inputName: String?): String {
        var name = inputName
        if (Character.isDigit(name!![1])) {
            name = name[0].toString() + "rg" + name[1]
        }
        return name
    }

    private fun getModifier(inputName: String?): String? {
        var name = inputName
        val nameParts = name!!.split("-".toRegex()).toTypedArray()
        if (nameParts[0] == "AM") {
            name = if (nameParts[1] == "CAU") "cause" else if (nameParts[1] == "LOC") "location" else if (nameParts[1] == "DIR") "direction" else if (nameParts[1] == "GOL") "goal" else if (nameParts[1] == "MNR") "manner" else if (nameParts[1] == "TMP") "time" else if (nameParts[1] == "EXT") "extent" else if (nameParts[1] == "PRP") "purpose" else if (nameParts[1] == "DIS") "discourse" else if (nameParts[1] == "CXN") "construction" else null
        }
        return name
    }
}