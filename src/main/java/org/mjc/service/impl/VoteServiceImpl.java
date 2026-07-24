package org.mjc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.mjc.dto.vote.VoteQueryDTO;
import org.mjc.dto.vote.VoteResponseDTO;
import org.mjc.dto.vote.VoteSaveDTO;
import org.mjc.entity.Vote;
import org.mjc.entity.VoteOption;
import org.mjc.exception.BusinessException;
import org.mjc.exception.ErrorCode;
import org.mjc.mapper.VoteMapper;
import org.mjc.service.VoteOptionService;
import org.mjc.service.VoteRecordService;
import org.mjc.service.VoteService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 投票服务实现类
 *
 * @author Online_Voting
 * @since 2026-07-22
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class VoteServiceImpl extends ServiceImpl<VoteMapper, Vote> implements VoteService {

    @Resource
    private VoteOptionService voteOptionService;

    @Resource
    private VoteRecordService voteRecordService;

    // ==================== 随机数据生成 ====================

    private static final String[] VOTE_TITLES = {
        "最喜欢的编程语言", "最佳开发工具", "最常用的数据库", "最喜欢的框架",
        "最好的IDE", "最常用的版本控制", "最喜欢的前端框架", "最常用的包管理器",
        "最好的代码编辑器", "最常用的测试框架", "最喜欢的云服务商", "最常用的CI/CD工具",
        "最好的容器技术", "最常用的监控工具", "最喜欢的日志框架", "最常用的构建工具"
    };

    private static final String[] VOTE_DESCRIPTIONS = {
        "请选择你最喜欢的技术", "投票选出最佳工具", "分享你的技术偏好",
        "帮助社区了解技术趋势", "技术选型参考", "开发者调查"
    };

    private static final String[][] VOTE_OPTIONS = {
        {"Java", "Python", "JavaScript", "Go", "Rust"},
        {"VS Code", "IntelliJ IDEA", "Sublime Text", "Vim", "Emacs"},
        {"MySQL", "PostgreSQL", "MongoDB", "Redis", "SQLite"},
        {"Spring Boot", "Django", "Express.js", "Gin", "Actix"},
        {"IntelliJ IDEA", "Visual Studio", "PyCharm", "WebStorm", "Eclipse"},
        {"Git", "SVN", "Mercurial", "Perforce", "Bazaar"},
        {"Vue.js", "React", "Angular", "Svelte", "jQuery"},
        {"npm", "yarn", "pnpm", "pip", "maven"}
    };

    private static final Long[] CREATOR_IDS = {1L, 2L, 3L, 4L, 5L};

    private final java.util.Random random = new java.util.Random();

    /**
     * 生成随机投票数据
     */
    @Override
    public List<Vote> generateRandomVotes(int count) {
        List<Vote> voteList = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (int i = 0; i < count; i++) {
            Vote vote = new Vote();

            // 随机选择标题
            String title = VOTE_TITLES[random.nextInt(VOTE_TITLES.length)];
            vote.setTitle(title);

            // 随机选择描述
            String description = VOTE_DESCRIPTIONS[random.nextInt(VOTE_DESCRIPTIONS.length)];
            vote.setDescription(description);

            // 随机状态（0-未开始，1-进行中，2-已结束）
            int status = random.nextInt(3);
            vote.setStatus(status);

            // 随机时间
            LocalDateTime startTime = now.minusDays(random.nextInt(7)); // 7天内开始
            LocalDateTime endTime = now.plusDays(random.nextInt(14)); // 14天内结束
            vote.setStartTime(startTime);
            vote.setEndTime(endTime);

            // 随机创建者
            Long creatorId = CREATOR_IDS[random.nextInt(CREATOR_IDS.length)];
            vote.setCreatorId(creatorId);

            // 设置时间
            LocalDateTime createTime = now.minusHours(random.nextInt(720)); // 30天内
            vote.setCreateTime(createTime);
            vote.setUpdateTime(createTime);

            voteList.add(vote);
        }

        // 批量插入
        this.saveBatch(voteList);

        // 为每个投票生成选项
        for (int i = 0; i < voteList.size(); i++) {
            Vote vote = voteList.get(i);
            String[] options = VOTE_OPTIONS[i % VOTE_OPTIONS.length];
            List<VoteOption> optionList = new ArrayList<>();
            for (int j = 0; j < options.length; j++) {
                VoteOption option = new VoteOption();
                option.setVoteId(vote.getId());
                option.setOptionText(options[j]);
                option.setSortOrder(j + 1);
                option.setCreateTime(now);
                optionList.add(option);
            }
            voteOptionService.addOptionsBatch(optionList);
        }

        log.info("成功生成 {} 条随机投票数据", count);
        return voteList;
    }

    // ==================== 分页查询 ====================

    @Override
    public Page<VoteResponseDTO> queryPageDTO(VoteQueryDTO queryDTO) {
        if (queryDTO == null) {
            queryDTO = new VoteQueryDTO();
        }

        Integer pageNum = queryDTO.getPageNum();
        Integer pageSize = queryDTO.getPageSize();

        // 分页参数校验
        if (pageNum == null || pageNum < 1) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "页码必须大于0");
        }
        if (pageSize == null || pageSize < 1) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "每页条数必须大于0");
        }
        if (pageSize > 100) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "每页条数不能超过100");
        }

        Page<Vote> page = new Page<>(pageNum, pageSize);

        LambdaQueryWrapper<Vote> wrapper = new LambdaQueryWrapper<>();

        // 标题模糊查询
        if (StringUtils.hasText(queryDTO.getTitle())) {
            wrapper.like(Vote::getTitle, queryDTO.getTitle());
        }

        // 状态查询（只在明确传入时查询）
        if (queryDTO.getStatus() != null && queryDTO.getStatus() >= 0) {
            wrapper.eq(Vote::getStatus, queryDTO.getStatus());
        }

        // 创建者查询（只在明确传入时查询）
        if (queryDTO.getCreatorId() != null && queryDTO.getCreatorId() > 0) {
            wrapper.eq(Vote::getCreatorId, queryDTO.getCreatorId());
        }

        // 时间范围查询
        if (queryDTO.getStartTimeBegin() != null) {
            wrapper.ge(Vote::getStartTime, queryDTO.getStartTimeBegin());
        }
        if (queryDTO.getStartTimeEnd() != null) {
            wrapper.le(Vote::getStartTime, queryDTO.getStartTimeEnd());
        }
        if (queryDTO.getEndTimeBegin() != null) {
            wrapper.ge(Vote::getEndTime, queryDTO.getEndTimeBegin());
        }
        if (queryDTO.getEndTimeEnd() != null) {
            wrapper.le(Vote::getEndTime, queryDTO.getEndTimeEnd());
        }

        // 排序逻辑
        String orderBy = queryDTO.getOrderBy();
        String orderDirection = queryDTO.getOrderDirection();

        if ("endTime".equals(orderBy)) {
            // 按截止时间排序
            if ("asc".equals(orderDirection)) {
                wrapper.orderByAsc(Vote::getEndTime);
            } else {
                wrapper.orderByDesc(Vote::getEndTime);
            }
        } else {
            // 默认按创建时间排序
            if ("asc".equals(orderDirection)) {
                wrapper.orderByAsc(Vote::getCreateTime);
            } else {
                wrapper.orderByDesc(Vote::getCreateTime);
            }
        }

        Page<Vote> votePage = this.page(page, wrapper);

        // 转换为DTO
        Page<VoteResponseDTO> dtoPage = new Page<>(votePage.getCurrent(), votePage.getSize(), votePage.getTotal());
        List<VoteResponseDTO> dtoList = votePage.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        dtoPage.setRecords(dtoList);

        return dtoPage;
    }

    @Override
    public Page<VoteResponseDTO> queryPageSimpleDTO(Integer pageNum, Integer pageSize) {
        // 分页参数校验
        if (pageNum == null || pageNum < 1) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "页码必须大于0");
        }
        if (pageSize == null || pageSize < 1) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "每页条数必须大于0");
        }
        if (pageSize > 100) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "每页条数不能超过100");
        }

        Page<Vote> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Vote> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Vote::getCreateTime);

        Page<Vote> votePage = this.page(page, wrapper);

        // 转换为DTO
        Page<VoteResponseDTO> dtoPage = new Page<>(votePage.getCurrent(), votePage.getSize(), votePage.getTotal());
        List<VoteResponseDTO> dtoList = votePage.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        dtoPage.setRecords(dtoList);

        return dtoPage;
    }

    // ==================== 按主键查询 ====================

    @Override
    public Vote getVoteById(Long id) {
        if (id == null) {
            return null;
        }
        return this.getById(id);
    }

    @Override
    public VoteResponseDTO getVoteDTOById(Long id) {
        Vote vote = getVoteById(id);
        return vote != null ? convertToDTO(vote) : null;
    }

    // ==================== 新增投票 ====================

    @Override
    public Vote addVote(VoteSaveDTO saveDTO) {
        if (saveDTO == null) {
            log.warn("新增投票失败：DTO对象为空");
            return null;
        }

        Vote vote = new Vote();
        BeanUtils.copyProperties(saveDTO, vote);

        // 设置创建时间和更新时间
        LocalDateTime now = LocalDateTime.now();
        vote.setCreateTime(now);
        vote.setUpdateTime(now);

        // 默认状态为进行中
        if (vote.getStatus() == null) {
            vote.setStatus(1);
        }

        boolean result = this.save(vote);
        if (result) {
            log.info("新增投票成功: {} (ID: {})", vote.getTitle(), vote.getId());

            // 保存选项
            if (saveDTO.getOptions() != null && !saveDTO.getOptions().isEmpty()) {
                List<VoteOption> optionList = new ArrayList<>();
                for (int i = 0; i < saveDTO.getOptions().size(); i++) {
                    VoteOption option = new VoteOption();
                    option.setVoteId(vote.getId());
                    option.setOptionText(saveDTO.getOptions().get(i));
                    option.setSortOrder(i + 1);
                    option.setCreateTime(now);
                    optionList.add(option);
                }
                voteOptionService.addOptionsBatch(optionList);
            }

            return vote;
        } else {
            log.warn("新增投票失败: {}", vote.getTitle());
            return null;
        }
    }

    // ==================== 修改投票 ====================

    @Override
    public Vote updateVote(VoteSaveDTO saveDTO) {
        if (saveDTO == null || saveDTO.getId() == null) {
            log.warn("修改投票失败：DTO对象为空或ID为空");
            return null;
        }

        // 检查投票是否存在
        if (!exists(saveDTO.getId())) {
            log.warn("修改投票失败：投票不存在 - ID: {}", saveDTO.getId());
            return null;
        }

        Vote vote = new Vote();
        BeanUtils.copyProperties(saveDTO, vote);

        // 设置更新时间
        vote.setUpdateTime(LocalDateTime.now());

        boolean result = this.updateById(vote);
        if (result) {
            log.info("修改投票成功: ID: {}", vote.getId());

            // 更新选项（先删后增）
            if (saveDTO.getOptions() != null && !saveDTO.getOptions().isEmpty()) {
                voteOptionService.deleteByVoteId(vote.getId());
                LocalDateTime now = LocalDateTime.now();
                List<VoteOption> optionList = new ArrayList<>();
                for (int i = 0; i < saveDTO.getOptions().size(); i++) {
                    VoteOption option = new VoteOption();
                    option.setVoteId(vote.getId());
                    option.setOptionText(saveDTO.getOptions().get(i));
                    option.setSortOrder(i + 1);
                    option.setCreateTime(now);
                    optionList.add(option);
                }
                voteOptionService.addOptionsBatch(optionList);
            }

            return vote;
        } else {
            log.warn("修改投票失败: ID: {}", vote.getId());
            return null;
        }
    }

    // ==================== 删除投票 ====================

    @Override
    public boolean deleteVoteById(Long id) {
        if (id == null) {
            log.warn("删除投票失败：投票ID为空");
            return false;
        }

        // 检查投票是否存在
        if (!exists(id)) {
            log.warn("删除投票失败：投票不存在 - ID: {}", id);
            return false;
        }

        boolean result = this.removeById(id);
        if (result) {
            log.info("删除投票成功: ID: {}", id);
        } else {
            log.warn("删除投票失败: ID: {}", id);
        }
        return result;
    }

    @Override
    public boolean deleteVoteBatch(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            log.warn("批量删除投票失败：ID列表为空");
            return false;
        }

        boolean result = this.removeByIds(ids);
        if (result) {
            log.info("批量删除投票成功: {} 条", ids.size());
        } else {
            log.warn("批量删除投票失败: {}", ids);
        }
        return result;
    }

    // ==================== 其他方法 ====================

    @Override
    public List<Vote> getAllVotes() {
        LambdaQueryWrapper<Vote> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Vote::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    public boolean exists(Long id) {
        if (id == null) {
            return false;
        }
        return this.getById(id) != null;
    }

    @Override
    public boolean endVote(Long id) {
        if (id == null) {
            log.warn("结束投票失败：投票ID为空");
            return false;
        }

        if (!exists(id)) {
            log.warn("结束投票失败：投票不存在 - ID: {}", id);
            return false;
        }

        Vote vote = new Vote();
        vote.setId(id);
        vote.setStatus(2); // 已结束
        vote.setUpdateTime(LocalDateTime.now());

        boolean result = this.updateById(vote);
        if (result) {
            log.info("结束投票成功: ID: {}", id);
        }
        return result;
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 转换实体为DTO
     */
    private VoteResponseDTO convertToDTO(Vote vote) {
        if (vote == null) {
            return null;
        }

        VoteResponseDTO dto = new VoteResponseDTO();
        BeanUtils.copyProperties(vote, dto);

        // 设置状态文本
        dto.setStatusText(getStatusText(vote.getStatus()));

        // 设置计算字段
        LocalDateTime now = LocalDateTime.now();
        dto.setIsStarted(vote.getStartTime() != null && now.isAfter(vote.getStartTime()));
        dto.setIsEnded(vote.getStatus() == 2 || (vote.getEndTime() != null && now.isAfter(vote.getEndTime())));
        dto.setIsOngoing(vote.getStatus() == 1 && !dto.getIsEnded());

        // 设置简短标题
        dto.setShortTitle(getShortTitle(vote.getTitle()));

        // 查询投票选项
        List<VoteOption> options = voteOptionService.getOptionsByVoteId(vote.getId());
        List<java.util.Map<String, Object>> optionList = new ArrayList<>();
        for (VoteOption option : options) {
            java.util.Map<String, Object> map = new java.util.HashMap<>();
            map.put("id", option.getId());
            map.put("optionText", option.getOptionText());
            map.put("sortOrder", option.getSortOrder());
            optionList.add(map);
        }
        dto.setOptions(optionList);

        // 查询总投票人数
        long totalVoters = voteRecordService.countByOptionId(vote.getId()).values().stream()
                .mapToLong(Long::longValue)
                .sum();
        dto.setTotalVoters(totalVoters);

        return dto;
    }

    /**
     * 获取状态文本
     */
    private String getStatusText(Integer status) {
        if (status == null) {
            return "未知";
        }
        switch (status) {
            case 0:
                return "未开始";
            case 1:
                return "进行中";
            case 2:
                return "已结束";
            default:
                return "未知";
        }
    }

    /**
     * 获取简短标题
     */
    private String getShortTitle(String title) {
        if (!StringUtils.hasText(title)) {
            return "";
        }
        return title.length() > 20 ? title.substring(0, 20) + "..." : title;
    }
}
