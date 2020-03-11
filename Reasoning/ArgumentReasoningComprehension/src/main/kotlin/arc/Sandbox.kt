package arc

import arc.utils.Dataset
import arc.utils.readDataset


fun main() {
    Dataset.values().forEach {
        val dataset = readDataset(it)
        println()
    }
}