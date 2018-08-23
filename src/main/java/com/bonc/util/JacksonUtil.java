package com.bonc.util;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.StringWriter;

/**
 * JacksonUtil工具类
 */
public class JacksonUtil {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private static final JsonFactory JSONFACTORY = new JsonFactory();

    /**
     * 转换vo 为 json
     */
    @SuppressWarnings("deprecation")
    public static String bean2Json(Object o) {
        StringWriter sw = new StringWriter();
        JsonGenerator jsonGenerator = null;

        try {
            jsonGenerator = JSONFACTORY.createJsonGenerator(sw);
            MAPPER.writeValue(jsonGenerator, o);
            return sw.toString();

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (jsonGenerator != null) {
                try {
                    jsonGenerator.close();
                } catch (IOException e) {

                }
            }
        }
    }

    /**
     * 把json参数转换成对应Vo
     *
     * @param json
     * @param valueTypeRef
     * @param <T>
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static <T> T json2Bean(String json, TypeReference valueTypeRef) {

        T rtn = null;
        try {
            rtn = MAPPER.readValue(json, valueTypeRef);
        } catch (JsonParseException e) {
            throw new RuntimeException(e);
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return rtn;
    }

}
