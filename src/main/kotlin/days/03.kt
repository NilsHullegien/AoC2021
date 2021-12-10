package days

import java.io.File

fun main() {
    run3a()
    run3b()
}

fun run3a() {
    val mcb = intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
    File("src/main/resources/03/03-input.txt").forEachLine{
        val line = it.split("")
        line.forEachIndexed { index, item ->
            when(item) {
                "0" -> mcb[index - 1] -= 1
                "1" -> mcb[index - 1] += 1
            }
        }

    }
    mcb.forEachIndexed { index, item ->
        if (item > 0) {
            mcb[index] = 1
        } else {
            mcb[index] = 0
        }
    }
    val gamma = Integer.parseInt(mcb.joinToString(""), 2)
    val epsilon = 4095 - gamma
    println(gamma * epsilon)

}

fun run3b() {
    val list = File("src/main/resources/03/03-input.txt").readLines().map { it.split("").subList(1, 13) }
    val oxygen = calcOxygen(list, 0)
    val co2 = calcCO2(list, 0)
    println(oxygen * co2)
}

fun calcOxygen(list: List<List<String>>, index: Int): Int {
    var mcb = 0
    list.forEach{ line ->
        when(line[index]) {
            "0" -> mcb -= 1
            "1" -> mcb += 1
        }
    }
    val newList: List<List<String>> = if (mcb >= 0) {
        list.filter { it[index] == "1" }
    } else {
        list.filter { it[index] == "0" }
    }

    if (newList.size == 1) {
        println("OXYGEN: ${newList[0]}")
        return Integer.parseInt(newList[0].joinToString(""), 2)
    }
    return calcOxygen(newList, index + 1)
}

fun calcCO2(list: List<List<String>>, index: Int): Int {
    var mcb = 0
    list.forEach{ line ->
        when(line[index]) {
            "0" -> mcb -= 1
            "1" -> mcb += 1
        }
    }
    val newList: List<List<String>> = if (mcb >= 0) {
        list.filter { it[index] == "0" }
    } else {
        list.filter { it[index] == "1" }
    }

    if (newList.size == 1) {
        println("CO2: ${newList[0]}")
        return Integer.parseInt(newList[0].joinToString(""), 2)
    }
    return calcCO2(newList, index + 1)
}
