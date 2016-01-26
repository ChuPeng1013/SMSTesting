package com.example.smstesting;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

public class LoginActivity extends Activity implements OnClickListener
{
	private EditText inputPhoneEt;
	private EditText inputCodeEt;
	private Button requestCodeBtn;
	private Button commitBtn;
	
	private int Event;
	private int Result;
	private Object Data;
	
	int i = 30;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);
		init();
	}

	private void init() 
	{
		inputPhoneEt = (EditText) findViewById(R.id.login_input_phone_et);
		inputCodeEt = (EditText) findViewById(R.id.login_input_code_et);
		requestCodeBtn = (Button) findViewById(R.id.login_request_code_btn);
		commitBtn = (Button) findViewById(R.id.login_commit_btn);
		
		requestCodeBtn.setOnClickListener(this);
		commitBtn.setOnClickListener(this);
		
		SMSSDK.initSDK(this, "e9c0723f3203", "70e79f94921a22c5c967e6be341a1a9e");
		EventHandler eventHandler = new EventHandler()
		{

			@Override
			public void afterEvent(int event, int result, Object data) 
			{
				Message msg = new Message();
				msg.arg1 = event;
				msg.arg2 = result;
				msg.obj = data;
				handler.sendMessage(msg);
			}
			
		};
		SMSSDK.registerEventHandler(eventHandler);
	}

	public void onClick(View v) 
	{
		String phoneNums = inputPhoneEt.getText().toString();
		if(v.getId() == R.id.login_request_code_btn)
		{
			SMSSDK.getVerificationCode("86", phoneNums);
			requestCodeBtn.setClickable(false);
			requestCodeBtn.setText("重新发送(" + i + ")");
			new Thread(new Runnable()
			{
				public void run() 
				{
					for(; i>0; i--)
					{
						handler.sendEmptyMessage(-9);
						if(i<=0)
						{
							break;
						}
						try
						{
							Thread.sleep(1000);
							
						}catch(Exception e)
						{
							e.printStackTrace();
						}
					}
					handler.sendEmptyMessage(-8);
				}
				
			}).start();
		}
		else if(v.getId() == R.id.login_commit_btn)
		{
			SMSSDK.submitVerificationCode("86", phoneNums, inputCodeEt.getText().toString());
		}
		
			
	}
	
	Handler handler = new Handler()
	{

		public void handleMessage(Message msg) 
		{
			
			if(msg.what == -9)
			{
				requestCodeBtn.setText("重新发送(" + i + ")");
			}
			else if(msg.what == -8)
			{
				requestCodeBtn.setText("获取验证码");
				requestCodeBtn.setClickable(true);
				i = 30;
			}
			else if(msg.what == -10)
			{
				int event = msg.arg1;
				int result = msg.arg2;
				Object data = msg.obj;
				if(result == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE)
				{
					Toast.makeText(getApplicationContext(), "提交验证码成功", Toast.LENGTH_SHORT).show();
					Intent intent = new Intent(LoginActivity.this, MainActivity.class);
					startActivity(intent);
				}
				else if(event == SMSSDK.EVENT_GET_VERIFICATION_CODE)
				{
					Toast.makeText(getApplicationContext(), "验证码已经发送", Toast.LENGTH_SHORT).show();
				}
			}
		}
	};
	protected void onDestroy() 
	{
		SMSSDK.unregisterAllEventHandler();
		super.onDestroy();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
