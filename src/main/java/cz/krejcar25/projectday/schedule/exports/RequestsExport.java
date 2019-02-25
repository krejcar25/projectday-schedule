package cz.krejcar25.projectday.schedule.exports;

import cz.krejcar25.projectday.schedule.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.jetbrains.annotations.NotNull;

import java.awt.Color;

public class RequestsExport extends ExcelExport {

    public RequestsExport(Project project) {
        super(project);
    }

    @Override
    protected void writeTable() {
        for (Group group : project.getGroups()) {
            Sheet sheet = wb.createSheet(group.getName());
            addStandHeaders(sheet);
            addNames(sheet, group);
            sheet.autoSizeColumn(0);
        }
    }

    private void addStandHeaders(Sheet sheet) {
        Row header = sheet.createRow(0);
        CellStyle block = wb.createCellStyle();
        block.setFillForegroundColor(IndexedColors.BLACK.index);
        block.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        block.setBorderTop(BorderStyle.DOUBLE);
        block.setBorderRight(BorderStyle.MEDIUM);
        block.setBorderBottom(BorderStyle.MEDIUM);
        block.setBorderLeft(BorderStyle.DOUBLE);

        header.createCell(0).setCellStyle(block);

        for (int i = 0; i < project.getStands().getSize(); i++) {
            Stand stand = project.getStands().getElementAt(i);
            XSSFFont font = wb.createFont();
            font.setBold(true);
            font.setFontHeightInPoints((short) 14);
            Color fore = MainWindow.blackOrWhite(stand.getColor());
            byte[] fontColorBytes = new byte[]{(byte) fore.getRed(), (byte) fore.getGreen(), (byte) fore.getBlue()};
            font.setColor(new XSSFColor(fontColorBytes, new DefaultIndexedColorMap()));

            XSSFCellStyle style = wb.createCellStyle();
            style.setFont(font);
            style.setBorderTop(BorderStyle.DOUBLE);
            style.setBorderRight(i + 1 == project.getStands().getSize() ? BorderStyle.DOUBLE : BorderStyle.THIN);
            style.setBorderBottom(BorderStyle.MEDIUM);
            byte[] colorBytes = new byte[]{(byte) stand.getColor().getRed(), (byte) stand.getColor().getGreen(), (byte) stand.getColor().getBlue()};
            style.setFillForegroundColor(new XSSFColor(colorBytes, new DefaultIndexedColorMap()));
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            Cell cell = header.createCell(i + 1);
            cell.setCellStyle(style);
            cell.setCellValue(stand.getName());
        }
    }

    @SuppressWarnings("Duplicates")
    private void addNames(Sheet sheet, @NotNull Group group) {
        for (int i = 0; i < group.getPeople().size(); i++) {
            Row row = sheet.createRow(i + 1);

            CellStyle nameStyle = wb.createCellStyle();
            nameStyle.setBorderRight(BorderStyle.MEDIUM);
            nameStyle.setBorderBottom(i + 1 == group.getPeople().size() ? BorderStyle.DOUBLE : BorderStyle.THIN);
            nameStyle.setBorderLeft(BorderStyle.DOUBLE);
            nameStyle.setFillForegroundColor(i % 2 == 0 ? IndexedColors.WHITE.index : IndexedColors.GREY_25_PERCENT.index);
            nameStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            Cell nameCell = row.createCell(0);
            nameCell.setCellStyle(nameStyle);
            nameCell.setCellValue(group.getPeople().get(i).getName());

            for (int j = 1; j <= project.getStands().getSize(); j++) {
                CellStyle style = wb.createCellStyle();
                style.setBorderRight(j == project.getStands().getSize() ? BorderStyle.DOUBLE : BorderStyle.DASHED);
                style.setBorderBottom(i + 1 == group.getPeople().size() ? BorderStyle.DOUBLE : BorderStyle.THIN);
                style.setFillForegroundColor(i % 2 == 0 ? IndexedColors.WHITE.index : IndexedColors.GREY_25_PERCENT.index);
                style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                row.createCell(j).setCellStyle(style);
            }
        }
    }
}
