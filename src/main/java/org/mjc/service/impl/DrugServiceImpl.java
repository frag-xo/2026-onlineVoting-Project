package org.mjc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.mjc.dto.drug.DrugQueryDTO;
import org.mjc.dto.drug.DrugResponseDTO;
import org.mjc.dto.drug.DrugSaveDTO;
import org.mjc.entity.Drug;
import org.mjc.mapper.DrugMapper;
import org.mjc.service.DrugService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 药品服务实现类
 *
 * @author System
 * @since 2026-07-13
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class DrugServiceImpl extends ServiceImpl<DrugMapper, Drug> implements DrugService {

    @Resource
    private DrugMapper drugMapper;

    // ==================== 随机数据生成 ====================

    private static final String[] DRUG_NAMES = {
        "复方感冒灵颗粒", "阿莫西林胶囊", "布洛芬缓释胶囊", "头孢拉定胶囊", "氯雷他定片",
        "奥美拉唑肠溶胶囊", "盐酸二甲双胍片", "阿托伐他汀钙片", "硝苯地平控释片", "蒙脱石散",
        "复方甘草片", "氢溴酸右美沙芬糖浆", "维生素C片", "葡萄糖酸锌口服液", "复方丹参滴丸",
        "连花清瘟胶囊", "板蓝根颗粒", "小柴胡颗粒", "藿香正气水", "云南白药粉",
        "正红花油", "风油精", "清凉油", "创可贴", "碘伏消毒液",
        "盐酸氟西汀胶囊", "碳酸锂缓释片", "丙戊酸钠片", "氯氮平片", "奥氮平片",
        "甲钴胺片", "维生素B12注射液", "复方阿胶浆", "生脉饮", "玉屏风颗粒",
        "补中益气丸", "六味地黄丸", "金匮肾气丸", "逍遥丸", "血府逐瘀口服液",
        "复方阿胶浆", "乌鸡白凤丸", "妇科千金片", "金刚藤胶囊", "桂枝茯苓胶囊",
        "脑心通胶囊", "通心络胶囊", "芪苈强心胶囊", "复方血栓通胶囊", "丹参酮胶囊",
        "复方甘草酸苷片", "护肝片", "熊去氧胆酸胶囊", "复方铝酸铋颗粒", "胶体果胶铋胶囊"
    };

    private static final String[][] DRUG_INFO = {
        {"金银花、五指柑、野菊花、三角枫、羊红膻"},
        {"阿莫西林"},
        {"布洛芬"},
        {"头孢拉定"},
        {"氯雷他定"},
        {"奥美拉唑"},
        {"盐酸二甲双胍"},
        {"阿托伐他汀钙"},
        {"硝苯地平"},
        {"蒙脱石"},
        {"甘草流浸膏、阿片粉、咖啡因、薄荷脑"},
        {"氢溴酸右美沙芬"},
        {"维生素C"},
        {"葡萄糖酸锌"},
        {"丹参、三七、冰片"}
    };

    private static final String[][] DRUG_EFFECT = {
        {"辛凉解表，清热解毒"},
        {"抗菌消炎"},
        {"镇痛、抗炎、抗风湿"},
        {"抗菌消炎"},
        {"抗过敏"},
        {"抑制胃酸分泌"},
        {"降血糖"},
        {"降低胆固醇"},
        {"降压"},
        {"止泻"},
        {"镇咳祛痰"},
        {"镇咳"},
        {"补充维生素C"},
        {"补锌"},
        {"活血化瘀，理气止痛"}
    };

    private static final String[] PUBLISHERS = {"管理员", "药师张三", "药师李四", "医师王五"};

    private static final String[] IMG_URLS = {
        "https://example.com/images/drug1.jpg",
        "https://example.com/images/drug2.jpg",
        "https://example.com/images/drug3.jpg",
        "https://example.com/images/drug4.jpg",
        "https://example.com/images/drug5.jpg"
    };

    private final java.util.Random random = new java.util.Random();

    /**
     * 生成随机药品数据
     */
    @Override
    public List<Drug> generateRandomDrugs(int count) {
        List<Drug> drugList = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (int i = 0; i < count; i++) {
            Drug drug = new Drug();

            // 随机选择药品名称（避免重复）
            String baseName = DRUG_NAMES[random.nextInt(DRUG_NAMES.length)];
            String suffix = i > 0 && i % 10 == 0 ? "（新规格）" : "";
            String drugName = baseName + suffix;
            drug.setDrugName(drugName);

            // 随机选择成分信息
            String drugInfo = DRUG_INFO[random.nextInt(DRUG_INFO.length)][0];
            drug.setDrugInfo(drugInfo);

            // 随机选择功效
            String drugEffect = DRUG_EFFECT[random.nextInt(DRUG_EFFECT.length)][0];
            drug.setDrugEffect(drugEffect);

            // 随机选择图片
            String imgUrl = IMG_URLS[random.nextInt(IMG_URLS.length)];
            drug.setDrugImg(imgUrl);

            // 随机选择发布者
            drug.setPublisher(PUBLISHERS[random.nextInt(PUBLISHERS.length)]);

            // 设置时间
            LocalDateTime createTime = now.minusHours(random.nextInt(720)); // 30天内
            drug.setCreateTime(createTime);
            drug.setUpdateTime(createTime);

            drugList.add(drug);
        }

        // 批量插入
        this.saveBatch(drugList);
        log.info("成功生成 {} 条随机药品数据", count);
        return drugList;
    }

    // ==================== 分页查询 ====================

    @Override
    public IPage<Drug> queryPage(DrugQueryDTO queryDTO) {
        if (queryDTO == null) {
            queryDTO = new DrugQueryDTO();
        }

        Page<Drug> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());

        LambdaQueryWrapper<Drug> wrapper = new LambdaQueryWrapper<>();

        // 药品名称模糊查询
        if (StringUtils.hasText(queryDTO.getDrugName())) {
            wrapper.like(Drug::getDrugName, queryDTO.getDrugName());
        }

        // 药品功能模糊查询
        if (StringUtils.hasText(queryDTO.getDrugEffect())) {
            wrapper.like(Drug::getDrugEffect, queryDTO.getDrugEffect());
        }

        // 发布者查询
        if (StringUtils.hasText(queryDTO.getPublisher())) {
            wrapper.eq(Drug::getPublisher, queryDTO.getPublisher());
        }

        // 时间范围查询
        if (queryDTO.getStartTime() != null) {
            wrapper.ge(Drug::getCreateTime, queryDTO.getStartTime());
        }
        if (queryDTO.getEndTime() != null) {
            wrapper.le(Drug::getCreateTime, queryDTO.getEndTime());
        }

        // 是否有图片
        if (queryDTO.getHasImage() != null && queryDTO.getHasImage()) {
            wrapper.isNotNull(Drug::getDrugImg);
            wrapper.ne(Drug::getDrugImg, "");
        }

        // 按创建时间降序
        wrapper.orderByDesc(Drug::getCreateTime);

        return this.page(page, wrapper);
    }

    @Override
    public IPage<DrugResponseDTO> queryPageDTO(DrugQueryDTO queryDTO) {
        IPage<Drug> page = queryPage(queryDTO);

        IPage<DrugResponseDTO> dtoPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        List<DrugResponseDTO> dtoList = page.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        dtoPage.setRecords(dtoList);

        return dtoPage;
    }

    @Override
    public IPage<Drug> queryPageSimple(Integer pageNum, Integer pageSize) {
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }

        Page<Drug> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Drug> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Drug::getCreateTime);

        return this.page(page, wrapper);
    }

    @Override
    public IPage<Drug> queryPageByName(String drugName, Integer pageNum, Integer pageSize) {
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }

        Page<Drug> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Drug> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(drugName)) {
            wrapper.like(Drug::getDrugName, drugName);
        }

        wrapper.orderByDesc(Drug::getCreateTime);
        return this.page(page, wrapper);
    }

    @Override
    public IPage<Drug> searchDrugsPage(String keyword, Integer pageNum, Integer pageSize) {
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }

        Page<Drug> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Drug> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(keyword)) {
            wrapper.like(Drug::getDrugName, keyword)
                    .or()
                    .like(Drug::getDrugEffect, keyword);
        }

        wrapper.orderByDesc(Drug::getCreateTime);
        return this.page(page, wrapper);
    }

    // ==================== 按主键查询 ====================

    @Override
    public Drug getDrugById(Long drugId) {
        if (drugId == null) {
            return null;
        }
        return this.getById(drugId);
    }

    @Override
    public DrugResponseDTO getDrugDTOById(Long drugId) {
        Drug drug = getDrugById(drugId);
        return drug != null ? convertToDTO(drug) : null;
    }

    @Override
    public List<Drug> getDrugsByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }
        return this.listByIds(ids);
    }

    // ==================== 新增Drug ====================

    @Override
    public boolean addDrug(Drug drug) {
        if (drug == null) {
            log.warn("新增药品失败：药品对象为空");
            return false;
        }

        // 检查名称是否重复
        if (isNameDuplicate(drug.getDrugName(), null)) {
            log.warn("新增药品失败：药品名称已存在 - {}", drug.getDrugName());
            throw new RuntimeException("药品名称已存在: " + drug.getDrugName());
        }

        // 设置创建时间和更新时间
        LocalDateTime now = LocalDateTime.now();
        drug.setCreateTime(now);
        drug.setUpdateTime(now);

        boolean result = this.save(drug);
        if (result) {
            log.info("新增药品成功: {} (ID: {})", drug.getDrugName(), drug.getDrugId());
        } else {
            log.warn("新增药品失败: {}", drug.getDrugName());
        }
        return result;
    }

    @Override
    public Drug addDrug(DrugSaveDTO saveDTO) {
        if (saveDTO == null) {
            log.warn("新增药品失败：DTO对象为空");
            return null;
        }

        Drug drug = new Drug();
        BeanUtils.copyProperties(saveDTO, drug);

        boolean result = addDrug(drug);
        return result ? drug : null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addDrugBatch(List<Drug> drugList) {
        if (drugList == null || drugList.isEmpty()) {
            log.warn("批量新增药品失败：药品列表为空");
            return false;
        }

        // 检查名称重复
        for (Drug drug : drugList) {
            if (isNameDuplicate(drug.getDrugName(), null)) {
                log.warn("批量新增药品失败：药品名称已存在 - {}", drug.getDrugName());
                throw new RuntimeException("药品名称已存在: " + drug.getDrugName());
            }
        }

        // 设置创建时间和更新时间
        LocalDateTime now = LocalDateTime.now();
        for (Drug drug : drugList) {
            drug.setCreateTime(now);
            drug.setUpdateTime(now);
        }

        boolean result = this.saveBatch(drugList);
        if (result) {
            log.info("批量新增药品成功: {} 条", drugList.size());
        } else {
            log.warn("批量新增药品失败");
        }
        return result;
    }

    // ==================== 修改Drug ====================

    @Override
    public boolean updateDrug(Drug drug) {
        if (drug == null || drug.getDrugId() == null) {
            log.warn("修改药品失败：药品对象为空或ID为空");
            return false;
        }

        // 检查药品是否存在
        if (!exists(drug.getDrugId())) {
            log.warn("修改药品失败：药品不存在 - ID: {}", drug.getDrugId());
            throw new RuntimeException("药品不存在: " + drug.getDrugId());
        }

        // 检查名称是否重复（排除自身）
        if (isNameDuplicate(drug.getDrugName(), drug.getDrugId())) {
            log.warn("修改药品失败：药品名称已存在 - {}", drug.getDrugName());
            throw new RuntimeException("药品名称已存在: " + drug.getDrugName());
        }

        // 设置更新时间
        drug.setUpdateTime(LocalDateTime.now());

        boolean result = this.updateById(drug);
        if (result) {
            log.info("修改药品成功: ID: {}", drug.getDrugId());
        } else {
            log.warn("修改药品失败: ID: {}", drug.getDrugId());
        }
        return result;
    }

    @Override
    public Drug updateDrug(DrugSaveDTO saveDTO) {
        if (saveDTO == null || saveDTO.getDrugId() == null) {
            log.warn("修改药品失败：DTO对象为空或ID为空");
            return null;
        }

        Drug drug = new Drug();
        BeanUtils.copyProperties(saveDTO, drug);

        boolean result = updateDrug(drug);
        return result ? drug : null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateDrugBatch(List<Drug> drugList) {
        if (drugList == null || drugList.isEmpty()) {
            log.warn("批量修改药品失败：药品列表为空");
            return false;
        }

        // 设置更新时间
        LocalDateTime now = LocalDateTime.now();
        for (Drug drug : drugList) {
            if (drug.getDrugId() == null) {
                log.warn("批量修改药品失败：存在ID为空的药品");
                throw new RuntimeException("药品ID不能为空");
            }
            drug.setUpdateTime(now);
        }

        boolean result = this.updateBatchById(drugList);
        if (result) {
            log.info("批量修改药品成功: {} 条", drugList.size());
        } else {
            log.warn("批量修改药品失败");
        }
        return result;
    }

    @Override
    public boolean updateDrugImg(Long drugId, String imgUrl) {
        if (drugId == null) {
            log.warn("更新药品图片失败：药品ID为空");
            return false;
        }

        if (!exists(drugId)) {
            log.warn("更新药品图片失败：药品不存在 - ID: {}", drugId);
            return false;
        }

        Drug drug = new Drug();
        drug.setDrugId(drugId);
        drug.setDrugImg(imgUrl);
        drug.setUpdateTime(LocalDateTime.now());

        boolean result = this.updateById(drug);
        if (result) {
            log.info("更新药品图片成功: ID: {}", drugId);
        }
        return result;
    }

    @Override
    public boolean updatePublisher(Long drugId, String publisher) {
        if (drugId == null || !StringUtils.hasText(publisher)) {
            log.warn("更新药品发布者失败：参数不完整");
            return false;
        }

        if (!exists(drugId)) {
            log.warn("更新药品发布者失败：药品不存在 - ID: {}", drugId);
            return false;
        }

        Drug drug = new Drug();
        drug.setDrugId(drugId);
        drug.setPublisher(publisher);
        drug.setUpdateTime(LocalDateTime.now());

        boolean result = this.updateById(drug);
        if (result) {
            log.info("更新药品发布者成功: ID: {}, Publisher: {}", drugId, publisher);
        }
        return result;
    }

    // ==================== 批量删除Drug ====================

    @Override
    public boolean deleteDrugBatch(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            log.warn("批量删除药品失败：ID列表为空");
            return false;
        }

        boolean result = this.removeByIds(ids);
        if (result) {
            log.info("批量删除药品成功: {} 条", ids.size());
        } else {
            log.warn("批量删除药品失败: {}", ids);
        }
        return result;
    }

    @Override
    public boolean deleteDrugsByPublisher(String publisher) {
        if (!StringUtils.hasText(publisher)) {
            log.warn("根据发布者删除药品失败：发布者为空");
            return false;
        }

        LambdaQueryWrapper<Drug> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Drug::getPublisher, publisher);

        boolean result = this.remove(wrapper);
        if (result) {
            log.info("根据发布者删除药品成功: {}", publisher);
        }
        return result;
    }

    @Override
    public boolean deleteAllDrugs() {
        boolean result = this.remove(null);
        if (result) {
            log.warn("删除所有药品成功");
        }
        return result;
    }

    // ==================== 按主键删除 ====================

    @Override
    public boolean deleteDrugById(Long drugId) {
        if (drugId == null) {
            log.warn("删除药品失败：药品ID为空");
            return false;
        }

        // 检查药品是否存在
        if (!exists(drugId)) {
            log.warn("删除药品失败：药品不存在 - ID: {}", drugId);
            return false;
        }

        boolean result = this.removeById(drugId);
        if (result) {
            log.info("删除药品成功: ID: {}", drugId);
        } else {
            log.warn("删除药品失败: ID: {}", drugId);
        }
        return result;
    }

    // ==================== 其他查询方法 ====================

    @Override
    public List<Drug> getAllDrugs() {
        LambdaQueryWrapper<Drug> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Drug::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    public List<Drug> getDrugsByName(String drugName) {
        if (!StringUtils.hasText(drugName)) {
            return new ArrayList<>();
        }

        LambdaQueryWrapper<Drug> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(Drug::getDrugName, drugName)
                .orderByDesc(Drug::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    public List<Drug> getDrugsByPublisher(String publisher) {
        if (!StringUtils.hasText(publisher)) {
            return new ArrayList<>();
        }

        LambdaQueryWrapper<Drug> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Drug::getPublisher, publisher)
                .orderByDesc(Drug::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    public List<Drug> getLatestDrugs(Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 10;
        }

        LambdaQueryWrapper<Drug> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Drug::getCreateTime)
                .last("LIMIT " + limit);
        return this.list(wrapper);
    }

    @Override
    public boolean exists(Long drugId) {
        if (drugId == null) {
            return false;
        }
        return this.getById(drugId) != null;
    }

    @Override
    public boolean isNameDuplicate(String drugName, Long excludeId) {
        if (!StringUtils.hasText(drugName)) {
            return false;
        }

        LambdaQueryWrapper<Drug> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Drug::getDrugName, drugName);

        if (excludeId != null) {
            wrapper.ne(Drug::getDrugId, excludeId);
        }

        return this.count(wrapper) > 0;
    }

    // ==================== 统计方法 ====================

    @Override
    public Long countDrugs() {
        return this.count();
    }

    @Override
    public Integer countByPublisher(String publisher) {
        if (!StringUtils.hasText(publisher)) {
            return 0;
        }

        LambdaQueryWrapper<Drug> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Drug::getPublisher, publisher);
        return Math.toIntExact(this.count(wrapper));
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 转换实体为DTO
     */
    private DrugResponseDTO convertToDTO(Drug drug) {
        if (drug == null) {
            return null;
        }

        DrugResponseDTO dto = new DrugResponseDTO();
        BeanUtils.copyProperties(drug, dto);

        // 设置额外属性
        dto.setHasImage(StringUtils.hasText(drug.getDrugImg()));
        dto.setShortName(getShortName(drug.getDrugName()));

        return dto;
    }

    /**
     * 获取药品简称
     */
    private String getShortName(String drugName) {
        if (!StringUtils.hasText(drugName)) {
            return "";
        }
        return drugName.length() > 10 ? drugName.substring(0, 10) + "..." : drugName;
    }
}