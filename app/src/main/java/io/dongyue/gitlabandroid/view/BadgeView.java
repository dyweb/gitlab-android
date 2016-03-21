package io.dongyue.gitlabandroid.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import io.dongyue.gitlabandroid.R;

/**
 * Created by Brotherjing on 2015/8/3.
 */
public class BadgeView extends View {

    private int badgeColor;
    private int badgeSize;
    private int badgeNum;
    private boolean showBackground;

    private Paint mPaint;
    private TextPaint mTextPaint;

    public BadgeView(Context context) {
        //super(context);
        this(context,null);
    }

    public BadgeView(Context context, AttributeSet attrs) {
        //super(context, attrs);
        this(context,attrs,0);
    }

    public BadgeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.BadgeView);
        int n = ta.getIndexCount();
        for(int i=0;i<n;++i){
            int attr = ta.getIndex(i);
            switch (attr){
                case R.styleable.BadgeView_mcolor:
                    badgeColor = ta.getColor(attr, Color.RED);
                    break;
                case R.styleable.BadgeView_show_background:
                    showBackground=ta.getBoolean(attr,true);
                default:
                    break;
            }
        }
        ta.recycle();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(badgeColor);
        mTextPaint = new TextPaint();
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        badgeNum = 0;
        badgeSize = 0;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        badgeSize = Math.min(width,height);
        mTextPaint.setTextSize(badgeSize / 2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //Log.i("yj","badgesize is "+badgeSize+" badgenum is "+badgeNum );
        if(badgeSize==0||badgeNum==0)return;
        if(showBackground)canvas.drawCircle(badgeSize / 2, badgeSize / 2, badgeSize / 2, mPaint);
        Paint.FontMetricsInt fontMetrics = mTextPaint.getFontMetricsInt();
        int baseline =  (badgeSize - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;

        if(badgeNum>99)canvas.drawText("99+",badgeSize/2,baseline,mTextPaint);
        else canvas.drawText(badgeNum+"",badgeSize/2,baseline,mTextPaint);
    }

    public void setBadgeNum(int number){
        badgeNum = number;
        postInvalidate();
    }
}
