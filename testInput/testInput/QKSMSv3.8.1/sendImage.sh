adb shell am start -n com.moez.QKSMS/.feature.main.MainActivity
//点击“yes“
adb shell input tap 1200 1423
//点击第一个联系人
adb shell input tap 142 436
//点击”+“添加发送附件
adb shell input tap 117 2273
//点击“attach a photo”
adb shell input tap 117 1883
//点击“download”
adb shell input tap 120 1127
//选择一张图片
adb shell input tap 187 455
//点击“done”
adb shell input tap 1334 178
//点击“发送”
adb shell input tap 1329 2268
adb shell input keyevent 4
adb shell am force-stop com.moez.QKSMS