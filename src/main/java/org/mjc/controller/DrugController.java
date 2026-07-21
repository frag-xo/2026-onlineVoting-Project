package org.mjc.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.mjc.dto.DTO;
import org.mjc.dto.drug.DrugQueryDTO;
import org.mjc.dto.drug.DrugResponseDTO;
import org.mjc.dto.drug.DrugSaveDTO;
import org.mjc.entity.Drug;
import org.mjc.exception.BusinessException;
import org.mjc.service.DrugService;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * 药品管理控制器
 *
 * @author System
 * @since 2026-07-13
 */
@Tag(name = "药品管理", description = "药品信息管理相关接口")
@RestController
@RequestMapping("/api/drug")
public class DrugController {

    @Resource
    private DrugService drugService;

    // ==================== 分页查询接口 ====================

    @Operation(summary = "分页查询药品", description = "根据条件分页查询药品信息")
    @PostMapping("/page")
    public DTO<IPage<DrugResponseDTO>> queryPage(@RequestBody DrugQueryDTO queryDTO) {
        IPage<DrugResponseDTO> page = drugService.queryPageDTO(queryDTO);
        return new DTO<>(200, "查询成功", page);
    }

    @Operation(summary = "简单分页查询", description = "无条件分页查询所有药品")
    @GetMapping("/page/simple")
    public DTO<IPage<Drug>> queryPageSimple(
            @Parameter(description = "当前页码", example = "1")
            @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页条数", example = "10")
            @RequestParam(defaultValue = "10") Integer pageSize) {
        IPage<Drug> page = drugService.queryPageSimple(pageNum, pageSize);
        return new DTO<IPage<Drug>>(200, "查询成功", page);
    }

    @Operation(summary = "根据名称分页查询", description = "根据药品名称模糊分页查询")
    @GetMapping("/page/name")
    public DTO<IPage<Drug>> queryPageByName(
            @Parameter(description = "药品名称", example = "感冒")
            @RequestParam(required = false) String drugName,
            @Parameter(description = "当前页码", example = "1")
            @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页条数", example = "10")
            @RequestParam(defaultValue = "10") Integer pageSize) {
        IPage<Drug> page = drugService.queryPageByName(drugName, pageNum, pageSize);
        return new DTO<IPage<Drug>>(200, "查询成功", page);
    }

    @Operation(summary = "关键词搜索分页", description = "根据关键词在药品名称和功能中搜索")
    @GetMapping("/page/search")
    public DTO<IPage<Drug>> searchPage(
            @Parameter(description = "搜索关键词", example = "感冒")
            @RequestParam String keyword,
            @Parameter(description = "当前页码", example = "1")
            @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页条数", example = "10")
            @RequestParam(defaultValue = "10") Integer pageSize) {
        IPage<Drug> page = drugService.searchDrugsPage(keyword, pageNum, pageSize);
        return new DTO<IPage<Drug>>(200, "查询成功", page);
    }

    // ==================== 按主键查询接口 ====================

    @Operation(summary = "根据ID查询药品", description = "根据药品ID查询药品详细信息")
    @GetMapping("/{id}")
    public DTO<DrugResponseDTO> getById(
            @Parameter(description = "药品ID", example = "12650466")
            @PathVariable("id") Long id) throws BusinessException {
        DrugResponseDTO drug = drugService.getDrugDTOById(id);
        if (drug == null) {
            throw new BusinessException(404, "药品不存在");
        }
        return new DTO<>(200, "查询成功", drug);
    }

    @Operation(summary = "批量根据ID查询药品", description = "根据ID列表批量查询药品")
    @PostMapping("/ids")
    public DTO<Drug> getByIds(@RequestBody List<Long> ids) throws BusinessException {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException(400, "ID列表不能为空");
        }
        List<Drug> drugs = drugService.getDrugsByIds(ids);
        if (drugs == null || drugs.isEmpty()) {
            throw new BusinessException(404, "未找到药品");
        }
        return new DTO<>(200, "查询成功", drugs);
    }

    // ==================== 新增药品接口 ====================

    @Operation(summary = "新增药品", description = "新增一条药品记录")
    @PostMapping
    public DTO<Drug> add(@Valid @RequestBody DrugSaveDTO saveDTO) throws BusinessException{
        Drug drug = drugService.addDrug(saveDTO);
        if (drug == null) {
            throw new BusinessException(500, "新增失败");
        }
        return new DTO<>(200, "新增成功", drug);
    }

    @Operation(summary = "批量新增药品", description = "批量新增多条药品记录")
    @PostMapping("/batch")
    public DTO<Drug> addBatch(@RequestBody List<Drug> drugList) throws BusinessException{
        if (drugList == null || drugList.isEmpty()) {
            throw new BusinessException(400, "药品列表不能为空");
        }
        boolean result = drugService.addDrugBatch(drugList);
        if (!result) {
            throw new BusinessException(500, "批量新增失败");
        }
        return new DTO<>(200, "批量新增成功", drugList);
    }

    // ==================== 修改药品接口 ====================

    @Operation(summary = "修改药品", description = "修改药品信息，必须传入药品ID")
    @PutMapping
    public DTO<Drug> update(@Valid @RequestBody DrugSaveDTO saveDTO)throws BusinessException {
        if (saveDTO.getDrugId() == null) {
            throw new BusinessException(400, "药品ID不能为空");
        }
        Drug drug = drugService.updateDrug(saveDTO);
        if (drug == null) {
            throw new BusinessException(500, "修改失败");
        }
        return new DTO<>(200, "修改成功", drug);
    }

    @Operation(summary = "批量修改药品", description = "批量修改多条药品记录")
    @PutMapping("/batch")
    public DTO<Drug> updateBatch(@RequestBody List<Drug> drugList) throws BusinessException{
        if (drugList == null || drugList.isEmpty()) {
            throw new BusinessException(400, "药品列表不能为空");
        }
        boolean result = drugService.updateDrugBatch(drugList);
        if (!result) {
            throw new BusinessException(500, "批量修改失败");
        }
        return new DTO<>(200, "批量修改成功", drugList);
    }

    @Operation(summary = "更新药品图片", description = "更新指定药品的图片URL")
    @PutMapping("/{id}/img")
    public DTO<String> updateImg(
            @Parameter(description = "药品ID", example = "12650466")
            @PathVariable("id") Long id,
            @Parameter(description = "图片URL", example = "http://localhost:8080/image/xxx.jpg")
            @RequestParam String imgUrl) throws BusinessException{
        boolean result = drugService.updateDrugImg(id, imgUrl);
        if (!result) {
            throw new BusinessException(500, "更新图片失败");
        }
        return new DTO<>(200, "更新图片成功", imgUrl);
    }

    @Operation(summary = "更新药品发布者", description = "更新指定药品的发布者信息")
    @PutMapping("/{id}/publisher")
    public DTO<String> updatePublisher(
            @Parameter(description = "药品ID", example = "12650466")
            @PathVariable("id") Long id,
            @Parameter(description = "发布者", example = "管理员")
            @RequestParam String publisher) throws BusinessException{
        boolean result = drugService.updatePublisher(id, publisher);
        if (!result) {
            throw new BusinessException(500, "更新发布者失败");
        }
        return new DTO<>(200, "更新发布者成功", publisher);
    }

    // ==================== 删除药品接口 ====================

    @Operation(summary = "根据ID删除药品", description = "删除指定ID的药品记录")
    @DeleteMapping("/{id}")
    public DTO<String> deleteById(
            @Parameter(description = "药品ID", example = "12650466")
            @PathVariable("id") Long id) throws BusinessException{
        boolean result = drugService.deleteDrugById(id);
        if (!result) {
            throw new BusinessException(500, "删除失败");
        }
        return new DTO<>(200, "删除成功", "药品ID: " + id);
    }

    @Operation(summary = "批量删除药品", description = "批量删除指定ID列表的药品记录")
    @DeleteMapping("/batch")
    public DTO<Long> deleteBatch(@RequestBody List<Long> ids) throws BusinessException{
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException(400, "ID列表不能为空");
        }
        boolean result = drugService.deleteDrugBatch(ids);
        if (!result) {
            throw new BusinessException(500, "批量删除失败");
        }
        return new DTO<>(200, "批量删除成功", ids);
    }

    @Operation(summary = "根据发布者删除药品", description = "删除指定发布者的所有药品记录")
    @DeleteMapping("/publisher/{publisher}")
    public DTO<String> deleteByPublisher(
            @Parameter(description = "发布者名称", example = "管理员")
            @PathVariable("publisher") String publisher) throws BusinessException{
        boolean result = drugService.deleteDrugsByPublisher(publisher);
        if (!result) {
            throw new BusinessException(500, "删除失败");
        }
        return new DTO<>(200, "删除成功", "发布者: " + publisher);
    }

    @Operation(summary = "删除所有药品", description = "删除所有药品记录（危险操作）")
    @DeleteMapping("/all")
    public DTO<String> deleteAll() throws BusinessException{
        boolean result = drugService.deleteAllDrugs();
        if (!result) {
            throw new BusinessException(500, "删除失败");
        }
        return new DTO<>(200, "删除所有药品成功");
    }

    // ==================== 查询接口 ====================

    @Operation(summary = "获取所有药品", description = "获取所有药品列表")
    @GetMapping("/all")
    public DTO<Drug> getAll() {
        List<Drug> drugs = drugService.getAllDrugs();
        return new DTO<>(200, "查询成功", drugs);
    }

    @Operation(summary = "根据药品名称查询", description = "根据药品名称模糊查询")
    @GetMapping("/name")
    public DTO<Drug> getByName(
            @Parameter(description = "药品名称", example = "感冒")
            @RequestParam String drugName) {
        List<Drug> drugs = drugService.getDrugsByName(drugName);
        return new DTO<>(200, "查询成功", drugs);
    }

    @Operation(summary = "根据发布者查询", description = "根据发布者查询药品列表")
    @GetMapping("/publisher/{publisher}")
    public DTO<Drug> getByPublisher(
            @Parameter(description = "发布者", example = "管理员")
            @PathVariable("publisher") String publisher) {
        List<Drug> drugs = drugService.getDrugsByPublisher(publisher);
        return new DTO<>(200, "查询成功", drugs);
    }

    @Operation(summary = "获取最近发布的药品", description = "获取最近发布的N条药品记录")
    @GetMapping("/latest")
    public DTO<Drug> getLatest(
            @Parameter(description = "数量限制", example = "10")
            @RequestParam(defaultValue = "10") Integer limit) {
        List<Drug> drugs = drugService.getLatestDrugs(limit);
        return new DTO<>(200, "查询成功", drugs);
    }





    // ==================== 检查接口 ====================

    @Operation(summary = "检查药品是否存在", description = "根据ID检查药品是否存在")
    @GetMapping("/{id}/exists")
    public DTO<Boolean> exists(
            @Parameter(description = "药品ID", example = "12650466")
            @PathVariable("id") Long id) {
        boolean exists = drugService.exists(id);
        return new DTO<>(200, "检查成功", exists);
    }

    @Operation(summary = "检查药品名称是否重复", description = "检查药品名称是否已存在")
    @GetMapping("/check-name")
    public DTO<Boolean> checkName(
            @Parameter(description = "药品名称", example = "复方感冒灵颗粒")
            @RequestParam String drugName,
            @Parameter(description = "排除的药品ID（更新时使用）", example = "12650466")
            @RequestParam(required = false) Long excludeId) {
        boolean duplicate = drugService.isNameDuplicate(drugName, excludeId);
        return new DTO<>(200, "检查成功", duplicate);
    }

    // ==================== 统计接口 ====================

    @Operation(summary = "统计药品总数", description = "统计所有药品的总数量")
    @GetMapping("/count")
    public DTO<Long> count() {
        Long count = drugService.countDrugs();
        return new DTO<>(200, "统计成功", count);
    }

    @Operation(summary = "统计发布者的药品数量", description = "统计指定发布者的药品数量")
    @GetMapping("/count/publisher")
    public DTO<Integer> countByPublisher(
            @Parameter(description = "发布者", example = "管理员")
            @RequestParam String publisher) {
        Integer count = drugService.countByPublisher(publisher);
        return new DTO<>(200, "统计成功", count);
    }

    // ==================== 数据初始化接口 ====================

    @Operation(summary = "生成随机药品数据", description = "生成指定数量的随机药品数据用于测试")
    @PostMapping("/init/random")
    public DTO<List<Drug>> initRandomData(
            @Parameter(description = "生成数量", example = "50")
            @RequestParam(defaultValue = "50") int count) throws BusinessException {
        if (count <= 0 || count > 1000) {
            throw new BusinessException(400, "生成数量必须在1-1000之间");
        }
        List<Drug> drugs = drugService.generateRandomDrugs(count);
        return new DTO<List<Drug>>(200, "成功生成 " + count + " 条药品数据", drugs);
    }
}