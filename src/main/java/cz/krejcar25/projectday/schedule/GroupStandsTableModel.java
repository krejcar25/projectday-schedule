package cz.krejcar25.projectday.schedule;

import org.intellij.lang.annotations.MagicConstant;
import org.jetbrains.annotations.NotNull;

import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import java.util.Vector;

public class GroupStandsTableModel extends AbstractTableModel
{
	private final Group group;
	private final Vector<RequestsLine> lines;
	@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
	private final Vector<TableModelListener> tableModelListeners;
	private final int mode;

	GroupStandsTableModel(@NotNull Group group, @MagicConstant(intValues = {RequestsLine.REQUEST_LINE, RequestsLine.ASSIGNMENT_LINE}) int mode)
	{
		this.group = group;
		this.lines = new Vector<>();
		this.tableModelListeners = new Vector<>();
		this.mode = mode;

		for (Person person : group.getPeople()) lines.add(new RequestsLine(person, mode));
	}

	@Override
	public int getRowCount()
	{
		return lines.size();
	}

	@Override
	public int getColumnCount()
	{
		switch (mode)
		{
			case RequestsLine.REQUEST_LINE:
				return group.project.getStands().getSize() + 1;
			case RequestsLine.ASSIGNMENT_LINE:
				return group.project.getBlockCount() + 1;
			default:
				return -1;
		}
	}

	@Override
	public String getColumnName(int columnIndex)
	{
		if (columnIndex > 0 && columnIndex <= getColumnCount())
			return String.valueOf(columnIndex);
		else if (columnIndex < 0 || columnIndex > getColumnCount())
			return "";
		else return Strings.get("requests.names.header");
	}

	@Override
	public Class<?> getColumnClass(int columnIndex)
	{
		return columnIndex == 0 ? String.class : Stand.class;
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex)
	{
		return columnIndex > 0;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		return lines.get(rowIndex).getValue(columnIndex);
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex)
	{
		lines.get(rowIndex).setValue(columnIndex, aValue);
	}

	@Override
	public void addTableModelListener(TableModelListener l)
	{
		this.tableModelListeners.add(l);
	}

	@Override
	public void removeTableModelListener(TableModelListener l)
	{
		this.tableModelListeners.remove(l);
	}
}
