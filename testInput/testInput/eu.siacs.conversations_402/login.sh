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
adb shell am force-stop eu.siacs.conversations