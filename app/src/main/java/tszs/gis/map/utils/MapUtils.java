package tszs.gis.map.utils;

import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.view.MapView;

import java.util.ArrayList;
import java.util.List;

public class MapUtils
{
    /**
     * 获取所有矢量图层集合
     *
     * @param mapView
     * @return
     */
    public static List<FeatureLayer> GetFeatureLayers(MapView mapView) {
        List<FeatureLayer> featureLayers = new ArrayList<>();
        for (Integer i = 0; i < mapView.getMap().getOperationalLayers().size(); i++) {
            if (mapView.getMap().getOperationalLayers().get(i) instanceof FeatureLayer){
                featureLayers.add((FeatureLayer) mapView.getMap().getOperationalLayers().get(i));
            }
        }
        return featureLayers;
    }
}
