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
//点击“single artwork”
adb shell input tap 427 1880
//点击一张图片
adb shell input tap 230 2050
//向上滑动
adb shell input swipe 700 1200  700 400
adb shell am force-stop net.nurik.roman.muzei