package org.cosmos.minecosmos;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

/**
 * @author TT432
 */
public class Nets {
    public static String postRequest(String url, String json){
        try {
            String encoding = "UTF-8";
            String body = "";
            //创建httpclient对象
            HttpPost httpGetWithEntity = new HttpPost(url);
            HttpEntity httpEntity = new StringEntity(json, ContentType.APPLICATION_JSON);
            httpGetWithEntity.setEntity(httpEntity);
            //执行请求操作，并拿到结果（同步阻塞）
            CloseableHttpClient client = HttpClientBuilder.create().build();
            CloseableHttpResponse response = client.execute(httpGetWithEntity);
            //获取结果实体
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                //按指定编码转换结果实体为String类型
                body = EntityUtils.toString(entity, encoding);
            }
            //释放链接
            response.close();
            return body;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
