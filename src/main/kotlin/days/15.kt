package days

import java.io.File

fun main() {
    run15a()
    run15b()
}

class Coord(val riskValue: Int, var minRiskToReach: Int) {
    override fun toString(): String {
        return "(riskValue=$riskValue, minRiskToReach=$minRiskToReach)"
    }
}

fun run15a() {
    val grid = File("src/main/resources/15/15-input.txt").readLines()
        .map { line -> line.toCharArray().map { el -> Coord(Character.getNumericValue(el), Int.MAX_VALUE) }.toList() }
    val startingPos = Pair(0,0)
    val endPoint = Pair(grid.size-1, grid.size-1)
    val totalRisk = 0
    findPath(grid, startingPos, endPoint, totalRisk, mutableListOf())
    println("15a: ${grid[grid.size - 1][grid.size - 1].minRiskToReach + grid[grid.size - 1][grid.size - 1].riskValue}")
}

fun findPath(grid: List<List<Coord>>, currentPos: Pair<Int, Int>, endPoint: Pair<Int, Int>, totalRisk: Int, path: MutableList<Pair<Int, Int>>) {
    println("CURRENT POS: $currentPos, ${grid[currentPos.first][currentPos.second]}")
    if (currentPos == endPoint) {
        return
    }
    if (currentPos.first >= 1) {
        println("COORD LEFT, path $path")
        val newPos = Pair(currentPos.first-1, currentPos.second)
        if (path.contains(newPos)) {
            println("NEWPOS $newPos ALREADY VISITED!")
        } else {
            val newCoord = grid[currentPos.first-1][currentPos.second]
            if (totalRisk <= newCoord.minRiskToReach) {
                newCoord.minRiskToReach = totalRisk
                val newPath = path.toMutableList()
                newPath.add(newPos)
                findPath(grid, Pair(currentPos.first - 1, currentPos.second), endPoint, totalRisk + grid[currentPos.first][currentPos.second].riskValue, newPath)
            }
        }
    }
    if (currentPos.first < endPoint.first) {
        println("COORD RIGHT, path $path")
        val newPos = Pair(currentPos.first+1, currentPos.second)
        if (path.contains(newPos)) {
            println("NEWPOS $newPos ALREADY VISITED!")
        } else {
            val newCoord = grid[currentPos.first+1][currentPos.second]
            if (totalRisk <= newCoord.minRiskToReach) {
                newCoord.minRiskToReach = totalRisk
                val newPath = path.toMutableList()
                newPath.add(newPos)
                findPath(grid, Pair(currentPos.first + 1, currentPos.second), endPoint, totalRisk + grid[currentPos.first][currentPos.second].riskValue, newPath)
            }
        }
    }
    if (currentPos.second >= 1) {
        println("COORD TOP, path $path")
        val newPos = Pair(currentPos.first, currentPos.second - 1)
        if (path.contains(newPos)) {
            println("NEWPOS $newPos ALREADY VISITED!")
        } else {
            val newCoord = grid[currentPos.first][currentPos.second-1]
            if (totalRisk <= newCoord.minRiskToReach) {
                newCoord.minRiskToReach = totalRisk
                val newPath = path.toMutableList()
                newPath.add(newPos)
                findPath(grid, Pair(currentPos.first, currentPos.second-1), endPoint, totalRisk + grid[currentPos.first][currentPos.second].riskValue, newPath)
            }
        }
    }
    if (currentPos.second < endPoint.second) {
        println("COORD BOTTOM, path $path")
        val newPos = Pair(currentPos.first, currentPos.second+1)
        if (path.contains(newPos)) {
            println("NEWPOS $newPos ALREADY VISITED!")
        } else {
            val newCoord = grid[currentPos.first][currentPos.second+1]
            if (totalRisk <= newCoord.minRiskToReach) {
                newCoord.minRiskToReach = totalRisk
                val newPath = path.toMutableList()
                newPath.add(newPos)
                findPath(grid, Pair(currentPos.first, currentPos.second+1), endPoint, totalRisk + grid[currentPos.first][currentPos.second].riskValue, newPath)
            }
        }
    }
}

fun run15b() {

}
