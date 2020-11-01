package com.joseph.mat.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

public class HelpMenu extends JMenu {
	private static final long serialVersionUID = -3540199589657645689L;
	
	private JMenuItem showHelp;
	private JMenuItem showAbout;

	public HelpMenu() {
		super("Help");
		
		this.showHelp = new JMenuItem("Show Help");
		this.showHelp.setToolTipText("Shows a help dialog"); // TODO make this show a help thing on github
		this.showHelp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// TODO
			}
		});
		
		this.showAbout = new JMenuItem("About");
		this.showAbout.setToolTipText("Shwos an about dialog");
		this.showAbout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// TODO
			}
		});
		
		this.add(showHelp);
		this.add(showAbout);
	}
}
