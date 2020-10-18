package com.joseph.mat.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.EventListener;
import java.util.EventObject;
import java.util.HashMap;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.event.EventListenerList;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

// https://stackoverflow.com/questions/21847411/java-swing-need-a-good-quality-developed-jtree-with-checkboxes
public class JCheckBoxTree extends JTree {
	private static final long serialVersionUID = -4378595726542326713L;

	private JCheckBoxTree selfPointer = this;
	private HashMap<TreePath, CheckedNode> nodesCheckingState;
	protected EventListenerList listenerList = new EventListenerList();
	
	public void addCheckChangeEventListener(CheckChangeEventListener listener) {
		listenerList.add(CheckChangeEventListener.class, listener);
	}
	
	public void removeCheckChangeEventListener(CheckChangeEventListener listener) {
		listenerList.remove(CheckChangeEventListener.class, listener);
	}
	
	private void fireCheckChangeEvent(CheckChangeEvent evt) {
		Object[] listeners = listenerList.getListenerList();
		for (int i = 0; i < listeners.length; i++) {
			if (listeners[i] == CheckChangeEventListener.class) {
				((CheckChangeEventListener) listeners[i + 1]).checkStateChanged(evt);
			}
		}
	}
	
	public void setModel(TreeModel newModel) {
		super.setModel(newModel);
		resetCheckingState();
	}
	
	public boolean isSelectedPartially(TreePath path) {
		CheckedNode cn = getCheckedNode(path);
		return cn.isSelected && cn.hasChildren && !cn.allChildrenSelected;
	}
	
	private void resetCheckingState() {
		nodesCheckingState = new HashMap<>();
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) getModel().getRoot();
		if (node == null) {
			return;
		}
		addSubtreeToCheckingStateTracking(node);
	}
	
	private void addSubtreeToCheckingStateTracking(DefaultMutableTreeNode node) {
		TreeNode[] path = node.getPath();
		TreePath tp = new TreePath(path);
		CheckedNode cn = new CheckedNode(false, node.getChildCount() > 0, false);
		nodesCheckingState.put(tp, cn);
		if (isExpanded(tp)) {
			for (int i = 0; i < node.getChildCount(); i++) {
				DefaultMutableTreeNode treeNode = getTreeNode(tp.pathByAddingChild(node.getChildAt(i)));
				addSubtreeToCheckingStateTracking(treeNode);
			}
		}
	}
	
	public JCheckBoxTree() {
		this(JTree.getDefaultTreeModel(), null, null);
	}
	
	public JCheckBoxTree(TreeModel treeModel) {
		this(treeModel, null, null);
	}
	
	public JCheckBoxTree(TreeModel treeModel, TreeWillExpandListener tweListener, TreeCellRenderer treeCellRenderer) {
		super(treeModel);
		
		// Disabling toggling by double-click
		this.setToggleClickCount(0);
		// Overriding cell renderer by new one defined above OR provided one
		if (treeCellRenderer == null)
			treeCellRenderer = new CheckBoxCellRenderer();
		// cellRenderer = treeCellRenderer;
		this.setCellRenderer(treeCellRenderer);
		
		// Overriding selection model by an empty one
		DefaultTreeSelectionModel dtsm = new DefaultTreeSelectionModel() {
			private static final long serialVersionUID = -7061711254131192550L;

			// Totally disabling the selection mechanism
			public void setSelectionPath(TreePath path) {
			}
			public void addSelectionPath(TreePath path) {
			}
			public void removeSelectionPath(TreePath path) {
			}
			public void setSelectionPaths(TreePath[] pPaths) {
			}
		};
		
		// Calling checking mechanism on mouse click
		this.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				TreePath tp = selfPointer.getPathForLocation(e.getX(), e.getY());
				if (tp == null) {
					return;
				}
				boolean checkMode = !getCheckMode(tp);
				checkSubTree(tp, checkMode, false); // func CHANGED for laziness
				
				updatePredecessorsWithCheckMode(tp);
				// Firing the check change event
				fireCheckChangeEvent(new CheckChangeEvent(tp));
				// Repainting tree after the data structures were updated
				selfPointer.repaint();
			}
			
			public void mouseEntered(MouseEvent e) {
			}
			public void mouseExited(MouseEvent e) {
			}
			public void mousePressed(MouseEvent e) {
			}
			public void mouseReleased(MouseEvent e) {
			}
		});
		
		// Do the checkbox update just before the tree expands
		if (tweListener == null)
			tweListener = new TreeWillExpandListener() {
				@Override
				public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
					TreePath expandingNodePath = event.getPath();
					boolean checkMode = getCheckMode(expandingNodePath);
					checkSubTree(expandingNodePath, checkMode, true);
				}
				
				@Override
				public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
				}
			};
		
		this.addTreeWillExpandListener(tweListener);
		this.setSelectionModel(dtsm);
	}
	
	public boolean getCheckMode(TreePath nodePath) {
		CheckedNode checkedNode = getCheckedNode(nodePath);
		return checkedNode.isSelected;
	}
	
	protected CheckedNode getCheckedNode(TreePath nodePath) {
		CheckedNode checkedNode = nodesCheckingState.get(nodePath);
		if (checkedNode == null) {
			DefaultMutableTreeNode node = getTreeNode(nodePath);
			boolean ancestorCheckedMode = getAncestorCheckMode(nodePath);
			checkedNode = new CheckedNode(ancestorCheckedMode, node.getChildCount() > 0, ancestorCheckedMode);
			nodesCheckingState.put(nodePath, checkedNode);
		}
		return checkedNode;
	}
	
	protected boolean getAncestorCheckMode(TreePath nodePath) {
		TreePath parentPath = nodePath.getParentPath();
		if (parentPath == null) {// nodePath is root so has null parent
			return false;
		} else {
			CheckedNode checkedNode = nodesCheckingState.get(parentPath);
			if (checkedNode == null)
				return getAncestorCheckMode(parentPath);
			else
				return checkedNode.isSelected;
		}
	}
	
	protected void updatePredecessorsWithCheckMode(TreePath tp) {
		TreePath parentPath = tp.getParentPath();
		// If it is the root, stop the recursive calls and return
		if (parentPath == null) {
			return;
		}
		CheckedNode parentCheckedNode = getCheckedNode(parentPath);
		DefaultMutableTreeNode parentNode = getTreeNode(parentPath);
		parentCheckedNode.allChildrenSelected = true;
		parentCheckedNode.isSelected = false;
		for (int i = 0; i < parentNode.getChildCount(); i++) {
			TreePath childPath = parentPath.pathByAddingChild(parentNode.getChildAt(i));
			CheckedNode childCheckedNode = getCheckedNode(childPath);
			// It is enough that even one subtree is not fully selected
			// to determine that the parent is not fully selected
			if (!childCheckedNode.allChildrenSelected) {
				parentCheckedNode.allChildrenSelected = false;
			}
			// If at least one child is selected, selecting also the parent
			if (childCheckedNode.isSelected) {
				parentCheckedNode.isSelected = true;
			}
		}
		// Go to upper predecessor
		updatePredecessorsWithCheckMode(parentPath);
	}
	
	protected void checkSubTree(TreePath tp, boolean check, boolean goOneLevelDown) {
		CheckedNode cn = getCheckedNode(tp);
		cn.isSelected = check;
		DefaultMutableTreeNode node = getTreeNode(tp);
		if (isExpanded(tp) || goOneLevelDown) {
			for (int i = 0; i < node.getChildCount(); i++) {
				checkSubTree(tp.pathByAddingChild(node.getChildAt(i)), check, false);
			}
		}
		cn.allChildrenSelected = check;
	}
	
	public static DefaultMutableTreeNode getTreeNode(TreePath path) {
		return (DefaultMutableTreeNode) (path.getLastPathComponent());
	}
	
	protected class CheckedNode {
		boolean isSelected;
		boolean hasChildren;
		boolean allChildrenSelected;
		
		public CheckedNode(boolean isSelected, boolean hasChildren, boolean allChildrenSelected) {
			this.isSelected = isSelected;
			this.hasChildren = hasChildren;
			this.allChildrenSelected = allChildrenSelected;
		}
	}
	
	private class CheckBoxCellRenderer extends JPanel implements TreeCellRenderer {
		private static final long serialVersionUID = 4222900400692042776L;
		JCheckBox checkBox;
		
		public CheckBoxCellRenderer() {
			super();
			this.setLayout(new BorderLayout());
			checkBox = new JCheckBox();
			add(checkBox, BorderLayout.CENTER);
			setOpaque(false);
		}
		
		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
			Object obj = node.getUserObject();
			TreePath tp = new TreePath(node.getPath());
			CheckedNode cn = getCheckedNode(tp);
			if (cn == null) {
				return this;
			}
			checkBox.setSelected(cn.isSelected);
			checkBox.setText(obj.toString());
			checkBox.setOpaque(cn.isSelected && cn.hasChildren && !cn.allChildrenSelected);
			return this;
		}
	}
	
	public class CheckChangeEvent extends EventObject {
		private static final long serialVersionUID = 4270167851847389855L;
		
		public CheckChangeEvent(Object source) {
			super(source);
		}
	}
	
	public interface CheckChangeEventListener extends EventListener {
		void checkStateChanged(CheckChangeEvent event);
	}
}
