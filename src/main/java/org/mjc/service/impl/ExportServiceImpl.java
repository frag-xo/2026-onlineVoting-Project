package org.mjc.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.mjc.entity.Vote;
import org.mjc.service.ExportService;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 导出服务实现类
 *
 * @author Online_Voting
 * @since 2026-07-22
 */
@Slf4j
@Service
public class ExportServiceImpl implements ExportService {

    @Override
    public byte[] exportVoteResultToExcel(Vote vote, List<Map<String, Object>> options, long totalCount) {
        try (Workbook workbook = new XSSFWorkbook()) {
            // 创建工作表
            Sheet sheet = workbook.createSheet("投票结果");

            // 创建标题样式
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 12);
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            // 创建数据样式
            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setAlignment(HorizontalAlignment.CENTER);

            // 第一行：投票标题
            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("投票标题：" + vote.getTitle());
            titleCell.setCellStyle(headerStyle);

            // 第二行：投票描述
            Row descRow = sheet.createRow(1);
            Cell descCell = descRow.createCell(0);
            descCell.setCellValue("投票描述：" + vote.getDescription());

            // 第三行：总票数
            Row totalRow = sheet.createRow(2);
            Cell totalCell = totalRow.createCell(0);
            totalCell.setCellValue("总投票人数：" + totalCount);

            // 第五行：表头
            Row headerRow = sheet.createRow(4);
            Cell headerCell1 = headerRow.createCell(0);
            headerCell1.setCellValue("选项");
            headerCell1.setCellStyle(headerStyle);

            Cell headerCell2 = headerRow.createCell(1);
            headerCell2.setCellValue("票数");
            headerCell2.setCellStyle(headerStyle);

            Cell headerCell3 = headerRow.createCell(2);
            headerCell3.setCellValue("占比");
            headerCell3.setCellStyle(headerStyle);

            // 填充数据
            int rowNum = 5;
            for (Map<String, Object> option : options) {
                Row row = sheet.createRow(rowNum++);

                Cell cell1 = row.createCell(0);
                cell1.setCellValue((String) option.get("optionText"));
                cell1.setCellStyle(dataStyle);

                long count = (Long) option.getOrDefault("count", 0L);
                Cell cell2 = row.createCell(1);
                cell2.setCellValue(count);
                cell2.setCellStyle(dataStyle);

                String percentage = totalCount > 0 ?
                        String.format("%.1f%%", (double) count / totalCount * 100) : "0%";
                Cell cell3 = row.createCell(2);
                cell3.setCellValue(percentage);
                cell3.setCellStyle(dataStyle);
            }

            // 设置列宽
            sheet.setColumnWidth(0, 5000);
            sheet.setColumnWidth(1, 3000);
            sheet.setColumnWidth(2, 3000);

            // 写入字节数组
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            workbook.write(baos);
            return baos.toByteArray();

        } catch (IOException e) {
            log.error("导出Excel失败", e);
            return new byte[0];
        }
    }
}
