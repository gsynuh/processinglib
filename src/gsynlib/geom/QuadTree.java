package gsynlib.geom;

import processing.core.*;
import static processing.core.PApplet.*;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

import gsynlib.base.*;
import gsynlib.utils.*;

public class QuadTree extends GsynlibBase {

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
		n.id = 0;
		if (!n.isLeaf()) {
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

		Bounds b = new Bounds(position.x - radius, position.y - radius, radius * 2f, radius * 2f);

		root.queryData(b, circleDataQueryResults);

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
		root.queryData(b, output);
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
		return root.searchNN(position, null);
	}

	public QuadTreeData getNearestData(PVector position, QuadTreeData excludeData) {
		return root.searchNN(position, excludeData);
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

		float centerX = root.bounds.center.x;
		float centerY = root.bounds.center.y;

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

		newRoot.bounds.set(root.bounds.position.x + newRootPosition.x, root.bounds.position.y + newRootPosition.y,
				w * 2f, h * 2f);

		newRoot.split();

		// assign old root to a side of new root
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

		resolveParentNodes(newRoot, null);

		root = newRoot;
	}

	void resolveParentNodes(QuadTreeNode n, QuadTreeNode parent) {

		n.parentNode = parent;

		if (!n.isLeaf()) {
			resolveParentNodes(n.A, n);
			resolveParentNodes(n.B, n);
			resolveParentNodes(n.C, n);
			resolveParentNodes(n.D, n);
		}

	}

	public void render() {
		app().pushMatrix();
		app().pushStyle();
		renderNode(this.root);
		app().popStyle();
		app().popMatrix();
		this.resetVisited();
	}

	void renderNode(QuadTreeNode n) {

		app().stroke(200, 200);
		app().strokeWeight(4);
		for (QuadTreeData d : n.data) {
			app().point(d.position.x, d.position.y);
		}
		
		app().strokeWeight(1);

		if (n.id == 1)
			app().fill(255, 0, 0, 30);
		else if (n.id == 2)
			app().fill(0, 0, 255, 30);
		else
			app().noFill();

		app().rect(n.bounds.position.x, n.bounds.position.y, n.bounds.size.x, n.bounds.size.y);

		if (!n.isLeaf()) {
			renderNode(n.A);
			renderNode(n.B);
			renderNode(n.C);
			renderNode(n.D);
		}
	}

}