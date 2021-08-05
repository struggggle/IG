package edu.nju.ics.alex.inputgenerator

import edu.nju.ics.alex.inputgenerator.InputMutation09.*
import java.io.*

/**
 * store all commands and a cmd
 * */
/**
 * Enum class for cmd commands
 * */
enum class Cmds(val command: String) {
    //commands for linux
    MKDIR("mkdir"),
    REMOVE("rm"),
    DEPLOYSCRIPT("./Client/deploy.sh"),
    //comands for adb
    INSTALLAPP("adb install"),
    UNINSTALLAPP("adb uninstall"),
    STARTMONKEY("adb shell monkey"), //used to run monkey for testing, replaying, and starting app
    STOPAPP("adb shell am force-stop"),
    DUMPLAYOUT("adb shell uiautomator dump"),
    PULL("adb pull"),  //从手机传送文件到电脑上
    PUSH("adb push"),
    BROADCAST("adb shell am broadcast"),
    GRANT("adb shell pm grant"),
    GETEVENT("adb shell getevent -lt"),
    GETCLIENTPID("adb shell ps | grep uiautomator"),
    KILL("adb shell kill "),
    SENDEVENT("adb shell input"),
    GETFOREGRROUND("adb shell dumpsys window windows | grep mCurrentFocus")
}
/**
 * Tags for logging command
 * */
enum class Tags(val tag: String) {
    //commands for linux
    MKDIR("mkdir"),
    REMOVE("rm"),
    DEPLOYSCRIPT("deploy shell script"),

    //comands for adb
    INSTALLAPP("install app"),
    UNINSTALLAPP("uninstall app"),
    STARTAPP("start app"),
    STOPAPP("stop app"),
    DUMPLAYOUT("dump GUI layout"),
    PULL("pull file"),
    PUSH("push file"),
    BROADCAST("broadcast"),
    GRANT("grant permission"),
    GETEVENT("getevent"),
    RUNSCRIPT("run script with Monkey"),
    GETCLIENTPID("get client pid"),
    KILL("kill process"),
    SENDEVENT("send event to the phone"),
    GETFOREGROUND("get the package name of the foreground app")
}

/**
 * Permissions
 * */
enum class Permissions(val sig: String) {
    WRITE_EXTERNAL_STORAGE("android.permission.WRITE_EXTERNAL_STORAGE"),
    READ_EXTERNAL_STORAGE("android.permission.READ_EXTERNAL_STORAGE")
}

/** the Cmd instance*/
val cmd = Cmd()    //这是一个全局变量

/**
 * class to hold cmd operations
 * */
class Cmd{
    private val runtime = Runtime.getRuntime()


    /*commands for shell*/

    /**
     * Make a directory (absolute path)
     * */
    fun mkdir(path: String) : Boolean
            = monitorResult("${Cmds.MKDIR.command} $path", Tags.MKDIR.tag)

    /**
     * Invoke deploy.sh to setup client
     * */
    fun deployClient() : Boolean
            = monitorResult("${Cmds.DEPLOYSCRIPT.command} $clientName $clientMain", Tags.DEPLOYSCRIPT.tag)


    /*commands for ADB*/

    /**
     * Uninstall the app
     * */

    fun uninstallApp(pack: String) : Boolean
            = monitorResult("${Cmds.UNINSTALLAPP.command} $pack", Tags.UNINSTALLAPP.tag)
    //以上是一个完整的命令执行吧：UNINSTALLAPP("adb uninstall"),  pack是传入的参数   Tags仅仅用来信息输出

    /**
     * Install the app
     * */

    fun installApp(apk: String) : Boolean
            = monitorResult("${Cmds.INSTALLAPP.command} $apk", Tags.INSTALLAPP.tag)

    /**
     * Start the app by Monkey with its package name
     * */
    fun startApp(packageName: String) : Boolean
            = monitorResult("${Cmds.STARTMONKEY.command} " +
            "-p $packageName -c android.intent.category.LAUNCHER 1", Tags.STARTAPP.tag)

    /**
     * Force-stop the app with its package name
     * */
    fun stopApp(packageName: String) : Boolean
            = monitorResult("${Cmds.STOPAPP.command} $packageName", Tags.STOPAPP.tag)
    /**
     * Grant a permission to the app. Need to *RESTART* the app.
     * */
    fun grantPermission(packageName: String, permission: Permissions) : Boolean
            = monitorResult("${Cmds.GRANT.command} $packageName ${permission.sig}", Tags.GRANT.tag)
    //|| stopApp(packageName) || startApp(packageName)


    /**
     * Dump the GUI layout hierarchy
     * */
    fun dump() : Boolean
            = monitorResult(Cmds.DUMPLAYOUT.command, Tags.DUMPLAYOUT.tag)

    fun dumpMT() : String
            = monitorResultMTT(Cmds.DUMPLAYOUT.command, Tags.DUMPLAYOUT.tag)

    /**
     * Dump the GUI layout via client
     * */
    fun dumpByClient() : Boolean {
        sendInput(Actions.DUMP)
        return true
    }

    /**
     * Pull file from the device
     * */
    fun pull(source: String, sink: String) : Boolean
            = monitorResult("${Cmds.PULL.command} $source $sink", Tags.PULL.tag)

    /**
     * Push a file to the device
     * */
    fun push(file: String, destination: String = "sdcard") : Boolean
            = monitorResult("${Cmds.PUSH.command} $file $destination", Tags.PUSH.tag)

    /**
     * Use Monkey to run a script. Require the script to be pushed first
     * */
    fun runScript(script: String, location: String = "sdcard") : Boolean
            = monitorResult("${Cmds.STARTMONKEY.command} -f $location/$script 1", Tags.RUNSCRIPT.tag)

    /**
     * Clean uiautomator process on the phone for new client to deploy
     * */
    fun cleanUIProcess() : Boolean
            = execute(Cmds.GETCLIENTPID.command).let {
                return if (it[0].isNotEmpty()) {
                    monitorResult("${Cmds.KILL.command} ${it[0]}", Tags.KILL.tag)
                } else {
                    true
                }
            }

    /**
     * Send event to the phone
     * */
    fun sendEvent(event: String) : Boolean
            = monitorResult("${Cmds.SENDEVENT.command} $event", Tags.SENDEVENT.tag)

    fun sendEventMT(EN: Int,event: String) : Boolean
            = monitorResultMT("$event", Tags.SENDEVENT.tag,EN)

    fun sendEventMT2(EN: Int,event: String) : Boolean
            = monitorResultMT2("$event", Tags.SENDEVENT.tag,EN)
    fun sendEventDump(EN: Int,event: String) : Boolean
            = monitorResultDump("$event", Tags.SENDEVENT.tag,EN)
    fun sendEventWithoutStamp(EN: Int,event: String) : Boolean
            = monitorResultWithoutStamp("$event", Tags.SENDEVENT.tag,EN)

    /**
     * Get the package name of the foreground app
     * !! return a string
     * */
    fun getForegroudApp() : String {
        val results = execute("${Cmds.GETFOREGRROUND.command}")
        return results[0].split("/")[0].split(" ").last()
    }


    /**
     * Use getevent to record low-level event
     * Start a thread to run getEvent, wait for readLine to kill the process
     * */
    fun getEvent(path: String) {
        var dump: Process? = null
        Thread{
            var inReader: BufferedReader? = null
            var errReader: BufferedReader? = null
            var fileWriter : BufferedWriter? = null
            try {
                dump = Runtime.getRuntime().exec("adb shell getevent -lt")
                inReader = BufferedReader(InputStreamReader(dump!!.inputStream))
                errReader = BufferedReader(InputStreamReader(dump!!.errorStream))
                fileWriter = BufferedWriter(FileWriter(path))
                var tem: String? = inReader.readLine()

                while (tem != null) {
                    fileWriter.write(tem)
                    fileWriter.newLine()
                    fileWriter.flush()
                    tem = inReader.readLine()
                }

            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                //don't need to close corresponding inputstreamreader, BufferedReader.close do that.
                if (inReader != null) {
                    try {
                        inReader.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                }
                if (errReader != null) {
                    try {
                        errReader.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                if (fileWriter != null) {
                    try {
                        fileWriter.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }.start()
        readLine()
        dump?.destroy()
    }


    private fun monitorResult(cmd: String, tag: String): Boolean {
        val out = execute(cmd)
        if (DEBUG) {
            println("这里是我需要的：  "+out[0]+"----"+out[1])
            printMessage(tag, out)
        }
        //return !out[0].contains("error:")//original
        return !out[0].contains("error:")
    }

    private fun monitorResultMTT(cmd: String, tag: String): String {
        val out = execute(cmd)
        if (DEBUG) {
            printMessage(tag, out)
        }
        //return !out[0].contains("error:")//original
        return out[1]
    }

    private fun monitorResultMT(cmd: String, tag: String,EN: Int): Boolean {
        val out = executeMT(EN,cmd)
        if (DEBUG) {
            printMessage(tag, out)
        }
        return !out[0].contains("error:")
    }

    private fun monitorResultMT2(cmd: String, tag: String,EN: Int): Boolean {
        val out = executeMT2(EN,cmd)
        if (DEBUG) {
            printMessage(tag, out)
        }
        return !out[0].contains("error:")
    }
    private fun monitorResultDump(cmd: String, tag: String,EN: Int): Boolean {
        val out = executeDump(EN,cmd)
        if (DEBUG) {
            printMessage(tag, out)
        }
        return !out[0].contains("error:")
    }

    private fun monitorResultWithoutStamp(cmd: String, tag: String,EN: Int): Boolean {
        val out = executeWithoutStamp(EN,cmd)
        if (DEBUG) {
            printMessage(tag, out)
        }
        return !out[0].contains("error:")
    }

    private fun printMessage(tag: String, out: Array<String>) {
        println("[$tag]: ")
        println(out[0])
        if (out[1].isNotEmpty()) {
            println("[$tag] error:")
            println(out[1])
        }
    }

    //这里的执行命令   返回一个String数组
    private fun execute(cmd: String): Array<String> {
        //System.out.println("Executing command: "+cmd);
        var inReader: BufferedReader? = null
        var errReader: BufferedReader? = null
        val rAE = Array(2){""}
        try {
            val dump = runtime.exec(cmd)  //执行命令，获得返回值
            inReader = BufferedReader(InputStreamReader(dump.inputStream))
            errReader = BufferedReader(InputStreamReader(dump.errorStream))
            val rBuilder = StringBuilder()
            val eBuilder = StringBuilder()
            var tem: String? = inReader.readLine()

            while (tem != null) {
                rBuilder.append(tem).append("\n")
                tem = inReader.readLine()
            }
            rAE[0] = rBuilder.toString()
            tem = errReader.readLine()
            while (tem != null) {
                eBuilder.append(tem).append("\n")
                tem = errReader.readLine()
            }
            rAE[1] = eBuilder.toString()

        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            //don't need to close corresponding inputstreamreader, BufferedReader.close do that.
            if (inReader != null) {
                try {
                    inReader.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
            if (errReader != null) {
                try {
                    errReader.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        }
        return rAE
    }
}

//这里的执行命令   返回一个String数组
private fun executeMT(EN: Int,cmd: String): Array<String> {
    //System.out.println("Executing command: "+cmd);
    var inReader: BufferedReader? = null
    var errReader: BufferedReader? = null
    val rAE = Array(2){""}
    try {
        val time=System.currentTimeMillis()
        val eventObj=EventTimeStamp()
        eventObj.index=EN
        eventObj.time=time
        eventTimeStampRecorder.add(eventObj)

        val dump = Runtime.getRuntime().exec(cmd)
        inReader = BufferedReader(InputStreamReader(dump.inputStream))
        errReader = BufferedReader(InputStreamReader(dump.errorStream))
        val rBuilder = StringBuilder()
        val eBuilder = StringBuilder()
        var tem: String? = inReader.readLine()

        while (tem != null) {
            rBuilder.append(tem).append("\n")
            tem = inReader.readLine()
        }
        rAE[0] = rBuilder.toString()
        tem = errReader.readLine()
        while (tem != null) {
            eBuilder.append(tem).append("\n")
            tem = errReader.readLine()
        }
        rAE[1] = eBuilder.toString()

    } catch (e: IOException) {
        e.printStackTrace()
    } finally {
        //don't need to close corresponding inputstreamreader, BufferedReader.close do that.
        if (inReader != null) {
            try {
                inReader.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
        if (errReader != null) {
            try {
                errReader.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }
    return rAE
}

private fun executeWithoutStamp(EN: Int,cmd: String): Array<String> {
    //System.out.println("Executing command: "+cmd);
    var inReader: BufferedReader? = null
    var errReader: BufferedReader? = null
    val rAE = Array(2){""}
    try {

        val dump = Runtime.getRuntime().exec(cmd)
        inReader = BufferedReader(InputStreamReader(dump.inputStream))
        errReader = BufferedReader(InputStreamReader(dump.errorStream))
        val rBuilder = StringBuilder()
        val eBuilder = StringBuilder()
        var tem: String? = inReader.readLine()

        while (tem != null) {
            rBuilder.append(tem).append("\n")
            tem = inReader.readLine()
        }
        rAE[0] = rBuilder.toString()
        tem = errReader.readLine()
        while (tem != null) {
            eBuilder.append(tem).append("\n")
            tem = errReader.readLine()
        }
        rAE[1] = eBuilder.toString()

    } catch (e: IOException) {
        e.printStackTrace()
    } finally {
        //don't need to close corresponding inputstreamreader, BufferedReader.close do that.
        if (inReader != null) {
            try {
                inReader.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
        if (errReader != null) {
            try {
                errReader.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }
    return rAE
}


private fun executeMT2(EN: Int,cmd: String): Array<String> {
    //var path="/Users/wenjieli/Library/Android/sdk/platform-tools/"
    //System.out.println("Executing command: "+cmd);
    var inReader: BufferedReader? = null
    var errReader: BufferedReader? = null
    val rAE = Array(2){""}
    try {
        //TODO 需要在这里调用loopInfo()
        if((EN==0)&&beginToObtainLoopInfo){
            obtainLoopInfo(packageNameJava)
        }

        val time=System.currentTimeMillis()
        val eventObj=EventTimeStamp()
        eventObj.index=EN
        eventObj.time=time
        eventObj.event=cmd
        eventTimeStampReplay.add(eventObj)
        println("Size of eventTImeStamp2:  "+ eventTimeStampReplay.size+"    "+EN)

        val dump = Runtime.getRuntime().exec(cmd)
        inReader = BufferedReader(InputStreamReader(dump.inputStream))
        errReader = BufferedReader(InputStreamReader(dump.errorStream))
        val rBuilder = StringBuilder()
        val eBuilder = StringBuilder()
        var tem: String? = inReader.readLine()

        while (tem != null) {
            rBuilder.append(tem).append("\n")
            tem = inReader.readLine()
        }
        rAE[0] = rBuilder.toString()
        tem = errReader.readLine()
        while (tem != null) {
            eBuilder.append(tem).append("\n")
            tem = errReader.readLine()
        }
        rAE[1] = eBuilder.toString()

    } catch (e: IOException) {
        e.printStackTrace()
    } finally {
        //don't need to close corresponding inputstreamreader, BufferedReader.close do that.
        if (inReader != null) {
            try {
                inReader.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
        if (errReader != null) {
            try {
                errReader.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }
    return rAE
}

private fun executeDump(EN: Int,cmd: String): Array<String> {
    //var path="/Users/wenjieli/Library/Android/sdk/platform-tools/"
    //System.out.println("Executing command: "+cmd);
    var inReader: BufferedReader? = null
    var errReader: BufferedReader? = null
    val rAE = Array(2){""}
    try {

        val dump = Runtime.getRuntime().exec(cmd)
        inReader = BufferedReader(InputStreamReader(dump.inputStream))
        errReader = BufferedReader(InputStreamReader(dump.errorStream))
        val rBuilder = StringBuilder()
        val eBuilder = StringBuilder()
        var tem: String? = inReader.readLine()

        while (tem != null) {
            rBuilder.append(tem).append("\n")
            tem = inReader.readLine()
        }
        rAE[0] = rBuilder.toString()
        tem = errReader.readLine()
        while (tem != null) {
            eBuilder.append(tem).append("\n")
            tem = errReader.readLine()
        }
        rAE[1] = eBuilder.toString()

    } catch (e: IOException) {
        e.printStackTrace()
    } finally {
        //don't need to close corresponding inputstreamreader, BufferedReader.close do that.
        if (inReader != null) {
            try {
                inReader.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
        if (errReader != null) {
            try {
                errReader.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }
    return rAE
}

/**
 * Set up the permissions of the app after installation for coverage collection
 * 这里是在进行app的权限设置
 * */
fun setUpCoverageCollection(): Boolean
        = cmd.startApp(appPack)
        && cmd.grantPermission(appPack, Permissions.WRITE_EXTERNAL_STORAGE)
        && cmd.grantPermission(appPack, Permissions.READ_EXTERNAL_STORAGE)
        && cmd.stopApp(appPack)
        && cmd.startApp(appPack)

//for testing only
fun main(args: Array<String>) {
    //cmd.getEvent("/home/alex-wang/lala.txt")
    println(cmd.getForegroudApp())
}