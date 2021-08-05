adb shell am start -n com.keylesspalace.tusky/.SplashActivity
//edit instance
adb shell input text "artalley.social"
//click "login"
adb shell input tap 700 1174
//click "just once"
adb shell input tap 948 1874
//click "Authorize"
adb shell input tap 700 1040
//click "edit"
adb shell input tap 1277 2231
//click 选择
adb shell input tap 84 1310
//click "add image"
adb shell input tap 117 1000
//click "allow"
adb shell input tap 1000 1390
//click "an image"
adb shell input tap 400 900
//click "TOOT"
adb shell input tap 1240 2287 
adb shell am force-stop com.keylesspalace.tusky