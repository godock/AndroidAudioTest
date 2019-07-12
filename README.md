# Android 音频测试软件
***Android 音频测试软件*** 使用 Android Java 的音频接口 [AudioRecord](https://developer.android.com/reference/android/media/AudioRecord.html) 和 [AudioTrack](https://developer.android.com/reference/android/media/AudioTrack.html) 接口采集和播放音频原始码流来测试 Android 的音频输入和输出质量。

## Android 音频录制

录制接口参数设置

* 音频源（Audio Source)
* 采样率（Sample Rate）
* 声道数（Number Channels）

录制数据文件设置

* 录制文件名
* 文件格式 - 目前仅支持 pcm

## Android 音频播放

播放接口参数设置

* 音频流类型（Stream Type)
* 采样率（Sample Rate）
* 声道数（Number Channels）

播放文件设置

* 播放文件
