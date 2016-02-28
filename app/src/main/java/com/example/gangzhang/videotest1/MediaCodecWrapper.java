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
import android.media.*;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Surface;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.Queue;

/**
 * Simplifies the MediaCodec interface by wrapping around the buffer processing operations.
 */
public class MediaCodecWrapper {

    // Handler to use for {@code OutputSampleListener} and {code OutputFormatChangedListener}
    // callbacks
    private Handler mHandler;


    // Callback when media output format changes.
    public interface OutputFormatChangedListener {
        void outputFormatChanged(MediaCodecWrapper sender, MediaFormat newFormat);
    }

    private OutputFormatChangedListener mOutputFormatChangedListener = null;

    /**
     * Callback for decodes frames. Observers can register a listener for optional stream
     * of decoded data
     */
    public interface OutputSampleListener {
        void outputSample(MediaCodecWrapper sender, MediaCodec.BufferInfo info, ByteBuffer buffer);
    }

    /**
     * The {@link MediaCodec} that is managed by this class.
     */
    private MediaCodec mDecoder;

    // References to the internal buffers managed by the codec. The codec
    // refers to these buffers by index, never by reference so it's up to us
    // to keep track of which buffer is which.
    private ByteBuffer[] mInputBuffers;
    private ByteBuffer[] mOutputBuffers;

    // Indices of the input buffers that are currently available for writing. We'll
    // consume these in the order they were dequeued from the codec.
    private Queue<Integer> mAvailableInputBuffers;

    // Indices of the output buffers that currently hold valid data, in the order
    // they were produced by the codec.
    private Queue<Integer> mAvailableOutputBuffers;

    // Information about each output buffer, by index. Each entry in this array
    // is valid if and only if its index is currently contained in mAvailableOutputBuffers.
    private MediaCodec.BufferInfo[] mOutputBufferInfo;

    // An (optional) stream that will receive decoded data.
    private OutputSampleListener mOutputSampleListener;

    private static FileInputStream inputStream;
    private static boolean has_find_frame_head ;
    private static boolean firstTime =true;
    private static File file;
    public  static boolean isEos;
    private static boolean isPlaying;
    private static int frameCnt;
    private static BufferedInputStream br;


    private TimeAnimator mTimeAnimator = new TimeAnimator();


    private  static int initFileIO(String path){
        file = new File(path);
        has_find_frame_head = false;
        try {
            inputStream = new FileInputStream(file);
        }catch (Exception e){
            e.printStackTrace();
            return -1;
        }
        isEos = false;
//        bytesCache = new byte[1024*100];
        frameCnt = 0;
        br = new BufferedInputStream(inputStream);
        return 0;
    }

    private MediaCodecWrapper(MediaCodec codec) {
        mDecoder = codec;
        codec.start();
        mInputBuffers = codec.getInputBuffers();
        mOutputBuffers = codec.getOutputBuffers();
        mOutputBufferInfo = new MediaCodec.BufferInfo[mOutputBuffers.length];
        mAvailableInputBuffers = new ArrayDeque<Integer>(mOutputBuffers.length);
        mAvailableOutputBuffers = new ArrayDeque<Integer>(mInputBuffers.length);
    }

    /**
     * Releases resources and ends the encoding/decoding session.
     */
    public void stopAndRelease() {
        Log.d("myapp","stopAndRelease");
        if(mDecoder!=null) {
            mDecoder.stop();
            mDecoder.release();
            mDecoder = null;
        }
        mHandler = null;
        isPlaying = false;
    }

    /**
     * Getter for the registered {@link OutputFormatChangedListener}
     */
    public OutputFormatChangedListener getOutputFormatChangedListener() {
        return mOutputFormatChangedListener;
    }

    /**
     *
     * @param outputFormatChangedListener the listener for callback.
     * @param handler message handler for posting the callback.
     */
    public void setOutputFormatChangedListener(final OutputFormatChangedListener
                                                       outputFormatChangedListener, Handler handler) {
        mOutputFormatChangedListener = outputFormatChangedListener;

        // Making sure we don't block ourselves due to a bad implementation of the callback by
        // using a handler provided by client.
        Looper looper;
        mHandler = handler;
        if (outputFormatChangedListener != null && mHandler == null) {
            if ((looper = Looper.myLooper()) != null) {
                mHandler = new Handler();
            } else {
                throw new IllegalArgumentException(
                        "Looper doesn't exist in the calling thread");
            }
        }
    }




    public static MediaCodecWrapper CreateCodecWrapper( String path,Surface surface){


        MediaCodecWrapper result = null;
        MediaCodec videoCodec = null;

        if(initFileIO(path)<0){
            return null;
        };

        MediaFormat mediaFormat = new MediaFormat();
        mediaFormat.setString(MediaFormat.KEY_MIME, "Video/AVC");
//            mediaFormat.setByteBuffer("csd-0", ByteBuffer.wrap(header_sps));
//            mediaFormat.setByteBuffer("csd-1", ByteBuffer.wrap(header_pps));
        mediaFormat.setInteger(MediaFormat.KEY_WIDTH, 1920);
        mediaFormat.setInteger(MediaFormat.KEY_HEIGHT, 1080);
        videoCodec = MediaCodec.createDecoderByType("video/avc");

        videoCodec.configure(mediaFormat, surface, null, 0);
        if (videoCodec != null) {
            result = new MediaCodecWrapper(videoCodec);
        }
        isPlaying = false;
        return result;
    }




    private boolean writeSample(final long presentationTimeUs,
                               int flags) {
        boolean result = false;
        boolean isEos = false;

        if (!mAvailableInputBuffers.isEmpty()) {
            int index = mAvailableInputBuffers.remove();
            ByteBuffer buffer = mInputBuffers[index];

            int size = nextFrame(buffer);
            if(mDecoder!=null)
            mDecoder.queueInputBuffer(index, 0, size, presentationTimeUs, flags);

            result = true;
        }
        return result;

    }



    private int nextFrame2(ByteBuffer buffer){
        int size = 0;
        byte[] b_array= new byte[1];
        int stage = 0;
        try {
            if(!has_find_frame_head) {
                while (true) {
                    if(br.read(b_array)==-1){
                        isEos=true;
                        return 0;
                    }
                    if (stage == 0 ) {
                        if(b_array[0] == 0) {
                            stage = 1;

                        }else{
                            stage=0;

                        }
                    } else if (stage == 1) {
                        if (b_array[0] == 0) {
                            stage = 2;

                        } else {
                            stage = 0;

                        }
                    } else if (stage == 2) {
                        if (b_array[0] == 1) {
                            break;
                        } else if (b_array[0] == 0) {
                            stage = 2;
                        } else {
                            stage = 0;
                        }
                    }
                }
            }
            size = 3;
            byte b = 0;
            buffer.put(b);
            b=0;
            buffer.put(b);
            b=1;
            buffer.put(b);
            has_find_frame_head = false;
            //has find start flag 001 or 0001 or 0000001
            while(true){
                if(br.read(b_array)==-1){
                    has_find_frame_head = true;
                    size -= 2;
                    isEos=true;
                    break;
                };
                buffer.put(b_array[0]);
                if(size >=2 && buffer.get(size)==1 && buffer.get(size - 1)==0 && buffer.get(size-2)==0) {
                    has_find_frame_head = true;
                    size -= 2;
                    break;
                }else {
                    size++;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
        if(size<=0)
            isEos=true;
        return size;

    }


    private int nextFrame(ByteBuffer buffer){
        int size = 0;
        byte[] b_array= new byte[1];
        int stage = 0;
        try {
            if(firstTime) {
                while (true) {
                    if(br.read(b_array)==-1){
                        break;
                    }
                    if (stage == 0 ) {
                        if(b_array[0] == 0) {
                            stage = 1;

                        }else{
                            stage=0;

                        }
                    } else if (stage == 1) {
                        if (b_array[0] == 0) {
                            stage = 2;

                        } else {
                            stage = 0;

                        }
                    } else if (stage == 2) {
                        if (b_array[0] == 1) {
                            break;
                        } else if (b_array[0] == 0) {
                            stage = 2;
                        } else {
                            stage = 0;
                        }
                    }
                }
                if(stage==2) {
                    firstTime = false;
                }else{
                    isEos=true;
                    return 0;
                }
            }

            size = 3;
            byte b = 0;
            buffer.put(b);
            b=0;
            buffer.put(b);
            b=1;
            buffer.put(b);
            //has find start flag 001 or 0001 or 0000001
            while(true){
                if(br.read(b_array)==-1){
                    isEos=true;
                    break;
                };
                buffer.put(b_array[0]);
                if(size >=2 && buffer.get(size)==1 && buffer.get(size - 1)==0 && buffer.get(size-2)==0) {
                    size -= 2;
                    break;
                }else {
                    size++;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            isEos=true;
            return 0;
        }
//        if(size<=0)
//            isEos=true;
        return size;

    }
    /**
     * Performs a peek() operation in the queue to extract media info for the buffer ready to be
     * released i.e. the head element of the queue.
     *
     * @param out_bufferInfo An output var to hold the buffer info.
     *
     * @return True, if the peek was successful.
     */
    public boolean peekSample(MediaCodec.BufferInfo out_bufferInfo) {
        // dequeue available buffers and synchronize our data structures with the codec.
        update();
        boolean result = false;
        if (!mAvailableOutputBuffers.isEmpty()) {
            int index = mAvailableOutputBuffers.peek();
            MediaCodec.BufferInfo info = mOutputBufferInfo[index];

            out_bufferInfo.set(
                    info.offset,
                    info.size,
                    info.presentationTimeUs,
                    info.flags);
            result = true;
        }
        return result;
    }

    /**
     * Processes, releases and optionally renders the output buffer available at the head of the
     * queue. All observers are notified with a callback. See {@link
     * OutputSampleListener#outputSample(MediaCodecWrapper, android.media.MediaCodec.BufferInfo,
     * java.nio.ByteBuffer)}
     *
     * @param render True, if the buffer is to be rendered on the {@link Surface} configured
     *
     */
    public void popSample(boolean render) {
        // dequeue available buffers and synchronize our data structures with the codec.
        update();
        if (!mAvailableOutputBuffers.isEmpty()) {
            int index = mAvailableOutputBuffers.remove();

            if (render && mOutputSampleListener != null) {
                ByteBuffer buffer = mOutputBuffers[index];
                MediaCodec.BufferInfo info = mOutputBufferInfo[index];
                mOutputSampleListener.outputSample(this, info, buffer);
            }
            ByteBuffer buffer = mOutputBuffers[index];
            MediaCodec.BufferInfo info = mOutputBufferInfo[index];
//            Log.d("myapp","info.offset="+info.offset);
//            Log.d("myapp","info.size="+info.size);
//            Log.d("myapp","info.presentationTimeUs="+info.presentationTimeUs);
//            Log.d("myapp","info.flags="+info.flags);
//            if(info.flags==1) {
//                for (int i = 0; i < 8; i++) {
//                    Log.d("myapp", "b=" + buffer.get(i));
//                }
//            }

            // releases the buffer back to the codec
            mDecoder.releaseOutputBuffer(index, render);
        }
    }

    /**
     * Synchronize this object's state with the internal state of the wrapped
     * MediaCodec.
     */
    private void update() {

        int index;
        // Get valid input buffers from the codec to fill later in the same order they were
        // made available by the codec.
        while ((index = mDecoder.dequeueInputBuffer(0)) != MediaCodec.INFO_TRY_AGAIN_LATER) {
            mAvailableInputBuffers.add(index);
        }


        // Likewise with output buffers. If the output buffers have changed, start using the
        // new set of output buffers. If the output format has changed, notify listeners.
        MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
        while ((index = mDecoder.dequeueOutputBuffer(info, 0)) !=  MediaCodec.INFO_TRY_AGAIN_LATER) {

            switch (index) {
                case MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED:
                    mOutputBuffers = mDecoder.getOutputBuffers();
                    mOutputBufferInfo = new MediaCodec.BufferInfo[mOutputBuffers.length];
                    mAvailableOutputBuffers.clear();
                    break;
                case MediaCodec.INFO_OUTPUT_FORMAT_CHANGED:
                    if (mOutputFormatChangedListener != null) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mOutputFormatChangedListener
                                        .outputFormatChanged(MediaCodecWrapper.this,
                                                mDecoder.getOutputFormat());

                            }
                        });
                    }
                    break;
                default:
                    // Making sure the index is valid before adding to output buffers. We've already
                    // handled INFO_TRY_AGAIN_LATER, INFO_OUTPUT_FORMAT_CHANGED &
                    // INFO_OUTPUT_BUFFERS_CHANGED i.e all the other possible return codes but
                    // asserting index value anyways for future-proofing the code.
                    if(index >= 0) {
                        mOutputBufferInfo[index] = info;
                        mAvailableOutputBuffers.add(index);
                    } else {
                        throw new IllegalStateException("Unknown status from dequeueOutputBuffer");
                    }
                    break;
            }

        }


    }

    public void Start(){

        try {
                // By using a {@link TimeAnimator}, we can sync our media rendering commands with
                // the system display frame rendering. The animator ticks as the {@link Choreographer}
                // recieves VSYNC events.
                mTimeAnimator.setTimeListener(new TimeAnimator.TimeListener() {
                    @Override
                    public void onTimeUpdate(final TimeAnimator animation,
                                             final long totalTime,
                                             final long deltaTime) {
                        if(isPlaying) {

                            if (!isEos) {
                                // Try to submit the sample to the codec and if successful advance the
                                // extractor to the next available sample to read.
                                boolean result = writeSample(frameCnt, 0);
                            }


                            // Examine the sample at the head of the queue to see if its ready to be
                            // rendered and is not zero sized End-of-Stream record.
                            MediaCodec.BufferInfo out_bufferInfo = new MediaCodec.BufferInfo();
                            peekSample(out_bufferInfo);


                            if (isEos) {
                                mTimeAnimator.end();
                                stopAndRelease();
                            } else if (out_bufferInfo.presentationTimeUs / 1000 < totalTime) {
                                // Pop the sample off the queue and send it to {@link Surface}
                                popSample(true);
                            }
                            frameCnt++;
                        }else{

                        }
                }

            });

            // We're all set. Kick off the animator to process buffers and render video frames as
            // they become available
            isPlaying = true;
            mTimeAnimator.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
