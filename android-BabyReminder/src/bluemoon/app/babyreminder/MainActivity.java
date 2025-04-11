package bluemoon.app.babyreminder;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import bluemoon.framework.ui.BaseActivity;
import bluemoon.framework.util.StringUtilities;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.*;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.*;
import android.net.wifi.*;
import android.os.Bundle;
import android.os.Vibrator;

public class MainActivity extends BaseActivity {

	
	ConnectivityManager.NetworkCallback _connCallback = null;
	static final int VIEW_MAIN= 1;
	static MainActivity s_Current = null;
	boolean _connected = false;
	MediaPlayer _player = null;
	static final String SSID = "BB-WIFI";
	static final String SSID_PWD = "hongcopass";
	int _netId = 0;
	WifiManager _wifiManager = null;
	String _ssid_quote = null;
	ConnectivityManager _connManager = null;
	BroadcastReceiver _receiver = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		s_Current = this;
		_connManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		
		
        _ssid_quote= "\"" + SSID + "\"";
        _wifiManager = (WifiManager)getSystemService(Context.WIFI_SERVICE);
        List<WifiConfiguration> allConf = _wifiManager.getConfiguredNetworks();
        if(allConf!=null){
        	for(WifiConfiguration conf: allConf){
    			if(conf.SSID.contentEquals(_ssid_quote)){
    				_netId = conf.networkId;
    				break;
    			}
    		}
        }
		
		if(_netId==0){
			WifiConfiguration conf = new WifiConfiguration();
			conf.SSID = _ssid_quote;   // Please note the quotes. String should contain ssid in quotes
			//WPA
			conf.preSharedKey = "\""+ SSID_PWD +"\"";
			conf.status = WifiConfiguration.Status.ENABLED;        
	        conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
	        conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
	        conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
	        conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
	        conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
	        conf.allowedProtocols.set(WifiConfiguration.Protocol.RSN);

	        _netId = _wifiManager.addNetwork(conf);
			
		}
        
		//*
		IntentFilter i = new IntentFilter();
		i.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
		i.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
		i.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
		i.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		_receiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context c, Intent intent) {
            	tryConnectWifi();
            }
        };
		registerReceiver(_receiver, i);
		//*/
		setContentView(new MainView(VIEW_MAIN,this));
		
		//scanIntent = setAlarm(this, ScanService.class, 15000);
		//pingIntent = setAlarm(this, PingService.class, 5000);
		//addNotification("KEEP ALIVE",false);
		Intent intent = new Intent(this, ScanBackendService.class);
        startService(intent);
        _networkCallback = new ConnectivityManager.NetworkCallback() {
			@Override
			public void onAvailable(Network network) {
				_connManager.unregisterNetworkCallback(this);
				if(_requestNetworkTimeout) return;
				_requestNetworkTimeout = true;
				WifiInfo wifiInfo = _wifiManager.getConnectionInfo();
				if(_ssid_quote.contentEquals(wifiInfo.getSSID())){
					try {
						if(!_connected){
							_connected = true;
							_shouldRinging = false;
							addNotification(R.drawable.ic_reminder,"DON'T FORGET YOUR BABY!!");
						}
						URL url = new URL("http://10.11.12.13/");
						HttpURLConnection urlConnection = (HttpURLConnection) network.openConnection(url);
						urlConnection.setConnectTimeout(3000);
						BufferedReader r = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
						r.readLine();
						urlConnection.disconnect();
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						notifyParent();
					}
				}else{
					notifyParent();
				}
			}
		};
        
	}
	ConnectivityManager.NetworkCallback _networkCallback = null;
	boolean _requestNetworkTimeout = false;
	void pingBBWifi(){
		
		try{
			NetworkRequest.Builder req = new NetworkRequest.Builder();
			req = new NetworkRequest.Builder();
			req.addTransportType(NetworkCapabilities.TRANSPORT_WIFI);
			//_connCallback
			_requestNetworkTimeout = false;
			_connManager.requestNetwork(req.build(), _networkCallback);
			new Thread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					try {
						Thread.sleep(7000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if(!_requestNetworkTimeout){
						notifyParent();
						_requestNetworkTimeout = true;
						_connManager.unregisterNetworkCallback(_networkCallback);
					}
				}
			}).start();
		}
		catch(Exception ex){
			showMessage("Error on pingBBWifi\r\n"+StringUtilities.getStackTrace(ex));
		}
		
	}
	
	//int p = 0;
	//long _lastTime = 0;
	void scanWifi(){
		_wifiManager.startScan();
	}
	void tryConnectWifi(){
		if(_connected) return;
		//long now = System.currentTimeMillis();
		//addNotification("CHECK WIFI "+(p++)+ "("+((now-_lastTime)/1000)+")");
		//_lastTime = now;
		try{
			WifiInfo wifiInfo = _wifiManager.getConnectionInfo();
			if(_ssid_quote.contentEquals(wifiInfo.getSSID())) return;//it is connected to SSID
			if (_wifiManager.isWifiEnabled())
	        {
				
				List<ScanResult> results = _wifiManager.getScanResults();
	        	if(results==null) return;
	        	
	        	boolean found = false;
	        	for(ScanResult w: results){
	        		if(w.SSID.contentEquals(SSID)){
	        			found = true;
	        			break;
	        		}
	        	}
	        	if(found){
	    			if(_netId>0){
	        			_wifiManager.enableNetwork(_netId, true);
	    			}
	    			
	    			
	        	}
	        }
		}
		catch(Exception ex){
			showMessage("Error on tryConnectWifi\r\n"+ex.getMessage());
		}
		
	}
	void notifyParent(){
		
		if(_connected){
			
			_connected = false;
			ringRingRing();
			Intent intent = new Intent(this, this.getClass());
			intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP |Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
			startActivity(intent);
			
		}
		
		
	}
	@Override
	protected boolean onBackPressed(int viewId) {
		// TODO Auto-generated method stub
		moveTaskToBack(true);
		return true;
	}
	void confirm(){
		if(_shouldRinging){
			_shouldRinging = false;
			addNotification(R.drawable.ic_launcher, "Watching...");
		}

		moveTaskToBack(true);
	}
	void exit(){
		unregisterReceiver(_receiver);
		finish();
		System.exit(0);
	}
	boolean _shouldRinging = false;
	void ringRingRing(){
		//Uri path = ;
		final Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), Uri.parse("android.resource://"+getPackageName()+"/raw/cry"));
		final Vibrator v = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
		_shouldRinging = true;
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				while(_shouldRinging){
					v.vibrate(new long[] { 500, 500 }, -1);
					if(!r.isPlaying()){
						r.play();
					}
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if(r.isPlaying()) r.stop();
			}
		}).start();
	}
	Notification buildNotification(int icon, String title){
		Context activity = this;
		Notification.Builder mBuilder = new Notification.Builder(activity);
		//mBuilder.setAutoCancel(true);
		mBuilder.setSmallIcon(icon);
		//mBuilder.setContentText(title);
		mBuilder.setContentTitle(title);
		

		mBuilder.setOngoing(true);
		//mBuilder.setOnlyAlertOnce(true);
		//Vibration
		mBuilder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });
		

		//
		Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		mBuilder.setSound(alarmSound);
		
		
		Intent intent = new Intent(activity, activity.getClass());
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		//intent.setAction("android.intent.action.MAIN");
		
		PendingIntent resultPendingIntent =PendingIntent.getActivity(activity, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(resultPendingIntent);
		return  mBuilder.build();
	}
	void addNotification(int icon, String text){
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		
		mNotificationManager.notify(101, buildNotification(icon, text));
	}
	
}
