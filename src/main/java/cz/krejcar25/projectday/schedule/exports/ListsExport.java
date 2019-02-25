package cz.krejcar25.projectday.schedule.exports;

import cz.krejcar25.projectday.schedule.MainWindow;
import cz.krejcar25.projectday.schedule.Person;
import cz.krejcar25.projectday.schedule.Project;
import cz.krejcar25.projectday.schedule.Stand;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;

import java.awt.*;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;

public class ListsExport extends ExcelExport {
    private HashMap<String, ArrayList<ArrayList<Person>>> lists;

    public ListsExport(Project project) {
        super(project);
        lists = new HashMap<>();
        for (Stand stand : project.getStands())
            lists.put(stand.getName(), new ArrayList<>() {
                {
                    for (int i = 0; i < project.getBlockCount(); i++) add(new ArrayList<>());
                }
            });
        for (Person person : project.getAllPeople())
            for (int i = 0; i < project.getBlockCount(); i++) {
                ArrayList<ArrayList<Person>> arrayLists = lists.get(person.getAssignmentForBlock(i));
                if (arrayLists != null) {
                    ArrayList<Person> people = arrayLists.get(i);
                    people.add(person);
                }
            }
    }

    @Override
    protected void writeTable() {
        for (Stand stand : project.getStands()) {
            XSSFSheet sheet = wb.createSheet(stand.getName());
            XSSFFont headerFont = wb.createFont();
            Color back = stand.getColor();
            Color fore = MainWindow.blackOrWhite(back);
            headerFont.setColor(new XSSFColor(new byte[]{(byte) fore.getRed(), (byte) fore.getBlue(), (byte) fore.getBlue()}, new DefaultIndexedColorMap()));
            headerFont.setFontHeight(20);
            XSSFCellStyle headerStyle = wb.createCellStyle();
            headerStyle.setBorderTop(BorderStyle.DOUBLE);
            headerStyle.setBorderRight(BorderStyle.DOUBLE);
            headerStyle.setBorderBottom(BorderStyle.THICK);
            headerStyle.setBorderLeft(BorderStyle.DOUBLE);
            headerStyle.setFillForegroundColor(new XSSFColor(new byte[]{(byte) back.getRed(), (byte) back.getBlue(), (byte) back.getBlue()}, new DefaultIndexedColorMap()));
            headerStyle.setFont(headerFont);
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            XSSFRow headerRow = sheet.createRow(0);
            XSSFCell headerCell = headerRow.createCell(0);
            headerCell.setCellStyle(headerStyle);
            headerCell.setCellValue(stand.getName());
            for (int i = 1; i < project.getBlockCount(); i++) headerRow.createCell(i).setCellStyle(headerStyle);
            CellRangeAddress header = new CellRangeAddress(0, 0, 0, project.getBlockCount() - 1);
            sheet.addMergedRegion(header);
            int longest = 0;
            for (ArrayList<Person> list : lists.get(stand.getName())) if (list.size() > longest) longest = list.size();
            for (int block = 0; block < project.getBlockCount(); block++) {
                ArrayList<Person> list = lists.get(stand.getName()).get(block);
                for (int p = 0; p < longest; p++) {
                    XSSFCellStyle cellStyle = wb.createCellStyle();
                    cellStyle.setBorderRight(block + 1 == project.getBlockCount() ? BorderStyle.DOUBLE : BorderStyle.THIN);
                    cellStyle.setBorderBottom(p + 1 == longest ? BorderStyle.DOUBLE : BorderStyle.DASHED);
                    if (block == 0) cellStyle.setBorderLeft(BorderStyle.DOUBLE);
                    XSSFRow row = sheet.getRow(p + 1);
                    if (row == null) row = sheet.createRow(p + 1);
                    XSSFCell cell = row.createCell(block);
                    cell.setCellStyle(cellStyle);
                    if (p < list.size()) cell.setCellValue(list.get(p).getName());
                }
                sheet.autoSizeColumn(block);
            }
            XSSFPrintSetup printSetup = sheet.getPrintSetup();
            printSetup.setPaperSize(PrintSetup.A4_PAPERSIZE);
            printSetup.setOrientation(PrintOrientation.PORTRAIT);
            printSetup.setFitHeight((short) 1);
            printSetup.setFitWidth((short) 1);
        }
    }
}
