package arc

import arc.util.userHome
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.readValue
import org.rocksdb.Options
import org.rocksdb.RocksDB
import java.io.ByteArrayOutputStream
import java.io.File

interface KeyValueRepository<K, V> {
    fun save(key: K, value: V)
    fun find(key: K): V?
    fun delete(key: K)
}

class GraphComponentCache : KeyValueRepository<String, GraphComponent> {

    val dbName = "graph-components"
    val dbDir: File = File(userHome("Dokumente"), "$dbName-db").also { it.mkdirs() }
    val options = Options()

    init {
        RocksDB.loadLibrary()
        options.setCreateIfMissing(true)
    }

    val database: RocksDB = RocksDB.open(options, dbDir.path)


    fun GraphComponent.serialize(): ByteArray {
        val mapper = ObjectMapper()
        val outputStream = ByteArrayOutputStream()
        mapper.enable(SerializationFeature.INDENT_OUTPUT)
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
        mapper.writeValue(outputStream, this)
        return outputStream.toByteArray()
    }

    fun ByteArray.deserialize(): GraphComponent {
        val mapper = ObjectMapper()
        mapper.enable(SerializationFeature.INDENT_OUTPUT)
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
        return mapper.readValue(this)
    }

    override fun save(key: String, value: GraphComponent) {
        database.put(key.toByteArray(), value.serialize())
    }

    override fun find(key: String): GraphComponent? {
        return try {
            database.get(key.toByteArray()).deserialize()
        } catch (t: Throwable) {
            null
        }
    }

    override fun delete(key: String) {
        database.delete(key.toByteArray())
    }
}