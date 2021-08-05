package edu.nju.ics.alex.inputgenerator

/**
 * parse the xml file of the GUI layout dumped by uiautomator
 */

import org.json.JSONException
import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.JsonNodeType
import org.json.XML
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.lang.Exception
import java.util.*

data class JNode(val p: LayoutNode, val v: JsonNode)

/**
 * Different modes to compare GUI layouts
 * */
enum class Modes(val config: Int){
    //all attributes are compared and all nodes are considered
    ALL_STRICT(3),
    //all attributes are compared but marked nodes are ignored
    PARTIAL_STRICT(1),
    //Texts, coordinates, item numbers of listView, rotation, and shadowed parts (e.g., main view when drawer is out) are ignored,
    //but all nodes are considered
    ALL_LOOSE(2),
    //Texts, coordinates, item numbers of listView, rotation, and shadowed parts (e.g., main view when drawer is out) are ignored,
    //and marked nodes are ignored
    PARTIAL_LOOSE(0)
}

class LayoutNode {
    //information of each GUI widgets
    var id: String? = null
    var clazz: String? = null
    var pack: String? = null
    var text: String? = null
    var rotation: Int = 0
    var instance: Int = 0
    val coords = IntArray(4)//包含了一个widget的域

    //ways to interact with users, not considered when comparing
    var checkable: Boolean = false
    var checked: Boolean = false
    var clickable: Boolean = false
    var enabled: Boolean = false
    var focusable: Boolean = false
    var focused: Boolean = false
    var scrollable: Boolean = false
    var longClickable: Boolean = false
    var password: Boolean = false
    var selected: Boolean = false
    //a list of actions acceptable by the widget.
    val actions = mutableListOf<Actions>()

    //children nodes
    val children = mutableListOf<LayoutNode>()
    //whether ignored when comparing
    var shouldIgnore: Boolean = false

    /*a set of comparing functions*/
    fun isSame(another: LayoutNode, mode: Modes) : Boolean {
        //if not in the same oritation, must be different
        if (rotation != another.rotation) return false

        //parse configurations
        val isALL = (mode.config shr 1) == 1
        val isStrict = (mode.config % 2) == 1

        if (DEBUG) {
           // println("[first] $clazz $id [second] ${another.clazz} ${another.id}")
        }
        val isSameLoose =
            id == another.id && clazz == another.clazz && pack == another.pack
        val isSameStrict =
            text == another.text && rotation == another.rotation &&
                    coords.foldIndexed(true){ index, acc, i -> acc && i == another.coords[index]}
        if(!isSameLoose || (isStrict && !isSameStrict)) return false

        //In loose mode, if the current node is a drawer_layout and have two children, we only consider the second one
        //cus the first one is the main screen and is shadowed by the drawer
        if(!isStrict && clazz?.contains("DrawerLayout") ?: false && (children.size > 1 || another.children.size > 1)) {
            if (DEBUG) println("Is DrawerLayout, ignore the first")
            if(children.size != another.children.size) return false
            val drawer = children[1]
            val anotherDrawer = another.children[1]
            if(drawer.shouldIgnore xor anotherDrawer.shouldIgnore) {
                return false
            }
            return if (drawer.shouldIgnore) true
            else drawer.isSame(anotherDrawer, mode)
        }

        //In losse , if the current node is a ListView, a RecyclerView or a GridView, then only compare if the list or grid has any children
        if(!isStrict && clazz?.contains("""(ListView)|(GridView)|(RecyclerView)""".toRegex()) ?: false) {
            if (DEBUG) println("Is ListView/GridView, ignore children")
            return !(children.isEmpty() xor another.children.isEmpty())
        }

        //compare children
        var index = 0
        var anotherIndex = 0
        while (index < children.size && anotherIndex < another.children.size) {
            while (index < children.size && children[index].shouldIgnore) index++
            while (anotherIndex < another.children.size && another.children[anotherIndex].shouldIgnore) anotherIndex++
            if ((index == children.size) xor (anotherIndex == another.children.size)) return false
            if (index == children.size) return true
            if( !children[index].isSame(another.children[anotherIndex], mode)) return false
            index++
            anotherIndex++
        }
        return true
    }

    //determine acceptable actions of the widget
    fun setAvaliableActions(){
        if (clazz != null && clazz!!.contains("EditText")) {
            actions.addAll(arrayOf(Actions.SET_TEXT,
                Actions.CLEAR_TEXTFIELD))
        } else {
            if (checkable || clickable) {
                actions.addAll(
                    arrayOf(
                        Actions.CLICK//,
                        //Actions.CLICK_BOTTOM_RIGHT,
                        //Actions.CLICK_TOP_LEFT
                    )
                )
            }
            if (longClickable) {
                actions.addAll(
                    arrayOf(
                        Actions.LONGCLICK//,
                        //Actions.LONGCLICK_BOTTOM_RIGHT,
                        //Actions.LONGCLICK_TOP_LEFT
                    )
                )
            }
            if (scrollable) {
                actions.addAll(
                    arrayOf(
                        Actions.SWIPE_UP,
                        Actions.SWIPE_DOWN,
                        Actions.SWIPE_LEFT,
                        Actions.SWIPE_RIGHT
                    )
                )
            }
        }
    }

    //set available actions in recursive style. Able to ignore unenabled parts
    fun setAvailableActionsRecur(actionList: MutableList<Pair<Actions, LayoutNode>>) {
        if (clazz != null && clazz!!.contains("EditText")) {
            actions.addAll(arrayOf(Actions.SET_TEXT,
                Actions.CLEAR_TEXTFIELD))
            actionList.addAll(arrayOf(Actions.SET_TEXT to this, Actions.CLEAR_TEXTFIELD to this))
        } else {
            if (checkable || clickable) {
                actions.addAll(
                    arrayOf(
                        Actions.CLICK//,
                        //Actions.CLICK_BOTTOM_RIGHT,
                        //Actions.CLICK_TOP_LEFT
                    )
                )
                actionList.add(Actions.CLICK to this)
            }
            if (longClickable) {
                actions.addAll(
                    arrayOf(
                        Actions.LONGCLICK//,
                        //Actions.LONGCLICK_BOTTOM_RIGHT,
                        //Actions.LONGCLICK_TOP_LEFT
                    )
                )
                actionList.add(Actions.LONGCLICK to this)
            }
            if (scrollable) {
                actions.addAll(
                    arrayOf(
                        Actions.SWIPE_UP,
                        Actions.SWIPE_DOWN,
                        Actions.SWIPE_LEFT,
                        Actions.SWIPE_RIGHT
                    )
                )
                actionList.addAll(
                    arrayOf(
                        Actions.SWIPE_UP to this,
                        Actions.SWIPE_DOWN to this,
                        Actions.SWIPE_RIGHT to this,
                        Actions.SWIPE_LEFT to this
                    )
                )
            }
        }

        //recursively set actions
        val start = if (clazz?.contains("DrawerLayout") ?: false && children.size > 1) 1 else 0
        for (i in start until children.size) {
            children[i].setAvailableActionsRecur(actionList)
        }
    }

    //debug purpose
    fun printTree(tab: String) {
        println("$tab$clazz $instance")
        printt(tab)
        for (node in children) {
            node.printTree("$tab ")
        }
    }
    fun printt(tab: String) {
        val fields = javaClass.declaredFields
        fields.forEach {
            if(it.name != "coords" && it.name != "children") {
                try {
                    println("$tab${it.name}: ${it.get(this)}")
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        print(tab)
        coords.forEachIndexed { index, i ->
            print(" coords $index $i")
        }
        println()
    }
}

// used to print the Json objects
val PRETTY_PRINT_INDENT_FACTOR = 4

/**
 * parse the xml file into a list of layoutNode (with hierarchy as children)
 * Input: the path of the xml file; Output: the list of LayoutNode
 * */
fun parse(path: String) : List<LayoutNode> {//LayoutNode包含了一个节点所涉及的所有属性信息
    val results = mutableListOf<LayoutNode>()
    var count = -1
    val reader = try {
        BufferedReader(FileReader(File(path)))//读取layout文件到reader
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
    if(reader != null) {
        val contentBuilder = StringBuilder()
        var tem = reader.readLine()//TODO 对文件逐行读取到contentBuilder
        while (tem != null){
            contentBuilder.append(tem)
            tem = reader.readLine()
        }
        val content = contentBuilder.toString()//将layout的所有内容读取到content
        if (content.isEmpty() ){
            println("Error: failed to load layout xml file.")
            return listOf(stub)
        }
        try {
            //TODO: seems that XML can directly generate Json object from a reader. Try it out!
            val layout = XML.toJSONObject(content)//将读取到到layout内容转换为json对象
            val out = layout.toString(PRETTY_PRINT_INDENT_FACTOR)//打印缩进系数
            //println(out)//到时候需要输出一个看看，以后输出到文件看看

            val nodeStack = Stack<JNode>()
            val mapper = ObjectMapper(JsonFactory())
            val tree = mapper.readTree(out)//读取为一个tree到形式
            val rootNode = tree.get("hierarchy").get("node")//获得根节点？
            nodeStack.push(JNode(LayoutNode(), rootNode))//layoutNode是一个节点类，包含类各种属性

            //pre-order iteration//对tree中到节点进行遍历
            while (nodeStack.isNotEmpty()) {
                val jNode = nodeStack.pop()
                val parent = jNode.p  //这里p是节点在android中的widget属性，v是jnode节点
                val node = jNode.v
                processObject(node, nodeStack, parent, results)//Process each JsonNode to a LayoutNode
                parent.instance = count++
                //not implemented yet
                parent.setAvaliableActions()
                results.add(parent)//得到到是一个完整到节点集合？
            }

            //add the rotation parameter to the root node

            results[0].rotation = tree.get("hierarchy").get("rotation").intValue()
        }catch (e: JSONException) {
            e.printStackTrace()
        }
    }
    return results
}

/**
 * Process each JsonNode to a LayoutNode
 * Input: jnode-> the to-be-processed JsonNode, nodeStack: the stack of the printTree traverse,
 * parent: the parent LayoutNode, results: the list of LayoutNode
 * */
fun processObject(jnode: JsonNode, nodeStack: Stack<JNode>, parent: LayoutNode, results: MutableList<LayoutNode>) {
    val fieldsIterator = jnode.fields()//获得widget的属性集合
    fieldsIterator.forEach{ (key, value) ->
        when(value.nodeType){
            JsonNodeType.OBJECT -> {
                val child = LayoutNode()
                parent.children.add(child)
                nodeStack.push(JNode(child, value))
            }
            JsonNodeType.ARRAY -> processArray(value, nodeStack, parent)//Process the array of JsonNodes, push each one onto the stack
            else -> fillValues(parent, key, value)//Fill a values of a LayoutNode containing an actual GUI widget
        }
    }
}

/**
 * Process the array of JsonNodes, push each one onto the stack
 * */
fun processArray(jnode: JsonNode, nodeStack: Stack<JNode>, parent: LayoutNode) {
    val prep = Stack<JNode>()
    jnode.elements().forEach {
        val child = LayoutNode()
        parent.children.add(child)
        prep.push(JNode(child, it))
    }
    while(prep.isNotEmpty()) {
        nodeStack.push(prep.pop())
    }
}

/**
 * Fill a values of a LayoutNode containing an actual GUI widget
 * Input: parent -> the to-be-filled layoutNode, key -> the key, value -> the value
 * */

fun fillValues(parent: LayoutNode, key: String, value : JsonNode) {
    when(key) {
        "resource-id" -> parent.id = value.asText()
        "class" -> parent.clazz = value.asText()
        "package" -> parent.pack = value.asText()
        "text" -> parent.text = value.asText()
        "checkable", "checked", "clickable", "enabled", "focusable", "focused",
        "scrollable", "password", "selected" -> {
            try {
                val judge = parent.javaClass.getDeclaredField(key)
                judge.isAccessible = true
                judge.setBoolean(parent, value.asBoolean())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        "long-clickable" -> parent.longClickable = value.asBoolean()
        "bounds" -> matchNumbers(parent, value.asText())
        else -> {}
    }
}

/**
 * Get the numbers of the bounds to determine the area of each GUI widget
 * Input: parent: the to-be-filled LayoutNode, cords -> the coordinates in String
 * */

fun matchNumbers(parent: LayoutNode, coords: String) {
    var hit = 0
    val matches = """([0-9]+)""".toRegex().findAll(coords)
    matches.forEach {
        parent.coords[hit] = it.value.toInt()
        hit++
    }
}

fun main(args: Array<String>) {
    //val tem = parse("/home/alex-wang/Data1/R-UGA2019/UseCase/case2/ending.xml")
    //tem[0].setAvailableActionsRecur(mutableListOf())
    //tem[0].printTree(" ")
    //val tem1 = parse("/home/alex-wang/Data1/R-UGA2019/UseCase/case3/starting.xml")
    //println(tem[0].isSame(tem1[0], Modes.ALL_STRICT))
    preSearch()
    getCurrentLayout()
    getCurrentLayout()
    val tem1 = ActionNode(parse("$workingDir/$xmlFolder/layout1.xml"))
    val tem2 = ActionNode(parse("$workingDir/$xmlFolder/layout2.xml"))
    println(tem1 == tem2)
}