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

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import com.joseph.mat.json.MinecraftAsset;

import javafx.util.Pair;

public class GuiMain {
	private JFrame frame;
	private JComboBox<String> selector;
	private JPanel currentPanel;
	private HashMap<String, JPanel> versionToPanel;
	
	public GuiMain(LoadingDialog ld, int progress, HashMap<File, HashMap<String, MinecraftAsset>> map) {
		this.frame = new JFrame("Minecraft Asset Tool");
		this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.frame.setLayout(new BorderLayout());
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		this.frame.setBounds(d.width / 2 - 400, d.height / 2 - 300, 800, 600);
		this.frame.setMinimumSize(new Dimension(800, 600));

		this.versionToPanel = new HashMap<String, JPanel>();
		int i = 0;
		int size = map.keySet().size();
		for (File f : map.keySet()) {
			ld.updateProgressBar(progress++, "Creating GUI panels: " + (i + 1) + "/" + size);
			Pair<String, JPanel> pair = this.createPanelForAssets(f, map.get(f));
			this.versionToPanel.put(pair.getKey(), pair.getValue());
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
		
		this.currentPanel = this.versionToPanel.get(versions[versions.length - 1]);
		this.selector.setSelectedIndex(versions.length - 1);
		
		this.frame.add(selector, BorderLayout.NORTH);
		this.frame.add(currentPanel, BorderLayout.CENTER);
		
		ld.closeFrame();
		
		this.frame.setVisible(true);
		this.frame.pack();
	}
	
	private void updateCurrentPanel(String newSelected) {
		this.frame.remove(currentPanel);;
		this.currentPanel = this.versionToPanel.get(newSelected);
		this.frame.add(currentPanel);
		this.frame.pack();
	}
	
	private Pair<String, JPanel> createPanelForAssets(File file, HashMap<String, MinecraftAsset> map) {
		JPanel panel = new JPanel(new GridLayout(1, 1));
		
		// creates the tree associated with the passed in map
		HashMap<String, HashMap<String, MinecraftAsset>> initialMap = new HashMap<String, HashMap<String, MinecraftAsset>>();
		initialMap.put("root", map);
		DefaultMutableTreeNode root = createTreeForMap(null, initialMap);
		
		JTree tree = new JTree(new DefaultTreeModel(root, false));
		
		JScrollPane pane = new JScrollPane(tree);
		
		panel.add(pane);
		
		String[] temp = file.getAbsolutePath().split("\\/|\\\\");
		String version = temp[temp.length - 1];
		return new Pair<String, JPanel>(version, panel);
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