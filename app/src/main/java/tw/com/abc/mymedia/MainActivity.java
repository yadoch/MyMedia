package tw.com.abc.mymedia;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {
    private AudioManager audioManager;
    private SeekBar seekBar;
    private MediaGBService mService;
    boolean isConnect;
    private ListView listview;
    private SimpleAdapter adapter;
    private String[] from = {"title","singer"};
    private int[] to = {R.id.title, R.id.singer};
    private LinkedList<HashMap<String,String>> data;

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

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,},
                    0);
        }else{
            init();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            init();
        }else{
            finish();
        }
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
    public void test3(View view) {
        Intent it = new Intent(this,Page2.class);
        startActivity(it);
    }

    

    private void init(){
        seekBar =(SeekBar) findViewById(R.id.seekbar);
        listview=(ListView) findViewById(R.id.listview);


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
        getMusicList();
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

    private void getMusicList() {
        // 順序為程式架構撰寫順序
        //3.透過MediaMetadataRetriever 取得檔案資訊
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();

        //1.取出音樂檔位置-之前有寫展示範例
        File musicPath =
                Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_MUSIC);

        File[] musics = musicPath.listFiles();
        //2.利用遞迴逐層取出目錄中的音樂檔案進行處理
        for (File music : musics) {
            if (music.isDirectory()) {
                File[] smusics = music.listFiles();
                for (File smusic : smusics) {
                    if (!smusic.isFile()) continue;
                    // 3.1 取出檔案資訊 子目錄
                    mmr.setDataSource(smusic.toString());

                    String title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                    String singer = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);

                    HashMap<String, String> musicInfo = new HashMap<>();
                    musicInfo.put("file", music.getAbsolutePath());
                    musicInfo.put(from[0], "sub:" + title);
                    musicInfo.put(from[1], singer);

                    data.add(musicInfo);
                }
            } else if (music.isFile()) {
                // 3.1 取出檔案資訊 第一層目錄
                mmr.setDataSource(music.toString());

                String title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                String singer = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);


                HashMap<String, String> musicInfo = new HashMap<>();
                musicInfo.put("file", music.getAbsolutePath());
                musicInfo.put(from[0], title);
                musicInfo.put(from[1], singer);

                data.add(musicInfo);
            }
        }
        adapter=new SimpleAdapter(this,data,R.layout.item,from,to);
        listview.setAdapter(adapter);
    }
    
}
