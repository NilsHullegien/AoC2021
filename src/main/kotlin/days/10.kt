package days

import java.io.File
import java.lang.Math.floor
import java.util.*

fun main() {
//    run10a()
    run10b()
}

fun run10a() {
    val list = File("src/main/resources/10/10-input.txt").readLines()
        .map { line -> line.toCharArray().toList() }
    val chunkStack = Stack<Char>()
    var score = 0
    for (line in list) {
        chunkStack.clear()
        for (char in line) {
            when (char) {
                '[' -> chunkStack.push(']')
                '(' -> chunkStack.push(')')
                '<' -> chunkStack.push('>')
                '{' -> chunkStack.push('}')
                ']' -> {
                    if (chunkStack.peek().equals(']')) {
                        chunkStack.pop()
                    } else {
                        println("EXPECTED ] but was $char in line $line")
                        score += 57
                        break
                    }
                }
                ')' -> {
                    if (chunkStack.peek().equals(')')) {
                        chunkStack.pop()
                    } else {
                        println("EXPECTED ) but was $char in line $line")
                        score += 3
                        break
                    }
                }
                '>' -> {
                    if (chunkStack.peek().equals('>')) {
                        chunkStack.pop()
                    } else {
                        println("EXPECTED > but was $char in line $line")
                        score += 25137
                        break
                    }
                }
                '}' -> {
                    if (chunkStack.peek().equals('}')) {
                        chunkStack.pop()
                    } else {
                        println("EXPECTED } but was $char in line $line")
                        score += 1197
                        break
                    }
                }
            }
        }
    }
    println("10a: $score")
}

fun run10b() {
    val list = File("src/main/resources/10/10-input.txt").readLines()
        .map { line -> line.toCharArray().toList() }
    val chunkStack = Stack<Char>()
    val scoreList = mutableListOf<Long>()
    for (line in list) {
        var isCorrupted = false
        chunkStack.clear()
        for (char in line) {
            when (char) {
                '[' -> chunkStack.push(']')
                '(' -> chunkStack.push(')')
                '<' -> chunkStack.push('>')
                '{' -> chunkStack.push('}')
                ']' -> {
                    if (chunkStack.peek().equals(']')) {
                        chunkStack.pop()
                    } else {
                        println("EXPECTED ] but was $char in line $line")
                        isCorrupted = true
                        break
                    }
                }
                ')' -> {
                    if (chunkStack.peek().equals(')')) {
                        chunkStack.pop()
                    } else {
                        println("EXPECTED ) but was $char in line $line")
                        isCorrupted = true
                        break
                    }
                }
                '>' -> {
                    if (chunkStack.peek().equals('>')) {
                        chunkStack.pop()
                    } else {
                        println("EXPECTED > but was $char in line $line")
                        isCorrupted = true
                        break
                    }
                }
                '}' -> {
                    if (chunkStack.peek().equals('}')) {
                        chunkStack.pop()
                    } else {
                        println("EXPECTED } but was $char in line $line")
                        isCorrupted = true
                        break
                    }
                }
            }
        }
        if (!isCorrupted) {
            println("INCOMPLETE LINE $line with chunkstack $chunkStack")
            var score = 0L
            while (!chunkStack.isEmpty()) {
                score *= 5L
                when(chunkStack.pop()) {
                    ')' -> score += 1L
                    ']' -> score += 2L
                    '}' -> score += 3L
                    '>' -> score += 4L
                }
            }
            scoreList.add(score)
        }
    }
    scoreList.sort()
    println("10b: ${scoreList[scoreList.size/2]}")
}
