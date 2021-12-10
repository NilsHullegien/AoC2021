package days

import java.io.File
import java.util.*
import kotlin.collections.HashMap

fun main() {
    run6(80) //6a
    run6(256) //6b
}

fun run6(maxDays: Int) {
    val list = File("src/main/resources/06/06-input.txt").readLines().first().split(",").map{ it.toInt()}.toMutableList()
    val map = HashMap<Int, Long>()
    for (item in list.distinct()) {
        map[item] = Collections.frequency(list, item).toLong()
    }
    for (i in 1..maxDays) {
        val iterMap = map.toMutableMap()
        val newFish = iterMap.getOrDefault(0, 0)
        for (idx in 0..7) {
            map[idx] = iterMap.getOrDefault(idx + 1, 0)
        }
        map.replace(6, map.getOrDefault(6, 0) + newFish)
        map[8] = newFish
        var sum = 0L
        for(value in map.values) {
            sum += value
        }
        if (i == maxDays) {
            println("AFTER $i day(s), total nr of fish: $sum")
            return
        }
    }
}
