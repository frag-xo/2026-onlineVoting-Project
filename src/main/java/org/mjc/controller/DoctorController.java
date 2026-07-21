package org.mjc.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.mjc.dto.DTO;
import org.mjc.dto.doctor.DoctorQueryDTO;
import org.mjc.dto.doctor.DoctorResponseDTO;
import org.mjc.dto.doctor.DoctorSaveDTO;
import org.mjc.entity.Doctor;
import org.mjc.exception.BusinessException;
import org.mjc.service.DoctorService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 医生管理控制器
 */
@Tag(name = "医生管理", description = "医生信息管理相关接口")
@RestController
@RequestMapping("/api/doctor")
public class DoctorController {

    @Resource
    private DoctorService doctorService;

    @Operation(summary = "分页查询医生", description = "根据条件分页查询医生信息")
    @PostMapping("/page")
    public DTO<IPage<DoctorResponseDTO>> queryPage(@RequestBody DoctorQueryDTO queryDTO) {
        IPage<DoctorResponseDTO> page = doctorService.queryPageDTO(queryDTO);
        return new DTO<>(200, "查询成功", page);
    }

    @Operation(summary = "根据ID查询医生", description = "根据医生ID查询医生详细信息")
    @GetMapping("/{id}")
    public DTO<DoctorResponseDTO> getById(
            @Parameter(description = "医生ID", example = "1")
            @PathVariable("id") Long id) throws BusinessException {
        DoctorResponseDTO doctor = doctorService.getDoctorDTOById(id);
        if (doctor == null) {
            throw new BusinessException(404, "医生不存在");
        }
        return new DTO<>(200, "查询成功", doctor);
    }

    @Operation(summary = "新增医生", description = "新增一条医生记录")
    @PostMapping
    public DTO<Doctor> add(@Valid @RequestBody DoctorSaveDTO saveDTO) throws BusinessException {
        Doctor doctor = doctorService.addDoctor(saveDTO);
        if (doctor == null) {
            throw new BusinessException(500, "新增失败");
        }
        return new DTO<>(200, "新增成功", doctor);
    }

    @Operation(summary = "修改医生", description = "修改医生信息，必须传入医生ID")
    @PutMapping
    public DTO<Doctor> update(@Valid @RequestBody DoctorSaveDTO saveDTO) throws BusinessException {
        if (saveDTO.getId() == null) {
            throw new BusinessException(400, "医生ID不能为空");
        }
        Doctor doctor = doctorService.updateDoctor(saveDTO);
        if (doctor == null) {
            throw new BusinessException(500, "修改失败");
        }
        return new DTO<>(200, "修改成功", doctor);
    }

    @Operation(summary = "根据ID删除医生", description = "删除指定ID的医生记录")
    @DeleteMapping("/{id}")
    public DTO<String> deleteById(
            @Parameter(description = "医生ID", example = "1")
            @PathVariable("id") Long id) throws BusinessException {
        boolean result = doctorService.deleteDoctorById(id);
        if (!result) {
            throw new BusinessException(500, "删除失败");
        }
        return new DTO<>(200, "删除成功", "医生ID: " + id);
    }

    @Operation(summary = "批量删除医生", description = "批量删除指定ID列表的医生记录")
    @DeleteMapping("/batch")
    public DTO<List<Long>> deleteBatch(@RequestBody List<Long> ids) throws BusinessException {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException(400, "ID列表不能为空");
        }
        boolean result = doctorService.deleteDoctorBatch(ids);
        if (!result) {
            throw new BusinessException(500, "批量删除失败");
        }
        return new DTO<List<Long>>(200, "批量删除成功", ids);
    }
}
