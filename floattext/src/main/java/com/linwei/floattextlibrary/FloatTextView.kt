package com.linwei.floattextlibrary

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.annotation.AnimRes
import com.linwei.floattextlibrary.ext.px2dip
import com.linwei.floattextlibrary.ext.px2sp
import com.linwei.floattextlibrary.listener.OnFloatTextViewClickListener
import java.util.*

/**
 * ---------------------------------------------------------------------
 * @Author: WeiShuai
 * @Time: 2020/9/21
 * @Contact: linwei9605@gmail.com"
 * @Follow: https://github.com/WeiShuaiDev
 * @Description: 随机浮动弹出文字，支持水平方向，垂直方向。
 *-----------------------------------------------------------------------
 */
class FloatTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    private var mViewFlipper: ViewFlipper? = null

    /**
     * 切换时间间隔，默认为3000
     */
    private var mInterval: Int = 3000

    /**
     * 是否为单行显示
     */
    private var mIsSingleLine: Boolean = false

    /**
     * 动画时间
     */
    private var mAnimDuration: Int = 1500

    /**
     * 文本默认颜色
     */
    private var mTextColor: Int = 0x000000

    /**
     * 文本大小
     */
    private var mTextSize: Float = 16f

    /**
     * 垂直内边距
     */
    private var mVerticalPadding: Float = 10f

    /**
     * 水平内边距
     */
    private var mHorizontalPadding: Float = 10f

    /**
     * 背景颜色
     */
    private var mBackground: Drawable?

    /**
     * Flags 类型；[FTV_FLAGS_STRIKE]
     * [FTV_FLAGS_UNDERLINE]
     */
    private var mFlags: Int = FTV_FLAGS_STRIKE

    /**
     * Gravity 类型; [FTV_GRAVITY_LEFT] 左边
     * [FTV_GRAVITY_CENTER] 中间
     * [FTV_GRAVITY_RIGHT] 右边
     *
     */
    private var mGravity: Int = Gravity.LEFT or Gravity.CENTER_VERTICAL

    /**
     * Direction 类型；[FTV_DIRECTION_BOTTOM_TO_TOP] 下往上
     *  [FTV_DIRECTION_TOP_TO_BOTTOM] 上往下
     *  [FTV_DIRECTION_RIGHT_TO_LEFT] 右往左
     *  [FTV_DIRECTION_LEFT_TO_RIGHT] 左往右
     *
     */
    private var mDirection: Int = FTV_DIRECTION_BOTTOM_TO_TOP


    /**
     * TypeFace 类型： [FTV_TYPEFACE_NORMAL] 默认
     * [FTV_TYPEFACE_BOLD] 加粗
     * [FTV_TYPEFACE_ITALIC] 斜体
     * [FTV_TYPEFACE_ITALIC_BOLD]  斜体\加粗
     */
    private var mTypeface: Int = FTV_TYPEFACE_NORMAL

    /**
     * 入场动画
     */
    @AnimRes
    private var mInAnimResId: Int = R.anim.anim_right_in

    /**
     * 出场动画
     */
    @AnimRes
    private var mOutAnimResId: Int = R.anim.anim_left_out

    /**
     * 显示数据集
     */
    private var mDatas: MutableList<String>? = null

    /**
     * 当前状态；false:未开始  true:开始
     */
    private var mIsStarted: Boolean = false

    /**
     * 当前是否 Detached
     */
    private var mIsDetachedFromWindow: Boolean = false

    /**
     * 点击事件
     */
    private var mClickListener: OnFloatTextViewClickListener? = null

    private val mLayoutParams = FrameLayout.LayoutParams(
        ViewGroup.LayoutParams.WRAP_CONTENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )


    companion object {
        private const val FTV_FLAGS_STRIKE: Int = 0
        private const val FTV_FLAGS_UNDERLINE: Int = 1

        private const val FTV_GRAVITY_LEFT: Int = 0
        private const val FTV_GRAVITY_CENTER: Int = 1
        private const val FTV_GRAVITY_RIGHT: Int = 2

        private const val FTV_DIRECTION_BOTTOM_TO_TOP: Int = 0
        private const val FTV_DIRECTION_TOP_TO_BOTTOM: Int = 1
        private const val FTV_DIRECTION_RIGHT_TO_LEFT: Int = 2
        private const val FTV_DIRECTION_LEFT_TO_RIGHT: Int = 3

        private const val FTV_TYPEFACE_NORMAL = 0
        private const val FTV_TYPEFACE_BOLD = 1
        private const val FTV_TYPEFACE_ITALIC = 2
        private const val FTV_TYPEFACE_ITALIC_BOLD = 3
    }

    init {
        val typeArray: TypedArray =
            context.obtainStyledAttributes(attrs, R.styleable.FloatTextView, defStyleAttr, 0)

        mInterval = typeArray.getInteger(R.styleable.FloatTextView_ftvInterval, mInterval)
        mIsSingleLine = typeArray.getBoolean(R.styleable.FloatTextView_ftvSingleLine, false)
        mAnimDuration =
            typeArray.getInteger(R.styleable.FloatTextView_ftvAnimDuration, mAnimDuration)
        mTextColor = typeArray.getColor(R.styleable.FloatTextView_ftvTextColor, mTextColor)

        mVerticalPadding =
            typeArray.getDimension(R.styleable.FloatTextView_ftvVerticalPadding, mVerticalPadding)

        mHorizontalPadding =
            typeArray.getDimension(R.styleable.FloatTextView_ftvHorizontalPadding, mVerticalPadding)

        mBackground = typeArray.getDrawable(R.styleable.FloatTextView_ftvBackground)


        if (typeArray.hasValue(R.styleable.FloatTextView_ftvTextSize)) {
            mTextSize = typeArray.getDimension(R.styleable.FloatTextView_ftvTextSize, mTextSize)
            //转换文字大小 px-sp
            mTextSize = px2sp(context, mTextSize).toFloat()
        }

        if (typeArray.hasValue(R.styleable.FloatTextView_ftvFlags)) {
            mFlags = typeArray.getInt(R.styleable.FloatTextView_ftvFlags, mFlags)
            when (mFlags) {
                FTV_FLAGS_STRIKE -> {
                    mFlags =
                        Paint.STRIKE_THRU_TEXT_FLAG or Paint.ANTI_ALIAS_FLAG
                }
                FTV_FLAGS_UNDERLINE -> {
                    mFlags =
                        Paint.UNDERLINE_TEXT_FLAG or Paint.ANTI_ALIAS_FLAG
                }
                else -> {
                    mFlags = 0 or Paint.ANTI_ALIAS_FLAG
                }
            }
        }

        if (typeArray.hasValue(R.styleable.FloatTextView_ftvGravity)) {
            mGravity = typeArray.getInt(R.styleable.FloatTextView_ftvGravity, mGravity)
            when (mGravity) {
                FTV_GRAVITY_LEFT -> {
                    mGravity = Gravity.LEFT or Gravity.CENTER_VERTICAL
                }

                FTV_GRAVITY_CENTER -> {
                    mGravity = Gravity.CENTER
                }

                FTV_GRAVITY_RIGHT -> {
                    mGravity = Gravity.RIGHT or Gravity.CENTER_VERTICAL
                }
            }
        }

        if (typeArray.hasValue(R.styleable.FloatTextView_ftvDirection)) {
            mDirection = typeArray.getInt(R.styleable.FloatTextView_ftvDirection, mDirection)
            when (mDirection) {
                FTV_DIRECTION_BOTTOM_TO_TOP -> {
                    mInAnimResId = R.anim.anim_bottom_in
                    mOutAnimResId = R.anim.anim_top_out
                }
                FTV_DIRECTION_TOP_TO_BOTTOM -> {
                    mInAnimResId = R.anim.anim_top_in
                    mOutAnimResId = R.anim.anim_bottom_out
                }
                FTV_DIRECTION_RIGHT_TO_LEFT -> {
                    mInAnimResId = R.anim.anim_right_in
                    mOutAnimResId = R.anim.anim_left_out
                }
                FTV_DIRECTION_LEFT_TO_RIGHT -> {
                    mInAnimResId = R.anim.anim_left_in
                    mOutAnimResId = R.anim.anim_right_out
                }
            }
        } else {
            mInAnimResId = R.anim.anim_right_in
            mOutAnimResId = R.anim.anim_left_out
        }

        if (typeArray.hasValue(R.styleable.FloatTextView_ftvTypeface)) {
            mTypeface = typeArray.getInt(R.styleable.FloatTextView_ftvTypeface, mTypeface)
            when (mTypeface) {
                FTV_TYPEFACE_BOLD -> {
                    mTypeface = Typeface.BOLD
                }
                FTV_TYPEFACE_ITALIC -> {
                    mTypeface = Typeface.ITALIC
                }
                FTV_TYPEFACE_ITALIC_BOLD -> {
                    mTypeface = Typeface.ITALIC or Typeface.BOLD
                }
            }
        }

        typeArray.recycle()

        initViewFlipper()
    }

    /**
     * 初始化 `ViewFlipper`,增加到容器中
     */
    private fun initViewFlipper() {
        mViewFlipper = ViewFlipper(context)
        mViewFlipper?.layoutParams =
            LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        addView(mViewFlipper)

        //监听用户点击事件
        mViewFlipper?.setOnClickListener {
            val childPosition: Int = mViewFlipper?.displayedChild ?: -1

            mDatas?.let {
                if (childPosition >= 0 && childPosition < mDatas!!.size) {
                    mClickListener?.onClick(childPosition, it[childPosition])
                }
            }
        }
    }

    /**
     * 进场、出场动画
     * @param inAnimResId  [Int] 进场动画资源
     * @param outAnimResId [Int] 出场动画资源
     */
    private fun setInAndOutAnimation(@AnimRes inAnimResId: Int, @AnimRes outAnimResId: Int) {
        val inAnim: Animation = AnimationUtils.loadAnimation(context, inAnimResId)
        inAnim.duration = mAnimDuration.toLong()
        mViewFlipper?.inAnimation = inAnim

        val outAnim: Animation = AnimationUtils.loadAnimation(context, outAnimResId)
        outAnim.duration = mAnimDuration.toLong()
        mViewFlipper?.outAnimation = outAnim
    }

    /**
     * 启动动画效果
     */
    fun startViewAnimator() {
        if (!mIsStarted) {
            if (!mIsDetachedFromWindow) {
                mIsStarted = true
                postDelayed(mRunnable, mInterval.toLong())
            }
        }
    }

    /**
     * 结束动画效果
     */
    fun stopViewAnimator() {
        if (mIsStarted) {
            removeCallbacks(mRunnable)
            mIsStarted = false
        }
    }

    private val mRunnable = AnimRunnable()

    private inner class AnimRunnable : Runnable {
        override fun run() {
            if (mIsStarted) {
                setInAndOutAnimation(mInAnimResId, mOutAnimResId)
                mViewFlipper?.showNext() //手动显示下一个子view。
                postDelayed(this, mInterval + mAnimDuration.toLong())
            } else {
                stopViewAnimator()
            }
        }
    }


    /**
     * 增加数据集
     * @param list [MutableList]
     */
    fun setData(list: MutableList<String>?) {
        this.mDatas = list
        list?.let {
            if (it.size > 0) mViewFlipper?.removeAllViews()

            it.forEachIndexed { index, content ->
                val view: TextView = createChildView(index, content)

                view.setPadding(
                    mHorizontalPadding.toInt(),
                    mVerticalPadding.toInt(),
                    mHorizontalPadding.toInt(),
                    mVerticalPadding.toInt()
                )
                view.layoutParams = mLayoutParams
                mViewFlipper?.addView(view, index)
            }
        }
    }

    /**
     * 增加数据集,图标
     * @param list [MutableList]
     * @param drawable [Drawable]
     * @param size [Int]
     * @param direction [Int]
     */
    fun setDataWithDrawableIcon(
        list: MutableList<String>?,
        drawable: Drawable,
        size: Int,
        direction: Int
    ) {
        this.mDatas = list
        list?.let {
            if (it.size > 0) mViewFlipper?.removeAllViews()

            it.forEachIndexed { index, content ->
                val view: TextView = createChildView(index, content)
                view.setPadding(
                    mHorizontalPadding.toInt(),
                    mVerticalPadding.toInt(),
                    mHorizontalPadding.toInt(),
                    mVerticalPadding.toInt()
                )

                view.compoundDrawablePadding = 8
                val muchDp: Int = px2dip(context, size.toFloat())

                drawable.setBounds(0, 0, muchDp + 10, muchDp)
                if (direction == Gravity.LEFT) {
                    view.setCompoundDrawables(drawable, null, null, null) //左边
                } else if (direction == Gravity.TOP) {
                    view.setCompoundDrawables(null, drawable, null, null) //顶部
                } else if (direction == Gravity.RIGHT) {
                    view.setCompoundDrawables(null, null, drawable, null) //右边
                } else if (direction == Gravity.BOTTOM) {
                    view.setCompoundDrawables(null, null, null, drawable) //底部
                }

                val linearLayout = LinearLayout(context)
                linearLayout.orientation = LinearLayout.HORIZONTAL //水平方向
                linearLayout.gravity = mGravity //子view显示位置跟随TextView
                val param = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )

                linearLayout.addView(view, param)
                linearLayout.layoutParams = mLayoutParams
                mViewFlipper!!.addView(linearLayout, index) //添加子view,并标识子view位置
            }
        }
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        var childHeight = 0
        mViewFlipper?.let {
            if (it.childCount > 0) {
                childHeight = it.getChildAt(0).measuredHeight
            }
        }
        val defaultSize: Int = childHeight + paddingTop + paddingBottom

        setMeasuredDimension(
            fetchDefaultSize(defaultSize, widthMeasureSpec),
            fetchDefaultSize(defaultSize, heightMeasureSpec)
        )
        val rootHeight: Int = measuredHeight - (paddingTop + paddingBottom)
        if (rootHeight > 0 && childHeight > 0) {
            mViewFlipper?.let {
                val childCount: Int = it.childCount
                for (index: Int in 0 until childCount) {
                    val childView: View = it.getChildAt(index)
                    val differHeight: Int = rootHeight - childHeight
                    if (differHeight > 0) {
                        mLayoutParams.topMargin = Random().nextInt(differHeight)
                        childView.layoutParams = mLayoutParams
                    }
                }
            }
        }
    }

    private fun fetchDefaultSize(size: Int, measureSpec: Int): Int {
        var result: Int = size
        val specMode: Int = MeasureSpec.getMode(measureSpec)
        val specSize: Int = MeasureSpec.getSize(measureSpec)
        when (specMode) {
            MeasureSpec.UNSPECIFIED,
            MeasureSpec.AT_MOST -> {
                result = size
            }
            MeasureSpec.EXACTLY -> {
                result = specSize
            }
        }
        return result
    }

    /**
     * 创建 Child View
     * @param index [Int] 角标
     * @param content [String] 内容
     */
    private fun createChildView(index: Int, content: String): TextView {
        return TextView(context).apply {
            text = content
            isSingleLine = mIsSingleLine
            ellipsize = TextUtils.TruncateAt.END
            setTextColor(mTextColor)  //字体颜色
            textSize = mTextSize   //字体大小
            gravity = mGravity   //字体位置
            paint.flags = mFlags  //字体划线
            setTypeface(null, mTypeface)  //字体样式
            background = mBackground
        }
    }

    /**
     * 设置 `FloatTextView` 点击事件
     */
    fun setOnFloatTextViewClickListener(listener: OnFloatTextViewClickListener) {
        this.mClickListener = listener
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mIsDetachedFromWindow = true
        stopViewAnimator()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        mIsDetachedFromWindow = false
        startViewAnimator()
    }
}