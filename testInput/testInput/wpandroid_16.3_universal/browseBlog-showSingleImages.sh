adb shell am start -n org.wordpress.android/org.wordpress.android.ui.WPLaunchActivity
adb shell input tap 544 178
#open the a blog
adb shell input tap 544 800
#click open a Web view
adb shell input tap 737 1296
#swipe
adb shell input swipe 1179 1200  200 1200
#swipe
adb shell input swipe 1179 1200  200 1200
#swipe
adb shell input swipe 200 1200  1179 1200
#back to blog list
adb shell input keyevent 4
adb shell input keyevent 4
#swipe down
adb shell input swipe 700 1400  700 400
#open a new blog and open images
adb shell input tap 544 800
#click open a Web view
adb shell input tap 737 1296
#swipe
adb shell input swipe 1179 1200  200 1200
#swipe
adb shell input swipe 1179 1200  200 1200
#swipe
adb shell input swipe 200 1200  1179 1200
#back to blog list
adb shell input keyevent 4
adb shell input keyevent 4
#shut down the app
adb shell am force-stop org.wordpress.android