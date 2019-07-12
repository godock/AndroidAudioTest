#pragma once

#include <string>

using std::string;

class AudioManager {
public:
    static AudioManager* getInstance();

    bool startRecording(string file, int sampleRate, int numChannels);
    bool stopRecording();
    int putData(void* pBuf, int bytes);

    bool startTracking(string file, int sampleRate, int numChannels);
    bool stopTracking();
    int getData(void* pBuf, int bytes);

private:
    AudioManager();
    ~AudioManager();

private:
    FILE* m_fpRecord;
    FILE* m_fpTrack;
};
