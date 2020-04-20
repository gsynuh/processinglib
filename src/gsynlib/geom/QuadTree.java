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
		root.split();
	}

	public void insert(PVector position, Object data) {
		if (!root.bounds.Contains(position, false)) {
			expand(position);
		}

		root.insert(position, data);
	}

	public QuadTreeNode search(PVector position) {
		return root.search(position);
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

		// A B
		// C D

		// 0 1
		// 2 3

		int placeofoldroot = 0;

		
		if (pos.x >= right && pos.y >= top) {
			newRootPosition.x = 0;
			newRootPosition.y = 0;
			placeofoldroot = 0;
		} else if (pos.x >= right && pos.y <= bottom) {
			newRootPosition.x = 0;
			newRootPosition.y = -h;
			placeofoldroot = 2;
		} else if (pos.x <= left && pos.y <= bottom ) {
			newRootPosition.x = -w;
			newRootPosition.y = -h;
			placeofoldroot = 3;
		}else {
			newRootPosition.x = -w;
			newRootPosition.y = 0;
			placeofoldroot = 1;
		}

		newRoot.bounds.position.set(
				root.bounds.position.x + newRootPosition.x,
				root.bounds.position.y + newRootPosition.y);

		newRoot.bounds.size.set(w, h);
		newRoot.split();
		
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