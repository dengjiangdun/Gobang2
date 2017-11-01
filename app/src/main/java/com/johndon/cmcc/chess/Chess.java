package com.johndon.cmcc.chess;

/**
 * Created by wanglin on 17-10-30.
 */

public class Chess {
    private int x;
    private int y;
    private int chessFlag;

    public Chess(int x, int y, int chessFlag) {
        this.x = x;
        this.y = y;
        this.chessFlag = chessFlag;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getChessFlag() {
        return chessFlag;
    }

    public void setChessFlag(int chessFlag) {
        this.chessFlag = chessFlag;
    }
}
