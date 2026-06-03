package com.dataasset.security.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dataasset.security.common.result.Result;
import com.dataasset.security.common.annotation.AuditLog;
import com.dataasset.security.common.enums.ObjectTypeEnum;
import com.dataasset.security.common.enums.OperationTypeEnum;
import com.dataasset.security.entity.DataAsset;
import com.dataasset.security.entity.DataField;
import com.dataasset.security.mapper.DataAssetMapper;
import com.dataasset.security.mapper.DataFieldMapper;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 报告控制器
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Slf4j
@RestController
@RequestMapping("/report")
@RequiredArgsConstructor
@Tag(name = "报告管理", description = "报告生成和导出")
public class ReportController {

    private final DataAssetMapper dataAssetMapper;
    private final DataFieldMapper dataFieldMapper;

    /** 中文字体（iText内置的STSong字体，无需外部字体文件） */
    private PdfFont getChineseFont() throws IOException {
        return PdfFontFactory.createFont("STSongStd-Light", "UniGB-UCS2-H");
    }

    /**
     * 生成资产清单报告
     */
    @GetMapping("/asset-list/generate")
    @Operation(summary = "生成资产清单报告", description = "生成资产清单报告")
    public Result<Map<String, Object>> generateAssetListReport(
            @RequestParam(required = false) String assetType,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false, defaultValue = "false") Boolean includeFieldDetails,
            @RequestParam(required = false, defaultValue = "pdf") String outputFormat,
            @RequestParam(required = false) String reportName,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "1000") Integer pageSize) {

        LambdaQueryWrapper<DataAsset> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DataAsset::getDeleted, 0);
        if (assetType != null && !assetType.isEmpty()) {
            wrapper.eq(DataAsset::getAssetType, assetType);
        }
        if (departmentId != null) {
            wrapper.eq(DataAsset::getDepartmentId, departmentId);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq(DataAsset::getStatus, status);
        }

        List<DataAsset> assets = dataAssetMapper.selectList(wrapper);
        List<Map<String, Object>> reportData = new ArrayList<>();

        for (DataAsset asset : assets) {
            Map<String, Object> item = new HashMap<>();
            item.put("assetId", asset.getAssetId());
            item.put("assetName", asset.getAssetName());
            item.put("assetCode", asset.getAssetCode());
            item.put("assetType", asset.getAssetType());
            item.put("systemName", asset.getSystemName());
            item.put("status", asset.getStatus());
            item.put("departmentId", asset.getDepartmentId());
            item.put("ownerId", asset.getOwnerId());
            item.put("classificationId", asset.getClassificationId());
            item.put("gradingId", asset.getGradingId());

            if (Boolean.TRUE.equals(includeFieldDetails)) {
                List<DataField> fields = dataFieldMapper.selectList(new LambdaQueryWrapper<DataField>()
                    .eq(DataField::getAssetId, asset.getAssetId())
                    .eq(DataField::getDeleted, 0));
                item.put("fieldCount", fields.size());
            }

            reportData.add(item);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("list", reportData);
        result.put("total", reportData.size());
        return Result.success(result);
    }

    /**
     * 导出资产清单报告（PDF格式）
     */
    @GetMapping("/asset-list/export")
    @Operation(summary = "导出资产清单报告", description = "导出资产清单报告为PDF")
    @AuditLog(operationType = OperationTypeEnum.EXPORT, objectType = ObjectTypeEnum.REPORT, description = "导出资产清单报告")
    public void exportAssetListReport(
            @RequestParam(required = false) String assetType,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false, defaultValue = "pdf") String format,
            HttpServletResponse response) throws IOException {

        LambdaQueryWrapper<DataAsset> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DataAsset::getDeleted, 0);
        if (assetType != null && !assetType.isEmpty()) {
            wrapper.eq(DataAsset::getAssetType, assetType);
        }
        if (departmentId != null) {
            wrapper.eq(DataAsset::getDepartmentId, departmentId);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq(DataAsset::getStatus, status);
        }

        List<DataAsset> assets = dataAssetMapper.selectList(wrapper);

        String fileName = URLEncoder.encode("数据资产清单报告", StandardCharsets.UTF_8);
        response.setContentType("application/pdf");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".pdf");

        PdfFont font = getChineseFont();
        PdfWriter writer = new PdfWriter(response.getOutputStream());
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // 标题
        document.add(new Paragraph("数据资产清单报告")
                .setFont(font).setFontSize(20).setBold()
                .setTextAlignment(TextAlignment.CENTER));
        document.add(new Paragraph("生成时间：" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .setFont(font).setFontSize(10)
                .setTextAlignment(TextAlignment.CENTER));
        document.add(new Paragraph("共 " + assets.size() + " 条资产记录")
                .setFont(font).setFontSize(10)
                .setTextAlignment(TextAlignment.CENTER));
        document.add(new Paragraph("\n").setFont(font));

        // 统计摘要
        Map<String, Long> typeCount = assets.stream()
                .collect(Collectors.groupingBy(a -> a.getAssetType() != null ? a.getAssetType() : "未知", Collectors.counting()));
        Map<String, Long> statusCount = assets.stream()
                .collect(Collectors.groupingBy(a -> a.getStatus() != null ? a.getStatus() : "未知", Collectors.counting()));

        document.add(new Paragraph("统计摘要").setFont(font).setFontSize(14).setBold());
        document.add(new Paragraph("按类型：" + typeCount.entrySet().stream()
                .map(e -> e.getKey() + "(" + e.getValue() + ")")
                .collect(Collectors.joining("、")))
                .setFont(font).setFontSize(10));
        document.add(new Paragraph("按状态：" + statusCount.entrySet().stream()
                .map(e -> e.getKey() + "(" + e.getValue() + ")")
                .collect(Collectors.joining("、")))
                .setFont(font).setFontSize(10));
        document.add(new Paragraph("\n").setFont(font));

        // 资产明细表格
        document.add(new Paragraph("资产明细").setFont(font).setFontSize(14).setBold());

        float[] colWidths = {80, 120, 80, 60, 80, 60, 60};
        Table table = new Table(UnitValue.createPointArray(colWidths));
        table.useAllAvailableWidth();

        // 表头
        String[] headers = {"资产名称", "资产编码", "资产类型", "所属系统", "数据源", "状态", "敏感度"};
        for (String h : headers) {
            table.addHeaderCell(new Cell().add(new Paragraph(h).setFont(font).setFontSize(8).setBold())
                    .setBackgroundColor(new com.itextpdf.kernel.colors.DeviceRgb(66, 133, 244))
                    .setFontColor(com.itextpdf.kernel.colors.ColorConstants.WHITE)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setPadding(4));
        }

        // 数据行
        for (DataAsset asset : assets) {
            String dataSource = "";
            if (asset.getDatabaseType() != null) {
                dataSource = asset.getDatabaseType() + ":" + asset.getDatabaseHost();
            }
            String sensScore = asset.getSensitivityScore() != null ? asset.getSensitivityScore().toString() : "-";

            String[] values = {
                    nvl(asset.getAssetName()),
                    nvl(asset.getAssetCode()),
                    nvl(asset.getAssetType()),
                    nvl(asset.getSystemName()),
                    dataSource,
                    nvl(asset.getStatus()),
                    sensScore
            };
            for (String v : values) {
                table.addCell(new Cell().add(new Paragraph(v).setFont(font).setFontSize(7))
                        .setTextAlignment(TextAlignment.CENTER)
                        .setPadding(3));
            }
        }

        document.add(table);
        document.close();
    }

    /**
     * 生成分类分级统计报告
     */
    @GetMapping("/classification-stats/generate")
    @Operation(summary = "生成分类分级统计报告", description = "生成分类分级统计报告")
    public Result<Map<String, Object>> generateClassificationStatsReport(
            @RequestParam(required = false, defaultValue = "comprehensive") String dimension,
            @RequestParam(required = false, defaultValue = "pdf") String outputFormat,
            @RequestParam(required = false) String reportName) {

        List<DataAsset> assets = dataAssetMapper.selectList(new LambdaQueryWrapper<DataAsset>()
            .eq(DataAsset::getDeleted, 0));

        Map<String, Object> reportData = new HashMap<>();

        // 按分类统计
        List<Map<String, Object>> byClassification = new ArrayList<>();
        Map<Long, Integer> classificationCount = new HashMap<>();
        for (DataAsset asset : assets) {
            if (asset.getClassificationId() != null) {
                classificationCount.merge(asset.getClassificationId(), 1, Integer::sum);
            }
        }
        List<Map<String, Object>> classificationList = new ArrayList<>();
        classificationCount.forEach((id, count) -> {
            Map<String, Object> item = new HashMap<>();
            item.put("name", "分类" + id);
            item.put("value", count);
            classificationList.add(item);
        });
        byClassification.addAll(classificationList);
        reportData.put("byClassification", byClassification);
        reportData.put("classificationDistribution", classificationList);

        // 按分级统计
        List<Map<String, Object>> byGrading = new ArrayList<>();
        Map<Long, Integer> gradingCount = new HashMap<>();
        for (DataAsset asset : assets) {
            if (asset.getGradingId() != null) {
                gradingCount.merge(asset.getGradingId(), 1, Integer::sum);
            }
        }
        List<Map<String, Object>> gradingList = new ArrayList<>();
        gradingCount.forEach((id, count) -> {
            Map<String, Object> item = new HashMap<>();
            item.put("name", "分级" + id);
            item.put("value", count);
            gradingList.add(item);
        });
        byGrading.addAll(gradingList);
        reportData.put("byGrading", byGrading);
        reportData.put("gradingDistribution", gradingList);

        // 按部门统计
        List<Map<String, Object>> byDepartment = new ArrayList<>();
        Map<Long, Integer> departmentCount = new HashMap<>();
        for (DataAsset asset : assets) {
            if (asset.getDepartmentId() != null) {
                departmentCount.merge(asset.getDepartmentId(), 1, Integer::sum);
            }
        }
        List<Map<String, Object>> departmentList = new ArrayList<>();
        departmentCount.forEach((id, count) -> {
            Map<String, Object> item = new HashMap<>();
            item.put("name", "部门" + id);
            item.put("value", count);
            departmentList.add(item);
        });
        byDepartment.addAll(departmentList);
        reportData.put("byDepartment", byDepartment);
        reportData.put("departmentDistribution", departmentList);

        // 构建departmentStats结构（前端ECharts需要）
        Map<String, Object> departmentStats = new HashMap<>();
        List<String> deptNames = new ArrayList<>();
        for (Map<String, Object> dept : departmentList) {
            deptNames.add((String) dept.get("name"));
        }
        departmentStats.put("categories", new ArrayList<>());
        departmentStats.put("departments", deptNames);
        departmentStats.put("series", new ArrayList<>());
        reportData.put("departmentStats", departmentStats);

        reportData.put("totalAssets", assets.size());

        return Result.success(reportData);
    }

    /**
     * 导出分类分级统计报告（PDF格式）
     */
    @GetMapping("/classification-stats/export")
    @Operation(summary = "导出分类分级统计报告", description = "导出分类分级统计报告为PDF")
    @AuditLog(operationType = OperationTypeEnum.EXPORT, objectType = ObjectTypeEnum.REPORT, description = "导出分类分级统计报告")
    public void exportClassificationStatsReport(HttpServletResponse response) throws IOException {

        List<DataAsset> assets = dataAssetMapper.selectList(new LambdaQueryWrapper<DataAsset>()
                .eq(DataAsset::getDeleted, 0));

        // 按分类统计
        Map<Long, Integer> classificationCount = new LinkedHashMap<>();
        for (DataAsset asset : assets) {
            if (asset.getClassificationId() != null) {
                classificationCount.merge(asset.getClassificationId(), 1, Integer::sum);
            }
        }

        // 按分级统计
        Map<Long, Integer> gradingCount = new LinkedHashMap<>();
        for (DataAsset asset : assets) {
            if (asset.getGradingId() != null) {
                gradingCount.merge(asset.getGradingId(), 1, Integer::sum);
            }
        }

        // 按部门统计
        Map<Long, Integer> departmentCount = new LinkedHashMap<>();
        for (DataAsset asset : assets) {
            if (asset.getDepartmentId() != null) {
                departmentCount.merge(asset.getDepartmentId(), 1, Integer::sum);
            }
        }

        // 按状态统计
        Map<String, Integer> statusCount = new LinkedHashMap<>();
        for (DataAsset asset : assets) {
            String s = asset.getStatus() != null ? asset.getStatus() : "未知";
            statusCount.merge(s, 1, Integer::sum);
        }

        // 按类型统计
        Map<String, Integer> typeCount = new LinkedHashMap<>();
        for (DataAsset asset : assets) {
            String t = asset.getAssetType() != null ? asset.getAssetType() : "未知";
            typeCount.merge(t, 1, Integer::sum);
        }

        String fileName = URLEncoder.encode("分类分级统计报告", StandardCharsets.UTF_8);
        response.setContentType("application/pdf");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".pdf");

        PdfFont font = getChineseFont();
        PdfWriter writer = new PdfWriter(response.getOutputStream());
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // 标题
        document.add(new Paragraph("数据分类分级统计报告")
                .setFont(font).setFontSize(20).setBold()
                .setTextAlignment(TextAlignment.CENTER));
        document.add(new Paragraph("生成时间：" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .setFont(font).setFontSize(10)
                .setTextAlignment(TextAlignment.CENTER));
        document.add(new Paragraph("共 " + assets.size() + " 条资产记录")
                .setFont(font).setFontSize(10)
                .setTextAlignment(TextAlignment.CENTER));
        document.add(new Paragraph("\n").setFont(font));

        // 总览
        document.add(new Paragraph("一、总览").setFont(font).setFontSize(14).setBold());
        document.add(new Paragraph("  资产总数：" + assets.size()).setFont(font).setFontSize(10));
        document.add(new Paragraph("  分类数量：" + classificationCount.size()).setFont(font).setFontSize(10));
        document.add(new Paragraph("  分级数量：" + gradingCount.size()).setFont(font).setFontSize(10));
        document.add(new Paragraph("  部门数量：" + departmentCount.size()).setFont(font).setFontSize(10));
        document.add(new Paragraph("\n").setFont(font));

        // 分类统计表
        document.add(new Paragraph("二、分类统计").setFont(font).setFontSize(14).setBold());
        Table classTable = new Table(UnitValue.createPointArray(new float[]{200, 100, 100}));
        classTable.useAllAvailableWidth();
        addStatTableHeader(classTable, font, new String[]{"分类名称", "资产数量", "占比"});
        int total = assets.size();
        for (Map.Entry<Long, Integer> entry : classificationCount.entrySet()) {
            String pct = total > 0 ? String.format("%.1f%%", entry.getValue() * 100.0 / total) : "0%";
            addStatTableRow(classTable, font, new String[]{"分类" + entry.getKey(), entry.getValue().toString(), pct});
        }
        document.add(classTable);
        document.add(new Paragraph("\n").setFont(font));

        // 分级统计表
        document.add(new Paragraph("三、分级统计").setFont(font).setFontSize(14).setBold());
        Table gradeTable = new Table(UnitValue.createPointArray(new float[]{200, 100, 100}));
        gradeTable.useAllAvailableWidth();
        addStatTableHeader(gradeTable, font, new String[]{"分级名称", "资产数量", "占比"});
        for (Map.Entry<Long, Integer> entry : gradingCount.entrySet()) {
            String pct = total > 0 ? String.format("%.1f%%", entry.getValue() * 100.0 / total) : "0%";
            addStatTableRow(gradeTable, font, new String[]{"分级" + entry.getKey(), entry.getValue().toString(), pct});
        }
        document.add(gradeTable);
        document.add(new Paragraph("\n").setFont(font));

        // 部门统计表
        document.add(new Paragraph("四、部门统计").setFont(font).setFontSize(14).setBold());
        Table deptTable = new Table(UnitValue.createPointArray(new float[]{200, 100, 100}));
        deptTable.useAllAvailableWidth();
        addStatTableHeader(deptTable, font, new String[]{"部门名称", "资产数量", "占比"});
        for (Map.Entry<Long, Integer> entry : departmentCount.entrySet()) {
            String pct = total > 0 ? String.format("%.1f%%", entry.getValue() * 100.0 / total) : "0%";
            addStatTableRow(deptTable, font, new String[]{"部门" + entry.getKey(), entry.getValue().toString(), pct});
        }
        document.add(deptTable);
        document.add(new Paragraph("\n").setFont(font));

        // 状态统计表
        document.add(new Paragraph("五、状态统计").setFont(font).setFontSize(14).setBold());
        Table statusTable = new Table(UnitValue.createPointArray(new float[]{200, 100, 100}));
        statusTable.useAllAvailableWidth();
        addStatTableHeader(statusTable, font, new String[]{"状态", "资产数量", "占比"});
        for (Map.Entry<String, Integer> entry : statusCount.entrySet()) {
            String pct = total > 0 ? String.format("%.1f%%", entry.getValue() * 100.0 / total) : "0%";
            addStatTableRow(statusTable, font, new String[]{entry.getKey(), entry.getValue().toString(), pct});
        }
        document.add(statusTable);
        document.add(new Paragraph("\n").setFont(font));

        // 类型统计表
        document.add(new Paragraph("六、类型统计").setFont(font).setFontSize(14).setBold());
        Table typeTable = new Table(UnitValue.createPointArray(new float[]{200, 100, 100}));
        typeTable.useAllAvailableWidth();
        addStatTableHeader(typeTable, font, new String[]{"资产类型", "资产数量", "占比"});
        for (Map.Entry<String, Integer> entry : typeCount.entrySet()) {
            String pct = total > 0 ? String.format("%.1f%%", entry.getValue() * 100.0 / total) : "0%";
            addStatTableRow(typeTable, font, new String[]{entry.getKey(), entry.getValue().toString(), pct});
        }
        document.add(typeTable);

        document.close();
    }

    /** 添加统计表表头 */
    private void addStatTableHeader(Table table, PdfFont font, String[] headers) {
        for (String h : headers) {
            table.addHeaderCell(new Cell().add(new Paragraph(h).setFont(font).setFontSize(9).setBold())
                    .setBackgroundColor(new com.itextpdf.kernel.colors.DeviceRgb(66, 133, 244))
                    .setFontColor(com.itextpdf.kernel.colors.ColorConstants.WHITE)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setPadding(5));
        }
    }

    /** 添加统计表数据行 */
    private void addStatTableRow(Table table, PdfFont font, String[] values) {
        for (String v : values) {
            table.addCell(new Cell().add(new Paragraph(v).setFont(font).setFontSize(9))
                    .setTextAlignment(TextAlignment.CENTER)
                    .setPadding(4));
        }
    }

    /** null转空字符串 */
    private String nvl(String val) {
        return val != null ? val : "";
    }

    /**
     * 获取报告历史
     */
    @GetMapping("/history")
    @Operation(summary = "报告历史", description = "获取报告生成历史")
    public Result<List<Map<String, Object>>> getReportHistory(@RequestParam(required = false) Map<String, Object> params) {
        return Result.success(new ArrayList<>());
    }

    /**
     * 删除报告
     */
    @DeleteMapping("/{reportId}")
    @Operation(summary = "删除报告", description = "删除指定报告")
    @AuditLog(operationType = OperationTypeEnum.DELETE, objectType = ObjectTypeEnum.REPORT, description = "删除报告")
    public Result<Void> deleteReport(@PathVariable Long reportId) {
        return Result.success();
    }
}
