package days

import java.io.File

fun main() {
    run13a()
    run13b()
}

fun run13a() {
    val list = File("src/main/resources/13/13-input.txt").readLines().filter { it != "" }
    val input = list.filter{ !it.startsWith("fold along") }.map{ it.split(",") }.map { Pair(it[0].toInt(), it[1].toInt()) }.toMutableList()
    val folds = list.filter{ it.startsWith("fold along")}.map { it.removePrefix("fold along ") }.map { it.split("=") }.map{ Pair(it[0], it[1].toInt())}
    println("13a: ${fold(folds[0], input).size}")
}

fun run13b() {
    val list = File("src/main/resources/13/13-input.txt").readLines().filter { it != "" }
    var input = list.filter{ !it.startsWith("fold along") }.map{ it.split(",") }.map { Pair(it[0].toInt(), it[1].toInt()) }.toMutableList()
    val folds = list.filter{ it.startsWith("fold along")}.map { it.removePrefix("fold along ") }.map { it.split("=") }.map{ Pair(it[0], it[1].toInt())}
    for (fold in folds) {
        input = fold(fold, input)
    }
    println(input)
    val minY = input.minOfOrNull { it.second }
    val maxY = input.maxOfOrNull { it.second }
    val minX = input.minOfOrNull { it.first }
    val maxX = input.maxOfOrNull { it.first }

    for (y in minY!!..maxY!!) {
        for (x in minX!!..maxX!!) {
            if (input.contains(Pair(x, y))) {
                print("#")
            } else {
                print(" ")
            }
        }
        println()
    }
}

fun fold(fold: Pair<String, Int>, input: MutableList<Pair<Int, Int>>): MutableList<Pair<Int, Int>> {
    when (fold.first) {
        "x" -> {
            for ((idx, coord) in input.withIndex()) {
                if (coord.first < fold.second) {
                    continue
                }
                input[idx] = Pair(fold.second - (coord.first - fold.second), coord.second)
            }
            return input.distinct().toMutableList()
        }
        "y" -> {
            for ((idx, coord) in input.withIndex()) {
                if (coord.second < fold.second) {
                    continue
                }
                input[idx] = Pair(coord.first, fold.second - (coord.second - fold.second))
            }
            return input.distinct().toMutableList()
        }
        else -> throw RuntimeException("SHOULDN'T BE ALLOWED")
    }
}
