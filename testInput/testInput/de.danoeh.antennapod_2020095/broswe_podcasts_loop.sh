adb shell am start -n de.danoeh.antennapod/.activity.SplashActivity
//click"add a podcast"
adb shell input tap 400 1033
//click "more"
adb shell input tap 1240 600
//click an item
adb shell input tap 155 436
//返回
adb shell input keyevent 4
adb shell am force-stop de.danoeh.antennapod