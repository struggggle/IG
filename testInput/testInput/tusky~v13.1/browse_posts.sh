adb shell am start -n com.keylesspalace.tusky/.SplashActivity
//edit instance
adb shell input text "artalley.social"
//click "login"
adb shell input tap 700 1174
//click "just once"
adb shell input tap 948 1874
//click "Authorize"
adb shell input tap 700 1040
//swipe
adb shell input swipe 700 1400  700 400
//swipe
adb shell input swipe 700 1400  700 400
adb shell am force-stop com.keylesspalace.tusky