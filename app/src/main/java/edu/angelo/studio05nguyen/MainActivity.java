//created by KE_Darkness ♐

package edu.angelo.studio05nguyen;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.graphics.Path;

import androidx.appcompat.app.AppCompatActivity;

import java.lang.Math;

/**
 * An Activity that allows the user to choose among different drawing modes.
 * It listens for changes to the Spinner to change the mode.
 * @author Rob LeGrand
 */
public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    // Constants that indicate drawing mode.
    private final int MODE_SKETCHY = 0;
    private final int MODE_FRACTAL = 1;
    private final int MODE_POINTS = 2;
    private final int MODE_AVERAGING = 3;
    private final int MODE_GEOMETRY = 4;
    private final int MODE_BEZIER = 5;

    /**
     * Instructions specific to each mode to be put in a TextView.
     */
    private final String[] instructionsStrings = new String[] {
            "Swipe the screen to make the points move.",
            "Swipe the screen to draw a pretty fractal.",
            "Tap the screen to plot a point.",
            "Tap the screen to register a point to include in the average.",
            "Move the green point around to change the shapes.",
            "Drag the points around to change the Bezier curve."
    };

    /**
     * Keeps track of the last View added so it can be found when it needs to be replaced.
     */
    private int indexOfAddedView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the Spinner that selects the mode.
        Spinner spinner = findViewById(R.id.modeSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.mode_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        // Add a generic new View and then replace it with a SketchyView.
        LinearLayout layout = findViewById(R.id.artsyLayout);
        View newView = new View(this);
        layout.addView(newView);
        indexOfAddedView = layout.indexOfChild(newView);
        changeMode(MODE_SKETCHY);
    }

    /**
     * Changes the instructions and replaces the View for the given mode.
     * @param mode   The number of the mode to change to.
     */
    private void changeMode(int mode) {
        // Change the instructions for the new mode.
        TextView instructionsTextView = findViewById(R.id.instructionsTextView);
        try {
            instructionsTextView.setText(instructionsStrings[mode]);
        } catch (ArrayIndexOutOfBoundsException ex) {
            // Then it must not be one of the valid modes.
            instructionsTextView.setText("Please choose a mode above.");
        }

        // Remove the old added View and add a new one according to the new mode.
        LinearLayout layout = findViewById(R.id.artsyLayout);
        layout.removeViewAt(indexOfAddedView);
        View newView;
        switch (mode) {
            case MODE_SKETCHY:
                newView = new SketchyView(this);
                break;
            case MODE_FRACTAL:
                newView = new FractalView(this);
                break;
            case MODE_POINTS:
                newView = new PointsMode(this);
                break;
            case MODE_AVERAGING:
                newView = new AveragingMode(this);
                break;
            case MODE_GEOMETRY:
                newView = new GeometryMode(this);
                break;
            case MODE_BEZIER:
                newView = new BezierMode(this);
                break;
            default: // It must not be one of the valid modes.
                newView = new View(this); // Generic and useless, but removable.
                break;
        }
        layout.addView(newView);
        indexOfAddedView = layout.indexOfChild(newView);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        // When a different mode is selected, change to that mode.
        changeMode(pos);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Do nothing.
    }
}

/**
 * A View that illustrates some simple drawing using a Canvas.
 * @author Rob LeGrand
 */
class SketchyView extends View implements View.OnTouchListener {

    /**
     * Becomes true once the SketchyView object has been touched,
     * which keeps the A and B points from being reinitialized.
     */
    private boolean hasBeenTouched;

    /**
     * The x coordinate of point A.
     */
    private float pointAX;

    /**
     * The y coordinate of point A.
     */
    private float pointAY;

    /**
     * The x coordinate of point B.
     */
    private float pointBX;

    /**
     * The y coordinate of point B.
     */
    private float pointBY;

    //Changes
    /**
     * The x coordinate of point C.
     */
    private float pointCX;

    /**
     * The y coordinate of point C.
     */
    private float pointCY;

    /**
     * The Paint object needed to draw on the Canvas.
     */
    private Paint paint;

    /**
     * Sets up a new SketchyView.
     * @param context   The Context (probably an Activity) the SketchyView is running in.
     */
    public SketchyView(Context context) {
        // Call the constructor of the View class.
        super(context);

        // Make it so that the onTouch method gets called when the SketchyView is touched.
        setOnTouchListener(this);

        // We'll set the beginning point locations only before the SketchyView is touched.
        hasBeenTouched = false;

        // Create a new paintbrush to use to draw to the Canvas.
    paint = new Paint();
    // Make the text size bigger.
        paint.setTextSize(40.0f);
    // Make the lines wider.
        paint.setStrokeWidth(5.0f);
}

    @Override
    public void onDraw(Canvas canvas) {
        // Paint the background a very light blue.
        canvas.drawColor(Color.rgb(235, 245, 255));

        if (!hasBeenTouched) {
            // Set the beginning point locations.
            pointAX = getWidth() / 3.0f;
            pointBX = 2.0f * getWidth() / 3.0f;
            pointAY = 2.0f * getHeight() / 3.0f;
            pointBY = getHeight() / 3.0f;

            //Get C position by the middle position of A and B
            pointCX = (pointAX + pointBX) / 2.0f;
            pointCY = (pointAY + pointBY) / 2.0f;
        }

        // Draw lines among the points and corners of the Canvas.
        paint.setColor(Color.BLACK);
        canvas.drawLine(0.0f, getHeight(), pointAX, pointAY, paint);
        canvas.drawLine(pointAX, pointAY, pointBX, pointBY, paint);
        canvas.drawLine(pointBX, pointBY, getWidth(), 0.0f, paint);

        // Draw the points and label them.
        paint.setColor(Color.rgb(0, 85, 170));
        canvas.drawCircle(pointAX, pointAY, 27.0f, paint);

        paint.setColor(Color.rgb(0, 170, 85));
        canvas.drawRect(pointBX - 24.0f, pointBY - 24.0f, pointBX + 24.0f, pointBY + 24.0f, paint);

        paint.setColor(Color.rgb(170, 0 , 85));
        canvas.drawOval(pointCX - 12.0f, pointCY - 24.0f, pointCX + 12.0f, pointCY + 24.0f, paint);

        paint.setColor(Color.WHITE);
        canvas.drawText("A", pointAX - 13.0f, pointAY + 14.0f, paint);
        canvas.drawText("B", pointBX - 13.0f, pointBY + 14.0f, paint);
        canvas.drawText("C", pointCX - 13.0f, pointCY + 14.0f, paint);
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        // Remember that we won't need to reset the beginning point locations now.
        hasBeenTouched = true;
        // React to the touch event: down, move, cancel or up.
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // Set new point A and force the Canvas to redraw.
                pointAX = event.getX();
                pointAY = event.getY();
                pointCX = (pointAX + pointBX) / 2.0f;
                pointCY = (pointAY + pointBY) / 2.0f;
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                // Do nothing.
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                // Set new point B and force the Canvas to redraw.
                pointBX = event.getX();
                pointBY = event.getY();
                pointCX = (pointAX + pointBX) / 2.0f;
                pointCY = (pointAY + pointBY) / 2.0f;
                invalidate();
                break;
        }
        return true; // Indicate that the touch event has been handled.
    }
}

/**
 * A View that draws substitution fractals, one per swipe.
 * @author Rob LeGrand
 */
class FractalView extends View implements View.OnTouchListener {

    /**
     * Is true when the user is currently swiping for a new fractal.
     */
    private boolean isMoving;

    /**
     * The x coordinate of the starting point of the fractal.
     */
    private float fromX;

    /**
     * The y coordinate of the starting point of the fractal.
     */
    private float fromY;

    /**
     * The x coordinate of the ending point of the fractal.
     */
    private float toX;

    /**
     * The y coordinate of the ending point of the fractal.
     */
    private float toY;

    /**
     * The recursion depth for drawing the fractal.
     */
    private int depth;

    /**
     * The Paint object needed to draw on the Canvas.
     */
    private Paint paint;

    /**
     * The TextView object needed to update the instructions with the fractal depth.
     */
    private TextView instructionsTextView;

    /**
     * Sets up a new FractalView.
     * @param context   The Context (probably an Activity) the FractalView is running in.
     */
    public FractalView(Context context) {
        // Call the constructor of the View class.
        super(context);

        // Make it so that the onTouch method gets called when the FractalView is touched.
        setOnTouchListener(this);

        // Make sure nothing will be drawn until the first swipe.
        isMoving = false;
        depth = 0;

        // Create a new paintbrush to use to draw to the Canvas.
        paint = new Paint();
        // Make the lines wider.
        paint.setStrokeWidth(3.0f);

        // Find the instructions TextView and save it to use later.
        try {
            Activity activity = (Activity) context;
            instructionsTextView = activity.findViewById(R.id.instructionsTextView);
        } catch (ClassCastException ex) {
            // context must not have been an Activity object, so we can't use instructionsTextView.
        } catch (NullPointerException ex) {
            // context must have been a null reference, so we can't use instructionsTextView.
        }
    }

    /**
     * Recursively draw a substitution fractal on the given Canvas.
     * @param canvas   The Canvas to draw on.
     * @param fromX    The x coordinate of the point to draw from.
     * @param fromY    The y coordinate of the point to draw from.
     * @param toX      The x coordinate of the point to draw to.
     * @param toY      The y coordinate of the point to draw to.
     * @param depth    The depth of the recursion tree to use.
     */
    private void drawFractal(Canvas canvas, float fromX, float fromY, float toX, float toY, int depth) {
        // The lines array determines how each line is turned into several lines.
        // These values will simulate the regular paperfolding sequence:
        final float[][][] lines = {
                {{0.00f, 0.00f}, {0.25f, 0.60f}},
                {{0.25f, 0.25f}, {0.75f, 0.75f}},
                {{0.75f, 0.40f}, {1.00f, 1.00f}}
        };
        if (depth <= 0) {
            // We've recursed enough, so just draw a line.
            canvas.drawLine(fromX, fromY, toX, toY, paint);
        } else {
            // We need to recurse some more.  Turn this line into several.
            float cosDistance = (toX - fromX + toY - fromY) / 2.0f;
            float sinDistance = (fromX - toX + toY - fromY) / 2.0f;


            int whichLine;
            for (whichLine = 0; whichLine < lines.length; whichLine += 1) {
                drawFractal(
                        canvas,
                        fromX + lines[whichLine][0][0] * cosDistance - lines[whichLine][0][1] * sinDistance,
                        fromY + lines[whichLine][0][0] * sinDistance + lines[whichLine][0][1] * cosDistance,
                        fromX + lines[whichLine][1][0] * cosDistance - lines[whichLine][1][1] * sinDistance,
                        fromY + lines[whichLine][1][0] * sinDistance + lines[whichLine][1][1] * cosDistance,
                        depth - 1
                );
            }
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (isMoving) {
            // Just draw a line to indicate the current selection.
            paint.setColor(Color.BLUE);
            canvas.drawLine(fromX, fromY, toX, toY, paint);
        } else if (depth > 0) {
            // Draw a fractal at the selected location and report the current depth.
            paint.setColor(Color.BLACK);
            drawFractal(canvas, fromX, fromY, toX, toY, depth);
            try {
                instructionsTextView.setText("Fractal depth: " + depth);
            } catch (NullPointerException ex) {
                // We couldn't get instructionsTextView, so we can't report the depth.
            }
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        // React to the touch event: down, move, cancel or up.
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // Set the original point.
                isMoving = true;
                fromX = event.getX();
                fromY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                // Set the terminal point and show the user the current line.
                toX = event.getX();
                toY = event.getY();
                invalidate(); // Force the Canvas to redraw.
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                // Set the terminal point and draw the fractal.
                isMoving = false;
                toX = event.getX();
                toY = event.getY();
                depth += 1; // Increase the recursion depth after each swipe.
                invalidate(); // Force the Canvas to redraw.
                break;
        }
        return true; // Indicate that the touch event has been handled.
    }
}

// Create new classes below.
class PointsMode extends View implements View.OnTouchListener {

    private int pointMax = 10;

    private float points[][];

    private int count;

    boolean firstDraw;

    private Paint paint;

    public PointsMode(Context context) {
        // Call the constructor of the View class.
        super(context);

        // Make it so that the onTouch method gets called when the SketchyView is touched.
        setOnTouchListener(this);

        // We'll set the beginning point locations only before the SketchyView is touched.
        firstDraw = false;

        count = 0;

        points = new float [pointMax][2];

        // Create a new paintbrush to use to draw to the Canvas.
        paint = new Paint();
        // Make the text size bigger.
        paint.setTextSize(40.0f);
        // Make the lines wider.
        paint.setStrokeWidth(5.0f);
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawColor(Color.WHITE);

        if (!firstDraw && count > 0) {
            firstDraw = true;

            paint.setColor(Color.BLACK);
            canvas.drawText(Integer.toString(count), points[0][0] - 13.0f, points[0][1] + 14.0f, paint);
        }

        else if (count > 1){
            // Draw lines among the points of the Canvas.
            for (int i = 1; i < count; ++i) {
                paint.setColor(Color.rgb(225, 225, 225));
                canvas.drawLine(points[i-1][0], points[i-1][1], points[i][0], points[i][1], paint);
            }

            for (int i = 0; i < count; ++i) {
                paint.setColor(Color.BLACK);
                canvas.drawText(Integer.toString(i+1), points[i][0] - 13.0f, points[i][1] + 14.0f, paint);
            }
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
            // React to the touch event: down, move, cancel or up.
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // Set new point A and force the Canvas to redraw.
                    ++count;
                    if (count < 10) {
                        points[count - 1][0] = event.getX();
                        points[count - 1][1] = event.getY();
                        invalidate();
                    } else {
                        count = 0;
                        points = new float[pointMax][2];
                        firstDraw = false;
                        invalidate();
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    //Do nothing.
                    break;
            }

        return true; // Indicate that the touch event has been handled.
    }
}

class AveragingMode extends View implements View.OnTouchListener {

    private float posX;
    private float posY;

    private int count;

    private Paint paint;

    public AveragingMode(Context context) {
        // Call the constructor of the View class.
        super(context);

        // Make it so that the onTouch method gets called when the SketchyView is touched.
        setOnTouchListener(this);

        //Count gets the initial point that has been pushed
        count = 0;

        // Create a new paintbrush to use to draw to the Canvas.
        paint = new Paint();
        // Make the text size bigger.
        paint.setTextSize(40.0f);
        // Make the lines wider.
        paint.setStrokeWidth(5.0f);
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawColor(Color.BLACK);

        if (count > 0) {
            paint.setColor(Color.WHITE);
            canvas.drawCircle(posX, posY, (float)(Math.sqrt(count) * 9), paint);
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        // React to the touch event: down, move, cancel or up.
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // Set new point A and force the Canvas to redraw.
                ++count;
                if (count == 1) {
                    posX = event.getX();
                    posY = event.getY();
                }

                else if (count > 1) {
                    float tempX = event.getX();
                    float tempY = event.getY();
                    posX = ((posX * (count - 1)) + tempX) / count;
                    posY = ((posY * (count - 1)) + tempY) / count;
                }

                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                //Do nothing.
                break;
        }

        return true; // Indicate that the touch event has been handled.
    }
}

class GeometryMode extends View implements View.OnTouchListener {

    private float posAX;
    private float posAY;

    private float posBX;
    private float posBY;

    boolean isA;

    boolean hasBeenTouched;

        private Paint paint;

    public GeometryMode(Context context) {
            // Call the constructor of the View class.
            super(context);

            // Make it so that the onTouch method gets called when the SketchyView is touched.
            setOnTouchListener(this);

            hasBeenTouched = false;

            isA = true;

        // Create a new paintbrush to use to draw to the Canvas.
        paint = new Paint();
        // Make the text size bigger.
        paint.setTextSize(40.0f);
        // Make the lines wider.
        paint.setStrokeWidth(5.0f);
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawColor(Color.WHITE);

        if (!hasBeenTouched) {
            // Set the beginning point locations.
            posAX = getWidth() / 3.0f;
            posBX = 2.0f * getWidth() / 3.0f;
            posAY = 2.0f * getHeight() / 3.0f;
            posBY = getHeight() / 3.0f;
        }

        paint.setColor(Color.BLACK);
        canvas.drawCircle((posAX+posBX)/2, (posAY+posBY)/2, (float)Math.sqrt((posAX-posBX)*(posAX-posBX) + (posAY-posBY)*(posAY-posBY))/2,paint);

        paint.setColor(Color.BLUE);
        canvas.drawRect(posAX, posBY, posBX, posAY, paint);

        paint.setColor(Color.WHITE);
        canvas.drawLine(posAX, posAY, posBX, posBY, paint);



        //Draw A and B Circle + Text A and B
        if (isA) {
            paint.setColor(Color.rgb(0,170,0));
            canvas.drawCircle(posAX, posAY, 27.0f, paint);

            paint.setColor(Color.RED);
            canvas.drawCircle(posBX, posBY, 27.0f, paint);

            paint.setColor(Color.WHITE);
            canvas.drawText("A", posAX - 13.0f, posAY + 14.0f, paint);
            canvas.drawText("B", posBX - 13.0f, posBY + 14.0f, paint);
        }

        else {
            paint.setColor(Color.rgb(0,170,0));
            canvas.drawCircle(posBX, posBY, 27.0f, paint);

            paint.setColor(Color.RED);
            canvas.drawCircle(posAX, posAY, 27.0f, paint);

            paint.setColor(Color.WHITE);
            canvas.drawText("A", posAX - 13.0f, posAY + 14.0f, paint);
            canvas.drawText("B", posBX - 13.0f, posBY + 14.0f, paint);
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        hasBeenTouched = true;

        // React to the touch event: down, move, cancel or up.
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                if (isA) {
                    posAX = event.getX();
                    posAY = event.getY();
                    invalidate();
                }
                else {
                    posBX = event.getX();
                    posBY = event.getY();
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (isA) {
                    isA = false;
                    invalidate();
                }

                else {
                    isA = true;
                    invalidate();
                }
                break;
        }

        return true; // Indicate that the touch event has been handled.
    }
}

class BezierMode extends View implements View.OnTouchListener {

    private float points[][];

    private float rangeReach = 50.0f;
    private int dragPoint;

    boolean hasBeenTouched;

    private Paint paint;
    private Path path;

    public BezierMode(Context context) {
        // Call the constructor of the View class.
        super(context);

        // Make it so that the onTouch method gets called when the SketchyView is touched.
        setOnTouchListener(this);

        hasBeenTouched = false;

        points = new float[4][2];
        dragPoint = 4;

        // Create a new paintbrush to use to draw to the Canvas.
        paint = new Paint();
        // Make the text size bigger.
        paint.setTextSize(40.0f);
        // Make the lines wider.
        paint.setStrokeWidth(5.0f);
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawColor(Color.WHITE);

        if (!hasBeenTouched) {
            // Set the beginning point locations.
            points[0][0] = getWidth() / 3.0f;
            points[0][1] = getHeight() / 3.0f;

            points[1][0] = 2.0f * getWidth() / 3.0f;
            points[1][1] = getHeight() / 3.0f;

            points[2][0] = 2.0f * getWidth() / 3.0f;
            points[2][1] = 2.0f * getHeight() / 3.0f;

            points[3][0] = getWidth() / 3.0f;
            points[3][1] = 2.0f * getHeight() / 3.0f;
        }

        paint.setColor(Color.rgb(227,227,227));

        //Draw line
        for (int i = 1; i < 4; ++i) {
            canvas.drawLine(points[i-1][0], points[i-1][1], points[i][0], points[i][1], paint);
        }

        super.onDraw(canvas);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLACK);

        path = new Path();
        path.reset();
        path.moveTo(points[0][0],points[0][1]);
        path.cubicTo(points[1][0],points[1][1],points[2][0],points[2][1],points[3][0],points[3][1]);
        canvas.drawPath(path,paint);

        paint.setStyle(Paint.Style.FILL);
        //Draw Circle
        paint.setColor(Color.RED);
        for (int i = 0; i < 4; ++i) {
            if (i != dragPoint) canvas.drawCircle(points[i][0],points[i][1],27.0f, paint);
            else {
                paint.setColor(Color.rgb(0,170,0));
                canvas.drawCircle(points[i][0],points[i][1],27.0f, paint);
                paint.setColor(Color.RED);
            }
        }

        //Draw Text
        paint.setColor(Color.WHITE);
        for (int i = 0; i < 4; ++i) {
            canvas.drawText(Integer.toString(i), points[i][0] - 13.0f, points[i][1] + 14.0f, paint);
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        hasBeenTouched = true;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                for (int i = 0; i < 4; ++i) {
                    if (Math.abs(event.getX() - points[i][0]) <= rangeReach  && Math.abs(event.getY() - points[i][1]) <= rangeReach) {
                        dragPoint = i;
                        points[i][0] = event.getX();
                        points[i][1] = event.getY();
                        invalidate();
                        break;
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                dragPoint = 4;
                invalidate();
                break;
        }

        return true; // Indicate that the touch event has been handled.
    }
}

