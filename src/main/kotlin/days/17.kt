package days

import java.io.File
import java.text.FieldPosition

fun main() {
    run17()
}

fun run17() {
    val input = File("src/main/resources/17/17-input.txt").readLines()[0]
        .removePrefix("target area: ").split(",").map {it.split("=")[1].split("..")}
    val xCoords = Pair(input[0][0].toInt(), input[0][1].toInt())
    val yCoords = Pair(input[1][0].toInt(), input[1][1].toInt())
    println("TARGET AREA: X $xCoords, Y $yCoords")
    var xPosition = 0
    var yPosition = 0
    var maxY = 0
    val totalDistinct = mutableListOf<Pair<Int, Int>>()
    for (xVelo in 0..300) {
        for (yVelo in -200..200) {
            if (xVelo == 0 && yVelo == 0) {
                continue
            }

            var setXVelo = xVelo
            var setYVelo = yVelo
            var tempMaxY = Integer.MIN_VALUE
            while (reachTargetAreaIsPossible(xPosition, yPosition, xCoords, yCoords)) {
                xPosition += setXVelo
                yPosition += setYVelo
                if (setXVelo > 0) {
                    setXVelo -= 1
                } else if (setXVelo < 0) {
                    setXVelo += 1
                }
                setYVelo -= 1
                println("NEW POSITION AFTER STEP: ($xPosition, $yPosition)")
                if (yPosition > tempMaxY) {
                    tempMaxY = yPosition
                }
                if (inTargetArea(Pair(xPosition, yPosition), xCoords, yCoords)) {
                    println("IN TARGET AREA!")
                    println("INITIAL VELOCITY: ($xVelo, $yVelo)")
                    totalDistinct.add(Pair(xVelo, yVelo))
                    if (tempMaxY > maxY) {
                        maxY = tempMaxY
                    }
//                    throw RuntimeException("TARGET AREA")
                }
            }
            println("NO LONGER POSSIBLE")
            xPosition = 0
            yPosition = 0
        }
    }
    println("17a: $maxY")
    println("17b: ${totalDistinct.distinct().size}")
}

fun inTargetArea(position: Pair<Int, Int>, xCoords: Pair<Int, Int>, yCoords: Pair<Int, Int>): Boolean {
    return position.first >= xCoords.first && position.first <= xCoords.second &&
            position.second >= yCoords.first && position.second <= yCoords.second
}

fun reachTargetAreaIsPossible(xPosition: Int, yPosition: Int, xCoords: Pair<Int, Int>, yCoords: Pair<Int, Int>): Boolean {
    return (xPosition < xCoords.second * 1.5) && (yPosition > yCoords.first)
}
