package com.jd.jrapp.other.pet.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.idst.util.NlsClient;
import com.alibaba.idst.util.SpeechRecognizer;
import com.alibaba.idst.util.SpeechRecognizerCallback;
import com.jd.jrapp.other.pet.R;
import com.jd.jrapp.other.pet.ui.BaseRecycler.BaseAdapterHelper;
import com.jd.jrapp.other.pet.ui.BaseRecycler.RecycleAdapter;
import com.jd.jrapp.other.pet.ui.dialog.bean.TouguInfo;
import com.jd.jrapp.other.pet.ui.view.LineWaveVoiceView;
import com.jd.jrapp.other.pet.ui.view.RecordAudioView;
import com.jd.jrapp.other.pet.utils.AppManager;
import com.jd.jrapp.other.pet.utils.DisplayUtil;

import java.io.File;
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
    private List<TouguInfo> infoList;
    private NlsClient client;
    public SpeechRecognizer speechRecognizer;
    private RecordAudioView recordAudioView;
    private LineWaveVoiceView mHorVoiceView;
    private TextView tvRecordTips;
    private RecyclerView lv;
    private RecordTask recordTask;
    private String result;
    private Timer timer;
    private TimerTask timerTask;
    private Handler mainHandler;
    private long recordTotalTime;
    private String[] recordStatusDescription = new String[]{"????????????", "????????????"};
    private String[] keyWords = new String[]{"??????", "??????", "??????", "??????", "??????", "?????????", "????????????"};
    private String[] words = null;
    private long maxRecordTime = DEFAULT_MAX_RECORD_TIME;
    private long minRecordTime = DEFAULT_MIN_RECORD_TIME;
    private String audioFileName;
    private boolean isCancel;

    public TouguDialog(Context context) {
        super(context, R.style.loadDialog);
        this.mContext = context;
        client = AppManager.client;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        words = new String[]{mContext.getString(R.string.licai), mContext.getString(R.string.licai),
                mContext.getString(R.string.jijin), mContext.getString(R.string.baoxian), "????????????", "????????????", "????????????"};
        width = (int) DisplayUtil.getScreenWidth(mContext);
        height = (int) DisplayUtil.getScreenHeight(mContext);
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.layout_tougu_dialog, null);
        if (!DisplayUtil.checkDeviceHasNavigationBar(mContext)) {
            contentView.setPadding(0, 0, 0, DisplayUtil.getNavigationBarHeight(mContext));
        } else {
            contentView.setPadding(0, 0, 0, 0);
        }
        mainHandler = new Handler();
        TextView tv_close = contentView.findViewById(R.id.tv_close);
        tvRecordTips = contentView.findViewById(R.id.record_tips);
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

//        recordAudioView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                switch (motionEvent.getAction()) {
//                    case MotionEvent.ACTION_DOWN: {
//                        scrollToBottom();
//                        startRecognizer();
//                        return true;
//                    }
//                    case MotionEvent.ACTION_MOVE: {
//                        break;
//                    }
//                    case MotionEvent.ACTION_CANCEL:
//                    case MotionEvent.ACTION_UP: {
//                        stopRecognizer(view);
//                        return true;
//                    }
//                }
//                return false;
//            }
//        });
        setCanceledOnTouchOutside(true);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(contentView);
        infoList = new ArrayList<>();
//        initData();
        lv.setAdapter(myAdapter);
        myAdapter.addAll(infoList);


        // ??????window??????
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = width;
        lp.height = height;
        lp.gravity = Gravity.BOTTOM;
        getWindow().setAttributes(lp);
    }

    public void audioInit() {
        recordAudioView.setRecordAudioListener(new RecordAudioView.IRecordAudioListener() {
            @Override
            public boolean onRecordPrepare() {
                return true;
            }

            @Override
            public String onRecordStart() {
                isCancel = false;
                recordTotalTime = 0;
                initTimer();
                timer.schedule(timerTask, 0, DEFAULT_MIN_TIME_UPDATE_TIME);
                audioFileName = mContext.getExternalCacheDir() + File.separator + createAudioName();
                mHorVoiceView.startRecord();
                scrollToBottom();
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
             * ????????????
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
     * ???????????????????????????????????????
     */
    private void initTimer() {
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        //??????1000??????????????????ui
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
            String string = String.format(" ????????? %s ", AppManager.formatRecordTime(recordTotalTime, maxRecordTime));
            mHorVoiceView.setText(string);
        }
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.tv_close) {
            dismiss();
        } else if (viewId == R.id.iv_clear) {
            infoList.clear();
            myAdapter.clear();
        }
    }

    private void initData() {
        infoList.add(new TouguInfo("??????????????????????????????????????????????????????", 0, ""));
        infoList.add(new TouguInfo("???????????????????????????????????????????????????50%??????????????????????????????????????????50%??????????????????" +
                "???????????????????????????????????????????????????????????????????????????????????????????????????4%?????????", 1, ""));
    }

    RecycleAdapter<TouguInfo> myAdapter = new RecycleAdapter<TouguInfo>(mContext, R.layout.item_tougu_layout, infoList) {
        @Override
        protected void convert(BaseAdapterHelper helper, TouguInfo item, int position) {
            TextView tv_message = helper.getTextView(R.id.tv_message);
            TextView tv_message_ai = helper.getTextView(R.id.tv_message_ai);
            LinearLayout ll_left = helper.getView(R.id.ll_left);
            LinearLayout ll_right = helper.getView(R.id.ll_right);
            if (item.getMessageType() == 1) {
                ll_left.setVisibility(View.VISIBLE);
                ll_right.setVisibility(View.GONE);
                tv_message_ai.setText(item.getMessage());
                if (item.getMessage().contains("??????????????????")) {
                    setRedText(item.getMessage(), tv_message_ai);
                }
            } else {
                ll_left.setVisibility(View.GONE);
                ll_right.setVisibility(View.VISIBLE);
                tv_message.setText(item.getMessage());
            }

        }
    };

    /**
     * ?????????????????????
     */
    public void startRecognizer() {
        if (TextUtils.isEmpty(AppManager.accessToken.getToken())) {
            return;
        }
        // ?????????????????????????????????

        // ????????????????????????request
        speechRecognizer = client.createRecognizerRequest(this);
        // ??????????????????????????????
        // Token????????????????????????https://help.aliyun.com/document_detail/72153.html ????????????token
        speechRecognizer.setToken(AppManager.accessToken.getToken());
        // ???????????????????????????????????????(https://nls-portal.console.aliyun.com/)????????????appkey
        speechRecognizer.setAppkey(AppManager.NLS_App_KEY);
        // ??????????????????????????????????????????????????????????????????????????????
        // ??????ITN
        speechRecognizer.enableInverseTextNormalization(true);
        // ????????????
        speechRecognizer.enablePunctuationPrediction(false);
        // ?????????????????????
        speechRecognizer.enableIntermediateResult(false);
        // ?????????????????????VAD
        speechRecognizer.enableVoiceDetection(true);
        speechRecognizer.setMaxStartSilence(3000);
        speechRecognizer.setMaxEndSilence(600);
        // ???????????????????????????
        // speechRecognizer.setCustomizationId("yourCustomizationId");
        // speechRecognizer.setVocabularyId("yourVocabularyId");
        speechRecognizer.start();

        //??????????????????
        recordTask = new RecordTask(mContext, speechRecognizer);
        recordTask.execute();
    }

    /**
     * ?????????????????????
     */
    public void stopRecognizer() {
        // ????????????
        Log.i(TAG, "Stoping recognizer...");
        if (recordTask == null) {
            return;
        }
        recordTask.stop();
        speechRecognizer.stop();
    }

    // ??????????????????????????????????????????????????????????????????????????????????????????????????????
    // ????????????????????????????????????????????????
    @Override
    public void onRecognizedStarted(String msg, int code) {
        Log.d(TAG, "OnRecognizedStarted " + msg + ": " + String.valueOf(code));
    }

    // ????????????
    @Override
    public void onTaskFailed(String msg, int code) {
        Log.d(TAG, "OnTaskFailed: " + msg + ": " + String.valueOf(code));
        recordTask.stop();
        speechRecognizer.stop();
    }

    // ?????????????????????????????????enableIntermediateResult(true)???????????????
    @Override
    public void onRecognizedResultChanged(final String msg, int code) {
        Log.d(TAG, "OnRecognizedResultChanged " + msg + ": " + String.valueOf(code));
    }

    // ???????????????????????????????????????????????????
    @Override
    public void onRecognizedCompleted(final String msg, int code) {
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
                        myAdapter.add(new TouguInfo(result, 0));
                        updateCancelUi();
                        scrollToBottom();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Message message = new Message();
                                for (int i = 0; i < keyWords.length; i++) {
                                    if (result.contains(keyWords[i])) {
                                        message.what = 102;
                                        message.obj = words[i];
                                        handler.sendMessage(message);
                                        return;
                                    }
                                }
                                String aiStr = AppManager.getServiceInfo(result);
                                message.what = 101;
                                message.obj = aiStr;
                                handler.sendMessage(message);
                            }
                        }).start();

                    }
                }
            } else if (msg.what == 101) {
                JSONObject jsonObject = JSONObject.parseObject(fullResult);
                if (jsonObject != null && jsonObject.containsKey("content")) {
                    String aiResult = jsonObject.getString("content");
                    myAdapter.add(new TouguInfo(aiResult, 1));
                    scrollToBottom();
                }

            } else if (msg.what == 102) {
                myAdapter.add(new TouguInfo(fullResult, 1));
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        scrollToBottom();
                    }
                }, 500);

            }

        }
    };

    private void scrollToBottom() {
        if (myAdapter != null && myAdapter.getItemCount() > 0) {
            lv.scrollToPosition(myAdapter.getItemCount() - 1);
        }
    }

    // ???????????????????????????
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
                // ????????????
                int readBytes = mAudioRecorder.read(buf, SAMPLES_PER_FRAME);
                byte[] bytes = new byte[SAMPLES_PER_FRAME];
                buf.get(bytes, 0, SAMPLES_PER_FRAME);
                if (readBytes > 0 && sending) {
                    // ?????????????????????????????????????????????
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

    protected void setRedText(String contentStr, TextView textView) {
        SpannableStringBuilder style = new SpannableStringBuilder();

        //????????????
        style.append(contentStr);
        //??????????????????????????????
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                ds.setUnderlineText(false);
            }
        };
        style.setSpan(clickableSpan, contentStr.length() - 7, contentStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(style);
        //????????????????????????
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(Color.parseColor("#EF4034"));
        style.setSpan(foregroundColorSpan, contentStr.length() - 7, contentStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        //?????????TextView
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setText(style);
    }


}
