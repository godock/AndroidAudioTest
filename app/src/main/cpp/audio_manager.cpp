#include <assert.h>
#include <errno.h>
#include <android/log.h>
#include "audio_manager.h"

#define TAG "AudioManager"

#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN, TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,TAG, __VA_ARGS__)

AudioManager::AudioManager(): m_fpRecord(NULL), m_fpTrack(NULL) {
}

AudioManager::~AudioManager() {
    if (m_fpRecord) {
        fclose(m_fpRecord);
        m_fpRecord = NULL;
    }

    if (m_fpTrack) {
        fclose(m_fpTrack);
        m_fpTrack = NULL;
    }
}

AudioManager* AudioManager::getInstance() {
    static AudioManager instance;
    return &instance;
}

bool AudioManager::startRecording(string file, int sampleRate, int numChannels) {
    LOGI("start recording, sample rate is %d, num channels is %d\n", sampleRate, numChannels);

    if (m_fpRecord != NULL) {
        LOGW("close unclosed recording file descriptor\n");
        fclose(m_fpRecord);
        m_fpRecord = NULL;
    }

    m_fpRecord = fopen(file.c_str(), "wb");

    if (m_fpRecord == NULL) {
        LOGE("open recording file %s failed, error: %s\n", file.c_str(), strerror(errno));
        return false;
    } else {
        LOGI("open recording file %s succeed\n", file.c_str());
        return true;
    }
}

bool AudioManager::stopRecording() {
    LOGI("stop recording\n");

    if (m_fpRecord != NULL) {
        fclose(m_fpRecord);
        m_fpRecord = NULL;
    }

    return true;
}

int AudioManager::putData(void* pBuf, int bytes) {
    if (m_fpRecord != NULL) {
        return fwrite(pBuf, 1, bytes, m_fpRecord);
    }

    return -1;
}

bool AudioManager::startTracking(string file, int sampleRate, int numChannels) {
    LOGI("start tracking, sample rate is %d, num channels is %d\n", sampleRate, numChannels);

    if (m_fpTrack != NULL) {
        LOGW("close unclosed track file descriptor\n");
        fclose(m_fpTrack);
    }

    m_fpTrack = fopen(file.c_str(), "rb");

    if (m_fpTrack == NULL) {
        LOGE("open track file %s failed, error: %s\n", file.c_str(), strerror(errno));
        return false;
    } else {
        LOGI("open track file %s succeed\n", file.c_str());
        return true;
    }
}

bool AudioManager::stopTracking() {
    LOGI("stop tracking\n");

    if (m_fpTrack != NULL) {
        fclose(m_fpTrack);
        m_fpTrack = NULL;
    }

    return true;
}

int AudioManager::getData(void* pBuf, int bytes) {
    if (m_fpTrack != NULL) {
        return fread(pBuf, 1, bytes, m_fpTrack);
    }

    return -1;
}
