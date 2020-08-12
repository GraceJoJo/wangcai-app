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
                goTestActivity()
            } else if (v == mTvTougu) {
                Toast.makeText(mContext, "投顾", Toast.LENGTH_SHORT).show()
            } else if (v == mTvDonghua) {
                mMainView?.postDelayed(object : Runnable {
                    override fun run() {
                        animGone()
                    }
                }, 300)
            }
        }
    }

    private fun goTestActivity(){
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
        layoutParam.height = getPxValue(100)
        layoutParam.gravity = Gravity.RIGHT or Gravity.BOTTOM
        layoutParam.x = 0
        layoutParam.y = 300

        mMainView = LayoutInflater.from(context).inflate(R.layout.layout_pet_float_window, null)
        val bg = GradientDrawable()
        bg.setColor(Color.YELLOW)
        bg.cornerRadius = getPxValue(20).toFloat()
        mClickView = mMainView?.findViewById(R.id.view_btn)
        mClickView?.background = bg
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
        mTvShouyi?.background = bg
        mTvTougu = mMainView?.findViewById(R.id.tv_tougu)
        mTvTougu?.background = bg
        mTvDonghua = mMainView?.findViewById(R.id.tv_anim)
        mTvDonghua?.background = bg
        mTvShouyi?.setOnClickListener(mOnclickListener)
        mTvTougu?.setOnClickListener(mOnclickListener)
        mTvDonghua?.setOnClickListener(mOnclickListener)
        mClickView?.setOnClickListener(mOnclickListener)

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
            Log.e(javaClass.simpleName, "anim:" + value)
        }
        anim.duration = 200
        anim.interpolator = AccelerateDecelerateInterpolator()
        anim.start()
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