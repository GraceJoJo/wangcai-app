package org.cocos2dx.lib;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.opengl.GLSurfaceView;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.ArrayMap;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.idst.util.NlsClient;
import com.alibaba.idst.util.SpeechRecognizer;
import com.alibaba.idst.util.SpeechRecognizerCallback;
import com.jd.jrapp.other.pet.R;
import com.jd.jrapp.other.pet.ui.dialog.PtDialog;
import com.jd.jrapp.other.pet.utils.AppManager;
import com.jd.jrapp.other.pet.utils.DisplayUtil;

import org.cocos2dx.javascript.JavaCocosBridge;
import org.cocos2dx.javascript.JavaCocosConstant;
import org.cocos2dx.javascript.SDKWrapper;
import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.util.Map;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;

import static android.media.AudioRecord.STATE_UNINITIALIZED;

/**
 * Created by yuguotao at 2020/9/9,6:27 PM
 */
public class Cocos2dxDialog extends Dialog  implements Cocos2dxHelperDialog.Cocos2dxHelperListener, SpeechRecognizerCallback {
    // ===========================================================
    // Constants
    // ===========================================================

    private final static String TAG = Cocos2dxDialog.class.getSimpleName();

    // ===========================================================
    // Fields
    // ===========================================================
//    private static Cocos2dxActivity sContext = null;

    protected RelativeLayout mFrameLayout = null;

    private Cocos2dxGLSurfaceView mGLSurfaceView = null;
    private int[] mGLContextAttrs = null;
//    private Cocos2dxHandler mHandler = null;
//    private Cocos2dxVideoHelper mVideoHelper = null;
    private Cocos2dxWebViewHelper mWebViewHelper = null;
    private boolean hasFocus = false;
    private Cocos2dxEditBox mEditBox = null;
    private boolean gainAudioFocus = false;
    private boolean paused = true;

    // DEBUG VIEW BEGIN
    private LinearLayout mLinearLayoutForDebugView;
    private TextView mFPSTextView;
    private TextView mJSBInvocationTextView;
    private TextView mGLOptModeTextView;
    private TextView mGameInfoTextView_0;
    private TextView mGameInfoTextView_1;
    private TextView mGameInfoTextView_2;
    private NlsClient client;
    public SpeechRecognizer speechRecognizer;

    private Handler mUiHandler;
    private Handler mainHandler = new Handler(Looper.getMainLooper());
    private String result;
    private RecordTask recordTask;
    private boolean isCancel;
    private String[] keyWords = new String[]{"知道", "请不要说话", "你好"};

    private TextView mPetInfoView;
    private ImageView mIvRecord, mIvClose, mIvShare;

    private static Map<String, String> actionKeywords = new ArrayMap(8);
    static {
        actionKeywords.put("哭", JavaCocosConstant.ACTION_CRY);
        actionKeywords.put("哭一个", JavaCocosConstant.ACTION_CRY);
        actionKeywords.put("打你", JavaCocosConstant.ACTION_CRY);
        actionKeywords.put("揍你", JavaCocosConstant.ACTION_CRY);
        actionKeywords.put("笑", JavaCocosConstant.ACTION_HAPPY);
        actionKeywords.put("笑一个", JavaCocosConstant.ACTION_HAPPY);
        actionKeywords.put("有趣", JavaCocosConstant.ACTION_HAPPY);
        actionKeywords.put("好看", JavaCocosConstant.ACTION_HAPPY);
        actionKeywords.put("漂亮", JavaCocosConstant.ACTION_HAPPY);
        actionKeywords.put("厉害", JavaCocosConstant.ACTION_HAPPY);
        actionKeywords.put("招呼", JavaCocosConstant.ACTION_GREET);
        actionKeywords.put("hello", JavaCocosConstant.ACTION_GREET);
        actionKeywords.put("hi", JavaCocosConstant.ACTION_GREET);
        actionKeywords.put("你好", JavaCocosConstant.ACTION_GREET);
        actionKeywords.put("您好", JavaCocosConstant.ACTION_GREET);
        actionKeywords.put("嗨", JavaCocosConstant.ACTION_GREET);
        actionKeywords.put("跳", JavaCocosConstant.ACTION_JUMP);
        actionKeywords.put("蹦", JavaCocosConstant.ACTION_JUMP);
        actionKeywords.put("后空翻", JavaCocosConstant.ACTION_JUMP);
        actionKeywords.put("功夫", JavaCocosConstant.ACTION_JUMP);
        actionKeywords.put("表演个节目", JavaCocosConstant.ACTION_JUMP);
        actionKeywords.put("翻个跟斗", JavaCocosConstant.ACTION_JUMP);
    }

    private static Map<String, String> licaiKeywords = new ArrayMap<>(8);
    static {
//        licaiKeywords.put("理财", "主人，根据您的情况，推荐您将资金的50%配置小金库，50%配置小金存。点击一键下单>");
//        licaiKeywords.put("投资", "主人，根据您的情况，推荐您将资金的50%配置小金库，50%配置小金存。点击一键下单>");
//        licaiKeywords.put("基金", "主人，推荐您将资金的40%配置广发沪深300指数基金，30%配置前海开源沪港深优势精选混合基金，30%的资金配置小金库。点击一键下单>");
//        licaiKeywords.put("保险", "主人，根据您的情况，推荐您将资金的70%配置小金保，30%配置小金存。点击一键下单>");
        licaiKeywords.put("名字", "我叫小京");
        licaiKeywords.put("你是谁", "我叫小京");
        licaiKeywords.put("你叫什么", "我叫小京");
        licaiKeywords.put("今天天气", "今天大兴区：浮沉转晴，西北风：4-5级，空气指数：良");
    }

    private JavaCocosBridge.CallJavaListener mStartRecordListener = new JavaCocosBridge.CallJavaListener() {
        @Override
        public void onCall(String param) {
            // TODO 开启录音
            Log.e(TAG, "开启录音。。。");
        }
    };

    private JavaCocosBridge.CallJavaListener mStopRecordListener = new JavaCocosBridge.CallJavaListener() {
        @Override
        public void onCall(String param) {
            // TODO 停止录音
            Log.e(TAG, "停止录音。。。");
            try{
                JSONObject obj = new JSONObject();
                obj.put("type", JavaCocosConstant.TYPE_DO_ACTION);
                obj.put("param", "sss");
                JavaCocosBridge.callCocos(obj.toString());
            }catch (Throwable t) {
                t.printStackTrace();
            }
        }
    };

    public Cocos2dxDialog(@NonNull Context context) {
        super(context, R.style.loadDialog);
        mUiHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                final String fullResult = (String) msg.obj;
                if (msg.what == 100) {
                    if (!TextUtils.isEmpty((String) msg.obj)) {
                        com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(fullResult);
                        if (jsonObject != null && jsonObject.containsKey("payload")) {
                            result = jsonObject.getJSONObject("payload").getString("result");
                            Log.v(TAG, "result---->" + result);
                            boolean actiomMatched = false;
                            for (String key :actionKeywords.keySet()){
                                if (result.contains(key)) {
                                    actiomMatched = true;
                                    final String action = actionKeywords.get(key);
                                    runOnGLThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try{
                                                JSONObject jsonObject = new JSONObject();
                                                jsonObject.put("action_type","normal");
                                                jsonObject.put("action", action);
                                                JSONObject obj = new JSONObject();
                                                obj.put("type", JavaCocosConstant.TYPE_DO_ACTION);
                                                obj.put("param", jsonObject);
                                                JavaCocosBridge.callCocos(obj.toString());
                                            }catch (Throwable t) {
                                                t.printStackTrace();
                                            }
                                        }
                                    });
                                    break;
                                }
                            }

                            boolean licaiMathed = false;
                            // 没有匹配默认动作，需要调用对话接口
                            if (!actiomMatched) {
                                for (String key: licaiKeywords.keySet()) {
                                    if (result.contains(key)) {
                                        licaiMathed = true;
                                        String txt = licaiKeywords.get(key);
                                        showLabelInfo(txt);
                                        break;
                                    }
                                }
                            }

                            // 最后调用对话接口
                            if (!actiomMatched && !licaiMathed) {
                                new Thread(){
                                    @Override
                                    public void run() {
                                        try{
                                            final String aiStr = AppManager.getServiceInfo(result);
                                            JSONObject json = new JSONObject(aiStr);
                                            final String content = json.optString("content");
                                            mUiHandler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    showLabelInfo(content);
                                                }
                                            });
                                        }catch (Throwable t) {
                                            t.printStackTrace();
                                        }
                                    }
                                }.start();
                            }
                        }
                    }
                }
            }
        };
    }

    private void showLabelInfo(String text) {
        if (mPetInfoView == null || TextUtils.isEmpty(text) || text.contains("未获取到相关信息")) {
            return;
        }
        mainHandler.removeCallbacksAndMessages(null);
        fadeInView(mPetInfoView);
        if (text.contains("点击一键下单")) {
            setRedText(text, mPetInfoView);
        } else {
            mPetInfoView.setText(text);
        }
        mainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                fadeOutView(mPetInfoView);
            }
        }, 3000);
    }

    // DEBUG VIEW END

    // ===========================================================
    // Inner class
    // ===========================================================

    public class Cocos2dxEGLConfigChooser implements GLSurfaceView.EGLConfigChooser {
        protected int[] configAttribs;
        public Cocos2dxEGLConfigChooser(int redSize, int greenSize, int blueSize, int alphaSize, int depthSize, int stencilSize)
        {
            configAttribs = new int[] {redSize, greenSize, blueSize, alphaSize, depthSize, stencilSize};
        }
        public Cocos2dxEGLConfigChooser(int[] attribs)
        {
            configAttribs = attribs;
        }

        private int findConfigAttrib(EGL10 egl, EGLDisplay display,
                                     EGLConfig config, int attribute, int defaultValue) {
            int[] value = new int[1];
            if (egl.eglGetConfigAttrib(display, config, attribute, value)) {
                return value[0];
            }
            return defaultValue;
        }

        class ConfigValue implements Comparable<ConfigValue> {
            public EGLConfig config = null;
            public int[] configAttribs = null;
            public int value = 0;
            private void calcValue() {
                // depth factor 29bit and [6,12)bit
                if (configAttribs[4] > 0) {
                    value = value + (1 << 29) + ((configAttribs[4]%64) << 6);
                }
                // stencil factor 28bit and [0, 6)bit
                if (configAttribs[5] > 0) {
                    value = value + (1 << 28) + ((configAttribs[5]%64));
                }
                // alpha factor 30bit and [24, 28)bit
                if (configAttribs[3] > 0) {
                    value = value + (1 << 30) + ((configAttribs[3]%16) << 24);
                }
                // green factor [20, 24)bit
                if (configAttribs[1] > 0) {
                    value = value + ((configAttribs[1]%16) << 20);
                }
                // blue factor [16, 20)bit
                if (configAttribs[2] > 0) {
                    value = value + ((configAttribs[2]%16) << 16);
                }
                // red factor [12, 16)bit
                if (configAttribs[0] > 0) {
                    value = value + ((configAttribs[0]%16) << 12);
                }
            }

            public ConfigValue(int[] attribs) {
                configAttribs = attribs;
                calcValue();
            }

            public ConfigValue(EGL10 egl, EGLDisplay display, EGLConfig config) {
                this.config = config;
                configAttribs = new int[6];
                configAttribs[0] = findConfigAttrib(egl, display, config, EGL10.EGL_RED_SIZE, 0);
                configAttribs[1] = findConfigAttrib(egl, display, config, EGL10.EGL_GREEN_SIZE, 0);
                configAttribs[2] = findConfigAttrib(egl, display, config, EGL10.EGL_BLUE_SIZE, 0);
                configAttribs[3] = findConfigAttrib(egl, display, config, EGL10.EGL_ALPHA_SIZE, 0);
                configAttribs[4] = findConfigAttrib(egl, display, config, EGL10.EGL_DEPTH_SIZE, 0);
                configAttribs[5] = findConfigAttrib(egl, display, config, EGL10.EGL_STENCIL_SIZE, 0);
                calcValue();
            }

            @Override
            public int compareTo(ConfigValue another) {
                if (value < another.value) {
                    return -1;
                } else if (value > another.value) {
                    return 1;
                } else {
                    return 0;
                }
            }

            @Override
            public String toString() {
                return "{ color: " + configAttribs[3] + configAttribs[2] + configAttribs[1] + configAttribs[0] +
                        "; depth: " + configAttribs[4] + "; stencil: " + configAttribs[5] + ";}";
            }
        }

        @Override
        public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display)
        {
            int[] EGLattribs = {
                    EGL10.EGL_RED_SIZE, configAttribs[0],
                    EGL10.EGL_GREEN_SIZE, configAttribs[1],
                    EGL10.EGL_BLUE_SIZE, configAttribs[2],
                    EGL10.EGL_ALPHA_SIZE, configAttribs[3],
                    EGL10.EGL_DEPTH_SIZE, configAttribs[4],
                    EGL10.EGL_STENCIL_SIZE,configAttribs[5],
                    EGL10.EGL_RENDERABLE_TYPE, 4, //EGL_OPENGL_ES2_BIT
                    EGL10.EGL_NONE
            };
            EGLConfig[] configs = new EGLConfig[1];
            int[] numConfigs = new int[1];
            boolean eglChooseResult = egl.eglChooseConfig(display, EGLattribs, configs, 1, numConfigs);
            if (eglChooseResult && numConfigs[0] > 0)
            {
                return configs[0];
            }

            // there's no config match the specific configAttribs, we should choose a closest one
            int[] EGLV2attribs = {
                    EGL10.EGL_RENDERABLE_TYPE, 4, //EGL_OPENGL_ES2_BIT
                    EGL10.EGL_NONE
            };
            eglChooseResult = egl.eglChooseConfig(display, EGLV2attribs, null, 0, numConfigs);
            if(eglChooseResult && numConfigs[0] > 0) {
                int num = numConfigs[0];
                ConfigValue[] cfgVals = new ConfigValue[num];

                // convert all config to ConfigValue
                configs = new EGLConfig[num];
                egl.eglChooseConfig(display, EGLV2attribs, configs, num, numConfigs);
                for (int i = 0; i < num; ++i) {
                    cfgVals[i] = new ConfigValue(egl, display, configs[i]);
                }

                ConfigValue e = new ConfigValue(configAttribs);
                // bin search
                int lo = 0;
                int hi = num;
                int mi;
                while (lo < hi - 1) {
                    mi = (lo + hi) / 2;
                    if (e.compareTo(cfgVals[mi]) < 0) {
                        hi = mi;
                    } else {
                        lo = mi;
                    }
                }
                if (lo != num - 1) {
                    lo = lo + 1;
                }
                Log.w("cocos2d", "Can't find EGLConfig match: " + e + ", instead of closest one:" + cfgVals[lo]);
                return cfgVals[lo].config;
            }

            Log.e(Context.DEVICE_POLICY_SERVICE, "Can not select an EGLConfig for rendering.");
            return null;
        }

    }

    // ===========================================================
    // Member methods
    // ===========================================================

    public Cocos2dxGLSurfaceView getGLSurfaceView(){
        return  mGLSurfaceView;
    }

    private void fadeInView(final View... views) {
        ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 1.0f);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                for (View view : views) {
                    view.setAlpha(value);
                }
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                super.onAnimationRepeat(animation);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                for (View view : views) {
                    view.setAlpha(0);
                    view.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationPause(Animator animation) {
                super.onAnimationPause(animation);
            }

            @Override
            public void onAnimationResume(Animator animation) {
                super.onAnimationResume(animation);
            }
        });
        animator.setDuration(200);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.start();
    }

    private void fadeOutView(final View... views) {
        ValueAnimator animator = ValueAnimator.ofFloat(1.0f, 0.0f);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                for (View view: views) {
                    view.setAlpha(value);
                }
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                for (View view : views) {
                    view.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                super.onAnimationRepeat(animation);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }

            @Override
            public void onAnimationPause(Animator animation) {
                super.onAnimationPause(animation);
            }

            @Override
            public void onAnimationResume(Animator animation) {
                super.onAnimationResume(animation);
            }
        });
        animator.start();
    }

    public void init() {
        ViewGroup.LayoutParams frameLayoutParams =
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
        mFrameLayout = new RelativeLayout(getContext());
        mFrameLayout.setLayoutParams(frameLayoutParams);

        Cocos2dxRenderer renderer = this.addSurfaceView();
        this.addDebugInfo(renderer);

        // Should create EditBox after adding SurfaceView, or EditBox will be hidden by SurfaceView.
//        mEditBox = new Cocos2dxEditBox(getContext(), mFrameLayout);

        // Set frame layout as the content view
        setContentView(mFrameLayout);
        mUiHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                fadeInView(mIvClose, mIvShare, mIvRecord);
            }
        }, 1000);
    }

    public void setKeepScreenOn(boolean value) {
        final boolean newValue = value;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mGLSurfaceView.setKeepScreenOn(newValue);
            }
        });
    }

    public void runOnUiThread(Runnable runnable) {
        mUiHandler.post(runnable);
    }

    public void setEnableAudioFocusGain(boolean value) {
        if(gainAudioFocus != value) {
            if(!paused) {
                if (value)
                    Cocos2dxAudioFocusManager.registerAudioFocusListener(getContext());
                else
                    Cocos2dxAudioFocusManager.unregisterAudioFocusListener(getContext());
            }
            gainAudioFocus = value;
        }
    }

    public Cocos2dxGLSurfaceView onCreateView() {

        Cocos2dxGLSurfaceView glSurfaceView = new Cocos2dxGLSurfaceView(getContext());
        // TestCpp should create stencil buffer
        glSurfaceView.setEGLConfigChooser(5, 6, 5, 0, 16, 8);
        SDKWrapper.getInstance().setGLSurfaceView(glSurfaceView, getContext());

        return glSurfaceView;
    }

    // ===========================================================
    // Override functions
    // ===========================================================

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        Log.d(TAG, "Cocos2dxActivity onCreate: " + this + ", savedInstanceState: " + savedInstanceState);
//        FixAndroidOSystem.fix(this);
        super.onCreate(savedInstanceState);
        AppManager.getInstance().getNLSToken();
        client = AppManager.client;
//        Utils.setActivity(this);

        // Workaround in https://stackoverflow.com/questions/16283079/re-launch-of-activity-on-home-button-but-only-the-first-time/16447508
//        if (!isTaskRoot()) {
//            // Android launched another instance of the root activity into an existing task
//            //  so just quietly finish and go away, dropping the user back into the activity
//            //  at the top of the stack (ie: the last state of this task)
//            finish();
//            Log.w(TAG, "[Workaround] Ignore the activity started from icon!");
//            return;
//        }

//        Utils.hideVirtualButton();

        Cocos2dxHelperDialog.registerBatteryLevelReceiver(getContext());

        onLoadNativeLibraries();

//        sContext = getContext();
//        this.mHandler = new Cocos2dxHandler(this);

        Cocos2dxHelperDialog.init(this);
        CanvasRenderingContext2DImpl.init(getContext());

        this.mGLContextAttrs = Cocos2dxActivity.getGLContextAttrs();
        this.init();

//        if (mVideoHelper == null) {
//            mVideoHelper = new Cocos2dxVideoHelper(this, mFrameLayout);
//        }

        if(mWebViewHelper == null){
            mWebViewHelper = new Cocos2dxWebViewHelper(mFrameLayout);
        }

        Window window = this.getWindow();
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = DisplayUtil.getScreenWidth(getContext());
        lp.height = DisplayUtil.dip2px(getContext(), getViewHeight());
        lp.gravity = Gravity.BOTTOM;
        getWindow().setAttributes(lp);
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                Log.e(TAG, "onDismiss");
                JavaCocosBridge.unRegistCallJavaListener(JavaCocosConstant.TYPE_START_RECORD, mStartRecordListener);
                JavaCocosBridge.unRegistCallJavaListener(JavaCocosConstant.TYPE_STOP_RECORD, mStopRecordListener);
                Cocos2dxHelperDialog.terminateProcess();
            }
        });
        setOnShowListener(new OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                JavaCocosBridge.registCallJavaListener(JavaCocosConstant.TYPE_START_RECORD, mStartRecordListener);
                JavaCocosBridge.registCallJavaListener(JavaCocosConstant.TYPE_STOP_RECORD, mStopRecordListener);
            }
        });
    }

//    @Override
//    protected void onResume() {
//        Log.d(TAG, "onResume()");
//        paused = false;
//        super.onResume();
//        if(gainAudioFocus)
//            Cocos2dxAudioFocusManager.registerAudioFocusListener(this);
////        Utils.hideVirtualButton();
//        resumeIfHasFocus();
//    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        Log.d(TAG, "onWindowFocusChanged() hasFocus=" + hasFocus);
        super.onWindowFocusChanged(hasFocus);

        this.hasFocus = hasFocus;
        resumeIfHasFocus();
    }

    private void resumeIfHasFocus() {
        if(hasFocus && !paused) {
//            Utils.hideVirtualButton();
            Cocos2dxHelperDialog.onResume();
            mGLSurfaceView.onResume();
        }
    }

//    @Override
//    protected void onPause() {
//        Log.d(TAG, "onPause()");
//        paused = true;
//        super.onPause();
//        if(gainAudioFocus)
//            Cocos2dxAudioFocusManager.unregisterAudioFocusListener(this);
//        Cocos2dxHelper.onPause();
//        mGLSurfaceView.onPause();
//    }

//    @Override
//    protected void onDestroy() {
//        if(gainAudioFocus)
//            Cocos2dxAudioFocusManager.unregisterAudioFocusListener(this);
//        Cocos2dxHelper.unregisterBatteryLevelReceiver(this);;
//        CanvasRenderingContext2DImpl.destroy();
//
//        super.onDestroy();
//
//        Log.d(TAG, "Cocos2dxActivity onDestroy: " + this + ", mGLSurfaceView" + mGLSurfaceView);
//        if (mGLSurfaceView != null) {
//            Cocos2dxHelper.terminateProcess();
//        }
//    }

    @Override
    public void showDialog(final String pTitle, final String pMessage) {
//        Message msg = new Message();
//        msg.what = Cocos2dxHandler.HANDLER_SHOW_DIALOG;
//        msg.obj = new Cocos2dxHandler.DialogMessage(pTitle, pMessage);
//        this.mHandler.sendMessage(msg);
    }

    @Override
    public void runOnGLThread(final Runnable runnable) {
        this.mGLSurfaceView.queueEvent(runnable);
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data)
//    {
//        for (PreferenceManager.OnActivityResultListener listener : Cocos2dxHelper.getOnActivityResultListeners()) {
//            listener.onActivityResult(requestCode, resultCode, data);
//        }
//
//        super.onActivityResult(requestCode, resultCode, data);
//    }

    // ===========================================================
    // Protected and private methods
    // ===========================================================

    protected void onLoadNativeLibraries() {
        try {
            ApplicationInfo ai = getContext().getPackageManager().getApplicationInfo(getContext().getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            String libName = bundle.getString("android.app.lib_name");
            System.loadLibrary(libName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Cocos2dxRenderer addSurfaceView() {
        this.mGLSurfaceView = this.onCreateView();
        this.mGLSurfaceView.setPreserveEGLContextOnPause(true);
        // Should set to transparent, or it will hide EditText
        // https://stackoverflow.com/questions/2978290/androids-edittext-is-hidden-when-the-virtual-keyboard-is-shown-and-a-surfacevie
        mGLSurfaceView.setBackgroundColor(Color.TRANSPARENT);
        // Switch to supported OpenGL (ARGB888) mode on emulator
        if (isAndroidEmulator())
            this.mGLSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);

        Cocos2dxRenderer renderer = new Cocos2dxRenderer();
        this.mGLSurfaceView.setCocos2dxRenderer(renderer);
        float dpi = getContext().getResources().getDisplayMetrics().density;
        int viewHeight = (int) (dpi * getViewHeight());
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, viewHeight);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        mFrameLayout.addView(this.mGLSurfaceView, layoutParams);
        mIvRecord = new ImageView(getContext());
        mIvRecord.setImageResource(R.drawable.icon_record_normal);
        mIvRecord.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    runOnGLThread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("action_type","pause");
                                jsonObject.put("action","listen");
                                jsonObject.put("pause_time",1);
                                JSONObject obj = new JSONObject();
                                obj.put("type", JavaCocosConstant.TYPE_DO_ACTION);
                                obj.put("param", jsonObject);
                                JavaCocosBridge.callCocos(obj.toString());
                            }catch (Throwable t) {
                                t.printStackTrace();
                            }
                        }
                    });
                    startRecognizer();
                } else if (action == MotionEvent.ACTION_UP) {
                    runOnGLThread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("action_type","resume");
                                JSONObject obj = new JSONObject();
                                obj.put("type", JavaCocosConstant.TYPE_DO_ACTION);
                                obj.put("param", jsonObject);
                                JavaCocosBridge.callCocos(obj.toString());
                            }catch (Throwable t) {
                                t.printStackTrace();
                            }
                        }
                    });
                    stopRecognizer();
                } else if (action == MotionEvent.ACTION_CANCEL) {
                    runOnGLThread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("action_type","resume");
                                JSONObject obj = new JSONObject();
                                obj.put("type", JavaCocosConstant.TYPE_DO_ACTION);
                                obj.put("param", jsonObject);
                                JavaCocosBridge.callCocos(obj.toString());
                            }catch (Throwable t) {
                                t.printStackTrace();
                            }
                        }
                    });
                    stopRecognizer();
                }
                return true;
            }
        });
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(DisplayUtil.dip2px(getContext(), 50), DisplayUtil.dip2px(getContext(), 50));
        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        lp.rightMargin = DisplayUtil.dip2px(getContext(), 16);
        lp.bottomMargin = DisplayUtil.dip2px(getContext(), 30);
        mIvRecord.setLayoutParams(lp);
        mFrameLayout.addView(mIvRecord);
        mIvRecord.setVisibility(View.GONE);

        mPetInfoView = new TextView(getContext());
        mPetInfoView.setBackgroundResource(R.drawable.pet_talk_bg);
        mPetInfoView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        mPetInfoView.setTextColor(Color.parseColor("#333333"));
        mPetInfoView.setMaxLines(4);
        mPetInfoView.setGravity(Gravity.CENTER_VERTICAL);
        mPetInfoView.setLineSpacing(0, 0.8f);
        mPetInfoView.setPadding(DisplayUtil.dip2px(getContext(), 8), DisplayUtil.dip2px(getContext(), 8), DisplayUtil.dip2px(getContext(), 8), DisplayUtil.dip2px(getContext(), 8));
        mPetInfoView.setMaxWidth(DisplayUtil.getScreenWidth(getContext()) / 2 - DisplayUtil.dip2px(getContext(), 16 * 2));
        mPetInfoView.setEllipsize(TextUtils.TruncateAt.END);
        RelativeLayout.LayoutParams lpInfo = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lpInfo.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        lpInfo.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lpInfo.leftMargin = DisplayUtil.dip2px(getContext(),16);
        lpInfo.rightMargin = DisplayUtil.dip2px(getContext(), 16) + DisplayUtil.getScreenWidth(getContext()) / 2;
        lpInfo.topMargin = DisplayUtil.dip2px(getContext(), 20);
        lpInfo.bottomMargin = DisplayUtil.dip2px(getContext(), 200);
        mFrameLayout.addView(mPetInfoView, lpInfo);
        mPetInfoView.setVisibility(View.GONE);

        mIvClose = new ImageView(getContext());
        mIvClose.setImageResource(R.drawable.icon_close_pet);
        mIvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        RelativeLayout.LayoutParams lpIvClose = new RelativeLayout.LayoutParams(DisplayUtil.dip2px(getContext(), 44), DisplayUtil.dip2px(getContext(), 44));
        lpIvClose.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        lpIvClose.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        lpIvClose.topMargin = DisplayUtil.dip2px(getContext(), 9);
        mFrameLayout.addView(mIvClose, lpIvClose);
        mIvClose.setVisibility(View.GONE);

        mIvShare = new ImageView(getContext());
        mIvShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO 送萌宠
                PtDialog ptDialog = new PtDialog(getContext());
                if (ptDialog != null) {
                    if (Build.VERSION.SDK_INT >= 25) {
                        ptDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
                    } else {
                        ptDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                    }
                    ptDialog.show();
                }
            }
        });
        mIvShare.setBackgroundResource(R.drawable.icon_share_pet);
        RelativeLayout.LayoutParams lpIvShare = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lpIvShare.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        lpIvShare.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        lpIvShare.rightMargin = DisplayUtil.dip2px(getContext(), 16);
        lpIvShare.topMargin = DisplayUtil.dip2px(getContext(), 58);
        mFrameLayout.addView(mIvShare, lpIvShare);
        mIvShare.setVisibility(View.GONE);

        return renderer;
    }

    private static int getViewHeight() {
        return 300;
    }

    private void addDebugInfo(final Cocos2dxRenderer renderer) {
        final LinearLayout.LayoutParams linearLayoutParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        linearLayoutParam.setMargins(30, 0, 0, 0);
        Cocos2dxHelper.setOnGameInfoUpdatedListener(new Cocos2dxHelper.OnGameInfoUpdatedListener() {
            @Override
            public void onFPSUpdated(final float fps) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mFPSTextView != null) {
                            mFPSTextView.setText("FPS: " + (int)Math.ceil(fps));
                        }
                    }
                });
            }

            @Override
            public void onJSBInvocationCountUpdated(final int count) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mJSBInvocationTextView != null) {
                            mJSBInvocationTextView.setText("JSB: " + count);
                        }
                    }
                });
            }

            @Override
            public void onOpenDebugView() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mLinearLayoutForDebugView != null || mFrameLayout == null) {
                            Log.e(TAG, "onOpenDebugView: failed!");
                            return;
                        }

                        mLinearLayoutForDebugView = new LinearLayout(getContext());
                        mLinearLayoutForDebugView.setOrientation(LinearLayout.VERTICAL);
                        mFrameLayout.addView(mLinearLayoutForDebugView);

                        mFPSTextView = new TextView(getContext());
                        mFPSTextView.setBackgroundColor(Color.RED);
                        mFPSTextView.setTextColor(Color.WHITE);
                        mLinearLayoutForDebugView.addView(mFPSTextView, linearLayoutParam);

                        mJSBInvocationTextView = new TextView(getContext());
                        mJSBInvocationTextView.setBackgroundColor(Color.GREEN);
                        mJSBInvocationTextView.setTextColor(Color.WHITE);
                        mLinearLayoutForDebugView.addView(mJSBInvocationTextView, linearLayoutParam);

                        mGLOptModeTextView = new TextView(getContext());
                        mGLOptModeTextView.setBackgroundColor(Color.BLUE);
                        mGLOptModeTextView.setTextColor(Color.WHITE);
                        mGLOptModeTextView.setText("GL Opt: Enabled");
                        mLinearLayoutForDebugView.addView(mGLOptModeTextView, linearLayoutParam);

                        mGameInfoTextView_0 = new TextView(getContext());
                        mGameInfoTextView_0.setBackgroundColor(Color.RED);
                        mGameInfoTextView_0.setTextColor(Color.WHITE);
                        mLinearLayoutForDebugView.addView(mGameInfoTextView_0, linearLayoutParam);

                        mGameInfoTextView_1 = new TextView(getContext());
                        mGameInfoTextView_1.setBackgroundColor(Color.GREEN);
                        mGameInfoTextView_1.setTextColor(Color.WHITE);
                        mLinearLayoutForDebugView.addView(mGameInfoTextView_1, linearLayoutParam);

                        mGameInfoTextView_2 = new TextView(getContext());
                        mGameInfoTextView_2.setBackgroundColor(Color.BLUE);
                        mGameInfoTextView_2.setTextColor(Color.WHITE);
                        mLinearLayoutForDebugView.addView(mGameInfoTextView_2, linearLayoutParam);
                    }
                });

                renderer.showFPS();
            }

            @Override
            public void onDisableBatchGLCommandsToNative() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mGLOptModeTextView != null) {
                            mGLOptModeTextView.setText("GL Opt: Disabled");
                        }
                    }
                });
            }

            @Override
            public void onGameInfoUpdated_0(final String text) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mGameInfoTextView_0 != null) {
                            mGameInfoTextView_0.setText(text);
                        }
                    }
                });
            }

            @Override
            public void onGameInfoUpdated_1(final String text) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mGameInfoTextView_1 != null) {
                            mGameInfoTextView_1.setText(text);
                        }
                    }
                });
            }

            @Override
            public void onGameInfoUpdated_2(final String text) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mGameInfoTextView_2 != null) {
                            mGameInfoTextView_2.setText(text);
                        }
                    }
                });
            }
        });
    }

    private final static boolean isAndroidEmulator() {
        String model = Build.MODEL;
        Log.d(TAG, "model=" + model);
        String product = Build.PRODUCT;
        Log.d(TAG, "product=" + product);
        boolean isEmulator = false;
        if (product != null) {
            isEmulator = product.equals("sdk") || product.contains("_sdk") || product.contains("sdk_");
        }
        Log.d(TAG, "isEmulator=" + isEmulator);
        return isEmulator;
    }

    // ===========================================================
    // Native methods
    // ===========================================================

    //native method,call GLViewImpl::getGLContextAttrs() to get the OpenGL ES context attributions
//    private static native int[] getGLContextAttrs();

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
        recordTask = new RecordTask(getContext(), speechRecognizer);
        recordTask.execute();
    }

    /**
     * 停止录音和识别
     */
    public void stopRecognizer() {
        // 停止录音
        Log.i(TAG, "Stoping recognizer...");
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
        recordTask.stop();
        if (isCancel) {
            return;
        }
        Message message = new Message();
        message.what = 100;
        message.obj = msg;
        mUiHandler.sendMessageDelayed(message, 1000);
    }

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

    protected void setRedText(String contentStr, TextView textView) {
        SpannableStringBuilder style = new SpannableStringBuilder();

        //设置文字
        style.append(contentStr);
        //设置部分文字点击事件
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
        //设置部分文字颜色
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(Color.parseColor("#EF4034"));
        style.setSpan(foregroundColorSpan, contentStr.length() - 7, contentStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        //配置给TextView
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setText(style);
    }

}
