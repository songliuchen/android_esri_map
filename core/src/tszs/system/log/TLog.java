package tszs.system.log;

import android.util.Log;

/**
 * 日志操作辅助类
 */
public class TLog
{
    /**
     * 打印日志
     * @param log 日志字符串
     * @param lever 日志类型
     */
    public static void Print(String log,Lever lever)
    {
        if (lever == Lever.Info)
        {
            Log.i("tszs", log);
        }
        else if (lever == Lever.Warn)
        {
            Log.w("tszs", log);
        }
        else if (lever == Lever.Error)
        {
            Log.e("tszs", log);
        }
        else
        {
            Log.i("tszs", log);
        }
    }

    public enum Lever
    {
        Info,
        Warn,
        Error
    }
}


