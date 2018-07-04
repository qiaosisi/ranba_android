package com.example.nimei1.ranba.jni;

/**
 * Created by qy on 2018/10/11.
 * desc
 */

public class AudioJniUtils {


    static {
        System.loadLibrary("native-lib");
    }
    public static native byte[] audioMix(byte[] sourceA,byte[] sourceB,byte[] dst,float firstVol , float secondVol);

    public static native String putString(String info);


}
