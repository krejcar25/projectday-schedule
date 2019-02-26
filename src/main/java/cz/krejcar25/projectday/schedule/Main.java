package cz.krejcar25.projectday.schedule;

import javax.swing.*;
import java.awt.*;
import java.util.logging.Logger;

public class Main
{
	private static final Logger log = Logger.getLogger(Main.class.getName());
	static boolean isMac;

	public static void main(String... args)
	{
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		System.setProperty("com.apple.mrj.application.apple.menu.about.name", Strings.get("name"));
		System.setProperty("apple.eawt.quitStrategy", "CLOSE_ALL_WINDOWS");
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			UIDefaults defaults = UIManager.getLookAndFeelDefaults();
			if (defaults.get("Table.alternateRowColor") == null)
				defaults.put("Table.alternateRowColor", new Color(240, 240, 240));
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e)
		{
			log.severe(e.getMessage());
		}
		String osName = System.getProperty("os.name").toLowerCase();
		isMac = osName.startsWith("mac os x");
		javax.swing.SwingUtilities.invokeLater(() -> new MainWindow().setVisible(true));
	}
}
