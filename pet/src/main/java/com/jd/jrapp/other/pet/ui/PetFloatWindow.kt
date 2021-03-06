package com.jd.jrapp.other.pet.ui

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.PixelFormat
import android.graphics.PointF
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.*
import android.provider.Settings
import android.support.constraint.ConstraintLayout
import android.util.Log
import android.view.*
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.TextView
import com.jd.jrapp.other.pet.R
import com.jd.jrapp.other.pet.ui.dialog.*
import com.jd.jrapp.other.pet.utils.AppManager
import org.cocos2dx.javascript.ICocosInterface
import org.cocos2dx.javascript.service.CocosService
import java.util.*

/**
 * Created by yuguotao at 2020/8/6,3:58 PM
 */
class PetFloatWindow private constructor() {

    companion object {
        val instance: PetFloatWindow by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            PetFloatWindow()
        }
        val WIDTH_CENTER = 44
        val WIDTH_NORMAL = 36
        val SPACE = 15
        val TOTAL_WIDTH = WIDTH_CENTER + SPACE + WIDTH_NORMAL
        val TOTAL_HEIGHT = WIDTH_CENTER + SPACE + WIDTH_NORMAL + SPACE + WIDTH_NORMAL
        val RADIUS = WIDTH_CENTER / 2 + SPACE + WIDTH_NORMAL / 2
        var zjsy = 50
    }

    private lateinit var windowManager: WindowManager
    private lateinit var layoutParam: WindowManager.LayoutParams
    var mMainView: View? = null
    var mClickView: TextView? = null
    var mTvShouyi: TextView? = null
    var mTvTougu: TextView? = null
    var mTvDonghua: TextView? = null
    var mTvSign: TextView? = null
    var mTvPet: TextView? = null
    var mShouYiDialog: ShouYiDialog? = null
    var mShouPayDialog: JDQrDialog? = null
    var mTouguDialog: TouguDialog? = null
    var mSignDialog: SignDialog? = null
    var mLicaiDialog: LicaiDialog? = null
    var mOrderDialog: OrderDialog? = null
    var mCommunityDialog: CommunityDialog? = null
    var mSearchDialog: SearchDialog? = null
    var mTouchSlop: Int = 8
    var mCustomDialog: CustomDialog? = null
    var mPtDialog: PtDialog? = null


    private var mContext: Context? = null

    private var mIsOpen = false

    private var mIsShowing = false

    private var mLastX: Float = 0f
    private var mLastY: Float = 0f

    private var mTouchEvent = false
    private var mCanTouch = true

    private val zjsys = intArrayOf(50, 49, 51, 46, 47, 52, 48, 46, 49, 45, 43)

    internal var handler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (msg.what == 100) {
                val number = Math.floor((Random().nextInt(10) + 1).toDouble()).toInt()
                zjsy = zjsys[number]
                setData(false)
            }

        }
    }

    fun startTimer() {
        if(!handler.hasMessages(100)){
            handler.postDelayed({ handler.sendEmptyMessage(100) }, (5 * 1000).toLong())
        }
    }

    fun stopTimer() {
        if(handler!=null){
            handler.removeCallbacksAndMessages(null)
        }
    }

    fun setData(isFirst: Boolean) {
        if (mClickView != null) {
            mClickView?.setText("+${zjsy}")
            if (!isFirst) {
                startTimer()
            }
        }
    }


    private var mOnclickListener = object : View.OnClickListener {
        //??????????????????????????????????????????????????????
        //?????????????????????????????????????????????2??????????????????3???????????????????????????????????????3...
        val mHints = LongArray(2);//???????????????0

        override fun onClick(v: View?) {
            Log.e("PetFloatWindow", "onClick: " + v)
            animSwitch()
            if (v == mTvShouyi) {
                showOrderDialog()
            } else if (v == mTvTougu) {
//                AppManager.getInstance().getNLSToken()
//                showTouguDialog();
                showCommunityDialog()
            } else if (v == mTvDonghua) {
//                showPayDialog()
                showSearchDialog()
            } else if (v == mTvSign) {
                showSignDialog();
            } else if (v == mClickView) {
//                animSwitch()
                //???mHints??????????????????????????????????????????
                System.arraycopy(mHints, 1, mHints, 0, mHints.size - 1);
                //???????????????????????????????????????
                mHints[mHints.size - 1] = SystemClock.uptimeMillis();
                if (SystemClock.uptimeMillis() - mHints[0] <= 500) {
                    showCustomDialog()
                }
            } else if (v == mTvPet) {
                showLicaiDialog()
//                showPtDialog()
            }
        }
    }


    private fun showPetDialog() {
        mCocosInterface?.showPetDialog()
    }

    private fun showShouYiDialog() {
        if (mShouYiDialog == null) {
            mShouYiDialog = ShouYiDialog(mContext)
        }
        if (mShouYiDialog != null) {
            if (Build.VERSION.SDK_INT >= 25) {
                mShouYiDialog?.getWindow()?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
            } else {
                mShouYiDialog?.getWindow()?.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            }
            mShouYiDialog?.show()
        }
    }

    private fun showPtDialog() {
        if (mPtDialog == null) {
            mPtDialog = PtDialog(mContext)
        }
        if (mPtDialog != null) {
            if (Build.VERSION.SDK_INT >= 25) {
                mPtDialog?.getWindow()?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
            } else {
                mPtDialog?.getWindow()?.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            }
            mPtDialog?.show()
        }
    }

    private fun showCustomDialog() {
        mCustomDialog = CustomDialog(mContext, object : CustomDialog.ItemClickCallback {
            override fun clickCallback(type: Int) {
                if (type == 0) {
                    mClickView?.alpha = 1f
                    mClickView?.setText("+" + zjsy)
                    mClickView?.setBackgroundResource(R.drawable.bg_oval)
                    setData(true)
                    startTimer()
                } else if (type == 1) {
                    mClickView?.alpha = 1f
                    mClickView?.setText("")
                    mClickView?.setBackgroundResource(R.drawable.icon_pw);
                    stopTimer()
                } else if (type == 2) {
                    mClickView?.setText("")
                    mClickView?.setBackgroundResource(R.drawable.bg_oval)
                    mClickView?.alpha = 0.2f
                    stopTimer()
                }
                animSwitch()
                mCustomDialog?.dismiss()
            }
        })
        if (mCustomDialog != null) {
            if (Build.VERSION.SDK_INT >= 25) {
                mCustomDialog?.getWindow()?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
            } else {
                mCustomDialog?.getWindow()?.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            }
            mCustomDialog?.show()
        }
    }

    private fun showLicaiDialog() {
        mLicaiDialog = LicaiDialog(mContext, zjsy)
        if (mLicaiDialog != null) {
            if (Build.VERSION.SDK_INT >= 25) {
                mLicaiDialog?.getWindow()?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
            } else {
                mLicaiDialog?.getWindow()?.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            }
            mLicaiDialog?.show()
        }
    }

    private fun showOrderDialog() {
        mOrderDialog = OrderDialog(mContext, zjsy)
        if (mOrderDialog != null) {
            if (Build.VERSION.SDK_INT >= 25) {
                mOrderDialog?.getWindow()?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
            } else {
                mOrderDialog?.getWindow()?.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            }
            mOrderDialog?.show()
        }
    }
    private fun showCommunityDialog() {
        mCommunityDialog = CommunityDialog(mContext, zjsy)
        if (mCommunityDialog != null) {
            if (Build.VERSION.SDK_INT >= 25) {
                mCommunityDialog?.getWindow()?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
            } else {
                mCommunityDialog?.getWindow()?.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            }
            mCommunityDialog?.show()
        }
    }
    private fun showSearchDialog() {
        mSearchDialog = SearchDialog(mContext, zjsy)
        if (mSearchDialog != null) {
            if (Build.VERSION.SDK_INT >= 25) {
                mSearchDialog?.getWindow()?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
            } else {
                mSearchDialog?.getWindow()?.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            }
            mSearchDialog?.show()
        }
    }

    private fun showPayDialog() {
        if (mShouPayDialog == null) {
            mShouPayDialog = JDQrDialog(mContext)
        }
        if (mShouPayDialog != null) {
            if (Build.VERSION.SDK_INT >= 25) {
                mShouPayDialog?.getWindow()?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
            } else {
                mShouPayDialog?.getWindow()?.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            }
            mShouPayDialog?.show()
        }
    }

    private fun showTouguDialog() {
        mTouguDialog = TouguDialog(mContext);
        if (mTouguDialog != null) {
            if (Build.VERSION.SDK_INT >= 25) {
                mTouguDialog?.getWindow()?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
            } else {
                mTouguDialog?.getWindow()?.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            }
            mTouguDialog?.show()
        }
    }

    private fun showSignDialog() {
        if (mSignDialog == null) {
            mSignDialog = SignDialog(mContext);
        }
        if (mSignDialog != null) {
            if (Build.VERSION.SDK_INT >= 25) {
                mSignDialog?.getWindow()?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
            } else {
                mSignDialog?.getWindow()?.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            }
            mSignDialog?.show()
        }
    }

    private fun goTestActivity() {
        val intent = Intent(mContext, TestActivity::class.java)
        mContext?.startActivity(intent)
    }

    fun checkCanShow(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true
        }
        return Settings.canDrawOverlays(context)
    }

    fun checkAndShow(context: Context) {
        this.mContext = context
        if (checkCanShow(context)) {
            // ??????????????????
            show(context)
        } else {
            // TODO ????????????
            goSettings(context)
        }
    }

    fun goSettings(context: Context) {
        val intent = Intent()
        intent.action = Settings.ACTION_MANAGE_OVERLAY_PERMISSION
        intent.data = Uri.parse("package:" + context.packageName)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    fun show(context: Context) {
        if (mIsShowing) {
            return
        }
        mTouchSlop = ViewConfiguration.get(mContext).scaledTouchSlop
        bindCocosService()
        mIsShowing = true
        windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        layoutParam = WindowManager.LayoutParams()
        layoutParam.type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY else WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
        layoutParam.format = PixelFormat.TRANSPARENT
        layoutParam.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM
        layoutParam.width = getPxValue(TOTAL_WIDTH)
        layoutParam.height = getPxValue(TOTAL_HEIGHT)
        layoutParam.gravity = Gravity.RIGHT or Gravity.BOTTOM
        layoutParam.x = 0
        layoutParam.y = 300

        mMainView = LayoutInflater.from(context).inflate(R.layout.layout_pet_float_window, null)
        val bg = GradientDrawable()
        bg.cornerRadius = getPxValue(25).toFloat()
        mClickView = mMainView?.findViewById(R.id.view_btn)
        mClickView?.setBackgroundResource(R.drawable.bg_oval)
        mClickView?.setOnTouchListener { v, event ->
            val curX = event.rawX
            val curY = event.rawY
            if (event.action == MotionEvent.ACTION_DOWN) {
                mCanTouch = true
                if (mIsOpen) {
//                    animSwitch()
                    mCanTouch = false
                }
                mTouchEvent = false
            } else if (event.action == MotionEvent.ACTION_MOVE) {
                if (mCanTouch && PointF(curX - mLastX, curY - mLastY).length() >= 8) {
                    mTouchEvent = true
                    layoutParam.x -= (curX - mLastX).toInt()
                    layoutParam.y -= (curY - mLastY).toInt()
                    windowManager.updateViewLayout(mMainView, layoutParam)
                } else if (!mCanTouch) {
                    if (mIsOpen && !animSwitching) {
                        animSwitch()
                    }
                }
            }
            mLastX = event.rawX
            mLastY = event.rawY
            return@setOnTouchListener mTouchEvent
        }
        mTvShouyi = mMainView?.findViewById(R.id.tv_shouyi)
        mTvTougu = mMainView?.findViewById(R.id.tv_tougu)
        mTvDonghua = mMainView?.findViewById(R.id.tv_pay)
        mTvSign = mMainView?.findViewById(R.id.tv_sign)
        mTvPet = mMainView?.findViewById(R.id.tv_pet)
        mTvShouyi?.setBackgroundResource(R.drawable.bg_oval)
        mTvTougu?.setBackgroundResource(R.drawable.bg_oval)
        mTvDonghua?.setBackgroundResource(R.drawable.bg_oval)
        mTvSign?.setBackgroundResource(R.drawable.bg_oval)
        mTvPet?.setBackgroundResource(R.drawable.bg_oval)
        mTvShouyi?.setTextSize(12f)
        mTvTougu?.setTextSize(12f)
        mTvSign?.setTextSize(12f)
        mTvDonghua?.setTextSize(12f)
        mTvPet?.setTextSize(12f)
        mTvTougu?.setBackgroundResource(R.drawable.bg_oval)
        mTvDonghua?.setBackgroundResource(R.drawable.bg_oval)
        mTvSign?.setBackgroundResource(R.drawable.bg_oval)
        mTvPet?.setBackgroundResource(R.drawable.bg_oval)
        mTvShouyi?.setOnClickListener(mOnclickListener)
        mTvTougu?.setOnClickListener(mOnclickListener)
        mTvDonghua?.setOnClickListener(mOnclickListener)
        mClickView?.setOnClickListener(mOnclickListener)
        mTvSign?.setOnClickListener(mOnclickListener)
        mTvPet?.setOnClickListener(mOnclickListener)
        windowManager.addView(mMainView, layoutParam)
    }

    var animSwitching = false

    private fun animSwitch() {
        if (animSwitching) {
            return
        }
        animSwitching = true
        mIsOpen = !mIsOpen
        val start = (mTvShouyi?.layoutParams as? ConstraintLayout.LayoutParams)?.circleRadius ?: 0
        val end = getPxValue(if (mIsOpen) RADIUS else 0)
        val anim = ValueAnimator.ofInt(start, end)
        anim.addUpdateListener {
            val value = it.animatedValue as Int
            setViewCircleRadiusAndAlpha(mTvShouyi, value)
            setViewCircleRadiusAndAlpha(mTvTougu, value)
            setViewCircleRadiusAndAlpha(mTvDonghua, value)
            setViewCircleRadiusAndAlpha(mTvSign, value)
            setViewCircleRadiusAndAlpha(mTvPet, value)
        }
        anim.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {

            }

            override fun onAnimationEnd(animation: Animator?) {
                animSwitching = false
//                if (!mIsOpen) {
//                    val oldH = layoutParam.height
//                    layoutParam.width = getPxValue(WIDTH_CENTER)
//                    layoutParam.height = getPxValue(WIDTH_CENTER)
//                    layoutParam.y = layoutParam.y - (layoutParam.height - oldH) /2
//
////                    val lpMain = mMainView?.layoutParams
////                    lpMain?.width = layoutParam.width
////                    lpMain?.height = layoutParam.height
////                    mMainView?.layoutParams = lpMain
//                    windowManager.updateViewLayout(mMainView, layoutParam)
//                } else {
//
//                }
            }

            override fun onAnimationCancel(animation: Animator?) {

            }

            override fun onAnimationStart(animation: Animator?) {
//                if (mIsOpen) {
//                    val oldH = layoutParam.height
//                    layoutParam.width = getPxValue(TOTAL_WIDTH)
//                    layoutParam.height = getPxValue(TOTAL_HEIGHT)
//                    layoutParam.y = layoutParam.y - (layoutParam.height - oldH) / 2
////                    val lpMain = mMainView?.layoutParams
////                    lpMain?.width = layoutParam.width
////                    lpMain?.height = layoutParam.height
////                    mMainView?.layoutParams = lpMain
//                    windowManager.updateViewLayout(mMainView, layoutParam)
//                } else {
//
//                }
            }
        })
        anim.duration = 200
        anim.interpolator = AccelerateDecelerateInterpolator()
        anim.start()
    }

    private fun closeWindow() {
        if (mIsShowing && mMainView != null && mIsOpen) {
            val windowManager = mContext?.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            windowManager.removeView(mMainView)
            mIsShowing = false
        }
    }

    private fun animGone() {
        val endX = 600
        val endY = 600
        val lp = mMainView?.layoutParams as WindowManager.LayoutParams
        val startX: Int = lp.x
        val startY: Int = lp.y
        val anim = ValueAnimator.ofInt(0, 100)
        val windowManager = mContext?.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        anim.addUpdateListener {
            val value = it.animatedValue as Int
            lp.x = startX + ((endX - startX) * value / 100f).toInt()
            lp.y = startY + ((endY - startY) * value / 100f).toInt()
            windowManager?.updateViewLayout(mMainView, lp)
        }
        anim.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {

            }

            override fun onAnimationEnd(animation: Animator?) {
                windowManager.removeView(mMainView)
                mIsShowing = false
            }

            override fun onAnimationCancel(animation: Animator?) {

            }

            override fun onAnimationStart(animation: Animator?) {

            }
        })
        anim.duration = 400
        anim.interpolator = AccelerateDecelerateInterpolator()
        anim.start()
    }

    private fun setViewCircleRadiusAndAlpha(view: View?, value: Int) {
        val lp = view?.layoutParams as? ConstraintLayout.LayoutParams
        lp?.circleRadius = value
        view?.layoutParams = lp
        view?.alpha = value.toFloat() / getPxValue(60)
    }

    private fun getPxValue(dp: Int): Int {
        mContext?.let {
            return (it.resources.displayMetrics.density * dp).toInt()
        }
        return 0
    }

    private fun startCocosService() {
        mContext?.startService(Intent(mContext, CocosService::class.java))
    }

    private var mCocosInterface: ICocosInterface? = null

    private val mDeathRecipent = object : IBinder.DeathRecipient {
        override fun binderDied() {
            onBinderDied()
        }
    }

    private fun onBinderDied() {
        Log.e("AppActivity", "onBinderDied!!!")
        mCocosInterface?.asBinder()?.unlinkToDeath(mDeathRecipent, 0)
        mCocosInterface = null
        bindCocosService()
    }

    private fun bindCocosService() {

        val intent = Intent(mContext, CocosService::class.java)
        mContext?.bindService(intent, object : ServiceConnection {
            override fun onServiceDisconnected(name: ComponentName?) {

            }

            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                try {
                    Log.e("AppActivity", "onServiceConnected:" + service)
                    mCocosInterface = ICocosInterface.Stub.asInterface(service)
                    service?.linkToDeath(mDeathRecipent, 0)
                } catch (e: Throwable) {
                    e.printStackTrace()
                }
            }
        }, Context.BIND_AUTO_CREATE)
    }


}