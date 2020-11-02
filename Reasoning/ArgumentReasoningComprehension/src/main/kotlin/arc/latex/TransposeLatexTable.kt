package arc.latex

fun transposeLatexTable(inputTable: String) = inputTable.trimIndent()
        .replace("\\hline", "")
        .split("\\\\")
        .map { line -> line.split("&").map { it.trim() }.filterNot { it.isBlank() } }
        .filterNot { it.isEmpty() }
        .transpose()
        .map { it.joinToString(" & ") }
        .joinToString("\\\\ \\hline \n")

private fun List<List<String>>.transpose(): List<List<String>> {
    val rows = mutableListOf<List<String>>()   //rows
    (first().indices).forEach { colNr ->
        val col = mutableListOf<String>()
        forEach {
            col.add(it[colNr])
        }
        rows.add(col)
    }
    return rows
}
