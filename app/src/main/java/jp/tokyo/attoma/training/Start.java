package jp.tokyo.attoma.training;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

public class Start extends Activity{
	ImageButton gazou1,gazou2,gazou3,select;
	ImageButton Lv1,Lv2,Lv3;

	int gazouFlag=0;
	int LvFlag=0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    	requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Windowインスタンスを取得
        Window window=getWindow();
        //getWindowManagerでWindowManagerのインスタンスを取得
//      WindowManager manager=window.getWindowManager();
        //フルスクリーン設定
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.start);

        gazou1=(ImageButton)findViewById(R.id.imageButton1);
        gazou1.setTag("画像１");
        gazou1.setOnClickListener(new GazouClickListener());
		//bt2.setPressed(true);

        gazou2=(ImageButton)findViewById(R.id.imageButton2);
        gazou2.setTag("画像２");
        gazou2.setOnClickListener(new GazouClickListener());

        gazou3=(ImageButton)findViewById(R.id.imageButton3);
        gazou3.setTag("画像３");
        gazou3.setOnClickListener(new GazouClickListener());

        select=(ImageButton)findViewById(R.id.imageButton4);

        Lv1=(ImageButton)findViewById(R.id.Lv1);
        Lv1.setTag("ＬＶ１");
        Lv1.setOnClickListener(new LevelClickListener());

        Lv2=(ImageButton)findViewById(R.id.Lv2);
        Lv2.setTag("ＬＶ２");
        Lv2.setOnClickListener(new LevelClickListener());

        Lv3=(ImageButton)findViewById(R.id.Lv3);
        Lv3.setTag("ＬＶ３");
        Lv3.setOnClickListener(new LevelClickListener());

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
    // ホームボタンを押された時も解放するために
    // onDestroyではなくonPauseでrelease()する
    @Override
    public void onPause() {
        super.onPause();
    }
//-------------------------------------------------------------------
    class LevelClickListener implements OnClickListener{
		public void onClick(View v) {
			String tag=(String)v.getTag();

			if(tag.equals("ＬＶ１")){
				if(LvFlag==1){		//押されている状態
					Lv1.setColorFilter(null);
					LvFlag=0;;
				}else{				//押されていない状態
					//フィルターを掛ける（αＲＧＭ）
					Lv1.setColorFilter(0xAAFFFFFF);
					Lv2.setColorFilter(null);
					Lv3.setColorFilter(null);
					LvFlag=1;
				}
			}else if(tag.equals("ＬＶ２")){
				if(LvFlag==2){	//押されている状態
					Lv2.setColorFilter(null);
					LvFlag=0;;
				}else{				//押されていない状態
					Lv1.setColorFilter(null);
					Lv2.setColorFilter(0xAAFFFFFF);
					Lv3.setColorFilter(null);
					LvFlag=2;
				}
			}
			else if(tag.equals("ＬＶ３")){
				if(LvFlag==3){	//押されている状態
					Lv3.setColorFilter(null);
					LvFlag=0;
				}else{			//押されていない状態
					Lv1.setColorFilter(null);
					Lv2.setColorFilter(null);
					Lv3.setColorFilter(0xAAFFFFFF);
					LvFlag=3;
				}
			}
			selectMode();
		}
    }
//-------------------------------------------------------------------
    class GazouClickListener implements OnClickListener{
		public void onClick(View v) {

			String tag=(String)v.getTag();

			if(tag.equals("画像１")){
				if(gazouFlag==1){	//押されている状態
					gazou1.setColorFilter(null);
					gazouFlag=0;;
				}else{				//押されていない状態
					gazou1.setColorFilter(0xAAFFFFFF);
					gazou2.setColorFilter(null);
					gazou3.setColorFilter(null);
					gazouFlag=1;
				}
			}else if(tag.equals("画像２")){
				if(gazouFlag==2){	//押されている状態
					gazou2.setColorFilter(null);
					gazouFlag=0;;
				}else{				//押されていない状態
					gazou1.setColorFilter(null);
					gazou2.setColorFilter(0xAAFFFFFF);
					gazou3.setColorFilter(null);
					gazouFlag=2;
				}
			}
			else if(tag.equals("画像３")){
				if(gazouFlag==3){	//押されている状態
					gazou3.setColorFilter(null);
					gazouFlag=0;
				}else{			//押されていない状態
					gazou1.setColorFilter(null);
					gazou2.setColorFilter(null);
					gazou3.setColorFilter(0xAAFFFFFF);
					gazouFlag=3;
				}
			}
			selectMode();
		}
    }
    private void selectMode(){
    	if(gazouFlag!=0&&LvFlag!=0){		//ステージ&レベル選択ＯＫ
    		select.setImageResource(R.drawable.gamestart);//gamestart
			select.setEnabled(true);

    		select.setOnClickListener(
            	new OnClickListener(){
					public void onClick(View arg0) {
						select.setColorFilter(0xAAFFFFFF);
						Intent intent=new Intent(Start.this,Puzzle.class);
			            intent.putExtra("SELECTED_LV",LvFlag);
			            intent.putExtra("SELECTED_PICTURE",gazouFlag);
			        	System.out.println("gazouFlag:"+gazouFlag);
			    		System.out.println("LvFlag:"+LvFlag);

			    		LvFlag=0;
			            gazouFlag=0;
						gazou1.setColorFilter(null);
						gazou2.setColorFilter(null);
						gazou3.setColorFilter(null);
						Lv1.setColorFilter(null);
						Lv2.setColorFilter(null);
						Lv3.setColorFilter(null);
						select.setColorFilter(null);
						select.setImageResource(R.drawable.selectstage);//
						select.setEnabled(false);

						startActivity(intent);
						overridePendingTransition(R.anim.animation_scale_1,R.anim.animation_translate_1);
					}
            	}
            );

    	}else if(gazouFlag!=0){				//ステージは選択
    		select.setImageResource(R.drawable.selectmode);//selectmode
    	}else if(LvFlag!=0){				//レベルは選択
    		select.setImageResource(R.drawable.selectstage);//selectLv
    	}else if(gazouFlag==0&&LvFlag==0){	//どちらも選択していない
       		select.setImageResource(R.drawable.selectstage);//selectmode
    	}
    	System.out.println("gazouFlag2:"+gazouFlag);
		System.out.println("LvFlag2:"+LvFlag);
    }
}
