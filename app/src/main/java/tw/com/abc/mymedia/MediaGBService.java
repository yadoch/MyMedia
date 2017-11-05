package tw.com.abc.mymedia;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class MediaGBService extends Service {
    private  final  LocalBinder mBinder = new LocalBinder();
    public MediaGBService() {
    }

    public class LocalBinder extends Binder{
        MediaGBService getService (){
            return MediaGBService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {

        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
