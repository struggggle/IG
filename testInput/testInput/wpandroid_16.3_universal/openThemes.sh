adb shell am start -n org.wordpress.android/org.wordpress.android.ui.WPLaunchActivity
#open Themes
adb shell input tap 365 2072
#swipe down
adb shell input swipe 700 1400  700 400
#swipe up
adb shell input swipe 700 400  700 1400
#open a Theme
adb shell input tap 750 2000
adb shell input keyevent 4