package cz.krejcar25.projectday.schedule;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "pair")
public class StandValue {
    @XmlAttribute(name = "stand")
    String standName;
    @XmlAttribute(name = "value")
    public int value;
}
