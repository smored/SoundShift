package com.bcit.soundshift;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class DrawConnection extends View {
    private Paint paint;
    private ArrayList<Connection> connectionList;

    private OnConnectionClickListener connectionClickListener;

    public void removeConnection(int id) {
        for (int i = 0; i < connectionList.size(); i++) {
            Connection connection = connectionList.get(i);
            if (connection.getID() == id) {
                connectionList.remove(i);
                invalidate(); // Request a redraw
                return; // Exit the loop after removing the connection
            }
        }
    }

    public interface OnConnectionClickListener {
        void onConnectionClick(Connection connectionId);
    }

    public void setOnConnectionClickListener(OnConnectionClickListener listener) {
        this.connectionClickListener = listener;
    }
    public DrawConnection(Context context) {
        super(context);
        init();
    }

    public DrawConnection(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DrawConnection(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(ContextCompat.getColor(getContext(), R.color.purple));
        paint.setStrokeWidth(5);
        paint.setTextSize(50);
        connectionList = new ArrayList<>();
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        for (Connection connection : connectionList) {
            float startX = connection.getStartX();
            float startY = connection.getStartY();
            float endX = connection.getEndX();
            float endY = connection.getEndY();

            // Draw the line
            canvas.drawLine(startX, startY, endX, endY, paint);

            // Calculate the midpoint of the line
            float midX = (startX + endX) / 2;
            float midY = (startY + endY) / 2;

            // Get the weighting of the connection
            String weightingText = String.valueOf(connection.getWeighting());

            // Draw the text
            canvas.drawText(weightingText, midX, midY, paint);
        }
    }

    public void updateConnections()
    {
        for (Connection conn: connectionList)
        {
            conn.calcLocation();
        }
    }

    public void setConnectionList(ArrayList<Connection> connections) {
        connectionList = connections;
        invalidate();
    }

    public void addConnection(Connection connection) {
        connectionList.add(connection);
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // Handle touch event
                float touchX = event.getX();
                float touchY = event.getY();
                // Check if the touch point is close to any connection line
                for (Connection connection : connectionList) {
                    if (isTouchNearLine(touchX, touchY, connection)) {
                        // Handle click event for the connection line
                        if (connectionClickListener != null) {
                            connectionClickListener.onConnectionClick(connection);
                        }
                        return true; // Event handled
                    }
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    public Connection findNearestConnection(float clickX, float clickY) {
        Connection nearestConnection = null;
        double minDistance = Double.MAX_VALUE;

        for (Connection connection : connectionList) {
            float startX = connection.getStartX();
            float startY = connection.getStartY();
            float endX = connection.getEndX();
            float endY = connection.getEndY();

            // Calculate distance from click to connection
            double distance = Math.hypot(clickX - startX, clickY - startY) +
                    Math.hypot(clickX - endX, clickY - endY);

            // Update nearest connection if distance is smaller
            if (distance < minDistance) {
                minDistance = distance;
                nearestConnection = connection;
            }
        }

        return nearestConnection;
    }
    private boolean isTouchNearLine(float touchX, float touchY, Connection connection) {
        // Calculate distance from touch point to the line using the formula for distance between a point and a line
        float distance = Math.abs(
                (connection.getEndY() - connection.getStartY()) * touchX
                        - (connection.getEndX() - connection.getStartX()) * touchY
                        + connection.getEndX() * connection.getStartY()
                        - connection.getEndY() * connection.getStartX()
        ) / (float) Math.sqrt(
                Math.pow(connection.getEndY() - connection.getStartY(), 2)
                        + Math.pow(connection.getEndX() - connection.getStartX(), 2)
        );
        // Adjust the threshold as needed
        return distance < 50; // Check if the touch point is within 50 pixels of the line
    }

    public float getConnectionWeight(int id)
    {
        for(Connection conn : connectionList)
        {
            if(conn.getID() == id)
            {
                return conn.getWeighting();
            }
        }
        return -1;
    }

    public void setConnectionWeight(int id, float weight)
    {
        for(Connection conn : connectionList)
        {
            if(conn.getID() == id)
            {
                conn.setWeighting(weight);
            }
        }
    }
}