package cz.krejcar25.projectday.schedule;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import cz.krejcar25.projectday.schedule.exports.AssignmentsExport;
import cz.krejcar25.projectday.schedule.exports.PresenceExport;
import cz.krejcar25.projectday.schedule.exports.RequestsExport;
import cz.krejcar25.projectday.schedule.exports.SingleListExport;
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
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.logging.Logger;

public class MainWindow extends JFrame implements ActionListener
{
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
	private JList<Stand> assignmentsPersonRequestsList;
	private JLabel assignmentsPersonNameLabel;
	private JButton listsExportButton;
	private JButton presenceExportButton;
	private JTextField newPersonNameTextField;
	private JComboBox<String> newPersonGroupComboBox;
	private JButton addPersonButton;
	private SpinnerNumberModel blockCountSpinnerModel;

	private JMenuItem editUndo;
	private JMenuItem editRedo;
	private JMenuItem editCut;
	private JMenuItem editCopy;
	private JMenuItem editPaste;

	MainWindow()
	{
		this(Project.loadFromFile(null, true));
	}

	private MainWindow(Project project)
	{
		setContentPane(root);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		this.project = project;
		this.project.setWindow(this);

		int menuKey = Main.isMac ? InputEvent.META_DOWN_MASK : InputEvent.CTRL_DOWN_MASK;

		tabbedPane.addChangeListener(e ->
		{
			int pane = tabbedPane.getSelectedIndex();
			String command = "";
			switch (pane)
			{
				case 0:
					command = "people.groupSelect";
					break;
				case 2:
					command = "requests.groupChanged";
					break;
				case 3:
					command = "assignments.groupChanged";
					updateSumsTable();
					break;
			}
			if (!command.equals(""))
			{
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
		editUndo.setEnabled(false);
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
		editRedo.setEnabled(false);
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

		addWindowListener(new WindowAdapter()
		{
			boolean alreadyOpen = false;

			private int confirm()
			{
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
			public void windowClosing(WindowEvent e)
			{
				switch (confirm())
				{
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

		updateNewPersonGroupComboBoxItems();

		addPersonButton.addActionListener(this);
		addPersonButton.setActionCommand("people.add");

		standsList.setModel(project.getStands());
		standsList.setCellRenderer(new StandRenderer());
		standsList.addListSelectionListener(e ->
		{
			if (standsList.getSelectedIndex() < 0)
			{
				editStandNameTextField.setText("");
				chooseEditColorButton.setForeground(Color.BLACK);
			}
			else
			{
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
		requestsTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer()
		{
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col)
			{

				super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

				if (value instanceof Stand)
				{
					setBackground(((Stand) value).getColor());
					setForeground(blackOrWhite(getBackground()));
				}
				else
				{
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
		assignmentsTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer()
		{
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col)
			{

				super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

				if (value instanceof Stand)
				{
					setBackground(((Stand) value).getColor());
					setForeground(blackOrWhite(getBackground()));
				}
				else
				{
					setBackground(table.getBackground());
					setForeground(table.getForeground());
				}
				return this;
			}
		});
		assignmentsTable.getSelectionModel().addListSelectionListener(e ->
		{
			try
			{
				Group group = (Group) assignmentsGroupComboBox.getSelectedItem();
				if (group != null)
				{
					int row = assignmentsTable.getSelectedRow();
					Vector<Person> people = project.getMembersOfGroup(group.getName());
					if (people != null && row > -1)
					{
						Person person = people.get(row);
						if (person != null)
						{
							assignmentsPersonNameLabel.setText(String.format("%s: %s", Strings.get("requests.label"), person.getName()));
							StandListModel stands = project.getStands().clone();
							stands.sort(person);
							assignmentsPersonRequestsList.setModel(stands);
						}
					}
					else if (people != null && row == -1)
					{
						assignmentsPersonNameLabel.setText("");
						assignmentsPersonRequestsList.setModel(new DefaultListModel<>());
					}
				}
			}
			catch (ClassCastException ex)
			{
				TaskDialogs.showException(ex);
			}
		});
		assignmentsPersonRequestsList.setCellRenderer(new StandRenderer());

		assignmentsExportButton.addActionListener(this);
		assignmentsExportButton.setActionCommand("exports.assignments.assignments");

		listsExportButton.addActionListener(this);
		listsExportButton.setActionCommand("exports.assignments.lists");

		presenceExportButton.addActionListener(this);
		presenceExportButton.setActionCommand("exports.assignments.presence");

		actionPerformed(new ActionEvent(this, 0, "people.groupSelect"));

		sumsTable.setDefaultRenderer(Stand.class, new DefaultTableCellRenderer()
		{
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
			{
				Stand stand = (Stand) value;
				Component c = super.getTableCellRendererComponent(table, stand.getName(), isSelected, hasFocus, row, column);
				c.setBackground(stand.getColor());
				c.setForeground(blackOrWhite(stand.getColor()));
				return c;
			}
		});
		sumsTable.setDefaultRenderer(String.class, new DefaultTableCellRenderer()
		{
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
			{
				Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				if (isSelected)
				{
					Stand stand = project.getStands().getElementAt(row);
					c.setBackground(stand.getColor());
					c.setForeground(blackOrWhite(stand.getColor()));
				}
				else
				{
					c.setBackground(sumsTable.getBackground());
					c.setForeground(sumsTable.getForeground());
				}
				return c;
			}
		});
		sumsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		sumsTable.setGridColor(Color.BLACK);

		pack();
	}

	private void updateSumsTable()
	{
		SumsTableModel model = new SumsTableModel(project);
		sumsTable.setModel(model);
		sumsStatusLabel.setText(Strings.get(model.isAllAssigned() ? "sums.status.allAssigned" : "sums.status.missing"));
	}

	public static Color blackOrWhite(@NotNull Color background)
	{
		int red = background.getRed();
		int green = background.getGreen();
		int blue = background.getBlue();

		return (red * 0.299 + green * 0.587 + blue * 0.114) > 186 ? Color.BLACK : Color.WHITE;
	}

	private void updateNewPersonGroupComboBoxItems()
	{
		Vector<String> groupNamesList = new Vector<>();
		project.getGroups().forEach(group -> groupNamesList.add(group.getName()));
		newPersonGroupComboBox.setModel(new DefaultComboBoxModel<>(groupNamesList));
	}

	@Override
	@SuppressWarnings({"SwitchStatementWithTooFewBranches"})
	public void actionPerformed(ActionEvent e)
	{
		String command = e.getActionCommand();
		String[] subcommands = command.split("\\.");
		switch (subcommands[0])
		{
			case "toolbar":
				switch (subcommands[1])
				{
					case "file":
						switch (subcommands[2])
						{
							case "new":
								new MainWindow(new Project()).setVisible(true);
							case "open":
								Project loadedProject = Project.loadFromFile(this, false);
								if (loadedProject != null)
								{
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
						switch (subcommands[2])
						{
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
				switch (subcommands[1])
				{
					case "import":
						importPeople();
						updateNewPersonGroupComboBoxItems();
						break;
					case "groupSelect":
						Group group = (Group) peopleGroupsComboBox.getSelectedItem();
						peopleOfGroupList.setModel(Objects.requireNonNullElseGet(group, DefaultListModel::new));
						break;
					case "remove":
						Person person = peopleOfGroupList.getSelectedValue();
						if (person != null)
						{
							project.removePerson(person.getName(), person.getGroup().getName());
							Vector<Person> membersOfGroup = project.getMembersOfGroup(person.getGroup().getName());
							if (membersOfGroup != null && membersOfGroup.size() == 0)
							{
								project.removeGroup(person.getGroup().getName());
								if (peopleGroupsComboBox.getSelectedIndex() > 0)
									peopleGroupsComboBox.setSelectedIndex(peopleGroupsComboBox.getSelectedIndex() - 1);
							}
						}
						break;
					case "add":
						String newPersonGroup = (String) newPersonGroupComboBox.getSelectedItem();
						if (newPersonGroup != null)
						{
							if (!newPersonNameTextField.getText().isEmpty() && !newPersonGroup.isEmpty())
							{
								project.addPerson(newPersonNameTextField.getText(), newPersonGroup);
							}
						}
						updateNewPersonGroupComboBoxItems();
						break;
				}
				break;
			case "stands":
				switch (subcommands[1])
				{
					case "new":
						switch (subcommands[2])
						{
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
						switch (subcommands[2])
						{
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
				switch (subcommands[1])
				{
					case "groupChanged":
						Group group = (Group) requestsGroupComboBox.getSelectedItem();
						if (group != null)
						{
							GroupStandsTableModel model = new GroupStandsTableModel(group, RequestsLine.REQUEST_LINE);
							requestsTable.setModel(model);
							TableColumnModel tableColumnModel = requestsTable.getColumnModel();
							for (int i = 1; i < model.getColumnCount(); i++)
							{
								JComboBox<Stand> box = new JComboBox<>(new StandComboBoxModel(project, false));
								box.setOpaque(true);
								box.addActionListener(evt ->
								{
									Stand stand = (Stand) box.getSelectedItem();
									if (stand != null) box.setBackground(stand.getColor());
								});
								box.setRenderer((list, value, index, isSelected, cellHasFocus) ->
								{
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
				switch (subcommands[1])
				{
					case "groupChanged":
						Group group = (Group) assignmentsGroupComboBox.getSelectedItem();
						if (group != null)
						{
							GroupStandsTableModel model = new GroupStandsTableModel(group, RequestsLine.ASSIGNMENT_LINE);
							assignmentsTable.setModel(model);
							TableColumnModel tableColumnModel = assignmentsTable.getColumnModel();
							for (int i = 1; i < model.getColumnCount(); i++)
							{
								JComboBox<Stand> box = new JComboBox<>(new StandComboBoxModel(project, true));
								box.setOpaque(true);
								box.addActionListener(evt ->
								{
									Stand stand = (Stand) box.getSelectedItem();
									if (stand != null) box.setBackground(stand.getColor());
									updateSumsTable();
								});
								box.setRenderer((list, value, index, isSelected, cellHasFocus) ->
								{
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
				switch (subcommands[1])
				{
					case "requests":
						switch (subcommands[2])
						{
							case "run":
								RequestsExport export = new RequestsExport(project);
								export.generate();
								export.save(this);
								break;
						}
						break;
					case "assignments":
						switch (subcommands[2])
						{
							case "assignments":
							{
								AssignmentsExport export = new AssignmentsExport(project);
								export.generate();
								export.save(this);
							}
							break;
							case "lists":
							{
								SingleListExport export = new SingleListExport(project);
								export.generate();
								export.save(this);
							}
							break;
							case "presence":
								PresenceExport export = new PresenceExport(project);
								export.generate();
								export.save(this);
								break;
						}
						break;
				}
				break;
		}
	}

	private void importPeople()
	{
		JFileChooser c = new JFileChooser();
		String xmlExt = "xml";
		c.addChoosableFileFilter(new FileNameExtensionFilter(Strings.get("people.import.fileType.desc"), xmlExt));
		c.setAcceptAllFileFilterUsed(false);
		int rVal = c.showOpenDialog(this);

		if (rVal == JFileChooser.APPROVE_OPTION)
		{
			File file = c.getSelectedFile();

			if (file.getName().endsWith(xmlExt))
			{
				try
				{
					// is Bakalari?
					if (XmlValidation.validateAgainstXSD(new FileInputStream(file), this.getClass().getResourceAsStream("bakalari.xsd")))
					{
						JAXBContext jaxbContext = JAXBContext.newInstance(Seznam.class, Student.class);
						Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

						Seznam seznam = (Seznam) jaxbUnmarshaller.unmarshal(file);

						for (Student student : seznam.getStudent())
							project.addPerson(student.getJmeno(), student.getTrida());
					}
					else JOptionPane.showMessageDialog(this,
							Strings.get("people.import.unknownFormat.message"),
							Strings.get("people.import.unknownFormat.title"),
							JOptionPane.WARNING_MESSAGE);
				}
				catch (FileNotFoundException e)
				{
					JOptionPane.showMessageDialog(this,
							Strings.get("people.import.fileNotFound.message"),
							Strings.get("people.import.fileNotFound.title"),
							JOptionPane.WARNING_MESSAGE
					);
				}
				catch (Exception e)
				{
					TaskDialogs.showException(e);
					log.warning(e.getMessage());
				}
			}
		}
	}

	{
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
		$$$setupUI$$$();
	}

	/**
	 * Method generated by IntelliJ IDEA GUI Designer
	 * >>> IMPORTANT!! <<<
	 * DO NOT edit this method OR call it in your code!
	 *
	 * @noinspection ALL
	 */
	private void $$$setupUI$$$()
	{
		root = new JPanel();
		root.setLayout(new BorderLayout(0, 0));
		tabbedPane = new JTabbedPane();
		root.add(tabbedPane, BorderLayout.CENTER);
		final JPanel panel1 = new JPanel();
		panel1.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
		tabbedPane.addTab(ResourceBundle.getBundle("strings").getString("people.label"), panel1);
		final JPanel panel2 = new JPanel();
		panel2.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
		panel1.add(panel2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		peopleGroupsComboBox = new JComboBox();
		peopleGroupsComboBox.setToolTipText(ResourceBundle.getBundle("strings").getString("people.groups.tooltip"));
		panel2.add(peopleGroupsComboBox, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JScrollPane scrollPane1 = new JScrollPane();
		panel2.add(scrollPane1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(150, 50), new Dimension(150, 50), null, 0, false));
		peopleOfGroupList = new JList();
		scrollPane1.setViewportView(peopleOfGroupList);
		final JPanel panel3 = new JPanel();
		panel3.setLayout(new FormLayout("fill:d:grow,left:4dlu:noGrow,fill:d:grow", "center:d:noGrow,top:3dlu:noGrow,center:max(d;4px):noGrow,top:3dlu:noGrow,center:max(d;4px):noGrow,top:3dlu:noGrow,center:max(d;4px):noGrow,top:3dlu:noGrow,center:max(d;4px):noGrow,top:3dlu:noGrow,center:max(d;4px):noGrow,top:3dlu:noGrow,center:max(d;4px):noGrow,top:3dlu:noGrow,center:max(d;4px):noGrow"));
		panel1.add(panel3, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		peopleImportButton = new JButton();
		this.$$$loadButtonText$$$(peopleImportButton, ResourceBundle.getBundle("strings").getString("people.import"));
		peopleImportButton.setToolTipText(ResourceBundle.getBundle("strings").getString("people.import.tooltip"));
		CellConstraints cc = new CellConstraints();
		panel3.add(peopleImportButton, cc.xy(3, 1));
		removePersonButton = new JButton();
		this.$$$loadButtonText$$$(removePersonButton, ResourceBundle.getBundle("strings").getString("people.remove"));
		removePersonButton.setToolTipText(ResourceBundle.getBundle("strings").getString("people.remove.tooltip"));
		panel3.add(removePersonButton, cc.xy(3, 3));
		final JSeparator separator1 = new JSeparator();
		panel3.add(separator1, cc.xyw(1, 5, 3, CellConstraints.FILL, CellConstraints.FILL));
		final JLabel label1 = new JLabel();
		Font label1Font = this.$$$getFont$$$(null, Font.BOLD, -1, label1.getFont());
		if (label1Font != null) label1.setFont(label1Font);
		this.$$$loadLabelText$$$(label1, ResourceBundle.getBundle("strings").getString("people.create.header"));
		panel3.add(label1, cc.xyw(1, 7, 3));
		newPersonNameTextField = new JTextField();
		panel3.add(newPersonNameTextField, cc.xy(3, 9, CellConstraints.FILL, CellConstraints.DEFAULT));
		newPersonGroupComboBox = new JComboBox();
		newPersonGroupComboBox.setEditable(true);
		panel3.add(newPersonGroupComboBox, cc.xy(3, 11));
		final JLabel label2 = new JLabel();
		this.$$$loadLabelText$$$(label2, ResourceBundle.getBundle("strings").getString("people.create.name"));
		panel3.add(label2, cc.xy(1, 9));
		final JLabel label3 = new JLabel();
		this.$$$loadLabelText$$$(label3, ResourceBundle.getBundle("strings").getString("people.create.group"));
		panel3.add(label3, cc.xy(1, 11));
		addPersonButton = new JButton();
		this.$$$loadButtonText$$$(addPersonButton, ResourceBundle.getBundle("strings").getString("people.create.add"));
		panel3.add(addPersonButton, cc.xy(3, 13));
		final JPanel panel4 = new JPanel();
		panel4.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
		tabbedPane.addTab(ResourceBundle.getBundle("strings").getString("stands.label"), panel4);
		final JPanel panel5 = new JPanel();
		panel5.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
		panel4.add(panel5, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		final JScrollPane scrollPane2 = new JScrollPane();
		panel5.add(scrollPane2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
		standsList = new JList();
		standsList.setSelectionMode(0);
		scrollPane2.setViewportView(standsList);
		final JPanel panel6 = new JPanel();
		panel6.setLayout(new FormLayout("fill:d:noGrow,left:4dlu:noGrow,fill:d:grow", "center:d:noGrow,top:3dlu:noGrow,center:max(d;4px):noGrow,top:3dlu:noGrow,center:max(d;4px):noGrow,top:3dlu:noGrow,center:32px:noGrow,top:3dlu:noGrow,center:d:noGrow,top:3dlu:noGrow,center:max(d;4px):noGrow,top:3dlu:noGrow,center:max(d;4px):noGrow,top:3dlu:noGrow,center:max(d;4px):noGrow,top:3dlu:noGrow,center:max(d;4px):noGrow,top:3dlu:noGrow,center:31px:noGrow,top:77dlu:grow,top:3dlu:noGrow,top:3dlu:noGrow,center:d:noGrow,center:max(d;4px):noGrow,center:max(d;4px):noGrow"));
		panel4.add(panel6, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		final JLabel label4 = new JLabel();
		Font label4Font = this.$$$getFont$$$(null, Font.BOLD, -1, label4.getFont());
		if (label4Font != null) label4.setFont(label4Font);
		this.$$$loadLabelText$$$(label4, ResourceBundle.getBundle("strings").getString("stands.new.label"));
		panel6.add(label4, cc.xyw(1, 1, 3));
		final JLabel label5 = new JLabel();
		this.$$$loadLabelText$$$(label5, ResourceBundle.getBundle("strings").getString("stands.name.label"));
		panel6.add(label5, cc.xy(1, 3));
		newStandNameTextField = new JTextField();
		newStandNameTextField.setToolTipText(ResourceBundle.getBundle("strings").getString("stands.new.name.tooltip"));
		panel6.add(newStandNameTextField, cc.xy(3, 3, CellConstraints.FILL, CellConstraints.DEFAULT));
		final JLabel label6 = new JLabel();
		Font label6Font = this.$$$getFont$$$(null, Font.BOLD, -1, label6.getFont());
		if (label6Font != null) label6.setFont(label6Font);
		this.$$$loadLabelText$$$(label6, ResourceBundle.getBundle("strings").getString("stands.edit.label"));
		panel6.add(label6, cc.xyw(1, 11, 3));
		final JLabel label7 = new JLabel();
		this.$$$loadLabelText$$$(label7, ResourceBundle.getBundle("strings").getString("stands.name.label"));
		panel6.add(label7, cc.xy(1, 13));
		editStandNameTextField = new JTextField();
		editStandNameTextField.setToolTipText(ResourceBundle.getBundle("strings").getString("stands.edit.name.tooltip"));
		panel6.add(editStandNameTextField, cc.xy(3, 13, CellConstraints.FILL, CellConstraints.DEFAULT));
		removeStandButton = new JButton();
		this.$$$loadButtonText$$$(removeStandButton, ResourceBundle.getBundle("strings").getString("stands.delete"));
		panel6.add(removeStandButton, cc.xy(3, 19));
		chooseNewColorButton = new JButton();
		this.$$$loadButtonText$$$(chooseNewColorButton, ResourceBundle.getBundle("strings").getString("stands.chooseColor.button"));
		panel6.add(chooseNewColorButton, cc.xy(1, 7));
		addStandButton = new JButton();
		this.$$$loadButtonText$$$(addStandButton, ResourceBundle.getBundle("strings").getString("stands.new"));
		panel6.add(addStandButton, cc.xy(3, 7));
		chooseEditColorButton = new JButton();
		this.$$$loadButtonText$$$(chooseEditColorButton, ResourceBundle.getBundle("strings").getString("stands.chooseColor.button"));
		panel6.add(chooseEditColorButton, cc.xy(1, 17));
		saveStandButton = new JButton();
		this.$$$loadButtonText$$$(saveStandButton, ResourceBundle.getBundle("strings").getString("stands.edit"));
		panel6.add(saveStandButton, cc.xy(3, 17));
		final JLabel label8 = new JLabel();
		Font label8Font = this.$$$getFont$$$(null, Font.BOLD, -1, label8.getFont());
		if (label8Font != null) label8.setFont(label8Font);
		this.$$$loadLabelText$$$(label8, ResourceBundle.getBundle("strings").getString("stands.blockCount.label"));
		panel6.add(label8, cc.xy(1, 25));
		blockCountSpinner = new JSpinner();
		blockCountSpinner.setToolTipText(ResourceBundle.getBundle("strings").getString("stands.blockCount.tooltip"));
		panel6.add(blockCountSpinner, cc.xy(3, 25, CellConstraints.FILL, CellConstraints.DEFAULT));
		final JLabel label9 = new JLabel();
		this.$$$loadLabelText$$$(label9, ResourceBundle.getBundle("strings").getString("stands.limit"));
		panel6.add(label9, cc.xy(1, 5));
		newStandLimitSpinner = new JSpinner();
		newStandLimitSpinner.setToolTipText(ResourceBundle.getBundle("strings").getString("stands.limit.tooltip"));
		panel6.add(newStandLimitSpinner, cc.xy(3, 5, CellConstraints.FILL, CellConstraints.DEFAULT));
		final JLabel label10 = new JLabel();
		this.$$$loadLabelText$$$(label10, ResourceBundle.getBundle("strings").getString("stands.limit"));
		panel6.add(label10, cc.xy(1, 15));
		editStandLimitSpinner = new JSpinner();
		editStandLimitSpinner.setToolTipText(ResourceBundle.getBundle("strings").getString("stands.limit.tooltip"));
		panel6.add(editStandLimitSpinner, cc.xy(3, 15, CellConstraints.FILL, CellConstraints.DEFAULT));
		final JSeparator separator2 = new JSeparator();
		panel6.add(separator2, cc.xyw(1, 9, 3, CellConstraints.FILL, CellConstraints.FILL));
		final JSeparator separator3 = new JSeparator();
		panel6.add(separator3, cc.xyw(1, 23, 3, CellConstraints.FILL, CellConstraints.FILL));
		final JPanel panel7 = new JPanel();
		panel7.setLayout(new BorderLayout(0, 0));
		tabbedPane.addTab(ResourceBundle.getBundle("strings").getString("requests.label"), panel7);
		final JPanel panel8 = new JPanel();
		panel8.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		panel7.add(panel8, BorderLayout.NORTH);
		requestsGroupComboBox = new JComboBox();
		panel8.add(requestsGroupComboBox);
		requestsImportButton = new JButton();
		requestsImportButton.setHideActionText(false);
		this.$$$loadButtonText$$$(requestsImportButton, ResourceBundle.getBundle("strings").getString("requests.importRequests"));
		requestsImportButton.setVisible(false);
		panel8.add(requestsImportButton);
		exportRequestChartsButton = new JButton();
		this.$$$loadButtonText$$$(exportRequestChartsButton, ResourceBundle.getBundle("strings").getString("requests.createRequestExcel"));
		panel8.add(exportRequestChartsButton);
		final JScrollPane scrollPane3 = new JScrollPane();
		panel7.add(scrollPane3, BorderLayout.CENTER);
		requestsTable = new JTable();
		requestsTable.setEnabled(true);
		requestsTable.setFillsViewportHeight(false);
		scrollPane3.setViewportView(requestsTable);
		final JPanel panel9 = new JPanel();
		panel9.setLayout(new BorderLayout(0, 0));
		tabbedPane.addTab(ResourceBundle.getBundle("strings").getString("assignments.label"), panel9);
		final JPanel panel10 = new JPanel();
		panel10.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		panel9.add(panel10, BorderLayout.NORTH);
		assignmentsGroupComboBox = new JComboBox();
		panel10.add(assignmentsGroupComboBox);
		assignmentsExportButton = new JButton();
		this.$$$loadButtonText$$$(assignmentsExportButton, ResourceBundle.getBundle("strings").getString("assignments.export.assignments"));
		panel10.add(assignmentsExportButton);
		listsExportButton = new JButton();
		this.$$$loadButtonText$$$(listsExportButton, ResourceBundle.getBundle("strings").getString("assignments.export.singleList"));
		listsExportButton.setToolTipText(ResourceBundle.getBundle("strings").getString("assignments.export.singleList.tooltip"));
		panel10.add(listsExportButton);
		presenceExportButton = new JButton();
		this.$$$loadButtonText$$$(presenceExportButton, ResourceBundle.getBundle("strings").getString("assignments.export.presenceLists"));
		presenceExportButton.setToolTipText(ResourceBundle.getBundle("strings").getString("assignments.export.presenceLists.tooltip"));
		panel10.add(presenceExportButton);
		final JPanel panel11 = new JPanel();
		panel11.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
		panel9.add(panel11, BorderLayout.CENTER);
		final JScrollPane scrollPane4 = new JScrollPane();
		panel11.add(scrollPane4, new GridConstraints(0, 0, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		assignmentsTable = new JTable();
		scrollPane4.setViewportView(assignmentsTable);
		final JPanel panel12 = new JPanel();
		panel12.setLayout(new BorderLayout(0, 0));
		panel11.add(panel12, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		assignmentsPersonNameLabel = new JLabel();
		Font assignmentsPersonNameLabelFont = this.$$$getFont$$$(null, Font.BOLD, -1, assignmentsPersonNameLabel.getFont());
		if (assignmentsPersonNameLabelFont != null) assignmentsPersonNameLabel.setFont(assignmentsPersonNameLabelFont);
		assignmentsPersonNameLabel.setText("");
		panel12.add(assignmentsPersonNameLabel, BorderLayout.NORTH);
		final JScrollPane scrollPane5 = new JScrollPane();
		panel12.add(scrollPane5, BorderLayout.CENTER);
		assignmentsPersonRequestsList = new JList();
		scrollPane5.setViewportView(assignmentsPersonRequestsList);
		final JPanel panel13 = new JPanel();
		panel13.setLayout(new BorderLayout(0, 0));
		panel11.add(panel13, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		final JPanel panel14 = new JPanel();
		panel14.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		panel13.add(panel14, BorderLayout.SOUTH);
		final JLabel label11 = new JLabel();
		this.$$$loadLabelText$$$(label11, ResourceBundle.getBundle("strings").getString("sums.status"));
		panel14.add(label11);
		sumsStatusLabel = new JLabel();
		sumsStatusLabel.setText("");
		panel14.add(sumsStatusLabel);
		final JScrollPane scrollPane6 = new JScrollPane();
		panel13.add(scrollPane6, BorderLayout.CENTER);
		sumsTable = new JTable();
		scrollPane6.setViewportView(sumsTable);
	}

	/**
	 * @noinspection ALL
	 */
	private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont)
	{
		if (currentFont == null) return null;
		String resultName;
		if (fontName == null)
		{
			resultName = currentFont.getName();
		}
		else
		{
			Font testFont = new Font(fontName, Font.PLAIN, 10);
			if (testFont.canDisplay('a') && testFont.canDisplay('1'))
			{
				resultName = fontName;
			}
			else
			{
				resultName = currentFont.getName();
			}
		}
		return new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
	}

	/**
	 * @noinspection ALL
	 */
	private void $$$loadLabelText$$$(JLabel component, String text)
	{
		StringBuffer result = new StringBuffer();
		boolean haveMnemonic = false;
		char mnemonic = '\0';
		int mnemonicIndex = -1;
		for (int i = 0; i < text.length(); i++)
		{
			if (text.charAt(i) == '&')
			{
				i++;
				if (i == text.length()) break;
				if (!haveMnemonic && text.charAt(i) != '&')
				{
					haveMnemonic = true;
					mnemonic = text.charAt(i);
					mnemonicIndex = result.length();
				}
			}
			result.append(text.charAt(i));
		}
		component.setText(result.toString());
		if (haveMnemonic)
		{
			component.setDisplayedMnemonic(mnemonic);
			component.setDisplayedMnemonicIndex(mnemonicIndex);
		}
	}

	/**
	 * @noinspection ALL
	 */
	private void $$$loadButtonText$$$(AbstractButton component, String text)
	{
		StringBuffer result = new StringBuffer();
		boolean haveMnemonic = false;
		char mnemonic = '\0';
		int mnemonicIndex = -1;
		for (int i = 0; i < text.length(); i++)
		{
			if (text.charAt(i) == '&')
			{
				i++;
				if (i == text.length()) break;
				if (!haveMnemonic && text.charAt(i) != '&')
				{
					haveMnemonic = true;
					mnemonic = text.charAt(i);
					mnemonicIndex = result.length();
				}
			}
			result.append(text.charAt(i));
		}
		component.setText(result.toString());
		if (haveMnemonic)
		{
			component.setMnemonic(mnemonic);
			component.setDisplayedMnemonicIndex(mnemonicIndex);
		}
	}

	/**
	 * @noinspection ALL
	 */
	public JComponent $$$getRootComponent$$$()
	{
		return root;
	}

}
