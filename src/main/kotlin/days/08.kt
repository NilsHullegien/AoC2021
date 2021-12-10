package days

import java.io.File
import kotlin.RuntimeException

fun main() {
    run8a()
    run8b()
}

fun run8a() {
    val list = File("src/main/resources/08/08-input.txt").readLines()
        .map { line -> line.split("|").map { el -> el.trim().split(" ") }.toMutableList() }
    var count = 0
    for (line in list) {
        for (output in line[1]) {
            if (output.length <= 4 || output.length == 7) {
                count++
            }
        }
    }
    println("8a: $count")
}

fun run8b() {
    //[0] is segments required to make 0
    val digitList =
        mutableListOf("abcefg", "cf", "acdeg", "acdfg", "bcdf", "abdfg", "abdefg", "acf", "abcdefg", "abcdfg")
    val segmentMap = HashMap<String, List<String?>>()
    var total = 0
    val list = File("src/main/resources/08/08-input.txt").readLines()
        .map { line -> line.split("|").map { el -> el.trim().split(" ") }.toMutableList() }
    for (line in list) {
        //Initialization, determine 1, 4, 7, 8
        val segmentsFor1 = line[0].first { input -> input.length == 2 }.trim().split("").toMutableList()
        val segmentsFor4 = line[0].first { input -> input.length == 4 }.trim().split("").toMutableList()
        val segmentsFor7 = line[0].first { input -> input.length == 3 }.trim().split("").toMutableList()
        segmentsFor1.removeLast()
        segmentsFor1.removeFirst()
        segmentsFor4.removeLast()
        segmentsFor4.removeFirst()
        segmentsFor7.removeLast()
        segmentsFor7.removeFirst()

        segmentMap["c"] = segmentsFor1
        segmentMap["f"] = segmentsFor1
        segmentMap["b"] = segmentsFor4.minus(segmentsFor1.toSet())
        segmentMap["d"] = segmentsFor4.minus(segmentsFor1.toSet())
        val segmentsFor6 = line[0].first { input ->
            input.length == 6 && !segmentMap["c"]?.let { input.split("").toMutableList().containsAll(it.toMutableList()) }!!
        }
        val segmentsFor0 = line[0].first { input ->
            input.length == 6 && !segmentMap["d"]?.let { input.split("").toMutableList().containsAll(it.toMutableList()) }!!
        }
        val segmentsFor9 = line[0].first { input -> input.length == 6 && input != segmentsFor6 && input != segmentsFor0 }
        segmentMap["a"] = segmentsFor7.minus(segmentsFor1.toSet())
        segmentMap["c"] = "abcdefg".split("").minus(segmentsFor6.split("").toSet())
        segmentMap["d"] = "abcdefg".split("").minus(segmentsFor0.split("").toSet())
        segmentMap["e"] = "abcdefg".split("").minus(segmentsFor9.split("").toSet())
        segmentMap["f"] = segmentsFor1.minus(segmentMap["c"]?.first())
        segmentMap["b"] = segmentsFor4
            .minus(listOf(segmentMap["d"]?.first(), segmentMap["c"]?.first(), segmentMap["f"]?.first()).toSet())
        segmentMap["g"] = "abcdefg".split("").minus(
            listOf(
                "",
                segmentMap["a"]?.first(),
                segmentMap["b"]?.first(),
                segmentMap["c"]?.first(),
                segmentMap["d"]?.first(),
                segmentMap["e"]?.first(),
                segmentMap["f"]?.first()
            ).toSet()
        )
        val mutatedDigitList = mutableListOf<String>()
        for(digit in digitList) {
            mutatedDigitList.add(digit.split("").filter { it != "" }.map { segmentMap[it]?.first() }.joinToString(""))
        }
        //Output check
        var finalNumber = ""
        for (output in line[1]) {
            when (output.length) {
                2 -> finalNumber += "1"
                3 -> finalNumber += "7"
                4 -> finalNumber += "4"
                7 -> finalNumber += "8"
                5 -> {
                    finalNumber += if (output.split("").containsAll(mutatedDigitList[2].split(""))) {
                        "2"
                    } else  if (output.split("").containsAll(mutatedDigitList[3].split(""))) {
                        "3"
                    } else if (output.split("").containsAll(mutatedDigitList[5].split(""))) {
                        "5"
                    } else {
                        throw RuntimeException("BORKED: $output")
                    }
                }
                6 -> {
                    finalNumber += if (output.split("").containsAll(mutatedDigitList[0].split(""))) {
                        "0"
                    } else if (output.split("").containsAll(mutatedDigitList[6].split(""))) {
                        "6"
                    } else if (output.split("").containsAll(mutatedDigitList[9].split(""))) {
                        "9"
                    } else {
                        throw RuntimeException("BORKED: $output")
                    }
                }
            }
        }
        total += finalNumber.toInt()
    }
    println("8b: $total")

}
