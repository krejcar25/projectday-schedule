package cz.krejcar25.projectday.schedule;

import org.intellij.lang.annotations.MagicConstant;

public class RequestsLine {
    static final int REQUEST_LINE = 0;
    static final int ASSIGNMENT_LINE = 1;
    public final Project project;
    private final Person person;
    private final Stand[] requests;
    private final int mode;

    RequestsLine(Person person, @MagicConstant(intValues = {REQUEST_LINE, ASSIGNMENT_LINE}) int mode) {
        this.person = person;
        this.project = person.getGroup().project;
        this.mode = mode;

        switch (mode) {
            case REQUEST_LINE:
                this.requests = new Stand[project.getStands().getSize()];
                for (int i = 0; i < project.getStands().getSize(); i++) {
                    Stand stand = project.getStands().getElementAt(i);
                    requests[person.getRequestForStand(stand.getName())] = stand;
                }
                break;
            case ASSIGNMENT_LINE:
                this.requests = new Stand[project.getBlockCount()];
                for (int i = 0; i < this.requests.length; i++) {
                    String name = person.getAssignmentForBlock(i);
                    this.requests[i] = name.equals(Stand.EMPTY.getName()) ? Stand.EMPTY : project.getStands().getStandByName(name);
                }
                break;
            default:
                throw new IllegalArgumentException("mode must be one of the valid MagicConstants");
        }
    }

    public Object getValue(int index) {
        if (index == 0) return person.getName();
        else if (index > 0 && index <= requests.length) return requests[index - 1];
        else return null;
    }

    void setValue(int index, Object value) {
        try {
            if (index > 0 && index <= requests.length) {
                Stand oldStand = requests[index - 1];
                int oldIndex = -1;
                boolean doOld = !((Stand) value).getName().equals(Stand.EMPTY.getName());
                if (doOld) {
                    for (int i = 0; i < requests.length; i++) if (requests[i].equals(value)) oldIndex = i;
                    if (oldIndex > -1) requests[oldIndex] = oldStand;
                    else doOld = false;
                }
                requests[index - 1] = (Stand) value;
                switch (mode) {
                    case REQUEST_LINE:
                        person.setRequestForStand(((Stand) value).getName(), index - 1);
                        person.setRequestForStand(oldStand.getName(), oldIndex);
                        break;
                    case ASSIGNMENT_LINE:
                        person.setAssignmentForBlock(index - 1, ((Stand) value).getName());
                        if (doOld) person.setAssignmentForBlock(oldIndex, oldStand.getName());
                        break;
                }
            }
        } catch (ClassCastException e) {
            // Shit...
        }
    }
}
