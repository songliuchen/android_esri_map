/**==================================================================
 *
 *        工程名称:  tszs.coreui.android.utils
 *        文件名称:  ResourceUtils.java
 *        创 建 人  :  宋 刘 陈
 *        联系方式:  756519755
 *        个人网址:  http://www.songliuchen.com
 *        创建时间:  2016年9月6日
 *        修 改 人  : 
 *        修改时间: 
 *
===================================================================*/

package tszs.esrigis.utils;

import tszs.system.config.ConfigManager;

public class ResourceUtils
{
	public static Object GetResultId(ResourceType type,Integer id)
	{
		switch (type)
		{
    		case Color:
    			return ConfigManager.getContext().getResources().getColor(id);
    		case Image:
    			return ConfigManager.getContext().getResources().getDrawable(id);
    		default:
    			return 0;
		}
	}
}
