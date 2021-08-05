adb shell am start -n org.wordpress.android/org.wordpress.android.ui.WPLaunchActivity
adb shell input tap 887 183
#click icon
adb shell input tap 718 500
#choose floder
adb shell input tap 190 1800
#choose an image
adb shell input tap 730 2000
#comform the image
adb shell input tap 944 169
adb shell input tap 1348 173
#back to main page
adb shell input tap 169 183
#shut down the app
adb shell am force-stop org.wordpress.android