package tszs.esrigis.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;

import tszs.esrigis.control.TMessage;
import tszs.system.TPath;
import tszs.system.TString;

import static android.R.attr.value;

/**
 * Created by songliuchen on 2018/4/21.
 * 用来打开系统视图面板
 * 例如选择文件对话框、摄像机、打开短信等
 */
public class IntentUtil
{
    //打开相机
    public static final int TAKE_PICTURE = 1;
    //选择文件
    public static final int FILE = 2;

    /**
     * 打开选择文件对话框
     * @param focused 当前activity对象
     * @param type 文件类型
     * @param title 文件标题
     */
    public static void openSelectFileIntent(Activity focused, String type, String title)
    {
        try
        {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/"+value);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            if(TString.IsNullOrEmpty(title))
                title = "选择上传附件";
            focused.startActivityForResult(Intent.createChooser(intent, title),FILE);
        }
        catch (android.content.ActivityNotFoundException ex)
        {
            TMessage.Show("未安装文件管理器");
        }
    }

    /**
     * 打开系统拍照功能
     * @param focused 当前页面
     * @param savePath 图片保存路径
     */
    public static void openCameraIntent(Activity focused,String savePath)
    {
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        //如果未给定保存路径，则默认存储到临时目录
        if(TString.IsNullOrEmpty(savePath))
            savePath = TPath.GetNewTempFilePath();
        Uri uri = Uri.fromFile(new File(savePath));
        // 指定存储路径，这样就可以保存原图了
        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        focused.startActivityForResult(openCameraIntent, TAKE_PICTURE);
    }
}
