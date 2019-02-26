package cz.krejcar25.projectday.schedule;

import javax.swing.*;
import java.awt.*;

public class StandRenderer implements ListCellRenderer<Stand>
{
	StandRenderer()
	{

	}

	@Override
	public Component getListCellRendererComponent(JList<? extends Stand> list, Stand value, int index, boolean isSelected, boolean cellHasFocus)
	{
		JPanel panel = new JPanel(new BorderLayout());
		panel.setOpaque(true);

		if (isSelected) panel.setBackground(new Color(100, 100, 255));
		JLabel color = new JLabel(" ");
		color.setBackground(value.getColor());
		color.setOpaque(true);
		panel.add(color, BorderLayout.WEST);

		JLabel name = new JLabel(value.getName());
		panel.add(name, BorderLayout.CENTER);

		return panel;
	}
}
