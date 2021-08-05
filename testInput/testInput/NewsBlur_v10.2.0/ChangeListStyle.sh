adb shell am start -n com.newsblur/.activity.InitActivity
//输入用户名
adb shell input text "struggggle"
//输入密码
adb shell input tap 329 887
adb shell input text "liwenjie"
//点击登陆
adb shell input tap 1172 1074
//点击saved_stories
adb shell input tap 310 1100
//点击“菜单”
adb shell input tap 1350 187
//点击“List style“
adb shell input tap 930 526
//点击”grid(file)“
adb shell input tap 915 1019
//返回
adb shell input keyevent 4
adb shell am force-stop com.newsblur