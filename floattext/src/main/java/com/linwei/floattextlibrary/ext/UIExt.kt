package com.linwei.floattextlibrary.ext

import android.app.Activity
import android.content.Context
import android.util.DisplayMetrics


/**
 * ---------------------------------------------------------------------
 * @Author: WeiShuai
 * @Time: 2020/9/21
 * @Contact: linwei9605@gmail.com"
 * @Follow: https://github.com/WeiShuaiDev
 * @Description: 随机浮动弹出文字，支持水平方向，垂直方向。
 *-----------------------------------------------------------------------
 */

/**
 * 判断字符串是否为空
 */
fun isEmptyParameter(vararg params: String?): Boolean {
    for (p: String? in params)
        if (p.isNullOrEmpty() || p == "null" || p == "NULL") {
            return true
        }
    return false
}

/**
 * 判断字符串数组是否为空
 */
fun isEmptyArraysParameter(params: Array<out String?>): Boolean {
    for (p: String? in params)
        if (p.isNullOrEmpty() || p == "null" || p == "NULL") {
            return true
        }
    return false
}

/**
 * 将px值转换为dip或dp值
 */
fun px2dip(context: Context, pxValue: Float): Int {
    val scale: Float = context.resources.displayMetrics.density
    return (pxValue / scale + 0.5f).toInt()
}

/**
 * 将px值转换为dip或dp值
 */
fun dip2px(context: Context, dipValue: Float): Int {
    val scale: Float = context.resources.displayMetrics.density
    return (dipValue * scale + 0.5f).toInt()
}

/**
 * 将px值转换为sp值
 */
fun px2sp(context: Context, pxValue: Float): Int {
    val fontScale: Float = context.resources.displayMetrics.scaledDensity
    return (pxValue / fontScale + 0.5f).toInt()
}

/**
 * 将sp值转换为px值
 */
fun sp2px(context: Context, spValue: Float): Int {
    val fontScale: Float = context.resources.displayMetrics.scaledDensity
    return (spValue * fontScale + 0.5f).toInt()
}

/**
 * 将sp值转换为px值
 */
fun getWindowWidth(context: Activity): Int {
    val metric = DisplayMetrics()
    context.windowManager.defaultDisplay.getMetrics(metric)
    return metric.widthPixels
}

/**
 * 屏幕高度（像素）
 */
fun getWindowHeight(context: Activity): Int {
    val metric = DisplayMetrics()
    context.windowManager.defaultDisplay.getMetrics(metric)
    return metric.heightPixels
}