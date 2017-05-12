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
	
	/*-------- �� ������ ���� -----*/

	//�Է°� ����Ʈ�� ����
	EditText [] edtNums   = new EditText[6];
	Integer	 [] edtNumIDs = { R.id.edtNum1, R.id.edtNum1Bot, R.id.edtNum1Top, 	//�Է°�(1)�� ����, ����, �и�
							  R.id.edtNum2, R.id.edtNum2Bot, R.id.edtNum2Top };	//�Է°�(2)�� ����, ����, �и�
	
	//������ ����Ʈ�� ����
	EditText edtOper;							
	
	//��°� �ؽ�Ʈ�� ����
	TextView tvResult, tvResultTop, tvResultBot; //��°��� ����, ����, �и�
	
	//���� ��ư�� ���� (0~9) ** �齺���̽� ��ư�� �Բ� ������.
	Button	[] numButtons 	= new Button[10];
	Integer	[] numButtonIDs	= {R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4, R.id.btn5, 
							   R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9, R.id.btn0};
	
	//�����ڹ�ư ���� ( +, -, ��, �� )
	Button	[] opButtons 	= new Button[4];
	int		[] opBtnIDs 	= {R.id.btnPlus, R.id.btnMinus, R.id.btnMultiply, R.id.btnDivide}; // ������ �迭
	
	Button  btnBackSpace, btnCleanAll, btnNext, btnPrev, // �齺���̽� ��ư, ��� �����, ��������, ��������
			btnShowResult1,btnShowResult2;				 // ���� ����
	
	ImageView row1, row2, row3;
	
	MyDBHelper myHelper;
	SQLiteDatabase sqlDB;
	
//	EditText currentView = edtNums[1];	// 	���� ��Ŀ���� ���� ��
//	EditText previousView;	//	currentView ������ ��Ŀ���� ���� ��
	
	
//	void changeFocus(EditText edt){ // ��Ŀ���� �̵��� �����ϴ� �޼ҵ�
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
	
	
	
	/*--------�� ������ ���� ��-------*/
	
	
	/*---------���� ����--------------*/ 
	
	// ���� : ���� �ȿ� �� ���ڵ�
	
	String 	num1,num1Top,num1Bot, 		// ù��° ������ ����, ����, �и�
			num2,num2Top,num2Bot; 		// ù��° ������ ����, ����, �и�
	
	String oper;		// ������
	
	String result;		// ��������� ����
	String resultTop;	// ��������� ����
	String resultBot;	// ��������� �и�
	
	
	int rslt, rsltTop, rsltBot; 			// ��������� ����
	
	/*---------- ���� ���� �� ----------*/ 
	
	
	
	/*----------����� ǥ��޼ҵ� (4���� ���)----------*/ 
	
	// 1. ���������� �̷���� ����� ��) 3
	void showIntTypeResult(){
		rslt += (rsltTop / rsltBot);
		
		tvResult	.setText(rslt+"");
		tvResultTop	.setText("");
		tvResultBot	.setText("");
	}
	
	// 2. ��м��� �̷���� ����� ��) 3 1/2
	void showResult(){
		tvResult	.setText(rslt+"");
		tvResultTop	.setText(rsltTop+"");
		tvResultBot	.setText(rsltBot+"");
	}
	
	// 3. ���� ���� �м��θ� �̷���� ����� ��) 1/7
	void showFResult(){
		tvResult	.setText("");
		tvResultTop	.setText(rsltTop+"");
		tvResultBot	.setText(rsltBot+"");
	}
	
	// 4. ������ȣ�� �м��� �̷���� ����� ��) - 1/7
	void showMinusResult(){
		tvResult	.setText("-");
		tvResultTop	.setText(rsltTop+"");
		tvResultBot	.setText(rsltBot+"");
	}
	
	
	
	//��� ����
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

	// ������� ����
	int orderNum 	= 3;
	int orderNum1 	= orderNum % 3;
	
	//���๮ ����
	String sql = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setTitle("�м�����");
		
		Log.d("MyTag", "aaa" );
		
		setWidget(); 	// ���� ����
		setButtons(); 	// ��ư ����
		setListener(); 	// ������ ���� 
		
		/*--- ��� �����--- */ 
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
	

	
	
	/* �������� */
	private void setWidget() {
		
		// ���ڹ�ư
		for (int i = 0; i < edtNums.length; i++) {
			edtNums[i] = (EditText) findViewById(edtNumIDs[i]);
		}
		
		edtOper 	= (EditText)findViewById(R.id.edtOper);
		
		tvResult 	= (TextView)findViewById(R.id.tvResult);
		tvResultTop = (TextView)findViewById(R.id.tvResultTop);
		tvResultBot = (TextView)findViewById(R.id.tvResultBot);
	}
	
	/*��ư���� */
	private void setButtons() {
		
		// ���ڹ�ư
		for (int i = 0; i < numButtons.length; i++) {
			numButtons[i] = (Button) findViewById(numButtonIDs[i]);
		}
		
		// �����ڹ�ư
		for (int i = 0; i < opButtons.length; i++) {
			opButtons[i] = (Button) findViewById(opBtnIDs[i]);
		}
		
		
		
		// ��������ư
		btnShowResult1 	= (Button)findViewById(R.id.btnShowResult1);
		btnShowResult2 	= (Button)findViewById(R.id.btnShowResult2);
		btnBackSpace 	= (Button)findViewById(R.id.btnBackSpace);
		
		btnNext			= (Button)findViewById(R.id.btnNext);
		btnPrev			= (Button)findViewById(R.id.btnPrev);
		
	}
	
	/* ������ ���� */
	private void setListener() {
		// ���ڹ�ư
		for (int i = 0; i < numButtons.length; i++) {
			numButtons[i].setOnClickListener(numBtnListener);
		}
				
		// �����ڹ�ư
		for (int i = 0; i < opButtons.length; i++) {
			opButtons[i].setOnClickListener(opBtnListener);
		}
		
		// ������� ��ư
		btnShowResult1.setOnClickListener(showRsltListener);
		btnShowResult2.setOnClickListener(showRsltListener);
		
		
		//�齺���̽� ���� �� ��Ŭ�� ������ �ޱ�
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

	// 0���� ������ ���ٴ� �佺Ʈ�޽��� ����. 
	// �佺Ʈ �޽����� �״�� ����ϴ� �� ��� ������ �̸��� ���� �޼ҵ带 ����� ���� onClick�� �ٱ��� ������.
	
//	public void indivisible(View v){
//		Toast.makeText(getApplicationContext(), "0���� ������ �����ϴ�.",
//		Toast.LENGTH_SHORT).show();
//	}
	
	
	
	/*---------------- �������� ��ư ������ -----------------*/
	
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

	
	/*---------------- �������� ��ư ������ -----------------*/
	
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
	

	
	/*---------------- �ҷ����� ��ư ������ -----------------*/
	
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
			dlg.setTitle("�ҷ��� ������� ������");
			dlg.setItems(histroyArray, 
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							//������ ���� �� ���
							
						}
					});
			dlg.setPositiveButton("�ݱ�", null);
			dlg.show();
			
			
			
			
			
			
		}
		
		
	};
	
	
	
	/*---------------- ������� ��ư ������ -----------------*/
	
	View.OnClickListener showRsltListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
			orderNum ++;//���������� ������� ������ 1�� �÷���.
			
			num1 			= edtNums[0].getText().toString(); 		// ù��° ������ ����
			num1Bot 		= edtNums[1].getText().toString(); 		// ù��° ������ �и�
			num1Top 		= edtNums[2].getText().toString();  	// ù��° ������ ����

			num2 			= edtNums[3].getText().toString(); 		// �ι�° ������ ����
			num2Bot 		= edtNums[4].getText().toString();  	// �ι�° ������ �и�
			num2Top 		= edtNums[5].getText().toString();  	// �ι�° ������ ����
			
			/*������ ���� �Էµ��� �ʾ��� ��쿡 0���� ó����.*/

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
				Toast.makeText(getApplicationContext(), "�и��� ���� 0�� �ü� �����ϴ�.", Toast.LENGTH_SHORT).show();
			} else if (edtNums[4].getText().toString().equals("0") )		{
				edtNums[4].setText("");
				Toast.makeText(getApplicationContext(), "�и��� ���� 0�� �ü� �����ϴ�.", Toast.LENGTH_SHORT).show();
			} else{
			
			
			
			/*-- ���� �����--*/
			
			try {

				int n1		= Integer.parseInt(num1);
				int n2		= Integer.parseInt(num2);
				
				int n1Top 	= Integer.parseInt(num1Top);
				int n1Bot 	= Integer.parseInt(num1Bot);
				
				int n2Top	= Integer.parseInt(num2Top);
				int n2Bot 	= Integer.parseInt(num2Bot);
				
				// ���м�ȭ 
				n1Top = n1Top+(n1*n1Bot);
				n2Top = n2Top+(n2*n2Bot);
				
				String txt = edtOper.getText().toString();
				

				if (txt.equals("+")) {
					
					rslt		= 0;									//	������ 0���� �ʱ�ȭ(���м�ȭ �����Ƿ�) 
					rsltTop 	= (n1Top * n2Bot) + (n2Top * n1Bot);	//	���ڰ� ���ϱ�
					rsltBot 	= (n1Bot * n2Bot); 						//	�и� ���
					
					// ������ ��м�ȭ �� ǥ�� �޼ҵ� 
					
					// ���ڰ� �и�� �� ������ �������� ���
					if (rsltTop % rsltBot == 0) {
						showIntTypeResult();

					
					// ���ڰ� �и𺸴� ū ��� 
					} else if(rsltTop>rsltBot){
						
						// ���� ���
						
						reductRslt = reduction(rsltTop, rsltBot);
						
						rsltTop = reductRslt[0];	// ����� ����
						rsltBot = reductRslt[1];	// ����� �и�
						
						rslt += (rsltTop/rsltBot); 			//��м�ȭ
						rsltTop = (rsltTop%rsltBot);		//��м�ȭ
						
						showResult();
						
					// ���ڰ� �и𺸴� ���� ��� 
					} else if(rsltTop<rsltBot){
											
						reductRslt = reduction(rsltTop, rsltBot);
						
						rsltTop = reductRslt[0];	// ����� ����
						rsltBot = reductRslt[1];	// ����� �и�
						
						showFResult(); //������ ���� �м�Ÿ���� �����
						
					}
				
				} else if (txt.equals("-")) {
					
					//	����
					rslt		=	0;
					rsltTop 	= (n1Top * n2Bot) - (n2Top * n1Bot);
					rsltBot 	= (n1Bot * n2Bot);
					
					
					/*rsltTop�� ������� �Ǵ�.*/
					
					// ����̰ų� 0�� �� :
					if(rsltTop >= 0){					
						
						// ������ ǥ�� 
						if (rsltTop % rsltBot == 0) {		// ���ڰ� �и�� �� ������ �������� ���
							showIntTypeResult();			// ������ ����� ���
							
						} else if(rsltTop > rsltBot){		// ���ڰ� �и𺸴� ū ��� : �̰�쿡�� ������ ����� ��.  ��) 507/25
							
							reductRslt = reduction(rsltTop, rsltBot);// ���� ���
							
							rsltTop = reductRslt[0];	// ����� ����
							rsltBot = reductRslt[1];	// ����� �и�
							
							rslt += (rsltTop/rsltBot); 			//��м�ȭ : ���� 		������ ������.
							rsltTop = (rsltTop%rsltBot);		//��м�ȭ : �������� 	���ڰ�����.
							 
							showResult();
							
						// ���ڰ� �и𺸴� ���� ��� 
						} else if(rsltTop<rsltBot){
												
							reductRslt = reduction(rsltTop, rsltBot);
							
							rsltTop = reductRslt[0];	// ����� ����
							rsltBot = reductRslt[1];	// ����� �и�
							
							showFResult(); //������ ���� �м�Ÿ���� �����
							
						}
						
					// ������ �� :	
					}else if(rsltTop < 0){		
						reductRslt = reduction(rsltTop, rsltBot);// ���� ���
						
						rsltTop = reductRslt[0];	// ����� ����
						rsltBot = reductRslt[1];	// ����� �и�
						
						if ((-1)*rsltTop>rsltBot) {	// 	�������� ��м����̸� ��м��� ��ȯ,
							
							rsltTop  = (-1)*(rsltTop) ;
							rslt 	+= (rsltTop/rsltBot); 			//��м�ȭ : ���� 		������ ������.
							rsltTop  = (rsltTop%rsltBot);			//��м�ȭ : �������� 	���ڰ�����.
							rslt	 = (-1)*rslt;
							
							showResult();
							
						}else if((-1)*rsltTop<rsltBot){	//	�������� ���м����̸� - �ٿ���.
							rsltTop = rsltTop*(-1);
							showMinusResult();
						}
						
					}
					
					

				

				} else if (txt.equals("��")) {
					
					rslt		=	0;
					rsltTop = (n1Top * n2Top);
					rsltBot = (n1Bot * n2Bot);
					
					
					// ������ ��м�ȭ �� ǥ�� �޼ҵ� 
					
					// ���ڰ� �и�� �� ������ �������� ���
					if (rsltTop % rsltBot == 0) {
						
						showIntTypeResult();

					
					// ���ڰ� �и𺸴� ū ��� 
					} else if(rsltTop>rsltBot){
						
						// ���� ���
						
						reductRslt = reduction(rsltTop, rsltBot);
						
						rsltTop = reductRslt[0];	// ����� ����
						rsltBot = reductRslt[1];	// ����� �и�
						
						rslt 		+= (rsltTop/rsltBot); 		//��м�ȭ
						rsltTop 	 = (rsltTop%rsltBot);		//��м�ȭ
						 
						showResult();
						
						
					// ���ڰ� �и𺸴� ���� ��� 
					} else if(rsltTop<rsltBot){
											
						reductRslt = reduction(rsltTop, rsltBot);
						
						rsltTop = reductRslt[0];	// ����� ����
						rsltBot = reductRslt[1];	// ����� �и�
						
						showFResult(); //������ ���� �м�Ÿ���� �����
						
					}
					
				
				} else if (txt.equals("��")) {

					rslt		=	0;
					rsltTop	= (n1Top * n2Bot);
					rsltBot 	= (n1Bot * n2Top);
					
					
					// ������ ��м�ȭ �� ǥ�� �޼ҵ� 
					
					// ���ڰ� �и�� �� ������ �������� ���
					if (rsltTop % rsltBot == 0) {
						
						showIntTypeResult();

					
					// ���ڰ� �и𺸴� ū ��� 
					} else if(rsltTop>rsltBot){
						
						// ���� ���
						
						reductRslt = reduction(rsltTop, rsltBot);
						
						rsltTop = reductRslt[0];		// ����� ����
						rsltBot = reductRslt[1];		// ����� �и�
						
						rslt += (rsltTop/rsltBot); 		//��м�ȭ
						rslt = (rsltTop%rsltBot);		//��м�ȭ
						 
						showResult();
						
						
					// ���ڰ� �и𺸴� ���� ��� 
					} else if(rsltTop<rsltBot){
											
						reductRslt = reduction(rsltTop, rsltBot);
						
						rsltTop = reductRslt[0];	// ����� ����
						rsltBot = reductRslt[1];	// ����� �и�
						
						showFResult(); //������ ���� �м�Ÿ���� �����
						
					} // if (rsltTop % rsltBot == 0)
					
				} // if (txt.equals("+"))

			} catch (NumberFormatException e) {
				Toast.makeText(getApplicationContext(), "���ڸ� �Է��ϼ���",
				Toast.LENGTH_SHORT).show();
			} // try
			
			}
			tvResult	.setTextColor(Color.RED);
			tvResultTop	.setTextColor(Color.RED);
			tvResultBot	.setTextColor(Color.RED);
			
				
			//����������ͺ��̽��� ����
			
			
			//���๮
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
				Toast.makeText(getApplicationContext(), "���ڸ� �Է��ϼ���",
				Toast.LENGTH_SHORT).show();
			}
			
			
			
		} // onClick
		
	}; //showRsltListener
	
	
	
	/*---------------- ������ ��ư ������ -----------------*/
	
	View.OnClickListener opBtnListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if (edtOper.isFocused()) {
				
				if (edtOper.getText().toString().equals("0")) {
					edtOper.setText("");
				}
				oper = ((Button) v).getText().toString();
				edtOper.setText(oper);
				edtNums[3].requestFocus(); // �����ڰ��� �Էµǰ���, ��ٷ� ����ĭ�� edtNum2Top���� ��Ŀ���� �̵���.
			} 
		}
			
	}; // opBtnListener
	
	
	
	//	�齺���̽���ư �޼ҵ�
	
	String BackSpace(EditText edt){
		if (edt.getText().toString().equals("")){
			edt.setText("");
		}
		String num = edt.getText().toString();
		edt.setText(num.substring(0, num.length()-1));
		return num;
	}
	
	
	/*---------------- �齺���̽� ��ư ������ -----------------*/
	
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
				Toast.makeText(getApplicationContext(), "������ڰ� �����ϴ�.", Toast.LENGTH_SHORT).show();
			}
			
			
		}
		
	}; // BackSpaceBtnListener
	
	
	// ���ڹ�ư ������ �޼ҵ�ȭ
	
	void numInput(View v, EditText editTextLocation, String editTextNumber){
		
		if (isZeroValue(editTextLocation.getText().toString())) {
			editTextLocation.setText("");// isZeroValue�� ����Ͽ� ó�� �Է��� ���� 0�̸� null������ ��ȯ
		}
		
		editTextNumber = editTextLocation.getText().toString() 
						+ ((Button) v).getText().toString(); 
		editTextLocation.setText(editTextNumber);
	}
	
	/*---------------- ���� ��ư ������ -----------------*/
	
	
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
				Toast.makeText(getApplicationContext(), "����Ʈ �ؽ�Ʈ�� �����ϼ���.",
						Toast.LENGTH_SHORT).show();
			} 

		} // onClick
	}; // numBtnListener
	
	
}


