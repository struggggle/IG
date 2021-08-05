adb shell am start -n com.newsblur/.activity.InitActivity
//点击“allow“
adb shell input tap 1038 1385
//点击“搜索框”
adb shell input tap 500 2282
//输入“1”
adb shell input tap
//滑动屏幕
adb shell input swipe 700 1400  700 400

adb shell input tap 
adb shell input tap 

adb shell input tap 236 828
adb shell input tap 442 429
adb shell input swipe 700 1400  700 400
adb shell input keyevent 4
adb shell input swipe 700 1400  700 400
adb shell input keyevent 4
adb shell am force-stop org.wordpress.android