package com.johndon.cmcc.chess;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by wanglin on 17-10-28.
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener,ChessboardView.ShowWinnerListener{

    private ChessboardView mChessboard;
    private TextView mWinnerTv;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button resetBt = (Button) findViewById(R.id.bt_restart);
        Button backBt = (Button) findViewById(R.id.bt_back);
        mWinnerTv = (TextView) findViewById(R.id.winnerTv);
        mChessboard = (ChessboardView) findViewById(R.id.chessboard);

        mChessboard.setWinnerListener(this);
        resetBt.setOnClickListener(this);
        backBt.setOnClickListener(this);
    }

    @Override
    public void showWinner(String winner) {
        mWinnerTv.setText(winner);
    }

    @Override
    public void cleanWinner() {
        mWinnerTv.setText("");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_back:
                mChessboard.back();
                break;
            case R.id.bt_restart:
                mChessboard.restart();
                break;
            default:
                break;
        }
    }
}
