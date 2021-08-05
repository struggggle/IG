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
//点击“my photos”
adb shell input tap 418 1446
//点击"allow"
adb shell input tap 1042 1470
//向上滑动一段距离
adb shell input swipe 700 1200  700 400
//点击"browse"
adb shell input tap 220 1620
//点击“+”
adb shell input tap 713 2226
//点击“图片”图标
adb shell input tap 356 2287
//进入图片浏览界面，滑动图片列表
adb shell input swipe 700 1200  700 400
//点击左上角三个横杠图标，变化显示形式
adb shell input swipe 1216 173
//返回上一层
adb shell input keyevent 4
adb shell am force-stop net.nurik.roman.muzei