package com.example.phonefir;

import java.lang.reflect.Method;
import com.android.internal.telephony.ITelephony;
import com.newqm.sdkoffer.QuMiConnect;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity implements OnClickListener {

	private EditText et_666;
	private EditText et_777;
	private EditText et_888;
	private Button bt_start;
	private TelephonyManager telephonyManager;
	private String phone = "10000";
	private int num = 3;
	private int interval = 3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		QuMiConnect.ConnectQuMi(this, "240d9de062b0dd53", "7fbbf9c85d875647");
		
		setContentView(R.layout.activity_main);
		

		et_666 = (EditText) findViewById(R.id.et_666);// 号码
		et_777 = (EditText) findViewById(R.id.et_777);// 次数
		et_888 = (EditText) findViewById(R.id.et_888);// 间隔
		bt_start = (Button) findViewById(R.id.bt_start);

		telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		telephonyManager.listen(new PhoneListener(), PhoneStateListener.LISTEN_CALL_STATE);

		bt_start.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.bt_start) {

			phone = et_666.getText().toString().trim();

			String n = et_777.getText().toString().trim();
			if (!TextUtils.isEmpty(n)) {
				num = Integer.valueOf(n);
			}

			String inter = et_888.getText().toString().trim();
			if (!TextUtils.isEmpty(inter)) {
				interval = Integer.valueOf(inter);
			}

			a(num);
		}
	}

	private int i;

	private void a(int number) {
		i = number;
		i = i - 1;
		Log.e("aaa", "num为" + i);
		if (i > 0) {
			Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
			startActivity(intent);
			int time = Integer.valueOf(interval) * 1000;
			TimeCount timeCount = new TimeCount(time, 1000);
			timeCount.start();
		}
	}

	private class TimeCount extends CountDownTimer {
		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
		}

		@Override
		public void onFinish() {// 计时完毕时触发
			endCall();
			a(i);
			this.cancel();
		}

		@Override
		public void onTick(long millisUntilFinished) {// 计时过程显示
		}
	}

	// 监听电话状态
	class PhoneListener extends PhoneStateListener {
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);

			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:// 来电状态
				Log.e("aaa", "RINGING");
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:// 接听状态
				Log.e("aaa", "OFFHOOK");
				return;
			case TelephonyManager.CALL_STATE_IDLE:// 挂断后回到空闲状态
				Log.e("aaa", "IDLE");
				break;
			default:
				Log.e("aaa", "default");
				break;
			}

		}
	}

	private void endCall() {
		// 初始化iTelephony
		Class<TelephonyManager> c = TelephonyManager.class;
		Method getITelephonyMethod = null;
		try {
			// 获取所有public/private/protected/默认
			// 方法的函数，如果只需要获取public方法，则可以调用getMethod.
			getITelephonyMethod = c.getDeclaredMethod("getITelephony", (Class[]) null);
			// 将要执行的方法对象设置是否进行访问检查，也就是说对于public/private/protected/默认
			// 我们是否能够访问。值为 true 则指示反射的对象在使用时应该取消 Java 语言访问检查。值为 false
			// 则指示反射的对象应该实施 Java 语言访问检查。
			getITelephonyMethod.setAccessible(true);
			ITelephony iTelephony = (ITelephony) getITelephonyMethod.invoke(telephonyManager, (Object[]) null);
			iTelephony.endCall();
			Log.v(this.getClass().getName(), "endCall......");
		} catch (Exception e) {
			Log.e(this.getClass().getName(), "endCallError", e);
		}
	}

}
