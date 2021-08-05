adb shell am start -n com.moez.QKSMS/.feature.main.MainActivity
//点击“yes“
adb shell input tap 1200 1423
//点击"+"
adb shell input tap 1280 2233
//输入名字
adb shell input text "133"
//点击item
adb shell input tap 584 414
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
//点击菜单
adb shell input tap 1350 182
//点击新建联系人
adb shell input tap 1110 404
//点击相机图标
adb shell input tap 1333 757
//点击“choose photo“
adb shell input tap 400 1319
//点击“download”
adb shell input tap 120 1127
//选择一张图片
adb shell input tap 187 455
//点击“down”
adb shell input tap 1100 2300
//点击“save“
adb shell input tap 1185 182
//点击🌟图标
adb shell input tap 1043 182
adb shell input keyevent 4
adb shell am force-stop com.moez.QKSMS