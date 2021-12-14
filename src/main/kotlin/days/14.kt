package days

import java.io.File

fun main() {
    val startTime = System.currentTimeMillis()
    run14(10)
    println("RUNTIME A: ${System.currentTimeMillis() - startTime} ms")
    run14(40)
    println("RUNTIME B: ${System.currentTimeMillis() - startTime} ms")
}

fun run14(maxSteps: Int) {
    val list = File("src/main/resources/14/14-input.txt").readLines().filter { it != "" }.toMutableList()
    val inputList = list.removeAt(0).toCharArray().toMutableList()

    val mutations = mutableMapOf<String, Char>()
    list.map { it.split(" -> ") }.forEach { mutations[it[0]] = it[1][0] }
    val inputMap = mutableMapOf<String, Long>()
    for (mut in mutations) {
        inputMap[mut.key] = 0L
    }
    for (i in 0 until inputList.size - 1) {
        val key = "${inputList[i]}${inputList[i+1]}"
        inputMap.replace(key, inputMap[key]!! + 1)
    }

    val nrOccurences = mutableMapOf<Char, Long>()
    for (char in inputList) {
        if (nrOccurences.containsKey(char)) {
            nrOccurences[char] = nrOccurences[char]!!.plus(1L)
        } else {
            nrOccurences[char] = 1
        }
    }


    for (step in 1..maxSteps) {
        val mutableInputMap = inputMap.toMutableMap()
        for (entry in mutableInputMap.filter{it.value > 0L}) {
            val value = mutations[entry.key]!!
            if (nrOccurences.containsKey(value)) {
                nrOccurences[value] = nrOccurences[value]!!.plus(entry.value)
            } else {
                nrOccurences[value] = 1

            }
            val firstKey = "${entry.key[0]}$value"
            val secondKey = "$value${entry.key[1]}"
            inputMap[entry.key] = inputMap[entry.key]!!.minus(entry.value)
            inputMap[firstKey] = inputMap[firstKey]!!.plus(entry.value)
            inputMap[secondKey] = inputMap[secondKey]!!.plus(entry.value)
        }
    }
    println("14: ${nrOccurences.values.maxOrNull()!! - nrOccurences.values.minOrNull()!!}")

}
