package cz.krejcar25.projectday.schedule;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.awt.*;
import java.util.Vector;

public class StandComboBoxModel implements ComboBoxModel<Stand> {
    private Stand selectedStand;
    private boolean empty;
    private Vector<ListDataListener> listDataListeners;
    private Vector<Stand> stands;

    StandComboBoxModel(Project project, boolean addEmpty) {
        listDataListeners = new Vector<>();
        stands = new Vector<>();
        this.empty = addEmpty;
        refillStands(project.getStands());
        project.getStands().addListDataListener(new ListDataListener() {
            @Override
            public void intervalAdded(ListDataEvent e) {

            }

            @Override
            public void intervalRemoved(ListDataEvent e) {

            }

            @Override
            public void contentsChanged(ListDataEvent e) {
                refillStands(project.getStands());
                changeListeners();
            }
        });
    }

    private void changeListeners() {
        for (ListDataListener l : listDataListeners)
            l.contentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, -1, -1));
    }

    private void refillStands(StandListModel model) {
        stands.removeAllElements();
        if (empty) stands.add(Stand.EMPTY);
        for (int i = 0; i < model.getSize(); i++) stands.add(model.getElementAt(i));
    }

    @Override
    public Object getSelectedItem() {
        return selectedStand;
    }

    @Override
    public void setSelectedItem(Object anItem) {
        if (anItem instanceof Stand) if (stands.contains(anItem)) selectedStand = (Stand) anItem;
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
}
