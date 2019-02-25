package cz.krejcar25.projectday.schedule.exports;

import cz.krejcar25.projectday.schedule.Project;
import cz.krejcar25.projectday.schedule.Strings;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.oxbow.swingbits.dialog.task.TaskDialogs;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public abstract class ExcelExport {
    protected final Project project;
    protected final XSSFWorkbook wb;
    private boolean generated;

    protected ExcelExport(Project project) {
        this.project = project;
        this.wb = new XSSFWorkbook();
        this.generated = false;
    }

    public final void generate() {
        if (generated)
            throw new IllegalStateException("This generator has already been used! Instances are single-use.");
        generated = true;
        writeTable();
    }

    public final void save(JFrame frame) {
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

    protected abstract void writeTable();
}
