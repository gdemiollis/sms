package com.and1droid.smsquebec;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

import com.and1droid.smsquebec.HttpBuilder.HttpMethod;

import android.util.Log;

public class TelusMessenger {

	private static final String messengerUrl = "http://msg.telus.com/msg/HTTPPostExtMgr";

	public static void send(Texto message) {
		HttpURLConnection connect = null;
		try {
			HttpBuilder httpBuilder = new HttpBuilder(HttpMethod.POST,
					messengerUrl);

			Map<String, String> data = new HashMap<String, String>();
			data.put("CODE", message.getCode());
			data.put("NUM", message.getNum());
			data.put("MESSAGE", message.getMessage());
			data.put("COUNT", message.getCount());
			data.put("FROM_LABEL", "From: ");

			httpBuilder.data(data);
			Map<String, String> infosHeader = new HashMap<String, String>();
			infosHeader
					.put("Content-Type", "application/x-www-form-urlencoded");
			httpBuilder.header(infosHeader);
			connect = httpBuilder.connect();
		} catch (Exception e) {
			Log.e(TelusMessenger.class.getSimpleName(), e.getMessage(), e);
			throw new RuntimeException("Erreur lors de l'appel : "
					+ e.getMessage());
		} finally {
			if (connect != null) {
				connect.disconnect();
			}
		}
	}
}
