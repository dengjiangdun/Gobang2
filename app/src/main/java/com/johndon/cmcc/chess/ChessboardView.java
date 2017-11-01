package com.johndon.cmcc.chess;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by wanglin on 17-10-28.
 */

public class ChessboardView extends View {

    private static final int ROWS = 15;
    private static final int COLS = 15;
    private static final int CHESS_BLACK = -1;
    private static final int CHESS_WHITE = 1;
    private int originX;
    private int originY;
    private int space;
    private int boardWidth;
    private int chessRadius;
    private int chessFlag;
    private int cursorX;
    private int cursorY;
    private int[][] mBoard = new int[COLS][ROWS];
    private boolean isFinish = false;
    private boolean isMoving = false;
    private ArrayList<Chess> mChesses = new ArrayList<>();
    private Paint linePaint = new Paint();
    private Paint chessPaint = new Paint();
    private Paint cursorPaint = new Paint();

    interface ShowWinnerListener {
        void showWinner(String winner);
        void cleanWinner();
    }

    private ShowWinnerListener winnerListener;

    public void setWinnerListener(ShowWinnerListener winnerListener) {
        this.winnerListener = winnerListener;
    }

    public void restart() {
        if (mChesses.size()>0) {
            mBoard = new int[COLS][ROWS];
            mChesses.clear();
            if (isFinish) {
                isFinish = false;
                winnerListener.cleanWinner();
            }
            postInvalidate();
        }
    }

    public void back() {
        if (mChesses.size() > 0) {
            mBoard[cursorX][cursorY] = 0;
            mChesses.remove(mChesses.size() - 1);
            if (mChesses.size() > 0){
                cursorX = mChesses.get(mChesses.size() - 1).getX();
                cursorY = mChesses.get(mChesses.size() - 1).getY();
            }
            if (isFinish) {
                isFinish = false;
                winnerListener.cleanWinner();
            }
            postInvalidate();
        }
    }

    public ChessboardView(Context context) {
        super(context);
    }

    public ChessboardView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    private void init(int width, int height) {
        cursorX = 0;
        cursorY = 0;
        linePaint.setColor(Color.BLACK);
        linePaint.setStrokeWidth(3);
        cursorPaint.setColor(getResources().getColor(R.color.colorPrimary));
        cursorPaint.setStrokeWidth(3);
        cursorPaint.setStyle(Paint.Style.STROKE);
        boardWidth = 7 * width / 8;     //the max width od chessboard is 0.75*width
        space = boardWidth / 14;        //space of two line
        originX = (width / 2) - (boardWidth / 2);    //the x of top-left point
        originY = (height / 2) - (boardWidth / 2);   //the y of top-left point
        chessRadius = 3 * space / 8;    //radius of chess
        chessFlag = CHESS_BLACK;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        init(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);


        //draw rows
        for (int i = 0; i < ROWS; i++) {
            canvas.drawLine(originX, originY + (i * space), originX + boardWidth, originY + (i * space), linePaint);
        }

        //draw cols
        for (int i = 0; i < COLS; i++) {
            canvas.drawLine(originX + (i * space), originY, originX + (i * space), originY + boardWidth, linePaint);
        }

        Chess chess;
        int chessX = 0;
        int chessY = 0;
        if (mChesses.size() != 0) {
            for (int i = 0; i < mChesses.size(); i++) {
                chess = mChesses.get(i);
                chessX = chess.getX();
                chessY = chess.getY();
                if (chess.getChessFlag() == CHESS_BLACK) {
                    chessPaint.setColor(Color.BLACK);
                } else {
                    chessPaint.setColor(Color.WHITE);
                }
                canvas.drawCircle(chessX * space + originX, chessY * space + originY, chessRadius, chessPaint);
            }
            if (!isMoving) {
                canvas.drawRect(chessX * space + originX - (space / 2), chessY * space + originY - (space / 2), chessX * space + originX + (space / 2), chessY * space + originY + (space / 2), cursorPaint);
            }
        }
        if (isMoving) {
            canvas.drawRect(cursorX * space + originX - (space / 2), cursorY * space + originY - (space / 2), cursorX * space + originX + (space / 2), cursorY * space + originY + (space / 2), cursorPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isFinish) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    isMoving = true;
                    drawCursor(event.getX(), event.getY());
                    break;
                case MotionEvent.ACTION_UP:
                    isMoving = false;
                    drawChess(cursorX, cursorY);
                    break;
                case MotionEvent.ACTION_MOVE:
                    drawCursor(event.getX(), event.getY());
                    break;
                default:
                    break;
            }
        }
        return true;
    }

    private void drawChess(int col, int row) {
        if (mBoard[col][row] == 0) {
            mBoard[col][row] = chessFlag;
            mChesses.add(new Chess(col, row, chessFlag));
            isFinish = isFinish(col, row);
            if (isFinish) {
                if (chessFlag == CHESS_BLACK) {
                    winnerListener.showWinner("BLACK WIN!");
                } else {
                    winnerListener.showWinner("WHITE WIN!");
                }
            }
            postInvalidate();

            if (chessFlag == CHESS_BLACK) {
                chessFlag = CHESS_WHITE;
            } else {
                chessFlag = CHESS_BLACK;
            }
        }
    }

    private void drawCursor(float x, float y) {
        int col = (int) ((x - originX) / space + 0.5);
        int row = (int) ((y - originY) / space + 0.5);
        if (col < 0) {
            col = 0;
        }
        if (col > COLS - 1) {
            col = COLS - 1;
        }
        if (row < 0) {
            row = 0;
        }
        if (row > ROWS - 1) {
            row = ROWS - 1;
        }

        if (mBoard[col][row] == 0) {
            cursorX = col;
            cursorY = row;
            postInvalidate();
        }
    }

    private boolean isFinish(int col, int row) {
        int firstX = 0;
        int firstY = 0;
        int offset = 0;
        int firstOffset = 0;
        int lastOffset = 0;

        firstOffset = Math.min(4, col);
        lastOffset = Math.min(4, COLS - 1 - col);
        offset = firstOffset + lastOffset - 4;
        firstX = col - firstOffset;
        for (int i = offset; i >= 0; i--) {
            if (mBoard[firstX][row] + mBoard[firstX + 1][row] + mBoard[firstX + 2][row] + mBoard[firstX + 3][row] + mBoard[firstX + 4][row] == 5 * chessFlag) {
                return true;
            }
            firstX += 1;
        }

        firstOffset = Math.min(4, row);
        lastOffset = Math.min(4, ROWS - 1 - row);
        offset = firstOffset + lastOffset - 4;
        firstY = row - firstOffset;
        for (int i = offset; i >= 0; i--) {
            if (mBoard[col][firstY] + mBoard[col][firstY + 1] + mBoard[col][firstY + 2] + mBoard[col][firstY + 3] + mBoard[col][firstY + 4] == 5 * chessFlag) {
                return true;
            }
            firstY += 1;
        }

        firstOffset = min(col, 4, row);
        lastOffset = min((COLS - 1) - col, (ROWS - 1) - row, 4);
        offset = firstOffset + lastOffset - 4;
        firstX = col - firstOffset;
        firstY = row - firstOffset;
        for (int i = 0; i <= offset; i++) {
            if (mBoard[firstX][firstY] + mBoard[firstX + 1][firstY + 1] + mBoard[firstX + 2][firstY + 2] + mBoard[firstX + 3][firstY + 3] + mBoard[firstX + 4][firstY + 4] == 5 * chessFlag) {
                return true;
            }
            firstX += 1;
            firstY += 1;
        }

        firstOffset = min(4, col, ROWS - 1 - row);
        lastOffset = min(4, row, COLS - 1 - col);
        offset = firstOffset + lastOffset - 4;
        firstX = col - firstOffset;
        firstY = row + firstOffset;
        for (int i = 0; i <= offset; i++) {
            if (mBoard[firstX][firstY] + mBoard[firstX + 1][firstY - 1] + mBoard[firstX + 2][firstY - 2] + mBoard[firstX + 3][firstY - 3] + mBoard[firstX + 4][firstY - 4] == 5 * chessFlag) {
                return true;
            }
            firstX += 1;
            firstY -= 1;
        }
        return false;
    }

    private int min(int x, int y, int z) {
        return Math.min(Math.min(x, y), z);
    }
}
