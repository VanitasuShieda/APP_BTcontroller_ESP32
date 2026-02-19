package mx.vanitasu.BTJoystick;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
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
    private float baseRadius, hatRadius;
    private float hatX, hatY;

    private Paint basePaint = new Paint();
    private Paint hatPaint = new Paint();

    public JoystickView(Context context, AttributeSet attrs) {
        super(context, attrs);

        basePaint.setARGB(255, 150,150,150);
        hatPaint.setARGB(255, 50,50,50);
    }

    public void setOnJoystickMoveListener(OnJoystickMoveListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        centerX = w / 2f;
        centerY = h / 2f;
        baseRadius = Math.min(w,h)/3f;
        hatRadius = Math.min(w,h)/5f;
        hatX = centerX;
        hatY = centerY;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawCircle(centerX,centerY,baseRadius,basePaint);
        canvas.drawCircle(hatX,hatY,hatRadius,hatPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float dx = event.getX() - centerX;
        float dy = event.getY() - centerY;

        float distance = (float)Math.sqrt(dx*dx + dy*dy);

        if(distance < baseRadius) {
            hatX = event.getX();
            hatY = event.getY();
        } else {
            float ratio = baseRadius / distance;
            hatX = centerX + dx * ratio;
            hatY = centerY + dy * ratio;
        }

        invalidate();

        if(listener != null) {
            if(event.getAction() != MotionEvent.ACTION_UP) {

                float xPercent = (hatX - centerX) / baseRadius;
                float yPercent = (centerY - hatY) / baseRadius;

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
