package bluemoon.app.babyreminder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import bluemoon.framework.ui.BaseActivity;
import bluemoon.framework.ui.LinearLayout;

public class MainView extends LinearLayout {

	public MainView(int id, BaseActivity ctx) {
		super(id, ctx);
		// TODO Auto-generated constructor stub
		setOrientation(LinearLayout.VERTICAL);
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,ctx.getScreenHeight()/2);
		layoutParams.gravity =  Gravity.CENTER;
		layoutParams.setMargins(15, 0, 15, 1);
		
		final MainActivity activity = (MainActivity)ctx;
		ImageView image = new ImageView(ctx);
		image.setImageResource(R.drawable.bbincar);
		image.setLayoutParams(layoutParams);
		image.setScaleType(ScaleType.CENTER_INSIDE);
		this.addView(image);
		
		layoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
		layoutParams.gravity =  Gravity.CENTER;
		layoutParams.setMargins(15, 0, 15, 1);
		
		TextView text = new TextView(ctx);
		text.setTextColor(Color.rgb(245,130,38));
		text.setTextSize(17);
		text.setText("DON'T FORGET YOUR BABY\r\n");
		text.setGravity(Gravity.CENTER);
		text.setLayoutParams(layoutParams);
		this.addView(text);
		
		
		layoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,120);
		layoutParams.gravity =  Gravity.CENTER;
		layoutParams.setMargins(15, 0, 15, 1);
		
		
		Button bt = new Button(ctx);
		bt.setText("CONFIRM");
		bt.setTextSize(15);
		bt.setTextColor(Color.rgb(245,130,38));
		bt.setLayoutParams(layoutParams);
		bt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				activity.confirm();
			}
		});
		this.addView(bt);

		layoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
		layoutParams.gravity =  Gravity.CENTER;
		layoutParams.setMargins(15, 0, 15, 1);
		
		bt = new Button(ctx);
		bt.setText("Exit");
		bt.setLayoutParams(layoutParams);
		bt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new AlertDialog.Builder(activity)
				.setTitle("Do you want to stop the app?")
				.setMessage("Once you stop the app, no reminder might happen when you leave your car.")
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog, int whichButton) {
				    	activity.exit();
				    }})
				 .setNegativeButton("No", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						activity.onBackPressed();
					}
				})
				 .show();
				
			}
		});
		this.addView(bt);
		
		
		
	}
	String getTime(Date d) {
		return new SimpleDateFormat("yyyy.MM.dd HH:mm",Locale.US).format(d); //DateFormat.format("yyyy.MM.dd kk:mm", saveTime).toString();
	}
}
