adb shell am start -n me.ccrama.redditslide/.Activities.Slide
//点击“get start“
adb shell input tap 1200 2287
//点击"done"
adb shell input tap 1315 2282
//点击“sllow”
adb shell input tap 1029 1390
//点击“changlog”
adb shell input tap 1176 2246
//点击“ok”
adb shell input tap 1051 2131
//点击“ART”
adb shell input tap 1243 361
//点击第一个图片
adb shell input tap 677 766
//点击返回
adb shell input keyevent 4
adb shell input keyevent 4
//点击第一张图片
adb shell input tap 677 766
//再点击一次
adb shell input keyevent 4
adb shell am force-stop me.ccrama.redditslide