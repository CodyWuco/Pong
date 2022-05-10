package com.gamecodeschool.pong;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.util.Log;

import java.io.IOException;

// All these are for playing sounds
class Sounds {
    private SoundPool mSP;
    private int mBeepID = -1;
    private int mBoopID = -1;
    private int mBopID = -1;
    private int mMissID = -1;

    Sounds(Context context){
        PrepareSoundPool();
        OpenSoundFile(context);
    }

    void PlaySoundBeep() { mSP.play(mBeepID, 1, 1, 0, 0, 1); }
    void PlaySoundBoop() { mSP.play(mBoopID, 1, 1, 0, 0, 1); }
    void PlaySoundMiss() { mSP.play(mMissID, 1, 1, 0, 0, 1); }
    void PlaySoundBop () { mSP.play(mBopID,  1, 1, 0, 0, 1); }

    void PrepareSoundPool(){
        // Prepares sound pool Depending upon the version of Android
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            mSP = new SoundPool.Builder()
                    .setMaxStreams(5)
                    .setAudioAttributes(audioAttributes)
                    .build();
        } else {
            mSP = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        }
    }

    // Open each of the sound files and load them in to Ram ready to play
    void OpenSoundFile(Context context){
        mBeepID = OpenDescriptor(context,"beep.ogg");
        mBoopID = OpenDescriptor(context,"boop.ogg");
        mMissID = OpenDescriptor(context,"miss.ogg");
        mBopID = OpenDescriptor(context, "bop.ogg" );
    }

    // The try-catch blocks handle when this fails
    int OpenDescriptor(Context context, String string){
        try{
            AssetManager assetManager = context.getAssets();
            AssetFileDescriptor descriptor;

            descriptor = assetManager.openFd( string);
            return mSP.load(descriptor, 0);


        }catch(IOException e){
            Log.e("error", string +"failed to load sound files");
            // returns sound's default state
            return -1;
        }

    }
}
