package days

import java.io.File

fun main() {
    run16a()
    run16b()
}
val hexToBinaryMap = mutableMapOf(
    '0' to "0000",
    '1' to "0001",
    '2' to "0010",
    '3' to "0011",
    '4' to "0100",
    '5' to "0101",
    '6' to "0110",
    '7' to "0111",
    '8' to "1000",
    '9' to "1001",
    'A' to "1010",
    'B' to "1011",
    'C' to "1100",
    'D' to "1101",
    'E' to "1110",
    'F' to "1111"
)

abstract class Packet(val version: Int, private val packetTypeId: Int) {
    override fun toString(): String {
        return "Packet(version=$version, packetTypeId=$packetTypeId)"
    }

    open fun getVersionTotal(): Int {
        return version
    }
}

class LiteralPacket(version: Int, packetTypeId: Int, private val fullBitsNumber: Int): Packet(version, packetTypeId) {
    override fun toString(): String {
        return "LiteralPacket(${super.toString()},fullBitsNumber=$fullBitsNumber)"
    }
}

class OperatorPacket(version: Int, packetTypeId: Int, private val subPackets: List<Packet>): Packet(version, packetTypeId) {
    override fun toString(): String {
        return "OperatorPacket(${super.toString()},subPackets=$subPackets)"
    }

    override fun getVersionTotal(): Int {
        return version + subPackets.sumOf { it.getVersionTotal() }
    }
}

class NewStringPacket(val newInput: String): Packet(0, 0)

fun run16a() {
    val input = File("src/main/resources/16/16-test-6.txt").readLines()
        .map { line -> line.toCharArray().map { hexToBinaryMap[it] }.joinToString("") }.first()
    println(input)
    val packets = parseToPackets(input)
    println(packets)
    println(packets.first.sumOf { it.getVersionTotal() })
}

fun run16b() {

}

fun parseToPackets(input: String): Pair<MutableList<Packet>, Int> {
    println("PACKETS PARSE FROM INPUT $input")
    if (input.length < 8) {
        println("INPUT ENDED $input, RETURNING")
        return Pair(mutableListOf(), -1)
    }
    val version = Integer.parseInt(input.substring(0, 3), 2)
    val typeId = Integer.parseInt(input.substring(3, 6), 2)
    println(version)
    println(typeId)
    when (typeId) {
        4 -> {
            println("LITERAL VALUE")
            val packetNextPair = extractNumberFromSegmentBits(input, 6)
            println("NEXT VALUE,IDX: $packetNextPair")
            if (input.substring(packetNextPair.second).length < 8) {
                println("INPUT FOR IDX ${packetNextPair.second} IS DONE: ${input.substring(packetNextPair.second)}")
                return Pair(mutableListOf(LiteralPacket(version, typeId, packetNextPair.first)), packetNextPair.second)
            } else if (packetNextPair.second == -1) {
                return Pair(mutableListOf(LiteralPacket(version, typeId, packetNextPair.first)), -1)
            } else {
                val packetList = parseToPackets(input.substring(packetNextPair.second))
                packetList.first.add(LiteralPacket(version, typeId, packetNextPair.first))
                return packetList
            }
        }
        else -> {
            when (input[6]) {
                '0' -> {
                    println("LENGTH TYPE ID 0")
                    //15 bits total length in bits of subpackets combined
                    val totalLength = Integer.parseInt(input.substring(7, 22), 2)
                    println("TOTAL LENGTH: $totalLength")

                    val packetsPair = parseToPackets(input.substring(22, 22 + totalLength))
                    println("PACKETS PAIR: $packetsPair")
                    var lengthHasBeenReached = packetsPair.second
                    val fullPacketList: MutableList<Packet> = mutableListOf(OperatorPacket(version, typeId, packetsPair.first))
                    var flag = false
                    while (lengthHasBeenReached == -1) {
                        println("SECOND IS -1, INPUT IS $input")
                        val parsedPair = parseToPackets(input)
                        fullPacketList.add(OperatorPacket(version, typeId, parsedPair.first))
                        lengthHasBeenReached = parsedPair.second
                        flag = true
                    }
                    if (flag) {
                        return Pair(fullPacketList, 22 + totalLength)
                    }
                    println("COMPARING ${packetsPair.second} TO ${22 + totalLength}")
                    if (packetsPair.second < 22 + totalLength) {
                        println("FULL LENGTH NOT REACHED YET, CONTINUING")
                        val packetList = parseToPackets(input.substring(22+packetsPair.second, 22+totalLength))
                        packetList.first.add(OperatorPacket(version, typeId, packetsPair.first))
                        return packetList
                    }
                    println("ALL DONE WITH LENGTH TYPE ID 0!")
                    return Pair(mutableListOf(OperatorPacket(version, typeId, packetsPair.first)), packetsPair.second)
                }
                '1' -> {
                    println("LENGTH TYPE ID 1")
                    //11 bit nr of subpackets immediately contained in this packet
                    val nrOfSubPackets = Integer.parseInt(input.substring(7, 18), 2)
                    println("Nr of subpackets: $nrOfSubPackets")
                    val list = mutableListOf<Packet>()
                    var lastIdx = 18
                    for (i in 1..nrOfSubPackets) {
                        val pair = parseToSinglePacket(input.substring(lastIdx))
                        list.add(pair.first)
                        lastIdx += pair.second
                    }
                    println("DONE WITH TYPE 1")
                    return Pair(mutableListOf(OperatorPacket(version, typeId, list)), lastIdx)
                }
                else -> throw RuntimeException("BIT IS NOT 0 OR 1")
            }
        }
    }
}

fun parseToSinglePacket(input: String): Pair<Packet, Int> {
    println("PARSE SINGLE, analysing string $input")
    if (input.length < 8) {
        throw RuntimeException("Shouldnt happen")
    }
    val version = Integer.parseInt(input.substring(0, 3), 2)
    val typeId = Integer.parseInt(input.substring(3, 6), 2)
    println(version)
    println(typeId)
    when (typeId) {
        4 -> {
            println("LITERAL VALUE")
            val pair = extractNumberFromSegmentBits(input, 6)
            println("SINGLE PAIR: $pair")
            return Pair(LiteralPacket(version, typeId, pair.first), pair.second)
        }
        else -> {
            when (input[6]) {
                '0' -> {
                    println("LENGTH TYPE ID 0")
                    //15 bits total length in bits of subpackets combined
                    val totalLength = Integer.parseInt(input.substring(7, 22), 2)
                    println("TOTAL LENGTH: $totalLength")
                    //Get all segments from 22 onwards until the end
                    return Pair(OperatorPacket(version, typeId, parseToPackets(input.substring(22, 22 + totalLength)).first), 22 + totalLength)
                }
                '1' -> {
                    println("LENGTH TYPE ID 1")
                    //11 bit nr of subpackets immediately contained in this packet
                    val nrOfSubPackets = Integer.parseInt(input.substring(7, 18), 2)
                    println("NR of subpackets: $nrOfSubPackets")
                    val list = mutableListOf<Packet>()
                    var lastIdx = 18
                    for (i in 1..nrOfSubPackets) {
                        println("SINGLE PACKET LOOP $i")
                        val pair = parseToSinglePacket(input.substring(lastIdx))
                        list.add(pair.first)
                        lastIdx += pair.second
                    }
                    println("DONE WITH TYPE 1")
                    return Pair(OperatorPacket(version, typeId, list), lastIdx)
                }
                else -> throw RuntimeException("BIT IS NOT 0 OR 1")
            }
        }
    }
}

fun extractNumberFromSegmentBits(input: String, startIdx: Int): Pair<Int, Int> {
    println("EXTRACING NR FROM SEGMENT ${input.substring(startIdx)}")
    var totalNr = ""
    var nextIdx = -1
    for (i in startIdx..input.length - 5 step 5) {
        val segment = input.substring(i, i+5)
        val prefixBit = segment[0]
        val nr = segment.substring(1..4)
        totalNr += nr
        if (prefixBit == '0') {
            nextIdx = i + 5
            break
        }
    }
    println("TOTAL NR: $totalNr (${Integer.parseInt(totalNr, 2)}), returning idx $nextIdx")
    return Pair(Integer.parseInt(totalNr, 2), nextIdx)
}

