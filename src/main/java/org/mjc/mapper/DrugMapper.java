package org.mjc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import org.mjc.entity.Drug;

import java.util.List;

/**
 * 药品 Mapper 接口（带结果映射）
 * 
 * @author System
 * @since 2026-07-13
 */
@Mapper
public interface DrugMapper extends BaseMapper<Drug> {

    /**
     * 定义结果映射
     */
    @Results(id = "drugResultMap", value = {
            @Result(column = "drug_id", property = "drugId", id = true),
            @Result(column = "drug_name", property = "drugName"),
            @Result(column = "drug_info", property = "drugInfo"),
            @Result(column = "drug_effect", property = "drugEffect"),
            @Result(column = "drug_img", property = "drugImg"),
            @Result(column = "createtime", property = "createTime"),
            @Result(column = "updatetime", property = "updateTime"),
            @Result(column = "publisher", property = "publisher")
    })
    @Select("SELECT * FROM drug WHERE drug_id = #{drugId}")
    Drug selectDrugById(@Param("drugId") Long drugId);

    /**
     * 使用预定义的结果映射
     */
    @ResultMap("drugResultMap")
    @Select("SELECT * FROM drug WHERE drug_name LIKE CONCAT('%', #{drugName}, '%')")
    List<Drug> selectByDrugNameWithMap(@Param("drugName") String drugName);

    /**
     * 使用预定义的结果映射
     */
    @ResultMap("drugResultMap")
    @Select("SELECT * FROM drug WHERE drug_effect LIKE CONCAT('%', #{effect}, '%')")
    List<Drug> selectByEffectWithMap(@Param("effect") String effect);

    /**
     * 使用预定义的结果映射
     */
    @ResultMap("drugResultMap")
    @Select("SELECT * FROM drug ORDER BY createtime DESC LIMIT #{limit}")
    List<Drug> selectLatestWithMap(@Param("limit") Integer limit);

    /**
     * 使用预定义的结果映射
     */
    @ResultMap("drugResultMap")
    @Select("SELECT * FROM drug WHERE publisher = #{publisher}")
    List<Drug> selectByPublisherWithMap(@Param("publisher") String publisher);
}