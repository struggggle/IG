adb shell am start -n fr.neamar.kiss/.MainActivity
//点击“allow”
adb shell input tap 1000 1386
//点击输入框
adb shell input tap 270 2282
//输入框
adb shell input text "1"
//点击一个item
adb shell input tap 150 1074
//返回
adb shell input keyevent 4
//点击输入框
adb shell input tap 400 2282
//输入框
adb shell input text "1"
adb shell am force-stop fr.neamar.kiss