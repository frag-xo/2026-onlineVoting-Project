package org.mjc.utils;//package org.lg.utils;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.mjc.dto.DTO;
import org.springframework.data.redis.serializer.SerializationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping.NON_FINAL;

public class JacksonUtils {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static final String CLASS_KEY = "@class";

    public static final String BASE_VALUE_KEY = "\"value\"";
    public static final String BASE_VALUE_NODE_KEY = "value";

    static {
        SimpleModule module = new SimpleModule();
        mapper.enableDefaultTypingAsProperty(NON_FINAL, CLASS_KEY);
//        module.addKeyDeserializer(Object.class, new MapKeyDeserializer());
//        module.addKeySerializer(Object.class, new MapKeySerializer());
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.registerModule(module);
    }

    public static String serialize(Object obj) throws SerializationException {
        try {
            return mapper.writeValueAsString(obj);
        } catch (Throwable t) {
            throw new SerializationException(t.getMessage());
        }
    }

    public static <T> T deserialize(String jsonString, Class<T> clazz) throws
            SerializationException {
        try {
            return mapper.readValue(jsonString, clazz);
        } catch (Throwable t) {
            throw new SerializationException(t.getMessage());
        }
    }

    public static JsonNode readNode(String jsonString) throws SerializationException {
        try {
            return mapper.readTree(jsonString);
        } catch (Throwable t) {
            throw new SerializationException(t.getMessage());
        }
    }

    /**
     * redis序列化器
     * @param jsonString
     * @param clazz
     * @param t
     * @param <T>
     * @return
     * @throws SerializationException
     */
    public static <T> DTO deserializeDto(String jsonString, Class<DTO> clazz, Class<T> t) throws
            SerializationException {
        DTO<T> dto = null;
        try {
            Optional<String> jsonStringOp = Optional.ofNullable(jsonString);
            if(!jsonStringOp.isPresent()){
              dto = new DTO(409,"json字符串为空");
            }else {
                dto = JSONUtil.toBean(jsonString, DTO.class);
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(jsonString);

                //手动处理t
                Optional<JsonNode> tJsonNodeOp = Optional.ofNullable(root.get("t"));
                if (tJsonNodeOp.isPresent() && !tJsonNodeOp.get().toString().equals("null")) {
                    System.out.println(tJsonNodeOp.get());
                    T parseT = JSONUtil.toBean(tJsonNodeOp.get().toString(), t);
                    dto.setT(parseT);
                }
                //手动处理tList
                JsonNode tListJsonNode = root.get("tList");
                Optional<JsonNode> tListJsonNodeOP = Optional.ofNullable(tListJsonNode);
                if (tListJsonNodeOP.isPresent() && !tListJsonNodeOP.get().toString().equals("null") && tListJsonNodeOP.get().size() > 0) {

                    JSONArray objects = JSONUtil.parseArray(tListJsonNode.get(1).toString());
                    List<T> tList = new ArrayList<T>();
                    for (Object obj : objects) {
                        T deT = deserialize(obj.toString(), t);
                        tList.add(deT);
                    }
                    dto.settList(tList);
                }
                //手动处理obj
                Optional<JsonNode> objNodeOp = Optional.ofNullable(root.get("obj"));
                if (objNodeOp.isPresent() && !objNodeOp.get().toString().equals("null")) {
                    Optional<JsonNode> classNameJSonNodeOp = Optional.ofNullable(objNodeOp.get().get("@class"));
                    if (classNameJSonNodeOp.isPresent() && !classNameJSonNodeOp.get().toString().equals("null")) {
                        Class objClass = Class.forName(classNameJSonNodeOp.get().toString().replace("\"", ""));
                        Object o = JSONUtil.toBean(objNodeOp.get().toString(), objClass);
                        dto.setObj(o);
                    }
                }
            }

            return dto;
        } catch (Throwable tr) {
            //tr.printStackTrace();//打印异常的引用栈
            throw new SerializationException(tr.getMessage());
        }
    }



}
