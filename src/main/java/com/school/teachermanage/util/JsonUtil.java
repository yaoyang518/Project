package com.school.teachermanage.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.school.teachermanage.bean.DataResult;
import net.sf.json.JSONObject;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * JSON 工具类
 *
 * @author zhangsl
 * @date 2018-01-24
 */
public class JsonUtil {

    private static final String NULL_EXCEPTION = "系统异常：JsonJacksonConverter.toString返回空值";
    private static final String CONVERTE_EXCEPTION = "Json转换异常";
    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    public static String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            e.printStackTrace();
            DataResult result = new DataResult();
            result.setSuccess(false);
            result.setMsg(CONVERTE_EXCEPTION);
            try {
                return objectMapper.writeValueAsString(result);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        return NULL_EXCEPTION;
    }

    public static <T> T toObject(String obj, Class<T> clazz) {
        try {
            return objectMapper.readValue(obj, clazz);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String readJsonFile(String filePath) {
        try {
            File file = new File(filePath);
            String content = FileUtils.readFileToString(file, "UTF-8");
            return content;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JSONObject fromObject(Object object){
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.getSerializerProvider().setNullValueSerializer(new JsonSerializer<Object>()
            {
                @Override
                public void serialize(Object o, JsonGenerator jsonGenerator,
                                      SerializerProvider serializerProvider)
                        throws IOException, JsonProcessingException
                {
                    jsonGenerator.writeString("");
                }
            });
            return JSONObject.fromObject(objectMapper.writeValueAsString(object));
        }catch (Exception e){
            e.printStackTrace();
        }
        return JSONObject.fromObject(object);
    }

    public static void main(String[] args) throws Exception {

    }

}
