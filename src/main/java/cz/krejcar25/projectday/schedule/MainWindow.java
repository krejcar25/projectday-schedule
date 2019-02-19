package cz.krejcar25.projectday.schedule;

import cz.krejcar25.projectday.schedule.exports.AssignmentsExport;
import cz.krejcar25.projectday.schedule.exports.RequestsExport;
import cz.krejcar25.projectday.schedule.imports.bakalari.Seznam;
import cz.krejcar25.projectday.schedule.imports.bakalari.Student;
import org.jetbrains.annotations.NotNull;
import org.oxbow.swingbits.dialog.task.TaskDialogs;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Objects;
import java.util.logging.Logger;

public class MainWindow extends JFrame implements ActionListener {
    private static final Logger log = Logger.getLogger(MainWindow.class.getName());
    private Project project;
    private JPanel root;
    private JComboBox<Group> peopleGroupsComboBox;
    private JList<Person> peopleOfGroupList;
    private JButton peopleImportButton;
    private JButton removePersonButton;
    private JList<Stand> standsList;
    private JTextField newStandNameTextField;
    private JButton chooseNewColorButton;
    private JButton addStandButton;
    private JTextField editStandNameTextField;
    private JButton chooseEditColorButton;
    private JButton saveStandButton;
    private JButton removeStandButton;
    private JSpinner blockCountSpinner;
    private JButton exportRequestChartsButton;
    private JComboBox<Group> requestsGroupComboBox;
    private JButton requestsImportButton;
    private JTable requestsTable;
    private JTable assignmentsTable;
    private JComboBox<Group> assignmentsGroupComboBox;
    private JTabbedPane tabbedPane;
    private JTable sumsTable;
    private JLabel sumsStatusLabel;
    private JSpinner newStandLimitSpinner;
    private JSpinner editStandLimitSpinner;
    private JButton assignmentsExportButton;
    private SpinnerNumberModel blockCountSpinnerModel;

    private JMenuItem editUndo;
    private JMenuItem editRedo;
    private JMenuItem editCut;
    private JMenuItem editCopy;
    private JMenuItem editPaste;

    MainWindow() {
        this(Project.loadFromFile(null, true));
    }

    private MainWindow(Project project) {
        setContentPane(root);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        this.project = project;
        this.project.setWindow(this);

        int menuKey = Main.isMac ? InputEvent.META_DOWN_MASK : InputEvent.CTRL_DOWN_MASK;

        tabbedPane.addChangeListener(e -> {
            int pane = tabbedPane.getSelectedIndex();
            String command = "";
            switch (pane) {
                case 0:
                    command = "people.groupSelect";
                    break;
                case 2:
                    command = "requests.groupChanged";
                    break;
                case 3:
                    command = "assignments.groupChanged";
                    break;
                case 4:
                    SumsTableModel model = new SumsTableModel(project);
                    sumsTable.setModel(model);
                    sumsStatusLabel.setText(Strings.get(model.isAllAssigned() ? "sums.status.allAssigned" : "sums.status.missing"));
                    break;
            }
            if (!command.equals("")) {
                actionPerformed(new ActionEvent(this, 0, command));
            }
        });

        JMenuBar menuBar = new JMenuBar();

        // File tree
        JMenu file = new JMenu(Strings.get("toolbar.file"));
        file.setMnemonic(KeyEvent.VK_F);
        file.getAccessibleContext().setAccessibleDescription(Strings.get("toolbar.file.tooltip"));
        menuBar.add(file);

        // File tree, New file
        JMenuItem fileNew = new JMenuItem(Strings.get("toolbar.file.new"));
        fileNew.setMnemonic(KeyEvent.VK_N);
        fileNew.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, menuKey));
        fileNew.getAccessibleContext().setAccessibleDescription(Strings.get("toolbar.file.new.tooltip"));
        fileNew.setActionCommand("toolbar.file.new");
        fileNew.addActionListener(this);
        file.add(fileNew);

        // File tree, Open file
        JMenuItem fileOpen = new JMenuItem(Strings.get("toolbar.file.open"));
        fileOpen.setMnemonic(KeyEvent.VK_O);
        fileOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, menuKey));
        fileOpen.getAccessibleContext().setAccessibleDescription(Strings.get("toolbar.file.open.tooltip"));
        fileOpen.setActionCommand("toolbar.file.open");
        fileOpen.addActionListener(this);
        file.add(fileOpen);

        // File tree, Save file
        JMenuItem fileSave = new JMenuItem(Strings.get("toolbar.file.save"));
        fileSave.setMnemonic(KeyEvent.VK_S);
        fileSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, menuKey));
        fileSave.getAccessibleContext().setAccessibleDescription(Strings.get("toolbar.file.save.tooltip"));
        fileSave.setActionCommand("toolbar.file.save");
        fileSave.addActionListener(this);
        file.add(fileSave);

        // File tree, Save file as
        JMenuItem fileSaveAs = new JMenuItem(Strings.get("toolbar.file.saveAs"));
        fileSaveAs.setMnemonic(KeyEvent.VK_Z);
        fileSaveAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, menuKey + InputEvent.SHIFT_DOWN_MASK));
        fileSaveAs.getAccessibleContext().setAccessibleDescription(Strings.get("toolbar.file.saveAs.tooltip"));
        fileSaveAs.setActionCommand("toolbar.file.saveAs");
        fileSaveAs.addActionListener(this);
        file.add(fileSaveAs);

        // Edit tree
        JMenu edit = new JMenu(Strings.get("toolbar.edit"));
        edit.setMnemonic(KeyEvent.VK_E);
        edit.getAccessibleContext().setAccessibleDescription(Strings.get("toolbar.edit.tooltip"));
        menuBar.add(edit);

        // Edit tree, Undo
        editUndo = new JMenuItem(Strings.get("toolbar.edit.undo"));
        editUndo.setMnemonic(KeyEvent.VK_U);
        editUndo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, menuKey));
        editUndo.getAccessibleContext().setAccessibleDescription(Strings.get("toolbar.edit.undo.tooltip"));
        editUndo.setActionCommand("toolbar.edit");
        editUndo.addActionListener(this);
        edit.add(editUndo);

        // Edit tree, Redo
        editRedo = new JMenuItem(Strings.get("toolbar.edit.redo"));
        editRedo.setMnemonic(KeyEvent.VK_R);
        if (Main.isMac)
            editRedo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, menuKey + InputEvent.SHIFT_DOWN_MASK));
        else editRedo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, menuKey));
        editRedo.getAccessibleContext().setAccessibleDescription(Strings.get("toolbar.edit.redo.tooltip"));
        editRedo.setActionCommand("toolbar.edit.redo");
        editRedo.addActionListener(this);
        edit.add(editRedo);

        edit.addSeparator();

        // Edit tree, Cut
        editCut = new JMenuItem(Strings.get("toolbar.edit.cut"));
        editCut.setMnemonic(KeyEvent.VK_T);
        editCut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, menuKey));
        editCut.setActionCommand("toolbar.edit.cut");
        editCut.addActionListener(this);
        edit.add(editCut);

        // Edit tree, Copy
        editCopy = new JMenuItem(Strings.get("toolbar.edit.copy"));
        editCopy.setMnemonic(KeyEvent.VK_C);
        editCopy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, menuKey));
        editCopy.setActionCommand("toolbar.edit.copy");
        editCopy.addActionListener(this);
        edit.add(editCopy);

        // Edit tree, Paste
        editPaste = new JMenuItem(Strings.get("toolbar.edit.paste"));
        editPaste.setMnemonic(KeyEvent.VK_P);
        editPaste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, menuKey));
        editPaste.setActionCommand("toolbar.edit.paste");
        editPaste.addActionListener(this);
        edit.add(editPaste);

        if (Main.isMac) setJMenuBar(menuBar);
        else root.add(menuBar, BorderLayout.NORTH);

        JFrame thisFrame = this;

        addWindowListener(new WindowAdapter() {
            boolean alreadyOpen = false;

            private int confirm() {
                String[] options = new String[]{
                        Strings.get("closeConfirm.yes"),
                        Strings.get("closeConfirm.no"),
                        Strings.get("closeConfirm.cancel")
                };
                if (alreadyOpen) return -1;
                alreadyOpen = true;
                int rVal = JOptionPane.showOptionDialog(
                        thisFrame,
                        Strings.get("closeConfirm.text"),
                        Strings.get("closeConfirm.title"),
                        JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.WARNING_MESSAGE,
                        null,
                        options,
                        options[0]);
                alreadyOpen = false;
                return rVal;
            }

            @Override
            public void windowClosing(WindowEvent e) {
                switch (confirm()) {
                    case 0:
                        project.save();
                        dispose();
                        break;
                    case 1:
                        dispose();
                        break;
                    case 2:
                        break;
                }
            }
        });

        peopleGroupsComboBox.setModel(new DefaultComboBoxModel<>(project.getGroups()));
        peopleGroupsComboBox.addActionListener(this);
        peopleGroupsComboBox.setActionCommand("people.groupSelect");

        peopleImportButton.addActionListener(this);
        peopleImportButton.setActionCommand("people.import");

        removePersonButton.addActionListener(this);
        removePersonButton.setActionCommand("people.remove");

        standsList.setModel(project.getStands());
        standsList.setCellRenderer(new StandRenderer());
        standsList.addListSelectionListener(e -> {
            if (standsList.getSelectedIndex() < 0) {
                editStandNameTextField.setText("");
                chooseEditColorButton.setForeground(Color.BLACK);
            } else {
                editStandNameTextField.setText(standsList.getSelectedValue().getName());
                chooseEditColorButton.setForeground(standsList.getSelectedValue().getColor());
                editStandLimitSpinner.setValue(standsList.getSelectedValue().getLimit());
            }
        });

        chooseNewColorButton.addActionListener(this);
        chooseNewColorButton.setActionCommand("stands.new.chooseColor");

        newStandLimitSpinner.setModel(new SpinnerNumberModel(-1, -1, Integer.MAX_VALUE, 1));
        editStandLimitSpinner.setModel(new SpinnerNumberModel(-1, -1, Integer.MAX_VALUE, 1));

        chooseEditColorButton.addActionListener(this);
        chooseEditColorButton.setActionCommand("stands.edit.chooseColor");

        saveStandButton.addActionListener(this);
        saveStandButton.setActionCommand("stands.edit.save");

        removeStandButton.addActionListener(this);
        removeStandButton.setActionCommand("stands.edit.remove");

        addStandButton.addActionListener(this);
        addStandButton.setActionCommand("stands.new.add");

        blockCountSpinnerModel = new SpinnerNumberModel(project.getBlockCount(), 1, 16, 1);
        blockCountSpinnerModel.addChangeListener(e -> project.setBlockCount((int) blockCountSpinnerModel.getValue()));
        blockCountSpinner.setModel(blockCountSpinnerModel);

        exportRequestChartsButton.addActionListener(this);
        exportRequestChartsButton.setActionCommand("exports.requests.run");

        requestsGroupComboBox.setModel(new DefaultComboBoxModel<>(project.getGroups()));
        requestsGroupComboBox.addActionListener(this);
        requestsGroupComboBox.setActionCommand("requests.groupChanged");

        requestsTable.setGridColor(Color.BLACK);
        requestsTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {

                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

                if (value instanceof Stand) {
                    setBackground(((Stand) value).getColor());
                    setForeground(blackOrWhite(getBackground()));
                } else {
                    setBackground(table.getBackground());
                    setForeground(table.getForeground());
                }
                return this;
            }
        });

        assignmentsGroupComboBox.setModel(new DefaultComboBoxModel<>(project.getGroups()));
        assignmentsGroupComboBox.addActionListener(this);
        assignmentsGroupComboBox.setActionCommand("assignments.groupChanged");

        assignmentsTable.setGridColor(Color.BLACK);
        assignmentsTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {

                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

                if (value instanceof Stand) {
                    setBackground(((Stand) value).getColor());
                    setForeground(blackOrWhite(getBackground()));
                } else {
                    setBackground(table.getBackground());
                    setForeground(table.getForeground());
                }
                return this;
            }
        });

        actionPerformed(new ActionEvent(this, 0, "people.groupSelect"));

        sumsTable.setDefaultRenderer(Stand.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Stand stand = (Stand) value;
                Component c = super.getTableCellRendererComponent(table, stand.getName(), isSelected, hasFocus, row, column);
                c.setBackground(stand.getColor());
                c.setForeground(blackOrWhite(stand.getColor()));
                return c;
            }
        });
        sumsTable.setDefaultRenderer(String.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (isSelected) {
                    Stand stand = project.getStands().getElementAt(row);
                    c.setBackground(stand.getColor());
                    c.setForeground(blackOrWhite(stand.getColor()));
                } else {
                    c.setBackground(sumsTable.getBackground());
                    c.setForeground(sumsTable.getForeground());
                }
                return c;
            }
        });
        sumsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sumsTable.setGridColor(Color.BLACK);

        assignmentsExportButton.addActionListener(this);
        assignmentsExportButton.setActionCommand("exports.assignments.run");

        pack();
    }

    public static Color blackOrWhite(@NotNull Color background) {
        int red = background.getRed();
        int green = background.getGreen();
        int blue = background.getBlue();

        return (red * 0.299 + green * 0.587 + blue * 0.114) > 186 ? Color.BLACK : Color.WHITE;
    }

    @Override
    @SuppressWarnings({"SwitchStatementWithTooFewBranches"})
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        String[] subcommands = command.split("\\.");
        switch (subcommands[0]) {
            case "toolbar":
                switch (subcommands[1]) {
                    case "file":
                        switch (subcommands[2]) {
                            case "new":
                                new MainWindow(new Project()).setVisible(true);
                            case "open":
                                Project loadedProject = Project.loadFromFile(this, false);
                                if (loadedProject != null) {
                                    MainWindow window = new MainWindow(loadedProject);
                                    loadedProject.setWindow(window);
                                    window.setVisible(true);
                                }
                                break;
                            case "save":
                                project.save();
                                break;
                            case "saveAs":
                                project.saveAs();
                                break;
                        }
                        break;
                    case "edit":
                        switch (subcommands[2]) {
                            case "undo":
                                break;
                            case "redo":
                                break;
                            case "cut":
                                break;
                            case "copy":
                                break;
                            case "paste":
                                break;
                        }
                        break;
                }
                break;
            case "people":
                switch (subcommands[1]) {
                    case "import":
                        importPeople();
                        break;
                    case "groupSelect":
                        Group group = (Group) peopleGroupsComboBox.getSelectedItem();
                        peopleOfGroupList.setModel(Objects.requireNonNullElseGet(group, DefaultListModel::new));
                        break;
                    case "remove":
                        Person person = peopleOfGroupList.getSelectedValue();
                        if (person != null) person.getGroup().removePerson(person.getName());
                }
                break;
            case "stands":
                switch (subcommands[1]) {
                    case "new":
                        switch (subcommands[2]) {
                            case "chooseColor":
                                Color oc = chooseNewColorButton.getForeground();
                                Color nc = JColorChooser.showDialog(null, Strings.get("stands.chooseColor.title"), oc);
                                chooseNewColorButton.setForeground(nc);
                                break;
                            case "add":
                                project.addStand(new Stand(newStandNameTextField.getText(), chooseNewColorButton.getForeground(), (int) newStandLimitSpinner.getValue()));
                                newStandNameTextField.setText("");
                                chooseNewColorButton.setForeground(Color.BLACK);
                                break;
                        }
                        break;
                    case "edit":
                        switch (subcommands[2]) {
                            case "chooseColor":
                                Color oc = chooseEditColorButton.getForeground();
                                Color nc = JColorChooser.showDialog(null, Strings.get("stands.chooseColor.title"), oc);
                                chooseEditColorButton.setForeground(nc);
                                break;
                            case "save":
                                Stand selected = standsList.getSelectedValue();
                                String oldName = selected.getName();
                                selected.setName(editStandNameTextField.getText());
                                selected.setColor(chooseEditColorButton.getForeground());
                                selected.setLimit((int) editStandLimitSpinner.getValue());
                                for (Person person : project.getAllPeople())
                                    person.renameRequest(oldName, selected.getName());
                                standsList.clearSelection();
                                break;
                            case "remove":
                                project.removeStand(standsList.getSelectedValue());
                                standsList.clearSelection();
                                break;
                        }
                        break;
                }
                break;
            case "requests":
                switch (subcommands[1]) {
                    case "groupChanged":
                        Group group = (Group) requestsGroupComboBox.getSelectedItem();
                        if (group != null) {
                            GroupStandsTableModel model = new GroupStandsTableModel(group, RequestsLine.REQUEST_LINE);
                            requestsTable.setModel(model);
                            TableColumnModel tableColumnModel = requestsTable.getColumnModel();
                            for (int i = 1; i < model.getColumnCount(); i++) {
                                JComboBox<Stand> box = new JComboBox<>(new StandComboBoxModel(project, false));
                                box.setOpaque(true);
                                box.addActionListener(evt -> {
                                    Stand stand = (Stand) box.getSelectedItem();
                                    if (stand != null) box.setBackground(stand.getColor());
                                });
                                box.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
                                    JLabel label = new JLabel(value.getName());
                                    label.setOpaque(true);
                                    label.setBackground(value.getColor());
                                    label.setForeground(blackOrWhite(value.getColor()));
                                    label.setFont(label.getFont().deriveFont(18f));
                                    return label;
                                });
                                tableColumnModel.getColumn(i).setCellEditor(new DefaultCellEditor(box));
                            }
                        }
                        break;
                }
                break;
            case "assignments":
                switch (subcommands[1]) {
                    case "groupChanged":
                        Group group = (Group) assignmentsGroupComboBox.getSelectedItem();
                        if (group != null) {
                            GroupStandsTableModel model = new GroupStandsTableModel(group, RequestsLine.ASSIGNMENT_LINE);
                            assignmentsTable.setModel(model);
                            TableColumnModel tableColumnModel = assignmentsTable.getColumnModel();
                            for (int i = 1; i < model.getColumnCount(); i++) {
                                JComboBox<Stand> box = new JComboBox<>(new StandComboBoxModel(project, true));
                                box.setOpaque(true);
                                box.addActionListener(evt -> {
                                    Stand stand = (Stand) box.getSelectedItem();
                                    if (stand != null) box.setBackground(stand.getColor());
                                });
                                box.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
                                    JLabel label = new JLabel(value.getName());
                                    label.setOpaque(true);
                                    label.setBackground(value.getColor());
                                    label.setForeground(blackOrWhite(value.getColor()));
                                    label.setFont(label.getFont().deriveFont(18f));
                                    return label;
                                });
                                tableColumnModel.getColumn(i).setCellEditor(new DefaultCellEditor(box));
                            }
                        }
                        break;
                }
                break;
            case "sums":
                break;
            case "exports":
                switch (subcommands[1]) {
                    case "requests":
                        switch (subcommands[2]) {
                            case "run":
                                RequestsExport export = new RequestsExport(project);
                                export.generate();
                                export.save(this);
                                break;
                        }
                        break;
                    case "assignments":
                        switch (subcommands[2]) {
                            case "run":
                                AssignmentsExport export = new AssignmentsExport(project);
                                export.generate();
                                export.save(this);
                                break;
                        }
                        break;
                }
                break;
        }
    }

    private void importPeople() {
        JFileChooser c = new JFileChooser();
        String xmlExt = "xml";
        c.addChoosableFileFilter(new FileNameExtensionFilter(Strings.get("people.import.fileType.desc"), xmlExt));
        c.setAcceptAllFileFilterUsed(false);
        int rVal = c.showOpenDialog(this);

        if (rVal == JFileChooser.APPROVE_OPTION) {
            File file = c.getSelectedFile();

            if (file.getName().endsWith(xmlExt)) {
                try {
                    // is Bakalari?
                    if (XmlValidation.validateAgainstXSD(new FileInputStream(file), this.getClass().getResourceAsStream("bakalari.xsd"))) {
                        JAXBContext jaxbContext = JAXBContext.newInstance(Seznam.class, Student.class);
                        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

                        Seznam seznam = (Seznam) jaxbUnmarshaller.unmarshal(file);

                        for (Student student : seznam.getStudent())
                            project.addPerson(student.getJmeno(), student.getTrida());
                    } else JOptionPane.showMessageDialog(this,
                            Strings.get("people.import.unknownFormat.message"),
                            Strings.get("people.import.unknownFormat.title"),
                            JOptionPane.WARNING_MESSAGE);
                } catch (FileNotFoundException e) {
                    JOptionPane.showMessageDialog(this,
                            Strings.get("people.import.fileNotFound.message"),
                            Strings.get("people.import.fileNotFound.title"),
                            JOptionPane.WARNING_MESSAGE
                    );
                } catch (Exception e) {
                    TaskDialogs.showException(e);
                    log.warning(e.getMessage());
                }
            }
        }
    }
}