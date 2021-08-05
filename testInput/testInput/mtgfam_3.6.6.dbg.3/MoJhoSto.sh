adb shell am start -n com.gelakinetic.mtgfam/.FamiliarActivity
//点击“菜单”
adb shell input tap 98 178
//点击“MoJhoSto Basic”
adb shell input tap 400 1841
//点击“lets play”
adb shell input tap 319 2165
//点击第一张图片
adb shell input tap 300 600
//返回上一层
adb shell input keyevent 4
adb shell am force-stop com.gelakinetic.mtgfam