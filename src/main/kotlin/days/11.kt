package days

import java.io.File

fun main() {
    run11a()
//    run11b()
}

class Octopus(var value: Int, var hasFlashed: Boolean)

fun run11a() {
    val grid = File("src/main/resources/11/11-input.txt").readLines()
        .map { line -> line.toCharArray().map { el -> Character.getNumericValue(el) }.toList() }
    println(grid)
    val maxSize = grid.size
    val octopusGrid = mutableListOf<MutableList<Octopus>>()
    for (x in 0 until maxSize) {
        val list = mutableListOf<Octopus>()
        for (y in 0 until maxSize) {
           list.add(Octopus(grid[x][y], false))
        }
        octopusGrid.add(list)
    }
    var flashCount = 0
    for (step in  1..1000) {
        val flashingOctos = mutableListOf<Pair<Int, Int>>()
        for (x in 0 until maxSize) {
            for (y in 0 until maxSize) {
                val octo = octopusGrid[x][y]
                if (octo.value == 9) {
                    octo.hasFlashed = true
                    flashingOctos.add(Pair(x, y))
                } else {
                    octo.value++
                }
            }
        }
        println("OCTOS FLASHING INITIALLY: $flashingOctos")
        while (flashingOctos.isNotEmpty()) {
            val coord = flashingOctos.removeFirst()
            println("ADDING LIGHT TO SURROUNDING OCTOS OF $coord")
            val flashList = mutableListOf<Pair<Int, Int>>()
            if (coord.first > 0) {
                if (coord.second > 0 && !octopusGrid[coord.first - 1][coord.second - 1].hasFlashed) {
                    octopusGrid[coord.first - 1][coord.second - 1].value++
                    if (octopusGrid[coord.first - 1][coord.second - 1].value > 9) {
                        octopusGrid[coord.first - 1][coord.second - 1].hasFlashed = true
                        flashList.add(Pair(coord.first - 1, coord.second - 1))
                    }
                }
                if (!octopusGrid[coord.first - 1][coord.second].hasFlashed) {
                    octopusGrid[coord.first - 1][coord.second].value++
                    if (octopusGrid[coord.first - 1][coord.second].value > 9) {
                        octopusGrid[coord.first - 1][coord.second].hasFlashed = true
                        flashList.add(Pair(coord.first - 1, coord.second))
                    }
                }
                if (coord.second < (maxSize - 1) && !octopusGrid[coord.first - 1][coord.second + 1].hasFlashed) {
                    octopusGrid[coord.first - 1][coord.second + 1].value++
                    if (octopusGrid[coord.first - 1][coord.second + 1].value > 9) {
                        octopusGrid[coord.first - 1][coord.second + 1].hasFlashed = true
                        flashList.add(Pair(coord.first - 1, coord.second + 1))
                    }
                }
            }
            if (coord.second > 0 && !octopusGrid[coord.first][coord.second - 1].hasFlashed) {
                octopusGrid[coord.first][coord.second - 1].value++
                if (octopusGrid[coord.first][coord.second - 1].value > 9) {
                    octopusGrid[coord.first][coord.second - 1].hasFlashed = true
                    flashList.add(Pair(coord.first, coord.second - 1))
                }
            }
            if (coord.second < (maxSize - 1) && !octopusGrid[coord.first][coord.second + 1].hasFlashed) {
                octopusGrid[coord.first][coord.second + 1].value++
                if (octopusGrid[coord.first][coord.second + 1].value > 9) {
                    octopusGrid[coord.first][coord.second + 1].hasFlashed = true
                    flashList.add(Pair(coord.first, coord.second + 1))
                }
            }
            if (coord.first < (maxSize - 1)) {
                if (coord.second > 0 && !octopusGrid[coord.first + 1][coord.second - 1].hasFlashed) {
                    octopusGrid[coord.first + 1][coord.second - 1].value++
                    if (octopusGrid[coord.first + 1][coord.second - 1].value > 9) {
                        octopusGrid[coord.first + 1][coord.second - 1].hasFlashed = true
                        flashList.add(Pair(coord.first + 1, coord.second - 1))
                    }
                }
                if (!octopusGrid[coord.first + 1][coord.second].hasFlashed) {
                    octopusGrid[coord.first + 1][coord.second].value++
                    if (octopusGrid[coord.first + 1][coord.second].value > 9) {
                        octopusGrid[coord.first + 1][coord.second].hasFlashed = true
                        flashList.add(Pair(coord.first + 1, coord.second))
                    }
                }

                if (coord.second < (maxSize - 1) && !octopusGrid[coord.first + 1][coord.second + 1].hasFlashed) {
                    octopusGrid[coord.first + 1][coord.second + 1].value++
                    if (octopusGrid[coord.first + 1][coord.second + 1].value > 9) {
                        octopusGrid[coord.first + 1][coord.second + 1].hasFlashed = true
                        flashList.add(Pair(coord.first + 1, coord.second + 1))
                    }
                }
            }
            flashingOctos.addAll(flashList)
        }
        var flashCountPerStep = 0
        for (x in 0 until maxSize) {
            for (y in 0 until maxSize) {
                if (octopusGrid[x][y].hasFlashed) {
                    octopusGrid[x][y].value = 0
                    octopusGrid[x][y].hasFlashed = false
                    flashCountPerStep++
                }
            }
        }
        if (flashCountPerStep == 100) {
            println("11b: $step")
            return
        }
        flashCount += flashCountPerStep
        println("GRID AFTER STEP $step: (total flashes $flashCount)")
        for (x in 0 until maxSize) {
           println(octopusGrid[x].map { it.value })
        }


    }




}

fun run11b() {

}
