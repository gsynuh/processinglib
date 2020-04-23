package gsynlib.geom;

import processing.core.*;
import static processing.core.PApplet.*;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

import gsynlib.utils.*;

public class QuadTree {

	QuadTreeNode root;
	ReentrantLock qtlock = new ReentrantLock();

	public QuadTreeNode getRoot() {
		return this.root;
	}

	public QuadTree(Bounds initialBounds) {
		root = new QuadTreeNode();

		root.bounds.set(initialBounds);
	}

	public void resetVisited() {
		resetVisited(root);
	}

	void resetVisited(QuadTreeNode n) {
		n.visited = false;
		if (n.isSplit) {
			resetVisited(n.A);
			resetVisited(n.B);
			resetVisited(n.C);
			resetVisited(n.D);
		}
	}
	
	public ArrayList<QuadTreeData> queryCircle(PVector position, float radius) {
		ArrayList<QuadTreeData> results = new ArrayList<QuadTreeData>();
		queryCircle(position, radius, results);
		return results;
	}
	
	ArrayList<QuadTreeData> circleDataQueryResults = new ArrayList<QuadTreeData>();
	public void queryCircle(PVector position, float radius, ArrayList<QuadTreeData> output) {
		output.clear();
		circleDataQueryResults.clear();
		
		Bounds b = new Bounds();
		b.position.set(position);
		b.size.set(radius*2f,radius*2f);
		
		b.position.x -= b.size.x*0.5f;
		b.position.y -= b.size.y*0.5f;
		
		root.query(b, circleDataQueryResults);
		
		float sqrRadius = radius * radius;
		
		for (QuadTreeData d : circleDataQueryResults) {
			if (GApp.sqrDist(d.position, position) <= sqrRadius) {
				output.add(d);
			}
		}
		
		circleDataQueryResults.clear();
	}

	public ArrayList<QuadTreeData> queryBounds(Bounds b) {
		ArrayList<QuadTreeData> results = new ArrayList<QuadTreeData>();
		queryBounds(b, results);
		return results;
	}

	public void queryBounds(Bounds b, ArrayList<QuadTreeData> output) {
		output.clear();
		root.query(b, output);
	}

	public void updatePosition(QuadTreeData d, PVector newPosition) {
		qtlock.lock();
		try {
			remove(d);
			d.position.set(newPosition);
			insert(d);
		} finally {
			qtlock.unlock();
		}
	}

	public Boolean insert(QuadTreeData data) {
		Boolean insertSuccess = false;
		if (data == null)
			return false;

		qtlock.lock();
		try {

			while (!(insertSuccess = root.insert(data))) {
				expand(data.position);
			}

		} finally {
			qtlock.unlock();
		}

		return insertSuccess;
	}

	public void remove(QuadTreeData d) {
		qtlock.lock();
		try {
			QuadTreeNode dataNode = getNodeUnder(d.position);
			dataNode.remove(d);
		} finally {
			qtlock.unlock();
		}
	}
	
	public QuadTreeData getNearestData(PVector position) {
		return root.searchNN(position);
	}

	public QuadTreeNode getNodeUnder(PVector position) {
		return root.getNodeUnder(position);
	}

	// Expand to encapsulate given position, make root a subdivision of the new node
	void expand(PVector pos) {

		if (root.bounds.Contains(pos))
			return;

		// Create new root, and expand so that new position is included
		float w = root.bounds.size.x;
		float h = root.bounds.size.y;

		QuadTreeNode newRoot = new QuadTreeNode();

		float centerX = root.bounds.position.x + root.bounds.size.x * .5f;
		float centerY = root.bounds.position.y + root.bounds.size.y * .5f;

		PVector newRootPosition = new PVector();

		// set new root position to include the old root bounds in a certain quadrant

		// NW - N
		// NE - N

		QuadTreeNode.DIRECTION oldRootPosition = QuadTreeNode.DIRECTION.UNKNOWN;

		if (pos.y < centerY) {
			if (pos.x > centerX) {
				newRootPosition.x = 0;
				newRootPosition.y = -h;
				oldRootPosition = QuadTreeNode.DIRECTION.SW;
			} else {
				newRootPosition.x = -w;
				newRootPosition.y = -h;
				oldRootPosition = QuadTreeNode.DIRECTION.SE;
			}
		}

		// SW - S
		// SE - S

		if (pos.y > centerY) {
			if (pos.x < centerX) {
				newRootPosition.x = 0;
				newRootPosition.y = 0;
				oldRootPosition = QuadTreeNode.DIRECTION.NW;
			} else {
				newRootPosition.x = -w;
				newRootPosition.y = 0;
				oldRootPosition = QuadTreeNode.DIRECTION.NE;
			}
		}

		// set new root position

		newRoot.bounds.position.set(root.bounds.position.x + newRootPosition.x,
				root.bounds.position.y + newRootPosition.y);

		newRoot.bounds.size.set(w * 2f, h * 2f);

		newRoot.split();

		switch (oldRootPosition) {
		case SW:
			newRoot.C = root;
			break;
		case NW:
			newRoot.A = root;
			break;
		case NE:
			newRoot.B = root;
			break;
		case SE:
			newRoot.D = root;
			break;
		}

		/*
		 * // SAFE WAY TO MERGE WITH NEW ROOT, get all previous data and re-insert it.
		 * 
		 * ArrayList<QuadTreeData> prevData = new ArrayList<QuadTreeData>();
		 * 
		 * root.getAllData(prevData);
		 * 
		 * for (QuadTreeData d : prevData) { if (d != null) newRoot.insert(d); }
		 * 
		 * root.data.clear();
		 */

		root.parentNode = newRoot;
		root = newRoot;
	}

}