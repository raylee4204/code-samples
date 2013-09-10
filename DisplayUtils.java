
/**
 * @author Kanghee Lee Created on 12-09-24
 */

public class DisplayUtils {

    private static final String TAG = DisplayUtils.class.getSimpleName();
  
    public static final String CHUNK_FIVE_FONT = "chunkfive.otf";
    public static final String HELVETICA_STD = "helvetica_neue_std.otf";
    public static final String HELVETICA_BLD = "helvetica_neue_std_bdcn.otf";

    public static Bitmap getCircularBitmap(Bitmap sourceBitmap) {
        int targetSize = Math.min(sourceBitmap.getWidth(), sourceBitmap.getHeight());
        Bitmap targetBitmap = Bitmap.createBitmap(targetSize, targetSize, Config.ARGB_8888);

        float r = targetSize * 0.5f;

        Canvas canvas = new Canvas(targetBitmap);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, targetSize, targetSize);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(r, r, r, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(sourceBitmap, rect, rect, paint);
        return targetBitmap;
    }

    public static Bitmap getBannerBitmap(Context context, Bitmap bitmap) {

        Bitmap targetBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(targetBitmap);

        BitmapShader shader;
        shader = new BitmapShader(targetBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(shader);

        float radius = context.getResources().getDimension(R.dimen.radius);

        Path path = new Path();
        path.moveTo(radius, 0.0f);
        path.lineTo(bitmap.getWidth() - radius, 0.0f);
        path.arcTo(new RectF(bitmap.getWidth() - radius, 0.0f, bitmap.getWidth(), radius), 270, 90);
        path.lineTo(bitmap.getWidth(), bitmap.getHeight());
        path.lineTo(0.0f, bitmap.getHeight());
        path.lineTo(0.0f, radius);
        path.arcTo(new RectF(0.0f, 0.0f, radius, radius), 180, 90);
        path.lineTo(radius, 0.0f);

        canvas.clipPath(path);

        Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return targetBitmap;
    }

    public static Bitmap getRoundedCornerBitmap(Context context, Bitmap bitmap) {
        Bitmap targetBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(),
                Bitmap.Config.ARGB_8888);

        float radius = context.getResources().getDimension(R.dimen.radius);

        Canvas canvas = new Canvas(targetBitmap);

        BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(shader);

        RectF rect = new RectF(0.0f, 0.0f, bitmap.getWidth(), bitmap.getHeight());

        // rect contains the bounds of the shape
        // radius is the radius in pixels of the rounded corners
        // paint contains the shader that will texture the shape
        canvas.drawRoundRect(rect, radius, radius, paint);

        return targetBitmap;
    }

    public static Drawable drawCircle(Context context, int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Drawable imageDrawable = new BitmapDrawable(context.getResources(), bitmap);

        Canvas canvas = new Canvas(bitmap);
        float size = width * 0.5f;

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(context.getResources().getColor(R.color.bright_bluegreen));

        canvas.drawCircle(size, size, size, paint);

        imageDrawable.draw(canvas);

        return imageDrawable;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static void disableHardwareAcceleration(View view) {
        if (android.os.Build.VERSION.SDK_INT >= 11) {
            view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
    }

    public static void loadImage(ImageView imageView, String imageURL, int placeholderId,
            int width, int height) {
        Options bOptions = new Options();
        bOptions.outWidth = width;
        bOptions.outWidth = height;

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showStubImage(placeholderId).showImageForEmptyUri(placeholderId)
                .imageScaleType(ImageScaleType.EXACTLY).cacheInMemory(true).cacheOnDisc(true)
                .decodingOptions(bOptions).build();
        mImageLoader.displayImage(imageURL, imageView, options, mDisplayListener);
    }

    public static void showToastOnUIThread(final Activity activity, final String message) {
        activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static float convertFromDP(Context context, float value) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, context.getResources()
                .getDisplayMetrics());
    }

    public static float convertFromSP(Context context, float value) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, value, context.getResources()
                .getDisplayMetrics());
    }

    public static void showProgressDialogOnUiThread(Activity activity, String text) {
        final ProgressDialog mProgressDialog = new ProgressDialog(activity);
        mProgressDialog.setMessage(text);
        activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                mProgressDialog.show();
            }
        });
    }

    public static SpannableString changeFont(Context context, CharSequence title, String fontName) {
        SpannableString str = new SpannableString(title);
        str.setSpan(new TypefaceSpan(context, fontName), 0, str.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return str;
    }

}
