package org.mjc.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.mjc.dto.doctor.DoctorQueryDTO;
import org.mjc.dto.doctor.DoctorResponseDTO;
import org.mjc.dto.doctor.DoctorSaveDTO;
import org.mjc.entity.Doctor;

import java.util.List;

/**
 * 医生服务接口
 */
public interface DoctorService extends IService<Doctor> {

    /**
     * 分页查询医生（返回DTO）
     */
    IPage<DoctorResponseDTO> queryPageDTO(DoctorQueryDTO queryDTO);

    /**
     * 根据ID查询医生（返回DTO）
     */
    DoctorResponseDTO getDoctorDTOById(Long id);

    /**
     * 新增医生
     */
    Doctor addDoctor(DoctorSaveDTO saveDTO);

    /**
     * 修改医生
     */
    Doctor updateDoctor(DoctorSaveDTO saveDTO);

    /**
     * 根据ID删除医生
     */
    boolean deleteDoctorById(Long id);

    /**
     * 批量删除医生
     */
    boolean deleteDoctorBatch(List<Long> ids);

    /**
     * 检查医生是否存在
     */
    boolean exists(Long id);
}
