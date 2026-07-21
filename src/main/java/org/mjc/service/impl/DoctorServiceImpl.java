package org.mjc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.mjc.dto.doctor.DoctorQueryDTO;
import org.mjc.dto.doctor.DoctorResponseDTO;
import org.mjc.dto.doctor.DoctorSaveDTO;
import org.mjc.entity.Doctor;
import org.mjc.mapper.DoctorMapper;
import org.mjc.service.DoctorService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 医生服务实现类
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class DoctorServiceImpl extends ServiceImpl<DoctorMapper, Doctor> implements DoctorService {

    @Override
    public IPage<DoctorResponseDTO> queryPageDTO(DoctorQueryDTO queryDTO) {
        if (queryDTO == null) {
            queryDTO = new DoctorQueryDTO();
        }

        Page<Doctor> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        LambdaQueryWrapper<Doctor> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(queryDTO.getName())) {
            wrapper.like(Doctor::getName, queryDTO.getName());
        }
        if (StringUtils.hasText(queryDTO.getHospital())) {
            wrapper.like(Doctor::getHospital, queryDTO.getHospital());
        }
        if (queryDTO.getLevelId() != null) {
            wrapper.eq(Doctor::getLevelId, queryDTO.getLevelId());
        }
        if (queryDTO.getTypeId() != null) {
            wrapper.eq(Doctor::getTypeId, queryDTO.getTypeId());
        }
        if (queryDTO.getSex() != null) {
            wrapper.eq(Doctor::getSex, queryDTO.getSex());
        }
        if (StringUtils.hasText(queryDTO.getPhone())) {
            wrapper.eq(Doctor::getPhone, queryDTO.getPhone());
        }

        wrapper.orderByDesc(Doctor::getCreateTime);

        IPage<Doctor> doctorPage = this.page(page, wrapper);
        IPage<DoctorResponseDTO> dtoPage = new Page<>(doctorPage.getCurrent(), doctorPage.getSize(), doctorPage.getTotal());
        dtoPage.setRecords(doctorPage.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList()));
        return dtoPage;
    }

    @Override
    public DoctorResponseDTO getDoctorDTOById(Long id) {
        if (id == null) {
            return null;
        }
        Doctor doctor = this.getById(id);
        return doctor != null ? convertToDTO(doctor) : null;
    }

    @Override
    public Doctor addDoctor(DoctorSaveDTO saveDTO) {
        if (saveDTO == null) {
            log.warn("新增医生失败：DTO对象为空");
            return null;
        }

        Doctor doctor = new Doctor();
        BeanUtils.copyProperties(saveDTO, doctor);

        LocalDateTime now = LocalDateTime.now();
        doctor.setCreateTime(now);
        doctor.setUpdateTime(now);

        boolean result = this.save(doctor);
        if (result) {
            log.info("新增医生成功: {} (ID: {})", doctor.getName(), doctor.getId());
            return doctor;
        }
        log.warn("新增医生失败: {}", doctor.getName());
        return null;
    }

    @Override
    public Doctor updateDoctor(DoctorSaveDTO saveDTO) {
        if (saveDTO == null || saveDTO.getId() == null) {
            log.warn("修改医生失败：DTO对象为空或ID为空");
            return null;
        }

        if (!exists(saveDTO.getId())) {
            log.warn("修改医生失败：医生不存在 - ID: {}", saveDTO.getId());
            throw new RuntimeException("医生不存在: " + saveDTO.getId());
        }

        Doctor doctor = new Doctor();
        BeanUtils.copyProperties(saveDTO, doctor);
        doctor.setUpdateTime(LocalDateTime.now());

        boolean result = this.updateById(doctor);
        if (result) {
            log.info("修改医生成功: ID: {}", doctor.getId());
            return doctor;
        }
        log.warn("修改医生失败: ID: {}", doctor.getId());
        return null;
    }

    @Override
    public boolean deleteDoctorById(Long id) {
        if (id == null) {
            log.warn("删除医生失败：医生ID为空");
            return false;
        }
        if (!exists(id)) {
            log.warn("删除医生失败：医生不存在 - ID: {}", id);
            return false;
        }

        boolean result = this.removeById(id);
        if (result) {
            log.info("删除医生成功: ID: {}", id);
        } else {
            log.warn("删除医生失败: ID: {}", id);
        }
        return result;
    }

    @Override
    public boolean deleteDoctorBatch(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            log.warn("批量删除医生失败：ID列表为空");
            return false;
        }

        boolean result = this.removeByIds(ids);
        if (result) {
            log.info("批量删除医生成功: {} 条", ids.size());
        } else {
            log.warn("批量删除医生失败: {}", ids);
        }
        return result;
    }

    @Override
    public boolean exists(Long id) {
        if (id == null) {
            return false;
        }
        return this.getById(id) != null;
    }

    private DoctorResponseDTO convertToDTO(Doctor doctor) {
        if (doctor == null) {
            return null;
        }

        DoctorResponseDTO dto = new DoctorResponseDTO();
        BeanUtils.copyProperties(doctor, dto);
        dto.setSexDesc(getSexDesc(doctor.getSex()));
        return dto;
    }

    private String getSexDesc(Integer sex) {
        if (sex == null) {
            return null;
        }
        return switch (sex) {
            case 1 -> "男";
            case 2 -> "女";
            default -> "未知";
        };
    }
}
