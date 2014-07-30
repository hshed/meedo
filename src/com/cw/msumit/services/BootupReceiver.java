package com.cw.msumit.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootupReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		if(Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
			Intent bootupService = new Intent(context, BootupService.class);
			context.startService(bootupService);
		}
	}

}
