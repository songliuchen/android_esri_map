package tszs.esrigis.query;

import com.esri.arcgisruntime.data.Feature;

import java.util.List;

/**
 * 属性查询回调接口
 */
public interface TAttributeListener
{
    /**
     * 执行回调
     * @param tableName  查询表名称
     * @param data list中的map 为要素的属性key 为属性名，value 为属性值
     */
    void execute(String tableName, List<Feature> data);
}
