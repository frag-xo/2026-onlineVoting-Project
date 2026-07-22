package org.mjc.service;

import org.mjc.entity.Vote;
import org.mjc.entity.VoteOption;
import org.mjc.entity.VoteRecord;

import java.util.List;
import java.util.Map;

/**
 * 导出服务接口
 *
 * @author Online_Voting
 * @since 2026-07-22
 */
public interface ExportService {

    /**
     * 导出投票结果为Excel
     *
     * @param vote 投票信息
     * @param options 选项列表（含票数）
     * @param totalCount 总票数
     * @return Excel文件的字节数组
     */
    byte[] exportVoteResultToExcel(Vote vote, List<Map<String, Object>> options, long totalCount);
}
