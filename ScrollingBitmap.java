
/**
 * @author Kanghee Lee Created on 13-02-04
 */

public class ScrollingBitmap {

    protected Bitmap mBitmap;
    protected float mScrollSpeed;
    protected int mWidth;
    protected int mHeight;
    protected int mScreenWidth;
    protected int mScreenHeight;

    protected Rect src;
    protected Rect dst;
    Rect src2;
    Rect dst2;

    int srcRightPos;
    int srcMultiplier;

    protected Paint mPaint;

    public ScrollingBitmap(Bitmap bitmap, float scrollSpeed, int width, int height) {

        mBitmap = bitmap;
        mWidth = bitmap.getWidth();
        mHeight = bitmap.getHeight();

        mScrollSpeed = scrollSpeed;

        src = new Rect();
        src2 = new Rect();
        dst = new Rect();
        dst2 = new Rect();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        
        mScreenWidth = width;
        mScreenHeight = height;
    }
    
    public void draw(Canvas canvas, int getScrollX) {
        srcRightPos = (int) ((getScrollX * mScrollSpeed) + mScreenWidth);
        srcMultiplier = srcRightPos / mWidth;
        if (srcRightPos >= mWidth) {
            int temp2 = srcRightPos - (mWidth * srcMultiplier);
            if (temp2 < mScreenWidth) {
                src.set((int) (getScrollX * mScrollSpeed) - (mWidth * (srcMultiplier - 1)), 0,
                        mWidth, mHeight);
                src2.set(0, 0, temp2, mHeight);
                dst.set(getScrollX, mScreenHeight - mHeight, getScrollX+mScreenWidth-temp2, mScreenHeight);
                dst2.set(getScrollX+mScreenWidth-temp2, mScreenHeight - mHeight, getScrollX+mScreenWidth, mScreenHeight);
                canvas.drawBitmap(mBitmap, src, dst, mPaint);
                canvas.drawBitmap(mBitmap, src2, dst2, mPaint);
            } else {
                src.set(temp2 - mScreenWidth, 0, temp2, mHeight);
                dst.set(getScrollX, mScreenHeight - mHeight, getScrollX + mScreenWidth,
                        mScreenHeight);
                canvas.drawBitmap(mBitmap, src, dst, mPaint);
            }
        } else {
            dst.set(getScrollX, mScreenHeight - mHeight, getScrollX + mScreenWidth, mScreenHeight);
            src.set((int) (getScrollX * mScrollSpeed), 0, srcRightPos, mHeight);
            canvas.drawBitmap(mBitmap, src, dst, mPaint);
        }

    }

}
