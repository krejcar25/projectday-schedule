package cz.krejcar25.projectday.schedule;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.awt.*;
import java.io.Serializable;

@XmlRootElement(name = "stand")
public class Stand implements Serializable, Cloneable {
    static final Stand EMPTY = new Stand("-", Color.WHITE, -1);
    private String name;
    private Color color;
    private StandListModel model;
    private int limit;

    @SuppressWarnings("unused")
    public Stand() {

    }

    Stand(String name, Color color, int limit) {
        this.name = name;
        this.color = color;
        this.limit = limit;
    }

    public String getName() {
        return name;
    }

    @XmlAttribute(name = "name", required = true)
    public void setName(String name) {
        this.name = name;
        if (model != null) model.standChanged(this);
    }

    public Color getColor() {
        return color;
    }

    @XmlJavaTypeAdapter(ColorAdapter.class)
    @XmlElement(name = "color", required = true)
    public void setColor(Color color) {
        this.color = color;
        if (model != null) model.standChanged(this);
    }

    public StandListModel getModel() {
        return model;
    }

    @XmlTransient
    void setModel(StandListModel model) {
        this.model = model;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public Stand clone() {
        try {
            super.clone();
        } catch (CloneNotSupportedException e) {
            // won't happen
        }
        Stand clone = new Stand();
        clone.name = String.valueOf(this.name);
        clone.color = new Color(this.color.getRed(), this.color.getGreen(), this.color.getBlue());
        clone.limit = this.limit;
        return clone;
    }

    int getLimit() {
        return limit;
    }

    @XmlAttribute(name = "limit", required = true)
    void setLimit(int limit) {
        this.limit = limit;
    }

    private static class ColorAdapter extends XmlAdapter<ColorAdapter.ColorValueType, Color> {

        @Override
        public Color unmarshal(ColorValueType v) {
            return new Color(v.red, v.green, v.blue);
        }

        @Override
        public ColorValueType marshal(Color v) {
            int r = v.getRed();
            int g = v.getGreen();
            int b = v.getBlue();
            return new ColorValueType(r, g, b);
        }

        @XmlAccessorType(XmlAccessType.FIELD)
        public static class ColorValueType {
            @XmlAttribute(name = "red")
            private int red;
            @XmlAttribute(name = "green")
            private int green;
            @XmlAttribute(name = "blue")
            private int blue;

            @SuppressWarnings("unused")
            public ColorValueType() {
            }

            ColorValueType(int red, int green, int blue) {
                this.red = red;
                this.green = green;
                this.blue = blue;
            }
        }
    }
}
