package tszs.gis.map.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.FeatureTable;
import com.esri.arcgisruntime.data.Field;
import com.esri.arcgisruntime.data.Geodatabase;
import com.esri.arcgisruntime.data.GeodatabaseFeatureTable;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.ImmutablePart;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.PointCollection;
import com.esri.arcgisruntime.geometry.Polygon;
import com.esri.arcgisruntime.geometry.Polyline;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
import com.esri.arcgisruntime.mapping.view.IdentifyLayerResult;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.mapping.view.SelectedVertexChangedEvent;
import com.esri.arcgisruntime.mapping.view.SelectedVertexChangedListener;
import com.esri.arcgisruntime.mapping.view.SketchCreationMode;
import com.esri.arcgisruntime.mapping.view.SketchEditor;
import com.esri.arcgisruntime.mapping.view.SketchGeometryChangedEvent;
import com.esri.arcgisruntime.mapping.view.SketchGeometryChangedListener;
import com.esri.arcgisruntime.mapping.view.SketchStyle;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import tszs.esrigis.TFeatureTable;
import tszs.esrigis.TGeodatabase;
import tszs.esrigis.TLayer;
import tszs.esrigis.TMap;
import tszs.esrigis.control.TMessage;
import tszs.esrigis.query.TAttributeListener;
import tszs.esrigis.query.TIdentifyListener;
import tszs.esrigis.utils.IntentUtil;
import tszs.gis.map.R;
import tszs.gis.map.utils.MapUtils;
import tszs.system.TPath;
import tszs.system.TString;
import tszs.system.log.TLog;

public class MainActivity extends Activity
{
    // permission to read external storage
    private final String[] reqPermission = new String[] { Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE };

    private MapView mMapView;

    //选择数据库文件路径
    String path ="";

    //是否开启I键查
     Boolean hasOpenIdentify=false;

    SketchEditor mSketchEditor = null;
    List<Feature> mSelectFeatures = new ArrayList<>();

    //0 创建 1、扣岛 2、花边 3、修边 4、节点编辑 5扩边
    Integer mEditType = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        //加载arcgis底图
        ArcGISMap map = new ArcGISMap(Basemap.createStreets());
        mMapView = (com.esri.arcgisruntime.mapping.view.MapView)findViewById(R.id.mapView);
        mMapView.setMap(map);


        loadShapeFile("/storage/emulated/0/ArcGIS/samples/省界/sheng.shp");
    }

    /**
     * 打开数据库功能按钮
     * @param v
     */
    public void doClick(View v)
    {
        IntentUtil.openSelectFileIntent(this,"geodatabase","选择本地数据库");
    }

    /**
     * I键查询功能按钮
     * @param v
     */
    public void doClickIdentify(View v)
    {
        if(!hasOpenIdentify)
        {
            if(TMap.GetLayerCount(mMapView)>0)
            {
                hasOpenIdentify = true;
                TMap.Identify(mMapView, new TIdentifyListener()
                {
                    @Override
                    public void execute(Map<String, List<Map<String, Object>>> data)
                    {
                        if(data == null || data.size() == 0)
                            return;

                        String message = "";
                        for (String key : data.keySet())
                        {
                            message+="图层："+key+"\n";
                            for(Integer i = 0;i<data.get(key).size();i++)
                            {
                                for (String key2 : data.get(key).get(i).keySet())
                                {
                                    message+=key2+"："+data.get(key).get(i).get(key2).toString()+"\n";
                                }
                                message+="\n";
                            }
                            message+="============================\n";
                        }
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                        // set title
                        alertDialogBuilder.setTitle("I键查询结果");

                        // set dialog message
                        alertDialogBuilder
                                .setMessage(message)
                                .setCancelable(false)
                                .setPositiveButton("确定", new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id)
                                    {

                                    }
                                });
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }
                });
            }
            else
            {
                TMessage.Show("没有可查询的图层对象！");
            }
        }
    }

    /**
     * 属性查询
     * @param v
     */
    public void doClickAttribute(View v)
    {
        if(TMap.GetLayerCount(mMapView)<=0)
        {
            TMessage.Show("没有可查询的图层，请先打开数据库！");
            return;
        }
        LinearLayout linearLayout = new LinearLayout(MainActivity.this);
        LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        linearLayout.setLayoutParams(params);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        final Spinner spinner = new Spinner(MainActivity.this);
        spinner.setLayoutParams(params);
        List<String> layerNames = TMap.GetLayerNames(mMapView);
        ArrayAdapter<String> adapter=new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, layerNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        linearLayout.addView(spinner);

        final EditText editText = new EditText(MainActivity.this);
        editText.setLayoutParams(params);
        linearLayout.addView(editText);
        new AlertDialog.Builder(MainActivity.this)
        /* 弹出窗口的最上头文字 */
        .setTitle("设置查询条件").setView(linearLayout)
        .setPositiveButton("确定",
        new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialoginterface, int i)
            {
                dialoginterface.dismiss();
                String condition = editText.getText().toString();
                final String select = (String) spinner.getSelectedItem();
                if(TString.IsNullOrEmpty(select))
                {
                    TMessage.Show("请选择查询图层");
                    return;
                }
                FeatureLayer featureLayer = TMap.GetFeatureLayerByName(mMapView, select);
                if(featureLayer == null)
                    return;

                //清除地图中所有的选择元素
                TMap.ClearSelection(mMapView);

                final FeatureTable featureTable =featureLayer.getFeatureTable();
                TFeatureTable.attributeQuery(featureTable, condition, new TAttributeListener()
                {
                    @Override
                    public void execute(String tableName, List<Feature> data)
                    {
                        if(data ==null || data.size() == 0)
                            return;
                        FeatureLayer featureLayer = TMap.GetFeatureLayerByName(mMapView, select);
                        for(Integer i = 0;i<data.size();i++)
                        {
                            featureLayer.selectFeature(data.get(i));
                        }

                        //设置当前地图范围
                        Geometry temp = data.get(0).getGeometry();
                        for(Integer i = 1;i<data.size();i++)
                        {
                            temp = GeometryEngine.union(temp,data.get(i).getGeometry());
                        }
                        mMapView.setViewpointGeometryAsync(temp.getExtent(), 10);
                    }
                });
            }
        })
        /* 设置弹出窗口的返回事件 */
        .setNegativeButton("取消",
        new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialoginterface, int i)
            {
                dialoginterface.dismiss();
            }
        }).show();
    }

    /**
     * 添加shape数据到地图中
     * @param v
     */
    public void doClickAddShape(View v)
    {
        IntentUtil.openSelectFileIntent(this,"shp","打开本地shape数据");
    }

    /**
     * 属性更新
     * @param v
     */
    public void doClickUpdateAttribute(View v)
    {
        if(TMap.GetLayerCount(mMapView) == 0)
        {
            TMessage.Show("没有可查询的图层！");
            return;
        }
        TMap.Identify(mMapView, new TIdentifyListener()
        {
            @Override
            public void execute(Map<String, List<Map<String, Object>>> data)
            {
                if(data == null || data.size() == 0)
                {
                    TMessage.Show("没有可更新的要素！");
                    return;
                }
                else if(data.size()>1)
                {
                    TMessage.Show("请选择单个图层的单个要素进行编辑！");
                    return;
                }
                else
                {
                    String layerName = "";
                    Map<String,Object> featureData = null;
                    for (String key : data.keySet())
                    {
                        List<Map<String,Object>> singleData = data.get(key);
                        if(singleData.size()>1)
                        {
                            TMessage.Show("请选择单个要素进行编辑！");
                            return;
                        }

                        layerName= key;
                        featureData = singleData.get(0);
                    }

                    final FeatureLayer featureLayer = TMap.GetFeatureLayerByName(mMapView,layerName);
                    if(featureLayer == null)
                        return;

                    String OIDFieldName = TFeatureTable.GetOIDFieldName(featureLayer.getFeatureTable());
                    Object objectId = featureData.get(OIDFieldName);
                    TFeatureTable.attributeQuery(featureLayer.getFeatureTable(), OIDFieldName+" = " + objectId, new TAttributeListener()
                    {
                        @Override
                        public void execute(String tableName, List<Feature> data)
                        {
                            Feature feature = data.get(0);
                            featureLayer.selectFeature(feature);
                            showCallout(feature,featureLayer.getFeatureTable());
                        }
                    });
                }
            }
        });
    }

    /**
     * 显示编辑对话框
     * @param feature 要素对象
     *                @param featureTable  图层对象
     */
    private void showCallout(final Feature feature,final FeatureTable featureTable)
    {
        Map<String,Object> attributes = feature.getAttributes();
        final LinearLayout linearLayout = new LinearLayout(MainActivity.this);
        LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        linearLayout.setLayoutParams(params);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        for (String key : attributes.keySet())
        {
            if(TFeatureTable.IsSystemField(featureTable,key))
                continue;

            LinearLayout linearLayout1 = new LinearLayout(MainActivity.this);
            linearLayout1.setLayoutParams(params);

            TextView textView = new TextView(MainActivity.this);
            textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            textView.setText(key+"：");
            linearLayout1.addView(textView);

            if(featureTable.getField(key).getFieldType() == Field.Type.TEXT)
            {
                EditText editText = new EditText(MainActivity.this);
                editText.setText(attributes.get(key).toString());
                editText.setTag(key);
                editText.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                linearLayout1.addView(editText);
                linearLayout.addView(linearLayout1);
            }
            else if(featureTable.getField(key).getFieldType() == Field.Type.INTEGER)
            {
                EditText editText = new EditText(MainActivity.this);
                editText.setText(attributes.get(key).toString());
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText.setTag(key);
                editText.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                linearLayout1.addView(editText);
                linearLayout.addView(linearLayout1);
            }
            else if(featureTable.getField(key).getFieldType() == Field.Type.DOUBLE || featureTable.getField(key).getFieldType() == Field.Type.FLOAT )
            {
                EditText editText = new EditText(MainActivity.this);
                editText.setTag(key);
                editText.setText(attributes.get(key).toString());
                editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                editText.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                linearLayout1.addView(editText);
                linearLayout.addView(linearLayout1);
            }
        }
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setTitle("属性编辑");
        alertDialogBuilder.setCancelable(true).
        setView(linearLayout)
        .setPositiveButton("确定", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int id)
            {
                for(Integer i = 0;i<linearLayout.getChildCount();i++)
                {
                    LinearLayout tempLinearLayout = (LinearLayout) linearLayout.getChildAt(i);
                    EditText editText = (EditText) tempLinearLayout.getChildAt(1);
                    String fieldName = (String) editText.getTag();
                    if(feature.getFeatureTable().getField(fieldName).getFieldType().equals(Field.Type.TEXT))
                        feature.getAttributes().put(fieldName,editText.getText().toString());

//                    else if(feature.getFeatureTable().getField(fieldName).getFieldType().equals(Field.Type.FLOAT))
//                        feature.getAttributes().put(fieldName,Float.parseFloat(editText.getText().toString()));
//
//                    else if(feature.getFeatureTable().getField(fieldName).getFieldType().equals(Field.Type.DOUBLE))
//                        feature.getAttributes().put(fieldName,Double.parseDouble(editText.getText().toString()));

                    else if(feature.getFeatureTable().getField(fieldName).getFieldType().equals(Field.Type.INTEGER))
                        feature.getAttributes().put(fieldName,Integer.parseInt(editText.getText().toString()));
                }

                if(!featureTable.canUpdate(feature))
                {
                    TMessage.Show("当前数据类型无法进行编辑，目前arcgis for android 只能针对shape数据进行编辑！");
                    return;
                }

                final ListenableFuture<Void> updateFeatures= featureTable.updateFeatureAsync(feature);
                // 在操作完成的监听事件中判断操作是否成功
                updateFeatures.addDoneListener(new Runnable() {
                    @Override
                    public void run()
                    {
                        try
                        {
                            updateFeatures.get();
                            if (updateFeatures.isDone())
                            {
                                TMessage.Show("要素更新成功！");
                            }
                        }
                        catch(Exception ex)
                        {
                            ex.printStackTrace();
                        }
                    }
                });
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    /**
     * 接收调用外部视图回调结果回显
     * @param requestCode
     * @param resultCode
     * @param data
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch (requestCode)
        {
            case IntentUtil.FILE:
                if(resultCode ==0)
                    return;

                //获取选择文件路径
                Uri uri = data.getData();
                path = TPath.GetPathFromUri(uri);
                // android 23 需要实时校验权限
                if (ContextCompat.checkSelfPermission(MainActivity.this, reqPermission[0]) == PackageManager.PERMISSION_GRANTED)
                {
                    if(path.endsWith(".shp"))
                    {
                        loadShapeFile(path);
                    }
                    else
                    {
                        //加载数据库
                        loadGeodatabase(path);
                    }
                }
                else
                {
                    ActivityCompat.requestPermissions(MainActivity.this, reqPermission, 2);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 加载数据库
     */
    private void loadGeodatabase(String path)
    {
        final Geodatabase geodatabase = TGeodatabase.GetGeodatabaseByPath(path);
        geodatabase.loadAsync();
        geodatabase.addDoneLoadingListener(new Runnable()
        {
            @Override
            public void run()
            {
                if (geodatabase.getLoadStatus() == LoadStatus.LOADED)
                {
                    List<GeodatabaseFeatureTable> geodatabaseFeatureTables = TGeodatabase.GetAllFeatureTables(geodatabase);
                    if(geodatabaseFeatureTables==null || geodatabaseFeatureTables.size() == 0)
                        return;

                    for(int i = 0;i<geodatabaseFeatureTables.size();i++)
                    {
                        GeodatabaseFeatureTable geodatabaseFeatureTable = geodatabaseFeatureTables.get(i);
                        geodatabaseFeatureTable.loadAsync();
                        //添加图层到地图中
                        TMap.AddGeodatabaseFeatureTable(mMapView,geodatabaseFeatureTable,true,"");
                    }
                }
                else
                {
                    TMessage.Show("数据加载失败！");
                    TLog.Print("数据库加载失败！", TLog.Lever.Info);
                }
            }
        });
    }

    /**
     * 加载本地shape数据
     * @param path
     */
    private void loadShapeFile(String path)
    {
        FeatureLayer featureLayer = TLayer.CreateFeatureLayerFormTable(TFeatureTable.GetFeatureTableFormShape(path));
        mMapView.getMap().getOperationalLayers().add(featureLayer);
    }

    /**
     * 创建要素
     * @param v
     */
    protected void doCreateFeature(View v)
    {
        mSketchEditor = new SketchEditor();
        mEditType = 0;
        SketchStyle mainSketchStyle = new SketchStyle();
        SimpleMarkerSymbol mVertexSymbol = new SimpleMarkerSymbol(com.esri.arcgisruntime.symbology.SimpleMarkerSymbol.Style.SQUARE, -65536, 7.0F);
        SimpleMarkerSymbol mSelectedVertexSymbol = new SimpleMarkerSymbol(com.esri.arcgisruntime.symbology.SimpleMarkerSymbol.Style.SQUARE, -65536, 7.0F);

        SimpleMarkerSymbol mMidVertexSymbol = new SimpleMarkerSymbol(com.esri.arcgisruntime.symbology.SimpleMarkerSymbol.Style.CIRCLE, -1, 5.0F);
        SimpleMarkerSymbol mSelectedMidVertexSymbol = new SimpleMarkerSymbol(com.esri.arcgisruntime.symbology.SimpleMarkerSymbol.Style.CIRCLE, -1, 5.0F);

        mainSketchStyle.setVertexSymbol(mVertexSymbol);
        mainSketchStyle.setSelectedVertexSymbol(mSelectedVertexSymbol);

        mainSketchStyle.setMidVertexSymbol(mMidVertexSymbol);
        mainSketchStyle.setSelectedMidVertexSymbol(mSelectedMidVertexSymbol);

        mSketchEditor.setSketchStyle(mainSketchStyle);
        mMapView.setSketchEditor(mSketchEditor);
        SketchCreationMode mode = SketchCreationMode.POLYGON;
        mSketchEditor.start(mode);
    }

    /**
     * 删除要素
     * @param v
     */
    protected void doClickDeleteFeature(View v)
    {
        final FeatureLayer featureLayer = (FeatureLayer) mMapView.getMap().getOperationalLayers().get(0);
        final ListenableFuture<FeatureQueryResult>  resultListenableFuture= featureLayer.getSelectedFeaturesAsync();
        resultListenableFuture.addDoneListener(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    Feature feature = resultListenableFuture.get().iterator().next();
                    featureLayer.getFeatureTable().deleteFeatureAsync(feature).get();
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                } catch (ExecutionException e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 选择要素
     * @param v
     */
    protected void doSelectFeature(View v)
    {
        //清除之前选择的要素
        doClearSelect(null);

        // 图层配置
        final List<FeatureLayer> featureLayers = MapUtils.GetFeatureLayers(mMapView);
        if (featureLayers == null || featureLayers.size() == 0)
        {
            return;
        }

        mMapView.setOnTouchListener(new DefaultMapViewOnTouchListener(this, mMapView)
        {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e)
            {
                if (mSketchEditor == null || mSketchEditor.getGeometry() == null)
                {
                    final android.graphics.Point screenPoint = new android.graphics.Point(Math.round(e.getX()), Math.round(e.getY()));
                    final ListenableFuture<List<IdentifyLayerResult>> identifyLayerResultsFuture = mMapView.identifyLayersAsync(screenPoint,12,false,1);
                    identifyLayerResultsFuture.addDoneListener(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            try
                            {
                                List<IdentifyLayerResult> identifyLayerResults = identifyLayerResultsFuture.get();
                                for (IdentifyLayerResult identifyLayerResult : identifyLayerResults)
                                {
                                    FeatureLayer sLayer = (FeatureLayer) identifyLayerResult.getLayerContent();
                                    for(Integer i = 0;i<identifyLayerResult.getElements().size();i++)
                                    {
                                        Feature sFeature = (Feature) identifyLayerResult.getElements().get(i);
                                        sLayer.selectFeature(sFeature);
                                        mSelectFeatures.add(sFeature);
                                    }
                                }
                            } catch (InterruptedException | ExecutionException e)
                            {
                            }
                        }
                    });
                }
                return true;
            }
        });
    }

    /**
     * 扣岛
     * @param v
     */
    protected void doClickKoudao(View v)
    {
        if(mSelectFeatures == null || mSelectFeatures.size()!=1)
        {
            Toast.makeText(this,"请选择一个要素",(int)300).show();
            return;
        }
        mSketchEditor = new SketchEditor();
        mEditType = 1;
        SketchStyle mainSketchStyle = new SketchStyle();
        SimpleMarkerSymbol mVertexSymbol = new SimpleMarkerSymbol(com.esri.arcgisruntime.symbology.SimpleMarkerSymbol.Style.SQUARE, -65536, 7.0F);
        SimpleMarkerSymbol mSelectedVertexSymbol = new SimpleMarkerSymbol(com.esri.arcgisruntime.symbology.SimpleMarkerSymbol.Style.SQUARE, -65536, 7.0F);

        SimpleMarkerSymbol mMidVertexSymbol = new SimpleMarkerSymbol(com.esri.arcgisruntime.symbology.SimpleMarkerSymbol.Style.CIRCLE, -1, 5.0F);
        SimpleMarkerSymbol mSelectedMidVertexSymbol = new SimpleMarkerSymbol(com.esri.arcgisruntime.symbology.SimpleMarkerSymbol.Style.CIRCLE, -1, 5.0F);

        mainSketchStyle.setVertexSymbol(mVertexSymbol);
        mainSketchStyle.setSelectedVertexSymbol(mSelectedVertexSymbol);

        mainSketchStyle.setMidVertexSymbol(mMidVertexSymbol);
        mainSketchStyle.setSelectedMidVertexSymbol(mSelectedMidVertexSymbol);

        mSketchEditor.setSketchStyle(mainSketchStyle);
        mMapView.setSketchEditor(mSketchEditor);
        SketchCreationMode mode = SketchCreationMode.POLYGON;
        mSketchEditor.start(mode);

    }

    /**
     * 节点编辑
     * @param v
     */
    protected  void doVertEdit(View v)
    {
        if(mSelectFeatures == null || mSelectFeatures.size()!=1)
        {
            Toast.makeText(this,"请选择一个要素",(int)300).show();
            return;
        }
        mSketchEditor = new SketchEditor();
        mEditType = 4;
        SketchStyle mainSketchStyle = new SketchStyle();
        SimpleMarkerSymbol mVertexSymbol = new SimpleMarkerSymbol(com.esri.arcgisruntime.symbology.SimpleMarkerSymbol.Style.SQUARE, -65536, 7.0F);
        SimpleMarkerSymbol mSelectedVertexSymbol = new SimpleMarkerSymbol(com.esri.arcgisruntime.symbology.SimpleMarkerSymbol.Style.SQUARE, -65536, 7.0F);

        SimpleMarkerSymbol mMidVertexSymbol = new SimpleMarkerSymbol(com.esri.arcgisruntime.symbology.SimpleMarkerSymbol.Style.CIRCLE, -1, 5.0F);
        SimpleMarkerSymbol mSelectedMidVertexSymbol = new SimpleMarkerSymbol(com.esri.arcgisruntime.symbology.SimpleMarkerSymbol.Style.CIRCLE, -1, 5.0F);

        mainSketchStyle.setVertexSymbol(mVertexSymbol);
        mainSketchStyle.setSelectedVertexSymbol(mSelectedVertexSymbol);

        mainSketchStyle.setMidVertexSymbol(mMidVertexSymbol);
        mainSketchStyle.setSelectedMidVertexSymbol(mSelectedMidVertexSymbol);

        mSketchEditor.setSketchStyle(mainSketchStyle);
        mSketchEditor.addGeometryChangedListener(new SketchGeometryChangedListener()
        {
            @Override
            public void geometryChanged(SketchGeometryChangedEvent sketchGeometryChangedEvent)
            {
                sketchGeometryChangedEvent.getGeometry();
            }
        });

        mSketchEditor.addSelectedVertexChangedListener(new SelectedVertexChangedListener()
        {
            @Override
            public void selectedVertexChanged(SelectedVertexChangedEvent selectedVertexChangedEvent)
            {
//                selectedVertexChangedEvent.getSketchVertex().
            }
        });
        mMapView.setSketchEditor(mSketchEditor);
        SketchCreationMode mode = SketchCreationMode.POLYGON;
        mSketchEditor.start(mSelectFeatures.get(0).getGeometry(),mode);
    }

    /**
     * 分割
     * @param v
     */
    protected  void doCutSub(View v)
    {
        if(mSelectFeatures == null || mSelectFeatures.size()!=1)
        {
            Toast.makeText(this,"请选择一个要素",(int)300).show();
            return;
        }
        mSketchEditor = new SketchEditor();
        mEditType = 3;
        SketchStyle mainSketchStyle = new SketchStyle();
        SimpleMarkerSymbol mVertexSymbol = new SimpleMarkerSymbol(com.esri.arcgisruntime.symbology.SimpleMarkerSymbol.Style.SQUARE, -65536, 7.0F);
        SimpleMarkerSymbol mSelectedVertexSymbol = new SimpleMarkerSymbol(com.esri.arcgisruntime.symbology.SimpleMarkerSymbol.Style.SQUARE, -65536, 7.0F);

        SimpleMarkerSymbol mMidVertexSymbol = new SimpleMarkerSymbol(com.esri.arcgisruntime.symbology.SimpleMarkerSymbol.Style.CIRCLE, -1, 5.0F);
        SimpleMarkerSymbol mSelectedMidVertexSymbol = new SimpleMarkerSymbol(com.esri.arcgisruntime.symbology.SimpleMarkerSymbol.Style.CIRCLE, -1, 5.0F);

        mainSketchStyle.setVertexSymbol(mVertexSymbol);
        mainSketchStyle.setSelectedVertexSymbol(mSelectedVertexSymbol);

        mainSketchStyle.setMidVertexSymbol(mMidVertexSymbol);
        mainSketchStyle.setSelectedMidVertexSymbol(mSelectedMidVertexSymbol);

        mSketchEditor.setSketchStyle(mainSketchStyle);
        mMapView.setSketchEditor(mSketchEditor);
        SketchCreationMode mode = SketchCreationMode.POLYLINE;
        mSketchEditor.start(mode);
    }

    /**
     * 拓扑保存
     * @param v
     */
    protected  void doTopoSave(View v)
    {
        if(mSelectFeatures == null || mSelectFeatures.size()==0)
        {
            Toast.makeText(this,"请选择一个要素",(int)300).show();
            return;
        }
        mSketchEditor = new SketchEditor();
        mEditType = 2;
        SketchStyle mainSketchStyle = new SketchStyle();
        SimpleMarkerSymbol mVertexSymbol = new SimpleMarkerSymbol(com.esri.arcgisruntime.symbology.SimpleMarkerSymbol.Style.SQUARE, -65536, 7.0F);
        SimpleMarkerSymbol mSelectedVertexSymbol = new SimpleMarkerSymbol(com.esri.arcgisruntime.symbology.SimpleMarkerSymbol.Style.SQUARE, -65536, 7.0F);

        SimpleMarkerSymbol mMidVertexSymbol = new SimpleMarkerSymbol(com.esri.arcgisruntime.symbology.SimpleMarkerSymbol.Style.CIRCLE, -1, 5.0F);
        SimpleMarkerSymbol mSelectedMidVertexSymbol = new SimpleMarkerSymbol(com.esri.arcgisruntime.symbology.SimpleMarkerSymbol.Style.CIRCLE, -1, 5.0F);

        mainSketchStyle.setVertexSymbol(mVertexSymbol);
        mainSketchStyle.setSelectedVertexSymbol(mSelectedVertexSymbol);

        mainSketchStyle.setMidVertexSymbol(mMidVertexSymbol);
        mainSketchStyle.setSelectedMidVertexSymbol(mSelectedMidVertexSymbol);

        mSketchEditor.setSketchStyle(mainSketchStyle);
        mMapView.setSketchEditor(mSketchEditor);
        SketchCreationMode mode = SketchCreationMode.POLYGON;
        mSketchEditor.start(mode);
    }

    /**
     * 清除选择
     * @param v
     */
    protected void doClearSelect(View v)
    {
        FeatureLayer featureLayer = (FeatureLayer) mMapView.getMap().getOperationalLayers().get(0);
        featureLayer.clearSelection();
        mSelectFeatures.clear();
    }

    /**
     * 扩边功能
     * @param v
     */
    protected  void doExpendBoundry(View v)
    {
        if(mSelectFeatures == null || mSelectFeatures.size()!=1)
        {
            Toast.makeText(this,"请选择一个要素",(int)300).show();
            return;
        }
        mSketchEditor = new SketchEditor();
        mEditType = 5;
        SketchStyle mainSketchStyle = new SketchStyle();
        SimpleMarkerSymbol mVertexSymbol = new SimpleMarkerSymbol(com.esri.arcgisruntime.symbology.SimpleMarkerSymbol.Style.SQUARE, -65536, 7.0F);
        SimpleMarkerSymbol mSelectedVertexSymbol = new SimpleMarkerSymbol(com.esri.arcgisruntime.symbology.SimpleMarkerSymbol.Style.SQUARE, -65536, 7.0F);

        SimpleMarkerSymbol mMidVertexSymbol = new SimpleMarkerSymbol(com.esri.arcgisruntime.symbology.SimpleMarkerSymbol.Style.CIRCLE, -1, 5.0F);
        SimpleMarkerSymbol mSelectedMidVertexSymbol = new SimpleMarkerSymbol(com.esri.arcgisruntime.symbology.SimpleMarkerSymbol.Style.CIRCLE, -1, 5.0F);

        mainSketchStyle.setVertexSymbol(mVertexSymbol);
        mainSketchStyle.setSelectedVertexSymbol(mSelectedVertexSymbol);

        mainSketchStyle.setMidVertexSymbol(mMidVertexSymbol);
        mainSketchStyle.setSelectedMidVertexSymbol(mSelectedMidVertexSymbol);

        mSketchEditor.setSketchStyle(mainSketchStyle);
        mMapView.setSketchEditor(mSketchEditor);
        SketchCreationMode mode = SketchCreationMode.POLYLINE;
        mSketchEditor.start(mode);
    }

    protected void doFinish(View v)
    {
        if(mSketchEditor !=null)
        {
            if(mEditType  == 0)
            {
                final FeatureLayer featureLayer = (FeatureLayer) mMapView.getMap().getOperationalLayers().get(0);
                Geometry geometry = mSketchEditor.getGeometry();
                geometry = GeometryEngine.project(geometry, featureLayer.getFeatureTable().getSpatialReference());
                Feature feature =  featureLayer.getFeatureTable().createFeature();
                feature.setGeometry(geometry);
                featureLayer.getFeatureTable().addFeatureAsync(feature);

                mSketchEditor.clearGeometry();
                mSketchEditor.stop();
                mSketchEditor = null;
                mSelectFeatures.clear();
            }
            else if(mEditType == 1)
            {
                if(mSelectFeatures.size() !=1)
                {
                    Toast.makeText(this,"请选择一个要素",(int)300).show();
                    return;
                }
                Feature mSelectFeature = mSelectFeatures.get(0);
                Geometry geometry = mSketchEditor.getGeometry();
                geometry = GeometryEngine.project(geometry, mSelectFeature.getGeometry().getSpatialReference());
                if(GeometryEngine.contains(mSelectFeature.getGeometry(),geometry))
                {
                    Geometry geometry1 = GeometryEngine.symmetricDifference(mSelectFeature.getGeometry(), geometry);
                    mSelectFeature.setGeometry(geometry1);
                    mSelectFeature.getFeatureTable().updateFeatureAsync(mSelectFeature);

                    Feature feature = mSelectFeature.getFeatureTable().createFeature();
                    feature.setGeometry(geometry);
                    mSelectFeature.getFeatureTable().addFeatureAsync(feature);
                }
                else
                {
                    Toast.makeText(this,"新创建图形必须在选择图形内部",(int)300).show();
                }
                mSketchEditor.clearGeometry();
                mSketchEditor.stop();
                mSketchEditor = null;
                mSelectFeatures.clear();
            }
            else if(mEditType == 2)
            {
                Feature mSelectFeature = mSelectFeatures.get(0);
                Geometry geometry = mSketchEditor.getGeometry();
                geometry = GeometryEngine.project(geometry, mSelectFeature.getGeometry().getSpatialReference());

                for(Integer i =0;i<mSelectFeatures.size();i++)
                {
                    Geometry geometry1 = GeometryEngine.intersection(geometry,mSelectFeatures.get(i).getGeometry());
                    geometry = GeometryEngine.symmetricDifference(geometry,geometry1);
                }

                Feature feature = mSelectFeature.getFeatureTable().createFeature();
                feature.setGeometry(geometry);
                mSelectFeature.getFeatureTable().addFeatureAsync(feature);

                mSketchEditor.clearGeometry();
                mSketchEditor.stop();
                mSketchEditor = null;
                mSelectFeatures.clear();
            }
            else if(mEditType == 3)
            {
                Feature mSelectFeature = mSelectFeatures.get(0);
                Geometry geometry = mSketchEditor.getGeometry();
                geometry = GeometryEngine.project(geometry, mSelectFeature.getGeometry().getSpatialReference());
                if(GeometryEngine.crosses(mSelectFeature.getGeometry(),geometry))
                {
                    Polyline tempLine = (Polyline)geometry;
                    Point start = tempLine.getParts().get(0).getStartPoint();
                    Point end = tempLine.getParts().get(0).getEndPoint();
                    if(!GeometryEngine.contains(mSelectFeature.getGeometry(),start) &&  !GeometryEngine.contains(mSelectFeature.getGeometry(),end))
                    {
                        for (Integer i = 0; i < mSelectFeatures.size(); i++)
                        {
                            List<Geometry> geometries = GeometryEngine.cut(mSelectFeatures.get(i).getGeometry(), (Polyline) geometry);
                            Geometry geometry1 = geometries.get(0);
                            for (Integer j = 1; j < geometries.size(); j++)
                            {
                                if (((Polygon) geometry1).getParts().size() > 1 && ((Polygon) geometries.get(j)).getParts().size() == 1)
                                {
                                    geometry1 = geometries.get(j);
                                } else if (GeometryEngine.area((Polygon) geometries.get(j)) > GeometryEngine.area((Polygon) geometry1)
                                        && ((Polygon) geometries.get(j)).getParts().size() == 1)
                                {
                                    geometry1 = geometries.get(j);
                                }
                            }

                            mSelectFeatures.get(i).setGeometry(geometry1);
                            mSelectFeatures.get(i).getFeatureTable().updateFeatureAsync(mSelectFeatures.get(i));
                        }
                    }
                    else
                    {
                        Toast.makeText(this,"绘制线起点和终点不能在选择要素内",(int)300).show();
                    }
                }
                else
                {
                    Toast.makeText(this,"绘制要素和选择要素没有交集",(int)300).show();
                }

                mSketchEditor.clearGeometry();
                mSketchEditor.stop();
                mSketchEditor = null;
                mSelectFeatures.clear();
            }
            else if(mEditType == 4)
            {
                final Feature mSelectFeature = mSelectFeatures.get(0);
                Geometry geometry = mSketchEditor.getGeometry();
                geometry = GeometryEngine.project(geometry, mSelectFeature.getGeometry().getSpatialReference());
                QueryParameters queryParams = new QueryParameters();
                if (geometry != null && !geometry.isEmpty())
                    queryParams.setGeometry(geometry);
                queryParams.setSpatialRelationship(QueryParameters.SpatialRelationship.INTERSECTS);
                final ListenableFuture<FeatureQueryResult> future = mSelectFeature.getFeatureTable().queryFeaturesAsync(queryParams);
                future.addDoneListener(new Runnable() {
                    @Override
                    public void run()
                    {
                        try
                        {
                            // 获取查询结果
                            FeatureQueryResult result = future.get();
                            Iterator<Feature> iterator = result.iterator();
                            Feature feature;
                            Geometry geometry2 = mSketchEditor.getGeometry();
                            geometry2 = GeometryEngine.project(geometry2, mSelectFeature.getGeometry().getSpatialReference());

                            Geometry geometry1 = null;
                            while (iterator.hasNext())
                            {
                                feature = iterator.next();
                                if (mSelectFeature.getAttributes().get("FID").equals(feature.getAttributes().get("FID")))
                                    continue;
                                geometry1 = GeometryEngine.intersection(feature.getGeometry(), geometry2);
                                Geometry geometry3 = GeometryEngine.symmetricDifference(feature.getGeometry(), geometry1);
                                feature.setGeometry(geometry3);
                                feature.getFeatureTable().updateFeatureAsync(feature);
                            }

                            mSelectFeature.setGeometry(geometry2);
                            mSelectFeature.getFeatureTable().updateFeatureAsync(mSelectFeature);
                            mSketchEditor.clearGeometry();
                            mSketchEditor.stop();
                            mSketchEditor = null;
                            mSelectFeatures.clear();
                        }
                        catch (InterruptedException | ExecutionException e)
                        {
                            e.printStackTrace();
                        }
                    }
                });
            }
            else if(mEditType == 5)
            {
                Feature mSelectFeature = mSelectFeatures.get(0);
                Geometry geometry = mSketchEditor.getGeometry();
                geometry = GeometryEngine.project(geometry, mSelectFeature.getGeometry().getSpatialReference());
                if(GeometryEngine.crosses(mSelectFeature.getGeometry(),geometry))
                {
                    Polyline tempLine = (Polyline)geometry;
                    Point start1 = tempLine.getParts().get(0).getStartPoint();
                    Point end1 = tempLine.getParts().get(0).getEndPoint();
                    if(!GeometryEngine.contains(mSelectFeature.getGeometry(),start1) && !GeometryEngine.contains(mSelectFeature.getGeometry(),end1))
                    {
                        for (Integer i = 0; i < mSelectFeatures.size(); i++)
                        {

                            List<Geometry> geometries = GeometryEngine.cut(mSelectFeatures.get(i).getGeometry(), (Polyline) geometry);
                            Geometry geometry1 = geometries.get(0);
                            for (Integer j = 1; j < geometries.size(); j++)
                            {
                                if (((Polygon) geometry1).getParts().size() > 1 && ((Polygon) geometries.get(j)).getParts().size() == 1)
                                {
                                    geometry1 = geometries.get(j);
                                }
                                else if (GeometryEngine.area((Polygon) geometries.get(j)) > GeometryEngine.area((Polygon) geometry1)
                                        && ((Polygon) geometries.get(j)).getParts().size() == 1)
                                {
                                    geometry1 = geometries.get(j);
                                }
                            }
                            Geometry resultGeometry = geometry1;

                            //获取图形与绘线相交剩余部分
                            Geometry differentGeometry = GeometryEngine.difference(geometry, GeometryEngine.boundary(geometry1));
                            Polyline diffPolyline = (Polyline) differentGeometry;
                            Polyline polyline = (Polyline) geometry;
                            for (Integer m = 0; m < diffPolyline.getParts().size(); m++)
                            {
                                Point start = diffPolyline.getParts().get(m).getStartPoint();
                                Point end = diffPolyline.getParts().get(m).getEndPoint();
                                Boolean isStartOrEnd = false;
                                for (Integer n = 0; n < polyline.getParts().size(); n++)
                                {
                                    ImmutablePart part = polyline.getParts().get(n);
                                    if ((part.getStartPoint().getX() == start.getX() && part.getStartPoint().getY() == start.getY()) ||
                                            (part.getEndPoint().getX() == end.getX() && part.getEndPoint().getY() == end.getY()))
                                    {
                                        isStartOrEnd = true;
                                        break;
                                    }
                                }

                                if (!isStartOrEnd)
                                {
                                    Polygon polygon = (Polygon) geometry1;
                                    for (Integer q = 0; q < polygon.getParts().size(); q++)
                                    {
                                        ImmutablePart part = polygon.getParts().get(q);
                                        Boolean isStarted = false;
                                        Boolean isEnd = false;
                                        Boolean isStartFirst = true;
                                        List<Point> points = new ArrayList<>();
                                        for (Integer t = 0; t < part.getPointCount(); t++)
                                        {
                                            if (Math.abs(part.getPoint(t).getX() - start.getX())<0.0001 && Math.abs(part.getPoint(t).getY() - start.getY())<0.0001)
                                            {
                                                isStarted = true;
                                                Log.i("point","----x对上");
                                            }
                                            else if (Math.abs(part.getPoint(t).getX() - end.getX())<0.0001 && Math.abs(part.getPoint(t).getY() - end.getY())<0.0001)
                                            {
                                                isEnd = true;
                                                if(!isStarted)
                                                    isStartFirst = false;
                                                Log.i("point","----y对上");
                                            }
                                            Log.i("point","-----开始x"+String.valueOf(part.getPoint(t).getX())+":"+String.valueOf(start.getX())+" y:"+
                                                    String.valueOf(part.getPoint(t).getY())+":"+String.valueOf(start.getY()));
                                            Log.i("point","-----结束x"+String.valueOf(part.getPoint(t).getX())+":"+String.valueOf(end.getX())+" y:"+
                                                    String.valueOf(part.getPoint(t).getY())+":"+String.valueOf(end.getY()));
                                            Log.i("point","-----");
                                            if (isStarted && isEnd)
                                            {
                                                points.add(part.getPoint(t));
                                                break;
                                            }
                                            else if (isStarted || isEnd)
                                            {
                                                points.add(part.getPoint(t));
                                            }
                                        }

                                        if (isStarted && isEnd)
                                        {
                                            PointCollection coloradoCorners = new PointCollection(geometry1.getSpatialReference());
                                            for (Integer b = 0; b < diffPolyline.getParts().get(m).getPointCount(); b++)
                                            {
                                                coloradoCorners.add(diffPolyline.getParts().get(m).getPoint(b));
                                            }
                                            if (isStartFirst)
                                            {
                                                for (Integer b = points.size() - 2; b >= 0; b--)
                                                {
                                                    coloradoCorners.add(points.get(b));
                                                }
                                            }
                                            else
                                            {
                                                for (Integer b = 1; b < points.size(); b++)
                                                {
                                                    coloradoCorners.add(points.get(b));
                                                }
                                            }
                                            Polygon resultPolygon = new Polygon(coloradoCorners);
                                            resultGeometry = GeometryEngine.union(resultGeometry, resultPolygon);
                                        }
                                    }
                                }
                            }
                            mSelectFeatures.get(i).setGeometry(resultGeometry);
                            mSelectFeatures.get(i).getFeatureTable().updateFeatureAsync(mSelectFeatures.get(i));
                        }
                    }
                    else
                    {
                        Toast.makeText(this,"绘制线起点和终点不能在选择要素内",(int)300).show();
                    }
                }
                else
                {
                    Toast.makeText(this,"绘制要素和选择要素没有交集",(int)300).show();
                }

                mSketchEditor.clearGeometry();
                mSketchEditor.stop();
                mSketchEditor = null;
                mSelectFeatures.clear();
            }
        }

        //清除选中效果
        doClearSelect(null);
    }

    /**
     * 询问是否允许访问本地文件权限回调
     */
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            if(path.endsWith(".shp"))
            {
                loadShapeFile(path);
            }
            else
            {
                //加载本地数据库
                loadGeodatabase(path);
            }
        }
        else
        {
            TMessage.Show("用户拒绝访问本地文件！");
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        mMapView.pause();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        mMapView.resume();
    }
}
