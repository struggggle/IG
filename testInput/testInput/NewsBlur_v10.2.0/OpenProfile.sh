adb shell am start -n com.newsblur/.activity.InitActivity
//输入用户名
adb shell input text "struggggle"
//输入密码
adb shell input tap 329 887
adb shell input text "liwenjie"
//点击登陆
adb shell input tap 1172 1074
//点击右上角“profile“
adb shell input tap 1348 169
adb shell am force-stop com.newsblur