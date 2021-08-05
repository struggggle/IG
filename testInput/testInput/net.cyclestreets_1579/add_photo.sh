adb shell am start -n net.cyclestreets/.CycleStreets
//click"allow"
adb shell input tap 1033 1432
//click"ok"
adb shell input tap 1221 2174
//click"meau"
adb shell input tap 93 164
//click"add photo"
adb shell input tap 342 1019
//click"existing photo"
adb shell input tap 700 670
//接下来需要进行注册
adb shell input tap 338 436
adb shell input text "strugglelwj"
adb shell input tap 338 636
adb shell input text "liwenjie312523"
//click "sign in"
adb shell input tap 1000 1300
adb shell am force-stop net.cyclestreets