package edu.nju.ics.alex.inputgenerator

import java.io.*
import java.net.ServerSocket
import java.net.Socket

/**
 * contain code to set up uiautomator client on the phone.
 * need to find a reliable port, write the port in the code frame,
 * and use old-version Android SDK to build and deploy with deploy.sh
 * IMPORTANT: DO NOT UPDATE SDK!!!!!
 * */

//find available port
val port: Int
    get() = _port!!
var _port: Int? = null
    get() {
        if (field == null) {
            field = getAPort()
        }
        return field
    }
private fun getAPort() : Int {
    var tryPort = 10000
    while (!usable(tryPort)){
        tryPort++
    }
    return tryPort
}
private fun usable(tryPort: Int) : Boolean =
    try {
        val test = ServerSocket(tryPort)
        test.close()
        true
    } catch (e: IOException) {
        false
    }

//use port and frame code to write complete clent code
private fun setUpClient() {
    val completeCode = frameCode.replace("int port =", "int port = $port;")
    File("$clientAddress/$clientCodePath").writeText(completeCode)
}

//set up connections with the client
val server = ServerSocket(port)
var reader: BufferedReader? = null
var writer: BufferedWriter? = null
var connection : Socket? = null
val connectionThread = Thread {
    while (!server.isClosed) {
        try {
            println("start accepting")
            connection = server.accept()
            reader = BufferedReader(InputStreamReader(connection!!.getInputStream()))
            writer = BufferedWriter(OutputStreamWriter(connection!!.getOutputStream()))

            println("[Connection established]")
        } catch (e: IOException) {
            try {
                Thread.sleep(1000)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            continue
        }
        try {
            Thread.sleep(10000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }
}
private fun setUpConnection() {
    connectionThread.start()
}

//use deploy.sh to deploy client
val deployThread = Thread{
    cmd.deployClient()
}
private fun deploy() {
    deployThread.start()
}

//cleanUp before the client can be reset
private fun cleanUp() {
    sendInput(Actions.STOP)
    try {
        server.close()
        connection?.close()
        reader?.close()
        writer?.close()
    } catch (e: IOException) {
        e.printStackTrace()
    }
}

//transform an action and a layout node to command
fun toClientCommand(action: Actions, node: LayoutNode? = null) =
    when {
        action == Actions.LONGCLICK -> {
            val x = (node!!.coords[2] + node.coords[0])/2
            val y = (node.coords[1] + node.coords[3])/2
            "swipe $x $y $x $y 1000"
        }
        action == Actions.SET_TEXT -> "${node!!.instance} ${action.action} test"
        node == null -> action.action
        else -> "${node.instance} ${action.action}"
    }

//used to send input to the client
fun sendInput(action: Actions, node: LayoutNode? = null) {
    //workaround: the longclick sent by client isn't long enough.
    //use adb shell input swipe x y x y 1000
    if (action == Actions.LONGCLICK) {
        val x = (node!!.coords[2] + node.coords[0])/2
        val y = (node.coords[1] + node.coords[3])/2
        cmd.sendEvent("swipe $x $y $x $y 1000")
        return
    }

    val message = toClientCommand(action, node)//if(node == null) action.action
        //else "${node.instance} ${action.action}"
    //wait until setup finishes
    while (connection == null || writer == null || reader == null) {
        try {
            Thread.sleep(10000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

    }
    while (true) {
        try {
            writer!!.write(message)
            writer!!.newLine()
            writer!!.flush()
            val result = reader!!.readLine()
            if (DEBUG) {
                println("[Client cmd stop execution] $result")
            }
            break
        } catch (e: IOException) {
            e.printStackTrace()
            try {
                Thread.sleep(10000)
            } catch (e1: InterruptedException) {
                e1.printStackTrace()
            }
            println("connection failed ,retry")
        }
    }
}

//to completely set up the client
fun setUpClientComplete() {
    setUpClient()
    setUpConnection()
    deploy()
}

//for unit test
fun main() {
    setUpClient()
    println(port)
    setUpConnection()
    deploy()
    readLine()
    //sendInput(Actions.LONGCLICK, 20)
    cleanUp()
}