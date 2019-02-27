package cz.krejcar25.projectday.schedule;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.Vector;

@XmlRootElement(name = "person")
public class Person implements Serializable
{
	@XmlAttribute(name = "name")
	private String name;
	private Group group;
	@XmlElementWrapper(name = "requests", required = true)
	@XmlElement(name = "pair")
	private Vector<StandValue> requests;
	@XmlElementWrapper(name = "assignments", required = true)
	@XmlElement(name = "pair")
	private Vector<StandValue> assignments;

	@SuppressWarnings("unused")
	public Person()
	{
		this.requests = new Vector<>();
		this.assignments = new Vector<>();
	}

	Person(String name, Group group)
	{
		this.name = name;
		this.group = group;
		this.requests = new Vector<>();
		this.assignments = new Vector<>();
	}

	public String getName()
	{
		return name;
	}

	public Group getGroup()
	{
		return group;
	}

	@XmlTransient
	public void setGroup(Group group)
	{
		this.group = group;
	}

	int getRequestForStand(String standName)
	{
		for (StandValue standValue : requests) if (standValue.standName.equals(standName)) return standValue.value;
		StandValue standValue = new StandValue();
		standValue.standName = standName;
		for (int i = 0; i < getGroup().project.getStands().getSize(); i++)
			if (getGroup().project.getStands().getElementAt(i).getName().equals(standName))
				standValue.value = i;
		requests.add(standValue);
		return getRequestForStand(standName);
	}

	void setRequestForStand(String standName, int value)
	{
		for (StandValue standValue : requests)
			if (standValue.standName.equals(standName))
			{
				standValue.value = value;
				return;
			}
		StandValue standValue = new StandValue();
		standValue.standName = standName;
		standValue.value = value;
		requests.add(standValue);
	}

	public String getAssignmentForBlock(int index)
	{
		for (StandValue standValue : assignments) if (standValue.value == index) return standValue.standName;
		StandValue standValue = new StandValue();
		standValue.standName = Stand.EMPTY.getName();
		standValue.value = index;
		assignments.add(standValue);
		return getAssignmentForBlock(index);
	}

	void setAssignmentForBlock(int index, String standName)
	{
		for (StandValue standValue : assignments)
			if (standValue.value == index)
			{
				standValue.standName = standName;
				return;
			}
		StandValue standValue = new StandValue();
		standValue.standName = standName;
		standValue.value = index;
		assignments.add(standValue);
	}

	@Override
	public String toString()
	{
		return String.format("%s (%s)", name, group);
	}

	void renameRequest(String oldName, String newName)
	{
		for (StandValue standValue : requests) if (standValue.standName.equals(oldName)) standValue.standName = newName;
	}
}
