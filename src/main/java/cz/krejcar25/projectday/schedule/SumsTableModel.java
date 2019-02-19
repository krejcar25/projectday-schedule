package cz.krejcar25.projectday.schedule;

import org.jetbrains.annotations.NotNull;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class SumsTableModel implements TableModel {
    private final int rowCount;
    private final int columnCount;
    private final String[] columnNames;
    private final Stand[] stands;
    private final String[][] counts;
    private boolean allAssigned;

    SumsTableModel(@NotNull Project project) {
        this.rowCount = project.getStands().getSize();
        this.columnCount = project.getBlockCount() + 4;
        this.columnNames = new String[this.columnCount];
        this.columnNames[0] = Strings.get("sums.headers.name");
        int c;
        for (c = 1; c <= project.getBlockCount(); c++) {
            this.columnNames[c] = String.format("%d.", c);
        }
        this.columnNames[c++] = Strings.get("sums.headers.average");
        this.columnNames[c++] = Strings.get("sums.headers.total");
        this.columnNames[c] = Strings.get("sums.headers.limit");
        this.stands = new Stand[this.rowCount];
        for (int i = 0; i < this.stands.length; i++) {
            this.stands[i] = project.getStands().getElementAt(i);
        }
        this.allAssigned = true;
        int[][] counts = new int[project.getStands().getSize()][project.getBlockCount()];
        for (int i = 0; i < counts.length; i++)
            for (int j = 0; j < project.getBlockCount(); j++)
                for (Person person : project.getAllPeople())
                    if (person.getAssignmentForBlock(j).equals(stands[i].getName())) counts[i][j]++;
                    else if (person.getAssignmentForBlock(j).equals(Stand.EMPTY.getName())) this.allAssigned = false;

        this.counts = new String[this.rowCount][this.columnCount - 1];
        for (int i = 0; i < this.counts.length; i++) {
            int j;
            int sum = 0;
            for (j = 0; j < project.getBlockCount(); j++) {
                sum += counts[i][j];
                this.counts[i][j] = String.valueOf(counts[i][j]);
            }
            this.counts[i][j] = String.valueOf(sum / (double) (j++));
            this.counts[i][j++] = String.valueOf(sum);
            this.counts[i][j] = String.valueOf(project.getStands().getElementAt(i).getLimit());
        }
    }

    @Override
    public int getRowCount() {
        return rowCount;
    }

    @Override
    public int getColumnCount() {
        return columnCount;
    }

    @Override
    public String getColumnName(int columnIndex) {
        return columnNames[columnIndex];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return columnIndex > 0 ? String.class : Stand.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            return stands[rowIndex];
        } else {
            return counts[rowIndex][columnIndex - 1];
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {

    }

    @Override
    public void addTableModelListener(TableModelListener l) {

    }

    @Override
    public void removeTableModelListener(TableModelListener l) {

    }

    boolean isAllAssigned() {
        return allAssigned;
    }
}
