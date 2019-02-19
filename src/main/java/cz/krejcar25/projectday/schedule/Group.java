package cz.krejcar25.projectday.schedule;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.Vector;

@XmlRootElement(name = "group")
public class Group implements Serializable, ListModel<Person> {
    @XmlTransient
    public Project project;
    @XmlElementWrapper(name = "people", required = true)
    @XmlElement(name = "person", required = true)
    private Vector<Person> people;
    @XmlAttribute(name = "name", required = true)
    private String name;
    private Vector<ListDataListener> listDataListeners;

    public Group() {
        this.listDataListeners = new Vector<>();
    }

    public Group(String name, Project project) {
        this.name = name;
        this.people = new Vector<>();
        this.listDataListeners = new Vector<>();
        this.project = project;
    }

    public Vector<Person> getPeople() {
        return people;
    }

    void addPerson(Person person) {
        for (Person p : people) if (p.getName().equals(person.getName())) return;
        person.setGroup(this);
        this.people.add(person);
        //noinspection Duplicates
        for (ListDataListener l : listDataListeners) {
            l.intervalAdded(new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, people.size() - 1, people.size() - 1));
            l.contentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, people.size() - 1, people.size() - 1));
        }
    }

    void removePerson(String name) {
        for (int i = people.size() - 1; i >= 0; i--) if (people.get(i).getName().equals(name)) people.remove(i);
        //noinspection Duplicates
        for (ListDataListener l : listDataListeners) {
            l.intervalRemoved(new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, people.size(), people.size()));
            l.contentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, people.size(), people.size()));
        }
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    // ListModel overloads

    @Override
    public int getSize() {
        return people.size();
    }

    @Override
    public Person getElementAt(int index) {
        return people.get(index);
    }

    @Override
    public void addListDataListener(ListDataListener l) {
        listDataListeners.add(l);
    }

    @Override
    public void removeListDataListener(ListDataListener l) {
        listDataListeners.remove(l);
    }

    public Project getProject() {
        return project;
    }

    @XmlTransient
    public void setProject(Project project) {
        this.project = project;
        for (Person person : people) person.setGroup(this);
    }
}
