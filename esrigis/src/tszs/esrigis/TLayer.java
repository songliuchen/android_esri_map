package tszs.esrigis;

import com.esri.arcgisruntime.data.FeatureTable;
import com.esri.arcgisruntime.layers.FeatureLayer;

/**
 * 图层对象
 */
public class TLayer
{
    /**
     * 根据矢量数据要素对象创建图层对象
     * @param featureTable 数据对象
     * @return
     */
    public static FeatureLayer CreateFeatureLayerFormTable(FeatureTable featureTable)
    {
        // 构建FeatureLayer
        FeatureLayer featureLayer = new FeatureLayer(featureTable);
        return  featureLayer;
    }
}
