package days

import java.io.File

fun main() {
    run1a()
    run1b()
}

private fun run1a() {
    var latest = Int.MAX_VALUE
    var count = 0
    File("src/main/resources/01/01-input.txt").forEachLine{
        if (it.toInt() > latest) {
            count++
        }
        latest = it.toInt()
    }
    println(count)
}

private fun run1b() {
    var count = 0
    val list = File("src/main/resources/01/01-input.txt").readLines().map { e -> e.toInt() }
    for ((i, item) in list.withIndex()) {
        println("CURRENT: $item")
        if (i < 3) {
            continue
        }
        println("ITEM BEFORE IN TOTAL: ${list[i-3]}")
        println("INCREASE? ${list[i-3]} < $item")
        if (list[i-3] < item) {
            println("INCREASE")
            count++
        }
    }
    println(count)
}
