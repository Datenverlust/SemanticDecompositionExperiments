package arc

import arc.util.exporter
import arc.util.importer
import arc.util.userHome
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import de.kimanufaktur.nsm.decomposition.graph.edges.WeightedEdge
import org.jgrapht.graph.DefaultDirectedWeightedGraph
import org.jgrapht.graph.DefaultListenableGraph
import org.rocksdb.Options
import org.rocksdb.RocksDB
import java.io.File
import java.io.StringWriter

class SemanticGraphCache : KeyValueRepository<String, DefaultListenableGraph<String, WeightedEdge>> {

    val dbName = "semanticGraph"
    val dbDir: File = File(userHome("Dokumente"), "$dbName-db").also { it.mkdirs() }
    val options = Options()
    val mapper: ObjectMapper = ObjectMapper().registerModule(KotlinModule())

    init {
        RocksDB.loadLibrary()
        options.setCreateIfMissing(true)
    }

    val database: RocksDB = RocksDB.open(options, dbDir.path)

    fun DefaultListenableGraph<String, WeightedEdge>.serialize(): ByteArray = StringWriter()
        .also { exporter.exportGraph(this, it) }
        .let { mapper.writeValueAsBytes(it.toString()) }


    fun ByteArray.deserialize(): DefaultListenableGraph<String, WeightedEdge> =
        DefaultListenableGraph(DefaultDirectedWeightedGraph<String, WeightedEdge>(WeightedEdge::class.java)).also { graph ->
            importer.importGraph(graph, mapper.readValue<String>(this).reader())
        }


    override fun save(key: String, value: DefaultListenableGraph<String, WeightedEdge>) {
        database.put(key.toByteArray(), value.serialize())
    }

    override fun find(key: String): DefaultListenableGraph<String, WeightedEdge>? =
        try {
            database.get(key.toByteArray()).deserialize()
        } catch (_: NullPointerException) {
            null
        }

    override fun delete(key: String) {
        database.delete(key.toByteArray())
    }
}