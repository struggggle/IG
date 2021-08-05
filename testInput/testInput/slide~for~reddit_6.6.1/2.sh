adb shell am start -n me.ccrama.redditslide/.Activities.Slide
//滑动新闻
adb shell input swipe 700 1400  700 400
//点击“all
adb shell input tap 363 296
//点击第一个新闻，显示了图片
adb shell input tap 463 558
//返回
adb shell input keyevent 4
//回到默认界面
adb shell input tap 141 287
adb shell am force-stop me.ccrama.redditslide