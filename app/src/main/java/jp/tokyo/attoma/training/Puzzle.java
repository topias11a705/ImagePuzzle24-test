package jp.tokyo.attoma.training;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;

public class Puzzle extends Activity {

	Context context;
	public static float disp_w,disp_h;		//画面サイズ
	static int Lv;
//	private MediaPlayer bgm_num,bgm1,bgm2,bgm3;
    int time_pouse;		//HOMEボタンによる一時的にTimerを保持
    int count_pouse;	//HOMEボタンによる一時的に表示用Timerを保持

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Windowインスタンスを取得
        Window window=getWindow();

        //getWindowManagerでWindowManagerのインスタンスを取得
        WindowManager manager=window.getWindowManager();

        // WindowManagerのgetDefaultDisplay()を呼び出し、
        // Displayのインスタンスを取得。これが画面の情報などを管理する
        // ためのクラスです。ここから画面サイズを得ることが出来ます
        Display disp=manager.getDefaultDisplay();

        disp_w=disp.getWidth();
        disp_h=disp.getHeight();

        System.out.println("disp_w,h"+(int)disp_w+(int)disp_h);

        //フルスクリーン設定
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

	    // インテント取得
		Intent data=getIntent();
        // インテントの付加情報取得
        Bundle extras = data.getExtras();

        // 付加情報から選択された値取得
        Lv = extras != null ? (Integer)(extras.getInt("SELECTED_LV")) : 1;
        PuzzleView.nowImage=extras !=
        	null ? (Integer)(extras.getInt("SELECTED_PICTURE"))-1: 0;

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
      }
  //---------------------------------------------------------
      // ヒント！
      // ホームボタンを押された時も解放するために
      // onDestroyではなくonPauseでrelease()する
      @Override
      public void onPause() {
          super.onPause();

          //タイマーの数値を保持
          time_pouse=PuzzleView.time_count;
          //表示用タイマーを保持
    	  count_pouse=PuzzleView.count;

      }
}
