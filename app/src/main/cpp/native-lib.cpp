#include <jni.h>
#include "audio_manager.h"

extern "C" {

    JNIEXPORT jboolean JNICALL
    Java_io_github_godock_androidaudiotest_NativeManager_startRecording(
        JNIEnv* env, jclass clazz, jstring file, jint sampleRate, jint numChannels) {
        const char* fileChars = env->GetStringUTFChars(file, NULL);
        bool result = AudioManager::getInstance()->startRecording(fileChars, sampleRate, numChannels);
        return result ? JNI_TRUE : JNI_FALSE;
    }

    JNIEXPORT jboolean JNICALL
    Java_io_github_godock_androidaudiotest_NativeManager_stopRecording(
        JNIEnv* env, jclass clazz) {
        bool result = AudioManager::getInstance()->stopRecording();
        return result ? JNI_TRUE : JNI_FALSE;
    }

    JNIEXPORT jint JNICALL
    Java_io_github_godock_androidaudiotest_NativeManager_putData(
        JNIEnv* env, jclass clazz, jbyteArray buffer, jint bytes) {
        int result = -1;
        jboolean isCopy = JNI_FALSE;
        jbyte* dataElements = env->GetByteArrayElements(buffer, &isCopy);

        if (dataElements) {
            result = AudioManager::getInstance()->putData(dataElements, bytes);
            env->ReleaseByteArrayElements(buffer, dataElements, 0);
        }

        return result;
    }

    JNIEXPORT jboolean JNICALL
    Java_io_github_godock_androidaudiotest_NativeManager_startTracking(
        JNIEnv* env, jclass clazz, jstring file, jint sampleRate, jint numChannels) {
        const char* fileChars = env->GetStringUTFChars(file, NULL);
        bool result = AudioManager::getInstance()->startTracking(fileChars, sampleRate, numChannels);
        return result ? JNI_TRUE : JNI_FALSE;
    }

    JNIEXPORT jboolean JNICALL
    Java_io_github_godock_androidaudiotest_NativeManager_stopTracking(
        JNIEnv* env, jclass clazz) {
        bool result = AudioManager::getInstance()->stopTracking();
        return result ? JNI_TRUE : JNI_FALSE;
    }

    JNIEXPORT jint JNICALL
    Java_io_github_godock_androidaudiotest_NativeManager_getData(
        JNIEnv* env, jclass clazz, jbyteArray buffer, jint bytes) {
        int result = -1;
        jboolean isCopy = JNI_FALSE;
        jbyte* dataElements = env->GetByteArrayElements(buffer, &isCopy);

        if (dataElements) {
            result = AudioManager::getInstance()->getData(dataElements, bytes);
            env->ReleaseByteArrayElements(buffer, dataElements, 0);
        }

        return result;
    }

}
