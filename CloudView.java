
/**
 * @author Kanghee Lee Created on 13-02-10
 */

public class CloudView extends View {

    private Bitmap mBitmap;
    private static final int NUM_CLOUDS = 4; // Current number of clouds
    ArrayList<Texture> clouds = new ArrayList<Texture>(NUM_CLOUDS); // List of
                                                                    // current
    
    // Animator used to drive all separate cloud animations. Rather than have
    // potentially hundreds of separate animators, we just use one and then update all
    // clouds for each frame of that single animation.
	
    ValueAnimator animator = ValueAnimator.ofFloat(0, 1);

    private Random mRandom;
    private ViewPager mPager;
    private Context mContext;

    private long mPrevTime; // Used to track elapsed time for
                            // animations and fps
    private int mMaxNum;
    private boolean mIsPlaying;
    private Matrix mMatrix = new Matrix();
    
    private int mBitmapWidth;

    public CloudView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mRandom = new Random();
        if (android.os.Build.VERSION.SDK_INT >= 11)
            setLayerType(LAYER_TYPE_HARDWARE, null);

        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.cloud);
        mBitmapWidth = mBitmap.getWidth();
        // This listener is where the action is for the flak animations. Every
        // frame of the animation, we calculate the elapsed time and update every cloud's
        // position and rotation according to its speed.
        animator.addUpdateListener(new AnimatorUpdateListener() {
            Texture cloud;
            float secs;
            long nowTime;

            @Override
            public void onAnimationUpdate(ValueAnimator arg0) {
                nowTime = System.currentTimeMillis();
                secs = (float) (nowTime - mPrevTime) * 0.01f;
                mPrevTime = nowTime;
                if (clouds.size() < mMaxNum) {
                    if (mRandom.nextInt(6) == 4) {
                        createCloud();
                    }
                }
                for (int i = 0; i < clouds.size(); i++) {
                    cloud = clouds.get(i);
                    cloud.x -= (cloud.speed * secs);
                    if (cloud.x + mBitmapWidth < 0) {
                        // If a flake falls off the bottom, send it back to the
                        // top
                        clouds.remove(cloud);
                    }
                }
                // Force a redraw to see the clouds in their new positions and
                // orientations
                invalidate();
            }
        });

        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setDuration(10000);
        animator.setInterpolator(new AccelerateInterpolator());
    }

    private void createCloud() {
        int count = mPager.getChildCount() - 1;
        View lastView = mPager.getChildAt(count);
        if (lastView != null) {
            Texture cloud = Texture.createTexture(mBitmap,
                    lastView.getRight() + mBitmapWidth,
                    DisplayUtils.convertFromDP(mContext, 5.0f + mRandom.nextFloat() * 40.0f),
                    DisplayUtils.convertFromDP(mContext, mRandom.nextFloat() * 2.0f + 1.0f));
            clouds.add(cloud);
            cloud = null;
        } else
            return;
    }

    public void createCloud(float x) {
        Texture cloud = Texture.createTexture(mBitmap, x + mBitmapWidth,
                DisplayUtils.convertFromDP(mContext, 5.0f + mRandom.nextFloat() * 40.0f),
                DisplayUtils.convertFromDP(mContext, mRandom.nextFloat() * 2.0f + 0.5f));
        clouds.add(cloud);
        cloud = null;
    }

    public void setViewPager(ViewPager pager) {
        mPager = pager;
    }

    public void setMaxNum(int num) {
        mMaxNum = num;
    }

    public void setBitmap(int resId) {
        mBitmap = BitmapFactory.decodeResource(getResources(), resId);
    }

    public int getBitmapWidth() {
        return mBitmapWidth;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mIsPlaying) {
            for (Texture cloud : clouds) {
                mMatrix.setTranslate(
                        cloud.x - mBitmapWidth - mPager.getScrollX() * 0.3f, cloud.y);
                canvas.drawBitmap(mBitmap, mMatrix, null);
            }
        }
    }

    public void pause() {
        if (mIsPlaying)
            animator.cancel();
        mIsPlaying = false;
    }

    public void resume() {
        if (!mIsPlaying)
            animator.start();
        mIsPlaying = true;
    }

}
