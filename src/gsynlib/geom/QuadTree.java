package gsynlib.geom;

import processing.core.*;
import static processing.core.PApplet.*;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

import gsynlib.base.*;
import gsynlib.utils.*;

public class QuadTree<T extends QuadTreeData> extends GsynlibBase {

	QuadTreeNode<T> root;
	ReentrantLock qtlock = new ReentrantLock();

	// HELPER LISTS
	public ArrayList<T> NNCandidates = new ArrayList<T>();
	public ArrayList<QuadTreeNode> QNCandidates = new ArrayList<QuadTreeNode>();

	public QuadTreeNode<T> getRoot() {
		return this.root;
	}
	
	public QuadTree(Bounds initialBounds) {
		create(initialBounds);
	}

	public QuadTree(float _x,float _y, float _w, float _h) {
		create(new Bounds(_x,_y,_w,_h));
	}
	
	void create(Bounds initialBounds) {
		root = new QuadTreeNode<T>();
		root.tree = this;
		root.bounds.set(initialBounds);
	}

	public void resetVisited() {
		resetVisited(root);
	}

	void resetVisited(QuadTreeNode<T> n) {
		n.id = 0;
		if (!n.isLeaf()) {
			resetVisited(n.A);
			resetVisited(n.B);
			resetVisited(n.C);
			resetVisited(n.D);
		}
	}

	public ArrayList<T> queryCircle(PVector position, float radius) {
		ArrayList<T> results = new ArrayList<T>();
		queryCircle(position, radius, results);
		return results;
	}

	ArrayList<T> circleDataQueryResults = new ArrayList<T>();

	public void queryCircle(PVector position, float radius, ArrayList<T> output) {
		output.clear();
		circleDataQueryResults.clear();

		Bounds b = new Bounds(position.x - radius, position.y - radius, radius * 2f, radius * 2f);

		root.queryData(b, circleDataQueryResults);

		float sqrRadius = radius * radius;

		for (T d : circleDataQueryResults) {
			if (GApp.sqrDist(d.position, position) <= sqrRadius) {
				output.add(d);
			}
		}

		circleDataQueryResults.clear();
	}

	public ArrayList<T> queryBounds(Bounds b) {
		ArrayList<T> results = new ArrayList<T>();
		queryBounds(b, results);
		return results;
	}

	public void queryBounds(Bounds b, ArrayList<T> output) {
		output.clear();
		root.queryData(b, output);
	}

	public void updateData(T d) {
		qtlock.lock();
		try {
			remove(d);
			insert(d);
		} finally {
			qtlock.unlock();
		}
	}

	public Boolean insert(T data) {
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

	public void remove(T d) {
		qtlock.lock();
		try {

			QuadTreeNode<?> dataNode = d.node;

			if (dataNode != null)
				dataNode.remove(d);
			else
				println("couldn't find node containing data " + d.position);

		} finally {
			qtlock.unlock();
		}
	}

	public void clear() {
		// RECURSIVELY GET ALL ROOT DATA AND REMOVE AS WE USUALLY WOULD.
		// BRUTEFORCE VERSION TO MAKE SURE CLEANUP IS DONE RIGHT, BUT SLOW.
		ArrayList<T> output = new ArrayList<T>();
		root.getAllData(output);
		for (T d : output) {
			this.remove(d);
		}
	}

	public QuadTreeData getNearestData(PVector position) {
		return root.searchNN(position, null);
	}

	public QuadTreeData getNearestData(PVector position, QuadTreeData excludeData) {
		return root.searchNN(position, excludeData);
	}

	public QuadTreeNode<T> getNodeUnder(PVector position) {
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

		n.tree = this;

		if (!n.isLeaf()) {
			resolveParentNodes(n.A, n);
			resolveParentNodes(n.B, n);
			resolveParentNodes(n.C, n);
			resolveParentNodes(n.D, n);
		}

	}

	public Boolean debugDrawData = true;
	public Boolean debugDrawNodes = true;
	public Boolean debugDrawVectors = true;
	public Boolean debugDrawVisited = true;
	public float debugVectorScale = 1f;
	public float debugLineScale = 1f;

	public void render() {
		app().pushMatrix();
		app().pushStyle();
		renderNode(this.root);
		app().popStyle();
		app().popMatrix();
		this.resetVisited();
	}

	void renderNode(QuadTreeNode<T> n) {

		app().stroke(255, 0, 0, 200);
		for (T d : n.data) {

			if (debugDrawVectors) {
				app().strokeWeight(debugLineScale);
				if (d instanceof QuadTreeDataVector) {
					QuadTreeDataVector v = (QuadTreeDataVector) d;
					app().line(v.position.x, v.position.y, v.position.x + v.vector.x * debugVectorScale,
							v.position.y + v.vector.y * debugVectorScale);
				}
			}

			if (debugDrawData) {
				app().strokeWeight(debugLineScale * 4);
				app().point(d.position.x, d.position.y);
			}

		}

		app().stroke(200, 200);

		if (debugDrawNodes) {
			app().strokeWeight(debugLineScale);
			app().noFill();

			if (debugDrawVisited) {
				if (n.id == 1)
					app().fill(255, 0, 0, 30);
				else if (n.id == 2)
					app().fill(0, 0, 255, 30);
			}

			app().rect(n.bounds.position.x, n.bounds.position.y, n.bounds.size.x, n.bounds.size.y);
		}

		if (!n.isLeaf()) {
			renderNode(n.A);
			renderNode(n.B);
			renderNode(n.C);
			renderNode(n.D);
		}
	}

}