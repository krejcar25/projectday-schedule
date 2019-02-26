package cz.krejcar25.projectday.schedule.exports;

import cz.krejcar25.projectday.schedule.Person;
import cz.krejcar25.projectday.schedule.Project;
import cz.krejcar25.projectday.schedule.Stand;
import cz.krejcar25.projectday.schedule.Strings;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.PrintOrientation;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;

import java.util.List;

public class PresenceExport extends ListsExport
{
	private final XSSFCellStyle leftMiddleCellStyle;
	private final XSSFCellStyle rightMiddleCellStyle;
	private final XSSFCellStyle leftBottomCellStyle;
	private final XSSFCellStyle rightBottomCellStyle;

	public PresenceExport(Project project)
	{
		super(project);

		leftMiddleCellStyle = wb.createCellStyle();
		leftMiddleCellStyle.setBorderRight(BorderStyle.DASHED);
		leftMiddleCellStyle.setBorderBottom(BorderStyle.THIN);
		leftMiddleCellStyle.setBorderLeft(BorderStyle.DOUBLE);

		rightMiddleCellStyle = wb.createCellStyle();
		rightMiddleCellStyle.setBorderRight(BorderStyle.DOUBLE);
		rightMiddleCellStyle.setBorderBottom(BorderStyle.THIN);
		rightMiddleCellStyle.setBorderLeft(BorderStyle.DASHED);

		leftBottomCellStyle = wb.createCellStyle();
		leftBottomCellStyle.setBorderRight(BorderStyle.DASHED);
		leftBottomCellStyle.setBorderBottom(BorderStyle.DOUBLE);
		leftBottomCellStyle.setBorderLeft(BorderStyle.DOUBLE);

		rightBottomCellStyle = wb.createCellStyle();
		rightBottomCellStyle.setBorderRight(BorderStyle.DOUBLE);
		rightBottomCellStyle.setBorderBottom(BorderStyle.DOUBLE);
		rightBottomCellStyle.setBorderLeft(BorderStyle.DASHED);
	}

	@Override
	protected void writeTable()
	{
		for (Stand stand : project.getStands())
		{
			XSSFSheet sheet = wb.createSheet(stand.getName());
			XSSFCellStyle headerStyle = createHeaderStyle(stand);
			for (int block = 0; block < project.getBlockCount(); block++)
			{
				XSSFRow headerRow = sheet.getRow(0);
				if (headerRow == null) headerRow = sheet.createRow(0);
				XSSFCell headerCell = headerRow.createCell(2 * block);
				headerCell.setCellStyle(headerStyle);
				headerCell.setCellValue(String.format("%s - %d. %s", stand.getName(), block + 1, Strings.get("block.label")));
				headerRow.createCell(2 * block + 1).setCellStyle(headerStyle);
				sheet.addMergedRegion(new CellRangeAddress(0, 0, 2 * block, 2 * block + 1));

				XSSFFont boldFont = wb.createFont();
				boldFont.setBold(true);

				XSSFRow labelRow = sheet.getRow(1);
				if (labelRow == null) labelRow = sheet.createRow(1);
				XSSFCell leftLabelCell = labelRow.createCell(2 * block);
				leftLabelCell.setCellValue(Strings.get("requests.names.header"));
				XSSFCellStyle leftLabelStyle = (XSSFCellStyle) leftMiddleCellStyle.clone();
				leftLabelStyle.setFont(boldFont);
				leftLabelCell.setCellStyle(leftLabelStyle);

				XSSFCell rightLabelCell = labelRow.createCell(2 * block + 1);
				rightLabelCell.setCellValue(Strings.get("assignments.export.presenceLists.signature"));
				XSSFCellStyle rightLabelStyle = (XSSFCellStyle) rightMiddleCellStyle.clone();
				rightLabelStyle.setFont(boldFont);
				rightLabelCell.setCellStyle(rightLabelStyle);

				List<Person> people = lists.get(stand.getName()).get(block);
				for (int p = 0; p < people.size(); p++)
				{
					XSSFRow row = sheet.getRow(p + 2);
					if (row == null) row = sheet.createRow(p + 2);
					XSSFCell cell = row.createCell(2 * block);
					cell.setCellValue(people.get(p).getName());
					boolean isLastRow = p + 1 == people.size();
					cell.setCellStyle(isLastRow ? leftBottomCellStyle : leftMiddleCellStyle);
					row.createCell(2 * block + 1).setCellStyle(isLastRow ? rightBottomCellStyle : rightMiddleCellStyle);
				}

				sheet.autoSizeColumn(2 * block);
			}
			XSSFPrintSetup printSetup = sheet.getPrintSetup();
			printSetup.setPaperSize(PrintSetup.A4_PAPERSIZE);
			printSetup.setOrientation(PrintOrientation.PORTRAIT);
			printSetup.setFitWidth((short) project.getBlockCount());
			printSetup.setFitHeight((short) 1);
		}
	}
}
