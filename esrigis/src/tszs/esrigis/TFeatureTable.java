package tszs.esrigis;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.FeatureTable;
import com.esri.arcgisruntime.data.Field;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.data.ShapefileFeatureTable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

import tszs.esrigis.control.TMessage;
import tszs.esrigis.query.TAttributeListener;
import tszs.system.TString;
import tszs.system.log.TLog;

/**
 * 矢量数据表对象
 */
public class TFeatureTable
{
    /***
     * 属性查询
     * @param featureTable 查询图层
     * @param params 查询条件
     */
    public static void attributeQuery(final FeatureTable featureTable, String params, final TAttributeListener listener)
    {
        QueryParameters queryParams = new QueryParameters();
        if(TString.IsNullOrEmpty(params))
            params = "1=1";
        queryParams.setWhereClause(params);

        final ListenableFuture<FeatureQueryResult> future = featureTable.queryFeaturesAsync(queryParams);
        future.addDoneListener(new Runnable()
        {
            @Override public void run()
            {
                try
                {
                    // 获取查询结果
                    FeatureQueryResult result = future.get();

                    List<Feature> features = new ArrayList<Feature>();
                    Iterator<Feature> iterator = result.iterator();
                    Feature feature;
                    while (iterator.hasNext())
                    {
                        feature = iterator.next();
                        features.add(feature);
                    }
                    if(listener != null)
                        listener.execute(featureTable.getTableName(),features);
                }
                catch (InterruptedException | ExecutionException e)
                {
                    TLog.Print("Error in FeatureQueryResult: " + e.getMessage(), TLog.Lever.Error);
                    TMessage.Show("SQL异常！");
                }
            }
        });
    }

    /**
     * 打开本地的shape文件转成FeatureTable
     * @param path
     * @return
     */
    public static FeatureTable GetFeatureTableFormShape(String path)
    {
        ShapefileFeatureTable shapefileFeatureTable = new ShapefileFeatureTable(path);
        return  shapefileFeatureTable;
    }

    /**
     * 判断是否为系统字段
     * @param featureTable 矢量图层对象
     * @param fieldName 字段名
     * @return
     */
    public static Boolean IsSystemField(FeatureTable featureTable, String fieldName)
    {
        if(TString.IsNullOrEmpty(fieldName))
            return  false;
        Field field = featureTable.getField(fieldName);
        if(field == null)
            return  null;

        if(field.getFieldType().equals(Field.Type.OID))
            return  true;

        if(field.getFieldType().equals(Field.Type.GEOMETRY))
            return  true;

        if(field.getFieldType().equals(Field.Type.GLOBALID))
            return  true;

        if(field.getFieldType().equals(Field.Type.GUID))
            return  true;

        if(fieldName.toUpperCase().equals("SHAPE_LENGTH"))
            return  true;

        if(fieldName.toUpperCase().equals("SHAPE_AREA"))
            return  true;

        return  false;
    }

    /**
     * 获取OID字段名
     * @param featureTable 矢量图层对象
     * @return
     */
    public static String GetOIDFieldName(FeatureTable featureTable)
    {
        if(featureTable == null)
            return "";

        for(Integer i = 0;i<featureTable.getFields().size();i++)
        {
            if(featureTable.getFields().get(i).getFieldType().equals(Field.Type.OID))
                return  featureTable.getFields().get(i).getName();
        }
        return  "";
    }
}
