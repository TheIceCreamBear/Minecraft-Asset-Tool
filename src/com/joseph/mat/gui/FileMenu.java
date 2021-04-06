package com.joseph.mat.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import com.joseph.mat.reference.Reference;

/**
 * Responsible for the File menu option and all of its items. Handles creation
 * and setting up of actions of the menu items.
 * @author Joseph
 *
 */
public class FileMenu extends JMenu {
	private static final long serialVersionUID = 7439222685484561479L;
	
	/** 
	 * Allows the user to choose a directory that contains MC asset index files,
	 * useful if they play with their .minecraft folder not in the default location
	 */
	private JMenuItem chooseAssetsDirectory;
	
	/**
	 * Allows the user to reset the assets directory back to the default one
	 */
	private JMenuItem resetAssetsDirectory;
	
	private GuiMain gui;
	
	public FileMenu(GuiMain gui) {
		super("File");
		
		this.gui = gui;
		
		// create the choose assets directory menu item and its action listener
		this.chooseAssetsDirectory = new JMenuItem("Choose new Asset Directory");
		this.chooseAssetsDirectory.setToolTipText("Opens a dialog to select a new directory to search");
		this.chooseAssetsDirectory.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// create the JFileChooser and set it to directories only
				JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				// open the dialog and get the result
				int result = chooser.showOpenDialog(null);
				// if they clicked the positive approve option
				if (result == JFileChooser.APPROVE_OPTION) {
					String file = chooser.getSelectedFile().getAbsolutePath();
					System.out.println(file);
					// TODO what is happening here on this line
					file = file.substring(0, file.lastIndexOf(File.separatorChar));
					System.out.println(file);
					// update the directory and reset the content
					Reference.updateRootDir(file);
					FileMenu.this.gui.resetContent();
				}
			}
		});
		
		// create the reset assets directory menu item and its action listener
		this.resetAssetsDirectory = new JMenuItem("Reset Assets Directory");
		this.resetAssetsDirectory.setToolTipText("Resets the assets directory to the default state");
		this.resetAssetsDirectory.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// reset the root dir and the content
				Reference.resetRootDir();
				FileMenu.this.gui.resetContent();
			}
		});
		
		// add the buttons to the menu
		this.add(chooseAssetsDirectory);
		this.add(resetAssetsDirectory);
	}
}
