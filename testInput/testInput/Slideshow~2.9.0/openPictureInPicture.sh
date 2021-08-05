adb shell am start -n link.standen.michael.slideshow/.MainActivity
//安装涉及的点击
adb shell input tap 1000 1437
adb shell input tap 1230 2212
//打开Download文件夹
adb shell input tap 333 981
//click an image
adb shell input tap 100 361
//点击界面
adb shell input tap 600 1200
//click 菜单
adb shell input tap 1357 178
//click picture in picture
adb shell input tap 1000 850
adb shell am force-stop link.standen.michael.slideshow