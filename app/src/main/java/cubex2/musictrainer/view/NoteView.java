package cubex2.musictrainer.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import cubex2.musictrainer.data.Tone;
import cubex2.musictrainer.data.ToneSequence;

public class NoteView extends View
{

    private static final int NUM_LINES = 9; // Must be odd
    private static final int MAX_NOTES = NUM_LINES * 2 - 1;
    private static final int VISIBLE_LINES = 5; // Must be odd
    private static final int INVISIBLE_LINES = NUM_LINES - VISIBLE_LINES;


    private Rect linesRect = new Rect();
    private int lineSpacing;
    private int border;

    private Paint linePaint;

    private ToneSequence sequence;
    private Tone lowTone;
    private Tone highTone;
    private int firstLine = 0; // The line that matches the bottom-most note

    public NoteView(Context context)
    {
        super(context);
        init(context, null);
    }

    public NoteView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context, attrs);
    }

    public NoteView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public NoteView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs)
    {
        setBackgroundColor(Color.WHITE);
        linePaint = new Paint();
        linePaint.setStrokeWidth(5f);
        //linePaint.setColor(Color.GREEN);
        linePaint.setAntiAlias(true);
        linePaint.setStyle(Paint.Style.STROKE);
    }

    public void setSequence(ToneSequence sequence)
    {
        this.sequence = sequence;
        if (sequence != null)
        {
            lowTone = Tone.forKeyNumber(Tone.MAX_KEY_NUMBER);
            highTone = Tone.forKeyNumber(Tone.MIN_KEY_NUMBER);
            for (Tone tone : sequence.getTones())
            {
                if (tone.getKeyNumber() < lowTone.getKeyNumber())
                    lowTone = tone;
                if (tone.getKeyNumber() > highTone.getKeyNumber())
                    highTone = tone;
            }

            mapToneToNotes();
        }

        invalidate();
    }

    private void mapToneToNotes()
    {
        firstLine = 0;
        while (!doesSequenceFit())
        {
            firstLine += 12;
        }
    }

    private boolean doesSequenceFit()
    {
        if (lowTone.getToneLine() < firstLine)
            return false;
        if (highTone.getToneLine() > firstLine + MAX_NOTES)
            return false;

        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        int height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);

        lineSpacing = height / (NUM_LINES + 1);
        border = lineSpacing;

        linesRect.set(border, border, width - border, height - border);

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        drawLines(canvas);

        if (sequence != null)
        {
            int i = 0;
            for (Tone tone : sequence.getTones())
            {
                drawNote(canvas, i, tone.getToneLine() - firstLine);

                i++;
            }
        }
    }

    private void drawLines(Canvas canvas)
    {
        int x1 = linesRect.left;
        int x2 = linesRect.right;
        int y1 = linesRect.top;
        int y2 = linesRect.bottom;

        for (int i = INVISIBLE_LINES / 2; i < NUM_LINES - INVISIBLE_LINES / 2; i++)
        {
            int y = y1 + i * lineSpacing;
            canvas.drawLine(x1, y, x2, y, linePaint);
        }

        y1 += INVISIBLE_LINES / 2 * lineSpacing;
        y2 -= INVISIBLE_LINES / 2 * lineSpacing;
        canvas.drawLine(x1, y1, x1, y2, linePaint);
        canvas.drawLine(x2, y1, x2, y2, linePaint);
    }

    private void drawNote(Canvas canvas, int noteIndex, int note)
    {
        int x = xForNote(noteIndex);
        int y = yForNote(note);
        canvas.drawCircle(x, y, lineSpacing / 2, linePaint);

        if (note < INVISIBLE_LINES - 1)
        {
            int line = (note + 1) / 2;
            int lineY = yForLine(line);

            canvas.drawLine(x - lineSpacing * 3 / 4, lineY, x + lineSpacing * 3 / 4, lineY, linePaint);

        } else if (note >= MAX_NOTES - (INVISIBLE_LINES - 1))
        {
            int line = (note) / 2;
            int lineY = yForLine(line);

            canvas.drawLine(x - lineSpacing * 3 / 4, lineY, x + lineSpacing * 3 / 4, lineY, linePaint);
        }
    }

    private int xForNote(int noteIndex)
    {
        return border + lineSpacing + noteIndex * lineSpacing * 2;
    }

    private int yForNote(int note)
    {
        int noteTransformed = MAX_NOTES - note - 1;
        return border + noteTransformed * lineSpacing / 2;
    }

    private int yForLine(int line)
    {
        int lineTransformed = NUM_LINES - line - 1;
        return border + lineTransformed * lineSpacing;
    }
}
