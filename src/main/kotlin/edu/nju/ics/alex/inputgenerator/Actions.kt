package edu.nju.ics.alex.inputgenerator

/**
 * store all actions acceptable by client
 * */

enum class Actions(val action: String) {
    //actions without parameter
    CLICK("click"),
    CLICK_BOTTOM_RIGHT("clickBottomRight"),
    CLICK_TOP_LEFT("clickTopLeft"),
    LONGCLICK("longClick"),
    LONGCLICK_BOTTOM_RIGHT("longClickBottomRight"),
    LONGCLICK_TOP_LEFT("longClickTopLeft"),
    CLEAR_TEXTFIELD("clearTextField"),

    //actions to control client
    STOP("stop"),
    DUMP("dump"),

    //actions with parameters
    SET_TEXT("setText"),
    SWIPE_DOWN("swipeDown"),
    SWIPE_UP("swipeUp"),
    SWIPE_LEFT("swipeLeft"),
    SWIPE_RIGHT("swipeRight"),

    //indicate no action is selected
    NULL("null")
}