adb shell am start -n eu.siacs.conversations/.ui.ConversationActivity
//点击“already have“
adb shell input tap 1000 2268
//填入“struggggle''
adb shell input text "struggggle"
//click 
adb shell input tap 305 624
//点击填入password
adb shell input tap 280 662
adb shell input text "liwenjie"
//点击“next”
adb shell input tap 1075 1319
//点击"deny"
adb shell input tap 800 1390
//点击第一个联系人
adb shell input tap 112 554
//点击添加附件
adb shell input tap 1216 178
//点击‘choose picture’
adb shell input tap 1033 352
//点击'allow'
adb shell input tap 1033 1437
//点击一张图片
adb shell input tap 400 900
//点击“发送”
adb shell input tap 1343 2188
//点击添加附件
adb shell input tap 1216 178
//点击‘choose picture’
adb shell input tap 1033 352
//点击一张图片
adb shell input tap 400 900
//点击“发送”
adb shell input tap 1343 2188
adb shell am force-stop eu.siacs.conversations