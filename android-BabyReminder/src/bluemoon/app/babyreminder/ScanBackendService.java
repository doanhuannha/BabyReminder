package bluemoon.app.babyreminder;

import android.app.IntentService;
import android.content.Intent;


public class ScanBackendService extends IntentService {

	public ScanBackendService() {
		super("RequestBackendService");
		// TODO Auto-generated constructor stub
		
	}
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		startForeground(101, MainActivity.s_Current.buildNotification(R.drawable.ic_launcher, "Watching..."));
	}
	@Override
	protected void onHandleIntent(Intent workIntent) {
		int counter = 0;
		MainActivity.s_Current.pingBBWifi();
		MainActivity.s_Current.tryConnectWifi();
		while(MainActivity.s_Current!=null){
			
			try {
				Thread.sleep(1000);
				counter++;
				if(counter%15==0) MainActivity.s_Current.pingBBWifi();
				if(counter%60==0) MainActivity.s_Current.tryConnectWifi();
				
				if(counter>=60){ 
					counter=0;
					MainActivity.s_Current.scanWifi();
				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				MainActivity.s_Current.showMessage(e.getMessage());
			}
			
		}
		
	}

}
