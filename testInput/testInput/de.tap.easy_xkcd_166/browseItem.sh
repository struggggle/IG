adb shell am start -n de.tap.easy_xkcd/.Activities.MainActivity
//点击“返回”
adb shell input keyevent 4
//click cancel
adb shell input tap 939 1681
//启动
adb shell am start -n de.tap.easy_xkcd/.Activities.MainActivity
//点击“返回”
adb shell input keyevent 4
//打开一个漫画
adb shell input tap 138 427
//点击“返回”
adb shell input keyevent 4
adb shell am force-stop de.tap.easy_xkcd