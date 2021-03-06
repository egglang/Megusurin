package ma10.megusurin.bt200demo;

import jp.epson.moverio.bt200.AudioControl;
import jp.epson.moverio.bt200.DisplayControl;
import jp.epson.moverio.bt200.SensorControl;
import ma10.megusurin.R;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.ToggleButton;

public class BT200CtrlDemoActivity extends Activity {
	private String TAG = "Bt2CtrlDemoActivity";
	private ToggleButton mToggleButton_2d3d = null;
	private Button mButton_dmute = null;
	private SeekBar mSeekBar_backlight = null;
	private ToggleButton mToggleButton_amute = null;
	private ToggleButton mToggleButton_sensor = null;

	private DisplayControl mDisplayControl = null;
	private AudioControl mAudioControl = null;
	private SensorControl mSensorControl = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bt2_ctrl_demo);
		
		mDisplayControl = new DisplayControl(this);
		mAudioControl = new AudioControl(this);
		mSensorControl = new SensorControl(this);
		// 2D/3D変換
		mToggleButton_2d3d = (ToggleButton)findViewById(R.id.toggleButton_2d3d);
		mToggleButton_2d3d.setOnCheckedChangeListener(new ToggleButton.OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub
				
				if(arg1){
					Log.d(TAG,"set 3D display mode.");
					mDisplayControl.setMode(DisplayControl.DISPLAY_MODE_3D, true);
				}
				else{
					Log.d(TAG,"set 2D display mode.");
					mDisplayControl.setMode(DisplayControl.DISPLAY_MODE_2D, false);
				}
			}
	    });
	    // ディスプレイのミュートの設定変更
		mButton_dmute = (Button)findViewById(R.id.Button_dmute);
		mButton_dmute.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.d(TAG, "Set LCD mute ON. (display OFF)");
				mDisplayControl.setMute(true);
			    try{
			    	Thread.sleep(3000); //3000ミリ秒Sleepする
			    }catch(InterruptedException e){}

				Log.d(TAG, "Set LCD mute OFF. (display ON)");
				mDisplayControl.setMute(false);
			}
		});
	    // 明るさの設定変更
		mSeekBar_backlight = (SeekBar)findViewById(R.id.seekBar_backlight);
		mSeekBar_backlight.setMax(20);
		mSeekBar_backlight.setProgress(mDisplayControl.getBacklight());
		mSeekBar_backlight.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				Log.d(TAG, "Set LCD back-light level:"+progress);
				mDisplayControl.setBacklight(progress);
			}
		});
		
		// オーディオのミュートの設定変更
		mToggleButton_amute = (ToggleButton)findViewById(R.id.toggleButton_amute);
		mToggleButton_amute.setOnCheckedChangeListener(new ToggleButton.OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub
				
				if(arg1){
					Log.d(TAG,"Set audio mute ON.");
					mAudioControl.setMute(true);
				}
				else{
					Log.d(TAG,"set audio mute OFF.");
					mAudioControl.setMute(false);
				}
			}
	    });
		
		// センサーの切り替え
		mToggleButton_sensor = (ToggleButton)findViewById(R.id.toggleButton_sensor);
		if(SensorControl.SENSOR_MODE_CONTROLLER == mSensorControl.getMode()){
			mToggleButton_sensor.setChecked(true);
		}
		else{
			mToggleButton_sensor.setChecked(false);
		}
		mToggleButton_sensor.setOnCheckedChangeListener(new ToggleButton.OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if(arg1){
					Log.d(TAG,"set sensor of controller.");
					mSensorControl.setMode(SensorControl.SENSOR_MODE_CONTROLLER);
				}
				else{
					Log.d(TAG,"set sensor of headset.");
					mSensorControl.setMode(SensorControl.SENSOR_MODE_HEADSET);
				}
			}
		});

	}

}
