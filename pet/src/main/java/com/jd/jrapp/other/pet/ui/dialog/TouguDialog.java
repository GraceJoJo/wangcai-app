package com.jd.jrapp.other.pet.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.idst.util.NlsClient;
import com.alibaba.idst.util.SpeechRecognizer;
import com.alibaba.idst.util.SpeechRecognizerCallback;
import com.google.gson.reflect.TypeToken;
import com.jd.jrapp.other.pet.R;
import com.jd.jrapp.other.pet.ui.dialog.bean.AutoPollAdapter;
import com.jd.jrapp.other.pet.ui.dialog.bean.CommitInfo;
import com.jd.jrapp.other.pet.ui.view.AutoPollRecyclerView;
import com.jd.jrapp.other.pet.ui.view.LineWaveVoiceView;
import com.jd.jrapp.other.pet.ui.view.RecordAudioView;
import com.jd.jrapp.other.pet.utils.AppManager;
import com.jd.jrapp.other.pet.utils.DisplayUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import static android.media.AudioRecord.STATE_UNINITIALIZED;

/**
 * Author: chenghuan15
 * Date: 2020/8/29
 * Time: 3:33 PM
 */

public class TouguDialog extends Dialog implements SpeechRecognizerCallback, View.OnClickListener {
    private static final String TAG = "TouguDialog";
    protected static final int DEFAULT_MIN_TIME_UPDATE_TIME = 1000;
    public static final long DEFAULT_MAX_RECORD_TIME = 600000;
    public static final long DEFAULT_MIN_RECORD_TIME = 2000;

    private Context mContext;
    private int width;
    private int height;
    private NlsClient client;
    public SpeechRecognizer speechRecognizer;
    private LinearSmoothScroller linearSmoothScroller;
    private LineWaveVoiceView mHorVoiceView;
    private RelativeLayout rl_video;
    private AutoPollRecyclerView autoPollRecyclerView;
    private RecordAudioView recordAudioView;
    private TextView tvRecordTips;
    private RecyclerView lv;
    private RecordTask recordTask;
    private String result;
    private Timer timer;
    private TimerTask timerTask;
    private Handler mainHandler;
    private long recordTotalTime;
    private String[] recordStatusDescription = new String[]{"按住录音", "上划取消"};
    private long maxRecordTime = DEFAULT_MAX_RECORD_TIME;
    private long minRecordTime = DEFAULT_MIN_RECORD_TIME;
    private String audioFileName;
    private boolean isCancel;
    private CommitInfo commitInfo;
    private AutoPollAdapter autoPollAdapter;
    private int currentPosition = 0;
    private LinearLayoutManager linearLayoutManager;
    private List<CommitInfo.CommitsBean> commitsBeans;
    private boolean isFirstInto = true;

    public TouguDialog(Context context) {
        super(context, R.style.loadDialog);
        this.mContext = context;
        client = AppManager.client;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        commitsBeans = new ArrayList<>();
        commitInfo = getJsonData("commits.json");
        commitsBeans = commitInfo.getCommits();
        width = (int) DisplayUtil.getScreenWidth(mContext);
        height = (int) DisplayUtil.getScreenHeight(mContext);
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.layout_community_dialog, null);
        if (!DisplayUtil.checkDeviceHasNavigationBar(mContext)) {
            contentView.setPadding(0, 0, 0, DisplayUtil.getNavigationBarHeight(mContext));
        } else {
            contentView.setPadding(0, 0, 0, 0);
        }

        initAutoRecycleView(contentView);
        mainHandler = new Handler();
        TextView tv_close = contentView.findViewById(R.id.tv_close);
        tvRecordTips = contentView.findViewById(R.id.record_tips);
        rl_video = contentView.findViewById(R.id.rl_video);
        recordAudioView = contentView.findViewById(R.id.iv_audio);
        audioInit();
        ImageView iv_clear = contentView.findViewById(R.id.iv_clear);
        mHorVoiceView = contentView.findViewById(R.id.horvoiceview);

        lv = contentView.findViewById(R.id.lv);
        LinearLayoutManager manager = new LinearLayoutManager(mContext);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        lv.setLayoutManager(manager);

        iv_clear.setOnClickListener(this);
        tv_close.setOnClickListener(this);

        setCanceledOnTouchOutside(true);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(contentView);
        // 设置window属性
        freshDialog(width, height);
    }

    private void freshDialog(int width, int height) {
        // 设置window属性
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = width;
        lp.height = height;
        lp.gravity = Gravity.BOTTOM;
        getWindow().setAttributes(lp);
    }

    private void initAutoRecycleView(View view) {
        autoPollRecyclerView = view.findViewById(R.id.autoPollRecyclerView);
        linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        autoPollRecyclerView.setLayoutManager(linearLayoutManager);
        linearSmoothScroller = new LinearSmoothScroller(mContext) {
            @Override
            protected int getVerticalSnapPreference() {
                return SNAP_TO_END;
            }

            @Override
            protected int getHorizontalSnapPreference() {
                return SNAP_TO_START;
            }

            @Override
            protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                return 5f / (displayMetrics.density);
            }
        };
        autoPollRecyclerView.setAutoTaskCallback(new AutoPollRecyclerView.AutoTaskLister() {
            @Override
            public void scrollCallback() {
                freshBarrage(currentPosition);
            }
        });
        autoPollAdapter = new AutoPollAdapter(mContext, commitsBeans);
        autoPollRecyclerView.start(false);
        autoPollRecyclerView.setAdapter(autoPollAdapter);
    }

    private void freshBarrage(int position) {
        linearSmoothScroller.setTargetPosition(position);
        linearLayoutManager.startSmoothScroll(linearSmoothScroller);
//            autoPollRecyclerView.scrollBy(0, DisplayUtil.dip2px(mContext, 30));
        if (position == commitsBeans.size() - 1) {
            currentPosition = 0;
        } else {
            currentPosition = (currentPosition + 1) % commitsBeans.size();
        }

    }

    public void audioInit() {
        recordAudioView.setRecordAudioListener(new RecordAudioView.IRecordAudioListener() {
            @Override
            public boolean onRecordPrepare() {
                return true;
            }

            @Override
            public String onRecordStart() {
                autoPollRecyclerView.stop();//停止弹幕轮播
                isCancel = false;
                recordTotalTime = 0;
                initTimer();
                timer.schedule(timerTask, 0, DEFAULT_MIN_TIME_UPDATE_TIME);
                audioFileName = mContext.getExternalCacheDir() + File.separator + createAudioName();
                mHorVoiceView.startRecord();
                startRecognizer();
                return audioFileName;
            }

            @Override
            public boolean onRecordStop() {
                if (recordTotalTime >= minRecordTime) {
                    timer.cancel();
                    stopRecognizer();
                } else {
                    onRecordCancel();
                    stopRecognizer();
                }
                return false;
            }

            @Override
            public boolean onRecordCancel() {
                if (timer != null) {
                    timer.cancel();
                }
                updateCancelUi();
                isCancel = true;
                return false;
            }

            /**
             * 上划取消
             */
            @Override
            public void onSlideTop() {
                isCancel = true;
                mHorVoiceView.setVisibility(View.INVISIBLE);
                tvRecordTips.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFingerPress() {
                mHorVoiceView.setVisibility(View.VISIBLE);
                tvRecordTips.setVisibility(View.VISIBLE);
                tvRecordTips.setText(recordStatusDescription[1]);
            }
        });
    }

    public String createAudioName() {
        long time = System.currentTimeMillis();
        String fileName = UUID.randomUUID().toString() + time + ".amr";
        return fileName;
    }

    /**
     * 初始化计时器用来更新倒计时
     */
    private void initTimer() {
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        //每隔1000毫秒更新一次ui
                        recordTotalTime += 1000;
                        updateTimerUI();
                    }
                });
            }
        };
    }

    private void updateCancelUi() {
        mHorVoiceView.setVisibility(View.INVISIBLE);
        tvRecordTips.setVisibility(View.VISIBLE);
        tvRecordTips.setText(recordStatusDescription[0]);
        mHorVoiceView.stopRecord();
    }

    private void updateTimerUI() {
        if (recordTotalTime >= maxRecordTime) {
            recordAudioView.invokeStop();
        } else {
            String string = String.format(" 倒计时 %s ", AppManager.formatRecordTime(recordTotalTime, maxRecordTime));
            mHorVoiceView.setText(string);
        }
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.tv_close) {
            if (autoPollRecyclerView != null) {
                autoPollRecyclerView.stop();
            }
            dismiss();
        }
    }

    /**
     * 启动录音和识别
     */
    public void startRecognizer() {
        if (TextUtils.isEmpty(AppManager.accessToken.getToken())) {
            return;
        }
        // 第二步，新建识别回调类

        // 第三步，创建识别request
        speechRecognizer = client.createRecognizerRequest(this);
        // 第四步，设置相关参数
        // Token有有效期，请使用https://help.aliyun.com/document_detail/72153.html 动态生成token
        speechRecognizer.setToken(AppManager.accessToken.getToken());
        // 请使用阿里云语音服务管控台(https://nls-portal.console.aliyun.com/)生成您的appkey
        speechRecognizer.setAppkey(AppManager.NLS_App_KEY);
        // 以下为设置各种识别参数，请按需选择，更多参数请见文档
        // 开启ITN
        speechRecognizer.enableInverseTextNormalization(true);
        // 开启标点
        speechRecognizer.enablePunctuationPrediction(false);
        // 不返回中间结果
        speechRecognizer.enableIntermediateResult(false);
        // 设置打开服务端VAD
        speechRecognizer.enableVoiceDetection(true);
        speechRecognizer.setMaxStartSilence(3000);
        speechRecognizer.setMaxEndSilence(600);
        // 设置定制模型和热词
        // speechRecognizer.setCustomizationId("yourCustomizationId");
        // speechRecognizer.setVocabularyId("yourVocabularyId");
        speechRecognizer.start();

        //启动录音线程
        recordTask = new RecordTask(mContext, speechRecognizer);
        recordTask.execute();
    }

    /**
     * 停止录音和识别
     */
    public void stopRecognizer() {
        // 停止录音
        Log.i(TAG, "Stoping recognizer...");
        if (autoPollRecyclerView != null) {
            autoPollRecyclerView.start(true);
        }
        updateCancelUi();
        if (recordTask == null) {
            return;
        }
        recordTask.stop();
        speechRecognizer.stop();
    }

    // 语音识别回调类，用户在这里得到语音识别结果，加入您自己的业务处理逻辑
    // 注意不要在回调方法里执行耗时操作
    @Override
    public void onRecognizedStarted(String msg, int code) {
        Log.d(TAG, "OnRecognizedStarted " + msg + ": " + String.valueOf(code));
    }

    // 请求失败
    @Override
    public void onTaskFailed(String msg, int code) {
        Log.d(TAG, "OnTaskFailed: " + msg + ": " + String.valueOf(code));
        recordTask.stop();
        speechRecognizer.stop();
    }

    // 识别返回中间结果，只有enableIntermediateResult(true)时才会回调
    @Override
    public void onRecognizedResultChanged(final String msg, int code) {
        Log.d(TAG, "OnRecognizedResultChanged " + msg + ": " + String.valueOf(code));
    }

    // 第七步，识别结束，得到最终完整结果
    @Override
    public void onRecognizedCompleted(final String msg, int code) {
        Log.d(TAG,"msg---->"+msg);
        recordTask.stop();
        if (isCancel) {
            return;
        }
        Message message = new Message();
        message.what = 100;
        message.obj = msg;
        handler.sendMessage(message);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(final Message msg) {
            super.handleMessage(msg);
            final String fullResult = (String) msg.obj;
            if (msg.what == 100) {
                if (!TextUtils.isEmpty((String) msg.obj)) {
                    JSONObject jsonObject = JSONObject.parseObject(fullResult);
                    if (jsonObject.containsKey("payload")) {
                        result = jsonObject.getJSONObject("payload").getString("result");
//                        result="hello";
                        Log.d(TAG,"识别结果：---->"+result);
                        if (!TextUtils.isEmpty(result)) {
                            int indexPosition = (currentPosition + 3) % commitsBeans.size();
                            commitsBeans.add(indexPosition, new CommitInfo.CommitsBean(true, result));
                            autoPollAdapter.notifyDataSetChanged();
                            updateCancelUi();
                        }
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                autoPollRecyclerView.start(false);
                            }
                        }, 200);
                    }
                }
            }

        }
    };

    // 请求结束，关闭连接
    @Override
    public void onChannelClosed(String msg, int code) {

        Log.d(TAG, "OnChannelClosed " + msg + ": " + String.valueOf(code));
    }

    static class RecordTask extends AsyncTask<Void, Integer, Void> {
        final static int SAMPLE_RATE = 16000;
        final static int SAMPLES_PER_FRAME = 640;
        private Context mContext;
        private boolean sending;
        private SpeechRecognizer speechRecognizer;

        public RecordTask(Context mContext, SpeechRecognizer speechRecognizer) {
            this.mContext = mContext;
            this.speechRecognizer = speechRecognizer;
        }

        public void stop() {
            sending = false;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Log.d(TAG, "Init audio recorder");
            int bufferSizeInBytes = AudioRecord.getMinBufferSize(SAMPLE_RATE,
                    AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
            AudioRecord mAudioRecorder = new AudioRecord(MediaRecorder.AudioSource.DEFAULT, SAMPLE_RATE,
                    AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSizeInBytes * 2);

            if (mAudioRecorder == null || mAudioRecorder.getState() == STATE_UNINITIALIZED) {
                throw new IllegalStateException("Failed to initialize AudioRecord!");
            }
            mAudioRecorder.startRecording();

            ByteBuffer buf = ByteBuffer.allocateDirect(SAMPLES_PER_FRAME);
            sending = true;
            while (sending) {
                buf.clear();
                // 采集语音
                int readBytes = mAudioRecorder.read(buf, SAMPLES_PER_FRAME);
                byte[] bytes = new byte[SAMPLES_PER_FRAME];
                buf.get(bytes, 0, SAMPLES_PER_FRAME);
                if (readBytes > 0 && sending) {
                    // 第六步，发送语音数据到识别服务
                    int code = speechRecognizer.sendAudio(bytes, bytes.length);
                    if (code < 0) {
                        Log.i(TAG, "Failed to send audio!");
                        speechRecognizer.stop();
                        break;
                    }
                    Log.d(TAG, "Send audio data length: " + bytes.length);
                }
                buf.position(readBytes);
                buf.flip();
            }
            speechRecognizer.stop();
            mAudioRecorder.stop();
            return null;
        }
    }

    public CommitInfo getJsonData(String fileName) {
        InputStream in = null;
        try {
            in = getContext().getResources().getAssets().open(fileName);
            // 获取文件的字节数
            int lenght = in.available();
            // 创建byte数组
            byte[] buffer = new byte[lenght];
            // 将文件中的数据读到byte数组中
            in.read(buffer);
            String countryJson = new String(buffer, "utf-8");
            CommitInfo moneyManagementData = (CommitInfo) AppManager.fromJson(countryJson, new TypeToken<CommitInfo>() {
            }.getType());
            return moneyManagementData;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }


}
