package com.licheedev.blurview;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;

/**
 * 部分覆盖高斯模糊
 * Created by John on 2015/7/18.
 */
public class PartBlurView extends BlurView {

    public PartBlurView(Context context) {
        super(context);
    }

    public PartBlurView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PartBlurView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected Bitmap doBlur() {
        Bitmap desBitmap = getSampleBitmap();
        desBitmap = getCroppedBitmap(desBitmap);
        // 模糊半径为0时，不进行模糊处理
        if (mBlurRadius != 0) {
            desBitmap = getBlurredBitmap(desBitmap);
        }
        return desBitmap;
    }

    /**
     * 截取被此视图覆盖掉的那一部分的图像
     *
     * @param toCropBitmap 被此视图覆盖掉的那一部分的图像
     * @return
     */
    protected Bitmap getCroppedBitmap(Bitmap toCropBitmap) {
        
        if (toCropBitmap == null) {
            return null;
        }
        int viewLeft = mToBlurView.getLeft();
        int viewTop = mToBlurView.getTop();
        int cropLeft, cropTop, cropWidth, cropHeight;
        if (mDownSampleFactor > 1) {
            cropLeft = (int) ((Math.abs(viewLeft - getLeft())) * mScale + 0.5f);
            cropTop = (int) ((Math.abs(viewTop - getTop())) * mScale + 0.5f);
            cropWidth = (int) ((getWidth() * mScale) + 0.5f);
            cropHeight = (int) (getHeight() * mScale + 0.5f);
        } else {
            cropLeft = Math.abs(getLeft() - viewLeft);
            cropTop = Math.abs(viewTop - getTop());
            cropWidth = getWidth();
            cropHeight = getHeight();
        }
        if (cropWidth == 0 || cropHeight == 0) {
            return null;
        }
        Bitmap croppedBitmap = Bitmap
                .createBitmap(toCropBitmap, cropLeft, cropTop, cropWidth, cropHeight);
        toCropBitmap.recycle();
        return croppedBitmap;
    }
}
