adb shell am start -n org.wordpress.android/org.wordpress.android.ui.WPLaunchActivity
adb shell input tap 709 2076
adb shell input tap 455 620
adb shell input text "wenjielinju@gmail.com"
adb shell input tap 713 977
adb shell input text "liwenjie312523"
adb shell input tap 690 1287
adb shell input tap 600 2292
//进入主页，点击按键提示框（消除界面不一致）
adb shell input tap 1000 1780
#open media
adb shell input tap 333 1412
#open an image information
adb shell input tap 300 700
adb shell input keyevent 4
#swipe down
adb shell input swipe 700 1400  700 400
#open an image information
adb shell input tap 300 700
#start to set
adb shell input tap 1000 173
adb shell input keyevent 4
adb shell input keyevent 4
adb shell input keyevent 4
adb shell am force-stop org.wordpress.android