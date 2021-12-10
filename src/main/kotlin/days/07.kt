package days

import java.io.File
import kotlin.math.abs

fun main() {
//    run7a()
    run7b()
}

fun run7a() {
    val list = File("src/main/resources/07/07-input.txt").readLines().first().split(",").map{ it.toInt()}.toMutableList()
    var minFuel = Integer.MAX_VALUE
    for (i in list.minOrNull()!!..list.maxOrNull()!!) {
        var fuel = 0
        for (crab in list) {
            fuel += abs(crab - i)
        }
        if (fuel < minFuel) {
            minFuel = fuel
        }
        println("FOR ALIGNMENT $i: Fuel req: $fuel")
    }
    println("7a: $minFuel")
}

fun run7b() {
    val list = File("src/main/resources/07/07-input.txt").readLines().first().split(",").map{ it.toInt()}.toMutableList()
    var minFuel = Integer.MAX_VALUE
    for (i in list.minOrNull()!!..list.maxOrNull()!!) {
        var fuel = 0
        for (crab in list) {
//            fuel += (1..abs(crab - i)).sum() OLD CODE, NEW IS WAY MORE EFFICIENT
            fuel += (crab - i) * ((crab - i) + 1)/2
        }
        if (fuel < minFuel) {
            minFuel = fuel
        }
        println("FOR ALIGNMENT $i: Fuel req: $fuel")
    }
    println("7b: $minFuel")
}
