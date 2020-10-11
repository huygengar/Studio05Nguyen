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

import androidx.appcompat.app.AppCompatActivity;

/**
 * An Activity that allows the user to choose among different drawing modes.
 * It listens for changes to the Spinner to change the mode.
 * @author Rob LeGrand
 */
public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    // Constants that indicate drawing mode.
    private final int MODE_SKETCHY = 0;
    private final int MODE_FRACTAL = 1;

    /**
     * Instructions specific to each mode to be put in a TextView.
     */
    private final String[] instructionsStrings = new String[] {
            "Swipe the screen to make the points move.",
            "Swipe the screen to draw a pretty fractal."
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
        paint.setColor(Color.WHITE);
        canvas.drawText("A", pointAX - 13.0f, pointAY + 14.0f, paint);
        canvas.drawText("B", pointBX - 13.0f, pointBY + 14.0f, paint);
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
                {{0.00f, 0.00f}, {0.02f, 0.98f}},
                {{1.00f, 1.00f}, {0.02f, 0.98f}}
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