/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.gangzhang.videotest1;


import android.animation.TimeAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.SurfaceTexture;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.widget.TextView;
import com.example.gangzhang.videotest1.MediaCodecWrapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * This activity uses a {@link TextureView} to render the frames of a video decoded using
 * {@link MediaCodec} API.
 */
public class MainActivity extends Activity {

    private TextureView mPlaybackView;
    static private int openfileDialogId = 0;

    // A utility that wraps up the underlying input and output buffer processing operations
    // into an east to use API.
    private MediaCodecWrapper mCodecWrapper;
    private String filepath;
    private Surface mSurface;
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPlaybackView = (TextureView) findViewById(R.id.PlaybackView);
        mPlaybackView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {

            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                if (mCodecWrapper != null) {
                    mCodecWrapper.stopAndRelease();
                    mCodecWrapper = null;
                }
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

            }
        });
       mSurface = new Surface( mPlaybackView.getSurfaceTexture());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
//        if (mCodecWrapper != null ) {
//            mCodecWrapper.stopAndRelease();
//        }
//        mCodecWrapper = null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_play) {
            startPlayback();
        }
        return true;
    }

    private void startPlayback() {
        showDialog(openfileDialogId);
//        if (mCodecWrapper != null) {
//            mCodecWrapper.stopAndRelease();
//        }

    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

    }

    @Override
    protected Dialog onCreateDialog(int id) {
            if(id == openfileDialogId){
            Map<String, Integer> images = new HashMap<String, Integer>();
//            // 下面几句设置各文件类型的图标， 需要你先把图标添加到资源文件夹
            images.put(OpenFileDialog.sRoot, R.mipmap.ic_audio_circle_pressed_o);   // 根目录图标
            images.put(OpenFileDialog.sParent, R.mipmap.ic_cone_o);    //返回上一层的图标
            images.put(OpenFileDialog.sFolder, R.mipmap.ic_crop_circle_pressed);   //文件夹图标
            images.put("wav", R.mipmap.ic_down);   //wav文件图标
            images.put(OpenFileDialog.sEmpty, R.mipmap.ic_launcher);
            Dialog dialog = OpenFileDialog.createDialog(id, this, "打开文件", new CallbackBundle() {
                        @Override
                        public void callback(Bundle bundle) {
                            filepath = bundle.getString("path");
                            setTitle(filepath); // 把文件路径显示在标题上
                            if(mCodecWrapper!=null)
                                mCodecWrapper.stopAndRelease();

                            mCodecWrapper = MediaCodecWrapper.CreateCodecWrapper(filepath, mSurface);
                            if(mCodecWrapper != null) {
                                mCodecWrapper.Start();
                            }
                        }
                    },
                    ".mp4;",
                    images);
            return dialog;
        }
        return null;
    }
}
