/**==================================================================
 *
 *        工程名称:  tszs.system
 *        文件名称:  Path.java
 *        创 建 人  :  宋 刘 陈
 *        联系方式:  756519755
 *        个人网址:  http://www.songliuchen.com
 *        创建时间:  2016年9月26日
 *        修 改 人  : 
 *        修改时间: 
 *
===================================================================*/

package tszs.system;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import java.io.File;

public class TPath
{
	public static java.lang.String GetFileName(java.lang.String path)
	{
		if(TString.IsNullOrEmpty(path))
			return "";
		
		if(path.contains("\\"))
		{
			Integer index = path.lastIndexOf("\\");
			return path.substring(index+1, path.length());
		}
		else if(path.contains("/"))
		{
			Integer index = path.lastIndexOf("/");
			return path.substring(index+1, path.length());
		}
		else
		{
			return "";
		}
	}
	
	
	public static java.lang.String GetFilePath(java.lang.String path)
	{
		if(TString.IsNullOrEmpty(path))
			return "";
		
		if(path.contains("\\"))
		{
			Integer index = path.lastIndexOf("\\");
			return path.substring(0, index+1);
		}
		else if(path.contains("/"))
		{
			Integer index = path.lastIndexOf("/");
			return path.substring(0, index+1);
		}
		else
		{
			return "";
		}
	}
	
	public static Boolean CreatePath(java.lang.String path)
	{
		if(TString.IsNullOrEmpty(path))
			return false;
		
		java.lang.String tempstring ="";
		if(path.contains("\\"))
		{
			Integer index = path.lastIndexOf("\\");
			tempstring =path.substring(0, index+1);
		
		}
		else if(path.contains("/"))
		{
			if(path.endsWith("/"))
				path = path.substring(0,path.length()-1);
			
			Integer index = path.lastIndexOf("/");
			tempstring =path.substring(0, index+1);
		}
		
		if(!TString.IsNullOrEmpty(tempstring))
		{
			File tempfile = new File(tempstring);
			if(!tempfile.exists())
			{
				Boolean success= tempfile.mkdir();
				if(success)
				{
					tempfile = new File(path);
					return tempfile.mkdir();
				}
				else
				{
					return false;
				}
			}
			else
			{
				tempfile = new File(path);
				if(!tempfile.exists())
					return tempfile.mkdir();
				else
				{
					return true;
				}
			}
		}
		return false;
	}
	
	public static java.lang.String GetTempPath()
	{
		return Environment.getExternalStorageDirectory()+"/tszs/temp/";
	}

	public static java.lang.String GetSystemPath()
	{
		return Environment.getExternalStorageDirectory()+"/tszs/";
	}
	
	public static java.lang.String GetNewTempFilePath()
	{
		java.lang.String path= GetTempPath();
		File file = new File(path);
		if(!file.exists())
		{
			Boolean success = file.mkdir();
			if(success)
			{
				 return path+ TString.GetNewFileNameByPath();
			}
		}
		else
		{
			return path+ TString.GetNewFileNameByPath();
		}
		return "";
	}
	
	public static Boolean IsFileExit(java.lang.String filepath)
	{
		File file = new File(filepath);
		return file.exists();
	}

	/**
	 * 专为Android4.4设计的从Uri获取文件绝对路径，以前的方法已不好使
	 */
	@SuppressLint("NewApi")
	public static java.lang.String GetPathFromUri(Uri uri)
	{

		final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
		// DocumentProvider
		if (isKitKat && DocumentsContract.isDocumentUri(tszs.system.config.ConfigManager.getContext(), uri)) {
			// ExternalStorageProvider
			if (isExternalStorageDocument(uri)) {
				final java.lang.String docId = DocumentsContract.getDocumentId(uri);
				final java.lang.String[] split = docId.split(":");
				final java.lang.String type = split[0];

				if ("primary".equalsIgnoreCase(type)) {
					return Environment.getExternalStorageDirectory() + "/" + split[1];
				}

				// TODO handle non-primary volumes
			}
			// DownloadsProvider
			else if (isDownloadsDocument(uri)) {

				final java.lang.String id = DocumentsContract.getDocumentId(uri);
				final Uri contentUri = ContentUris.withAppendedId(
						Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

				return getDataColumn(tszs.system.config.ConfigManager.getContext(), contentUri, null, null);
			}
			// MediaProvider
			else if (isMediaDocument(uri)) {
				final java.lang.String docId = DocumentsContract.getDocumentId(uri);
				final java.lang.String[] split = docId.split(":");
				final java.lang.String type = split[0];

				Uri contentUri = null;
				if ("image".equals(type)) {
					contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				} else if ("video".equals(type)) {
					contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				} else if ("audio".equals(type)) {
					contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				}

				final java.lang.String selection = "_id=?";
				final java.lang.String[] selectionArgs = new java.lang.String[] { split[1] };

				return getDataColumn(tszs.system.config.ConfigManager.getContext(), contentUri, selection, selectionArgs);
			}
		}
		// MediaStore (and general)
		else if ("content".equalsIgnoreCase(uri.getScheme())) {
			return getDataColumn(tszs.system.config.ConfigManager.getContext(), uri, null, null);
		}
		// File
		else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}

		return null;
	}

	/**
	 * Get the value of the data column for this Uri. This is useful for
	 * MediaStore Uris, and other file-based ContentProviders.
	 *
	 * @param context
	 *            The context.
	 * @param uri
	 *            The Uri to query.
	 * @param selection
	 *            (Optional) Filter used in the query.
	 * @param selectionArgs
	 *            (Optional) Selection arguments used in the query.
	 * @return The value of the _data column, which is typically a file path.
	 */
	private static java.lang.String getDataColumn(Context context, Uri uri, java.lang.String selection,
												 java.lang.String[] selectionArgs) {

		Cursor cursor = null;
		final java.lang.String column = "_data";
		final java.lang.String[] projection = { column };

		try {
			cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
					null);
			if (cursor != null && cursor.moveToFirst()) {
				final int column_index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(column_index);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return null;
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is ExternalStorageProvider.
	 */
	private static boolean isExternalStorageDocument(Uri uri) {
		return "com.android.externalstorage.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is DownloadsProvider.
	 */
	private static boolean isDownloadsDocument(Uri uri) {
		return "com.android.providers.downloads.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is MediaProvider.
	 */
	private static boolean isMediaDocument(Uri uri) {
		return "com.android.providers.media.documents".equals(uri.getAuthority());
	}
}
