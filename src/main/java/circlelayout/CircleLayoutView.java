package circlelayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.licheng.github.circlelayoutview.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by licheng on 19/1/16.
 */
public class CircleLayoutView extends ViewGroup {
    private float angle;
    private float angleOffset;
    private int radius;
    private int centerViewId;
    private int direction;
    private int presentsytle;

    public CircleLayoutView(Context context) {
        super(context);
    }

    public CircleLayoutView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CircleLayoutView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CircleLayoutView, defStyleAttr, 0);
        centerViewId = array.getResourceId(R.styleable.CircleLayoutView_centerViewId, View.NO_ID);
        angle = (float) Math.toRadians(array.getFloat(R.styleable.CircleLayoutView_angle,0));
        angleOffset = (float) Math.toRadians(array.getFloat(R.styleable.CircleLayoutView_angleOffset,0));
        radius = array.getDimensionPixelSize(R.styleable.CircleLayoutView_radius,0);
        presentsytle = array.getInt(R.styleable.CircleLayoutView_circlePresentStyle,0);
        direction = array.getInt(R.styleable.CircleLayoutView_direction,-1);
        array.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //测量所有孩子的view大小
        measureChildren(widthMeasureSpec,heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int displayAreaLeft = getLeft() + getPaddingLeft();
        int displayAreaTop = getTop() + getPaddingTop();
        int displayAreaRight = getRight() - getPaddingRight();
        int displayAreaButtom = getBottom() - getPaddingBottom();

        Log.i("测量主视图参数：",displayAreaTop +" "+displayAreaRight+" "+displayAreaButtom+" "+displayAreaLeft);

        //获取当前视图的高度和宽度
        int displayWidth = displayAreaRight - displayAreaLeft;
        int dispalyHeight = displayAreaButtom - displayAreaTop;
        //获取圆心横纵坐标
        int centerX = displayWidth / 2 + getPaddingLeft();
        int centerY = dispalyHeight / 2 + getPaddingTop();

        //获取园的半径
        int outerRadius = Math.min(dispalyHeight,displayWidth) / 2;

        //获取中心view 布局到圆心位置
        View centerView = this.findViewById(R.id.centerView);

        if(centerView != null){
            ViewUtils.layoutFromCenter(centerView, centerX, centerY);
//            int left = centerX - centerView.getMeasuredWidth() / 2;
//            int top = centerY - centerView.getMeasuredHeight() / 2;
//            int right = centerX + centerView.getMeasuredWidth() / 2;
//            int buttom = centerY +centerView.getMeasuredHeight() / 2;
//            Log.i("园的布局坐标：",left +" "+top+" "+right+" "+buttom);
//            centerView.layout(left,top,right,buttom);
        }else{
            Log.i("错误","获取视图失败");
        }

        //取得各个child的半径 分别得到最小半径和最大半径
        int childCount = getChildCount();
//        View[] childViews = new View[childCount];
        List<View> childViews = new ArrayList<>();
        int minRaidus = outerRadius;
        int maxRaidus = 0;

        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            //不能把圆中心view加进去
            if(child != null && child.getVisibility() != GONE &&
                    (child.getId() != R.id.centerView || child.getId() == View.NO_ID)){

                childViews.add(child);
                //获取child的半径
                int childRadius = Math.max(child.getMeasuredHeight(),child.getMeasuredWidth()) / 2;
                if(childRadius > maxRaidus){
                    maxRaidus = childRadius;
                }
                if(childRadius < minRaidus){
                    minRaidus = childRadius;
                }
            }
        }

        int childSize = childViews.size();

        Log.i("child总数",childSize+"");

        int layoutRadius = radius;
        if(layoutRadius == 0){
            if(presentsytle == 0){
                layoutRadius = outerRadius - maxRaidus;
            }
            //更大的半径
            if(presentsytle == 1){
                layoutRadius = outerRadius - minRaidus;
            }
        }

        //获得平均弧度
        float angleIncrement = angle;
        if(angleIncrement == 0){
            angleIncrement = getEqualAngle(childSize);
            Log.i("圆弧平均值",angleIncrement+" ");
        }

        //存放当前childview的弧度
        float angleCurrent = angleIncrement;

        for (int i = 0; i < childSize; i++) {
            View child = childViews.get(i);
            if(child == null){
                continue;
            }
            if(i % 2 == 0){

            }else{

            }
            Log.i("child圆弧值",angleCurrent+"");

            //获得child的横纵坐标位置
            int childCenterX = (int) (Math.cos(angleCurrent) * layoutRadius + centerX);
            int childCenterY = (int) (centerY - Math.sin(angleCurrent) * layoutRadius);
            //child位置布局
            ViewUtils.layoutFromCenter(child,childCenterX,childCenterY);
            angleCurrent += angleIncrement;
        }
    }

    private float getEqualAngle(int n) {
        if (n == 0) {
            n = 1;
        }
        return 2 * (float) Math.PI / n;
    }
}
