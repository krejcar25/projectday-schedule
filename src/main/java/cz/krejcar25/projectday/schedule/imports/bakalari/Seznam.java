package cz.krejcar25.projectday.schedule.imports.bakalari;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "seznam")
public class Seznam {
    private Student[] student;

    public Student[] getStudent() {
        return student;
    }

    @XmlElement(name = "Student")
    public void setStudent(Student[] student) {
        this.student = student;
    }

    @Override
    public String toString() {
        return String.format("Seznam %d studentu", student.length);
    }
}

