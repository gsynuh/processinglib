package gsynlib.geom;

import java.util.*;

import gsynlib.utils.*;
import processing.core.*;
import static processing.core.PApplet.*;

public class QuadTreeNode<T extends QuadTreeData> {

	public static int maxNodeDataNum = 4;
	public static Boolean speedHack = true;

	public QuadTree<T> tree = null;
	public QuadTreeNode<T> parentNode = null;
	public int id = 0;

	public static enum DIRECTION {
		UNKNOWN, N, NE, E, SE, S, SW, W, NW
	}

	// A B
	// C D

	public QuadTreeNode<T> A;
	public QuadTreeNode<T> B;
	public QuadTreeNode<T> C;
	public QuadTreeNode<T> D;
	public Bounds bounds = new Bounds();

	public Boolean isLeaf() {
		return (A == null || B == null || C == null || D == null);
	}

	public ArrayList<T> data = new ArrayList<T>();

	public QuadTreeNode getNodeUnder(PVector pos) {
		return getNodeUnder(this, pos);
	}

	public QuadTreeNode getNodeUnder(QuadTreeNode n, PVector pos) {

		if (!n.isLeaf()) {
			if (A.bounds.Contains(pos)) {
				return A.getNodeUnder(A, pos);
			} else if (B.bounds.Contains(pos)) {
				return B.getNodeUnder(B, pos);
			} else if (C.bounds.Contains(pos)) {
				return C.getNodeUnder(C, pos);
			} else if (D.bounds.Contains(pos)) {
				return D.getNodeUnder(D, pos);
			} else {
				return n;
			}

		} else {
			return n;
		}
	}

	public DIRECTION getDirection() {
		if (this.parentNode == null)
			return DIRECTION.UNKNOWN;

		if (this == this.parentNode.A)
			return DIRECTION.NW;
		if (this == this.parentNode.B)
			return DIRECTION.NE;
		if (this == this.parentNode.C)
			return DIRECTION.SW;
		if (this == this.parentNode.D)
			return DIRECTION.SE;

		return DIRECTION.UNKNOWN;
	}

	QuadTreeData getClosestDataInCandidates(PVector point, ArrayList<T> list) {
		QuadTreeData result = null;
		float maxDist = Float.MAX_VALUE;
		for (QuadTreeData d : list) {
			float dist = GApp.sqrDist(d.position, point);
			if (dist < maxDist) {
				result = d;
				maxDist = dist;
			}
		}
		return result;
	}

	Bounds nnBounds = new Bounds();

	public QuadTreeData searchNN(PVector position, QuadTreeData excludeData) {

		tree.NNCandidates.clear();
		tree.QNCandidates.clear();

		QuadTreeNode nodeUnder = this.getNodeUnder(position);

		nodeUnder.id = 2;

		getNodeNeighborsCardinal(nodeUnder, tree.QNCandidates);

		tree.QNCandidates.add(nodeUnder);

		if (nodeUnder.parentNode != null) {

			nnBounds.set(nodeUnder.bounds);

			float biggestNDim = 0;

			for (QuadTreeNode n : tree.QNCandidates) {
				nnBounds.Encapsulate(n.bounds);

				if (n == nodeUnder)
					continue;

				if (n.bounds.size.x > biggestNDim) {
					biggestNDim = n.bounds.size.x;
				}
			}

			// WORST HACK POSSIBLE TO EXCLUDE EXTRA NODES*
			// TODO: fix that by testing bounds differently or instead of intersection query
			// do it with inclusive "contains"
			if (speedHack)
				nnBounds.InflateFromCenter(-0.0001f);

			// ALGORITHM ISN'T SUCCESSFUL IF QUAD IS SURROUNDED BY RECURSIVELY EMPTY
			// NEIGHBORS, SO INFLATE BOUNDS UTIL YOU GET SOMETHING
			while (tree.NNCandidates.size() < 1) {
				this.queryData(nnBounds, tree.NNCandidates);
				nnBounds.InflateFromCenter(biggestNDim);
			}

		} else { // POINT IN ROOT.
			tree.NNCandidates.addAll(nodeUnder.data);
		}

		if (excludeData != null) {
			tree.NNCandidates.remove(excludeData);
		}

		QuadTreeData result = getClosestDataInCandidates(position, tree.NNCandidates);

		return result;
	}

	synchronized static void getNodeNeighborsCardinal(QuadTreeNode n, ArrayList<QuadTreeNode> output) {
		getNeighborsInDirection(n, DIRECTION.N, output);
		getNeighborsInDirection(n, DIRECTION.E, output);
		getNeighborsInDirection(n, DIRECTION.S, output);
		getNeighborsInDirection(n, DIRECTION.W, output);
	}

	static ArrayList<QuadTreeNode> neighborCandidates = new ArrayList<QuadTreeNode>();

	static void getNeighborsInDirection(QuadTreeNode n, DIRECTION dir, ArrayList<QuadTreeNode> output) {
		QuadTreeNode neighbor = getEqualOrBigger(n, dir);
		getSmaller(neighbor, dir, output);
	}

	static QuadTreeNode getEqualOrBigger(QuadTreeNode n, DIRECTION dir) {
		if (n == null || n.parentNode == null)
			return null;

		QuadTreeNode node = null;

		// CARDINAL DIRS

		if (dir == DIRECTION.N) {
			// GOING N, IF SELF IS OPPOSITE, ADD SYMMETRIC EQUIVALENT.
			if (n.getDirection() == DIRECTION.SW)
				return n.parentNode.A;
			if (n.getDirection() == DIRECTION.SE)
				return n.parentNode.B;

			node = getEqualOrBigger(n.parentNode, dir);

			if (node == null || node.isLeaf())
				return node;

			// NODE IS NOW NORTH, DISCRIMINATE W/E AND ADD NODE'S OPPOSITE (C = SW, D = SE)
			if (n.getDirection() == DIRECTION.NW)
				return node.C;
			else
				return node.D;

		} else if (dir == DIRECTION.S) {

			if (n.getDirection() == DIRECTION.NW)
				return n.parentNode.C;
			if (n.getDirection() == DIRECTION.NE)
				return n.parentNode.D;

			node = getEqualOrBigger(n.parentNode, dir);

			if (node == null || node.isLeaf())
				return node;

			if (n.getDirection() == DIRECTION.SW)
				return node.A;
			else
				return node.B;

		} else if (dir == DIRECTION.W) {

			if (n.getDirection() == DIRECTION.NE)
				return n.parentNode.A;
			if (n.getDirection() == DIRECTION.SE)
				return n.parentNode.C;

			node = getEqualOrBigger(n.parentNode, dir);

			if (node == null || node.isLeaf())
				return node;

			if (n.getDirection() == DIRECTION.NW)
				return node.B;
			else
				return node.D;

		} else if (dir == DIRECTION.E) {

			if (n.getDirection() == DIRECTION.NW)
				return n.parentNode.B;
			if (n.getDirection() == DIRECTION.SW)
				return n.parentNode.D;

			node = getEqualOrBigger(n.parentNode, dir);

			if (node == null || node.isLeaf())
				return node;

			if (n.getDirection() == DIRECTION.NE)
				return node.A;
			else
				return node.C;

		}

		return null;
	}

	static void getSmaller(QuadTreeNode neighbor, DIRECTION dir, ArrayList<QuadTreeNode> output) {
		neighborCandidates.clear();

		if (neighbor != null)
			neighborCandidates.add(neighbor);

		while (neighborCandidates.size() > 0) {
			QuadTreeNode c = neighborCandidates.get(0);

			if (c.isLeaf()) {
				output.add(c);
			} else {
				if (dir == DIRECTION.N) {
					neighborCandidates.add(c.C);
					neighborCandidates.add(c.D);
				} else if (dir == DIRECTION.S) {
					neighborCandidates.add(c.A);
					neighborCandidates.add(c.B);
				} else if (dir == DIRECTION.W) {
					neighborCandidates.add(c.B);
					neighborCandidates.add(c.D);
				} else if (dir == DIRECTION.E) {
					neighborCandidates.add(c.A);
					neighborCandidates.add(c.C);
				}
			}

			neighborCandidates.remove(0);
		}

	}

	public void getAllData(ArrayList<T> output) {
		getAllData(this, output);
	}

	void getAllData(QuadTreeNode<T> n, ArrayList<T> output) {
		
		for (T d : n.data) {
			if (d != null)
				output.add(d);
		}
		
		if (!n.isLeaf()) {
			getAllData(n.A, output);
			getAllData(n.B, output);
			getAllData(n.C, output);
			getAllData(n.D, output);
		}
	}

	// RECT QUERY
	public void queryData(Bounds b, ArrayList<T> results) {
		queryData(this, b, results);
	}

	public void queryNodes(Bounds b, ArrayList<QuadTreeNode<T>> results) {
		queryNodes(this, b, results);
	}

	void queryNodes(QuadTreeNode<T> n, Bounds b, ArrayList<QuadTreeNode<T>> results) {

		if (!n.bounds.Intersects(b)) {
			return;
		}

		if (!n.isLeaf()) {
			queryNodes(n.A, b, results);
			queryNodes(n.B, b, results);
			queryNodes(n.C, b, results);
			queryNodes(n.D, b, results);
		} else {
			n.id = 2;
			results.add(n);
		}
	}

	void queryData(QuadTreeNode<T> n, Bounds b, ArrayList<T> results) {

		if (!n.bounds.Intersects(b)) {
			return;
		}

		if (!n.isLeaf()) {
			queryData(n.A, b, results);
			queryData(n.B, b, results);
			queryData(n.C, b, results);
			queryData(n.D, b, results);
		} else {
			n.id = 2;
			for (T d : n.data) {
				if (d != null) {
					if (b.Contains(d.position))
						results.add(d);
				}
			}
		}
	}

	public Boolean insert(T d) {

		if (!this.bounds.Contains(d.position))
			return false;

		if (!isLeaf()) {

			if (A.insert(d)) {
			} else if (B.insert(d)) {
			} else if (C.insert(d)) {
			} else if (D.insert(d)) {
			}

		} else {

			if (this.data.size() < PApplet.max(1, maxNodeDataNum)) {
				if (!this.data.contains(d)) {
					d.node = this;
					this.data.add(d);
				}
			} else {
				this.split();
				return insert(d);
			}
		}

		return true;
	}

	public Boolean remove(QuadTreeData d) {

		Boolean r = false;

		for (Iterator<T> iterator = data.iterator(); iterator.hasNext();) {
			T da = iterator.next();
			if (da == d) {
				iterator.remove();
				da.node = null;
				r = true;
			}
		}

		if (r)
			collapse();

		return r;
	}

	ArrayList<T> collect = new ArrayList<T>();
	ArrayList<T> splitData = new ArrayList<T>();

	public void collapse() {
		if (this.parentNode == null) // ROOT
			return;

		collect.clear();
		this.parentNode.getAllData(collect);

		int countParentChildren = collect.size();

		if (countParentChildren < maxNodeDataNum) {
			this.parentNode.A = null;
			this.parentNode.B = null;
			this.parentNode.C = null;
			this.parentNode.D = null;
					
			this.parentNode.data.clear();
			
			for (T d : collect) {
				this.parentNode.insert(d);
			}

			this.parentNode.collapse();

		}

		collect.clear();
	}

	public void split() {

		// CREATE ABCD and their bounds

		A = new QuadTreeNode<T>();
		B = new QuadTreeNode<T>();
		C = new QuadTreeNode<T>();
		D = new QuadTreeNode<T>();

		A.parentNode = this;
		B.parentNode = this;
		C.parentNode = this;
		D.parentNode = this;

		A.tree = this.tree;
		B.tree = this.tree;
		C.tree = this.tree;
		D.tree = this.tree;

		float w = this.bounds.size.x * 0.5f;
		float h = this.bounds.size.y * 0.5f;

		A.bounds = new Bounds(this.bounds.position.x, this.bounds.position.y, w, h);
		B.bounds = new Bounds(this.bounds.position.x + w, this.bounds.position.y, w, h);
		C.bounds = new Bounds(this.bounds.position.x, this.bounds.position.y + h, w, h);
		D.bounds = new Bounds(this.bounds.position.x + w, this.bounds.position.y + h, w, h);

		// FILL SUBDIVISION WITH DATA

		splitData.clear();
		splitData.addAll(this.data);
		this.data.clear();

		for (T d : splitData) {
			this.insert(d);
		}
		splitData.clear();
	}
}
