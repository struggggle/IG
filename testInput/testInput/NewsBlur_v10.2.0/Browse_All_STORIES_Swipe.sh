adb shell am start -n com.newsblur/.activity.InitActivity
//输入用户名
adb shell input text "struggggle"
//输入密码
adb shell input tap 329 887
adb shell input text "liwenjie"
//点击登陆
adb shell input tap 1172 1074
//点击all_stories
adb shell input tap 270 1100
//上滑
adb shell input swipe 700 1400  700 400
//点击一个新闻
adb shell input tap 1300 474
adb shell input keyevent 4
adb shell input keyevent 4
adb shell am force-stop com.newsblur