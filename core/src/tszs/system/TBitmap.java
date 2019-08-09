/**==================================================================
 *
 *        工程名称:  tszs.system
 *        文件名称:  BitMap.java
 *        创 建 人  :  宋 刘 陈
 *        联系方式:  756519755
 *        个人网址:  http://www.songliuchen.com
 *        创建时间:  2016年9月10日
 *        修 改 人  : 
 *        修改时间: 
 *
===================================================================*/

package tszs.system;

import android.annotation.SuppressLint;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Build;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;

public class TBitmap
{
	/**
	 * 获取网落图片资源
	 * 
	 * @param url
	 * @return
	 */
	public static android.graphics.Bitmap GetHttpBitmap(java.lang.String url)
	{
		URL myFileURL;
		android.graphics.Bitmap bitmap = null;
		HttpURLConnection conn =null;
		InputStream is=null;
		try
		{
			myFileURL = new URL(url);
			// 获得连接
			conn = (HttpURLConnection) myFileURL.openConnection();
			// 设置超时时间为6000毫秒，conn.setConnectionTiem(0);表示没有时间限制
			conn.setConnectTimeout(6000);
			// 连接设置获得数据流
			conn.setDoInput(true);
			// 不使用缓存
			conn.setUseCaches(false);
			// 得到数据流
			is = conn.getInputStream();
			// 解析得到图片
			bitmap = BitmapFactory.decodeStream(is);
			return bitmap;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(conn !=null)
					conn.disconnect();

				if(is !=null)
					is.close();
			}
			catch (Exception e)
			{
			}
		}
		return bitmap;
	}

	/**
	 * 获取本地存储的图片
	 * 
	 * @param pathString
	 *            图片路径
	 * @return
	 */
	public static android.graphics.Bitmap GetDiskBitmap(java.lang.String pathString)
	{
		FileInputStream stream=null;
		try
		{
			android.graphics.Bitmap bitmap = null;
			File file = new File(pathString);
			if(file.exists())
			{
				stream = new FileInputStream(file);
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inPurgeable = true;
				options.inInputShareable = true;
				options.inPreferredConfig = Config.RGB_565;
				bitmap = BitmapFactory.decodeStream(stream, null, options);
			}
			return bitmap;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
		finally
		{
			try
			{
				if(stream !=null)
					stream.close();
			}
			catch (Exception e)
			{
			}
		}
	}

	/**
	 * 获取本地存储的图片
	 * 
	 * @param pathString
	 *            图片路径
	 * @return
	 */
	public static android.graphics.Bitmap GetDiskThumbBitmap(java.lang.String pathString)
	{
		return GetDiskThumbBitmap(pathString, 20);
	}

	/**
	 * 获取本地存储的图片
	 * 
	 * @param pathString
	 *            图片路径
	 * @return
	 */
	public static android.graphics.Bitmap GetDiskThumbBitmap(java.lang.String pathString, Integer scale)
	{
		if(TString.IsNullOrEmpty(pathString))
			return null;
		
		if(pathString.toUpperCase().indexOf(".MP4")!=-1)
			return CreateVideoThumbnail(pathString);
		
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = false;
		options.inPreferredConfig = Config.RGB_565;
		options.inSampleSize = scale; // width，hight设为原来的十分一
		options.inDither = true;
		android.graphics.Bitmap bitmap = null;
		try
		{
			File file = new File(pathString);
			if(file.exists())
			{
				bitmap = BitmapFactory.decodeFile(pathString, options);
			}
			else
			{
				return null;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return bitmap;
	}

	/**
	 * 获取本地存储的图片
	 * 
	 * @param pathString
	 *            图片路径
	 * @return
	 */
	public static android.graphics.Bitmap GetDiskThumbBitmap(java.lang.String pathString, Integer scale, Integer width, Integer height)
	{
		// BitmapFactory.Options options = new BitmapFactory.Options();
		// options.inJustDecodeBounds = false;
		// options.inPreferredConfig = Config.RGB_565;
		// options.inSampleSize = scale; //width，hight设为原来的十分一
		// options.inDither = true;
		// android.graphics.Bitmap bitmap = null;
		// try
		// {
		// File file = new File(pathString);
		// if(file.exists())
		// {
		// bitmap = BitmapFactory.decodeFile(pathString,options);
		// }
		// else
		// {
		// return null;
		// }
		// }
		// catch (Exception e)
		// {
		// e.printStackTrace();
		// }
		// return bitmap;
		return null;
	}

	/**
	 * 根据资源id 获取图片
	 * 
	 * @param bitmapid
	 *            资源id
	 * @return
	 */
	public static android.graphics.Bitmap GetResourceBitmap(Integer bitmapid)
	{
		android.graphics.Bitmap bitmap = BitmapFactory.decodeResource(tszs.system.config.ConfigManager.getContext().getResources(), bitmapid);
		return bitmap;
	}

	/**
	 * 压缩图片
	 * 
	 * @param filePath
	 *            原图片路径
	 * @param targetPath
	 *            压缩后图片存储路径
	 * @param quality
	 *            压缩质量,例如30
	 * @return 生成压缩后图片路径
	 */
	public static Boolean CompressImage(java.lang.String filePath, java.lang.String targetPath, Integer quality)
	{
		android.graphics.Bitmap tempbitmap = BitmapFactory.decodeFile(filePath);
		return CompressImage(tempbitmap, targetPath, quality);
	}

	/**
	 * 压缩图片
	 * 
	 * @param bitmap
	 *            图片对象
	 * @param targetPath
	 *            压缩后图片存储路径
	 * @param quality
	 *            压缩质量,例如30
	 * @return 生成压缩后图片路径
	 */
	public static Boolean CompressImage(android.graphics.Bitmap bitmap, java.lang.String targetPath, Integer quality)
	{
		FileOutputStream out=null;
		try
		{
			File tempfile = new File(targetPath);
			if(!tempfile.getParentFile().exists())
			{
				TPath.CreatePath(TPath.GetFilePath(targetPath));
				tempfile = new File(targetPath);
			}
			out = new FileOutputStream(tempfile);
			bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, quality, out);
			out.flush();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
			return false;
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return false;
		}
		finally
		{
			try
			{
				if(out!=null)
					out.close();

				if(bitmap!=null)
				{
					bitmap.recycle();
					bitmap = null;
				}
			}
			catch (Exception ex)
			{

			}
		}
		return true;
	}

	/**
	 * 根据路径获得图片信息并按比例压缩，返回bitmap
	 */
	private static android.graphics.Bitmap getSmallBitmap(java.lang.String filePath)
	{
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;// 只解析图片边沿，获取宽高
		BitmapFactory.decodeFile(filePath, options);
		// 计算缩放比
		options.inSampleSize = calculateInSampleSize(options, 480, 800);
		// 完整解析图片返回bitmap
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(filePath, options);
	}

	// 计算图片的缩放值
	private static Integer calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight)
	{
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if(height > reqHeight || width > reqWidth)
		{
			final int heightRatio = Math.round((float) height / (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}
		return inSampleSize;
	}

	/**
	 * 获取照片角度
	 * 
	 * @param path
	 * @return
	 */
	private static Integer readPictureDegree(java.lang.String path)
	{
		int degree = 0;
		try
		{
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
			switch (orientation)
			{
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return degree;
	}

	/**
	 * 旋转照片
	 * 
	 * @param bitmap
	 * @param degress
	 * @return
	 */
	private static android.graphics.Bitmap RotateBitmap(android.graphics.Bitmap bitmap, Integer degress)
	{
		if(bitmap != null)
		{
			Matrix m = new Matrix();
			m.postRotate(degress);
			bitmap = android.graphics.Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
			return bitmap;
		}
		return bitmap;
	}

	/**
	 * 保存图片到本地
	 * 
	 * @param path
	 *            存储路径
	 * @param bitmap
	 *            图片对象
	 */

	public static Boolean SaveBitmap(java.lang.String path, android.graphics.Bitmap bitmap)
	{
		if(bitmap == null)
			return false;

		java.lang.String fileapth = TPath.GetFilePath(path);
		File tempdir = new File(fileapth);
		Boolean success = false;
		if(tempdir.exists())
		{
			File f = new File(path);
			if(f.exists())
			{
				success = f.delete();
			}
			else
			{
				success = true;
			}
		}
		else
		{
			success = TPath.CreatePath(TPath.GetFilePath(path));
		}

		if(success)
		{
			try
			{
				tempdir = new File(path); 
                tempdir.createNewFile();
				while (!tempdir.exists())
				{
					tempdir.createNewFile();
				}
				FileOutputStream out = new FileOutputStream(tempdir);
				bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, out);
				out.flush();
				out.close();
				return true;
			}
			catch (FileNotFoundException e)
			{
				e.printStackTrace();
				return false;
			}
			catch (IOException e)
			{
				e.printStackTrace();
				return false;
			}
		}
		else
		{
			return false;
		}
	}

	@SuppressLint("NewApi")
	public static android.graphics.Bitmap CreateVideoThumbnail(java.lang.String filePath)
	{
		Class<?> clazz = null;
		Object instance = null;
		try
		{
			clazz = Class.forName("android.media.MediaMetadataRetriever");
			instance = clazz.newInstance();

			Method method = clazz.getMethod("setDataSource", java.lang.String.class);
			method.invoke(instance, filePath);

			// The method name changes between API Level 9 and 10.
			if(Build.VERSION.SDK_INT <= 9)
			{
				return (android.graphics.Bitmap) clazz.getMethod("captureFrame").invoke(instance);
			}
			else
			{
				byte[] data = (byte[]) clazz.getMethod("getEmbeddedPicture").invoke(instance);
				if(data != null)
				{
					android.graphics.Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
					if(bitmap != null)
						return bitmap;
				}
				return (android.graphics.Bitmap) clazz.getMethod("getFrameAtTime").invoke(instance);
			}
		}
		catch (IllegalArgumentException ex)
		{
			// Assume this is a corrupt video file
		}
		catch (RuntimeException ex)
		{
			// Assume this is a corrupt video file.
		}
		catch (InstantiationException e)
		{
			Log.e("TAG", "createVideoThumbnail", e);
		}
		catch (InvocationTargetException e)
		{
			Log.e("TAG", "createVideoThumbnail", e);
		}
		catch (ClassNotFoundException e)
		{
			Log.e("TAG", "createVideoThumbnail", e);
		}
		catch (NoSuchMethodException e)
		{
			Log.e("TAG", "createVideoThumbnail", e);
		}
		catch (IllegalAccessException e)
		{
			Log.e("TAG", "createVideoThumbnail", e);
		}
		finally
		{
			try
			{
				if(instance != null)
				{
					clazz.getMethod("release").invoke(instance);
					instance= null;
				}
			}
			catch (Exception ignored)
			{
			}
		}
		return null;
	}
}
