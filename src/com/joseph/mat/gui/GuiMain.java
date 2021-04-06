package com.joseph.mat.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import com.joseph.mat.Main;
import com.joseph.mat.Tripple;
import com.joseph.mat.json.MinecraftAsset;

/**
 * The main class of the Gui System. Handles the window creation and the creation of the
 * content on the frame.
 * @author Joseph
 *
 */
public class GuiMain {
	private JFrame frame;
	private JComboBox<String> selector;
	private JPanel currentPanel;
	private HashMap<String, JPanel> versionToPanel;
	private HashMap<String, JCheckBoxTree> versionToTree;
	
	private JPanel southPanel;
	private JButton browseButton;
	private JTextField saveFolder;
	private JButton transferButton;
	
	private JMenuBar menuBar;
	private FileMenu file;
	private HelpMenu help;
	
	private LoadingDialog ld;
	
	public GuiMain() {
		// create jframe
		this.frame = new JFrame("Minecraft Asset Tool");
		this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.frame.setLayout(new BorderLayout());
		// center window and set minimum size
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		this.frame.setBounds(d.width / 2 - 400, d.height / 2 - 300, 800, 600);
		this.frame.setMinimumSize(new Dimension(800, 600));

		// the bread and butter of the gui main system, generates all content and gets a list of versions
		String[] versions = this.generateContent();
		
		// create the selector for the current version
		this.selector = new JComboBox<String>(versions);
		this.selector.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// when a new item gets selected
				// update the currently displayed pannel with the currently selected item
				GuiMain.this.updateCurrentPanel((String) GuiMain.this.selector.getSelectedItem());
			}
		});
		
		// create the button to browse for the destination directory
		this.browseButton = new JButton("Browse");
		this.browseButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// create the file chooser
				JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				// if a directory was already set, set the current directory of the chooser to that location
				String currText = GuiMain.this.saveFolder.getText();
				if (currText != null && !currText.isEmpty()) {
					chooser.setCurrentDirectory(new File(currText));
				}
				// open the dialog and capture the result
				int result = chooser.showOpenDialog(null);
				// if they clicked the positive approve option
				if (result == JFileChooser.APPROVE_OPTION) {
					// set the text of the save folder to be the selected file
					String file = chooser.getSelectedFile().getAbsolutePath();
					GuiMain.this.saveFolder.setText(file);
				}
			}
		});
		
		// create the save folder text box
		this.saveFolder = new JTextField();
		this.saveFolder.setEditable(false);
		this.saveFolder.setToolTipText("The location that your files will be saved to");
		
		// create the transfer button
		this.transferButton = new JButton("Save Selected");
		this.transferButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// on click, get the current destination
				String saveDestination = GuiMain.this.saveFolder.getText();
				if (saveDestination.isEmpty()) {
					// if there is no string set, remind them to set one
					JOptionPane.showMessageDialog(null, "Please make sure to set a destination folder by clicking browse");
					return;
				}
				
				// get the selected version and the associated assets
				String selectedVersion = (String) GuiMain.this.selector.getSelectedItem();
				final MinecraftAsset[] assets = GuiMain.this.getSelectedAssets(GuiMain.this.versionToTree.get(selectedVersion));
				// spawn a new thread to transfer the selected assets to the destination
				new Thread(new Runnable() {
					@Override
					public void run() {
						Main.convertSelectedMinecraftAssets(assets, saveDestination);
					}
				}).start();
			}
		});
		
		// setup the menus
		this.initMenus();
		this.frame.setJMenuBar(menuBar);
		
		// set up the south panel
		this.southPanel = new JPanel(new BorderLayout());
		this.southPanel.add(browseButton, BorderLayout.WEST);
		this.southPanel.add(saveFolder, BorderLayout.CENTER);
		this.southPanel.add(transferButton, BorderLayout.EAST);
		
		// decide the current panel to use
		this.currentPanel = this.versionToPanel.get(versions[versions.length - 1]);
		this.selector.setSelectedIndex(versions.length - 1);
		
		// add all panels to the frame
		this.frame.add(selector, BorderLayout.NORTH);
		this.frame.add(currentPanel, BorderLayout.CENTER);
		this.frame.add(southPanel, BorderLayout.SOUTH);
		
		// close the loading frame and set it to null so it gets collected by the garbage collector
		this.ld.closeFrame();
		this.ld = null;
		
		// set the frame visible
		this.frame.setVisible(true);
		this.frame.pack();
	}
	
	/**
	 * internal method to set up the menu bar and the menu items
	 */
	private void initMenus() {
		// set the menu bar
		this.menuBar = new JMenuBar();
		
		// create the file menu and add it to the bar
		this.file = new FileMenu(this);
		this.menuBar.add(file);
		
		// create the help menu and add it to the bar
		this.help = new HelpMenu();
		this.menuBar.add(help);
	}
	
	/**
	 * Reset the content, assuming the root directory has been updated or reset
	 */
	protected void resetContent() {
		// generate the new content
		String[] versions = this.generateContent();
		
		// set the new list ov versions
		this.selector.setModel(new DefaultComboBoxModel<String>(versions));

		// close the loading frame and set it to null so it gets collected by the garbage collector
		this.ld.closeFrame();
		this.ld = null;
	}
	
	protected String[] generateContent() {
		// get the new index files that will be used
		File[] indexFiles = Main.getIndexFiles();
		
		// create a new loading dialog
		int maxLoadingProgress = 2 * indexFiles.length + 1;
		ld = new LoadingDialog(maxLoadingProgress);
		
		// get the result of the parsing of all of the files
		HashMap<File, HashMap<String, MinecraftAsset>> map = Main.generateFileToParseMap(indexFiles, ld);
		
		// set local progress
		int progress = indexFiles.length;
		
		// set up the version maps
		this.versionToPanel = new HashMap<String, JPanel>();
		this.versionToTree = new HashMap<String, JCheckBoxTree>();
		// prepare to look
		int i = 0;
		int size = map.keySet().size();
		// for each version file, 
		for (File f : map.keySet()) {
			ld.updateProgressBar(progress++, "Creating GUI panels: " + (i + 1) + "/" + size);
			// create the panel and tree for its assets
			Tripple<String, JPanel, JCheckBoxTree> tripple = this.createPanelForAssets(f, map.get(f));
			// and put those into their maps
			this.versionToPanel.put(tripple.getA(), tripple.getB());
			this.versionToTree.put(tripple.getA(), tripple.getC());
			i++;
		}
		
		// get the key set of the version to panel map and sort it
		String[] versions = this.versionToPanel.keySet().toArray(new String[this.versionToPanel.keySet().size()]);
		Arrays.sort(versions);
		
		// update the progress
		ld.updateProgressBar(progress++, "Creating main GUI");
		
		return versions;
	}
	
	/**
	 * Gets all of the selected nodes with the given tree as the root, then
	 * returns the assets associated with those paths
	 * @param tree - the root node of the tree
	 * @return an array of all selected assets under the given root
	 */
	private MinecraftAsset[] getSelectedAssets(JCheckBoxTree tree) {
		// get nodes and create assets array
		DefaultMutableTreeNode[] paths = tree.getCheckedLeafNodes();
		MinecraftAsset[] assets = new MinecraftAsset[paths.length];
		
		// copy assets into array
		for (int i = 0; i < paths.length; i++) {
			assets[i] = (MinecraftAsset) paths[i].getUserObject();
		}
		
		return assets;
	}
	
	/**
	 * Updates the currently selected panel via the version string
	 * @param newSelected
	 */
	private void updateCurrentPanel(String newSelected) {
		this.frame.remove(currentPanel);
		this.currentPanel = this.versionToPanel.get(newSelected);
		this.frame.add(currentPanel);
		this.frame.pack();
	}
	
	/**
	 * Creates the panel and check box tree for the given asset map and file. Mostly handles the 
	 * gui stuff and delegates the construction of the tree to {@link #createTreeForMap(DefaultMutableTreeNode, HashMap)}
	 * @param file - the file associated with this panel (to get the version string from the name)
	 * @param map - a map of the name of the asset to the asset
	 * @return a triple containing the version string, the jpanel, and jcheckboxtree
	 */
	private Tripple<String, JPanel, JCheckBoxTree> createPanelForAssets(File file, HashMap<String, MinecraftAsset> map) {
		// make new panel
		JPanel panel = new JPanel(new GridLayout(1, 1));
		
		// creates the tree associated with the passed in map
		HashMap<String, HashMap<String, MinecraftAsset>> initialMap = new HashMap<String, HashMap<String, MinecraftAsset>>();
		initialMap.put("root", map);
		DefaultMutableTreeNode root = createTreeForMap(null, initialMap);
		
		// construct tree  with the given root and add a scroll pane to the tree
		JCheckBoxTree tree = new JCheckBoxTree(new DefaultTreeModel(root, false));
		JScrollPane pane = new JScrollPane(tree);
		
		// add the pane to the panel
		panel.add(pane);
		
		// get the version string
		// TODO why of all ways did i do it this way??
		String[] temp = file.getAbsolutePath().split("\\/|\\\\");
		String version = temp[temp.length - 1];
		return new Tripple<String, JPanel, JCheckBoxTree>(version, panel, tree);
	}
	
	private DefaultMutableTreeNode createTreeForMap(DefaultMutableTreeNode node, HashMap<String, HashMap<String, MinecraftAsset>> map) {
		if (node == null) {
			// no node exists so create one and name it root
			node = new DefaultMutableTreeNode("root");
			
			HashMap<String, HashMap<String, MinecraftAsset>> subNodes = new HashMap<String, HashMap<String, MinecraftAsset>>();
			
			// sort the key set
			String[] sortedKeys = map.get("root").keySet().toArray(new String[map.get("root").keySet().size()]);
			Arrays.sort(sortedKeys);
			
			// iterate through the keys of the map for the root
			for (String item : sortedKeys) {
				MinecraftAsset current = map.get("root").get(item);
				
				// if there is no '/' (meaning file)
				if (item.indexOf('/') == -1) {
					// save this MinecraftAsset into the map with item being its key at both levels
					HashMap<String, MinecraftAsset> mip = new HashMap<String, MinecraftAsset>();
					mip.put(item, current);
					subNodes.put(item, mip);
					continue;
				}
				
				// get the current directory prefix and the next directory string
				String prefix = item.substring(0, item.indexOf('/'));
				String nextString = item.substring(item.indexOf('/') + 1);

				// if a map does not exist for this prefix, make one
				if (subNodes.get(prefix) == null) {
					subNodes.put(prefix, new HashMap<String, MinecraftAsset>());
				}

				// add this current MinecraftAsset to the map at prefix with key nextString
				subNodes.get(prefix).put(nextString, current);
			}
			
			// recurse to the next level
			createTreeForMap(node, subNodes);
			
			// return the node
			return node;
		}
		
		// sort the main key set
		String[] mainSortedKeys = map.keySet().toArray(new String[map.keySet().size()]);
		Arrays.sort(mainSortedKeys);
		
		// process each subpath in the current map
		for (String path : mainSortedKeys) {
			// if at file
			if (path.indexOf('.') != -1) {
				// add the MinecraftAsset associated with that file
				node.add(new DefaultMutableTreeNode(map.get(path).get(path)));
				continue;
			}
			
			// create current node and add it to tree
			DefaultMutableTreeNode currentNode = new DefaultMutableTreeNode(path);
			node.add(currentNode);
			
			// create subnode map
			HashMap<String, HashMap<String, MinecraftAsset>> subNodes = new HashMap<String, HashMap<String, MinecraftAsset>>();
			
			// sort the key set
			String[] sortedKeys = map.get(path).keySet().toArray(new String[map.get(path).keySet().size()]);
			Arrays.sort(sortedKeys);
			// iterate through the keys of the map for this path
			for (String item : sortedKeys) {
				// save current Mincraft Asset
				MinecraftAsset current = map.get(path).get(item);
				
				// if there is no '/' (meaning file)
				if (item.indexOf('/') == -1) {
					// special case for when the file does not have a dot
					if (item.indexOf('.') == -1) {
						// this was an issue introduced in the 1.17 assets file, where two items in the list
						// are listed as a hash rather than a regular file. I currently do not know why these
						// assets are listed like that or are included in the shipped assets
						
						// add the MinecraftAsset associated with that file
						currentNode.add(new DefaultMutableTreeNode(map.get(path).get(item)));
						continue;	
					}
					
					// save this MinecraftAsset into the map with item being its key at both levels
					HashMap<String, MinecraftAsset> mip = new HashMap<String, MinecraftAsset>();
					mip.put(item, current);
					subNodes.put(item, mip);
					continue;
				}
				
				// get the current directory prefix and the next directory string
				String prefix = item.substring(0, item.indexOf('/'));
				String nextString = item.substring(item.indexOf('/') + 1);
				
				// if a map does not exist for this prefix, make one
				if (subNodes.get(prefix) == null) {
					subNodes.put(prefix, new HashMap<String, MinecraftAsset>());
				}
				
				// add this current MinecraftAsset to the map at prefix with key nextString
				subNodes.get(prefix).put(nextString, current);
			}
			
			// recurse to the next level
			createTreeForMap(currentNode, subNodes);
		}
		
		// return the node
		return node;
	}
}