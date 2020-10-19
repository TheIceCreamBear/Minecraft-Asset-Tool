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

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import com.joseph.mat.json.MinecraftAsset;

import javafx.util.Pair;

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
	
	public GuiMain(LoadingDialog ld, int progress, HashMap<File, HashMap<String, MinecraftAsset>> map) {
		this.frame = new JFrame("Minecraft Asset Tool");
		this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.frame.setLayout(new BorderLayout());
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		this.frame.setBounds(d.width / 2 - 400, d.height / 2 - 300, 800, 600);
		this.frame.setMinimumSize(new Dimension(800, 600));

		this.versionToPanel = new HashMap<String, JPanel>();
		this.versionToTree = new HashMap<String, JCheckBoxTree>();
		int i = 0;
		int size = map.keySet().size();
		for (File f : map.keySet()) {
			ld.updateProgressBar(progress++, "Creating GUI panels: " + (i + 1) + "/" + size);
			Pair<String, Pair<JPanel, JCheckBoxTree>> stringToPair = this.createPanelForAssets(f, map.get(f));
			Pair<JPanel, JCheckBoxTree> pair = stringToPair.getValue();
			this.versionToPanel.put(stringToPair.getKey(), pair.getKey());
			this.versionToTree.put(stringToPair.getKey(), pair.getValue());
			i++;
		}
		
		String[] versions = this.versionToPanel.keySet().toArray(new String[this.versionToPanel.keySet().size()]);
		Arrays.sort(versions);
		
		ld.updateProgressBar(progress++, "Creating main GUI");
		
		this.selector = new JComboBox<String>(versions);
		this.selector.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GuiMain.this.updateCurrentPanel((String) GuiMain.this.selector.getSelectedItem());
			}
		});
		
		this.browseButton = new JButton("Browse");
		this.browseButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int result = chooser.showOpenDialog(null);
				if (result == JFileChooser.APPROVE_OPTION) {
					String file = chooser.getSelectedFile().getAbsolutePath();
					GuiMain.this.saveFolder.setText(file);
				}
			}
		});
		
		this.saveFolder = new JTextField();
		this.saveFolder.setEditable(false);
		this.saveFolder.setToolTipText("The location that your files will be saved to");
		
		this.transferButton = new JButton("Save Selected");
		this.transferButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("Transer putton pushed");
				String selectedVersion = (String) GuiMain.this.selector.getSelectedItem();
				MinecraftAsset[] assets = GuiMain.this.getSelectedAssets(GuiMain.this.versionToTree.get(selectedVersion));
				// for now and testing purposes
				System.out.println(Arrays.toString(assets));
			}
		});
		
		this.southPanel = new JPanel(new BorderLayout());
		this.southPanel.add(browseButton, BorderLayout.WEST);
		this.southPanel.add(saveFolder, BorderLayout.CENTER);
		this.southPanel.add(transferButton, BorderLayout.EAST);
		
		this.currentPanel = this.versionToPanel.get(versions[versions.length - 1]);
		this.selector.setSelectedIndex(versions.length - 1);
		
		this.frame.add(selector, BorderLayout.NORTH);
		this.frame.add(currentPanel, BorderLayout.CENTER);
		this.frame.add(southPanel, BorderLayout.SOUTH);
		
		ld.closeFrame();
		
		this.frame.setVisible(true);
		this.frame.pack();
	}
	
	private MinecraftAsset[] getSelectedAssets(JCheckBoxTree tree) {
		DefaultMutableTreeNode[] paths = tree.getCheckedLeafNodes();
		MinecraftAsset[] assets = new MinecraftAsset[paths.length];
		
		for (int i = 0; i < paths.length; i++) {
			assets[i] = (MinecraftAsset) paths[i].getUserObject();
		}
		
		return assets;
	}
	
	private void updateCurrentPanel(String newSelected) {
		this.frame.remove(currentPanel);
		this.currentPanel = this.versionToPanel.get(newSelected);
		this.frame.add(currentPanel);
		this.frame.pack();
	}
	
	private Pair<String, Pair<JPanel, JCheckBoxTree>> createPanelForAssets(File file, HashMap<String, MinecraftAsset> map) {
		JPanel panel = new JPanel(new GridLayout(1, 1));
		
		// creates the tree associated with the passed in map
		HashMap<String, HashMap<String, MinecraftAsset>> initialMap = new HashMap<String, HashMap<String, MinecraftAsset>>();
		initialMap.put("root", map);
		DefaultMutableTreeNode root = createTreeForMap(null, initialMap);
		
		JCheckBoxTree tree = new JCheckBoxTree(new DefaultTreeModel(root, false));
		
		JScrollPane pane = new JScrollPane(tree);
		
		panel.add(pane);
		
		String[] temp = file.getAbsolutePath().split("\\/|\\\\");
		String version = temp[temp.length - 1];
		return new Pair<String, Pair<JPanel, JCheckBoxTree>>(version, new Pair<JPanel, JCheckBoxTree>(panel, tree));
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
				// add the MinecraftAsset asociated with that file
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