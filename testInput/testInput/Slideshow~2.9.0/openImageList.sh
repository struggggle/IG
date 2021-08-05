adb shell am start -n link.standen.michael.slideshow/.MainActivity
//安装涉及的点击
adb shell input tap 1000 1437
adb shell input tap 1230 2212
//打开Download文件夹
adb shell input tap 333 981
//返回到上一层
adb shell input keyevent 4
adb shell am force-stop link.standen.michael.slideshow