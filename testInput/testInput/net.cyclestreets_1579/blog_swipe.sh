adb shell am start -n net.cyclestreets/.CycleStreets
//click"allow"
adb shell input tap 1033 1432
//click"ok"
adb shell input tap 1221 2174
//click"meau"
adb shell input tap 93 164
//click"blog"
adb shell input tap 340 1188
adb shell input swipe 700 1400  700 400
adb shell am force-stop net.cyclestreets