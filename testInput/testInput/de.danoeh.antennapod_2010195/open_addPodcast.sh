adb shell am start -n de.danoeh.antennapod/.activity.SplashActivity
//click"add a podcast"
adb shell input tap 400 1033
//click "an image"
adb shell input tap 560 850
//返回
adb shell input keyevent 4
adb shell am force-stop de.danoeh.antennapod