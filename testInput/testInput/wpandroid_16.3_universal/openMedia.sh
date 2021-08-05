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
//open media
adb shell input tap 1258 1000
//open an image
adb shell input tap 728 986
//fangda
adb shell input tap 1282 944
adb shell input keyevent 4
adb shell input keyevent 4