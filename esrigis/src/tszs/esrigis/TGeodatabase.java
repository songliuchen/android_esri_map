package tszs.esrigis;

import com.esri.arcgisruntime.data.Geodatabase;
import com.esri.arcgisruntime.data.GeodatabaseFeatureTable;

import java.util.List;

/**
 * 空间数据库操作辅助类
 */
public class TGeodatabase
{
    /**
     * 根据名称获取空间表对象
     * @param geodatabase 数据库对象
     * @param name 空间表名称
     * @return 空间表对象
     */
    public static GeodatabaseFeatureTable GetGeodatabaseFeatureTableByName(Geodatabase geodatabase,String name)
    {
        GeodatabaseFeatureTable geodatabaseFeatureTable  = geodatabase.getGeodatabaseFeatureTable(name);
        return geodatabaseFeatureTable;
    }

    /**
     * 根据路径获取数据库对象
     * @param path 路径
     * @return
     */
    public static Geodatabase GetGeodatabaseByPath(String path)
    {
        Geodatabase geodatabase = new Geodatabase(path);
        geodatabase.loadAsync();
        return  geodatabase;
    }

    /**
     * 获取数据库中所有的矢量图层
     * @param geodatabase
     * @return
     */
    public static List<GeodatabaseFeatureTable> GetAllFeatureTables(Geodatabase geodatabase)
    {
        return geodatabase.getGeodatabaseFeatureTables();
    }
}
