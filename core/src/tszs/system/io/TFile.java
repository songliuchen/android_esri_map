/**=======================================================
 *
 *        工程名称:  com.tszs.core
 *        文件名称:  MFile.java
 *        创 建 人  :  宋 刘 陈
 *        联系方式:  756519755
 *        个人网址:  http://www.songliuchen.com
 *        创建时间:  2015年4月28日
 *        修 改 人  : 
 *        修改时间: 
 *
 *=======================================================*/

package tszs.system.io;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import tszs.system.TPath;

public class TFile extends java.lang.Object
{
	public static String ConvertToString(String path)
	{
		try
		{
			java.io.File file = new java.io.File(path);
			if(file.exists())
			{
				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file.getPath()), "UTF-8"));
				String tempvalue = br.readLine();
				String result = "";
				while (tempvalue != null)
				{
					result += tempvalue + " \n ";
					tempvalue = br.readLine();
				}
				br.close();
				return new String(result);
			}
			return null;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}

	public static void CopyAssetsFile(String assetsname, String newpath)
	{
		try
		{
			if(!TFile.Exits(newpath))
				TPath.CreatePath(newpath);
			InputStream myInput;
			OutputStream myOutput = new FileOutputStream(newpath);
			myInput = tszs.system.config.ConfigManager.getContext().getAssets().open(assetsname);
			byte[] buffer = new byte[1024];
			int length = myInput.read(buffer);
			while (length > 0)
			{
				myOutput.write(buffer, 0, length);
				length = myInput.read(buffer);
			}

			myOutput.flush();
			myInput.close();
			myOutput.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public static void CopyAssetssFolder(String newpath,String subpath)
	{
		InputStream myInput= null;
		OutputStream myOutput = null;
		try
		{
			String[] list = tszs.system.config.ConfigManager.getContext().getAssets().list(subpath);
			if(list!= null && list.length>0)
			{
				if(!TFile.Exits(newpath))
					TPath.CreatePath(newpath);
				for(Integer i = 0;i<list.length;i++)
				{
					java.lang.String tempfilepath  = newpath+list[i];
					if(TFile.Exits(tempfilepath))
						TFile.Delete(tempfilepath);
					myOutput = new FileOutputStream(tempfilepath);
					myInput = tszs.system.config.ConfigManager.getContext().getAssets().open(subpath+"/"+list[i]);
					byte[] buffer = new byte[1024];
					int length = myInput.read(buffer);
					while (length > 0)
					{
						myOutput.write(buffer, 0, length);
						length = myInput.read(buffer);
					}
					myOutput.flush();
					myOutput.close();
					myInput.close();
				}
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if (myInput != null)
				{
					myInput.close();
					myInput = null;
				}

				if (myOutput != null)
				{
					myOutput.close();
					myOutput = null;
				}
			}
			catch (IOException ex)
			{
				ex.printStackTrace();
			}
		}
	}

	/**
	 * 判断文件是否存在
	 * 
	 * @param path
	 * @return
	 */
	public static Boolean Exits(String path)
	{
		java.io.File file = new java.io.File(path);
		return file.exists();
	}

	/**
	 * 删除存在文件
	 *
	 * @param path
	 * @return
	 */
	public static void Delete(String path)
	{
		new java.io.File(path).deleteOnExit();
	}

	/**
	 * 下载网络文件
	 * 
	 * @param url
	 *            服务文件地址
	 * @param savepath
	 *            保存地址
	 * @return
	 */
	public static Boolean DownloadFile(java.lang.String url, java.lang.String savepath)
	{
		URL myFileURL;
		HttpURLConnection conn=null;
		InputStream input=null;
		BufferedReader br=null;
		FileOutputStream output=null;
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
			// 这句可有可无，没有影响
			if(conn.getResponseCode() ==200)
			{
				input = conn.getInputStream();
				// 读取大文件
				br = new BufferedReader(new InputStreamReader(input));
				String result = "";
				String line = null;
				while ((line = br.readLine()) != null)
					result += line;

				java.io.File file = new java.io.File(savepath);
				if(file.exists())
					file.delete();

				file.createNewFile();// 新建文件
				output = new FileOutputStream(file);
				output.write(result.getBytes());
				output.flush();
				return true;
			}
			return false;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
		finally
		{
			try
			{
				if(conn != null)
					conn.disconnect();

				if(input != null)
					input.close();

				if(output!=null)
					output.close();

				if(br!=null)
					br.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	/**
	 * 获取文件后缀名
	 * @param path 文件路径
	 *             @param widthpoint 是否返回点号
	 * @return 文件后缀名
     */
	public static java.lang.String GetFileExpresstion(java.lang.String path,Boolean widthpoint)
	{
		java.io.File f =new java.io.File(path);
		java.lang.String fileName=f.getName();
		if(!widthpoint)
			return fileName.substring(fileName.lastIndexOf(".")+1);
		else
			return fileName.substring(fileName.lastIndexOf("."));
	}

	/**
	 * 获取文件后缀名
	 * @param path 文件路径
	 * @return 文件后缀名
	 */
	public static java.lang.String GetFileExpresstion(java.lang.String path)
	{
		return  GetFileExpresstion(path,true);
	}

	/**
	 * 是否文件为图片
	 * @param path 文件路径
	 * @return
     */
	public static boolean IsFileImage(java.lang.String path)
	{
		String express = TFile.GetFileExpresstion(path,false);
		if(express.toUpperCase().equals("JPEG") || express.toUpperCase().equals("PNG") || express.toUpperCase().equals("JPG"))
			return  true;

		return  false;
	}

	/**
	 * 是否文件为视频或者音频
	 * @param path 文件路径
	 * @return
	 */
	public static boolean IsFileVedio(java.lang.String path)
	{
		String express = TFile.GetFileExpresstion(path,false);
		if(express.toUpperCase().equals("MP4") || express.toUpperCase().equals("MPEG") || express.toUpperCase().equals("MP3"))
			return  true;

		return  false;
	}

	/**
	 * 复制单个文件
	 * @param sourcepath String 原文件路径 如：c:/fqf.txt
	 * @param destpath String 复制后路径 如：f:/fqf.txt
	 * @return boolean
	 */
	public static void CopyFile(java.lang.String sourcepath, java.lang.String destpath)
	{
		InputStream inStream=null;
		FileOutputStream fs=null;
		try
		{
			int bytesum = 0;
			int byteread = 0;
			java.io.File oldfile = new java.io.File(sourcepath);
			if (oldfile.exists() && oldfile.isFile() && oldfile.canRead())
			{
				//文件存在时
				inStream = new FileInputStream(sourcepath); //读入原文件
				java.io.File newfile = new java.io.File(destpath);
				if(newfile.exists())
				{
					newfile.delete();
					newfile.createNewFile();
				}
				else
				{
					String path = TPath.GetFilePath(destpath);
					java.io.File folder = new java.io.File(path);

					if(!folder.exists())
						folder.mkdir();

					newfile.createNewFile();
				}
				fs = new FileOutputStream(destpath);
				byte[] buffer = new byte[1024];
				while ( (byteread = inStream.read(buffer)) != -1)
				{
					bytesum += byteread;
					fs.write(buffer, 0, byteread);
					fs.flush();
				}
			}
		}
		catch (Exception e)
		{
			System.out.println("复制单个文件操作出错");
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(inStream!=null)
					inStream.close();

				if(fs!=null)
					fs.close();
			}
			catch (Exception ex)
			{

			}
		}
	}
}