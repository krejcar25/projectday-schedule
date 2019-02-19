package cz.krejcar25.projectday.schedule.exports;

import cz.krejcar25.projectday.schedule.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.binary.XSSFBStylesTable;
import org.apache.poi.xssf.usermodel.*;
import org.jetbrains.annotations.NotNull;
import org.oxbow.swingbits.dialog.task.TaskDialogs;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class AssignmentsExport {
    private Project project;
    private Workbook wb;
    private boolean generated;

    public AssignmentsExport(Project project) {
        this.project = project;
        wb = new XSSFWorkbook();
        generated = false;
    }

    public void generate() throws IllegalStateException {
        if (generated)
            throw new IllegalStateException("This generator has already been used! Instances are single-use.");
        generated = true;
        for (Group group : project.getGroups()) {
            Sheet sheet = wb.createSheet(group.getName());
            addBlockHeaders(sheet);
            addNames(sheet, group);
            sheet.autoSizeColumn(0);
        }
    }

    public void save(JFrame frame) throws IllegalStateException {
        if (!generated)
            throw new IllegalStateException("The workbook has not been generated. Run the generate method first.");
        JFileChooser c = new JFileChooser();
        String ext = "xlsx";
        c.addChoosableFileFilter(new FileNameExtensionFilter(Strings.get("exports.excel.fileType.desc"), ext));
        c.setAcceptAllFileFilterUsed(false);
        int rVal = c.showSaveDialog(frame);

        if (rVal == JFileChooser.APPROVE_OPTION) {
            File file = c.getSelectedFile();
            if (!file.getAbsoluteFile().toString().endsWith(ext)) file = new File(file.getAbsolutePath() + "." + ext);
            try (FileOutputStream outputStream = new FileOutputStream(file)) {
                wb.write(outputStream);
            } catch (FileNotFoundException e) {
                // We don't care
            } catch (IOException e) {
                TaskDialogs.showException(e);
            }
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
            XSSFFont font = (XSSFFont) wb.createFont();
            font.setBold(true);
            font.setFontHeightInPoints((short) 14);

            XSSFCellStyle style = (XSSFCellStyle) wb.createCellStyle();
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

                XSSFCellStyle style = (XSSFCellStyle) wb.createCellStyle();
                style.setBorderRight(j == project.getStands().getSize() ? BorderStyle.DOUBLE : BorderStyle.DASHED);
                style.setBorderBottom(i + 1 == group.getPeople().size() ? BorderStyle.DOUBLE : BorderStyle.THIN);
                if (stand != null) {
                    XSSFFont font = (XSSFFont) wb.createFont();
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
