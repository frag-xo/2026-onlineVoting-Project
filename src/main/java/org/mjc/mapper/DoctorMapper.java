package org.mjc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.mjc.entity.Doctor;

import java.util.List;

/**
 * 医生 Mapper 接口
 */
@Mapper
public interface DoctorMapper extends BaseMapper<Doctor> {

    @Results(id = "doctorResultMap", value = {
            @Result(column = "id", property = "id", id = true),
            @Result(column = "name", property = "name"),
            @Result(column = "age", property = "age"),
            @Result(column = "sex", property = "sex"),
            @Result(column = "level_id", property = "levelId"),
            @Result(column = "phone", property = "phone"),
            @Result(column = "type_id", property = "typeId"),
            @Result(column = "hospital", property = "hospital"),
            @Result(column = "createtime", property = "createTime"),
            @Result(column = "updatetime", property = "updateTime"),
            @Result(column = "account_id", property = "accountId")
    })
    @Select("SELECT * FROM doctor WHERE id = #{id}")
    Doctor selectDoctorById(@Param("id") Long id);

    @ResultMap("doctorResultMap")
    @Select("SELECT * FROM doctor WHERE name LIKE CONCAT('%', #{name}, '%')")
    List<Doctor> selectByNameWithMap(@Param("name") String name);
}
