adb shell am start -n org.wordpress.android/org.wordpress.android.ui.WPLaunchActivity
adb shell input tap 380 1249
#click edit
adb shell input tap 230 1080
#choose an image
adb shell input tap 230 1080
#begin to edit
adb shell input tap 718 760
#input image title
adb shell input text "cute"
#save edit
adb shell input tap 1334 183
#update
adb shell input tap 1334 183
#这时候返回blog list，再次click edit
adb shell input tap 230 1080
#choose an image
adb shell input tap 230 1080
#begin to add an image
adb shell input tap 93 1334
#select a photo from gallery
adb shell input tap 544 822
#select an image
adb shell input tap 1000 800
#选好图片后界面发生了变化，添加image的按钮位置变了
#begin to add an image。继续添加图片
adb shell input tap 93 2311
#select a photo from gallery
adb shell input tap 544 822
#select an image
adb shell input tap 1000 1700
#选好图片后界面发生了变化，添加image的按钮位置变了
#begin to add an image。继续添加图片
adb shell input tap 93 2311
#select a photo from gallery
adb shell input tap 544 822
#select an image
adb shell input tap 300 1700
#update
adb shell input tap 1310 183
#go back to blog posts