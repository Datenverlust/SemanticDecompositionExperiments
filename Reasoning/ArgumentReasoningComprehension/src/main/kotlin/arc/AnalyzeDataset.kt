package arc

import arc.util.Dataset
import arc.util.readDataset

fun main(){
    readDataset(Dataset.ADVERSIAL_TEST)?.forEach { _ ->
        val x = "nothing"
    }
}