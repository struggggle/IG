package edu.nju.ics.alex.inputgenerator

import edu.nju.ics.alex.inputgenerator.InputMutation.getLayoutEvents
import java.util.*

/**
 * 这里的执行分为简单执行和替换执行。
 * 简单执行：直接从历史的layout中生成坐标
 * 替换执行：查找相似的widget
 * 存储已经执行过的path
 *
 * -----这里还需要设计如何对已经执行过的path进行记录，能够顺利实现点击的是不同的widget
 */
class KUtils{

    fun getCurrentLayoutStable(packagename:String){
        waitForStableMT(0,packagename)//这里的参数没有任何作用
        getCurrentLayoutExplore(999)//这里999是给的一个编号，没有实际作用
    }

    fun doPrint(){
        println("kotlin中的函数方法doPrint()")
    }

    // 带参数fun方法
    fun setPhone(phone: String) {
        println("$phone")
    }

    fun test() {
        println("hahahaha")
    }


    fun executeEvent(event: String, index:Int,target_fileName:String, layoutsPath:String,savePath:String):Array<String> {
        println("Execute widget:   "+event)
        val result: Array<String> = arrayOf("False","False")
        //进行比较的layout  browseBlog.shLayout1

        var layoutsPathToBeCompare = layoutsPath+target_fileName+"Layout"+(index+1)+".xml"
        var layoutAfterEvent=savePath+target_fileName+"Layout"+(index+1)+".xml"
        var layoutBeforeEvent=savePath+target_fileName+"Layout"+index+".xml"
        println("layoutsPathToBeCompare:  "+layoutsPathToBeCompare)
        println("layoutAfterEvent:  "+layoutAfterEvent)
        println("layoutBeforeEvent:  "+layoutBeforeEvent)

        getCurrentLayoutExplore(0)//这里0是给的一个编号
        var currentLayout ="$directoryPath/testOutput/wordPress/layoutsExplore/layout0.xml"

        //下面根据widget生成event
        var widgetList=ArrayList<String>()
        var eventGen: String
        if(event.contains("adb shell")){ //这些是绝对坐标的
            eventGen=event
        }else{
            //第2个event在第一个layout中找到坐标
            //这里首先获得执行当前事件之前的layout，获得event list，然后获得坐标信息
            widgetList=getLayoutEvents(currentLayout)
            //var widgetListFile="$directoryPath/testOutput/wordPress/Xpaths/"+target_fileName+"Xpath"+(index-1)+".txt";
            //根据相对path，读取绝对坐标
            var intsArrayList= InputMutation.getTargetPositionFromWidgetList(event,widgetList)
            println("insArrayList number:  "+intsArrayList.size)
            //TODO 这里的意思是根据widget在界面上找不到所需要的控件？
            if(intsArrayList.size==0){
                return result
            }

            var positions=intsArrayList.get(0)
            var x=positions[0]
            var y=positions[1]
            eventGen="adb shell input tap "+x+"  "+y
        }

        println("Execute event:   "+eventGen)
        //save the event
        val time=System.currentTimeMillis()
        var eventSaveFile="$directoryPath/testOutput/"+ InputMutation09.appNameJava+"eventLogs.txt"
        saveEvent(eventSaveFile,time.toString()+"-"+eventGen.toString())

        cmd.sendEventMT2(index,eventGen)//执行event并记录时间戳
        waitForStableExplore(index)//这里仅仅是等待界面稳定而已
        var compareResult=compareLayoutEvent(index,layoutsPathToBeCompare,layoutAfterEvent)
        return compareResult
    }

    fun executeEventPrefix(event: String, index:Int,target_fileName:String, layoutsPath:String,savePath:String):Array<String> {
        println("Execute widget:   "+event)
        val result: Array<String> = arrayOf("False","False")
        //进行比较的layout  browseBlog.shLayout1

        var layoutsPathToBeCompare = layoutsPath+target_fileName+"Layout"+(index+1)+".xml"
        var layoutAfterEvent=savePath+target_fileName+"Layout"+(index+1)+".xml"
        var layoutBeforeEvent=savePath+target_fileName+"Layout"+index+".xml"
        println("layoutsPathToBeCompare:  "+layoutsPathToBeCompare)
        println("layoutAfterEvent:  "+layoutAfterEvent)
        println("layoutBeforeEvent:  "+layoutBeforeEvent)

        getCurrentLayoutExplore(0)//这里0是给的一个编号
        var currentLayout ="$directoryPath/testOutput/wordPress/layoutsExplore/layout0.xml"

        //下面根据widget生成event
        var widgetList=ArrayList<String>()
        var eventGen: String
        if(event.contains("adb shell")){ //这些是绝对坐标的
            eventGen=event
        }else{
            //第2个event在第一个layout中找到坐标
            //这里首先获得执行当前事件之前的layout，获得event list，然后获得坐标信息
            widgetList=getLayoutEvents(currentLayout)
            //var widgetListFile="$directoryPath/testOutput/wordPress/Xpaths/"+target_fileName+"Xpath"+(index-1)+".txt";
            //根据相对path，读取绝对坐标
            var intsArrayList= InputMutation.getTargetPositionFromWidgetList(event,widgetList)
            println("insArrayList number:  "+intsArrayList.size)
            //TODO 这里的意思是根据widget在界面上找不到所需要的控件？
            if(intsArrayList.size==0){
                return result
            }

            var positions=intsArrayList.get(0)
            var x=positions[0]
            var y=positions[1]
            eventGen="adb shell input tap "+x+"  "+y
        }

        println("Execute event:   "+eventGen)
        cmd.sendEventMT2(index,eventGen)//执行event并记录时间戳
        waitForStableExplore(index)//这里仅仅是等待界面稳定而已
        var compareResult=compareLayoutEvent(index,layoutsPathToBeCompare,layoutAfterEvent)
        return compareResult
    }

    fun executeEventLoop(prefixSize:Int, event: String, index:Int,target_fileName:String, layoutsPath:String,savePath:String):Array<String> {
        println("Execute widget:   "+index+"  "+event)
        val result: Array<String> = arrayOf("False","False")

        //这里的index就是测试用例中第几个event，从1开始计数
        var layoutsPathToBeCompare = layoutsPath+target_fileName+"Layout"+(index+1)+".xml"
        var layoutAfterEvent=savePath+target_fileName+"Layout"+(index+1)+".xml"
        println("layoutsPathToBeCompare:  "+layoutsPathToBeCompare)
        println("layoutAfterEvent:  "+layoutAfterEvent)

        getCurrentLayoutExplore(0)//这里0是给的一个编号
        var currentLayout ="$directoryPath/testOutput/wordPress/layoutsExplore/layout0.xml"

        //下面根据widget生成event
        var widgetList=ArrayList<String>()
        var eventGen: String
        if(event.contains("adb shell")){ //这些是绝对坐标的
            eventGen=event
        }else{
            //这里首先获得执行当前事件之前的layout，然后获得坐标信息
            widgetList=getLayoutEvents(currentLayout)
            //根据相对path，读取绝对坐标
            var intsArrayList= InputMutation.getTargetPositionFromWidgetList(event,widgetList)
            println("insArrayList number:  "+intsArrayList.size)
            //TODO 这里的意思是根据widget在界面上找不到所需要的控件？
            if(intsArrayList.size==0){
                return result
            }

            var positions=intsArrayList.get(0)
            var x=positions[0]
            var y=positions[1]
            eventGen="adb shell input tap "+x+"  "+y
        }

        println("Execute event:   "+eventGen)
        cmd.sendEventMT2(index,eventGen)//执行event并记录时间戳
        waitForStableExplore(index)//这里仅仅是等待界面稳定而已
        var compareResult=compareLayoutEvent(index,layoutsPathToBeCompare,layoutAfterEvent)
        return compareResult
    }

    fun executeEventSuffix(event: String, index:Int,target_fileName:String, layoutsPath:String,savePath:String):Array<String> {
        println("Execute widget:   "+event)
        val result: Array<String> = arrayOf("False","False")
        //进行比较的layout  browseBlog.shLayout1

        var layoutsPathToBeCompare = layoutsPath+target_fileName+"Layout"+(index+1)+".xml"
        var layoutAfterEvent=savePath+target_fileName+"Layout"+(index+1)+".xml"
        var layoutBeforeEvent=savePath+target_fileName+"Layout"+index+".xml"
        println("layoutsPathToBeCompare:  "+layoutsPathToBeCompare)
        println("layoutAfterEvent:  "+layoutAfterEvent)
        println("layoutBeforeEvent:  "+layoutBeforeEvent)

        getCurrentLayoutExplore(0)//这里0是给的一个编号
        var currentLayout ="$directoryPath/testOutput/wordPress/layoutsExplore/layout0.xml"

        //下面根据widget生成event
        var widgetList=ArrayList<String>()
        var eventGen: String
        if(event.contains("adb shell")){ //这些是绝对坐标的
            eventGen=event
        }else{
            //第2个event在第一个layout中找到坐标
            //这里首先获得执行当前事件之前的layout，获得event list，然后获得坐标信息
            widgetList=getLayoutEvents(currentLayout)
            //var widgetListFile="$directoryPath/testOutput/wordPress/Xpaths/"+target_fileName+"Xpath"+(index-1)+".txt";
            //根据相对path，读取绝对坐标
            var intsArrayList= InputMutation.getTargetPositionFromWidgetList(event,widgetList)
            println("insArrayList number:  "+intsArrayList.size)
            //TODO 这里的意思是根据widget在界面上找不到所需要的控件？
            if(intsArrayList.size==0){
                return result
            }

            var positions=intsArrayList.get(0)
            var x=positions[0]
            var y=positions[1]
            eventGen="adb shell input tap "+x+"  "+y
        }

        println("Execute event:   "+eventGen)
        cmd.sendEventMT2(index,eventGen)//执行event并记录时间戳
        waitForStableExplore(index)//这里仅仅是等待界面稳定而已
        var compareResult=compareLayoutEvent(index,layoutsPathToBeCompare,layoutAfterEvent)
        return compareResult
    }

    /**这里比较swipe event执行前后的layout是否发生来变化*/
    fun executeSwipeEvent(event: String, index:Int,target_fileName:String, layoutsPath:String,savePath:String):Array<String> {
        val result: Array<String> = arrayOf("False","False")
        //进行比较的layout  browseBlog.shLayout1

        waitForStableExplore(index)//这里仅仅是等待界面稳定而已
        getCurrentLayoutExplore(1001)//这里0是给的一个编号
        var currentLayout1 ="$directoryPath/testOutput/wordPress/layoutsExplore/layout1001.xml"

        println("Execute swipe event:   "+event)
        cmd.sendEventMT2(index,event)//执行event并记录时间戳
        waitForStableExplore(index)//这里仅仅是等待界面稳定而已

        getCurrentLayoutExplore(1002)//这里1是给的一个编号
        var currentLayout2 ="$directoryPath/testOutput/wordPress/layoutsExplore/layout1002.xml"
        var compareResult=compareLayoutSwipeEvent(index,currentLayout1,currentLayout2)
        return compareResult
    }


    fun executeSwipeEvent(event: String) {
        var index=-1;
        cmd.sendEventMT2(index,event)//执行event并记录时间戳
        waitForStableExplore(index)//这里仅仅是等待界面稳定而已
    }

    fun executeLogEvent(event: String) {
        var index=-1;
        cmd.sendEventMT2(index,event)//执行event并记录时间戳
    }

    fun executeDumpEvent(event: String) {
        var index=-1;
        cmd.sendEventDump(index,event)//执行event并记录时间戳
    }

    //这里将layout文件的位置进行单独的存储，需要修改存储的路径，因为需要一个path信息
    fun compareLayoutEvent(index:Int,layoutsPathToBeCompare: String,savePath: String):Array<String> {
        //layoutCountStable++
        //cmd.dumpByClient()
        //cmd.pull("$parseDir$parseFile", "$workingDir/$xmlFolder/layout$layoutCount.xml")
        var result = arrayOf("null","null")
        cmd.dump()
        //cmd.pull("$parseDir/$parseFile", "$workingDir/$xmlFolderStable/eventLayout$EN.xml")//将内容重命名并转移到pc
        cmd.pull("$parseDir/$parseFile", savePath)
        println("dump layout.xml: "+savePath)
        var compareResult=CallPython.callPython(layoutsPathToBeCompare,savePath)//比较两个path的layout是否相似
        result[0]=savePath
        result[1]=compareResult
        return result
    }

    fun compareLayoutSwipeEvent(index:Int,layoutsPathToBeCompare: String,savePath: String):Array<String> {
        var result = arrayOf("null","null")
        var compareResult=CallPython.callPython(layoutsPathToBeCompare,savePath)//比较两个path的layout是否相似
        result[0]=savePath
        result[1]=compareResult
        return result
    }

    fun getScreenShot(count:Int){
        println("开始截图： $count")
        var event="adb shell /system/bin/screencap -p /sdcard/screenshot.png"
        cmd.sendEventMT2(count,event)
        //var savePath="$directoryPath/testOutput/wordPress/screenShot/$count.png"
        cmd.sendEventMT2(count,"adb pull /sdcard/screenshot.png $directoryPath/testOutput/wordPress/screenShot/$count.png")
    }
}