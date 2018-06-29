package jp.tokyo.attoma.training;

import java.util.ArrayList;
import java.util.Collections;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class PuzzleBoard {
	final static int PIECE=24;			//切り取り数
	int gazouX,gazouY;					//画像のx､y座標
	int pW=70;							//元ピースの横幅
	int pH=60;							//元ピースの縦幅
	int pW2=(int)(PuzzleView.dw*70);	//ピースの横幅
	int pH2=(int)(PuzzleView.dh*60);	//ピースの縦幅

	Bitmap img[]=new Bitmap[PuzzleView.imgnum];	//パズル画像
	Rect[] img_rect,position_rect;		//各パーツの切り取り座標と正解座標
	Rect[] sell_rect;					//パーツの現在位置座標
	Rect[] kari_rect;					//コピー用
	static int hitCount;				//正解数

// コンストラクタ
//----------------------------------------------------
	PuzzleBoard(Bitmap[] img,int gazouX,int gazouY){
		this.img=img;
		this.gazouX=gazouX;
		this.gazouY=gazouY;

		img_rect=new Rect[PIECE];
		position_rect=new Rect[PIECE];
		sell_rect=new Rect[PIECE];
		kari_rect=new Rect[PIECE];

		for(int i=0;i<img_rect.length;i++){
			//切り取る画像矩形の座標配列（24個）の初期化
			img_rect[i]=new Rect((pW*(i%4)),(pH*(i/4)),
					(pW+pW*(i%4)),(pH+pH*(i/4)));

			//正解座標クラス配列（24個）の初期化
			position_rect[i]=new Rect(
					(gazouX+pW2*(i%4)),(gazouY+pH2*(i/4)),
					(gazouX+pW2*(i%4)+pW2),(gazouY+pH2*(i/4)+pH2));

			//画像の現座標レクト配列（24個）の初期化
			sell_rect[i]=new Rect(
					(gazouX+pW2*(i%4)),(gazouY+pH2*(i/4)),
					(gazouX+pW2*(i%4)+pW2),(gazouY+pH2*(i/4)+pH2));
		}

	}

//PuzzleViewクラスでSTARTをタッチした時に呼び出される。
//ランダムを発生させ、画像を再配置するため座標を変更
//--------------------------------------------------
	void init(){

		ArrayList<Integer> list=new ArrayList<Integer>();

		//0～23の数字をlistに入れる
		for(int i=0;i<img_rect.length;i++){
			list.add(i);
		}

		// 0～23の数字の順番をかき混ぜる
		Collections.shuffle(list);

		// コレクションの内容を表示する
		//System.out.println("listの内容 (シャッフル後)");
		//System.out.println(list);

		//System.out.println("入れ替え前");
		for(int i=0;i<img_rect.length;i++){
			kari_rect[i]=sell_rect[i];
		}

		//System.out.println("入れ替え後");
		for(int i=0;i<img_rect.length;i++){
			sell_rect[i]=kari_rect[list.get(i)];
		}
	}
//--------------------------------------------------
//タッチしたパネルを入れ替える
	void changePanel(){
		kari_rect[0]=sell_rect[PuzzleView.firstTouch];
		sell_rect[PuzzleView.firstTouch]=sell_rect[PuzzleView.twoTouch];
		sell_rect[PuzzleView.twoTouch]=kari_rect[0];
		PuzzleView.firstTouch=-1;	//タッチしたパネル番号の初期化
		PuzzleView.twoTouch=-1;
		checkmove();
	}
//----------------------------------------------------------------
//ＨＩＴ数
	void checkmove(){
		int x=0;
		for(int i=0;i<PIECE;i++){
			if(sell_rect[i].equals(position_rect[i])){
				x++;
			}
		}
		hitCount=x;
		//System.out.println("hitCount"+hitCount);
	}
//--------------------------------------------------
	public void panel_draw(Canvas canvas,int nowImage){
		for(int i=0;i<img_rect.length;i++){
			canvas.drawBitmap(img[nowImage],img_rect[i],sell_rect[i],new Paint());
		}
		if(hitCount<PIECE){
			Paint paint=new Paint();
			paint.setAntiAlias(true);
			paint.setTextSize(18*PuzzleView.dw);
			paint.setColor(Color.WHITE);
			canvas.drawText("HIT:"+hitCount+"/24",
					(int)(PuzzleView.dw*14),
					(int)(PuzzleView.dh*460),paint);

			paint.setTextSize(20*PuzzleView.dw);
			paint.setColor(Color.BLACK);
//			canvas.drawText("TIME:"+PuzzleView.message,110,35,paint);
		}
		//System.out.println("nowimg"+nowImage);
	}
}
