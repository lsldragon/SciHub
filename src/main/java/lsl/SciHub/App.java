package lsl.SciHub;

import javax.swing.SwingUtilities;

import com.alee.laf.WebLookAndFeel;

public class App {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {

				WebLookAndFeel.install();
				new MainFrame().setVisible(true);
			}
		});
	}
}
