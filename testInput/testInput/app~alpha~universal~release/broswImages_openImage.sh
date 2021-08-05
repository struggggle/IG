adb shell am start -n org.wikipedia.alpha/org.wikipedia.main.MainActivity
//点击"skip"
adb shell input tap 150 2296
//swipe up
adb shell input swipe 700 1400  700 400
//click an image
adb shell input tap 700 888
//返回
adb shell input keyevent 4
adb shell am force-stop org.wikipedia.alpha