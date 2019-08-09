package tszs.esrigis;

import android.view.MotionEvent;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.FeatureTable;
import com.esri.arcgisruntime.data.GeodatabaseFeatureTable;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.layers.Layer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
import com.esri.arcgisruntime.mapping.view.IdentifyLayerResult;
import com.esri.arcgisruntime.mapping.view.MapView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import tszs.esrigis.control.TMessage;
import tszs.esrigis.query.TIdentifyListener;
import tszs.system.TString;
import tszs.system.log.TLog;

/**
 * arcgis 地图操作类
 */
public class TMap
{
    /**
     * 添加FeatureLaayer 到地图中
     * @param mapView 地图对象
     * @param featureLayer 图层对象
     * @param extend 是否缩放到对应的图层中
     * @param errorTips 错误提示
     */
    public static void AddFeatureLayer(MapView mapView, FeatureLayer featureLayer, Boolean extend,String errorTips)
    {
        if(mapView == null || featureLayer ==null)
            return;
        final FeatureLayer tempFeatureLayer = featureLayer;
        final MapView tempMapView = mapView;
        final Boolean tempExtend = extend;
        final String tempErrorTips = errorTips;
        tempFeatureLayer.addDoneLoadingListener(new Runnable()
        {
            @Override
            public void run()
            {
                if (tempFeatureLayer.getLoadStatus() == LoadStatus.LOADED)
                {
                    //缩放到图层范围内
                    if(tempExtend)
                    {
                        tempMapView.setViewpointAsync(new Viewpoint(tempFeatureLayer.getFullExtent()));
                    }
                }
                else
                {
                    //如果日志参数不为空则弹出日志提示
                    if(!TString.IsNullOrEmpty(tempErrorTips))
                        TMessage.Show(tempErrorTips);
                    TLog.Print("Feature Layer failed to load!", TLog.Lever.Info);
                }
            }
        });
        // 添加图层到地图中
        tempMapView.getMap().getOperationalLayers().add(featureLayer);
    }


    /**
     * 添加FeatureLaayer 到地图中
     * @param mapView 地图对象
     * @param geodatabaseFeatureTable 图层对象
     * @param extend 是否缩放到对应的图层中
     * @param errorTips 错误提示
     */
    public static void AddGeodatabaseFeatureTable(MapView mapView, GeodatabaseFeatureTable geodatabaseFeatureTable, Boolean extend, String errorTips)
    {
        if(mapView == null || geodatabaseFeatureTable ==null)
            return;

        FeatureLayer featureLayer = new FeatureLayer(geodatabaseFeatureTable);
        AddFeatureLayer(mapView,featureLayer,extend,errorTips);
    }

    /**
     * 获取地图图层数量
     * @param mapView
     * @return
     */
    public static Integer GetLayerCount(MapView mapView)
    {
        if(mapView == null)
            return  0;

        return mapView.getMap().getOperationalLayers().size();
    }

    /**
     * 获取地图图层名称集合
     * @param mapView
     * @return
     */
    public static List<String> GetLayerNames(MapView mapView)
    {
        if(mapView == null)
            return  null;

        List<String> names = new ArrayList<>();
        for(Integer i=0;i<mapView.getMap().getOperationalLayers().size();i++)
            names.add(mapView.getMap().getOperationalLayers().get(i).getName());

        return names;
    }

    /**
     * 清除地图中的选择元素
     * @param mapView
     */
    public static void ClearSelection(MapView mapView)
    {
        for(Integer i=0;i<mapView.getMap().getOperationalLayers().size();i++)
        {
            Layer layer= mapView.getMap().getOperationalLayers().get(i);
            if(layer instanceof FeatureLayer)
            {
                FeatureLayer featureLayer = (FeatureLayer)layer;
                featureLayer.clearSelection();
            }
        }
    }

    /**
     * 根据图层名称获取图层对象
     * @param name
     * @return
     */
    public static Layer GetLayerByName(MapView mapView,String name)
    {
        if(mapView == null)
            return  null;

        List<String> names = new ArrayList<>();
        for(Integer i=0;i<mapView.getMap().getOperationalLayers().size();i++)
        {
            if(mapView.getMap().getOperationalLayers().get(i).getName().equals(name))
                return  mapView.getMap().getOperationalLayers().get(i);
        }

        return null;
    }

    /**
     * 根据名称获取矢量图层对象
     * @param name
     * @return
     */
    public static FeatureLayer GetFeatureLayerByName(MapView mapView,String name)
    {
        Layer layer = GetLayerByName(mapView,name);
        if(layer instanceof  FeatureLayer)
            return  (FeatureLayer)layer;
        return  null;
    }

    /**
     * 根据图层名称获取图层数据数据
     * @param name
     * @return
     */
    public static FeatureTable GetFeatureClassFromMapByLayerName(MapView mapView, String name)
    {
        FeatureLayer featureLayer = GetFeatureLayerByName(mapView,name);
        if(featureLayer != null)
        {
            return  featureLayer.getFeatureTable();
        }
        return  null;
    }

    /**
     * I键盘查询
     * @param mapView 地图对象
     * @param listener 查询结果回调显示
     */
    public static void Identify(MapView mapView, final TIdentifyListener listener)
    {
        final  MapView tempMapView = mapView;
        mapView.setOnTouchListener(new DefaultMapViewOnTouchListener(mapView.getContext(), mapView)
        {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e)
            {
                android.graphics.Point screenPoint = new android.graphics.Point(Math.round(e.getX()),Math.round(e.getY()));
                final ListenableFuture<List<IdentifyLayerResult>> identifyLayerResultsFuture = tempMapView.identifyLayersAsync(screenPoint, 12, false, 10);
                identifyLayerResultsFuture.addDoneListener(new Runnable()
                {
                    @Override
                    public void run()
                    {
                    try
                    {
                        List<IdentifyLayerResult> identifyLayerResults = identifyLayerResultsFuture.get();
                        Map<String,List<Map<String,Object>>> result = new LinkedHashMap<>();
                        for (IdentifyLayerResult identifyLayerResult : identifyLayerResults)
                        {
                            List<Map<String,Object>> layerData = new ArrayList<>();

                            for(Integer i = 0;i<identifyLayerResult.getElements().size();i++)
                                layerData.add(identifyLayerResult.getElements().get(i).getAttributes());

                            String layerName = identifyLayerResult.getLayerContent().getName();
                            result.put(layerName,layerData);
                        }

                        if(listener!=null)
                            listener.execute(result);
                    }
                    catch (InterruptedException | ExecutionException e)
                    {
                        TLog.Print("Error identifying results: " + e.getMessage(), TLog.Lever.Error);
                    }
                    }
                });
                return true;
            }
        });
    }

    /**
     * 获取I键查询结果
     */
    private static void identifyResult(MapView mapView, android.graphics.Point screenPoint, final TIdentifyListener listener)
    {

    }
}
