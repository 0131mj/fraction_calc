package com.bmj.individual_project;

import android.app.*;
import android.content.*;
import android.database.*;
import android.database.sqlite.*;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.graphics.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.widget.*;


public class MainActivity extends Activity{
	
	/*-------- 각 위젯의 선언 -----*/

	//입력값 에디트뷰 선언
	EditText [] edtNums   = new EditText[6];
	Integer	 [] edtNumIDs = { R.id.edtNum1, R.id.edtNum1Bot, R.id.edtNum1Top, 	//입력값(1)의 정수, 분자, 분모
							  R.id.edtNum2, R.id.edtNum2Bot, R.id.edtNum2Top };	//입력값(2)의 정수, 분자, 분모
	
	//연산자 에디트뷰 선언
	EditText edtOper;							
	
	//출력값 텍스트뷰 선언
	TextView tvResult, tvResultTop, tvResultBot; //출력값의 정수, 분자, 분모
	
	//숫자 버튼의 정의 (0~9) ** 백스페이스 버튼도 함께 정의함.
	Button	[] numButtons 	= new Button[10];
	Integer	[] numButtonIDs	= {R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4, R.id.btn5, 
							   R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9, R.id.btn0};
	
	//연산자버튼 정의 ( +, -, ×, ÷ )
	Button	[] opButtons 	= new Button[4];
	int		[] opBtnIDs 	= {R.id.btnPlus, R.id.btnMinus, R.id.btnMultiply, R.id.btnDivide}; // 연산자 배열
	
	Button  btnBackSpace, btnCleanAll, btnNext, btnPrev, // 백스페이스 버튼, 모두 지우기, 다음으로, 이전으로
			btnShowResult1,btnShowResult2;				 // 연산 실행
	
	ImageView row1, row2, row3;
	
	MyDBHelper myHelper;
	SQLiteDatabase sqlDB;
	
//	EditText currentView = edtNums[1];	// 	현재 포커스를 얻은 뷰
//	EditText previousView;	//	currentView 직전에 포커스를 얻은 뷰
	
	
//	void changeFocus(EditText edt){ // 포커스의 이동을 저장하는 메소드
//		Log.d("MyTag", "changeFocus" );
//		previousView = currentView;
//		currentView = edt;
//		
//		Log.d("MyTag", previousView.toString());
//		Log.d("MyTag", currentView.toString());
//		
//		if (currentView != previousView){
//			edt.setText("");
//		}
//	}
	
	
	
	/*--------각 위젯의 정의 끝-------*/
	
	
	/*---------변수 선언--------------*/ 
	
	// 변수 : 위젯 안에 들어갈 숫자들
	
	String 	num1,num1Top,num1Bot, 		// 첫번째 숫자의 정수, 분자, 분모
			num2,num2Top,num2Bot; 		// 첫번째 숫자의 정수, 분자, 분모
	
	String oper;		// 연산자
	
	String result;		// 결과숫자의 정수
	String resultTop;	// 결과숫자의 분자
	String resultBot;	// 결과숫자의 분모
	
	
	int rslt, rsltTop, rsltBot; 			// 결과숫자의 정수
	
	/*---------- 변수 선언 끝 ----------*/ 
	
	
	
	/*----------결과값 표기메소드 (4가지 경우)----------*/ 
	
	// 1. 정수만으로 이루어진 결과값 예) 3
	void showIntTypeResult(){
		rslt += (rsltTop / rsltBot);
		
		tvResult	.setText(rslt+"");
		tvResultTop	.setText("");
		tvResultBot	.setText("");
	}
	
	// 2. 대분수로 이루어진 결과값 예) 3 1/2
	void showResult(){
		tvResult	.setText(rslt+"");
		tvResultTop	.setText(rsltTop+"");
		tvResultBot	.setText(rsltBot+"");
	}
	
	// 3. 정수 없이 분수로만 이루어진 결과값 예) 1/7
	void showFResult(){
		tvResult	.setText("");
		tvResultTop	.setText(rsltTop+"");
		tvResultBot	.setText(rsltBot+"");
	}
	
	// 4. 음수부호와 분수로 이루어진 결과값 예) - 1/7
	void showMinusResult(){
		tvResult	.setText("-");
		tvResultTop	.setText(rsltTop+"");
		tvResultBot	.setText(rsltBot+"");
	}
	
	
	
	//약분 로직
	static int[] reductRslt = new int[2];
	
	public static int[] reduction(int tNum, int bNum) {

		int[] reduct = new int[2];
		for (int i = bNum; i >= 1; i--) {
			if (bNum % i == 0 && tNum % i == 0) {

				tNum /= i;
				bNum /= i;
				break;
			}
		}

		reduct[0] = tNum;
		reduct[1] = bNum;
		
		if (tNum>bNum) {
			
		}
		
		return reduct;
	}

	// 결과값의 순서
	int orderNum 	= 3;
	int orderNum1 	= orderNum % 3;
	
	//실행문 선언
	String sql = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setTitle("분수계산기");
		
		Log.d("MyTag", "aaa" );
		
		setWidget(); 	// 위젯 설정
		setButtons(); 	// 버튼 설정
		setListener(); 	// 리스너 설정 
		
		/*--- 모두 지우기--- */ 
		btnCleanAll  =(Button)findViewById(R.id.btnCleanAll);
		btnCleanAll.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				for (int i = 0; i < edtNums.length; i++) {
					edtNums[i].setText("");
				}
				
				tvResult	.setText("");
				tvResultTop	.setText("");
				tvResultBot	.setText("");
			}
		});
		
		btnCleanAll.setLongClickable(true);
		btnCleanAll.setOnLongClickListener(new View.OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				edtNums[0].setText(tvResult.getText().toString());
				edtNums[1].setText(tvResultBot.getText().toString());
				edtNums[2].setText(tvResultTop.getText().toString());
				
				for (int i = 3; i < 6; i++) {
					edtNums[i].setText("");
				}
				
				tvResult	.setText("");
				tvResultTop	.setText("");
				tvResultBot	.setText("");
				
				edtNums[3].requestFocus();
				return true;
			}
		});
		
		myHelper = new MyDBHelper(this, "groupDB", null, 1);
		
		
	}
	
	
	
	class MyDBHelper extends SQLiteOpenHelper {

		public MyDBHelper(
				Context context, 
				String name, 
				CursorFactory factory,
				int version) {
			super(context, name, factory, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			String sql = "CREATE TABLE groupTBL ( "
					+ "orderNum INTEGER, "
					+ "Num1 	INTEGER, "
					+ "Num1Bot 	INTEGER, "
					+ "Num1Top 	INTEGER, "
					+ "Operater	CHAR(20), "
					+ "Num2 	INTEGER, "
					+ "Num2Bot	INTEGER, "
					+ "Num2Top	INTEGER, "
					+ "Result INTEGER, "
					+ "ResultTop INTEGER, "
					+ "ResultBot INTEGER)";
			db.execSQL(sql);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			String sql = "DROP TABLE IF EXISTS groupTBL";
			db.execSQL(sql);
			onCreate(db);
		}
		
	}
	

	
	
	/* 위젯정의 */
	private void setWidget() {
		
		// 숫자버튼
		for (int i = 0; i < edtNums.length; i++) {
			edtNums[i] = (EditText) findViewById(edtNumIDs[i]);
		}
		
		edtOper 	= (EditText)findViewById(R.id.edtOper);
		
		tvResult 	= (TextView)findViewById(R.id.tvResult);
		tvResultTop = (TextView)findViewById(R.id.tvResultTop);
		tvResultBot = (TextView)findViewById(R.id.tvResultBot);
	}
	
	/*버튼정의 */
	private void setButtons() {
		
		// 숫자버튼
		for (int i = 0; i < numButtons.length; i++) {
			numButtons[i] = (Button) findViewById(numButtonIDs[i]);
		}
		
		// 연산자버튼
		for (int i = 0; i < opButtons.length; i++) {
			opButtons[i] = (Button) findViewById(opBtnIDs[i]);
		}
		
		
		
		// 연산실행버튼
		btnShowResult1 	= (Button)findViewById(R.id.btnShowResult1);
		btnShowResult2 	= (Button)findViewById(R.id.btnShowResult2);
		btnBackSpace 	= (Button)findViewById(R.id.btnBackSpace);
		
		btnNext			= (Button)findViewById(R.id.btnNext);
		btnPrev			= (Button)findViewById(R.id.btnPrev);
		
	}
	
	/* 리스너 장착 */
	private void setListener() {
		// 숫자버튼
		for (int i = 0; i < numButtons.length; i++) {
			numButtons[i].setOnClickListener(numBtnListener);
		}
				
		// 연산자버튼
		for (int i = 0; i < opButtons.length; i++) {
			opButtons[i].setOnClickListener(opBtnListener);
		}
		
		// 결과보기 버튼
		btnShowResult1.setOnClickListener(showRsltListener);
		btnShowResult2.setOnClickListener(showRsltListener);
		
		
		//백스페이스 정의 및 온클릭 리스너 달기
		btnBackSpace.setOnClickListener(backSpaceBtnListener);
		btnNext		.setOnClickListener(btnNextListener);
		btnPrev		.setOnClickListener(btnPrevListener);
		
//		edtNum1.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				changeFocus(edtNum1);
//			}
//		});
		
	}
	
	
	private boolean isZeroValue(String s) {
		if (s.equals("0")) {
			return true;
		} else {
			return false;
		}
	}

	// 0으로 나눌수 없다는 토스트메시지 생성. 
	// 토스트 메시지를 그대로 출력하는 게 길기 때문에 이름을 붙인 메소드를 만들기 위해 onClick의 바깥에 정의함.
	
//	public void indivisible(View v){
//		Toast.makeText(getApplicationContext(), "0으로 나눌수 없습니다.",
//		Toast.LENGTH_SHORT).show();
//	}
	
	
	
	/*---------------- 다음으로 버튼 리스너 -----------------*/
	
	View.OnClickListener btnNextListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
			if(edtNums[0].isFocused())		{
				edtNums[1].requestFocus();	
			}else if(edtNums[1].isFocused()){
				edtNums[2].requestFocus();	
			}else if(edtNums[2].isFocused()){
				edtOper.requestFocus();	
			}else if(edtOper.isFocused()){
				edtNums[3].requestFocus();	
			}else if(edtNums[3].isFocused()){
				edtNums[4].requestFocus();	
			}else if(edtNums[4].isFocused()){
				edtNums[5].requestFocus();	
			}else if(edtNums[5].isFocused()){
				edtNums[0].requestFocus();	
			}
		}
	};

	
	/*---------------- 이전으로 버튼 리스너 -----------------*/
	
	View.OnClickListener btnPrevListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
			if(edtNums[0].isFocused())		{
				edtNums[5].requestFocus();	
			}else if(edtNums[5].isFocused()){
				edtNums[4].requestFocus();	
			}else if(edtNums[4].isFocused()){
				edtNums[3].requestFocus();	
			}else if(edtNums[3].isFocused()){
				edtOper.requestFocus();	
			}else if(edtOper.isFocused()){
				edtNums[2].requestFocus();	
			}else if(edtNums[2].isFocused()){
				edtNums[1].requestFocus();	
			}else if(edtNums[1].isFocused()){
				edtNums[0].requestFocus();	
			}
		}
	}; 
	

	
	/*---------------- 불러오기 버튼 리스너 -----------------*/
	
	View.OnClickListener btnLoadListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
			sqlDB = myHelper.getReadableDatabase();
			sql = "SELECT * FROM groupTBL"
				+ " where orderNum = 1";
			Cursor cursor = sqlDB.rawQuery(sql, null);
			
			String strOrderNum	= orderNum + "";
			String strNum1 		= "";
			String strNum1Bot 	= "";
			String strNum1Top 	= "";
			String strOperater 	= "";
			String strNum2 		= "";
			String strNum2Bot 	= "";
			String strNum2Top 	= "";
			String strResult 	= "";
			String strResultBot	= "";
			String strResultTop	= "";
			while (cursor.moveToNext()) {
				
				strOrderNum		+= cursor.getString(0) + "";
				strNum1 		+= cursor.getString(1) + "";
				strNum1Bot 		+= cursor.getString(2) + "";
				strNum1Top 		+= cursor.getString(3) + "";
				strOperater 	+= cursor.getString(4) + "";
				strNum2 		+= cursor.getString(5) + "";
				strNum2Bot 		+= cursor.getString(6) + "";
				strNum2Top 		+= cursor.getString(7) + "";
				strResult 		+= cursor.getString(8) + "";
				strResultBot 	+= cursor.getString(9) + "";
				strResultTop 	+= cursor.getString(10)+ "";
			}
			
			String Result1 = strNum1 + "  " + strNum1Bot + "/" + strNum1Top + " " + strOperater + " " 
							+strNum2 + "  " + strNum2Bot + "/" + strNum2Top + " = "
   						   +strResult+ "  "+strResultBot + "/"+ strResultTop;	
						
			cursor.close();
			sqlDB.close();

//			Toast.makeText(getApplicationContext(), Result1, Toast.LENGTH_SHORT).show();
			
			final String[] histroyArray = new String[]{Result1,"Result2","Result3"};
			AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
			dlg.setTitle("불러올 결과값을 고르세요");
			dlg.setItems(histroyArray, 
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							//실제로 들어가게 될 명령
							
						}
					});
			dlg.setPositiveButton("닫기", null);
			dlg.show();
			
			
			
			
			
			
		}
		
		
	};
	
	
	
	/*---------------- 결과보기 버튼 리스너 -----------------*/
	
	View.OnClickListener showRsltListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
			orderNum ++;//누를때마다 결과값의 순서를 1씩 올려줌.
			
			num1 			= edtNums[0].getText().toString(); 		// 첫번째 숫자의 정수
			num1Bot 		= edtNums[1].getText().toString(); 		// 첫번째 숫자의 분모
			num1Top 		= edtNums[2].getText().toString();  	// 첫번째 숫자의 분자

			num2 			= edtNums[3].getText().toString(); 		// 두번째 숫자의 정수
			num2Bot 		= edtNums[4].getText().toString();  	// 두번째 숫자의 분모
			num2Top 		= edtNums[5].getText().toString();  	// 두번째 숫자의 분자
			
			/*정수의 값이 입력되지 않았을 경우에 0으로 처리함.*/

			if (num1.equals("")){
				num1 = "0";
			}else if (num2.equals("")){
				num2 = "0";
			}if (num1.equals("")||num2.equals("")){
				num1 = "0";
				num2 = "0";
			}
			
			if (edtNums[1].getText().toString().equals("0") )		{
				edtNums[1].setText("");
				Toast.makeText(getApplicationContext(), "분모의 값에 0이 올수 없습니다.", Toast.LENGTH_SHORT).show();
			} else if (edtNums[4].getText().toString().equals("0") )		{
				edtNums[4].setText("");
				Toast.makeText(getApplicationContext(), "분모의 값에 0이 올수 없습니다.", Toast.LENGTH_SHORT).show();
			} else{
			
			
			
			/*-- 실제 연산부--*/
			
			try {

				int n1		= Integer.parseInt(num1);
				int n2		= Integer.parseInt(num2);
				
				int n1Top 	= Integer.parseInt(num1Top);
				int n1Bot 	= Integer.parseInt(num1Bot);
				
				int n2Top	= Integer.parseInt(num2Top);
				int n2Bot 	= Integer.parseInt(num2Bot);
				
				// 가분수화 
				n1Top = n1Top+(n1*n1Bot);
				n2Top = n2Top+(n2*n2Bot);
				
				String txt = edtOper.getText().toString();
				

				if (txt.equals("+")) {
					
					rslt		= 0;									//	정수값 0으로 초기화(가분수화 했으므로) 
					rsltTop 	= (n1Top * n2Bot) + (n2Top * n1Bot);	//	분자값 더하기
					rsltBot 	= (n1Bot * n2Bot); 						//	분모 통분
					
					// 연산결과 대분수화 및 표기 메소드 
					
					// 분자가 분모로 딱 나누어 떨어지는 경우
					if (rsltTop % rsltBot == 0) {
						showIntTypeResult();

					
					// 분자가 분모보다 큰 경우 
					} else if(rsltTop>rsltBot){
						
						// 먼저 약분
						
						reductRslt = reduction(rsltTop, rsltBot);
						
						rsltTop = reductRslt[0];	// 약분한 분자
						rsltBot = reductRslt[1];	// 약분한 분모
						
						rslt += (rsltTop/rsltBot); 			//대분수화
						rsltTop = (rsltTop%rsltBot);		//대분수화
						
						showResult();
						
					// 분자가 분모보다 작은 경우 
					} else if(rsltTop<rsltBot){
											
						reductRslt = reduction(rsltTop, rsltBot);
						
						rsltTop = reductRslt[0];	// 약분한 분자
						rsltBot = reductRslt[1];	// 약분한 분모
						
						showFResult(); //정수가 없는 분수타입의 결과값
						
					}
				
				} else if (txt.equals("-")) {
					
					//	연산
					rslt		=	0;
					rsltTop 	= (n1Top * n2Bot) - (n2Top * n1Bot);
					rsltBot 	= (n1Bot * n2Bot);
					
					
					/*rsltTop의 양수음수 판단.*/
					
					// 양수이거나 0일 때 :
					if(rsltTop >= 0){					
						
						// 연산결과 표기 
						if (rsltTop % rsltBot == 0) {		// 분자가 분모로 딱 나누어 떨어지는 경우
							showIntTypeResult();			// 정수형 결과값 출력
							
						} else if(rsltTop > rsltBot){		// 분자가 분모보다 큰 경우 : 이경우에는 무조건 양수가 됨.  예) 507/25
							
							reductRslt = reduction(rsltTop, rsltBot);// 먼저 약분
							
							rsltTop = reductRslt[0];	// 약분한 분자
							rsltBot = reductRslt[1];	// 약분한 분모
							
							rslt += (rsltTop/rsltBot); 			//대분수화 : 몫은 		정수부 값으로.
							rsltTop = (rsltTop%rsltBot);		//대분수화 : 나머지는 	분자값으로.
							 
							showResult();
							
						// 분자가 분모보다 작은 경우 
						} else if(rsltTop<rsltBot){
												
							reductRslt = reduction(rsltTop, rsltBot);
							
							rsltTop = reductRslt[0];	// 약분한 분자
							rsltBot = reductRslt[1];	// 약분한 분모
							
							showFResult(); //정수가 없는 분수타입의 결과값
							
						}
						
					// 음수일 때 :	
					}else if(rsltTop < 0){		
						reductRslt = reduction(rsltTop, rsltBot);// 먼저 약분
						
						rsltTop = reductRslt[0];	// 약분한 분자
						rsltBot = reductRslt[1];	// 약분한 분모
						
						if ((-1)*rsltTop>rsltBot) {	// 	연산결과가 대분수형이면 대분수로 변환,
							
							rsltTop  = (-1)*(rsltTop) ;
							rslt 	+= (rsltTop/rsltBot); 			//대분수화 : 몫은 		정수부 값으로.
							rsltTop  = (rsltTop%rsltBot);			//대분수화 : 나머지는 	분자값으로.
							rslt	 = (-1)*rslt;
							
							showResult();
							
						}else if((-1)*rsltTop<rsltBot){	//	연산결과가 순분수형이면 - 붙여줌.
							rsltTop = rsltTop*(-1);
							showMinusResult();
						}
						
					}
					
					

				

				} else if (txt.equals("×")) {
					
					rslt		=	0;
					rsltTop = (n1Top * n2Top);
					rsltBot = (n1Bot * n2Bot);
					
					
					// 연산결과 대분수화 및 표기 메소드 
					
					// 분자가 분모로 딱 나누어 떨어지는 경우
					if (rsltTop % rsltBot == 0) {
						
						showIntTypeResult();

					
					// 분자가 분모보다 큰 경우 
					} else if(rsltTop>rsltBot){
						
						// 먼저 약분
						
						reductRslt = reduction(rsltTop, rsltBot);
						
						rsltTop = reductRslt[0];	// 약분한 분자
						rsltBot = reductRslt[1];	// 약분한 분모
						
						rslt 		+= (rsltTop/rsltBot); 		//대분수화
						rsltTop 	 = (rsltTop%rsltBot);		//대분수화
						 
						showResult();
						
						
					// 분자가 분모보다 작은 경우 
					} else if(rsltTop<rsltBot){
											
						reductRslt = reduction(rsltTop, rsltBot);
						
						rsltTop = reductRslt[0];	// 약분한 분자
						rsltBot = reductRslt[1];	// 약분한 분모
						
						showFResult(); //정수가 없는 분수타입의 결과값
						
					}
					
				
				} else if (txt.equals("÷")) {

					rslt		=	0;
					rsltTop	= (n1Top * n2Bot);
					rsltBot 	= (n1Bot * n2Top);
					
					
					// 연산결과 대분수화 및 표기 메소드 
					
					// 분자가 분모로 딱 나누어 떨어지는 경우
					if (rsltTop % rsltBot == 0) {
						
						showIntTypeResult();

					
					// 분자가 분모보다 큰 경우 
					} else if(rsltTop>rsltBot){
						
						// 먼저 약분
						
						reductRslt = reduction(rsltTop, rsltBot);
						
						rsltTop = reductRslt[0];		// 약분한 분자
						rsltBot = reductRslt[1];		// 약분한 분모
						
						rslt += (rsltTop/rsltBot); 		//대분수화
						rslt = (rsltTop%rsltBot);		//대분수화
						 
						showResult();
						
						
					// 분자가 분모보다 작은 경우 
					} else if(rsltTop<rsltBot){
											
						reductRslt = reduction(rsltTop, rsltBot);
						
						rsltTop = reductRslt[0];	// 약분한 분자
						rsltBot = reductRslt[1];	// 약분한 분모
						
						showFResult(); //정수가 없는 분수타입의 결과값
						
					} // if (rsltTop % rsltBot == 0)
					
				} // if (txt.equals("+"))

			} catch (NumberFormatException e) {
				Toast.makeText(getApplicationContext(), "숫자를 입력하세요",
				Toast.LENGTH_SHORT).show();
			} // try
			
			}
			tvResult	.setTextColor(Color.RED);
			tvResultTop	.setTextColor(Color.RED);
			tvResultBot	.setTextColor(Color.RED);
			
				
			//결과값데이터베이스에 저장
			
			
			//실행문
			sqlDB = myHelper.getWritableDatabase();
			
			try {
				
				sql = 	 "INSERT INTO groupTBL VALUES ( "+  
						+ orderNum1						  + ","
						+ edtNums[0].getText().toString() + ","
						+ edtNums[1].getText().toString() + ","
						+ edtNums[2].getText().toString() + ", '"
						+ edtOper	.getText().toString() + "',"
						+ edtNums[3].getText().toString() + ","
						+ edtNums[4].getText().toString() + ","
						+ edtNums[5].getText().toString() + ","
						+ tvResult	.getText().toString() + ","
						+ tvResultBot.getText().toString()+ ","
						+ tvResultTop.getText().toString()+ ")";
				
				Log.d("MyTag", sql);
				
				sqlDB.execSQL(sql);
				sqlDB.close();
				
			} catch (SQLException e) {
				Toast.makeText(getApplicationContext(), "숫자를 입력하세요",
				Toast.LENGTH_SHORT).show();
			}
			
			
			
		} // onClick
		
	}; //showRsltListener
	
	
	
	/*---------------- 연산자 버튼 리스너 -----------------*/
	
	View.OnClickListener opBtnListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if (edtOper.isFocused()) {
				
				if (edtOper.getText().toString().equals("0")) {
					edtOper.setText("");
				}
				oper = ((Button) v).getText().toString();
				edtOper.setText(oper);
				edtNums[3].requestFocus(); // 연산자값이 입력되고나서, 곧바로 다음칸인 edtNum2Top으로 포커스가 이동됨.
			} 
		}
			
	}; // opBtnListener
	
	
	
	//	백스페이스버튼 메소드
	
	String BackSpace(EditText edt){
		if (edt.getText().toString().equals("")){
			edt.setText("");
		}
		String num = edt.getText().toString();
		edt.setText(num.substring(0, num.length()-1));
		return num;
	}
	
	
	/*---------------- 백스페이스 버튼 리스너 -----------------*/
	
	View.OnClickListener backSpaceBtnListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			try {
				for (int i = 0; i < edtNums.length; i++) {
					if(edtNums[i].isFocused()){
						BackSpace(edtNums[i]);
					}
				}
				
			} catch (Exception e) {
				Toast.makeText(getApplicationContext(), "지울글자가 없습니다.", Toast.LENGTH_SHORT).show();
			}
			
			
		}
		
	}; // BackSpaceBtnListener
	
	
	// 숫자버튼 리스너 메소드화
	
	void numInput(View v, EditText editTextLocation, String editTextNumber){
		
		if (isZeroValue(editTextLocation.getText().toString())) {
			editTextLocation.setText("");// isZeroValue를 사용하여 처음 입력한 값이 0이면 null값으로 변환
		}
		
		editTextNumber = editTextLocation.getText().toString() 
						+ ((Button) v).getText().toString(); 
		editTextLocation.setText(editTextNumber);
	}
	
	/*---------------- 숫자 버튼 리스너 -----------------*/
	
	
	View.OnClickListener numBtnListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			
			
			if (edtNums[0].isFocused()) {
				numInput(v, edtNums[0], num1);

			} else if (edtNums[1].isFocused()) {
				numInput(v, edtNums[1], num1Top);

			} else if (edtNums[2].isFocused()) {
				numInput(v, edtNums[2], num1Bot);

			} else if (edtNums[3].isFocused()) {
				numInput(v, edtNums[3], num2);

			} else if (edtNums[4].isFocused()) {
				numInput(v, edtNums[4], num2Top);

			} else if (edtNums[5].isFocused()) { 
				numInput(v, edtNums[5], num2Bot);

			} else {
				Toast.makeText(getApplicationContext(), "에디트 텍스트를 선택하세요.",
						Toast.LENGTH_SHORT).show();
			} 

		} // onClick
	}; // numBtnListener
	
	
}


