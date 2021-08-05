package edu.nju.ics.alex.inputgenerator

import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.lang.Exception
import kotlin.streams.toList

/**
 * store all configurations.
 * capable of accepting out configuration file.
 * */


val appMutation="wordPressMutation"

val config = File("Config.txt").readLines()

//settings reading from the configuration file
val apkName = getFromConfig("apkName")

val appPack = getFromConfig("appPack")

val appLaunchAct = getFromConfig("appLaunchAct")

val clientAddress = getFromConfig("clientAddress")

private fun getFromConfig(item: String)
        = config.first { it.startsWith(item) }
    .split("=")[1].trim()

//constant settings
val DEBUG = true

val xmlFolder = "/layouts"
val xmlFolderMT = "/layoutsMT"
val xmlFolderExplore = "/layoutsExplore"
val xmlFolderStable = "/layoutsStable"
val parseFile = "window_dump.xml"//"dump.xml"
val parseDir = "/sdcard"//"/data/local/tmp/"
val apkFolder = "apks/"
val workingDir = System.getProperty("user.dir")
val inputSequenceFile = "inputsequence.txt"//--这里应该是存储了Input序列，可以找出来看看

val clientCodePath = "src/edu/nju/Alex/uiautomator/test/UiAutomatorTestCase.java"
val clientName = "UiTraverse"
val clientMain = "edu.nju.Alex.uiautomator.test.UiAutomatorTestCase"
val compareMode = Modes.ALL_LOOSE