package days

import java.io.File

fun main() {
    run9a()
    run9b()
}

fun run9a() {
    val list = File("src/main/resources/09/09-input-bram.txt").readLines()
        .map { line -> line.toCharArray().map { el -> Character.getNumericValue(el) }.toList() }
    val MAX_WIDTH = list.size
    val MAX_HEIGHT = list[0].size
    var sum = 0
    for (x in 0 until MAX_WIDTH) {
        for (y in 0 until MAX_HEIGHT) {
            var left = Int.MAX_VALUE
            var right = Int.MAX_VALUE
            var top = Int.MAX_VALUE
            var bottom = Int.MAX_VALUE
            if (x - 1 >= 0) {
                left = list[x-1][y]
            }
            if (x + 1 < MAX_WIDTH) {
                right = list[x+1][y]
            }
            if (y - 1 >= 0) {
                top = list[x][y-1]
            }
            if (y + 1 < MAX_HEIGHT) {
                bottom = list[x][y+1]
            }

            if (mutableListOf(left, right, top, bottom).none { el -> el <= list[x][y] }) {
                println("COORD: ($x, $y) with value ${list[x][y]}")
                sum += list[x][y] + 1
            }
        }
    }
    println("9a: $sum")
}

fun run9b() {
    val list = File("src/main/resources/09/09-input.txt").readLines()
        .map { line -> line.toCharArray().map { el -> Character.getNumericValue(el) }.toMutableList() }
    val MAX_WIDTH = list.size
    val MAX_HEIGHT = list[0].size
    val pitsList = mutableListOf<Pair<Int, Int>>()
    for (x in 0 until MAX_WIDTH) {
        for (y in 0 until MAX_HEIGHT) {
            var left = Int.MAX_VALUE
            var right = Int.MAX_VALUE
            var top = Int.MAX_VALUE
            var bottom = Int.MAX_VALUE
            if (x - 1 >= 0) {
                left = list[x-1][y]
            }
            if (x + 1 < MAX_WIDTH) {
                right = list[x+1][y]
            }
            if (y - 1 >= 0) {
                top = list[x][y-1]
            }
            if (y + 1 < MAX_HEIGHT) {
                bottom = list[x][y+1]
            }

            if (mutableListOf(left, right, top, bottom).none { el -> el <= list[x][y] }) {
//                println("COORD: ($x, $y) with value ${list[x][y]}")
                pitsList.add(Pair(x, y))
            }
        }
    }
    println("LIST OF 9a: $pitsList")

    val total = mutableListOf<Int>()
    for (pit in pitsList) {
        total.add(computePit(mutableListOf(pit), list, MAX_HEIGHT, MAX_WIDTH))
    }
    total.sort()
    total.reverse()
    println(total)
    println("9b: ${total.subList(0, 3).reduce{ a, b -> a * b }}")
}

fun computePit(pitsList: MutableList<Pair<Int, Int>>, list: List<List<Int>>, MAX_HEIGHT: Int, MAX_WIDTH: Int): Int {
    val mutatingPitsList = pitsList.toMutableList()
    for (pair in pitsList) {
        val x = pair.first
        val y = pair.second
        if (list[x][y] == 9) {
            continue
        }
        if (x - 1 >= 0 && !pitsList.contains(Pair(x-1, y)) && isLowestPoint(x-1, y, list, getExcludedDirections(x-1, y, pitsList), MAX_HEIGHT, MAX_WIDTH)) {
            mutatingPitsList.add(Pair(x-1, y))
            break
        }
        if (x + 1 < MAX_WIDTH && !pitsList.contains(Pair(x+1, y)) && isLowestPoint(x+1, y, list, getExcludedDirections(x+1, y, pitsList), MAX_HEIGHT, MAX_WIDTH)) {
            mutatingPitsList.add(Pair(x+1, y))
            break
        }
        if (y - 1 >= 0 && !pitsList.contains(Pair(x, y-1)) && isLowestPoint(x, y-1, list, getExcludedDirections(x, y-1, pitsList), MAX_HEIGHT, MAX_WIDTH)) {
            mutatingPitsList.add(Pair(x, y-1))
            break
        }
        if (y + 1 < MAX_HEIGHT && !pitsList.contains(Pair(x, y+1)) && isLowestPoint(x, y+1, list, getExcludedDirections(x, y+1, pitsList), MAX_HEIGHT, MAX_WIDTH)) {
            mutatingPitsList.add(Pair(x, y+1))
            break
        }
    }
    if (mutatingPitsList.size == pitsList.size) {
        return mutatingPitsList.distinct().size
    }
    return computePit(mutatingPitsList, list, MAX_HEIGHT, MAX_WIDTH)
}

fun getExcludedDirections(x: Int, y: Int, pitsList: MutableList<Pair<Int, Int>>): List<Direction> {
    val excludedList = mutableListOf<Direction>()
    if (pitsList.contains(Pair(x-1, y))){
        excludedList.add(Direction.UP)
    }
    if (pitsList.contains(Pair(x+1, y))){
        excludedList.add(Direction.DOWN)
    }
    if (pitsList.contains(Pair(x, y-1))){
        excludedList.add(Direction.LEFT)
    }
    if (pitsList.contains(Pair(x, y+1))){
        excludedList.add(Direction.RIGHT)
    }
    return excludedList
}

fun isLowestPoint(x: Int, y: Int, list: List<List<Int>>,  excluded: List<Direction>, MAX_HEIGHT: Int, MAX_WIDTH: Int): Boolean {
    var left = Int.MAX_VALUE
    var right = Int.MAX_VALUE
    var up = Int.MAX_VALUE
    var down = Int.MAX_VALUE
    if (list[x][y] == 9) {
        return false
    }
    if (x - 1 >= 0 && !excluded.contains(Direction.UP)) {
        up = list[x-1][y]
    }
    if (x + 1 < MAX_WIDTH && !excluded.contains(Direction.DOWN)) {
        down = list[x+1][y]
    }
    if (y - 1 >= 0 && !excluded.contains(Direction.LEFT)) {
        left = list[x][y-1]
    }
    if (y + 1 < MAX_HEIGHT && !excluded.contains(Direction.RIGHT)) {
        right = list[x][y+1]
    }

    return mutableListOf(left, right, up, down).none { el -> el < list[x][y] }
}

enum class Direction {
    UP, DOWN, LEFT, RIGHT
}
