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
}

class LeafFish(var value: Int, depth: Int, parent: NodeFish?): Fish(depth, parent) {
    override fun toString(): String {
        return "LeafFish(${super.toString()}, value=$value)"
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
}

fun main() {
    run18a()
}

fun run18a() {
    val input = File("src/main/resources/18/split/18-test-1.txt").readLines()
    val lines = mutableListOf<Fish>()
    for (line in input) {
        lines.add(parseJsonToFish(Klaxon().parseJsonArray(StringReader(line)), 0, null))
    }
    var line = lines[0]
    for (i in 0 until lines.size) {
//        val newLine = lines[i]
//        line.updateDepth()
//        line = NodeFish(line, newLine, 0, null)
//        println("After addition: $line")
        while (true) { //True until no more explosions and splits occur
            var hasExploded = false
            var hasSplit = false
            if (hasExplodeCriteria(line)) {
                val explodeFish = line.getFirstExplodingFish()!!

                val firstLeftValue = getFirstValueLeft(explodeFish)
                val firstRightValue = getFirstValueRight(explodeFish)

                var parentExplodeFish = explodeFish.parent ?: throw RuntimeException("HAAAAALP")
                if (parentExplodeFish.left == explodeFish) {
                    val leftLeaf = LeafFish(0, parentExplodeFish.depth, parentExplodeFish.parent)
                    val rightLeaf = LeafFish(
                        (explodeFish.right as LeafFish).value + (parentExplodeFish.right as LeafFish).value,
                        parentExplodeFish.depth,
                        parentExplodeFish.parent
                    )
                    parentExplodeFish = NodeFish(leftLeaf, rightLeaf, parentExplodeFish.depth, parentExplodeFish.parent)
                } else {
                    val leftLeaf = LeafFish(
                        (explodeFish.left as LeafFish).value + (parentExplodeFish.left as LeafFish).value,
                        parentExplodeFish.depth,
                        parentExplodeFish.parent
                    )
                    val rightLeaf = LeafFish(0, parentExplodeFish.depth, parentExplodeFish.parent)
                    parentExplodeFish = NodeFish(leftLeaf, rightLeaf, parentExplodeFish.depth, parentExplodeFish.parent)
                }
                if (parentExplodeFish.parent!!.left == explodeFish.parent) {
                    parentExplodeFish.parent!!.left = parentExplodeFish
                } else {
                    parentExplodeFish.parent!!.right = parentExplodeFish
                }

                if (firstLeftValue != null) {
                    firstLeftValue.value += (explodeFish.left as LeafFish).value
                }

                if (firstRightValue != null) {
                    firstRightValue.value += (explodeFish.right as LeafFish).value
                }

                println("After explode: $line")
                hasExploded = true
            }
            if (!hasExploded && hasValueOver9(line)) {
                val splittingFish = line.getFirstSplittingFish()!!
                println("SPLIT FISH $splittingFish")
                println("PARENT SPLIT FISH $splittingFish")
                val newSplittedFish = NodeFish(
                    LeafFish(floor(splittingFish.value / 2f).toInt(), splittingFish.depth, splittingFish.parent),
                    LeafFish(ceil(splittingFish.value / 2f).toInt(), splittingFish.depth, splittingFish.parent),
                    splittingFish.depth,
                    splittingFish.parent
                )
                println("NEW SPLITTED FISH: $newSplittedFish")
                if (splittingFish.parent!!.left == splittingFish) {
                    splittingFish.parent!!.left = newSplittedFish
                } else {
                    splittingFish.parent!!.right = newSplittedFish
                }
                hasSplit = true
                println("After split: $line")
            }

            if (!hasExploded && !hasSplit) {
                println("FULLY OPTIMIZED!")
                break
            }
        }
        println("After everything: $line")
    }
}

fun getFirstValueLeft(explodeFish: NodeFish): LeafFish? {
    if (explodeFish.parent == null) {
        return null
    }
    if (explodeFish.parent!!.left == explodeFish) {
        return getFirstValueLeft(explodeFish.parent!!)
    }
    return rightMostLeafFish(explodeFish.parent!!.left)
}

fun getFirstValueRight(explodeFish: NodeFish): LeafFish? {
    if (explodeFish.parent == null) {
        return null
    }
    if (explodeFish.parent!!.right == explodeFish) {
        return getFirstValueRight(explodeFish.parent!!)
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
    if (input[0] is Int) {
        leftPartFish = LeafFish(input[0] as Int, depth, parentFish)
    }

    if (input[1] is Int) {
        rightPartFish = LeafFish(input[1] as Int, depth, parentFish)
    }

    val resFish = NodeFish(leftPartFish, rightPartFish, depth, parentFish)
    if (input[0] is JsonArray<*>) {
        resFish.left = parseJsonToFish(input[0] as JsonArray<*>, depth + 1,  resFish)
    }

    if (input[1] is JsonArray<*>) {
        resFish.right = parseJsonToFish(input[1] as JsonArray<*>, depth + 1, resFish)
    }
    return resFish
}
