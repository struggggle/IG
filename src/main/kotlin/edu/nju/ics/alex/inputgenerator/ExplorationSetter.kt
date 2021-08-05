package edu.nju.ics.alex.inputgenerator

import edu.nju.ics.alex.inputgenerator.layout.LayoutTree
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths

/**
 * implement app-exploration related methods
 * */

//pre-process before start searching
fun preSearch() {
    cmd.cleanUIProcess()//cmd是一个全局变量吗？
    println("[Prepare the exploration]")

    //setUpClientComplete()

    println("\tInstall the app and start it")
    reinstallApp()
    //grant permission for coverage, which also starts the app
    setUpCoverageCollection()
    waitForStable()//这里是等待界面稳定？   包含getCurrentLayout()
}

//uninstall and install the app
fun reinstallApp() {
    cmd.uninstallApp(appPack)

    if (!cmd.installApp(apkFolder + apkName)) {
        System.err.println("Error: cannot install the app!")
        System.exit(-1)
    }
}

//restart the app
fun restartApp() {
    if (!cmd.stopApp(appPack)) {
        System.err.println("Failed to kill the app!")
        System.exit(-1)
    }
    //waitForStable()
    if (!cmd.startApp(appPack)) {
        System.err.println("Failed to start the app!")
        System.exit(-1)
    }
    waitForStable()
}

//wait a certain period of time for stable
fun waitForStable() {
    try {
        Thread.sleep(1000)
    } catch (e: InterruptedException) {
        e.printStackTrace()
    }
    getCurrentLayout()//获得GUI视图并保存到文件夹
    //路径信息：workingDir = System.getProperty("user.dir")
    var lastGUI = parse("$workingDir/$xmlFolder/layout$layoutCount.xml")[0]//对gui视图进行解析
    try {
        Thread.sleep(1000)
    } catch (e: InterruptedException) {
        e.printStackTrace()
    }
    getCurrentLayout()
    var currentGUI = parse("$workingDir/$xmlFolder/layout$layoutCount.xml")[0]
    var count = 5
    while (!lastGUI.isSame(currentGUI, compareMode) && count > 0){
        lastGUI = currentGUI
        count--
        try {
            Thread.sleep(1000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        getCurrentLayout()
        currentGUI = parse("$workingDir/$xmlFolder/layout$layoutCount.xml")[0]
    }
}

//仅仅保存最后稳定的界面
fun waitForStableMT(EN:Int,packageName: String) {
    try {
        Thread.sleep(1000)
    } catch (e: InterruptedException) {
        e.printStackTrace()
    }
    //获得GUI视图并保存到文件
    getCurrentLayoutMT()
    //路径信息：workingDir = System.getProperty("user.dir")
    var lastGUI = parse("$workingDir/$xmlFolderMT/layout$layoutCount.xml")[0]//对gui视图进行解析

    //TODO 额外添加的，用来处理登陆的情况（主要针对wordpress这个app），判断layout文件中是否包含"Logging"这个关键词
    var file = File("$workingDir/$xmlFolderMT/layout$layoutCount.xml")
    var contents= file.readText()
    println("contents:"+contents.toString())
    println("checking logging or updating or caching: ")
    println(contents.contains("Updating")||contents.contains("Caching"))
    if(contents.contains("CONNECTING")||contents.contains("Logging in")||contents.contains("Updating")||contents.contains("Caching")){
        println("begin to logging or updating or caching (delay 30s)")
        Thread.sleep(30000)
    }
    println("EN:"+EN)//begin from 1
    if(EN==1&&packageName.equals("de.tap.easy_xkcd")){
        println("de.tap.easy_xkcd: can not obtain layout file (delay 100s)")
        Thread.sleep(100000)
    }

    try {
        Thread.sleep(1000)
    } catch (e: InterruptedException) {
        e.printStackTrace()
    }
    getCurrentLayoutMT()
    var currentGUI = parse("$workingDir/$xmlFolderMT/layout$layoutCount.xml")[0]

    var count = 5
    while (!lastGUI.isSame(currentGUI, compareMode) && count > 0){//比较前后两个gui是否相同
        lastGUI = currentGUI
        count--
        try {
            Thread.sleep(1000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        getCurrentLayoutMT()

        currentGUI = parse("$workingDir/$xmlFolderMT/layout$layoutCount.xml")[0]
    }
}

fun waitForStableExplore(EN:Int) {
    try {
        Thread.sleep(1000)
    } catch (e: InterruptedException) {
        e.printStackTrace()
    }
    //获得GUI视图并保存到文件夹
    getCurrentLayoutMT()
    //路径信息：workingDir = System.getProperty("user.dir")
    var lastGUI = parse("$workingDir/$xmlFolderMT/layout$layoutCount.xml")[0]//对gui视图进行解析

    //TODO 额外添加的，用来处理登陆的情况（主要针对wordpress这个app），判断layout文件中是否包含"Logging"这个关键词
    var file = File("$workingDir/$xmlFolderMT/layout$layoutCount.xml")
    var contents= file.readText()
    if(contents.contains("org.wordpress.android")&& contents.contains("Logging in")){
        //15s登陆时间
        println("begin to logging in (delay 30s)")
        Thread.sleep(30000)
    }
    //----------
    
    try {
        Thread.sleep(1000)
    } catch (e: InterruptedException) {
        e.printStackTrace()
    }
    getCurrentLayoutMT()
    var currentGUI = parse("$workingDir/$xmlFolderMT/layout$layoutCount.xml")[0]
    var count = 5
    while (!lastGUI.isSame(currentGUI, compareMode) && count > 0){//比较前后两个gui是否相同
        lastGUI = currentGUI
        count--
        try {
            Thread.sleep(1000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        getCurrentLayoutMT()

        //这里获得了，但是没有保存
        currentGUI = parse("$workingDir/$xmlFolderMT/layout$layoutCount.xml")[0]
    }
    //getCurrentLayoutStableMT(EN)//获得GUI视图并保存到文件夹
}

//count the overall layout
var layoutCount = 0
var layoutCountStable = 0
//dump the current layout
fun getCurrentLayout() {
    layoutCount++
    //cmd.dumpByClient()
    //cmd.pull("$parseDir$parseFile", "$workingDir/$xmlFolder/layout$layoutCount.xml")
    cmd.dump()
    cmd.pull("$parseDir/$parseFile", "$workingDir/$xmlFolder/layout$layoutCount.xml")//将内容重命名并转移到pc

}

fun getCurrentLayoutMT() {
    layoutCount++
    //cmd.dumpByClient()
    //cmd.pull("$parseDir$parseFile", "$workingDir/$xmlFolder/layout$layoutCount.xml")
    var condition=true
    while (condition){
        var result=cmd.dumpMT()
        //判断是否获得dump失败
        if (!(result.contains("null root node returned by UiTestAutomationBridge"))){
            println("获得dump成功！"+condition+"   "+result)
            condition=false
        }
        println("获得dump失败！"+condition+"   "+result)
    }
    println("lwjlayout"+"$parseDir/$parseFile")
    cmd.pull("$parseDir/$parseFile", "$workingDir/$xmlFolderMT/layout$layoutCount.xml")//将内容重命名并转移到pc
    println("dump layout.xml to: $workingDir/$xmlFolderMT/layout$layoutCount.xml")
    //"$workingDir/$xmlFolderMT/layout$layoutCount.xml"
}


fun getCurrentLayoutExplore(index:Int) {
    //cmd.dumpByClient()
    //cmd.pull("$parseDir$parseFile", "$workingDir/$xmlFolder/layout$layoutCount.xml")
    cmd.dump()

    //$directoryPath/testOutput/wordPress/layoutsExplore/layout0.xml
    cmd.pull("$parseDir/$parseFile", "$directoryPath/testOutput/wordPress/layoutsExplore/layout$index.xml")//将内容重命名并转移到pc
    println("dump layout.xml to: $directoryPath/testOutput/wordPress/layoutsExplore/layout$index.xml")
    //"$workingDir/$xmlFolderMT/layout$layoutCount.xml"
}

fun getCurrentLayoutExplore(index:Int, appName: String) {
    cmd.dump()
    cmd.pull("$parseDir/$parseFile", "$directoryPath/testOutput/$appName/layoutsExplore/layout$index.xml")//将内容重命名并转移到pc
    println("dump layout.xml to: $directoryPath/testOutput/$appName/layoutsExplore/layout$index.xml")
}

fun getCurrentLayoutStableMT(appName:String, event: String,EN:Int,fileName:String):String {

    var condition=true
    while (condition){
        var result=cmd.dumpMT()
        if (!(result.contains("null root node returned by UiTestAutomationBridge"))){
            println("获得dump成功！"+condition+"   "+result)
            condition=false;
        }
        println("获得dump失败！"+condition+"   "+result)
    }
    //cmd.pull("$parseDir/$parseFile", "$workingDir/$xmlFolderStable/eventLayout$EN.xml")//将内容重命名并转移到pc
    cmd.pull("$parseDir/$parseFile", "$directoryPath/testOutput/$appName/layout/"+fileName+"Layout$EN.xml")
    println("dump layout.xml: $$directoryPath/testOutput/$appName/layout/"+fileName+"Layout$EN.xml")
    //var outFile="$directoryPath/testOutput/wordPress/activity/browseBlogAC.txt"/
    //begin to get widget    在parseLayoutEvent()中再将第n个event与第n-1个layout进行比较分析
    var layoutPath="$directoryPath/testOutput/$appName/layout/"+fileName+"Layout"+EN+".xml"
    var widget=InputMutation.parseLayoutEvent(appName,EN,event,layoutPath,fileName)
    return widget
}

fun getCurrentLayoutStableNoWidget(appName:String, event: String,EN:Int,fileName:String) {
    //layoutCountStable++
    //cmd.dumpByClient()
    //cmd.pull("$parseDir$parseFile", "$workingDir/$xmlFolder/layout$layoutCount.xml")

    //cmd.dump()  替换为下面的
    var condition=true
    while (condition){
        var result=cmd.dumpMT()
        //判断是否获得dump失败
        if (!(result.contains("null root node returned by UiTestAutomationBridge"))){
            println("获得dump成功！"+condition+"   "+result)
            condition=false;
        }
        println("获得dump失败！"+condition+"   "+result)
    }
    //cmd.pull("$parseDir/$parseFile", "$workingDir/$xmlFolderStable/eventLayout$EN.xml")//将内容重命名并转移到pc
    cmd.pull("$parseDir/$parseFile", "$directoryPath/testOutput/$appName/layout/"+fileName+"Layout$EN.xml")
    println("dump layout.xml: $$directoryPath/testOutput/$appName/layout/"+fileName+"Layout$EN.xml")
    //var outFile="$directoryPath/testOutput/wordPress/activity/browseBlogAC.txt"/
    //begin to get widget    在parseLayoutEvent()中再将第n个event与第n-1个layout进行比较分析
    //这里只解析，不返回具体的widget坐标
    var layoutPath="$directoryPath/testOutput/$appName/layout/"+fileName+"Layout"+EN+".xml"
    InputMutation.parseLayoutEventNoWidget(appName,EN,event,layoutPath,fileName)
    //return widget
}

fun justGetCurrentLayoutStableMT(appName:String, event: String,EN:Int,fileName:String) {
    cmd.dump()
    cmd.pull("$parseDir/$parseFile", "$directoryPath/testOutput/$appName/layout/"+fileName+"Layout$EN.xml")
    println("dump layout.xml: $$directoryPath/testOutput/$appName/layout/"+fileName+"Layout$EN.xml")

    //write the layout's widgets to a file
    var layoutPath="$directoryPath/testOutput/$appName/layout/"+fileName+"Layout$EN.xml"
    val layoutTree = LayoutTree(layoutPath)
    val path_allXpath = "$directoryPath/testOutput/" + appName + "/Xpaths/" + fileName + "Xpath" + EN + ".txt"
    try {
        InputMutation.writeArrayList2File(layoutTree.eventPosition, path_allXpath)
    } catch (ex: IOException) {
        ex.printStackTrace()
    }
}

fun compareLayout(appName: String,EN:Int,target_fileName: String):String {
    //layoutCountStable++
    //cmd.dumpByClient()
    //cmd.pull("$parseDir$parseFile", "$workingDir/$xmlFolder/layout$layoutCount.xml")
    cmd.dump()
    //cmd.pull("$parseDir/$parseFile", "$workingDir/$xmlFolderStable/eventLayout$EN.xml")//将内容重命名并转移到pc
    cmd.pull("$parseDir/$parseFile", "$directoryPath/testOutput/$appName/replayLayout/eventLayout$EN.xml")
    println("dump layout.xml: $$directoryPath/testOutput/$appName/replayLayout/eventLayout$EN.xml")
    //var outFile="$directoryPath/testOutput/wordPress/activity/browseBlogAC.txt"/
    //比较是否属于layout范围
    val layout="$directoryPath/testOutput/$appName/replayLayout/eventLayout$EN.xml"
    val path="$directoryPath/testOutput/$appName/layout"
    //下面的函数调用返回值没有问题
    var fileName="$target_fileName"+"Layout$EN.xml"
    return CallPython.compare(path,layout,fileName)
}

//get input command via adb
fun toSendEventCommand(action: Actions, node: LayoutNode) : String
        = when {
    action == Actions.LONGCLICK -> {

        //这里对/2是为了得到点击的控件的中间点
        val x = (node.coords[2] + node.coords[0])/2
        val y = (node.coords[1] + node.coords[3])/2
        "swipe $x $y $x $y 1000"
    }
    action == Actions.CLICK -> {
        val x = (node.coords[2] + node.coords[0])/2
        val y = (node.coords[1] + node.coords[3])/2
        "tap $x $y"
    }
    action == Actions.SWIPE_LEFT -> {
        val y = (node.coords[1] + node.coords[3])/2
        "swipe ${node.coords[0]} $y ${node.coords[2]} $y 500"
    }
    action == Actions.SWIPE_RIGHT -> {
        val y = (node.coords[1] + node.coords[3])/2
        "swipe ${node.coords[2]} $y ${node.coords[0]} $y 500"
    }
    action == Actions.SWIPE_DOWN -> {
        val x = (node.coords[2] + node.coords[0])/2
        "swipe $x ${node.coords[1]} $x ${node.coords[3]}"
    }
    action == Actions.SWIPE_UP -> {
        val x = (node.coords[2] + node.coords[0])/2
        "swipe $x ${node.coords[3]} $x ${node.coords[1]}"
    }
    action == Actions.SET_TEXT -> {
        "text \"test\""
    }
    action == Actions.CLEAR_TEXTFIELD -> {
        "text \"\""
    }
    else -> "null"
}

//for testing propose only
fun main() {
    //preSearch()
    //getCurrentLayout()
    getCurrentLayout()
}