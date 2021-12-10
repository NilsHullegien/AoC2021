package days

import java.io.File

fun main() {
    run10a()
    run10b()
}

fun run10a() {
    val list = File("src/main/resources/10/10-test.txt").readLines()
        .map { line -> line.toCharArray().toList() }
    println(list)
}

fun run10b() {
    TODO("Not yet implemented")
}
