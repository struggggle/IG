adb shell am start -n org.ligi.passandroid/.ui.PassListActivity
//点击+
adb shell input tap 1250 2197
//点击“create pass”
adb shell input tap 945 1943
//点击"add head image"
adb shell input tap 717 873
//点击“allow“
adb shell input tap 1029 1414
//点击“第一张图片”
adb shell input tap 361 855
//点击edit图标
adb shell input tap 1211 173
//点击，去掉键盘
adb shell input tap 324 2475
//向上滑倒底
adb shell input swipe 700 1400  700 400
adb shell input swipe 700 1400  700 400
//点击"add logo image"
adb shell input tap 700 2062
//点击“第一张图片”
adb shell input tap 361 855
//向上滑倒底
adb shell input swipe 700 1400  700 400
adb shell input swipe 700 1400  700 400
//点击"add footer image"
adb shell input tap 718 2296
//点击“第一张图片”
adb shell input tap 361 855
adb shell input keyevent 4
adb shell input keyevent 4
adb shell am force-stop org.ligi.passandroid