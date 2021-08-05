package edu.nju.ics.alex.inputgenerator

import org.xml.sax.SAXException
import java.io.IOException
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException
import kotlin.collections.mutableListOf
import org.w3c.dom.Node

/**
 * 获得每一个widget的path，然后还可以根据一个event的坐标，得到对应的widget的path
 * 找到了一个与image decoding相关的event，找到他的path，然后对这个layout里面所有对path进行遍历，比较相似性，然后进行排名。
 * 很可能出现的情况，同时存在多个event导致的layut具体相同的相似性
 * 关键是我们需要进行排名吗？如果是完全进行遍历，就不需要进行排名了。
 *
 * 有一个说法是说：我们并不需要将所有的image 都显示出来，仅仅是显示一定数量的就可以了，那么我们可以做一个排名，只有完成排名靠前的就能结束测试了。
 *
 * 我们是先跑不组合的，如果能达到一定数量的图片显示，就完成，如果不能，再尝试组合
 *
 * 我么对score是什么？应该是layout的相似性的打分。用一个计算公式。---》分支距离？
 * **/
    fun DOM(file: String):List<PathNode>? {
    //这里在一个节点集合中存储所有的节点，同时，每个节点中存储自己的父亲节点和子节点
        //存储所有node到path信息到列表
        var nodeList=mutableListOf<PathNode>()
        var currentPath=mutableListOf<Path>()
        var nodeStack=mutableListOf<Node>()
        var root=PathNode()//创建对象
        root.name="root"
        var temp_path=Path()
        temp_path.clazz="root"
        temp_path.id="0"
        currentPath.add(temp_path)//初始的path只有root

        //1.创建一个DocumentBuilderFactory对象
        val builderFactory = DocumentBuilderFactory.newInstance()
        try {
            //2.创建一个DocumentBuilder
            val documentBuilder = builderFactory.newDocumentBuilder()
            //通过DocumentBuilder对象的parse方法加载books.xml到当前项目下
            val parse = documentBuilder.parse(file)
            //获取root节点
            var temp=parse.getElementsByTagName("hierarchy")
            var root1=temp.item(0)
            root1.parentNode
            //获得child node集合
            var childNodes=root1.childNodes//父节点的子节点
            println("childNodes number: "+childNodes.length)
            for(i in 0 until childNodes.length){
                //获得每个child node的属性列表
                nodeStack.add(childNodes.item(i))
                var attrList=childNodes.item(i).attributes
                println("attributes number:  "+attrList.length)
                //将有用的信息存储到path中去
                var tem_path=PathNode()

                println(attrList.getNamedItem("checkable").nodeValue)

                tem_path.clickable=attrList.getNamedItem("clickable").nodeValue
                tem_path.scrollable=attrList.getNamedItem("scrollable").nodeValue
                tem_path.checkable=attrList.getNamedItem("checkable").nodeValue
                var bounds=attrList.getNamedItem("bounds").nodeValue
                tem_path.bounds=Utils.string2int(bounds)
                tem_path.clazz=attrList.getNamedItem("class").nodeValue
                tem_path.index=attrList.getNamedItem("index").nodeValue
                var index=Utils.toInt(tem_path.index)

                //println(tem_path.checkable+"     "+tem_path.clazz)
                root.children.add(index,tem_path)
            }

            while (!nodeStack.isEmpty()){//遍历所有节点，存储每一个node的信息，深度优先
                var lastIndex=nodeStack.size
                var temp_node=nodeStack.get(lastIndex)
                nodeStack.removeAt(lastIndex)
                //开始更新current_path信息


                //开始遍历所有
                var childNodes=temp_node.childNodes//父节点的子节点
                println("childNodes number: "+childNodes.length)
                for(i in 0 until childNodes.length){
                    //获得每个child node的属性列表
                    var attrList=childNodes.item(i).attributes
                    println("attributes number:  "+attrList.length)
                    //将有用的信息存储到path中去
                    var tem_path=PathNode()

                    println(attrList.getNamedItem("checkable").nodeValue)

                    tem_path.clickable=attrList.getNamedItem("clickable").nodeValue
                    tem_path.scrollable=attrList.getNamedItem("scrollable").nodeValue
                    tem_path.checkable=attrList.getNamedItem("checkable").nodeValue
                    var bounds=attrList.getNamedItem("bounds").nodeValue
                    tem_path.bounds=Utils.string2int(bounds)
                    tem_path.clazz=attrList.getNamedItem("class").nodeValue
                    tem_path.index=attrList.getNamedItem("index").nodeValue
                    var index=Utils.toInt(tem_path.index)

                    //println(tem_path.checkable+"     "+tem_path.clazz)
                    root.children.add(index,tem_path)
                    //nodeStack.add(tem_path)
                }


            }




            //val bookNodeList = parse.getElementsByTagName("book")


            //遍历每一个book节点
//            for (i in 0 until bookNodeList.length) {
//                //遍历每个book节点的所有属性的集合
//                val bookNodeAttributes = bookNodeList.item(i).attributes
//                for (j in 0 until bookNodeAttributes.length) {
//                    println("第${i + 1} 个book节点公有${bookNodeList.item(i).attributes.length}个属性")
//                    println("属性：" + bookNodeAttributes.item(j))
//                    println(bookNodeAttributes.item(j).nodeName)
//                    println(bookNodeAttributes.item(j).nodeValue)
//
//                    //获取当前book节点的子节点集合
//                    val bookNodeChildNodes = bookNodeList.item(i).childNodes
//                    //会把空格和换行符也当成节点
//                    println("第${i + 1} 本书共有${(bookNodeChildNodes.length - 1) / 2}个子节点")
////                    for (k in 0 until (bookNodeChildNodes.length - 1) / 2) {
//                    (0 until bookNodeChildNodes.length - 1)
//                        .filter { bookNodeChildNodes.item(it).nodeType == Node.ELEMENT_NODE }
//                        .forEach {
//                            print("子节点" + bookNodeChildNodes.item(it).nodeName)
//                            //null
////                                println(bookNodeChildNodes.item(it).nodeValue)
//                            println("   对应的值为   " + bookNodeChildNodes.item(it).firstChild.nodeValue)
////                                println("   对应的值为   " + bookNodeChildNodes.item(it).textContent)
//                        }
//                }
//            }
        } catch (e: ParserConfigurationException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: SAXException) {
            e.printStackTrace()
        }
    return nodeList

    }

    fun main() {
        val inputFile="$directoryPath/xml/layout33.xml"//33
        var nodeList:List<Node>?=null
        //nodeList=DOM(inputFile)
    }