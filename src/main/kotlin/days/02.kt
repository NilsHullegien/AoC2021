package days

import java.io.File

fun main() {
    run2a()
    run2b()
}

fun run2a() {
    var horPos = 0
    var depth = 0
    File("src/main/resources/02/02-input.txt").forEachLine{
        val line = it.split(" ")
        val movement = line[0]
        val posChange = line[1].toInt()
        when (movement) {
            "forward" -> horPos += posChange
            "down" -> depth += posChange
            "up" -> depth -= posChange
        }
    }
    println(horPos * depth)
}

fun run2b() {
    var horPos = 0
    var depth = 0
    var aim = 0
    File("src/main/resources/02/02-input.txt").forEachLine{
        val line = it.split(" ")
        val movement = line[0]
        val posChange = line[1].toInt()
        when (movement) {
            "forward" ->  {
                horPos += posChange
                depth += (aim * posChange)
            }
            "down" -> aim += posChange
            "up" -> aim -= posChange
        }
    }
    println(horPos * depth)
}
