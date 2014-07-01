package com.sobremesa.birdwatching.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Created by omegatai on 2014-06-20.
 */
public class SquareFrameLayout extends FrameLayout {

    public SquareFrameLayout(Context context)
    {
        super(context);
    }


    public SquareFrameLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public SquareFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }

}
