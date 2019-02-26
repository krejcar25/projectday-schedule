package cz.krejcar25.projectday.schedule.exports;

import cz.krejcar25.projectday.schedule.Person;
import cz.krejcar25.projectday.schedule.Project;
import cz.krejcar25.projectday.schedule.Stand;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.PrintOrientation;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;

import java.util.ArrayList;

public class SingleListExport extends ListsExport
{
	public SingleListExport(Project project)
	{
		super(project);
	}

	@Override
	protected void writeTable()
	{
		for (Stand stand : project.getStands())
		{
			XSSFSheet sheet = wb.createSheet(stand.getName());
			XSSFCellStyle headerStyle = createHeaderStyle(stand);
			XSSFRow headerRow = sheet.createRow(0);
			XSSFCell headerCell = headerRow.createCell(0);
			headerCell.setCellStyle(headerStyle);
			headerCell.setCellValue(stand.getName());
			for (int i = 1; i < project.getBlockCount(); i++) headerRow.createCell(i).setCellStyle(headerStyle);
			CellRangeAddress header = new CellRangeAddress(0, 0, 0, project.getBlockCount() - 1);
			sheet.addMergedRegion(header);
			int longest = 0;
			for (ArrayList<Person> list : lists.get(stand.getName())) if (list.size() > longest) longest = list.size();
			for (int block = 0; block < project.getBlockCount(); block++)
			{
				ArrayList<Person> list = lists.get(stand.getName()).get(block);
				for (int p = 0; p < longest; p++)
				{
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
			printSetup.setFitWidth((short) 1);
			printSetup.setFitHeight((short) 1);
		}
	}
}
