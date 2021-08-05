adb shell am start -n net.nurik.roman.muzei/com.google.android.apps.muzei.MuzeiActivity
//点击“activity”
adb shell input tap 718 1803
//点击"set wallpaper"
adb shell input tap 1164 173
//点击“home screen”
adb shell input tap 544 1230
//点击中间的“muzei“图标
adb shell input tap 718 1038
//点击"sources"
adb shell input tap 718 2273
//向上滑动一段距离
adb shell input swipe 700 1200  700 400
//返回上一层
adb shell input keyevent 4
//点击"sources"
adb shell input tap 718 2273
//返回上一层
adb shell input keyevent 4
//点击"sources"
adb shell input tap 718 2273
//返回上一层
adb shell input keyevent 4
//点击"sources"
adb shell input tap 718 2273
//返回上一层
adb shell input keyevent 4
//点击"sources"
adb shell input tap 718 2273
//返回上一层
adb shell input keyevent 4
//点击"sources"
adb shell input tap 718 2273
//返回上一层
adb shell input keyevent 4
//点击"sources"
adb shell input tap 718 2273
//返回上一层
adb shell input keyevent 4
//点击"sources"
adb shell input tap 718 2273
//返回上一层
adb shell input keyevent 4
//点击"sources"
adb shell input tap 718 2273
adb shell am force-stop net.nurik.roman.muzei