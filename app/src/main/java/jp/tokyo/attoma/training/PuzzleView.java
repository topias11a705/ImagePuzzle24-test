/*
* ダイアログ表示をこのクラスで指定するために
* ActivityクラスのPuzzleのインスタンスpzを作成。
* Activityの終了をpz.finishによりこのクラスで始動。
*
*/

package jp.tokyo.attoma.training;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
//import android.media.MediaPlayer;
//import android.media.MediaPlayer.OnPreparedListener;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class PuzzleView extends View{
	static final float HVGA_W=320;	//開発をしている画像サイズ
	static final float HVGA_H=480;	//開発をしている画像サイズ
	public float w;					//取得した画面サイズＷ
	public float h;					//取得した画面サイズＨ
	public static float dw;			//ＨＶＧＡ画面との比率
	public static float dh;			//ＨＶＧＡ画面との比率
//------
	int cp_w;
	int cp_h;
	int mp_w;
	int mp_h;
	int sp_w;
	int sp_h;
	int stimg_w;
	int stimg_h;
	int climg_w;
	int climg_h;

	int tOver_w;	//タイムオーバ画像横幅
	int tOver_h;	//タイムオーバ画像縦幅

	Context context;
	AttributeSet attrs;
	Drawable mainpanel,startpanel,start,start2,clear,tOverImage;
	Drawable[] countpanel;		//タイムカウンター１０個
	Bitmap img[];				//パズル画像３枚
	PuzzleBoard board;			//バズル画像描画クラス

	Rect[] countpanelRect;		//タイムカウンター用レクト
	Rect mainpanelRect;			//メインパネル用レクト
	Rect startpanelRect;		//スタートパネル用レクト
	Rect startRect;
	Rect clearRect;
	Rect tOverRect;
	int gazouX;
	int gazouY;

	static int firstTouch=-1;		//1回目にタッチした際のパネルの番号
	static int twoTouch=-1;			//2回目にタッチした際のパネルの番号
	static boolean gameStart;		//ゲームスタート
	static boolean stageClear;		//ステージクリア
	static final int imgnum=3;		//絵の枚数
	static int nowImage=0;			//現在の絵の種類
	static int countnum=10;			//カウントパネルの数
	static int time_count=0;		//タイムカウント
	static int count=0;				//タイムカウント(パネルを減らす用)
	static boolean timeOver=false;	//タイムオーバ
	static boolean toStart=false;
	int timeUnit;					//レベルに応じてタイムの減る値

	Timer timer;	//
	static String message="Timer";

	Puzzle pz;		//変更

	public PuzzleView(Context context) {
		this(context,null);
	}

//-------------------------------------------------------------------
	public PuzzleView(Context context,AttributeSet attrs) {
		super(context,attrs);
		this.context=(Activity)context;
		this.attrs=attrs;

		pz=(Puzzle)context;

		System.out.println("PuzzleView"+Puzzle.Lv);

		if(Puzzle.Lv==1)timeUnit=30;
		if(Puzzle.Lv==2)timeUnit=30;
		if(Puzzle.Lv==3)timeUnit=30;

		Resources resources=context.getResources();

		//カウントパネルの読み込みと設定
		countpanel=new Drawable[countnum];
		//１つのオブジェを１０個に対応
		for(int i=0;i<countpanel.length;i++){
			countpanel[i]=resources.getDrawable(R.drawable.countpanel1);
		}

		cp_w=countpanel[0].getIntrinsicWidth();		//オブジェの幅を取得
		cp_h=countpanel[0].getIntrinsicHeight();	//オブジェの高さ取得

		//メインパネルの読み込みと設定
		mainpanel=resources.getDrawable(R.drawable.mainpanel);
		mp_w=mainpanel.getIntrinsicWidth();
		mp_h=mainpanel.getIntrinsicHeight();

		//スタートパネルの読み込みと設定
		startpanel=resources.getDrawable(R.drawable.startpanel);
		sp_w=startpanel.getIntrinsicWidth();
		sp_h=startpanel.getIntrinsicHeight();

		//スタート画像の読み込みと設定
		start=resources.getDrawable(R.drawable.start);
		start2=resources.getDrawable(R.drawable.start2);
		stimg_w=start.getIntrinsicWidth();
		stimg_h=start.getIntrinsicHeight();

		//クリア画像の読み込み
		clear=resources.getDrawable(R.drawable.clear);
		climg_w=clear.getIntrinsicWidth();
		climg_h=clear.getIntrinsicHeight();

		//タイムオーバ画像の読み込み
		tOverImage=resources.getDrawable(R.drawable.timeover);
		tOver_w=tOverImage.getIntrinsicWidth();
		tOver_h=tOverImage.getIntrinsicHeight();

		//カウントパネルレクト配列初期化
		countpanelRect=new Rect[countnum];

		setPuzzleSize();	//それぞれのオブジェのサイズ調整
		rectSize();			//レクトの計算

		//オブジェクトの座標セット
		start.setBounds(startRect);
		start2.setBounds(startRect);
		startpanel.setBounds(startpanelRect);
		mainpanel.setBounds(mainpanelRect);
		clear.setBounds(clearRect);
		tOverImage.setBounds(tOverRect);	//

		//カウントパネルの座標セット
		for(int i=0;i<countpanel.length;i++)
			countpanel[i].setBounds(countpanelRect[i]);

		//パズル画像の読み込み
		img=new Bitmap[imgnum];
		img[0]=BitmapFactory.decodeResource(resources,R.drawable.image0);
		img[1]=BitmapFactory.decodeResource(resources,R.drawable.image1);
		img[2]=BitmapFactory.decodeResource(resources,R.drawable.image2);

		//PuzzleBoardクラスの初期化
		board=new PuzzleBoard(img,gazouX,gazouY);
	}

//-------------------------------------------------------------------------------
	void rectSize(){
		//カウントパネルの座標
		for(int i=0;i<countpanel.length;i++)
		countpanelRect[i]=new Rect((int)((15*dw)+(30*i*dw)),(int)(9*dh),
				cp_w+(int)((15*dw)+(30*i*dw)),(int)(cp_h+9*dh));

		mainpanelRect=new Rect(0,0,
				(int)(mp_w),(int)(mp_h));						//メインパネルの座標

		startpanelRect=new Rect((int)(8*dw),(int)(430*dh),
				(int)(sp_w+8*dw),(int)(sp_h+dh*430));			//スタートパネルの座標

		startRect=new Rect((int)(106*dw),(int)(430*dh),
				(int)(stimg_w+dw*106),(int)(stimg_h+(dh*430)));	//スタート画像の座標

		clearRect=new Rect((int)(dw*8),(int)(dh*8),
				(int)(climg_w+(dw*8)),(int)(climg_h+(dh*8)));	//合格画像

		tOverRect=new Rect((int)(dw*60),(int)(dh*8),
				(int)(tOver_w+(dw*60)),(int)(tOver_h+(dh*8)));	//タイムオーバ画像の座標

		gazouX=mainpanelRect.left+(int)(20*dw);		//パズル画像の左上X
		gazouY=mainpanelRect.top+(int)(60*dh);		//パズル画像の左上Y
	}
//-------------------------------------------------------------------
	public void setPuzzleSize(){
		w=Puzzle.disp_w;		//取得した画面サイズＷ
		h=Puzzle.disp_h;		//取得した画面サイズＨ
		dw=w/HVGA_W;			//ＨＶＧＡ画面との比率
		dh=h/HVGA_H;			//ＨＶＧＡ画面との比率

		//比率に応じたサイズに修正
		cp_w=(int)(cp_w*dw);		//カウントパネル横幅
		cp_h=(int)(cp_h*dh);		//カウントパネル縦束
		mp_w=(int)(mp_w*dw);
		mp_h=(int)(mp_h*dh);
		sp_w=(int)(sp_w*dw);
		sp_h=(int)(sp_h*dh);
		stimg_w=(int)(stimg_w*dw);
		stimg_h=(int)(stimg_h*dh);
		climg_w=(int)(climg_w*dw);
		climg_h=(int)(climg_h*dh);
		tOver_w=(int)(tOver_w*dw);
		tOver_h=(int)(tOver_h*dh);
	}
//-------------------------------------------------------------------
	@Override
	protected void onDraw(Canvas c){
		c.drawColor(Color.BLACK);

		mainpanel.draw(c);		//メインパネル描画

		startpanel.draw(c);		//スタートパネル描画
		//count=10秒で1増える。countが１増える毎に表示パネル１減る
		if(count!=countpanel.length){
			for(int i=0;i<countpanel.length-count;i++)
				countpanel[i].draw(c);		//カウントパネル描画
		}

		if(!gameStart){
			start.draw(c);		//スタート画像描画
		}else{
			start2.draw(c);
		}

		if(timeOver){
			timer.cancel();
			tOverImage.draw(c);	//タイムオーバ画像
			diaLog();
		}

		//すべての絵が揃えば
		if(PuzzleBoard.hitCount==PuzzleBoard.PIECE&&!stageClear){
			clear.draw(c);		//合格表示
			diaLog();

		}
		board.panel_draw(c,nowImage);

	}

//-------------------------------------------------------------------
	//ダイアログ表示（ゲームクリア及びタイムオーバ時）
	public void diaLog(){
		//タイムオーバ
		if(PuzzleView.timeOver){

			//ダイアログ表示
			AlertDialog.Builder ad=new AlertDialog.Builder(pz);
			ad.setTitle("タイムオーバ");
			ad.setMessage("タイムオーバです。やり直しますか？");

			ad.setPositiveButton("OK",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					replay_game();
				}
			});

			ad.setNegativeButton("NO",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					replay_game();
					pz.finish();
				}
			});
			ad.show();
		}
	//---------------------------------------------
		//すべての絵が揃えば
		if(PuzzleBoard.hitCount==PuzzleBoard.PIECE&&!stageClear){

	        //ダイアログ表示
			AlertDialog.Builder ad=new AlertDialog.Builder(pz);
			ad.setTitle("ゲームクリア");
			ad.setMessage("次のステージにチェンジします");

			ad.setPositiveButton("OK",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					stage_clear();		//ステージクリアメソッド
				}
			});
			ad.setNegativeButton("NO",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					replay_game();		//
					pz.finish();
				}
			});
			ad.show();

		}
	}
//-------------------------------------------------------------------
	//画面タッチ
	@Override
	public boolean onTouchEvent(MotionEvent event){
		int action=event.getAction();
		int x=(int)event.getX();
		int y=(int)event.getY();
		//-------------------------------------------------
		switch(action){
			case MotionEvent.ACTION_DOWN:	//タッチダウン

				//タッチした場所がstartパネル
				if(isIn(x,y,start.getBounds())||isIn(x,y,start2.getBounds())){
					if(!gameStart){		//ゲームスタートまだ
						Toast toast=Toast.makeText
						(context,"スタート！",Toast.LENGTH_SHORT);
						toast.show();
						PuzzleBoard.hitCount=0;
						gameStart=true;		//ゲーム中フラグON
						stageClear=false;	//ステージクリアフラグON
						startTimer();
						board.init();		//PuzzleBoardクラスのinit呼び出し(画像シャッフル）
					}
					invalidate();		//このクラスのonDrawの呼び出し
				}
				//タッチした場所がstart以外
				else{
					//メインパネル内
					for(int i=0;i<board.img_rect.length;i++){	//座標チェック
						if(isIn(x,y,board.sell_rect[i])&&gameStart){
							if(firstTouch==-1&&twoTouch==-1){	//タッチ一回目
								firstTouch=i;
							}else if(twoTouch==-1){//タッチ二回目
								twoTouch=i;
								board.changePanel();
							}
							break;
						}
						invalidate();		//このクラスのonDrawの呼び出し
					}
				}
		}
		return false;
	}
//-------------------------------------------------------------------
//クリックした位置が各レクト内かチェック
	public boolean isIn(int x,int y,Rect rect){
		return x>rect.left && x<rect.right
		&& y>rect.top && y<rect.bottom;
	}
//-------------------------------------------------------------------
//ステージクリア時に初期化するフラグ
	//static public void  stage_clear(){
	void  stage_clear(){
		stageClear=true;
		gameStart=false;
		time_count=0;			//タイマーを０にする
		count=0;				//タイマーカウント（表示用）を０にする
		if(nowImage==2){
			nowImage=0;
		}else{
			nowImage++;
		}
		pz.onPause();
		pz.onResume();

	}
//-------------------------------------------------------------------
//やり直し時に初期化するフラグ
	//static public void replay_game(){
	void replay_game(){
		stageClear=true;
		gameStart=false;
		count=0;				//タイマーカウントを０にする
		timeOver=false;
		nowImage=0;
	}
//-------------------------------------------------------------------
//タイマースケジューラ
	public void startTimer(){
		final Handler handler=new Handler();
		TimerTask task=new TimerTask(){
			public void run(){
				message="Time:"+ ++time_count+"sec";
				if(time_count%timeUnit==0)
					++count;	//timeUnit秒毎にcount+1
				handler.post(new Runnable(){
					public void run(){
						invalidate();
					}
				});
				if(count==10){
					timeOver=true;
				}
			}
		};
		timer=new Timer();
		timer.schedule(task,0,1000);
	}
}
