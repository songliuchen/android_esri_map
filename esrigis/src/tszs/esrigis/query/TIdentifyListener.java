package tszs.esrigis.query;

import java.util.List;
import java.util.Map;

/**
 * I键查询回调接口
 */
public interface TIdentifyListener
{
    /**
     * 执行回调
     * @param data
     * 第一个key 为图层名称，list 为图层查到的数据集合，list中的map 为要素的属性key 为属性名，value 为属性值
     */
    void execute(Map<String,List<Map<String, Object>>> data);
}
