package days

import java.io.File
import kotlin.math.abs


fun main() {
    run19a()
}

class Scanner(
    val beaconsScanned: MutableList<Triple<Int, Int, Int>>,
    val distanceBetweenBeacons: MutableMap<Pair<Int, Int>, Triple<Int, Int, Int>>,
    var coords: Triple<Int, Int, Int>?, //RELATIVE TO SCANNER 0
    var rotation: Triple<Int, Int, Int>? //RELATIVE TO SCANNER 0
) {
    override fun toString(): String {
        return "Scanner(beaconsScanned=$beaconsScanned, distanceBetweenBeacons=$distanceBetweenBeacons)"
    }

    fun intersect(otherScanner: Scanner): MutableMap<Pair<Int, Int>, Pair<Int, Int>> {
        val intersectMap = mutableMapOf<Pair<Int, Int>, Pair<Int, Int>>()
        for (elem in this.distanceBetweenBeacons) {
            for (otherElem in otherScanner.distanceBetweenBeacons) {
                if (mutableListOf(abs(elem.value.first), abs(elem.value.second), abs(elem.value.third)).containsAll(
                    mutableListOf(abs(otherElem.value.first), abs(otherElem.value.second), abs(otherElem.value.third)) )) {
//                if ((abs(elem.value.first) == abs(otherElem.value.first)) &&
//                    (abs(elem.value.second) == abs(otherElem.value.second)) &&
//                    (abs(elem.value.third) == abs(otherElem.value.third))) {
                    intersectMap[elem.key] = otherElem.key
                }
            }
        }
        return intersectMap
    }
}

fun run19a() {
    val input = File("src/main/resources/19/19-test.txt").readLines()
    val parsedScannerList = mutableListOf<MutableList<Triple<Int, Int, Int>>>()
    var tmpList = mutableListOf<Triple<Int, Int, Int>>()
    for (line in input) {
        if (line == "" || line.contains("--- scanner 0")) {
            continue
        }
        if (line.contains("--- scanner")) {
            parsedScannerList.add(tmpList)
            tmpList = mutableListOf()
        } else {
            val coords = line.split(",")
            tmpList.add(Triple(coords[0].toInt(), coords[1].toInt(), coords[2].toInt()))
        }
    }
    parsedScannerList.add(tmpList)

    val scannerList: MutableList<Scanner> = mutableListOf()
    for (parsedScanner in parsedScannerList) {
        val scanner = Scanner(parsedScanner, mutableMapOf(), null, null)
        for (i in 0 until parsedScanner.size) {
            for (j in i until parsedScanner.size) {
                if (i == j) {
                    continue
                }
                val firstBeacon = parsedScanner[i]
                val secondBeacon = parsedScanner[j]
                val distance = Triple(
                    abs(firstBeacon.first - secondBeacon.first),
                    abs(firstBeacon.second - secondBeacon.second),
                    abs(firstBeacon.third - secondBeacon.third)
                )
                scanner.distanceBetweenBeacons[Pair(i, j)] = distance
            }
        }
        scannerList.add(scanner)
    }
    scannerList.first().coords = Triple(0, 0, 0)
    scannerList.first().rotation = Triple(0, 0, 0)

    //Compare values of distanceBetweenBeacons for different scanners
    for ((i, scanner) in scannerList.withIndex()) {
        println("FOR SCANNER $i")
        if (scanner.coords == null) {
            continue
        }
        for ((j, otherScanner) in scannerList.withIndex()) {
            if (i >= j) {
                continue
            }
            println("COMPARED TO SCANNER $j")
            val duplicatesMap = scanner.intersect(otherScanner)
            if (duplicatesMap.isEmpty()) {
                continue
            }
            println("OVERLAP BETWEEN SCANNER $i AND SCANNER $j")
            println(duplicatesMap)
            val mapBeaconIdx = mutableMapOf<Int, Int>()
            for (firstEntry in duplicatesMap) {
                for (secondEntry in duplicatesMap) {
                    if (firstEntry == secondEntry || firstEntry.key.first > secondEntry.key.first ||
                        (firstEntry.key.first == secondEntry.key.first && firstEntry.key.second > secondEntry.key.second)) {
                        continue
                    }
                    if (mapBeaconIdx.containsKey(firstEntry.key.first) && mapBeaconIdx.containsKey(firstEntry.key.second) && mapBeaconIdx.containsKey(secondEntry.key.second)) {
                        continue
                    }
                    if (firstEntry.key.first == secondEntry.key.first) {
                        val thirdKey = Pair(firstEntry.key.second, secondEntry.key.second)
                        if (duplicatesMap.containsKey(thirdKey)) {
                            val thirdValue = duplicatesMap[thirdKey]!!
                            if ((thirdValue.first == firstEntry.value.second && thirdValue.second == secondEntry.value.second) ||
                                thirdValue.second == firstEntry.value.second && thirdValue.first == secondEntry.value.second) {
                                mapBeaconIdx[firstEntry.key.first] = firstEntry.value.first
                                mapBeaconIdx[firstEntry.key.second] = firstEntry.value.second
                                mapBeaconIdx[secondEntry.key.second] = secondEntry.value.second
                            }
                        }
                    }
                }
            }
//            println("DONE! Size ${mapBeaconIdx.size}")
//            println(mapBeaconIdx)
            if (mapBeaconIdx.size < 12) {
                println("NOT ENOUGH BEACONS DETECTED!")
                continue
            }

            println("FIGURE OUT ROTATION")
            val scannerIter = mapBeaconIdx.toSortedMap().iterator()
            val entry1 = scannerIter.next()
            val entry2 = scannerIter.next()
            val scannerBeacon = scanner.distanceBetweenBeacons[Pair(entry1.key, entry2.key)]!!
            val otherScannerBeacon = otherScanner.distanceBetweenBeacons[Pair(entry1.value, entry2.value)]!!
            figureOutRotation(otherScanner, scannerBeacon, otherScannerBeacon)
            println("NEW ROTATION: ${otherScanner.rotation}")
            val otherScannerBeaconsMutatedList = mutableListOf<Triple<Int, Int, Int>>()
            for (otherScannerBeaconToMutate in otherScanner.beaconsScanned.toMutableList()) {
                otherScannerBeaconsMutatedList.add(mapBeacon(otherScannerBeaconToMutate, otherScanner.rotation!!))
            }
            println("SCANNER: $scannerBeacon, OTHERSCANNER: $otherScannerBeacon")

//            val firstValues = mutableListOf(firstCoord.first + secondCoord.first, firstCoord.first - secondCoord.first)
            val firstList = mutableListOf<Int>();
            val secondList = mutableListOf<Int>();
            val thirdList = mutableListOf<Int>();
            for (entry in mapBeaconIdx.entries) {
                firstList.add(scanner.beaconsScanned[entry.key].first + otherScannerBeaconsMutatedList[entry.value].first)
                firstList.add(scanner.beaconsScanned[entry.key].first - otherScannerBeaconsMutatedList[entry.value].first)
                firstList.add(scanner.beaconsScanned[entry.key].first + otherScannerBeaconsMutatedList[entry.value].first)
                firstList.add(scanner.beaconsScanned[entry.key].first - otherScannerBeaconsMutatedList[entry.value].first)
                secondList.add(scanner.beaconsScanned[entry.key].second + otherScannerBeaconsMutatedList[entry.value].second)
                secondList.add(scanner.beaconsScanned[entry.key].second - otherScannerBeaconsMutatedList[entry.value].second)
                secondList.add(scanner.beaconsScanned[entry.key].second + otherScannerBeaconsMutatedList[entry.value].second)
                secondList.add(scanner.beaconsScanned[entry.key].second - otherScannerBeaconsMutatedList[entry.value].second)
                thirdList.add(scanner.beaconsScanned[entry.key].third + otherScannerBeaconsMutatedList[entry.value].third)
                thirdList.add(scanner.beaconsScanned[entry.key].third - otherScannerBeaconsMutatedList[entry.value].third)
                thirdList.add(scanner.beaconsScanned[entry.key].third + otherScannerBeaconsMutatedList[entry.value].third)
                thirdList.add(scanner.beaconsScanned[entry.key].third - otherScannerBeaconsMutatedList[entry.value].third)
//                firstList.add(-(scanner.coords!!.first + scanner.beaconsScanned[entry.key].first + otherScanner.beaconsScanned[entry.value].first))
//                firstList.add(-(scanner.coords!!.first + scanner.beaconsScanned[entry.key].first - otherScanner.beaconsScanned[entry.value].first))
//                firstList.add(-(scanner.coords!!.first - scanner.beaconsScanned[entry.key].first + otherScanner.beaconsScanned[entry.value].first))
//                firstList.add(-(scanner.coords!!.first - scanner.beaconsScanned[entry.key].first - otherScanner.beaconsScanned[entry.value].first))
//                secondList.add(-(scanner.coords!!.second + scanner.beaconsScanned[entry.key].second + otherScanner.beaconsScanned[entry.value].second))
//                secondList.add(-(scanner.coords!!.second + scanner.beaconsScanned[entry.key].second - otherScanner.beaconsScanned[entry.value].second))
//                secondList.add(-(scanner.coords!!.second - scanner.beaconsScanned[entry.key].second + otherScanner.beaconsScanned[entry.value].second))
//                secondList.add(-(scanner.coords!!.second - scanner.beaconsScanned[entry.key].second - otherScanner.beaconsScanned[entry.value].second))
//                thirdList.add(-(scanner.coords!!.third + scanner.beaconsScanned[entry.key].third + otherScanner.beaconsScanned[entry.value].third))
//                thirdList.add(-(scanner.coords!!.third + scanner.beaconsScanned[entry.key].third - otherScanner.beaconsScanned[entry.value].third))
//                thirdList.add(-(scanner.coords!!.third - scanner.beaconsScanned[entry.key].third + otherScanner.beaconsScanned[entry.value].third))
//                thirdList.add(-(scanner.coords!!.third - scanner.beaconsScanned[entry.key].third - otherScanner.beaconsScanned[entry.value].third))
            }
            val firstTmp = firstList
                .groupingBy { it }.eachCount().toList().sortedByDescending { it.second }.toMap()
            val secondTmp = secondList
                .groupingBy { it }.eachCount().toList().sortedByDescending { it.second }.toMap()
            val thirdTmp = thirdList
                .groupingBy { it }.eachCount().toList().sortedByDescending { it.second }.toMap()
//            println("FIRST: $firstTmp")
//            println("SECOND: $secondTmp")
//            println("THIRD: $thirdTmp")
            val firstValue = firstTmp.maxByOrNull { it.value }?.key!!
            val secondValue = secondTmp.maxByOrNull { it.value }?.key!!
            val thirdValue = thirdTmp.maxByOrNull { it.value }?.key!!
            otherScanner.coords = Triple(firstValue - scanner.coords!!.first, secondValue - scanner.coords!!.second, thirdValue - scanner.coords!!.third)
            println("COORDS SCANNER $j: ${otherScanner.coords}")
        }
        println("DONE")
    }
}

fun figureOutRotation(otherScanner: Scanner, scannerBeacon: Triple<Int, Int, Int>, unparsedOtherScannerBeacon: Triple<Int, Int, Int>) {
    val newRotation = arrangeValues(scannerBeacon, unparsedOtherScannerBeacon)
    val otherScannerBeacon = mapBeacon(unparsedOtherScannerBeacon, newRotation)
    if (scannerBeacon.first == otherScannerBeacon.first) {
        if (scannerBeacon.second == otherScannerBeacon.second) {
            if (scannerBeacon.third == otherScannerBeacon.third) {
                otherScanner.rotation = newRotation
            } else if (scannerBeacon.third == -otherScannerBeacon.third) {
                otherScanner.rotation = Triple(newRotation.first, newRotation.second, -newRotation.third)
            } else {
                throw RuntimeException("$scannerBeacon, $otherScannerBeacon")
            }
        } else if (scannerBeacon.second == -otherScannerBeacon.second) {
            if (scannerBeacon.third == otherScannerBeacon.third) {
                otherScanner.rotation = Triple(newRotation.first, -newRotation.second, newRotation.third)
            } else if (scannerBeacon.third == -otherScannerBeacon.third) {
                otherScanner.rotation = Triple(newRotation.first, -newRotation.second, -newRotation.third)
            } else {
                throw RuntimeException("$scannerBeacon, $otherScannerBeacon")
            }
        } else {
            throw RuntimeException("$scannerBeacon, $otherScannerBeacon")
        }
    } else if (scannerBeacon.second == -otherScannerBeacon.second) {
        if (scannerBeacon.third == otherScannerBeacon.third) {
            otherScanner.rotation = Triple(-newRotation.first, newRotation.second, newRotation.third)
        } else if (scannerBeacon.third == -otherScannerBeacon.third) {
            otherScanner.rotation = Triple(-newRotation.first, newRotation.second, -newRotation.third)
        } else {
            throw RuntimeException("$scannerBeacon, $otherScannerBeacon")
        }
    } else if (scannerBeacon.second == -otherScannerBeacon.second) {
        if (scannerBeacon.third == otherScannerBeacon.third) {
            otherScanner.rotation = Triple(-newRotation.first, -newRotation.second, newRotation.third)
        } else if (scannerBeacon.third == -otherScannerBeacon.third) {
            otherScanner.rotation = Triple(-newRotation.first, -newRotation.second, -newRotation.third)
        } else {
            throw RuntimeException("$scannerBeacon, $otherScannerBeacon")
        }
    } else {
        throw RuntimeException("$scannerBeacon, $otherScannerBeacon")
    }
}

fun mapBeacon(unparsedOtherScannerBeacon: Triple<Int, Int, Int>, newRotation: Triple<Int, Int, Int>): Triple<Int, Int, Int> {
    return when(newRotation) {
        Triple(1, 2, 3) -> unparsedOtherScannerBeacon
        Triple(1, 3, 2) -> Triple(unparsedOtherScannerBeacon.first, unparsedOtherScannerBeacon.third, unparsedOtherScannerBeacon.second)
        Triple(2, 1, 3) -> Triple(unparsedOtherScannerBeacon.second, unparsedOtherScannerBeacon.first, unparsedOtherScannerBeacon.third)
        Triple(2, 3, 1) -> Triple(unparsedOtherScannerBeacon.second, unparsedOtherScannerBeacon.third, unparsedOtherScannerBeacon.first)
        Triple(3, 1, 2) -> Triple(unparsedOtherScannerBeacon.third, unparsedOtherScannerBeacon.first, unparsedOtherScannerBeacon.second)
        Triple(3, 2, 1) -> Triple(unparsedOtherScannerBeacon.third, unparsedOtherScannerBeacon.second, unparsedOtherScannerBeacon.first)
        else -> throw RuntimeException("$unparsedOtherScannerBeacon, $newRotation")
    }
}

fun arrangeValues(scannerBeacon: Triple<Int, Int, Int>, otherScannerBeacon: Triple<Int, Int, Int>): Triple<Int, Int, Int> {
    if (abs(scannerBeacon.first) == abs(otherScannerBeacon.first)) {
        if (abs(scannerBeacon.second) == abs(otherScannerBeacon.second)) {
            if (abs(scannerBeacon.third) == abs(otherScannerBeacon.third)) {
                return Triple(1, 2, 3)
            } else {
                throw RuntimeException("$scannerBeacon, $otherScannerBeacon")
            }
        } else if (abs(scannerBeacon.second) == abs(otherScannerBeacon.third)) {
            return Triple(1, 3, 2)
        } else {
            throw RuntimeException("$scannerBeacon, $otherScannerBeacon")
        }
    } else if (abs(scannerBeacon.first) == abs(otherScannerBeacon.second)) {
        if (abs(scannerBeacon.third) == abs(otherScannerBeacon.third)) {
            return Triple(2, 1, 3)
        } else if (abs(scannerBeacon.second) == abs(otherScannerBeacon.third)) {
            return Triple(2, 3, 1)
        } else {
            throw RuntimeException("$scannerBeacon, $otherScannerBeacon")
        }
    } else {
        if (abs(scannerBeacon.second) == abs(otherScannerBeacon.second)) {
            return Triple(3, 2, 1)
        } else if (abs(scannerBeacon.second) == abs(otherScannerBeacon.third)) {
            return Triple (3, 1, 2)
        } else {
            throw RuntimeException("$scannerBeacon, $otherScannerBeacon")
        }
    }
}
