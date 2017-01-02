package com.binasphere.yuri;

import android.content.Intent;
import android.hardware.display.VirtualDisplay;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static int REQUEST_PROJECTION = 1;
    private MediaProjectionManager pm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        pm = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
        Intent intent = pm.createScreenCaptureIntent();
        startActivityForResult(intent, REQUEST_PROJECTION);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("Projection Request", resultCode + "");
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        MediaProjection projection = pm.getMediaProjection(resultCode, data);


        try {
            MediaCodec codec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_AVC);
            MediaFormat format = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, width, height);
            format.setInteger(MediaFormat.KEY_BIT_RATE, 125000);
            format.setInteger(MediaFormat.KEY_FRAME_RATE, 15);
            format.setInteger(MediaFormat.KEY_CAPTURE_RATE, 15);
            format.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
            format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 5);
            codec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);

//            MediaCodec codec = MediaCodec.createByCodecName(new MediaCodecList(MediaCodecList.REGULAR_CODECS).findEncoderForFormat(MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, width, height)));
            Surface surface = codec.createInputSurface();
            codec.start();
            VirtualDisplay display = projection.createVirtualDisplay("Yuri", width, height, displayMetrics.densityDpi, 0, surface, null, null);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
