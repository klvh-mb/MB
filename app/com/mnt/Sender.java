package com.mnt;


import java.io.IOException;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;

public class Sender {
    private static final String AUTHORIZATION_KEY = "AIzaSyDsN0c8yM8ZNl97SWhxUfJvVet14F6atfk";
    
    public static boolean sendNotificationMessage(String regId, String userMessage) throws IOException {
    	try {
			com.google.android.gcm.server.Sender sender = new com.google.android.gcm.server.Sender(AUTHORIZATION_KEY);
			Message message = new Message.Builder().timeToLive(30).collapseKey("message")
                    .delayWhileIdle(true)
                    .addData("message", userMessage).build();
			Result result = sender.send(message, regId, 1);
			System.out.println(result);
			return true;
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return false;
    }
}
