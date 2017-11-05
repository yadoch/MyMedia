package tw.com.abc.mymedia;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.os.Binder;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;

public class MainActivity extends AppCompatActivity {
    private AudioManager audioManager;
    private SeekBar seekBar;
    private MediaGBService mService;
    boolean isConnect;

    // 自動產生 Override 方法
    private ServiceConnection mCommection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MediaGBService.LocalBinder binder = (MediaGBService.LocalBinder) iBinder;
            mService = binder.getService();
            isConnect = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            isConnect = false;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();


    }
    public void test0(View view){
        audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD);

    }
    public void test1(View view){
        audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,AudioManager.ADJUST_RAISE,0);
        seekBar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
    }
    public void test2(View view){
        audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,AudioManager.ADJUST_LOWER,0);
        seekBar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
    }

    private void init(){
        seekBar =(SeekBar) findViewById(R.id.seekbar);



        audioManager = (AudioManager)getSystemService(AUDIO_SERVICE);
        int vol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        seekBar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        seekBar.setProgress(vol);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                // i 是由onProgressChanged(中的i)
                audioManager.adjustStreamVolume(audioManager.STREAM_MUSIC,i,0);
            }


            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        Intent it = new Intent(this, MediaGBService.class);
        bindService(it,mCommection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 中斷時暫停-非App 中止
        if(isConnect){
            unbindService(mCommection);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
