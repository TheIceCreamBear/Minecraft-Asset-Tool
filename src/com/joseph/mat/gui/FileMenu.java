package com.joseph.mat.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import com.joseph.mat.reference.Reference;

public class FileMenu extends JMenu {
	private static final long serialVersionUID = 7439222685484561479L;
	
	private JMenuItem chooseAssetsDirectory;
	private JMenuItem resetAssetsDirectory;
	
	// TODO does this need more

	public FileMenu() {
		super("File");
		
		this.chooseAssetsDirectory = new JMenuItem("Choose new Asset Directory");
		this.chooseAssetsDirectory.setToolTipText("Opens a dialog to select a new directory to search");
		this.chooseAssetsDirectory.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int result = chooser.showOpenDialog(null);
				if (result == JFileChooser.APPROVE_OPTION) {
					String file = chooser.getSelectedFile().getAbsolutePath();
					Reference.updateRootDir(file);
					// TODO make GUI main reset
				}
			}
		});
		
		this.resetAssetsDirectory = new JMenuItem("Reset Assets Directory");
		this.resetAssetsDirectory.setToolTipText("Resets the assets directory to the default state");
		this.resetAssetsDirectory.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Reference.resetRootDir();
				// TODO make GUI main reset
			}
		});
		
		this.add(chooseAssetsDirectory);
		this.add(resetAssetsDirectory);
	}
}
