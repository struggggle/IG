adb shell am start -n org.wikipedia.alpha/org.wikipedia.main.MainActivity
//点击"skip"
adb shell input tap 150 2296
//向上滑动
adb shell input swipe 700 1400  700 400
adb shell input swipe 700 1400  700 400
adb shell input swipe 700 1400  700 400
adb shell input swipe 700 1400  700 400
adb shell am force-stop org.wikipedia.alpha