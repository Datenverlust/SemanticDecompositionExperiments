package arc.cache

import arc.util.readGraphFromString
import arc.util.userHome
import arc.util.writeGraphAsString
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import de.kimanufaktur.nsm.decomposition.graph.edges.WeightedEdge
import org.jgrapht.graph.DefaultListenableGraph
import org.rocksdb.Options
import org.rocksdb.RocksDB
import java.io.File

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

    fun DefaultListenableGraph<String, WeightedEdge>.serialize(): ByteArray =
        mapper.writeValueAsBytes(writeGraphAsString(this))

    fun ByteArray.deserialize(): DefaultListenableGraph<String, WeightedEdge> =
        readGraphFromString(mapper.readValue(this))

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