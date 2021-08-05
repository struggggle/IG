adb shell am start -n link.standen.michael.slideshow/.MainActivity
//安装涉及的点击
adb shell input tap 1000 1437
adb shell input tap 1230 2212
//打开Download文件夹
adb shell input tap 333 981
//上滑
adb shell input swipe 700 1400  700 400
adb shell am force-stop link.standen.michael.slideshow