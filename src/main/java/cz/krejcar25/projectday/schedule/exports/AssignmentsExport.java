package cz.krejcar25.projectday.schedule.exports;

import cz.krejcar25.projectday.schedule.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.jetbrains.annotations.NotNull;

import java.awt.Color;

public class AssignmentsExport extends ExcelExport {

    public AssignmentsExport(Project project) {
        super(project);
    }

    @Override
    protected void writeTable() {
        for (Group group : project.getGroups()) {
            Sheet sheet = wb.createSheet(group.getName());
            addBlockHeaders(sheet);
            addNames(sheet, group);
            sheet.autoSizeColumn(0);
        }
    }

    private void addBlockHeaders(Sheet sheet) {
        Row header = sheet.createRow(0);
        CellStyle block = wb.createCellStyle();
        block.setFillForegroundColor(IndexedColors.BLACK.index);
        block.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        block.setBorderTop(BorderStyle.DOUBLE);
        block.setBorderRight(BorderStyle.MEDIUM);
        block.setBorderBottom(BorderStyle.MEDIUM);
        block.setBorderLeft(BorderStyle.DOUBLE);

        header.createCell(0).setCellStyle(block);

        for (int i = 0; i < project.getBlockCount(); i++) {
            XSSFFont font = wb.createFont();
            font.setBold(true);
            font.setFontHeightInPoints((short) 14);

            XSSFCellStyle style = wb.createCellStyle();
            style.setFont(font);
            style.setBorderTop(BorderStyle.DOUBLE);
            style.setBorderRight(i + 1 == project.getStands().getSize() ? BorderStyle.DOUBLE : BorderStyle.THIN);
            style.setBorderBottom(BorderStyle.MEDIUM);

            Cell cell = header.createCell(i + 1);
            cell.setCellStyle(style);
            cell.setCellValue(String.format("%d.", i + 1));
        }
    }

    @SuppressWarnings("Duplicates")
    private void addNames(Sheet sheet, @NotNull Group group) {
        for (int i = 0; i < group.getPeople().size(); i++) {
            Person person = group.getPeople().get(i);
            Row row = sheet.createRow(i + 1);

            CellStyle nameStyle = wb.createCellStyle();
            nameStyle.setBorderRight(BorderStyle.MEDIUM);
            nameStyle.setBorderBottom(i + 1 == group.getPeople().size() ? BorderStyle.DOUBLE : BorderStyle.THIN);
            nameStyle.setBorderLeft(BorderStyle.DOUBLE);

            Cell nameCell = row.createCell(0);
            nameCell.setCellStyle(nameStyle);
            nameCell.setCellValue(person.getName());

            for (int j = 1; j <= project.getBlockCount(); j++) {
                Stand stand = project.getStands().getStandByName(person.getAssignmentForBlock(j - 1));

                Cell cell = row.createCell(j);

                XSSFCellStyle style = wb.createCellStyle();
                style.setBorderRight(j == project.getStands().getSize() ? BorderStyle.DOUBLE : BorderStyle.DASHED);
                style.setBorderBottom(i + 1 == group.getPeople().size() ? BorderStyle.DOUBLE : BorderStyle.THIN);
                if (stand != null) {
                    XSSFFont font = wb.createFont();
                    font.setBold(true);
                    font.setFontHeightInPoints((short) 14);
                    Color fore = MainWindow.blackOrWhite(stand.getColor());
                    font.setColor(new XSSFColor(fore));
                    byte[] colorBytes = new byte[]{(byte) stand.getColor().getRed(), (byte) stand.getColor().getGreen(), (byte) stand.getColor().getBlue()};
                    style.setFillForegroundColor(new XSSFColor(colorBytes, new DefaultIndexedColorMap()));
                    style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                    style.setFont(font);
                    cell.setCellValue(stand.getName());
                }
                cell.setCellStyle(style);
            }
        }
    }
}
