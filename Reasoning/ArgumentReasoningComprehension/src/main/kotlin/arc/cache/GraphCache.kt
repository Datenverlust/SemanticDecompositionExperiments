package arc.cache

import arc.CompressedData
import arc.GraphData
import arc.compress
import arc.decompress
import arc.util.userHome
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import org.rocksdb.Options
import org.rocksdb.RocksDB
import java.io.File

interface KeyValueRepository<K, V> {
    fun save(key: K, value: V)
    fun find(key: K): V?
    fun delete(key: K)
}

class GraphCache : KeyValueRepository<String, GraphData> {

    val dbName = "graph"
    val dbDir: File = File(userHome("Dokumente"), "$dbName-db").also { it.mkdirs() }
    val options = Options()
    val mapper: ObjectMapper = ObjectMapper().registerModule(KotlinModule())

    init {
        RocksDB.loadLibrary()
        options.setCreateIfMissing(true)
    }

    val database: RocksDB = RocksDB.open(options, dbDir.path)

    fun GraphData.serialize(): ByteArray = mapper.writeValueAsBytes(compress())

    fun ByteArray.deserialize(): GraphData = mapper.readValue<CompressedData>(this).decompress()

    override fun save(key: String, value: GraphData) {
        database.put(key.toByteArray(), value.serialize())
    }

    override fun find(key: String): GraphData? =
        try {
            database.get(key.toByteArray()).deserialize()
        } catch (_: NullPointerException) {
            null
        }

    override fun delete(key: String) {
        database.delete(key.toByteArray())
    }
}