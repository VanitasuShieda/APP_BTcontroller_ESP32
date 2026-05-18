package mx.vanitasu.BTJoystick;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class JoystickView extends View {

    public interface OnJoystickMoveListener {
        void onMove(float xPercent, float yPercent);
        void onRelease();
    }

    private OnJoystickMoveListener listener;

    private float centerX, centerY;
    private float baseWidth, baseHeight;
    private float hatRadius;
    private float hatX, hatY;

    private Paint basePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint hatPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint arrowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private boolean isVertical = true; // Default to vertical (Move)

    public void setVertical(boolean isVertical) {
        this.isVertical = isVertical;
        invalidate();
    }

    public JoystickView(Context context, AttributeSet attrs) {
        super(context, attrs);

        basePaint.setColor(Color.parseColor("#FCE4EC")); // Very light pink
        basePaint.setStyle(Paint.Style.FILL);
        
        hatPaint.setColor(Color.parseColor("#F06292")); // Darker pink
        hatPaint.setStyle(Paint.Style.FILL);

        arrowPaint.setColor(Color.WHITE);
        arrowPaint.setStyle(Paint.Style.FILL);
        arrowPaint.setStrokeWidth(5f);
    }

    public void setOnJoystickMoveListener(OnJoystickMoveListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        centerX = w / 2f;
        centerY = h / 2f;
        
        if (isVertical) {
            baseWidth = w * 0.4f;
            baseHeight = h * 0.8f;
        } else {
            baseWidth = w * 0.8f;
            baseHeight = h * 0.4f;
        }
        
        hatRadius = Math.min(w, h) / 6f;
        hatX = centerX;
        hatY = centerY;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // Draw base rectangle with rounded corners
        RectF baseRect = new RectF(
                centerX - baseWidth / 2f,
                centerY - baseHeight / 2f,
                centerX + baseWidth / 2f,
                centerY + baseHeight / 2f
        );
        canvas.drawRoundRect(baseRect, 30f, 30f, basePaint);

        // Draw hat (the moving part)
        canvas.drawCircle(hatX, hatY, hatRadius, hatPaint);

        // Draw arrows on the hat
        drawArrows(canvas);
    }

    private void drawArrows(Canvas canvas) {
        float arrowSize = hatRadius * 0.5f;
        Path path = new Path();
        
        if (isVertical) {
            // Up Arrow
            path.moveTo(hatX, hatY - arrowSize);
            path.lineTo(hatX - arrowSize/2, hatY - arrowSize/3);
            path.lineTo(hatX + arrowSize/2, hatY - arrowSize/3);
            path.close();
            
            // Down Arrow
            path.moveTo(hatX, hatY + arrowSize);
            path.lineTo(hatX - arrowSize/2, hatY + arrowSize/3);
            path.lineTo(hatX + arrowSize/2, hatY + arrowSize/3);
            path.close();
        } else {
            // Left Arrow
            path.moveTo(hatX - arrowSize, hatY);
            path.lineTo(hatX - arrowSize/3, hatY - arrowSize/2);
            path.lineTo(hatX - arrowSize/3, hatY + arrowSize/2);
            path.close();
            
            // Right Arrow
            path.moveTo(hatX + arrowSize, hatY);
            path.lineTo(hatX + arrowSize/3, hatY - arrowSize/2);
            path.lineTo(hatX + arrowSize/3, hatY + arrowSize/2);
            path.close();
        }
        canvas.drawPath(path, arrowPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float dx = event.getX() - centerX;
        float dy = event.getY() - centerY;

        // Constraint movement to the base rectangle
        float halfW = baseWidth / 2f;
        float halfH = baseHeight / 2f;

        hatX = Math.max(centerX - halfW, Math.min(centerX + halfW, event.getX()));
        hatY = Math.max(centerY - halfH, Math.min(centerY + halfH, event.getY()));

        // In move mode, we mainly care about Y. In turn mode, X.
        // But for generic joystick, we can pass both.
        
        invalidate();

        if (listener != null) {
            if (event.getAction() != MotionEvent.ACTION_UP) {
                float xPercent = (hatX - centerX) / halfW;
                float yPercent = (centerY - hatY) / halfH;
                listener.onMove(xPercent, yPercent);
            } else {
                hatX = centerX;
                hatY = centerY;
                invalidate();
                listener.onRelease();
            }
        }

        return true;
    }
}
