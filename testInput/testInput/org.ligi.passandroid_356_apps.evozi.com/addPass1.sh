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
//点击添加进去的图片
adb shell input tap 685 1290
adb shell am force-stop org.ligi.passandroid