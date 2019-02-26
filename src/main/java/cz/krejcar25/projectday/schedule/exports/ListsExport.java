package cz.krejcar25.projectday.schedule.exports;

import cz.krejcar25.projectday.schedule.MainWindow;
import cz.krejcar25.projectday.schedule.Person;
import cz.krejcar25.projectday.schedule.Project;
import cz.krejcar25.projectday.schedule.Stand;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.binary.XSSFBStylesTable;
import org.apache.poi.xssf.usermodel.DefaultIndexedColorMap;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class ListsExport extends ExcelExport
{
	protected HashMap<String, ArrayList<ArrayList<Person>>> lists;

	public ListsExport(Project project)
	{
		super(project);
		lists = new HashMap<>();
		for (Stand stand : project.getStands())
			lists.put(stand.getName(), new ArrayList<>()
			{
				{
					for (int i = 0; i < project.getBlockCount(); i++) add(new ArrayList<>());
				}
			});
		for (Person person : project.getAllPeople())
			for (int i = 0; i < project.getBlockCount(); i++)
			{
				ArrayList<ArrayList<Person>> arrayLists = lists.get(person.getAssignmentForBlock(i));
				if (arrayLists != null)
				{
					ArrayList<Person> people = arrayLists.get(i);
					people.add(person);
				}
			}
	}

	protected XSSFCellStyle createHeaderStyle(Stand stand)
	{
		XSSFFont headerFont = wb.createFont();
		Color back = stand.getColor();
		Color fore = MainWindow.blackOrWhite(back);
		headerFont.setColor(new XSSFColor(fore));
		headerFont.setFontHeight(20);
		headerFont.setBold(true);
		XSSFCellStyle headerStyle = wb.createCellStyle();
		headerStyle.setBorderTop(BorderStyle.DOUBLE);
		headerStyle.setBorderRight(BorderStyle.DOUBLE);
		headerStyle.setBorderBottom(BorderStyle.THICK);
		headerStyle.setBorderLeft(BorderStyle.DOUBLE);
		headerStyle.setFillForegroundColor(new XSSFColor(back));
		headerStyle.setFont(headerFont);
		headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		headerStyle.setAlignment(HorizontalAlignment.CENTER);
		return headerStyle;
	}
}
