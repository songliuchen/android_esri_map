package tszs.system;

import android.annotation.SuppressLint;
import android.app.Application;

/**
 * Created by songliuchen on 2018/4/21.
 */

public class TApplication extends Application
{
    @SuppressLint("NewApi")
    @Override
    public void onCreate()
    {
        super.onCreate();

        //初始化它山之石框架容器
        tszs.system.config.ConfigManager.setContext(this.getApplicationContext());
    }
}
