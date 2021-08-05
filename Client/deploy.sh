current=$PWD/Client
#cd /home/alex-wang/Data1/AndroidSDK/platforms/android-23
#android create uitest-project -n $1 -t 6 -p $current/${1}
cd $current/${1}
ant clean
ant build
adb push $current/${1}/bin/${1}.jar data/local/tmp
adb shell uiautomator runtest ${1}.jar -c ${2}
