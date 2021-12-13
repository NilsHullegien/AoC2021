package days

import java.io.File

fun main() {
    run5a()
    run5b()
}

fun run5a() {
    val grid: HashMap<Pair<Int, Int>, Int> = HashMap()
    File("src/main/resources/05/05-input.txt").forEachLine { it ->
        val coords = it.split(" -> ").map {
            val coord = it.split(",").map { el -> el.toInt() }
            Pair(coord[0], coord[1])
        }
        val smallX: Int
        val largeX: Int
        val smallY: Int
        val largeY: Int

        if (coords[0].first < coords[1].first) {
            smallX = coords[0].first
            largeX = coords[1].first
        } else {
            largeX = coords[0].first
            smallX = coords[1].first
        }

        if (coords[0].second < coords[1].second) {
            smallY = coords[0].second
            largeY = coords[1].second
        } else {
            largeY = coords[0].second
            smallY = coords[1].second
        }

        if (smallX != largeX && smallY != largeY) {
            return@forEachLine
        }
        for (x in smallX..largeX) {
            for (y in smallY..largeY) {
                grid[Pair(x, y)] = grid.getOrDefault(Pair(x, y), 0) + 1
            }
        }
    }
    println(grid.filter { entry -> entry.value >= 2 }.count())
}


fun run5b() {
    val grid: HashMap<Pair<Int, Int>, Int> = HashMap()
    File("src/main/resources/05/05-input-remco.txt").forEachLine { it ->
        val coords = it.split(" -> ").map {
            val coord = it.split(",").map { el -> el.toInt() }
            Pair(coord[0], coord[1])
        }
        var smallX: Int
        val largeX: Int
        var smallY: Int
        val largeY: Int

        if (coords[0].first < coords[1].first) {
            smallX = coords[0].first
            largeX = coords[1].first
        } else {
            largeX = coords[0].first
            smallX = coords[1].first
        }

        if (coords[0].second < coords[1].second) {
            smallY = coords[0].second
            largeY = coords[1].second
        } else {
            largeY = coords[0].second
            smallY = coords[1].second
        }

        if (smallX != largeX && smallY != largeY) {
            if (coords[0].first == smallX) {
                if (coords[0].second == smallY) {
                    while (smallX <= largeX) {
                        grid[Pair(smallX, smallY)] = grid.getOrDefault(Pair(smallX, smallY), 0) + 1
                        smallX++
                        smallY++
                    }
                } else {
                    var iterCoords = coords[0]
                    while (iterCoords.first <= largeX) {
                        grid[Pair(iterCoords.first, iterCoords.second)] = grid.getOrDefault(Pair(iterCoords.first, iterCoords.second), 0) + 1
                        iterCoords = Pair(iterCoords.first + 1, iterCoords.second - 1)
                    }
                }
            } else {
                if (coords[0].second == smallY) {
                    var iterCoords = coords[0]
                    while (iterCoords.first >= smallX) {
                        grid[Pair(iterCoords.first, iterCoords.second)] = grid.getOrDefault(Pair(iterCoords.first, iterCoords.second), 0) + 1
                        iterCoords = Pair(iterCoords.first - 1, iterCoords.second + 1)
                    }
                } else {
                    while (smallX <= largeX) {
                        grid[Pair(smallX, smallY)] = grid.getOrDefault(Pair(smallX, smallY), 0) + 1
                        smallX++
                        smallY++
                    }
                }
            }
        }
        else {
            for (x in smallX..largeX) {
                for (y in smallY..largeY) {
                    grid[Pair(x, y)] = grid.getOrDefault(Pair(x, y), 0) + 1
                }
            }
        }
    }
    println(grid.filter { entry -> entry.value >= 2 }.count())
}




