adb shell am start -n org.mariotaku.twidere/.activity.MainActivity
//click "twitter"
adb shell input tap 310 900
//click "twitter"
adb shell input tap 250 1100
//click "sign in"
adb shell input tap 700 1200
//edit "username"
adb shell input tap 270 1132
adb shell input text "struggggle1"
//edit "password"
adb shell input tap 200 1287
adb shell input text "liwenjie"
//click "login in"
adb shell input tap 347 695
adb shell input tap 244 1667
//click 
adb shell input tap 1280 2245
//click "no"
adb shell input tap 890 1400
//swipe to fresh
adb shell input swipe 700 400  700 1400  
//click an image
adb shell input tap 800 1150
//返回
adb shell input keyevent 4
adb shell input keyevent 4
adb shell am force-stop org.mariotaku.twidere