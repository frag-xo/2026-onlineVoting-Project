package org.mjc.mapper;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import jakarta.annotation.Resource;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@Component
public class RedisMapper {
    /*定义自动装配就不能指定泛型*/
    @Resource
    protected RedisTemplate<Serializable,Serializable> redisTemplate;

    /**
     * 设置redisTemplate
     * @param redisTemplate the redisTemplate to set
     */
    public void setRedisTemplate(RedisTemplate<Serializable,Serializable> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 获取 RedisSerializer
     * <br>------------------------------<br>
     */
    protected RedisSerializer<String> getRedisSerializer() {
        return redisTemplate.getStringSerializer();
    }
    /**
     * 添加对象
     */
//    @Override
//    public boolean add(final Chart chart) {
//        boolean result = redisTemplate.execute(new RedisCallback<Boolean>() {
//            public Boolean doInRedis(RedisConnection connection)
//                    throws DataAccessException {
//                RedisSerializer<String> serializer = getRedisSerializer();
//                byte[] key = serializer.serialize(chart.getTypeName());
//                byte[] name = serializer.serialize(chart.getName());
//                return connection.setNX(key, name);
//            }
//        });
//        return result;
//    }

    /**
     * 添加集合
     */
//    @Override
//    public boolean add(final List<Chart> list) {
//        Assert.notEmpty(list);
//        boolean result = redisTemplate.execute(new RedisCallback<Boolean>() {
//            public Boolean doInRedis(RedisConnection connection)
//                    throws DataAccessException {
//                RedisSerializer<String> serializer = getRedisSerializer();
//                for (Chart chart : list) {
////                    byte[] key  = serializer.serialize(chart.getPid());
////                    byte[] name = serializer.serialize(chart.getPname());
////                    byte[] key  = serializer.serialize(chart.getPid());
////                    byte[] key  = serializer.serialize(chart.getPid());
//                    //connection.setNX(key, name);
//                }
//                return true;
//            }
//        }, false, true);
//        return result;
//    }

    /**
     * 删除对象 ,依赖key
     */

    public void delete(String key) {
        List<String> list = new ArrayList<String>();
        list.add(key);
        delete(list);
    }

    /**
     * 删除集合 ,依赖key集合
     */
    public void delete(List keys) {
        redisTemplate.delete(keys);
        redisTemplate.exec();
    }

/**
 * 批量按前缀删除key
 */
    public Long delByPrefix(final String prefixKey){
        Set<Serializable> keys = redisTemplate.keys(prefixKey);
        keys.stream().forEach(System.out::println);
        if(!CollectionUtils.isEmpty(keys)) {
            Long delete = redisTemplate.delete(keys);
            redisTemplate.exec();
            return delete;
        }

        return null;
    }





    /**
     * 修改对象
     */
   // public boolean update(final Chart chart) {
//        String key = chart.getPid();
//        if (get(key) == null) {
//            throw new NullPointerException("数据行不存在, key = " + key);
//        }
//        boolean result = redisTemplate.execute(new RedisCallback<Boolean>() {
//            public Boolean doInRedis(RedisConnection connection)
//                    throws DataAccessException {
//                RedisSerializer<String> serializer = getRedisSerializer();
//                byte[] key  = serializer.serialize(chart.getPid());
//                byte[] name = serializer.serialize(chart.getPname());
//                connection.set(key, name);
//                return true;
//            }
//        });
        //return result;
//        return false;
//    }

    /**
     * 根据key获取对象
     */
        public String get(final String keyId) {
            String result = redisTemplate.execute(new RedisCallback<String>() {
            public String doInRedis(RedisConnection connection)
                    throws DataAccessException {
                RedisSerializer<String> serializer = getRedisSerializer();//拿到原来的序列化器
                byte[] key = serializer.serialize(keyId);//正确序列化后的key才能查到值
                byte[] value = connection.get(key);
                if (value == null) {
                    return null;
                }
                String val = serializer.deserialize(value);//查到的值正确反序列化
                // return new Chart(keyId, nickname);
                return val;
            }
        });
        return result;
    }

//    public T Page<T> getPagePoint(final String keyId) {
//        Page<T> result = redisTemplate.execute(new RedisCallback<Page<T>>() {
//            public Page<T> doInRedis(RedisConnection connection)
//                    throws DataAccessException {
//                RedisSerializer<String> serializer = getRedisSerializer();//拿到原来的序列化器
//                byte[] key = serializer.serialize(keyId);//正确序列化后的key才能查到值
//                byte[] value = connection.get(key);
//                if (value == null) {
//                    return null;
//                }
//                Page<T> val = (Page<T>)redisTemplate.getValueSerializer().deserialize(value);//查到的值正确反序列化
//                // return new Chart(keyId, nickname);
//                return val;
//            }
//        });
//        return result;
//    }

    /**
     * 根据key删除对象
     */
    public Boolean del(final String keyId) {
        Boolean result = redisTemplate.execute(new RedisCallback<Boolean>() {
            public Boolean doInRedis(RedisConnection connection)
                    throws DataAccessException {
                RedisSerializer<String> serializer = getRedisSerializer();//拿到原来的序列化器
                byte[] key = serializer.serialize(keyId);//正确序列化后的key才能查到值
                Long del = connection.del(key);
                if (del == 0) {
                    return false;
                }
                return true;
            }
        });
        return result;
    }

//    public Chart get(final String keyId) {
//        Chart result = redisTemplate.execute(new RedisCallback<Chart>() {
//            public Chart doInRedis(RedisConnection connection)
//                    throws DataAccessException {
//                RedisSerializer<String> serializer = getRedisSerializer();
//                byte[] key = serializer.serialize(keyId);
//                byte[] value = connection.get(key);
//                if (value == null) {
//                    return null;
//                }
//                String nickname = serializer.deserialize(value);
//                // return new Chart(keyId, nickname);
//                return null;
//            }
//        });
//        return result;
//    }
//
//    @Override
//    public List<Chart> getCharts(String typeName) {
//        List<Chart> charts = redisTemplate.execute(new RedisCallback<List<Chart>>() {
//            public  List<Chart> doInRedis(RedisConnection connection)
//                    throws DataAccessException {
//                GenericJackson2JsonRedisSerializer jackson2JsonRedisSerializer = new GenericJackson2JsonRedisSerializer();
//
//                RedisSerializer<String> serializer = getRedisSerializer();
//                byte[] key = serializer.serialize(typeName);
//                byte[] value = connection.get(key);
//                return (List<Chart>)jackson2JsonRedisSerializer.deserialize(value);
//            }
//        },true,false);
//        return charts;
//    }
//
//    public boolean pushCharts(List<Chart> charts,String typeName) {
//        boolean result = redisTemplate.execute(new RedisCallback<Boolean>() {
//            public Boolean doInRedis(RedisConnection connection)
//                    throws DataAccessException {
//                GenericJackson2JsonRedisSerializer jackson2JsonRedisSerializer = new GenericJackson2JsonRedisSerializer();
//
//                RedisSerializer<String> serializer = getRedisSerializer();
//                byte[] key = serializer.serialize(typeName);
//                byte[] name = jackson2JsonRedisSerializer.serialize(charts);
//                connection.set(key, name);
//                System.out.println(key+" "+name);
//                return true;
//            }
//        },true,false);
//        return result;
//    }
}