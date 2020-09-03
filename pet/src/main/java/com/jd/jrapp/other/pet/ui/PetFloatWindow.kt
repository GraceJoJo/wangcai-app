package com.jd.jrapp.other.pet.ui

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.PointF
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.support.constraint.ConstraintLayout
import android.util.Log
import android.view.*
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.jd.jrapp.other.pet.R
import com.jd.jrapp.other.pet.ui.dialog.JDQrDialog
import com.jd.jrapp.other.pet.ui.dialog.ShouYiDialog
import org.cocos2dx.javascript.AppActivity

/**
 * Created by yuguotao at 2020/8/6,3:58 PM
 */
class PetFloatWindow private constructor() {

    companion object {
        val instance: PetFloatWindow by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            PetFloatWindow()
        }
    }

    var mMainView: View? = null
    var mClickView: ImageView? = null
    var mTvShouyi: TextView? = null
    var mTvTougu: TextView? = null
    var mTvDonghua: TextView? = null
    var mTvSign: TextView? = null
    var mTvPet: TextView? = null
    var mShouYiDialog: ShouYiDialog? = null
    var mShouPayDialog: JDQrDialog? = null

    private var mContext: Context? = null

    private var mIsOpen = false

    private var mIsShowing = false

    private var mLastX: Float = 0f
    private var mLastY: Float = 0f

    private var mTouchEvent = false
    private var mCanTouch = true

    private var mOnclickListener = object : View.OnClickListener {
        override fun onClick(v: View?) {
            Log.e("PetFloatWindow", "onClick: " + v)
            animSwitch()
            if (v == mTvShouyi) {
                showShouYiDialog()
            } else if (v == mTvTougu) {
                val intent = Intent(mContext, SpeechRecognizerActivity::class.java)
                mContext?.startActivity(intent)
            } else if (v == mTvDonghua) {
                showPayDialog()
            }else if (v == mTvSign) {
                val intent = Intent(mContext, AppActivity::class.java)
                mContext?.startActivity(intent)
            } else if (v == mClickView) {
//                animSwitch()
            }
        }
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
            // 可以展示弹框
            show(context)
        } else {
            // TODO 申请权限
            goSettings(context)
        }
    }

    fun goSettings(context: Context) {
        val intent = Intent()
        intent.action = Settings.ACTION_MANAGE_OVERLAY_PERMISSION
        intent.data = Uri.parse("package:" + context.packageName)
        context.startActivity(intent)
    }

    fun show(context: Context) {
        if (mIsShowing) {
            return
        }
        mIsShowing = true
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val layoutParam = WindowManager.LayoutParams()
        layoutParam.type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY else WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
        layoutParam.format = PixelFormat.TRANSPARENT
        layoutParam.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM
        layoutParam.width = getPxValue(100)
        layoutParam.height = getPxValue(200)
        layoutParam.gravity = Gravity.RIGHT or Gravity.BOTTOM
        layoutParam.x = 0
        layoutParam.y = 300

        mMainView = LayoutInflater.from(context).inflate(R.layout.layout_pet_float_window, null)
        val bg = GradientDrawable()
        bg.cornerRadius = getPxValue(20).toFloat()
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
                    if (mIsOpen) {
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

    private fun animSwitch() {
        mIsOpen = !mIsOpen
        val start = (mTvShouyi?.layoutParams as? ConstraintLayout.LayoutParams)?.circleRadius ?: 0
        val end = getPxValue(if (mIsOpen) 60 else 0)
        val anim = ValueAnimator.ofInt(start, end)
        anim.addUpdateListener {
            val value = it.animatedValue as Int
            setViewCircleRadiusAndAlpha(mTvShouyi, value)
            setViewCircleRadiusAndAlpha(mTvTougu, value)
            setViewCircleRadiusAndAlpha(mTvDonghua, value)
            setViewCircleRadiusAndAlpha(mTvSign, value)
            setViewCircleRadiusAndAlpha(mTvPet, value)
//            Log.e(javaClass.simpleName, "anim:" + value)
        }
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


}