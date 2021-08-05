adb shell am start -n com.money.manager.ex/.home.MainActivity
//点击"close"
adb shell input tap 1277 2315
//点击返回到主页面
adb shell input tap 93 192
//click "allow"
adb shell input tap 1060 1432
//click "create database"
adb shell input tap 1060 1432
//click "run"
adb shell input tap 680 1122
//click "menu"
adb shell input tap 98 178
adb shell am force-stop com.money.manager.ex