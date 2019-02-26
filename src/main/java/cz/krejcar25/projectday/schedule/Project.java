package cz.krejcar25.projectday.schedule;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.oxbow.swingbits.dialog.task.TaskDialogs;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.undo.UndoManager;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.*;
import java.security.MessageDigest;
import java.util.Enumeration;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

@XmlRootElement(name = "project")
public class Project
{
	private static final String FILE_TYPE = "pdproj";
	private final transient UndoManager undoManager;
	private transient File lastSaveLocation;
	private transient MainWindow window;
	@XmlElementWrapper(name = "groups", required = true)
	@XmlElement(name = "group", required = true)
	private Vector<Group> groups;
	@XmlElement(name = "StandListModel", required = true)
	private StandListModel stands;
	private int blockCount;

	public Project()
	{
		this.undoManager = new UndoManager();
		this.undoManager.setLimit(100);
	}

	public Project(MainWindow window)
	{
		this.undoManager = new UndoManager();
		this.undoManager.setLimit(100);
		this.window = window;
		this.groups = new Vector<>();
		this.stands = new StandListModel();
		this.blockCount = 4;
	}

	@Nullable
	static Project loadFromFile(MainWindow window, boolean returnNewOnCancel)
	{
		File file = openFileChooser(window);
		if (file == null) return returnNewOnCancel ? new Project(window) : null;
		try
		{
			Project project = loadFromXml(new FileInputStream(file), Project.class.getResourceAsStream("project.xsd"));
			if (project == null)
			{
				ZipFile zip = new ZipFile(file);
				Enumeration<? extends ZipEntry> entries = zip.entries();
				while (entries.hasMoreElements())
				{
					ZipEntry entry = entries.nextElement();
					if ("project.xml".equals(entry.getName()))
					{
						project = loadFromXml(zip.getInputStream(entry), Project.class.getResourceAsStream("project.xsd"));
						return project;
					}
				}
			}
			else
			{
				project.lastSaveLocation = file;
				return project;
			}
		}
		catch (Exception e)
		{
			TaskDialogs.showException(e);
		}
		return null;
	}

	@Nullable
	private static Project loadFromXml(InputStream xml, InputStream xsd)
	{
		try
		{
			byte[] xmlB = xml.readAllBytes();
			if (XmlValidation.validateAgainstXSD(new ByteArrayInputStream(xmlB), xsd))
			{
				JAXBContext context = JAXBContext.newInstance(Project.class, Group.class, Person.class, StandListModel.class, Stand.class);
				Unmarshaller unmarshaller = context.createUnmarshaller();
				Project project = (Project) unmarshaller.unmarshal(new ByteArrayInputStream(xmlB));
				for (Group group : project.groups) group.setProject(project);
				return project;
			}
		}
		catch (Exception ex)
		{
			TaskDialogs.showException(ex);
		}
		return null;
	}

	@Nullable
	private static File saveFileChooser(JFrame frame)
	{
		JFileChooser c = createChooser();
		int rVal = c.showSaveDialog(frame);

		if (rVal == JFileChooser.APPROVE_OPTION)
		{
			File file = c.getSelectedFile();
			if (!file.getName().endsWith(FILE_TYPE)) return new File(file.getAbsolutePath() + "." + FILE_TYPE);
			else return file;
		}
		else return null;
	}

	@Nullable
	private static File openFileChooser(JFrame frame)
	{
		JFileChooser c = createChooser();
		int rVal = c.showOpenDialog(frame);

		if (rVal == JFileChooser.APPROVE_OPTION) return c.getSelectedFile();
		else return null;
	}

	private static JFileChooser createChooser()
	{
		JFileChooser c = new JFileChooser();
		c.addChoosableFileFilter(new FileNameExtensionFilter(Strings.get("fileType.desc"), FILE_TYPE));
		c.setAcceptAllFileFilterUsed(false);
		return c;
	}

	public Vector<Person> getAllPeople()
	{
		Vector<Person> all = new Vector<>();
		for (Group group : groups) all.addAll(group.getPeople());
		return all;
	}

	public Vector<Group> getGroups()
	{
		return groups;
	}

	@Nullable
	private Group getGroup(String name)
	{
		for (Group group : groups) if (group.getName().equals(name)) return group;
		return null;
	}

	private boolean groupExists(String name)
	{
		for (Group group : groups) if (group.getName().equals(name)) return true;
		return false;
	}

	private void addGroup(Group group)
	{
		if (groupExists(group.getName()))
		{
			for (Person person : group.getPeople()) addPerson(person.getName(), person.getGroup().getName());
		}
		else groups.addElement(group);
	}

	@Nullable
	public Vector<Person> getMembersOfGroup(String groupName)
	{
		if (groupExists(groupName))
		{
			Group group = getGroup(groupName);
			if (group == null) return null;
			else return group.getPeople();
		}
		else return null;
	}

	private void addPerson(@NotNull Person newPerson)
	{
		if (groupExists(newPerson.getGroup().getName()))
		{
			Group group = getGroup(newPerson.getGroup().getName());
			if (group != null) group.addPerson(newPerson);
		}
		else groups.addElement(newPerson.getGroup());
	}

	void addPerson(String name, String group)
	{
		if (groupExists(group))
		{
			addPerson(new Person(name, getGroup(group)));
		}
		else
		{
			addGroup(new Group(group, this));
			addPerson(name, group);
		}
	}

	public void removePerson(String name, String group)
	{
		if (groupExists(group))
		{
			Group g = getGroup(group);
			if (g != null) g.removePerson(name);
		}
	}

	public StandListModel getStands()
	{
		return stands;
	}

	void addStand(Stand stand)
	{
		stands.addStand(stand);
	}

	void removeStand(Stand stand)
	{
		stands.removeStand(stand);
	}

	public int getBlockCount()
	{
		return blockCount;
	}

	@XmlAttribute(name = "blockCount", required = true)
	void setBlockCount(int blockCount)
	{
		this.blockCount = blockCount;
	}

	void save()
	{
		if (lastSaveLocation == null) saveAs(saveFileChooser(window));
		else saveAs(lastSaveLocation);
	}

	void saveAs()
	{
		saveAs(saveFileChooser(window));
	}

	private void saveAs(File file)
	{
		try (ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(file)))
		{
			JAXBContext context = JAXBContext.newInstance(Project.class, Group.class, Person.class, StandListModel.class, Stand.class);
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			ZipEntry project_xml = new ZipEntry("project.xml");
			ByteArrayOutputStream xmlByteStream = new ByteArrayOutputStream();
			marshaller.marshal(this, xmlByteStream);
			byte[] xmlBytes = xmlByteStream.toByteArray();
			zip.putNextEntry(project_xml);
			new ByteArrayInputStream(xmlBytes).transferTo(zip);
			zip.closeEntry();
			MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
			byte[] checksum = sha256.digest(xmlBytes);
			zip.putNextEntry(new ZipEntry("checksum.sha256"));
			new ByteArrayInputStream(checksum).transferTo(zip);
			zip.closeEntry();

			lastSaveLocation = file;
		}
		catch (Exception e)
		{
			TaskDialogs.showException(e);
		}
	}

	void setWindow(MainWindow window)
	{
		this.window = window;
	}

	void removeGroup(String name)
	{
		Group group = getGroup(name);
		if (group != null && group.getPeople().size() == 0) groups.remove(group);
	}
}
