package com.jd.jrapp.other.pet.appwidget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import com.jd.jrapp.other.pet.R

/**
 * Created by yuguotao at 2020/8/7,3:53 PM
 */
class PetAppWidget : AppWidgetProvider() {
    val TAG = "PetAppWidget"

    var flag = 0

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        Log.e(TAG, "onReceive:" + intent)
    }

    override fun onUpdate(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetIds: IntArray?) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        Log.e(TAG, "onUpdate")
        val rv = RemoteViews(context?.packageName, R.layout.pet_app_widget_layout)
        flag++
        rv.setTextViewText(R.id.tv1, flag.toString())
        appWidgetManager?.updateAppWidget(R.id.tv1, rv)
    }
}