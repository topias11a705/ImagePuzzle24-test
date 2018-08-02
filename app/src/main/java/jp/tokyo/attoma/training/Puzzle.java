package jp.tokyo.attoma.training;

import android.os.Bundle;
import android.os.Build;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Display;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.graphics.Point;
import android.util.Log;
import android.view.GestureDetector;

public class Puzzle extends Activity {

    Context context;
    public static float disp_w,disp_h;	//画面サイズ
    static int Lv;
    //	private MediaPlayer bgm_num,bgm1,bgm2,bgm3;
    int time_pouse;	//HOMEボタンによる一時的にTimerを保持
    int count_pouse;	//HOMEボタンによる一時的に表示用Timerを保持
    static public boolean onresume_bool;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Log.i("memory","Puzzle onCreate");
        //Windowインスタンスを取得
        Window window=getWindow();

        //getWindowManagerでWindowManagerのインスタンスを取得
        WindowManager manager=window.getWindowManager();

        // WindowManagerのgetDefaultDisplay()を呼び出し、
        // Displayのインスタンスを取得。これが画面の情報などを管理する
        // ためのクラスです。ここから画面サイズを得ることが出来ます

        Display disp=manager.getDefaultDisplay();
        Display display = getWindowManager().getDefaultDisplay();
        //************************************
        /* 修正前コード　2018.06.29kouno
        disp_w=disp.getWidth();
        disp_h=disp.getHeight();
         */
        Point size = new Point();
        if(Build.VERSION.SDK_INT <= 12){
            disp_w=disp.getWidth();
            disp_h=disp.getHeight();
            Log.i("test","Using getWidth Method & getHeight Method");
        }else {
            display.getSize(size);
            disp_w=size.x;
            disp_h=size.y;
            Log.i("test","Using getSize Method");
        }
        //*************************************

        System.out.println("disp_w,h"+(int)disp_w+(int)disp_h);

        //フルスクリーン設定
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // インテント取得
        Intent data=getIntent();
        // インテントの付加情報取得
        Bundle extras = data.getExtras();

        // 付加情報から選択された値取得
        Lv = extras != null ? (Integer)(extras.getInt("SELECTED_LV")) : 1;
        PuzzleView.nowImage=extras != null ? (Integer)(extras.getInt("SELECTED_PICTURE"))-1: 0;

        setContentView(new PuzzleView(this));
    }

    //---------------------------------------------------------
    // ヒント！
    // ホームボタンで閉じて戻ってきた時に
    // MediaPlayerを正しい状態にしてあげるため
    // onCreateではなくonResumeで準備する
    @Override
    public void onResume() {
        super.onResume();
        Log.i("memory","Puzzle onResume");
        //*********************************************
        PuzzleView.time_count = time_pouse;
        PuzzleView.count = count_pouse;
        onresume_bool = true;
        //*********************************************
    }
    //---------------------------------------------------------
    // ヒント！
    // ホームボタンを押された時も解放するために
    // onDestroyではなくonPauseでrelease()する
    @Override
    public void onPause() {
        Log.i("memory","Puzzle onPause");
        super.onPause();

        //タイマーの数値を保持　表示用タイマーを保持
        time_pouse=PuzzleView.time_count;
        count_pouse=PuzzleView.count;
        onresume_bool = false;
    }
    @Override protected void onStart() { Log.i("memory","Puzzle onStart"); super.onStart(); }
    @Override protected void onRestart() { Log.i("memory","Puzzle onRestart"); super.onRestart();}
    @Override protected void onStop() { Log.i("memory","Puzzle onStop"); super.onStop();}
    @Override protected void onDestroy() {
        Log.i("memory","Puzzle onDestroy");
        super.onDestroy();
        PuzzleView.replay_game();
        PuzzleView.timer.cancel();
    }
}