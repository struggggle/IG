adb shell am start -n com.moez.QKSMS/.feature.main.MainActivity
//点击“yes“
adb shell input tap 1200 1423
//点击第一个联系人
adb shell input tap 142 436
//返回
adb shell input keyevent 4
adb shell am force-stop com.moez.QKSMS