package com.licheedev.blurview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
import android.util.AttributeSet;
import android.view.View;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 全覆盖高斯模糊
 * Created by John on 2015/7/18.
 */
public class BlurView extends View {

    protected RenderScript mRenderScript;
    protected ScriptIntrinsicBlur mBlurScript;

    protected final int DEFAULT_BLUR_RADIUS = 4;
    protected final int DEFAULT_DOWN_SAMPLE_FACTOR = 10;
    protected final int DEFAULT_OVERLAY_COLOR = Color.TRANSPARENT;

    protected int mBlurRadius; // 模糊半径
    protected int mDownSampleFactor; // 缩小样本因数
    protected int mOverlayColor; // 覆盖层颜色
    protected float mScale;

    protected View mToBlurView;

    protected AtomicBoolean isWorking = new AtomicBoolean(false);

    public BlurView(Context context) {
        this(context, null);
    }

    public BlurView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BlurView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs, defStyleAttr);
        initRenderScript(context);
    }

    /**
     * 初始化属性
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    protected void initAttrs(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BlurView, defStyleAttr, 0);
        mBlurRadius = a.getInt(R.styleable.BlurView_blurRadius, DEFAULT_BLUR_RADIUS);
        setBlurRadius(mBlurRadius);
        mDownSampleFactor = a.getInt(R.styleable.BlurView_downSampleFactor,
                DEFAULT_DOWN_SAMPLE_FACTOR);
        setDownSampleFactor(mDownSampleFactor);
        mOverlayColor = a.getColor(R.styleable.BlurView_overlayColor, DEFAULT_OVERLAY_COLOR);
        a.recycle();
    }

    /**
     * 初始化RenderScript
     *
     * @param context
     */
    protected void initRenderScript(Context context) {
        mRenderScript = RenderScript.create(context.getApplicationContext());
        mBlurScript = ScriptIntrinsicBlur.create(mRenderScript, Element.U8_4(mRenderScript));
    }

    /**
     * 销毁RenderScript
     */
    protected void destroyRenderScript() {
        if (mBlurScript != null) {
            mBlurScript.destroy();
            mBlurScript = null;
        }
        if (mRenderScript != null) {
            mRenderScript.destroy();
            mRenderScript = null;
        }
    }

    /**
     * 获取要被模糊的视图对象
     *
     * @return
     */
    public View getToBlurView() {
        return mToBlurView;
    }

    /**
     * 设置要被模糊的视图对象
     *
     * @param toBlurView
     */
    public void setToBlurView(View toBlurView) {
        mToBlurView = toBlurView;
    }

    /**
     * 获取模糊半径
     *
     * @return
     */
    public int getBlurRadius() {
        return mBlurRadius;
    }

    /**
     * 设置模糊半径
     *
     * @param blurRadius 模糊半径，数值越大，图像越模糊，处理耗时越长
     */
    public void setBlurRadius(int blurRadius) {
        if (blurRadius < 0 || blurRadius > 25) {
            throw new IllegalArgumentException(
                    "the blurRadius must be (0 <= blurRadius <= 25), current is " + blurRadius);
        }
        mBlurRadius = blurRadius;
    }

    /**
     * 获取模糊图像缩小样本因数
     *
     * @return
     */
    public int getDownSampleFactor() {
        return mDownSampleFactor;
    }

    /**
     * 设置进行模糊时，图像缩小样本因数，处理图象时，会先对目标图像进行 1/样本因数 的比例进行缩放，再进行模糊处理。
     * 因数越大，选取的图像越小，模糊细节越少，但性能更好。
     *
     * @param downSampleFactor 图像缩小样本因数，因数越大，选取的图像越小，模糊细节越少，但性能更好
     */
    public void setDownSampleFactor(int downSampleFactor) {
        if (downSampleFactor < 1 || downSampleFactor > 64) {
            throw new IllegalArgumentException(
                    "the downSampleFactor must be (1 <= downSampleFactor <= 64), current is " + downSampleFactor);
        }
        mDownSampleFactor = downSampleFactor;
        mScale = 1f / mDownSampleFactor;
    }

    public int getOverlayColor() {
        return mOverlayColor;
    }

    public void setOverlayColor(int overlayColor) {
        mOverlayColor = overlayColor;
    }

    /**
     * 获取目标视图的图像，进行模糊处理，并将模糊好的图像设置为此视图的背景
     */
    public void blur() {
        if (mToBlurView == null) {
            blurBackground(null);
        } else {
            startBlur();
        }

    }

    /**
     * 获取目标视图的图像，进行模糊处理，并将模糊好的图像设置为此视图的背景
     *
     * @param blurRadius       模糊半径
     * @param downSampleFactor 缩小样本因数
     * @param overlayColor     覆盖层颜色
     */
    public void blur(int blurRadius, int downSampleFactor, int overlayColor) {
        setBlurRadius(blurRadius);
        setDownSampleFactor(downSampleFactor);
        setOverlayColor(overlayColor);
        blur();
    }

    protected void startBlur() {
        if (!isWorking.get() && mToBlurView.getWidth() != 0 && mToBlurView.getHeight() != 0) {
            isWorking.compareAndSet(false, true);
            Bitmap bitmap = doBlur();
            blurBackground(bitmap);
            isWorking.compareAndSet(true, false);
        }
    }

    /**
     * 进行模糊处理
     *
     * @return 已经进行模糊的图像
     */
    protected Bitmap doBlur() {
        Bitmap desBitmap = getSampleBitmap();
        // 模糊半径为0时，不进行模糊处理
        if (mBlurRadius != 0) {
            desBitmap = getBlurredBitmap(desBitmap);
        }
        return desBitmap;
    }

    /**
     * 获取视图的样本图像
     *
     * @return
     */
    protected Bitmap getSampleBitmap() {
        int viewWidth = mToBlurView.getWidth();
        int viewHeight = mToBlurView.getHeight();
        Canvas canvas = new Canvas();
        int downWidth, downHeight;
        if (mDownSampleFactor > 1) {
            downWidth = (int) (viewWidth * mScale + 0.5f);
            downHeight = (int) (viewHeight * mScale + 0.5f);
            canvas.scale(mScale, mScale); // 缩小画布
        } else {
            downWidth = viewWidth;
            downHeight = viewHeight;
        }
        if (downWidth == 0 || downHeight == 0) {
            return null;
        }
        Bitmap sampleBitmap = Bitmap.createBitmap(downWidth, downHeight, Bitmap.Config.ARGB_8888);
        canvas.setBitmap(sampleBitmap);
        // TODO 背景方面有问题
        mToBlurView.draw(canvas); // 将需要模糊的视图的画面画在画布上
        canvas.drawColor(mOverlayColor); // 画覆盖层
        return sampleBitmap;
    }

    /**
     * 获取视图样本的已模糊图像
     *
     * @param toBlurBitmap 需要被模糊的图像
     * @return
     */
    protected Bitmap getBlurredBitmap(Bitmap toBlurBitmap) {
        if (toBlurBitmap == null) {
            return null;
        }
        final Allocation input = Allocation.createFromBitmap(mRenderScript, toBlurBitmap,
                Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);
        final Allocation output = Allocation.createTyped(mRenderScript, input.getType());
        mBlurScript.setRadius(mBlurRadius);
        mBlurScript.setInput(input);
        mBlurScript.forEach(output);
        output.copyTo(toBlurBitmap);
        return toBlurBitmap;
    }

    /**
     * 将处理好的图像设置为视图的背景
     * @param bitmap
     */
    protected void blurBackground(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            if (bitmap == null) {
                setBackgroundDrawable(null);
            } else {
                setBackgroundDrawable(new BitmapDrawable(getResources(), bitmap));
            }
        } else {
            if (bitmap == null) {
                setBackground(null);
            } else {
                setBackground(new BitmapDrawable(getResources(), bitmap));
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        destroyRenderScript();
    }

}
