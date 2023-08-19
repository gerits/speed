package be.rubengerits.speedview;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.core.content.ContextCompat;

import be.rubengerits.speedview.utils.BoundaryRectangle;
import be.rubengerits.speedview.utils.DisplayUtils;
import be.rubengerits.speedview.utils.TypeFaceRepository;

public class SpeedView extends View implements ValueAnimator.AnimatorUpdateListener, ValueAnimator.AnimatorListener {

    public static final float PADDING = 16f; // dp
    public static final float SPEED_ANGLE = 260f;
    public static final float NEEDLE_RADIUS = 16f;
    public static final int DEFAULT_ANIMATION_DURATION = 500;
    /* Inner Values */
    private final RectF innerCircle = new RectF();
    /* Paint */
    private Paint majorPaint;
    private Paint middlePaint;
    private Paint minorPaint;
    private Paint needlePaint;
    private Paint textPaint;
    /* Colors */
    private int colorPrimary;
    private int colorPrimaryDark;
    private int colorAccent;
    /* Settings */
    private float value = 0f;
    private float maxValue = 0f;
    private SpeedUnit locality = SpeedUnit.metric;

    private boolean animationActive = false;
    private int size;

    private BoundaryRectangle boundaries = new BoundaryRectangle();

    private Bitmap bitmap;
    private Canvas virtualCanvas;

    public SpeedView(Context context) {
        this(context, null);
    }

    public SpeedView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SpeedView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SpeedView, 0, 0);
        try {
            int defaultColor = ContextCompat.getColor(getContext(), android.R.color.black);
            colorPrimary = a.getColor(R.styleable.SpeedView_speedColorPrimary, defaultColor);
            colorPrimaryDark = a.getColor(R.styleable.SpeedView_speedColorPrimaryDark, defaultColor);
            colorAccent = a.getColor(R.styleable.SpeedView_speedColorAccent, defaultColor);
        } finally {
            a.recycle();
        }

        setupPaint();
    }

    private void setupPaint() {
        // major paint
        majorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        majorPaint.setColor(colorPrimary);
        majorPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        majorPaint.setStrokeCap(Paint.Cap.ROUND);
        majorPaint.setStrokeWidth(DisplayUtils.convertDpToPixels(getContext(), 4));
        majorPaint.setStrokeJoin(Paint.Join.ROUND);

        // middle paint
        middlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        middlePaint.setColor(colorPrimaryDark);
        middlePaint.setStyle(Paint.Style.STROKE);
        middlePaint.setStrokeCap(Paint.Cap.ROUND);
        middlePaint.setStrokeWidth(DisplayUtils.convertDpToPixels(getContext(), 4));

        // minor paint
        minorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        minorPaint.setColor(colorPrimaryDark);
        minorPaint.setStyle(Paint.Style.STROKE);
        minorPaint.setStrokeCap(Paint.Cap.ROUND);
        minorPaint.setStrokeWidth(DisplayUtils.convertDpToPixels(getContext(), 2));

        // needle paint
        needlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        needlePaint.setColor(colorAccent);
        needlePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        needlePaint.setStrokeWidth(DisplayUtils.convertDpToPixels(getContext(), 4));
        needlePaint.setStrokeCap(Paint.Cap.ROUND);
        needlePaint.setStrokeJoin(Paint.Join.ROUND);
        setLayerType(LAYER_TYPE_SOFTWARE, needlePaint);

        // text paint
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(colorAccent);
        textPaint.setTextSize(DisplayUtils.convertSpToPixels(getContext(), 16));
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTypeface(TypeFaceRepository.MONOSPACE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        boolean cropped = boundaries.isSet();

        boundaries.clear();

        virtualCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        if (maxValue > 0 && locality != null) {
            drawUnits(virtualCanvas);
            drawNeedle(virtualCanvas);
        } else {
            drawErrorMessage(virtualCanvas);
        }

        int padding = (int) DisplayUtils.convertDpToPixels(getContext(), PADDING);
        boundaries.validateXMin(boundaries.getXMin() - padding);
        boundaries.validateYMin(boundaries.getYMin() - padding);
        boundaries.validateXMax(boundaries.getXMax() + padding);
        boundaries.validateYMax(boundaries.getYMax() + padding);

        Bitmap tmpBitmap = this.bitmap;
        if (!cropped) {
            tmpBitmap = cropBitmapToBoundaries(this.bitmap, boundaries);
            virtualCanvas = new Canvas(this.bitmap);
            virtualCanvas.save();
        }

        int x = (getMeasuredWidth() - tmpBitmap.getWidth()) / 2;
        int y = (getMeasuredHeight() - tmpBitmap.getHeight()) / 2;

        canvas.drawBitmap(tmpBitmap, x, y, null);
    }

    private void drawNeedle(Canvas c) {
        float handleOuterRadius = DisplayUtils.convertDpToPixels(getContext(), NEEDLE_RADIUS);

        innerCircle.set(size / 2 - handleOuterRadius, size / 2 - handleOuterRadius, size / 2 + handleOuterRadius, size / 2 + handleOuterRadius);

        float angle = speedToAngle(Math.min(convertMeterToLocalValue(value, 4), convertMeterToLocalValue(maxValue, 2)));

        Path path = new Path();
        path.arcTo(innerCircle, (angle + 150), 340);
        Point pointForNeedle = findPointOnCircle((size / 2) - DisplayUtils.convertDpToPixels(getContext(), PADDING + 4), angle);
        path.lineTo(pointForNeedle.x, pointForNeedle.y);
        path.close();

        c.drawPath(path, needlePaint);
    }

    private Bitmap cropBitmapToBoundaries(Bitmap sourceBitmap, BoundaryRectangle boundaryRectangle) {
        int minX = boundaryRectangle.getXMin();
        int minY = boundaryRectangle.getYMin();
        int maxX = boundaryRectangle.getXMax();
        int maxY = boundaryRectangle.getYMax();

        return Bitmap.createBitmap(sourceBitmap, minX, minY, (maxX - minX) + 1, (maxY - minY) + 1);
    }

    private Point findPointOnCircle(float radius, float angle) {
        Point p = new Point();

        float offset = size / 2;
        float actualAngle = calculateActualAngle(angle);

        int maxX = (int) (offset + (radius * Math.cos(actualAngle)));
        int maxY = (int) (offset + (radius * Math.sin(actualAngle)));

        p.set(maxX, maxY);

        return p;
    }

    private void drawUnits(Canvas canvas) {
        int width = size;
        int height = size;

        float radius = (width / 2) - DisplayUtils.convertDpToPixels(getContext(), PADDING);
        double density = calculateDensity(radius);
        double minDensity = DisplayUtils.convertDpToPixels(getContext(), 8);

        float speed = Math.min(convertMeterToLocalValue(value, 0), convertMeterToLocalValue(getMaxValue(), 0));

        for (float fraction = 0; fraction <= convertMeterToLocalValue(maxValue, 2); fraction += 1) {
            Paint paint;
            float speedDistance = -1;
            float size = 0;

            if (fraction % 10 == 0) {
                paint = majorPaint;
                size = DisplayUtils.convertDpToPixels(getContext(), 16);

                speedDistance = Math.max(Math.abs(speed - fraction) - 10, 0);
            } else if (fraction % 5 == 0) {
                if (density > minDensity /*&& !animationActive*/) {
                    paint = majorPaint;
                } else {
                    paint = minorPaint;
                }
                size = DisplayUtils.convertDpToPixels(getContext(), 8);
                speedDistance = Math.max(Math.abs(speed - fraction) - 5, 0);
            } else {
                paint = minorPaint;
                if (density > minDensity /*&& !animationActive*/) {
                    size = DisplayUtils.convertDpToPixels(getContext(), 4);
                }
            }

            if (paint != null && size > 0) {
                drawSegment(canvas, radius, size, speedToAngle(fraction), speedDistance, paint);

                if (fraction % 10 == 0) {
                    drawLabel(canvas, fraction);
                }
            }
        }

        RectF innerArc = new RectF(width * 0.25f, height * 0.25f, width * 0.75f, height * 0.75f);
        canvas.drawArc(innerArc, 140f, SPEED_ANGLE, false, middlePaint);
    }

    private void drawLabel(Canvas canvas, float fraction) {
        float spacing = DisplayUtils.convertDpToPixels(getContext(), 48f);
        float angle = speedToAngle(fraction);

        Point pointOnCircle = findPointOnCircle(size / 2 - spacing, angle);

        canvas.drawText(String.valueOf(Math.round(fraction)), pointOnCircle.x, (float) pointOnCircle.y + (textPaint.getTextSize() / 2), textPaint);
    }

    private float speedToAngle(float speed) {
        return (speed / convertMeterToLocalValue(maxValue, 2)) * SPEED_ANGLE;
    }

    double calculateDensity(float radius) {
        Point min1 = findPointOnCircle(radius, speedToAngle(1f));
        Point min2 = findPointOnCircle(radius, speedToAngle(2f));

        float dx = calculateDistance(min1.x, min2.x);
        float dy = calculateDistance(min1.y, min2.y);

        return Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
    }

    private void drawSegment(Canvas c, float radius, float length, float angle, float speedDistance, Paint paint) {
        float calculatedDistance = DisplayUtils.convertDpToPixels(getContext(), Math.max(0, 20 - speedDistance)) / 4;
        Point min = findPointOnCircle(radius - length - calculatedDistance, angle);
        Point max = findPointOnCircle(radius, angle);

        boundaries.validateXMin(max.x);
        boundaries.validateYMin(max.y);
        boundaries.validateXMax(max.x);
        boundaries.validateYMax(max.y);

        c.drawLine(min.x, min.y, max.x, max.y, paint);
    }

    private float calculateActualAngle(float angle) {
        return (float) (((angle + 140f) / 180f) * Math.PI);
    }

    private void drawErrorMessage(Canvas canvas) {
        Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_error_24dp);
        drawable.setColorFilter(colorPrimary, PorterDuff.Mode.MULTIPLY);
        drawable.setAlpha(51);
        drawable.setBounds((int) (size * 0.25f), (int) (size * 0.25f), (int) (size * 0.75f), (int) (size * 0.75f));
        drawable.draw(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();

        size = Math.min(measuredHeight, measuredWidth);

        updateBitmap(size);
    }

    private void updateBitmap(int size) {
        bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        virtualCanvas = new Canvas(bitmap);
        virtualCanvas.save();
        boundaries.clear();
    }

    private int calculateDistance(int min, int max) {
        if (max > 0) {
            return Math.abs(max - min);
        } else if (min > 0) {
            return Math.abs(min - max);
        }
        return Math.abs(max + min);
    }

    private void animateProperty(String propertyName, float value) {
        ValueAnimator animator = ObjectAnimator.ofFloat(this, propertyName, value);
        animator.addUpdateListener(this);
        animator.addListener(this);
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(DEFAULT_ANIMATION_DURATION);
        animator.start();
    }

    private float convertMeterToLocalValue(float metersPerSecond, int roundLevel) {
        if (animationActive) {
            return locality.convertFromMetersPerSecond(metersPerSecond, 5);
        }
        return locality.convertFromMetersPerSecond(metersPerSecond, roundLevel);
    }

    /* listeners */

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        animationActive = true;
        invalidate();
    }

    @Override
    public void onAnimationStart(Animator animation) {
        animationActive = true;
    }

    @Override
    public void onAnimationEnd(Animator animation) {
        animationActive = false;
    }

    @Override
    public void onAnimationCancel(Animator animation) {
    }

    @Override
    public void onAnimationRepeat(Animator animation) {
    }

    /* Getters & Setters */

    public float getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(float maxValue) {
        this.maxValue = maxValue;
    }

    public SpeedUnit getLocality() {
        return locality;
    }

    public void setLocality(SpeedUnit locality) {
        this.locality = locality;

        animateProperty("localityFactor", locality.getFactor());
    }

    public float getValue() {
        return value;
    }

    private void setValue(float value) {
        this.value = value;
    }

    public void animateProperty(float value) {
        ValueAnimator animator = ObjectAnimator.ofFloat(this, "value", value);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                invalidate();
            }
        });
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(1000);
        animator.start();
    }

    public void animateMaxValue(float value) {
        animateProperty("maxValue", value);
    }
}

