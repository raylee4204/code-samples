
/**
 * @author Ray Created on 2013-06-05
 */

public class LoadingAnimator implements Runnable {

    private static final int REFRESH_RATE = 25; // milliseconds

    private ImageView mImageView;
    private int indexFrame = 0;

    private Handler mHandler;
    private ArrayList<Bitmap> mBitmaps;
    private int size;

    public LoadingAnimator(ImageView imageView) {
        mHandler = new Handler();
        mImageView = imageView;
        mBitmaps = SofitApplication.getLoadingBitmaps();
        size = mBitmaps.size();
    }

    @Override
    public void run() {
        mImageView.setImageBitmap(mBitmaps.get(indexFrame));
        indexFrame = (indexFrame + 1) % size;
        mHandler.postDelayed(this, REFRESH_RATE);
    }

    public void animate() {
        if (mBitmaps.isEmpty()) {
            Log.e(LoadingAnimator.class.toString(), "Images not loaded");
            return;
        } else if (mImageView == null) {
            Log.e(LoadingAnimator.class.toString(), "ImageView hasn't been set");
            return;
        }
        if (mHandler == null) {
            mHandler = new Handler();
            mHandler.post(this);
        } else {
            mHandler.removeCallbacks(this);
            mHandler.post(this);
        }
    }

    public void cancel() {
        if (mHandler != null) {
            mHandler.removeCallbacks(this);
            mHandler = null;
        }
        indexFrame = 0;
    }
}
