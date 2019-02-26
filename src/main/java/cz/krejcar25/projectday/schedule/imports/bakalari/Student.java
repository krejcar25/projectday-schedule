package cz.krejcar25.projectday.schedule.imports.bakalari;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "student")
public class Student
{
	private String trida;

	private String jmeno;

	public String getTrida()
	{
		return trida;
	}

	@XmlElement(name = "trida")
	public void setTrida(String trida)
	{
		this.trida = trida;
	}

	public String getJmeno()
	{
		return jmeno;
	}

	@XmlElement(name = "jmeno")
	public void setJmeno(String jmeno)
	{
		this.jmeno = jmeno;
	}

	@Override
	public String toString()
	{
		return String.format("%s (%s)", jmeno, trida);
	}
}
