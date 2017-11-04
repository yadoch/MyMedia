package tw.com.abc.mymedia;

import android.media.AudioManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;

public class MainActivity extends AppCompatActivity {
    private AudioManager audioManager;
    private SeekBar seekBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        seekBar =(SeekBar) findViewById(R.id.seekbar);



        audioManager = (AudioManager)getSystemService(AUDIO_SERVICE);
        int vol = audioManager.getStreamMaxVolume()
        seekBar.setMax();
        AudioManager.ge

    }
    public void test0(View view){
        audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD);

    }
    public void test1(View view){
        audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,AudioManager.ADJUST_RAISE,0);

    }
    public void test2(View view){
        audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,AudioManager.ADJUST_LOWER,0);

    }
}
