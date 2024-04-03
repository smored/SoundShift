package com.bcit.soundshift;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class DrawConnection extends View {
    private float weighting;
    private ArrayList<int[][]> lineList;
    private Paint paint;


    public DrawConnection(Context context) {
        super(context);
        paint = new Paint();
        paint.setColor(ContextCompat.getColor(this.getContext(), R.color.purple));
        paint.setStrokeWidth(5);
        paint.setTextSize(50);
        lineList = new ArrayList<>();
    }

/*    public void drawLine(float X1, float Y1, float X2, float Y2) {
        this.X1 = X1;
        this.Y1 = Y1;
        this.X2 = X2;
        this.Y2 = Y2;
        invalidate();
    }*/

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        for (int[][] line : lineList) {
            int X1 = line[0][0];
            int Y1 = line[0][1];
            int X2 = line[1][0];
            int Y2 = line[1][1];
            Log.i("asdf", " X1 " + X1 + " Y1 " + Y1 + " X2 " + X2 + " Y2 " + Y2);
            canvas.drawLine(X1, Y1, X2, Y2, paint);
            canvas.drawText("TEST", (float) (X1 + X2) /2, (float) (Y1 + Y2) /2, paint);
        }
    }


    public void setList(ArrayList<Connection> cons) {
        final int lineOffsetX = 0;
        final int lineOffsetY = 0;
        lineList.clear();

        for (Connection c : cons) {
            int X1 = c.getStartX();
            int Y1 = c.getStartY();
            int X2 = c.getEndX();
            int Y2 = c.getEndY();

            int[][] temp = new int[2][2];

            temp[0][0] = X1 + lineOffsetX; //X1
            temp[0][1] = Y1 + lineOffsetY; //Y1
            temp[1][0] = X2 + lineOffsetX; //X2
            temp[1][1] = Y2 + lineOffsetY; //Y2

            lineList.add(temp);
        }
    }

}
