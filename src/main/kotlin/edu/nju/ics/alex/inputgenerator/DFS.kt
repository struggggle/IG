package edu.nju.ics.alex.inputgenerator

import java.util.*
import kotlin.system.exitProcess

/**
 * implement the DFS exploration strategy
 * TODO: fix the bug somewhere, tons of shit won't work now
 * */


//a class that contain each explored state, its layout,
// and lastly intersected widget and sent input
//a stub layout
val stub = LayoutNode()
class ActionNode(val nodes: List<LayoutNode>){
    //lastly intersected widget and sent input
    //private var lastWidget = -1
    //private var lastInput = -1

    //list to store acceptable inputs
    private val actionList = mutableListOf<Pair<Actions, LayoutNode>>()
    private var currentInput = -1

    //find the first interactable widget
    init {
        /*lastWidget = 0
        while (lastWidget < nodes.size && nodes[lastWidget].actions.isEmpty()) {
            lastWidget++
        }

        if (lastWidget >= nodes.size) {
            lastWidget = -1
        }*/
        nodes[0].setAvailableActionsRecur(actionList)
    }

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (other !is ActionNode) return false
        return isSame(other)
    }

    override fun hashCode(): Int {
        return nodes.size
    }

    fun isSame(another: ActionNode) : Boolean
            = if (nodes.size != another.nodes.size) false
                else nodes[0].isSame(another.nodes[0], compareMode)

    fun nextChoice() : Pair<Actions, LayoutNode> {
       /* if (lastWidget == -1) return Actions.NULL to -1

        lastInput++
        if (lastInput >= nodes[lastWidget].actions.size) {
            lastWidget++
            while (lastWidget < nodes.size && nodes[lastWidget].actions.isEmpty()) {
                lastWidget++
            }

            lastInput = if (lastWidget == -1) -1 else 0
        }
        return if (lastInput == -1) Actions.NULL to -1
            else nodes[lastWidget].actions[lastInput] to nodes[lastWidget].instance*/
        currentInput++
        return if (currentInput < actionList.size) actionList[currentInput]
            else Actions.NULL to stub
    }

    fun lastChoice() : Pair<Actions, LayoutNode>
            = if (currentInput == -1) Actions.NULL to stub
                else actionList[currentInput]

}

//all explored states
val allNodes = mutableSetOf<ActionNode>()

//a stack to store explored states
val stack = LinkedList<ActionNode>()
//max depth for exploration, -1 for unlimited
val maxDepth = -1

fun searchDFS() {
    preSearch()//这里仅仅是启动一个app，并获得初始的layout.xml文件
    println("[Get the initial layout]")
    getCurrentLayout()
    stack.push(ActionNode(parse("$workingDir/$xmlFolder/layout$layoutCount.xml")))
    allNodes.add(stack.peek())//peek()的目的是获取栈顶元素，现在仅仅添加一个NODE

    println("[Start DFS search]")//感觉这里的DFS存在问题，运行到这里就停止了
    while (stack.isNotEmpty()) {
        //indicate whether succeed backtracking by sending back command
        var backFailed = false
        var nextChoice = stack.peek().nextChoice()
        val originalSize = stack.size
        while ((stack.size > maxDepth && maxDepth != -1) || nextChoice.first == Actions.NULL) {
            backFailed = backtrack()
            nextChoice = stack.peek().nextChoice()
        }
        if (stack.isNotEmpty()) {
            if (backFailed) {
                replay()
            }
            if (originalSize != stack.size) nextChoice = stack.peek().nextChoice()
            sendInput(nextChoice.first, nextChoice.second)
            waitForStable()
            getCurrentLayout()
            val newActionNode = ActionNode(parse("$workingDir/$xmlFolder/layout$layoutCount.xml"))
            if (newActionNode == stack.peek())
                continue

            stack.push(newActionNode)

            if (!allNodes.contains(newActionNode)) {
                allNodes.add(newActionNode)
            } else {
                if (!backtrack()) {
                    replay()
                }
            }
        }
    }
}

//try to backtrack by sending back keyevent
private fun backtrack() : Boolean {
    cmd.sendEvent("keyevent 4")
    waitForStable()
    getCurrentLayout()
    val newActionNode = ActionNode(parse("$workingDir/$xmlFolder/layout$layoutCount.xml"))
    stack.pop()
    return newActionNode.isSame(stack.peek())
}

//backtrack by replaying all inputs
private fun replay() {
    val tem = stack.pop()
    //first try to find if we could start halfway
    val currentActionNode = ActionNode(parse("$workingDir/$xmlFolder/layout$layoutCount.xml"))//这里似乎很重要！
    val index = stack.indexOfFirst { it == currentActionNode }
    if (index != -1) {
       for (i in index downTo 0) {
           val (lastChoice, instance) = stack[i].lastChoice()
           sendInput(lastChoice, instance)
           waitForStable()
       }
    } else {
        restartApp()//stop and then start the app
        stack.reversed().forEach {
            val (lastChoice, instance) = it.lastChoice()//这是直接获取已经被执行了的Input
            sendInput(lastChoice, instance)
            waitForStable()
        }
    }
    stack.push(tem)
    getCurrentLayout()
    if (stack.peek() != ActionNode(parse("$workingDir/$xmlFolder/layout$layoutCount.xml"))) {
            println("[ERROR: unable to restore stack]")
            exitProcess(-1)
        }
}

//for testing only
fun main() {
    searchDFS()
}