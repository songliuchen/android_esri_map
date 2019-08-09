/**==================================================================
 *
 *        工程名称:  tszs.system
 *        文件名称:  DateTime.java
 *        创 建 人  :  宋 刘 陈
 *        联系方式:  756519755
 *        个人网址:  http://www.songliuchen.com
 *        创建时间:  2015年7月8日
 *        修 改 人  : 
 *        修改时间: 
 *
===================================================================*/

package tszs.system;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@SuppressLint("SimpleDateFormat")
public class TDateTime
{
	public final static java.lang.String YEAR_YUE_RI = "yyyy-MM-dd HH:mm:ss";

	public final static java.lang.String YEAR_N_YUE_Y_RI = "yyyy年MM月dd日 HH:mm:ss";
	
	public final static java.lang.String YEAR_N_YUE_Y_RI_SHI_FEN_MIAO = "yyyy年MM月dd日 HH时mm分ss秒";

	public final static java.lang.String YEAR_N_YUE_Y_RI_SHI_FEN = "yyyy年MM月dd日 HH时mm分";

	public final static java.lang.String YEAR_D_YUE_D_RI = "yyyy.MM.dd HH:mm:ss";
	
	public final static java.lang.String YEAR_D_YUE_D_RI_SHORT = "yyyy.MM.dd";
	
	public final static java.lang.String YEAR_N_YUE_Y_RI_SHORT = "yyyy年MM月dd日";
	
	public final static java.lang.String YEAR_YUE_RI_SHORT = "yyyy-MM-dd";

	/**
	 * 获取当前手机时间的长整形
	 * 
	 * @return
	 */
	public static Long GetNow()
	{
		return GetNow(new Date());
	}
	
	/**
	 * 获取当前手机时间的长整形
	 * 
	 * @return
	 */
	public static Long GetNow(Date date)
	{
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.getTimeInMillis();
	}

	/**
	 * 获取当前手机时间的年份
	 * 
	 * @return
	 */
	public static Integer GetYear()
	{
		return GetYear(new Date());
	}
	
	/**
	 * 获取当前手机时间的年份
	 * 
	 * @return
	 */
	public static Integer GetYear(Date date)
	{
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.YEAR);

	}

	/**
	 * 获取当前手机时间的月份
	 * 
	 * @return
	 */
	public static java.lang.String GetMonth()
	{
		return GetMonth(new Date());
	}
	
	/**
	 * 获取当前手机时间的月份
	 * 
	 * @return
	 */
	public static java.lang.String GetMonth(Date date)
	{
		if(date ==null)
			return "";
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		java.lang.String month =java.lang.String.valueOf(calendar.get(Calendar.MONTH));
		if(month.length()==1)
		{
			month="0"+month;
		}
		return month;
	}

	/**
	 * 获取当前手机时间的日期
	 * 
	 * @return
	 */
	public static java.lang.String GetDayofMonth()
	{
		return GetDayofMonth(new Date());
	}
	
	/**
	 * 获取当前手机时间的日期
	 * 
	 * @return
	 */
	public static java.lang.String GetDayofMonth(Date date)
	{
		if(date ==null)
			return "";
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
        java.lang.String day= java.lang.String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
		if(day.length()==1)
		{
			day="0"+day;
		}
		return day;
	}
	
	/**
	 * 获取当前手机时间的星期
	 * 
	 * @return
	 */
	public static java.lang.String GetWeekday()
	{
		return GetWeekday(new Date());
	}
	
	/**
	 * 获取当前手机时间的星期
	 * 
	 * @return
	 */
	public static java.lang.String GetWeekday(Date date)
	{
		if(date==null)
			return "";
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		
		java.lang.String mWay = java.lang.String.valueOf(calendar.get(Calendar.DAY_OF_WEEK));
		if("1".equals(mWay))
		{
			mWay = "天";
		}
		else if("2".equals(mWay))
		{
			mWay = "一";
		}
		else if("3".equals(mWay))
		{
			mWay = "二";
		}
		else if("4".equals(mWay))
		{
			mWay = "三";
		}
		else if("5".equals(mWay))
		{
			mWay = "四";
		}
		else if("6".equals(mWay))
		{
			mWay = "五";
		}
		else if("7".equals(mWay))
		{
			mWay = "六";
		}
		
		return "星期"+mWay;
	}

	/**
	 * 将时间日期字符串转成秒数值
	 * 
	 * @param time
	 * @return
	 * @throws ParseException
	 */
	@SuppressLint("SimpleDateFormat")
	public static Date GetDate(Long time)
	{
		return new Date(time);
	}

	/**
	 * 将时间日期字符串转成秒数值
	 * 
	 * @param datetime
	 * @return
	 * @throws ParseException
	 */
	@SuppressLint("SimpleDateFormat")
	public static Long GetTime(java.lang.String datetime)
	{
		if(TString.IsNullOrEmpty(datetime))
			return Long.valueOf(0);

		try
		{
			SimpleDateFormat formatter = new SimpleDateFormat(YEAR_N_YUE_Y_RI);
			Date curDate = formatter.parse(datetime);

			return curDate.getTime();

		}
		catch (Exception e)
		{
			e.printStackTrace();
			return (long) -1;
		}
	}

	/**
	 * 将时间日期字符串转成秒数值
	 * 
	 * @param datetime
	 * @return
	 * @throws ParseException
	 */
	@SuppressLint("SimpleDateFormat")
	public static Date GetDate(java.lang.String datetime, java.lang.String format)
	{
		if(TString.IsNullOrEmpty(datetime))
			return null;
		try
		{
			SimpleDateFormat formatter = new SimpleDateFormat(format);
			Date resultdate = formatter.parse(datetime);
			return resultdate;
		}
		catch (ParseException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 将时间日期字符串转成秒数值
	 * 
	 * @param datetime
	 * @return
	 * @throws ParseException
	 */
	@SuppressLint("SimpleDateFormat")
	public static Date GetDate(java.lang.String datetime)
	{
		if(TString.IsNullOrEmpty(datetime))
			return null;
		try
		{
			SimpleDateFormat formatter = new SimpleDateFormat(YEAR_YUE_RI);
			Date resultdate = formatter.parse(datetime);
			return resultdate;
		}
		catch (ParseException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 将时间日期转成字符串
	 * 
	 * @param date 日期
	 * @param  format 格式
	 * @return
	 * @throws ParseException
	 */
	@SuppressLint("SimpleDateFormat")
	public static java.lang.String GetDateString(Date date, java.lang.String format)
	{
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		java.lang.String datestring = formatter.format(date);
		return datestring;
	}

	/**
	 * 将时间日期转成字符串
	 * 
	 * @param date
	 * @return
	 * @throws ParseException
	 */
	@SuppressLint("SimpleDateFormat")
	public static java.lang.String GetDateString(Date date)
	{
		return GetDateString(date, YEAR_YUE_RI);
	}
	
	/**
	 * 将时间日期转成字符串
	 *
	 * @return
	 * @throws ParseException
	 */
	@SuppressLint("SimpleDateFormat")
	public static java.lang.String GetDateString()
	{
		return GetDateString(new Date(), YEAR_YUE_RI);
	}

	/**
	 * 判断时期是否是今天
	 * @param date 日期对象
	 * @return
	 */
	public static Boolean IsToday(Date date)
	{
		Date day = new Date();
		return date.getTime() >= GetDayBegin(day).getTime() && date.getTime() <= GetDayEnd(day).getTime();
	}
	
	/**
	 * 判断时期是否相同一天
	 * @param date1 日期1
	 * @param date2 日期2
	 * @return
	 */
	public static Boolean IsSameDay(Date date1,Date date2)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	    java.lang.String d1 = sdf.format(date1);//第一个时间
	    java.lang.String d2 = sdf.format(date2);//第二个时间
	    return d1.equals(d2);
	}

	/**
	 * 是否是指定日期
	 * 
	 * @param date
	 * @param day
	 * @return
	 */
	public static boolean IsTheDay(Date date,Date day)
	{
		return date.getTime() >= GetDayBegin(day).getTime() && date.getTime() <= GetDayEnd(day).getTime();
	}
	
	/**
	 * 判断是否为今天之后日期
	 * @param date
	 * @return
	 */
	public static Boolean IsAfterToday(Date date)
	{
		Date tempdate = GetDayEnd(new Date());
		return tempdate.getTime()<date.getTime();
	}
	
	/**
	 * 判断是否为今天之前日期
	 * @param date
	 * @return
	 */
	public static Boolean IsBeforeToday(Date date)
	{
		Date tempdate = GetDayBegin(new Date());
		return tempdate.getTime()>date.getTime();
	}

	/**
	 * 获取指定时间的那天 00:00:00.000 的时间
	 * 
	 * @param date
	 * @return
	 */
	private static Date GetDayBegin(Date date)
	{
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return c.getTime();
	}

	/**
	 * 获取指定时间的那天 23:59:59.999 的时间
	 * 
	 * @param date
	 * @return
	 */
	private static Date GetDayEnd(Date date)
	{
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.HOUR_OF_DAY, 23);
		c.set(Calendar.MINUTE, 59);
		c.set(Calendar.SECOND, 59);
		c.set(Calendar.MILLISECOND, 999);
		return c.getTime();
	}
}
