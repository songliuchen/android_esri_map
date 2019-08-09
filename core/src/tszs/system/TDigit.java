/**==================================================================
 *
 *        工程名称:  tszs.system
 *        文件名称:  Digit.java
 *        创 建 人  :  宋 刘 陈
 *        联系方式:  756519755
 *        个人网址:  http://www.songliuchen.com
 *        创建时间:  2016年9月11日
 *        修 改 人  : 
 *        修改时间: 
 *
===================================================================*/

package tszs.system;

import java.math.BigDecimal;

public class TDigit
{
	public static Double ScaleDouble(Double value,int scale)
	{
		BigDecimal b = new BigDecimal(value);
		double f1 = b.setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue();
		return f1;
	}
}
