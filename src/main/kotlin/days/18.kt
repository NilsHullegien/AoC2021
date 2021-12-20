package days

import com.beust.klaxon.JsonArray
import com.beust.klaxon.Klaxon
import java.io.File
import java.io.StringReader
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max

abstract class Fish(var depth: Int, var parent: NodeFish?) {
    override fun toString(): String {
        return "depth=$depth"
    }
    abstract fun getMaxDepth(): Int
    abstract fun getMaxValue(): Int
    abstract fun getFirstExplodingFish(): NodeFish?
    abstract fun getFirstSplittingFish(): LeafFish?
    abstract fun updateDepth()
    abstract fun getMagnitude(): Int
}

class NodeFish(var left: Fish, var right: Fish, depth: Int, parent: NodeFish?): Fish(depth, parent) {
    override fun toString(): String {
        return "NodeFish(${super.toString()}, " +
                "\n   left=$left, " +
                "\n   right=$right" +
                ")\n"
    }

    override fun getMaxDepth(): Int {
        return max(left.getMaxDepth(), right.getMaxDepth())
    }

    override fun getMaxValue(): Int {
        return max(left.getMaxValue(), right.getMaxValue())
    }

    override fun getFirstExplodingFish(): NodeFish? {
        if (this.depth >= 4) {
            return this
        }
        if (left.getFirstExplodingFish() != null) {
            return left.getFirstExplodingFish()
        } else {
            return right.getFirstExplodingFish()
        }
    }

    override fun getFirstSplittingFish(): LeafFish? {
        return if (left.getFirstSplittingFish() != null) {
            left.getFirstSplittingFish()
        } else {
            right.getFirstSplittingFish()
        }
    }

    override fun updateDepth() {
        this.depth++
        left.updateDepth()
        right.updateDepth()
    }

    override fun getMagnitude(): Int {
        return 3 * left.getMagnitude() + 2 * right.getMagnitude()
    }
}

class LeafFish(var value: Int, depth: Int, parent: NodeFish?): Fish(depth, parent) {
    override fun toString(): String {
        return "LeafFish(${super.toString()}, value=$value)"
    }

    override fun getMagnitude(): Int {
        return value
    }

    override fun getMaxDepth(): Int {
        return depth
    }

    override fun getMaxValue(): Int {
        return value
    }

    override fun getFirstExplodingFish(): NodeFish? {
        return null
    }

    override fun getFirstSplittingFish(): LeafFish? {
        return if (value > 9) {
            this
        } else {
            null
        }
    }

    override fun updateDepth() {
        depth++
    }
}

class NullFish(depth: Int, parent: NodeFish?): Fish(depth, parent) {
    override fun getMaxDepth(): Int {
        return -1
    }

    override fun getMaxValue(): Int {
        return -1
    }

    override fun getFirstExplodingFish(): NodeFish? {
        return null
    }

    override fun getFirstSplittingFish(): LeafFish? {
        return null
    }

    override fun updateDepth() {
        depth++
    }

    override fun getMagnitude(): Int {
        return -1
    }
}

fun main() {
//    run18a()
    run18b()
}

fun run18a() {
    val input = File("src/main/resources/18/18-input.txt").readLines()
    val lines = mutableListOf<Fish>()
    for (line in input) {
        lines.add(parseJsonToFish(Klaxon().parseJsonArray(StringReader(line)), 0, null))
    }
    println("18a: ${calculateMagnitude(lines)}")
}

fun run18b() {

    val inputTmp = File("src/main/resources/18/18-input.txt").readLines()
    var maxValue = -1
    for (x in inputTmp.indices) {
        for (y in inputTmp.indices) {

            if (x == y) {
                continue
            }
            val input = File("src/main/resources/18/18-input.txt").readLines()
            val lines = mutableListOf<Fish>()
            for (line in input) {
                lines.add(parseJsonToFish(Klaxon().parseJsonArray(StringReader(line)), 0, null))
            }

            val tempMax = calculateMagnitude(mutableListOf(lines[x], lines[y]))
            if (tempMax > maxValue) {
//                println("NEW MAX ($tempMax) WITH ${lines[x]} \nAND\n ${lines[y]}")
                maxValue = tempMax
            }
        }
    }
    println("18b: $maxValue")
}

fun calculateMagnitude(lines: MutableList<Fish>): Int {
    var line = lines[0]

    for (i in 1 until lines.size) {
        val newLine = lines[i]
        line.updateDepth()
        newLine.updateDepth()
        val res = NodeFish(NullFish(-1, null), NullFish(-1, null), 0, null)
        line.parent = res
        newLine.parent = res
        res.left = line
        res.right = newLine
        line = res
//        println("After addition: $line")
        while (true) { //True until no more explosions and splits occur
            var hasExploded = false
            var hasSplit = false
            if (hasExplodeCriteria(line)) {
                val explodeFish = line.getFirstExplodingFish()!!
//                println("EXPLODE FISH: $explodeFish")

                val firstLeftValue = getFirstValueLeft(explodeFish, explodeFish)
                val firstRightValue = getFirstValueRight(explodeFish, explodeFish)
//                println("LEFT VALUE: $firstLeftValue")
//                println("RIGHT VALUE: $firstRightValue")

                val parentExplodeFish = explodeFish.parent ?: throw RuntimeException("HAAAAALP")
//                println("PARENT EXPLODE FISH: $parentExplodeFish")
                if (parentExplodeFish.left == explodeFish) {
                    parentExplodeFish.left = LeafFish(0, parentExplodeFish.depth, parentExplodeFish)
                } else if (parentExplodeFish.right == explodeFish) {
                    parentExplodeFish.right = LeafFish(0, parentExplodeFish.depth, parentExplodeFish)

                } else {
                    throw RuntimeException("SOMETHING GOING WRONG WITH EXPLODING;\n FISH $explodeFish\n ParentFish $parentExplodeFish")
                }

                if (firstLeftValue != null) {
                    firstLeftValue.value += (explodeFish.left as LeafFish).value
                }

                if (firstRightValue != null) {
                    firstRightValue.value += (explodeFish.right as LeafFish).value
                }

//                println("After explode: $line")
                hasExploded = true
            }
            if (!hasExploded && hasValueOver9(line)) {
                val splittingFish = line.getFirstSplittingFish()!!
                val parentSplitFish = splittingFish.parent ?: throw RuntimeException("HAAAAALP")
//                println("SPLIT FISH $splittingFish")
//                println("PARENT SPLIT FISH $parentSplitFish")
                val newSplitFish = NodeFish(NullFish(-1, null), NullFish(-1, null), splittingFish.depth + 1, parentSplitFish)
                val leftLeafFish =
                    LeafFish(floor(splittingFish.value / 2f).toInt(), splittingFish.depth + 1, newSplitFish)
                val rightLeafFish = LeafFish(ceil(splittingFish.value / 2f).toInt(), splittingFish.depth + 1, newSplitFish)
                newSplitFish.left = leftLeafFish
                newSplitFish.right = rightLeafFish
//                println("NEW SPLITTED FISH: $newSplitFish")
                if (parentSplitFish.left == splittingFish) {
                    parentSplitFish.left = newSplitFish
                } else if (parentSplitFish.right == splittingFish){
                    parentSplitFish.right = newSplitFish
                } else {
                    println("ERROR: $parentSplitFish does not contain subFish $splittingFish")
                    throw RuntimeException("SHOULD NOT HAPPEN!")
                }


//                println("After split: $line")
                hasSplit = true
            }

            if (!hasExploded && !hasSplit) {
//                println("FULLY OPTIMIZED!")
                break
            }
        }
    }
//    println("After everything: $line")
    return line.getMagnitude()
}

fun getFirstValueLeft(explodeFish: NodeFish, prevFish: Fish): LeafFish? {
    if (explodeFish.parent == null) {
        if (explodeFish.right == prevFish || explodeFish == prevFish) {
            return rightMostLeafFish(explodeFish.left)
        }
        return null
    }
    if (explodeFish.parent!!.left == explodeFish) {
        return getFirstValueLeft(explodeFish.parent!!, explodeFish)
    }
    return rightMostLeafFish(explodeFish.parent!!.left)
}

fun getFirstValueRight(explodeFish: NodeFish, prevFish: Fish): LeafFish? {
    if (explodeFish.parent == null) {
        if (explodeFish.left == prevFish || explodeFish == prevFish) {
            return leftMostLeafFish(explodeFish.right)
        }
        return null
    }
    if (explodeFish.parent!!.right == explodeFish) {
        return getFirstValueRight(explodeFish.parent!!, explodeFish)
    }
    return leftMostLeafFish(explodeFish.parent!!.right)
}

fun leftMostLeafFish(fish: Fish): LeafFish? {
    if (fish is LeafFish) {
        return fish
    }
    return leftMostLeafFish((fish as NodeFish).left)
}

fun rightMostLeafFish(fish: Fish): LeafFish? {
    if (fish is LeafFish) {
        return fish
    }
    return rightMostLeafFish((fish as NodeFish).right)
}


fun hasExplodeCriteria(line: Fish): Boolean {
    return line.getMaxDepth() >= 4
}

fun hasValueOver9(line: Fish): Boolean {
    return line.getMaxValue() > 9
}

fun parseJsonToFish(input: JsonArray<*>, depth: Int, parentFish: NodeFish?): Fish {
    var leftPartFish: Fish = NullFish(depth, parentFish)
    var rightPartFish: Fish = NullFish(depth, parentFish)
    val resFish = NodeFish(leftPartFish, rightPartFish, depth, parentFish)
    if (input[0] is Int) {
        leftPartFish = LeafFish(input[0] as Int, depth, resFish)
    }

    if (input[1] is Int) {
        rightPartFish = LeafFish(input[1] as Int, depth, resFish)
    }

    resFish.left = leftPartFish
    resFish.right = rightPartFish
    if (input[0] is JsonArray<*>) {
        resFish.left = parseJsonToFish(input[0] as JsonArray<*>, depth + 1,  resFish)
    }

    if (input[1] is JsonArray<*>) {
        resFish.right = parseJsonToFish(input[1] as JsonArray<*>, depth + 1, resFish)
    }
    return resFish
}
