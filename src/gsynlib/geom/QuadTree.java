package gsynlib.geom;

import processing.core.*;
import static processing.core.PApplet.*;

import java.util.ArrayList;

public class QuadTree {

	QuadTreeNode root;

	public QuadTreeNode getRoot() {
		return this.root;
	}

	public QuadTree(Bounds initialBounds) {
		root = new QuadTreeNode();

		root.bounds.copyFrom(initialBounds);
		root.bounds.floorValues();
		
	}
	
	public void resetVisited() {
		resetVisited(root);
	}
	
	void resetVisited(QuadTreeNode n) {
		n.visited = false;
		if(n.isSplit) {
			resetVisited(n.A);
			resetVisited(n.B);
			resetVisited(n.C);
			resetVisited(n.D);
		}
	}

	public void insert(PVector position, Object data) {
		if (!root.bounds.Contains(position, false)) {
			expand(position);
		}

		root.insert(position, data);
	}
	
	public QuadTreeData getNearestData(PVector position) {
		return root.searchNN(position);
	}

	public QuadTreeNode getNodeUnder(PVector position) {
		return root.getNodeUnder(position);
	}

	public static ArrayList<QuadTreeData> dataPool = new ArrayList<QuadTreeData>();

	// Expand to encapsulate given position, make root a subdivision of the new node
	void expand(PVector pos) {

		if (root.bounds.Contains(pos))
			return;

		// Create new root, and expand so that new position is included
		float w = floor(root.bounds.size.x * 2f);
		float h = floor(root.bounds.size.y * 2f);

		QuadTreeNode newRoot = new QuadTreeNode();
		
		
		float left = ceil(root.bounds.position.x);
		float top = ceil(root.bounds.position.y);
		float right = ceil(root.bounds.position.x + root.bounds.size.x);
		float bottom = ceil(root.bounds.position.y + root.bounds.size.y);

		PVector newRootPosition = new PVector();

		//set new root position to include the old root bounds in a certain quadrant
		
		if (pos.x >= right && pos.y >= top) {
			newRootPosition.x = 0;
			newRootPosition.y = 0;
		} else if (pos.x >= right && pos.y <= bottom) {
			newRootPosition.x = 0;
			newRootPosition.y = -h;
		} else if (pos.x <= left && pos.y <= bottom ) {
			newRootPosition.x = -w;
			newRootPosition.y = -h;
		}else {
			newRootPosition.x = -w;
			newRootPosition.y = 0;
		}
		
		//set new root position

		newRoot.bounds.position.set(
				root.bounds.position.x + newRootPosition.x,
				root.bounds.position.y + newRootPosition.y);

		newRoot.bounds.size.set(w, h);
		
		//SAFE WAY TO MERGE WITH NEW ROOT, get all previous data and re-insert it.
		
		QuadTree.dataPool.clear();
		root.getAllData(QuadTree.dataPool);
		
		for (QuadTreeData d : QuadTree.dataPool) {
			newRoot.insert(d.position, d);
		}

		QuadTree.dataPool.clear();
		root.data.clear();

		root = newRoot;
	}

}