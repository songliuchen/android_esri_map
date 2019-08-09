/**==================================================================
 *
 *        工程名称:  tszs.system
 *        文件名称:  Collection.java
 *        创 建 人  :  宋 刘 陈
 *        联系方式:  756519755
 *        个人网址:  http://www.songliuchen.com
 *        创建时间:  2016年9月8日
 *        修 改 人  : 
 *        修改时间: 
 *
===================================================================*/

package tszs.system;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
public class TCollection
{
	public static List<java.lang.String> GetKeysFromMap(Map map)
	{
		List<java.lang.String> keys = new ArrayList<java.lang.String>();
		Iterator i = map.entrySet().iterator();
		while (i.hasNext())
		{
			Entry entry = (java.util.Map.Entry) i.next();
			if(!TString.IsNullOrEmpty(entry.getKey()))
				keys.add((java.lang.String)entry.getKey());
		}
		return keys;
	}
}
