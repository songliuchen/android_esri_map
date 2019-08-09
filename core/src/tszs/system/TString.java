/**=======================================================
 *
 *        工程名称:  com.tszs.core
 *        文件名称:  MString.java
 *        创 建 人  :  宋 刘 陈
 *        联系方式:  756519755
 *        个人网址:  http://www.songliuchen.com
 *        创建时间:  2015年4月28日
 *        修 改 人  : 
 *        修改时间: 
 *
 *=======================================================*/

package tszs.system;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TString
{
	/**
	 * 判断对象是否为空
	 * 
	 * @param value
	 * @return
	 */
	public static Boolean IsNullOrEmpty(java.lang.String value)
	{
		return value == null || value.trim().length() <= 0;
	}

	/**
	 * 判断对象是否为空
	 * 
	 * @param value
	 * @return
	 */
	public static Boolean IsNullOrEmpty(Object value)
	{
		return (value == null || value.toString().trim().length() <= 0);
	}

	/**
	 * 字符串替换
	 * 
	 * @param source
	 *            字符串对象
	 * @param replace1
	 *            替换对象
	 * @param replace2
	 *            替换后对象
	 * @return
	 */
	public static java.lang.String Replace(java.lang.String source, java.lang.String replace1, java.lang.String replace2)
	{
		if(IsNullOrEmpty(source))
			return null;

		java.lang.String result = source;

		result = result.replaceAll(replace1, replace2);
		return result;
	}

	/**
	 * 获取字符串对象
	 * 
	 * @param value
	 * @param length
	 * @return
	 */
	public static java.lang.String Remove(java.lang.String value, Integer length)
	{
		return value.substring(0, length);
	}

	/**
	 * 格式化字符串
	 * 
	 * @param value
	 * @return
	 */
	public static java.lang.String ToUpper(java.lang.String value)
	{
		if(IsNullOrEmpty(value))
			return null;

		return value.toUpperCase();
	}

	/**
	 * 将json转化为字符串
	 * 
	 * @param json
	 * @return
	 */
	public static java.lang.String ConvertToString(java.lang.String json)
	{
		if(IsNullOrEmpty(json))
			return null;

		StringBuffer ret = new StringBuffer();
		try
		{
			JSONTokener tokener = new JSONTokener(json);
			JSONObject joResult = new JSONObject(tokener);

			JSONArray words = joResult.getJSONArray("ws");
			for (int i = 0; i < words.length(); i++)
			{
				// 转写结果词，默认使用第一个结果
				JSONArray items = words.getJSONObject(i).getJSONArray("cw");
				JSONObject obj = items.getJSONObject(0);
				ret.append(obj.getString("w"));
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return ret.toString();
	}

	public static java.lang.String GetGUID()
	{
		return GetGUID(false, true);
	}

	public static java.lang.String Format(java.lang.String value, java.lang.String... args)
	{
		if(IsNullOrEmpty(value))
			return "";

		Integer index = 0;
		for (java.lang.String temp : args)
		{
			value = value.replace("{" + index + "}", temp);
			index++;
		}
		return value;
	}

	/**
	 * 获取唯一标识字符串
	 * 
	 * @param upcase
	 *            是否大写
	 * @param showline
	 *            是否显示间隔符-
	 * @return
	 */
	public static java.lang.String GetGUID(Boolean upcase, Boolean showline)
	{
		UUID uuid = UUID.randomUUID();
		java.lang.String result = uuid.toString();
		if(upcase)
			result = result.toUpperCase();

		if(!showline)
			result = result.replace("-", "");

		return result;
	}

	/**
	 * 验证邮箱
	 * 
	 * @param email
	 * @return
	 */
	public static Boolean IsEmail(java.lang.String email)
	{
		boolean flag = false;
		try
		{
			java.lang.String check = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
			Pattern regex = Pattern.compile(check);
			Matcher matcher = regex.matcher(email);
			flag = matcher.matches();
		}
		catch (Exception e)
		{
			flag = false;
		}
		return flag;
	}

	/**
	 * 验证QQ号码
	 * 
	 * @param qq
	 * @return
	 */
	public static Boolean IsQQ(java.lang.String qq)
	{
		java.lang.String regex = "[1-9][0-9]{4,14}";
		return check(qq, regex);
	}

	/**
	 * 判断是否为手机号
	 * @param phone
	 * @return
	 */
	public static Boolean IsPhone(java.lang.String phone)
	{
		java.lang.String regex = "^((13[0-9])|(15[^4])|(18[0,2,3,5-9])|(17[0-8])|(147))\\d{8}$";
		return check(phone, regex);
	}

	/**
	 * 验证是否为数字和字母
	 * 
	 * @param word
	 * @return
	 */
	public static Boolean IsWordAndNumber(java.lang.String word)
	{
		java.lang.String regex = "(?!^[0-9]*$)(?!^[a-zA-Z]*$)^([a-zA-Z0-9]{2,})$";
		return check(word, regex);
	}

	/**
	 * 验证是否为数字或字母
	 * 
	 * @param word
	 * @return
	 */
	public static Boolean IsWordOrNumber(java.lang.String word)
	{
		java.lang.String regex = "^[A-Za-z0-9]+$";
		return check(word, regex);
	}

	/**
	 * 判断是否全为汉字
	 * 
	 * @param word
	 * @return
	 */
	public static Boolean IsChinese(java.lang.String word)
	{
		java.lang.String regex = "^[\u4E00-\u9FA5\uF900-\uFA2D]*$";
		return check(word, regex);
	}

	/**
	 * 判断是否全为数字
	 * 
	 * @param word
	 * @return
	 */
	public static Boolean IsNumber(java.lang.String word)
	{
		java.lang.String regex = "^[0-9]*$";
		return check(word, regex);
	}

	/**
	 * 判断是否全为字母
	 * 
	 * @param word
	 * @return
	 */
	public static Boolean IsWord(java.lang.String word)
	{
		java.lang.String regex = "^[a-zA-z]+$";
		return check(word, regex);
	}

	/**
	 * 获取文件名
	 * 
	 * @param path
	 *            文件全路径
	 * @return
	 */
	public static java.lang.String GetFileName(java.lang.String path)
	{
		if(TString.IsNullOrEmpty(path))
			return "";

		if(path.contains("\\"))
		{
			Integer index = path.lastIndexOf("\\");
			return path.substring(index + 1, path.length());
		}
		else if(path.contains("/"))
		{
			Integer index = path.lastIndexOf("/");
			return path.substring(index + 1, path.length());
		}
		else
		{
			return "";
		}
	}

	/**
	 * 获取文件目录
	 * 
	 * @param path
	 *            文件全路径
	 * @return
	 */
	public static java.lang.String GetFilePath(java.lang.String path)
	{
		if(TString.IsNullOrEmpty(path))
			return "";

		if(path.contains("\\"))
		{
			Integer index = path.lastIndexOf("\\");
			return path.substring(0, index + 1);
		}
		else if(path.contains("/"))
		{
			Integer index = path.lastIndexOf("/");
			return path.substring(0, index + 1);
		}
		else
		{
			return "";
		}
	}
	
	/**
	 * 获取文件目录
	 *   文件全路径
	 * @return
	 */
	public static java.lang.String GetNewFileNameByPath()
	{
		return  java.lang.String.valueOf(System.currentTimeMillis())+".jpeg";
	}

	/**
	 *  去除空白换行符等
	 * @param value
	 * @return
     */
	public static java.lang.String ReplaceBlank(java.lang.String value)
	{
		java.lang.String dest = "";
		if (!TString.IsNullOrEmpty(value))
		{
			Pattern p = Pattern.compile("\\s*|\t|\r|\n");
			Matcher m = p.matcher(value);
			dest = m.replaceAll("");
		}
		return dest;
	}

	/**
	 *  去除空白换行符等
	 * @param value
	 * @return
	 */
	public static Boolean HasBlank(java.lang.String value)
	{
		java.lang.String dest = "";
		if (value != null)
		{
			if(value.contains("\n") || value.contains("\\n") || value.contains("\r\n") || value.contains("\\r\\n"))
				return  true;
			else if(value.equals("\n") || value.equals("\\n") || value.equals("\r\n") || value.equals("\\r\\n"))
				return  true;
		}
		return false;
	}

	/**
	 * 得到一个字符串的长度,显示的长度,一个汉字或日韩文长度为2,英文字符长度为1
	 * 
	 * @param s
	 *            s 需要得到长度的字符串
	 * @return int 得到的字符串长度
	 */
	public static Integer GetStringLength(java.lang.String s)
	{
		if(s == null)
			return 0;
		char[] c = s.toCharArray();
		int len = 0;
		for (int i = 0; i < c.length; i++)
		{
			len++;
			if(!isLetter(c[i]))
			{
				len++;
			}
		}
		return len;
	}

	private static JSONArray data = null;
	private static java.lang.String[] province=null;
	private static Map<java.lang.String, java.lang.String[]> areas = new HashMap<java.lang.String, java.lang.String[]>();//key - 省 value - 市s
	private static Map<java.lang.String, java.lang.String[]> cities = new HashMap<java.lang.String, java.lang.String[]>();//key - 市 values - 区s
	public static java.lang.String[]  GetProvince()
	{
		InputStream myInput = null;
		try
		{
			if (province == null)
			{
				myInput = tszs.system.config.ConfigManager.getContext().getAssets().open("city.json");
				java.lang.String str = "";
				BufferedReader reader = new BufferedReader(new InputStreamReader(myInput, "utf-8"));
				StringBuffer sb = new StringBuffer();
				while ((str = reader.readLine()) != null)
				{
					sb.append(str).append("\n");
				}
				myInput.close();
				JSONObject tempobject = new JSONObject(sb.toString());
				JSONArray data = tempobject.getJSONArray("citylist");
				province=new java.lang.String[data.length()];
				for (int i = 0; i < data.length(); i++)
				{
					JSONObject jsonP = data.getJSONObject(i);// 每个省的json对象
					province[i] = jsonP.getString("p");
					JSONArray jsonCs = null;
					jsonCs = jsonP.getJSONArray("c");
					java.lang.String[] mCitiesDatas = new java.lang.String[jsonCs.length()];
					for (int j = 0; j < jsonCs.length(); j++)
					{
						JSONObject jsonCity = jsonCs.getJSONObject(j);
						java.lang.String city = jsonCity.getString("n");// 市名字
						mCitiesDatas[j] = city;
						if(jsonCity.has("a"))
						{
							JSONArray jsonAreas = jsonCity.getJSONArray("a");
							;
							java.lang.String[] mAreasDatas = new java.lang.String[jsonAreas.length()];// 当前市的所有区
							for (int k = 0; k < jsonAreas.length(); k++)
							{
								java.lang.String area = jsonAreas.getJSONObject(k).getString("s");// 区域的名称
								mAreasDatas[k] = area;
							}
							areas.put(city, mAreasDatas);
						}
						else
						{
							areas.put(city, new java.lang.String[]{"无"});
						}
					}

					cities.put(province[i], mCitiesDatas);
				}
			}
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
		catch (JSONException ex)
		{
			ex.printStackTrace();
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
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			return  province;
		}
	}

	public static java.lang.String[]  GetAreas(java.lang.String city)
	{
		return  areas.get(city);
	}

	public static java.lang.String[]  GetCities(java.lang.String province)
	{
		return  cities.get(province);
	}

	/**
	 * 度转经纬度显示
	 * 
	 * @param d
	 * @return
	 */
	public static java.lang.String ConvertDoubleToDegree(double d)
	{
		if(d <= 0)
			return "00.00";

		java.lang.String str = "" + d;

		int p = str.indexOf(".");
		int dt = Integer.parseInt(str.substring(0, p));
		d = d - dt;
		double M = d * 60;
		int mt = (int) M;
		M = (M - mt) * 60;
		if(Math.abs(M - 60) < 0.001)
		{
			M = 0;
			mt = mt + 1;
		}
		if(mt == 60)
		{
			dt = dt + 1;
			mt = 0;
		}

		M = Double.valueOf(java.lang.String.valueOf(M).substring(0, 7));
		return "" + dt + " °  " + mt + " ′  " + M + " ″";
	}

	private static boolean isLetter(char c)
	{
		int k = 0x80;
		return c / k == 0 ? true : false;
	}

	private static Boolean check(java.lang.String str, java.lang.String regex)
	{
		Boolean flag = false;
		try
		{
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(str);
			flag = matcher.matches();
		}
		catch (Exception e)
		{
			flag = false;
		}
		return flag;
	}
}
