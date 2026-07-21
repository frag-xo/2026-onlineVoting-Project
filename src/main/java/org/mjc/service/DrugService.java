package org.mjc.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.mjc.entity.Drug;
import org.mjc.dto.drug.DrugQueryDTO;
import org.mjc.dto.drug.DrugSaveDTO;
import org.mjc.dto.drug.DrugResponseDTO;

import java.util.List;

/**
 * 药品服务接口
 * 
 * @author System
 * @since 2026-07-13
 */
public interface DrugService extends IService<Drug> {

    // ==================== 分页查询 ====================

    /**
     * 分页查询药品（返回实体）
     *
     * @param queryDTO 查询参数
     * @return 分页结果
     */
    IPage<Drug> queryPage(DrugQueryDTO queryDTO);

    /**
     * 分页查询药品（返回DTO）
     *
     * @param queryDTO 查询参数
     * @return 分页结果（DTO）
     */
    IPage<DrugResponseDTO> queryPageDTO(DrugQueryDTO queryDTO);

    /**
     * 简单分页查询（无条件）
     *
     * @param pageNum 当前页码
     * @param pageSize 每页条数
     * @return 分页结果
     */
    IPage<Drug> queryPageSimple(Integer pageNum, Integer pageSize);

    /**
     * 根据药品名称分页查询
     *
     * @param drugName 药品名称
     * @param pageNum 当前页码
     * @param pageSize 每页条数
     * @return 分页结果
     */
    IPage<Drug> queryPageByName(String drugName, Integer pageNum, Integer pageSize);

    /**
     * 关键词搜索分页
     *
     * @param keyword 关键词
     * @param pageNum 当前页码
     * @param pageSize 每页条数
     * @return 分页结果
     */
    IPage<Drug> searchDrugsPage(String keyword, Integer pageNum, Integer pageSize);

    // ==================== 按主键查询 ====================

    /**
     * 根据ID查询药品
     *
     * @param drugId 药品ID
     * @return 药品实体
     */
    Drug getDrugById(Long drugId);

    /**
     * 根据ID查询药品（返回DTO）
     *
     * @param drugId 药品ID
     * @return 药品响应DTO
     */
    DrugResponseDTO getDrugDTOById(Long drugId);

    /**
     * 批量根据ID查询药品
     *
     * @param ids 药品ID列表
     * @return 药品列表
     */
    List<Drug> getDrugsByIds(List<Long> ids);

    // ==================== 新增Drug ====================

    /**
     * 新增药品
     *
     * @param drug 药品实体
     * @return 是否成功
     */
    boolean addDrug(Drug drug);

    /**
     * 新增药品（使用DTO）
     *
     * @param saveDTO 保存参数
     * @return 保存后的药品
     */
    Drug addDrug(DrugSaveDTO saveDTO);

    /**
     * 批量新增药品
     *
     * @param drugList 药品列表
     * @return 是否成功
     */
    boolean addDrugBatch(List<Drug> drugList);

    // ==================== 修改Drug ====================

    /**
     * 修改药品
     *
     * @param drug 药品实体
     * @return 是否成功
     */
    boolean updateDrug(Drug drug);

    /**
     * 修改药品（使用DTO）
     *
     * @param saveDTO 保存参数
     * @return 修改后的药品
     */
    Drug updateDrug(DrugSaveDTO saveDTO);

    /**
     * 批量修改药品
     *
     * @param drugList 药品列表
     * @return 是否成功
     */
    boolean updateDrugBatch(List<Drug> drugList);

    /**
     * 更新药品图片
     *
     * @param drugId 药品ID
     * @param imgUrl 图片URL
     * @return 是否成功
     */
    boolean updateDrugImg(Long drugId, String imgUrl);

    /**
     * 更新药品发布者
     *
     * @param drugId 药品ID
     * @param publisher 发布者
     * @return 是否成功
     */
    boolean updatePublisher(Long drugId, String publisher);

    // ==================== 批量删除Drug ====================

    /**
     * 批量删除药品（根据ID列表）
     *
     * @param ids 药品ID列表
     * @return 是否成功
     */
    boolean deleteDrugBatch(List<Long> ids);

    /**
     * 批量删除药品（根据条件）
     *
     * @param publisher 发布者
     * @return 是否成功
     */
    boolean deleteDrugsByPublisher(String publisher);

    /**
     * 删除所有药品
     *
     * @return 是否成功
     */
    boolean deleteAllDrugs();

    // ==================== 按主键删除 ====================

    /**
     * 根据ID删除药品
     *
     * @param drugId 药品ID
     * @return 是否成功
     */
    boolean deleteDrugById(Long drugId);

    // ==================== 其他查询方法 ====================

    /**
     * 查询所有药品
     *
     * @return 药品列表
     */
    List<Drug> getAllDrugs();

    /**
     * 根据药品名称模糊查询
     *
     * @param drugName 药品名称
     * @return 药品列表
     */
    List<Drug> getDrugsByName(String drugName);

    /**
     * 根据发布者查询药品
     *
     * @param publisher 发布者
     * @return 药品列表
     */
    List<Drug> getDrugsByPublisher(String publisher);

    /**
     * 查询最近发布的药品
     *
     * @param limit 数量限制
     * @return 药品列表
     */
    List<Drug> getLatestDrugs(Integer limit);

    /**
     * 检查药品是否存在
     *
     * @param drugId 药品ID
     * @return 是否存在
     */
    boolean exists(Long drugId);

    /**
     * 检查药品名称是否重复
     *
     * @param drugName 药品名称
     * @param excludeId 排除的ID（更新时使用）
     * @return 是否重复
     */
    boolean isNameDuplicate(String drugName, Long excludeId);

    // ==================== 统计方法 ====================

    /**
     * 统计药品总数
     *
     * @return 药品总数
     */
    Long countDrugs();

    /**
     * 统计发布者的药品数量
     *
     * @param publisher 发布者
     * @return 药品数量
     */
    Integer countByPublisher(String publisher);

    /**
     * 生成随机药品数据
     *
     * @param count 生成数量
     * @return 生成的药品列表
     */
    List<Drug> generateRandomDrugs(int count);
}