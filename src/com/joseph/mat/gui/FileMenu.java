package com.joseph.mat.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

public class FileMenu extends JMenu {
	private static final long serialVersionUID = 7439222685484561479L;
	
	private JMenuItem chooseAssetsDirectory;
	
	// TODO does this need more

	public FileMenu() {
		super("File");
		
		this.chooseAssetsDirectory = new JMenuItem("Choose new Asset Directory");
		this.chooseAssetsDirectory.setToolTipText("Opens a dialog to select a new directory to search");
		this.chooseAssetsDirectory.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// TODO fill this in
			}
		});
		
		this.add(chooseAssetsDirectory);
	}
}
