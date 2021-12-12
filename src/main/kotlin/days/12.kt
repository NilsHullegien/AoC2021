package days

import java.io.File

fun main() {
    run12a()
    run12b()
}

class RouteA(val route: MutableList<String>, val visitedNodes: MutableList<String>) {
    override fun toString(): String {
        return "(route=${route.joinToString(",")}, visitedNodes=$visitedNodes)"
    }
}

class RouteB(val route: MutableList<String>, val visitedNodes: MutableMap<String, Int>) {
    override fun toString(): String {
        return "(route=${route.joinToString(",")}, visitedNodes=$visitedNodes)"
    }
}

fun run12a() {
    val edgesList = File("src/main/resources/12/12-input.txt").readLines()
        .map { line -> line.split("-") }.map { el -> Pair(el[0], el[1]) }.toList()
    val possibilitiesMap = hashMapOf<String, MutableList<String>>()
    for (edge in edgesList) {
        if (possibilitiesMap.containsKey(edge.first)) {
            possibilitiesMap[edge.first]?.add(edge.second)
        } else {
            possibilitiesMap[edge.first] = mutableListOf(edge.second)
        }

        if (possibilitiesMap.containsKey(edge.second)) {
            possibilitiesMap[edge.second]?.add(edge.first)
        } else {
            possibilitiesMap[edge.second] = mutableListOf(edge.first)
        }
    }

    val finishedRoutes = mutableListOf<RouteA>()
    val ongoingRoutes = mutableListOf<RouteA>()
    for (startEdge in possibilitiesMap["start"]!!) {
        if (startEdge[0].isLowerCase()) {
            ongoingRoutes.add(RouteA(mutableListOf("start", startEdge), mutableListOf("start", startEdge)))
        } else {
            ongoingRoutes.add(RouteA(mutableListOf("start", startEdge), mutableListOf("start")))
        }
    }
    while (ongoingRoutes.isNotEmpty()) {
        val currentRoute = ongoingRoutes.removeAt(0)
        val possibleNextNodes = possibilitiesMap[currentRoute.route.last()]
        for (nextNode in possibleNextNodes!!) {
            // Do not create a path here after this as we have visited the node already, includes "start"
            if (currentRoute.visitedNodes.contains(nextNode)) {
                continue
            }
            //Do not keep track of capital nodes
            if (nextNode[0].isUpperCase()) {
                val newRoute = currentRoute.route.toMutableList()
                newRoute.add(nextNode)
                ongoingRoutes.add(RouteA(newRoute, currentRoute.visitedNodes))
            }

            // FOUND THE END
            if (nextNode == "end") {
                val newRoute = currentRoute.route.toMutableList()
                val newVisitedNodes = currentRoute.visitedNodes.toMutableList()
                newRoute.add(nextNode)
                newVisitedNodes.add(nextNode)
                finishedRoutes.add(RouteA(newRoute, newVisitedNodes))
            }

            //Keep track of lowercase nodes
            if (nextNode[0].isLowerCase()) {
                val newRoute = currentRoute.route.toMutableList()
                val newVisitedNodes = currentRoute.visitedNodes.toMutableList()
                newRoute.add(nextNode)
                newVisitedNodes.add(nextNode)
                ongoingRoutes.add(RouteA(newRoute, newVisitedNodes))
            }
        }
    }
    println("12a: ${finishedRoutes.distinct().size}")
}

fun run12b() {
    val edgesList = File("src/main/resources/12/12-input.txt").readLines()
        .map { line -> line.split("-")}.map { el -> Pair(el[0], el[1]) }.toList()
    val possibilitiesMap = hashMapOf<String, MutableList<String>>()
    for (edge in edgesList) {
        if (possibilitiesMap.containsKey(edge.first)) {
            possibilitiesMap[edge.first]?.add(edge.second)
        } else {
            possibilitiesMap[edge.first] = mutableListOf(edge.second)
        }

        if (possibilitiesMap.containsKey(edge.second)) {
            possibilitiesMap[edge.second]?.add(edge.first)
        } else {
            possibilitiesMap[edge.second] = mutableListOf(edge.first)
        }
    }

    val finishedRoutes = mutableListOf<RouteB>()
    val ongoingRoutes = mutableListOf<RouteB>()
    for (startEdge in possibilitiesMap["start"]!!) {
        if (startEdge[0].isLowerCase()) {
            ongoingRoutes.add(RouteB(mutableListOf("start", startEdge), mutableMapOf(startEdge to 1)))
        } else {
            ongoingRoutes.add(RouteB(mutableListOf("start", startEdge), mutableMapOf()))
        }
    }
    while (ongoingRoutes.isNotEmpty()) {
        val currentRoute = ongoingRoutes.removeAt(0)
        val possibleNextNodes = possibilitiesMap[currentRoute.route.last()]
        for (nextNode in possibleNextNodes!!) {
            if (nextNode == "start") {
                continue
            }

            // FOUND THE END
            if (nextNode == "end") {
                val newRoute = currentRoute.route.toMutableList()
                newRoute.add(nextNode)
                finishedRoutes.add(RouteB(newRoute, currentRoute.visitedNodes))
                continue
            }

            if (currentRoute.visitedNodes.containsKey(nextNode)) {
                if (currentRoute.visitedNodes.values.maxOrNull() == 2) {
                    continue
                } else {
                    val newRoute = currentRoute.route.toMutableList()
                    val newVis =  currentRoute.visitedNodes.toMutableMap()
                    newRoute.add(nextNode)
                    newVis.replace(nextNode, 2)
                    ongoingRoutes.add(RouteB(newRoute, newVis))
                    continue
                }
            }
            //Do not keep track of capital nodes
            if (nextNode[0].isUpperCase()) {
                val newRoute = currentRoute.route.toMutableList()
                newRoute.add(nextNode)
                ongoingRoutes.add(RouteB(newRoute, currentRoute.visitedNodes))
                continue
            }

            //Keep track of lowercase nodes
            if (nextNode[0].isLowerCase()) {
                val newRoute = currentRoute.route.toMutableList()
                val newVisitedNodes =  currentRoute.visitedNodes.toMutableMap()
                newRoute.add(nextNode)
                newVisitedNodes[nextNode] = 1
                ongoingRoutes.add(RouteB(newRoute, newVisitedNodes))
                continue
            }
        }
    }
    println("12b: ${finishedRoutes.distinct().size}")
}
