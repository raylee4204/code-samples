
/**
 * @author Kanghee Lee Created on 13-01-10
 */

public class CustomViewPager extends ViewPager {

    private ScrollingBitmap mGrassBitmap;
    private ScrollingBitmap mFieldBitmap;

    private float mLastMotionX, mLastMotionY;
    private float yDiff;
    private boolean disableSwipe;
    
    public CustomViewPager(Context context) {
        super(context);
        disableSwipe = false;
    }

    public CustomViewPager(Context context, AttributeSet attr) {
        super(context, attr);
        disableSwipe = false;
    }

    public void setFieldBitmap(ScrollingBitmap field) {
        mFieldBitmap = field;
    }

    public void setGrassBitmap(ScrollingBitmap grass) {
        mGrassBitmap = grass;
    }

    public void disableSwipe() {
        disableSwipe = true;
    }
    
    public void next() {
        setCurrentItem(getCurrentItem() + 1, true);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (disableSwipe)
                    mLastMotionX = ev.getX();
                else
                    mLastMotionY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (disableSwipe) {
                    final float x = ev.getX();
                    final float deltaX = mLastMotionX - x;
                    if (deltaX > 0)
                        return false;
                } else {
                    final float y = ev.getY();
                    final float deltaY = mLastMotionY - y;
                    yDiff = Math.abs(deltaY);
                    if (yDiff > 50)
                        getParent().requestDisallowInterceptTouchEvent(false);
                    else
                        getParent().requestDisallowInterceptTouchEvent(true);
                }
                break;
            case MotionEvent.ACTION_UP:
                getParent().requestDisallowInterceptTouchEvent(false);
                break;
            default:
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastMotionX = ev.getX();
                if (yDiff < 50)
                    getParent().requestDisallowInterceptTouchEvent(true);
                break;

            case MotionEvent.ACTION_MOVE:
                if (disableSwipe) {
                    final float x = ev.getX();
                    final float deltaX = mLastMotionX - x;
                    if (deltaX > 0)
                        return false;
                }
                break;
            case MotionEvent.ACTION_UP:
                getParent().requestDisallowInterceptTouchEvent(false);
                break;
            default:
                break;
        }
        return super.onTouchEvent(ev);
    }

    @Override
    public void draw(Canvas canvas) {
        if (!isInEditMode()) {
            mFieldBitmap.draw(canvas, getScrollX());
            super.draw(canvas);
            mGrassBitmap.draw(canvas, getScrollX());
        }
    }
}
