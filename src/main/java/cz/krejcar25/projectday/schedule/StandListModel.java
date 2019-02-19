package cz.krejcar25.projectday.schedule;

import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.util.Vector;

@XmlRootElement(name = "StandListModel")
public class StandListModel implements ListModel<Stand>, Serializable {
    @XmlElementWrapper(name = "stands", required = true)
    @XmlElement(name = "stand", required = true)
    private Vector<Stand> stands;
    @XmlTransient
    private Vector<ListDataListener> listDataListeners;

    StandListModel() {
        this.stands = new Vector<>();
        this.listDataListeners = new Vector<>();
    }

    void addStand(Stand stand) {
        stands.add(stand);
        stand.setModel(this);
        //noinspection Duplicates
        for (ListDataListener l : listDataListeners) {
            l.intervalAdded(new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, stands.size() - 1, stands.size() - 1));
            l.contentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, stands.size() - 1, stands.size() - 1));
        }
    }

    void removeStand(Stand stand) {
        stands.remove(stand);
        stand.setModel(null);
        //noinspection Duplicates
        for (ListDataListener l : listDataListeners) {
            l.intervalRemoved(new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, stands.size(), stands.size()));
            l.contentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, stands.size(), stands.size()));
        }
    }

    @Nullable
    public Stand getStandByName(String name) {
        for (Stand stand : stands) if (stand.getName().equals(name)) return stand;
        return null;
    }

    @Override
    public int getSize() {
        return stands.size();
    }

    @Override
    public Stand getElementAt(int index) {
        return stands.get(index);
    }

    @Override
    public void addListDataListener(ListDataListener l) {
        listDataListeners.add(l);
    }

    @Override
    public void removeListDataListener(ListDataListener l) {
        listDataListeners.remove(l);
    }

    void standChanged(Stand stand) {
        if (stands.contains(stand)) {
            int i = stands.indexOf(stand);
            for (ListDataListener l : listDataListeners) {
                l.contentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, i, i));
            }
        }
    }
}
