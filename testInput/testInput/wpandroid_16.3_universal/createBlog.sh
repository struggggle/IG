adb shell am start -n org.wordpress.android/org.wordpress.android.ui.WPLaunchActivity
#在主界面添加
adb shell input tap 1291 2235
adb shell input text "TITLE5"
adb shell input tap 352 634
adb shell input tap 103 1324
adb shell input tap 455 817
adb shell input tap 427 883
adb shell input tap 84 2306
adb shell input tap 455 817
adb shell input tap 1000 883
adb shell input tap 84 2306
adb shell input tap 455 817
adb shell input tap 400 1700
adb shell input tap 84 2306
adb shell input tap 455 817
adb shell input tap 800 1700
#publish
adb shell input tap 1315 178
#上面到执行后跳转到主界面，下面打开blog list
adb shell input tap 333 1249
#在blog list这里开始create blog
adb shell input tap 1291 2235
#开始编辑
adb shell input text "TITLE4"
adb shell input tap 352 634
adb shell input tap 103 1324
adb shell input tap 455 817
adb shell input tap 427 883
adb shell input tap 84 2306
adb shell input tap 455 817
adb shell input tap 1000 883
#publish  一次提交成功并停留在blog list界面
adb shell input tap 1315 178
#回到主界面
adb shell input keyevent 4
