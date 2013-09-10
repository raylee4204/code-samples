
/**
 * @author Kanghee Created on 2013-04-04
 */

public class AutoStateImageView extends ImageView {

    public AutoStateImageView(Context context) {
        super(context);
    }

    public AutoStateImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoStateImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setImageResource(int resId) {
        // Replace the original background drawable (e.g. image) with a
        // LayerDrawable that
        // contains the original drawable.
        Drawable d = getResources().getDrawable(resId);
        AutoBgButtonBackgroundDrawable layer = new AutoBgButtonBackgroundDrawable(d);
        super.setImageDrawable(layer);
    }

    @Override
    public void setImageBitmap(Bitmap bitmap) {
        // Replace the original background drawable (e.g. image) with a
        // LayerDrawable that
        // contains the original drawable.
        Drawable d = new BitmapDrawable(getResources(), bitmap);
        AutoBgButtonBackgroundDrawable layer = new AutoBgButtonBackgroundDrawable(d);
        super.setImageDrawable(layer);
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        // TODO Auto-generated method stub
        AutoBgButtonBackgroundDrawable layer = new AutoBgButtonBackgroundDrawable(drawable);
        super.setImageDrawable(layer);
    }

    @Override
    public void setBackgroundDrawable(Drawable d) {
        // TODO Auto-generated method stub
        AutoBgButtonBackgroundDrawable layer = new AutoBgButtonBackgroundDrawable(d);
        super.setBackgroundDrawable(layer);
    }

    /**
     * The stateful LayerDrawable used by this button.
     */
    protected class AutoBgButtonBackgroundDrawable extends LayerDrawable {

        // The color filter to apply when the button is pressed
        protected ColorFilter pressedFilter = new LightingColorFilter(
                Color.parseColor("#CCBCBCBC"), 1);
        protected ColorFilter focusedFilter = new LightingColorFilter(
                Color.parseColor("#88BCBCBC"), 1);

        // Alpha value when the button is disabled
        protected int disabledAlpha = 85;
        // Alpha value when the button is enabled
        protected int transparent = 255;

        public AutoBgButtonBackgroundDrawable(Drawable d) {
            super(new Drawable[] {
                d
            });
        }

        @Override
        protected boolean onStateChange(int[] states) {
            boolean enabled = false;
            boolean pressed = false;
            boolean focused = false;
            for (int state : states) {
                if (state == android.R.attr.state_enabled)
                    enabled = true;
                else if (state == android.R.attr.state_pressed)
                    pressed = true;
                else if (state == android.R.attr.state_focused)
                    focused = true;
            }

            mutate();
            if (enabled && pressed) {
                setColorFilter(pressedFilter);
            } else if (enabled && focused) {
                setColorFilter(focusedFilter);
            } else if (!enabled) {
                setColorFilter(null);
                setAlpha(disabledAlpha);
            } else {
                setColorFilter(null);
                setAlpha(transparent);
            }

            invalidateSelf();

            return super.onStateChange(states);
        }

        @Override
        public boolean isStateful() {
            return true;
        }
    }
}
