package days

import java.io.File

fun main() {
//    run4a()
    run4b()
}

class Board(val boardList: List<Int>) {
    private val markedList: MutableList<Int> = mutableListOf()
    private var isActive = true
    fun hasBingo(): Boolean {
        if (markedList.size < 5) return false
        //Rows
        return markedList.containsAll(boardList.subList(0, 5)) ||
                markedList.containsAll(boardList.subList(5, 10)) ||
                markedList.containsAll(boardList.subList(10, 15)) ||
                markedList.containsAll(boardList.subList(15, 20)) ||
                markedList.containsAll(boardList.subList(20, 25)) || //Columns
                markedList.containsAll(mutableListOf(boardList[0], boardList[5], boardList[10], boardList[15], boardList[20])) ||
                markedList.containsAll(mutableListOf(boardList[1], boardList[6], boardList[11], boardList[16], boardList[21])) ||
                markedList.containsAll(mutableListOf(boardList[2], boardList[7], boardList[12], boardList[17], boardList[22])) ||
                markedList.containsAll(mutableListOf(boardList[3], boardList[8], boardList[13], boardList[18], boardList[23])) ||
                markedList.containsAll(mutableListOf(boardList[4], boardList[9], boardList[14], boardList[19], boardList[24]))
    }

    fun numberMarked(num: Int) {
        markedList.add(num)
    }

    fun getFinalScore(): Int {
        return boardList.filter { !markedList.contains(it) }.sum()
    }

    fun deactivate() {
        isActive = false
    }

    fun isActive(): Boolean {
        return isActive
    }
}

fun run4a() {
    val list = File("src/main/resources/04/04-input-kevin.txt").readLines()
    val bingoInput = list[0].split(",").map { it.toInt() }
    val boardsList = mutableListOf<Board>()
    for (i in 2..list.size step 6) {
        val board = mutableListOf<List<Int>>()
        for (j in 0..4) {
            board.add(list[i + j].split(" ").filter { it != "" }.map { it.toInt() })
        }

        boardsList.add(Board(board.flatten()))
    }

    for (input in bingoInput) {
        for(board in boardsList) {
            if (board.boardList.contains(input)) {
                board.numberMarked(input)
                if (board.hasBingo()) {
                    println("BOARD ${board.boardList} has bingo after input $input!")
                    println("FINAL RESULT: ${board.getFinalScore()} * input = ${board.getFinalScore() * input}")
                    return
                }
            }
        }
    }
}

fun run4b() {
    val list = File("src/main/resources/04/04-input-kevin.txt").readLines()
    val bingoInput = list[0].split(",").map { it.toInt() }
    val boardsList = mutableListOf<Board>()
    for (i in 2..list.size step 6) {
        val board = mutableListOf<List<Int>>()
        for (j in 0..4) {
            board.add(list[i + j].split(" ").filter { it != "" }.map { it.toInt() })
        }

        boardsList.add(Board(board.flatten()))
    }

    for (input in bingoInput) {
        for(board in boardsList) {
            if (board.boardList.contains(input)) {
                board.numberMarked(input)
                if (board.hasBingo()) {
                    if (board.isActive()) {
                        println("BOARD ${board.boardList} has bingo after input $input, deactivating!")
                        println("FINAL RESULT: ${board.getFinalScore()} * input = ${board.getFinalScore() * input}")
                        board.deactivate()
                    }
                }
            }
        }
    }
}

