adb shell am start -n net.cyclestreets/.CycleStreets
//click"allow"
adb shell input tap 1033 1432
//click"ok"
adb shell input tap 1221 2174
//click"meau"
adb shell input tap 93 164
//click"photomap"
adb shell input tap 340 850
//click "an image"
adb shell input tap 500 1117
//返回
adb shell input keyevent 4
adb shell am force-stop net.cyclestreets