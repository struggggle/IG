adb shell am start -n org.wikipedia.alpha/org.wikipedia.main.MainActivity
//点击"skip"
adb shell input tap 150 2296
//点击输入框
adb shell input tap 340 360
//输入信息
adb shell input text "dog"
//打开一个item
adb shell input tap 1300 390
//返回
adb shell input keyevent 4
adb shell am force-stop org.wikipedia.alpha