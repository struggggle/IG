adb shell am start -n com.keylesspalace.tusky/.SplashActivity
//edit instance
adb shell input text "artalley.social"
//click "login"
adb shell input tap 700 1174
//click "just once"
adb shell input tap 948 1874
//click "Authorize"
adb shell input tap 700 1040
//click an image
adb shell input tap 800 1100
//返回
adb shell input keyevent 4
adb shell am force-stop com.keylesspalace.tusky