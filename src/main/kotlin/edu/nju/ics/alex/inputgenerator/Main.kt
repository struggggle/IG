package edu.nju.ics.alex.inputgenerator

import com.sun.xml.internal.fastinfoset.util.StringArray
import edu.nju.ics.alex.inputgenerator.CheckCycle.getLoops
import edu.nju.ics.alex.inputgenerator.InputMutation09.goToMutate
import edu.nju.ics.alex.inputgenerator.UZipFile.createfolder
import edu.nju.ics.alex.inputgenerator.Utils.getDecodingEvents
import java.io.*
import java.util.*
import kotlin.random.Random

//TODO: make it in the configuration
val numberOfInputs = -1

var eventTimeStampRecorder = ArrayList<EventTimeStamp>()
var eventTimeStampReplay = ArrayList<EventTimeStamp>()

val random = Random(System.currentTimeMillis())

private fun getActivityName(packageName: String,EN:Int):String {
    var ActivityName=""
    var temp_ActivityName="null"
    val cmd="adb shell dumpsys activity | grep -i run"
    var inReader: BufferedReader? = null
    var errReader: BufferedReader? = null
    try {
        println("Time and event:  "+System.currentTimeMillis()+"---"+cmd)
        val dump = Runtime.getRuntime().exec(cmd)
        inReader = BufferedReader(InputStreamReader(dump.inputStream))
        var tem = inReader.readLines()
        var teml=tem.get(2)//locate to the first activity
        println("teml: "+teml)
            if(teml!=null && teml.contains(packageName)){
                var indexStrat=teml.indexOf(packageName)
                var indexEnd=teml.length
                ActivityName=teml.subSequence(indexStrat,indexEnd).toString()
            }

        var outFile="$directoryPath/ACStack/ACStack"+EN+".txt"
        val writeFile=File(outFile)
        writeFile.delete()//clear the contents in the file
        println("start to read activity info:")

        for(line in tem){
            if(line!=null && line.contains(packageName)){
                println("checking line: "+line)
                var indexStrat=line.indexOf(packageName)
                var indexEnd=line.length
                temp_ActivityName=line.subSequence(indexStrat,indexEnd).toString()
                println("name:   "+temp_ActivityName)
                writeFile.appendText(temp_ActivityName+"\n")
            }
        }
        println("write to file: "+outFile)
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return temp_ActivityName
}


private fun executeMT(appName:String,event: String,packageName: String, EN:Int, fileName: String):Array<String> {
    //record time stamp and execute the event
    cmd.sendEventMT(EN,event)
    waitForStableMT(EN,packageName)
    var widget=getCurrentLayoutStableMT(appName,event,EN,fileName)

    var activityName=getActivityName(packageName,EN)
    var strArray: Array<String> = arrayOf(activityName,widget)
    return strArray
}


fun compare(appName: String,event: String,packageName: String, EN:Int,target_fileName:String):String {
    var eventGen: String
    if(event.contains("adb shell")){
        eventGen=event
    }else{
        var widgetListFile="$directoryPath/testOutput/$appName/Xpaths/"+target_fileName+"Xpath"+(EN-1)+".txt"
        var intsArrayList= InputMutation.getTargetPosition(event,widgetListFile)
        println("insArrayList number:  "+intsArrayList.size)
        if(intsArrayList.size==0){
            widgetListFile="$directoryPath/testOutput/$appName/XpathsClick/"+target_fileName+"Xpath"+(EN-1)+".txt"
            println("Begin to find the event's target:  $event")
            intsArrayList= InputMutation.getTargetPosition(event,widgetListFile)
            println("insArrayList number（in caseWidgetClick）:  "+intsArrayList.size)
        }
        var positions=intsArrayList.get(0)
        var x=positions[0]
        var y=positions[1]
        eventGen="adb shell input tap "+x+"  "+y
    }

    println("replay execute:   "+eventGen)
    cmd.sendEventMT2(EN,eventGen)
    waitForStableMT(EN,packageName)

    val compareResult=compareLayout(appName,EN,target_fileName)
    var activityName=getActivityName(packageName,EN)
    println("CompareResult:   "+compareResult)
    return compareResult
}

fun record(appName:String,packageName: String,casePath:String,fileName:String) {//recorder: BufferedWriter,
    var EN=0//the event index in the test case
    var numberOfGenerated = 0//writing ac to a file
    var activityFile="$directoryPath/testOutput/$appName/activity/"+fileName+".txt"//这里存储每个event所位于的activity
    var widgetFile="$directoryPath/testOutput/$appName/caseWidget/"+fileName+".txt"
    val file=File(casePath)
    if(!file.exists()){
        println("Error:   "+"The file $casePath is not exist!")
    }
    var writeFileActivity=File(activityFile)
    var writeFileWidget=File(widgetFile)

    if(!writeFileActivity.exists()){
        writeFileActivity.createNewFile()
    }
    if(!writeFileWidget.exists()){
        println("Create widgetFile:   "+widgetFile)
        writeFileWidget.createNewFile()
    }

    //clear the contents in the file
    writeFileActivity.delete()
    writeFileWidget.delete()

    //read and execute each event in the test case file
    val inputStream: InputStream=file.inputStream()
    inputStream.bufferedReader().useLines { lines ->
        lines.forEach {
            if(it.startsWith("adb shell")){
                EN++//begin from 1
                println("Execute the event: $it")
                var strArray=executeMT(appName,it,packageName,EN,fileName)
                var ac=strArray.get(0)
                var widget=strArray.get(1)
                writeFileActivity.appendText(EN.toString()+":"+ac+"\n")
                writeFileWidget.appendText(EN.toString()+":"+widget+"\n")
            }
        }
    }
}

fun replay(appName: String,packageName: String,casePath:String,target_fileName:String):String {
    println("Begin to replay the file:    "+casePath)
    var result="Ture"
    var EN=0
    val file=File(casePath)
    if(!file.exists()){
        println("Error:   "+"The file $casePath is not exist!")
    }

    val inputStream: InputStream=file.inputStream()
    inputStream.bufferedReader().useLines { lines ->
        lines.forEach {
            EN++
            println("Log--Execute the event: $it")

            var index=it.indexOf(":")
            var ac=compare(appName,it.drop(index+1),packageName,EN,target_fileName)
            if(ac=="False"){
                println("The event is not suitable!")
                result="False"
            }else{
                println("The event is suitable!")
            }
        }
    }
    return result
}

/**Path Setting**/
var apkPath="/Users/wenjieli/Desktop/testInput/apks/"
var directoryPath="/Users/wenjieli/Desktop/testInput"

fun main(args: Array<String>) {
    File("$workingDir/$inputSequenceFile").bufferedWriter().use {
//        var target_fileName="addTwoPasses.sh"
//        var apkName="org.ligi.passandroid_356_apps.evozi.com"
//        var packageName="org.ligi.passandroid"
        apkPath=args[0]
        directoryPath=args[1]
        var target_fileName=args[2]
        var apkName=args[3]
        var packageName=args[4]


        val appName=apkName+target_fileName
        var casePath="$directoryPath/testInput/$apkName/"
        var target="$directoryPath/testInput/$apkName/$target_fileName"


        var zipPath="$directoryPath/testOutput/temp.zip"
        var parentPath="$directoryPath/testOutput/"
        var zipFolderName="$directoryPath/testOutput/temp"
        var newName="$directoryPath/testOutput/$appName"
        createfolder(zipPath, zipFolderName,parentPath,  newName)

        var savedImagePath="/data/user/0/$packageName/images/"  ///data/user/0/org.wordpress.android/images/
        var savedDisplayedImagePath="/data/user/0/$packageName/displayedImages/"
        var imageFloderRecorder="$directoryPath/testOutput/$appName/images/recordImages/"
        var displayedImageFloderRecorder="$directoryPath/testOutput/$appName/images/recordDisplayedImages/"
        var imageFloderReplay="$directoryPath/testOutput/$appName/images/replayImages/"
        var displayedImageFloderReplay="$directoryPath/testOutput/$appName/images/replayDisplayedImages/"

        clearImages(savedImagePath, savedDisplayedImagePath)

        PrefixAndSuffix.unstallAPK(packageName)
        PrefixAndSuffix.obtainRoot()
        PrefixAndSuffix.installAPK(apkName,apkPath)

        val fileTree: FileTreeWalk = File(casePath).walk()
        fileTree.maxDepth(1) //The traversal directory level is 1, i.e. no subdirectories need to be checked
            .filter { it.isFile } //Select documents only
            .filter { it.extension in listOf("sh") }//select .sh files
            .forEach {
                if(it.absolutePath.contentEquals(target)){
                    println("Begin to record!")
                    val func_start_time=System.currentTimeMillis()
                    record(appName,packageName,it.absolutePath,it.name)
                    val func_end_time=System.currentTimeMillis()

                    /**save the time consumption of functional test case*/
                    var func_time_consumption=func_end_time-func_start_time
                    var timeStampFile="$directoryPath/testOutput/$appName/timeStamp/$target_fileName"+"_timeConsumption.txt"
                    saveTimeStamp(timeStampFile,func_time_consumption.toString())
                }
            }
        Thread.sleep(4000);    //waiting for all image decoding finished
        pullImages(savedImagePath, savedDisplayedImagePath,imageFloderRecorder,displayedImageFloderRecorder)
        clearImages(savedImagePath, savedDisplayedImagePath)

        var timeStampFile="$directoryPath/testOutput/$appName/timeStamp/$target_fileName"+"_eventRecord.txt"
        var file=File(timeStampFile)
        if(!file.exists()){
            println("Error:   "+"The file $timeStampFile is not exist!")
        }
        var writeFileStamp=File(timeStampFile)

        if(!writeFileStamp.exists()) {
            writeFileStamp.createNewFile()
        }
        writeFileStamp.delete();
        for (item in eventTimeStampRecorder){
            writeFileStamp.appendText(item.index.toString()+":"+item.time.toString()+"\n")
        }


        //var mutationCasePath="$directoryPath/testInput/wordPressMutation/"
        var mutationCasePath="$directoryPath/testOutput/$appName/caseWidget/"
        var mutation_fileName=target_fileName+".txt"
        var mutation_target=mutationCasePath+mutation_fileName

        PrefixAndSuffix.unstallAPK(packageName)
        PrefixAndSuffix.installAPK(apkName,apkPath)
        System.out.println("waiting for apk install")
        Thread.sleep(5000);    //waiting for apk installing

        //create a list
        var replayResults=ArrayList<String>()
        var fileTreeMutation: FileTreeWalk = File(mutationCasePath).walk()
        fileTreeMutation.maxDepth(1) //The traversal directory level is 1, i.e. no subdirectories need to be checked
            .filter { it.isFile } //Select documents only
            .filter { it.extension in listOf("txt") }//select .sh files
            .forEach {
                println("absolutePath: "+it.absolutePath)
                if(it.absolutePath.contentEquals(mutation_target)) {
                    println("Begin to replay!")
                    var result = replay(appName, packageName, it.absolutePath, target_fileName)
                    replayResults.add(result)
                }
            }

        println("Replay results:"+replayResults.size)
        var itemCount=0
        for (item in replayResults) {
            println((itemCount++).toString()+":"+item)
        }

        var timeStampFile1="$directoryPath/testOutput/$appName/timeStamp/$target_fileName"+"EventReplay.txt"
        var file1=File(timeStampFile1)
        if(!file1.exists()){
            println("Error:   "+"The file $timeStampFile1 is not exist!")
        }
        var writeFileStamp1=File(timeStampFile1)
        if(!writeFileStamp1.exists()) {
            writeFileStamp1.createNewFile()
        }
        writeFileStamp1.delete()
        println("The size of eventTimeStamp2:  "+ eventTimeStampReplay.size)
        for (item in eventTimeStampReplay){
            writeFileStamp1.appendText(item.index.toString()+":"+item.time.toString()+"\n")
        }

        pullImages(savedImagePath, savedDisplayedImagePath,imageFloderReplay,displayedImageFloderReplay)
        clearImages(savedImagePath, savedDisplayedImagePath)

        var layoutPath="$directoryPath/testOutput/$appName/layout/"
        var loops=getLoops(target_fileName,layoutPath)
        var imagesPath=imageFloderRecorder+"images/"
        var event_decoding = getDecodingEvents(eventTimeStampRecorder,imagesPath)


        val widgetCasePath =
            "$directoryPath/testOutput/$appName/caseWidget/$target_fileName.txt"//replay的case
        val layoutsPath =
            "$directoryPath/testOutput/$appName/layout/"//进行比较的layout  browseBlog.shLayout1.xml
        val generateTestCaseFile =
            "$directoryPath/testOutput/$appName/generateTestCase/generateTestCase.txt"

        var activityFile="$directoryPath/testOutput/$appName/activity/"+target_fileName+".txt"//这里存储每个event所位于的activity
        goToMutate(target_fileName,apkName,appName, widgetCasePath, layoutsPath, target_fileName, generateTestCaseFile, loops,event_decoding,packageName, apkPath,activityFile)

        clearImages(savedImagePath, savedDisplayedImagePath)
        PrefixAndSuffix.unstallAPK(packageName)
    }
}

fun pullImages(savedImagePath:String, savedDisplayedImagePath:String,imageFloderRecorder:String,displayedImageFloderRecorder:String){
    val recordFolder1 = File(imageFloderRecorder)
    recordFolder1.mkdirs()
    val recordFolder2 = File(displayedImageFloderRecorder)
    recordFolder2.mkdirs()

    var pullCommondRecord1="adb pull $savedImagePath           $imageFloderRecorder"
    cmd.sendEventWithoutStamp(1000000,pullCommondRecord1)
    println("pull images: "+pullCommondRecord1)

    var pullCommondRecord2="adb pull $savedDisplayedImagePath           $displayedImageFloderRecorder"
    cmd.sendEventWithoutStamp(1000000,pullCommondRecord2)
    println("pull images: "+pullCommondRecord2)
}

fun executeDumpCmd(adb:String){
    cmd.sendEventWithoutStamp(1000000,adb)
}

fun clearImages(savedImagePath:String, savedDisplayedImagePath:String){
    val clearCommondRecord1="adb shell rm -r  $savedImagePath"
    cmd.sendEventWithoutStamp(1000001,clearCommondRecord1)

    val clearCommondRecord2="adb shell rm -r  $savedDisplayedImagePath"
    cmd.sendEventWithoutStamp(1000001,clearCommondRecord2)
}

fun saveTimeStamp(timeStampFile:String,timeConsumption:String){
    var file=File(timeStampFile)
    if(!file.exists()){
        println("Error:   "+"The file $timeStampFile is not exist!")
    }
    var writeFileStamp=File(timeStampFile)

    if(!writeFileStamp.exists()) {
        writeFileStamp.createNewFile()
    }
    writeFileStamp.delete()
    writeFileStamp.appendText(timeConsumption.toString())
}

fun saveEvent(timeStampFile:String,event:String){
    var file=File(timeStampFile)
    if(!file.exists()){
        println("Error:   "+"The file $timeStampFile is not exist!")
    }
    var writeFileStamp=File(timeStampFile)

    if(!writeFileStamp.exists()) {
        writeFileStamp.createNewFile()
    }
    writeFileStamp.appendText(event.toString()+"\n")
}



fun <T> MutableList<T>.writeAndAdd(item: T, recorder: BufferedWriter) {
    add(item)
    recorder.write(item as String)
    recorder.newLine()
    recorder.flush()
}