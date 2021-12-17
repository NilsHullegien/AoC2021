package days

import java.io.File

fun main() {
    val input = File("src/main/resources/16/16-input.txt").readLines()
        .map { line -> line.toCharArray().map { hexToBinaryMap[it] }.joinToString("") }.first()
    println("$input, ${input.length}")
    val packets = parse(input).first
//    run16a(packets)
    run16b(packets)
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

    abstract fun getValue(): Long

}

class LiteralPacket(version: Int, packetTypeId: Int, private val fullBitsNumber: Long): Packet(version, packetTypeId) {
    override fun toString(): String {
        return "LiteralPacket(${super.toString()},fullBitsNumber=$fullBitsNumber)"
    }

    override fun getValue(): Long {
        return fullBitsNumber
    }

}

abstract class OperatorPacket(version: Int, packetTypeId: Int, val subPackets: List<Packet>): Packet(version, packetTypeId) {
    override fun getVersionTotal(): Int {
        return version + subPackets.sumOf { it.getVersionTotal() }
    }
}

class SumPacket(version: Int, packetTypeId: Int, subPackets: List<Packet>) : OperatorPacket(version, packetTypeId, subPackets) {
    override fun getValue(): Long {
        return subPackets.sumOf { it.getValue() }
    }
    override fun toString(): String {
        return "SumPacket(${super.toString()},subPackets=$subPackets)"
    }

}

class ProductPacket(version: Int, packetTypeId: Int, subPackets: List<Packet>) : OperatorPacket(version, packetTypeId, subPackets) {
    override fun getValue(): Long {
        var total = 1L
        for (subPacket in subPackets) {
            total *= subPacket.getValue()
        }
        return total
    }
    override fun toString(): String {
        return "ProductPacket(${super.toString()},subPackets=$subPackets)"
    }
}

class MinimumPacket(version: Int, packetTypeId: Int, subPackets: List<Packet>) : OperatorPacket(version, packetTypeId, subPackets) {
    override fun getValue(): Long {
        return subPackets.minOf { it.getValue() }
    }
    override fun toString(): String {
        return "MinimumPacket(${super.toString()},subPackets=$subPackets)"
    }
}

class MaximumPacket(version: Int, packetTypeId: Int, subPackets: List<Packet>) : OperatorPacket(version, packetTypeId, subPackets) {
    override fun getValue(): Long {
        return subPackets.maxOf { it.getValue() }
    }
    override fun toString(): String {
        return "MaximumPacket(${super.toString()},subPackets=$subPackets)"
    }
}

class GreaterThanPacket(version: Int, packetTypeId: Int, subPackets: List<Packet>) : OperatorPacket(version, packetTypeId, subPackets) {
    override fun getValue(): Long {
        if (subPackets.size > 2) {
            throw RuntimeException("NOT POSSIBLE!")
        }
        return if (subPackets[0].getValue() > subPackets[1].getValue()) {
            1L
        } else {
            0L
        }
    }
    override fun toString(): String {
        return "GreaterThanPacket(${super.toString()},subPackets=$subPackets)"
    }
}

class LessThanPacket(version: Int, packetTypeId: Int, subPackets: List<Packet>) : OperatorPacket(version, packetTypeId, subPackets) {
    override fun getValue(): Long {
        if (subPackets.size > 2) {
            throw RuntimeException("NOT POSSIBLE!")
        }
        return if (subPackets[0].getValue() < subPackets[1].getValue()) {
            1L
        } else {
            0L
        }
    }
    override fun toString(): String {
        return "LessThanPacket(${super.toString()},subPackets=$subPackets)"
    }
}

class EqualToPacket(version: Int, packetTypeId: Int, subPackets: List<Packet>) : OperatorPacket(version, packetTypeId, subPackets) {
    override fun getValue(): Long {
        if (subPackets.size > 2) {
            throw RuntimeException("NOT POSSIBLE!")
        }
        return if (subPackets[0].getValue() == subPackets[1].getValue()) {
            1L
        } else {
            0L
        }
    }
    override fun toString(): String {
        return "EqualToPacket(${super.toString()},subPackets=$subPackets)"
    }

}

fun run16a(packets: MutableList<Packet>) {
    println("16a: ${packets.sumOf { it.getVersionTotal() }}")
}

fun run16b(packets: MutableList<Packet>) {
    println(packets)
    println("16b: ${packets[0].getValue()}")
}

fun parse(input: String): Pair<MutableList<Packet>,String>  {
    if (input.length < 8) {
        return Pair(mutableListOf(), "")
    }
    val version = Integer.parseInt(input.substring(0, 3), 2)
    val typeId = Integer.parseInt(input.substring(3, 6), 2)
    println("V $version")
    println("T $typeId")
    val packetsList = mutableListOf<Packet>()
    when (typeId) {
        4 -> {
            println("LITERAL")
            val extractNumberNextInputStringPair = extractLiteralNumberFromSegmentBits(input.substring(6, input.length))
            packetsList.add(LiteralPacket(version, typeId, extractNumberNextInputStringPair.first))
            return Pair(packetsList, extractNumberNextInputStringPair.second)
        }
        else -> {
            println("OPERATOR")
            when(input[6]) {
                '0' -> {
                    println("typeId = 0")
                    //15 bits total length in bits of subpackets combined
                    val totalLength = Integer.parseInt(input.substring(7, 22), 2)
                    println("TOTAL LENGTH: $totalLength")
                    val subPackets = mutableListOf<Packet>()
                    var workingInput = input.substring(22, 22 + totalLength)
                    while (workingInput != "") {
                        val resultPair = parse(workingInput)
                        subPackets.addAll(resultPair.first)
                        workingInput = resultPair.second
                    }
                    packetsList.add(getCorrectOperatorPacket(version, typeId, subPackets))
                    return Pair(packetsList, input.substring(22 + totalLength))
                }
                '1' -> {
                    println("typeId = 1")
                    //11 bit nr of subpackets immediately contained in this packet
                    val nrOfSubPackets = Integer.parseInt(input.substring(7, 18), 2)
                    println("Nr of subpackets: $nrOfSubPackets")
                    val subPackets = mutableListOf<Packet>()
                    var nextInput = input.substring(18)
                    for (i in 1..nrOfSubPackets) {
                        println("LOOP $i")
                        val resultPair = parse(nextInput)
                        subPackets.addAll(resultPair.first)
                        nextInput = resultPair.second
                    }
                    packetsList.add(getCorrectOperatorPacket(version, typeId, subPackets))
                    return Pair(packetsList, nextInput)

                }
                else -> throw RuntimeException("CANNOT HAPPEN")
            }

        }
    }
}

fun extractLiteralNumberFromSegmentBits(input: String): Pair<Long, String> {
    println("EXTRACING NR FROM SEGMENT $input")
    var totalNr = ""
    var nextIdx = -1
    for (i in 0..input.length - 5 step 5) {
        val segment = input.substring(i, i+5)
        val prefixBit = segment[0]
        val nr = segment.substring(1..4)
        totalNr += nr
        if (prefixBit == '0') {
            println("SEGMENT $segment, value ${totalNr.toLong(2)}")
            nextIdx = i + 5
            break
        }
    }
    println("TOTAL NR: $totalNr (${totalNr.toLong(2)}), returning substring ${input.substring(nextIdx)}")
    return Pair(totalNr.toLong(2), input.substring(nextIdx))
}

fun getCorrectOperatorPacket(version: Int, packetTypeId: Int, subPackets: List<Packet>): OperatorPacket {
    return when (packetTypeId) {
        0 -> SumPacket(version, packetTypeId, subPackets)
        1 -> ProductPacket(version, packetTypeId, subPackets)
        2 -> MinimumPacket(version, packetTypeId, subPackets)
        3 -> MaximumPacket(version, packetTypeId, subPackets)
        4 -> throw RuntimeException("4 is not operator ID")
        5 -> GreaterThanPacket(version, packetTypeId, subPackets)
        6 -> LessThanPacket(version, packetTypeId, subPackets)
        7 -> EqualToPacket(version, packetTypeId, subPackets)
        else -> throw RuntimeException("BROKEN")
    }
}
