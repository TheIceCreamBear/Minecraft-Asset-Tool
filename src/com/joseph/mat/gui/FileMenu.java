package com.joseph.mat.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import com.joseph.mat.reference.Reference;

public class FileMenu extends JMenu {
	private static final long serialVersionUID = 7439222685484561479L;
	
	private JMenuItem chooseAssetsDirectory;
	private JMenuItem resetAssetsDirectory;
	
	private GuiMain gui;
	
	public FileMenu(GuiMain gui) {
		super("File");
		
		this.gui = gui;
		
		this.chooseAssetsDirectory = new JMenuItem("Choose new Asset Directory");
		this.chooseAssetsDirectory.setToolTipText("Opens a dialog to select a new directory to search");
		this.chooseAssetsDirectory.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int result = chooser.showOpenDialog(null);
				if (result == JFileChooser.APPROVE_OPTION) {
					String file = chooser.getSelectedFile().getAbsolutePath();
					System.out.println(file);
					file = file.substring(0, file.lastIndexOf(File.separatorChar));
					System.out.println(file);
					Reference.updateRootDir(file);
					FileMenu.this.gui.resetContent();
				}
			}
		});
		
		this.resetAssetsDirectory = new JMenuItem("Reset Assets Directory");
		this.resetAssetsDirectory.setToolTipText("Resets the assets directory to the default state");
		this.resetAssetsDirectory.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Reference.resetRootDir();
				FileMenu.this.gui.resetContent();
			}
		});
		
		this.add(chooseAssetsDirectory);
		this.add(resetAssetsDirectory);
	}
}
