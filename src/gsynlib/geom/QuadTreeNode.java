package gsynlib.geom;

import java.util.*;

import processing.core.*;
import static processing.core.PApplet.*;

public class QuadTreeNode {

	public static int maxNodeDataNum = 4;

	public DIRECTION direction = DIRECTION.UNKNOWN;
	public QuadTreeNode parentNode = null;
	public Boolean visited = false;

	public static enum DIRECTION {
		UNKNOWN, N, NE, E, SE, S, SW, NW
	}

	// A B
	// C D

	public QuadTreeNode A;
	public QuadTreeNode B;
	public QuadTreeNode C;
	public QuadTreeNode D;
	public Bounds bounds = new Bounds();

	public Boolean isSplit = false;
	public ArrayList<QuadTreeData> data = new ArrayList<QuadTreeData>();

	public QuadTreeNode getNodeUnder(PVector pos) {
		return getNodeUnder(this, pos);
	}

	public QuadTreeNode getNodeUnder(QuadTreeNode n, PVector pos) {

		if (n.isSplit) {
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

	public QuadTreeData getClosestDataInSelf(PVector point) {
		return getClosestDataInCandidates(point, this.data);
	}

	static float sqrDist(PVector a, PVector b) {
		return (a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y);
	}

	public static QuadTreeData getClosestDataInCandidates(PVector point, ArrayList<QuadTreeData> list) {
		QuadTreeData result = null;
		float maxDist = Float.MAX_VALUE;
		for (QuadTreeData d : list) {
			float dist = sqrDist(d.position, point);
			if (dist < maxDist) {
				result = d;
				maxDist = dist;
			}
		}
		return result;
	}

	static ArrayList<QuadTreeData> NNCandidates = new ArrayList<QuadTreeData>();
	static ArrayList<QuadTreeNode> QNCandidates = new ArrayList<QuadTreeNode>();

	static void getNodeNeighbors(QuadTreeNode n, ArrayList<QuadTreeNode> output) {
		getNeighborsInDirection(n, DIRECTION.N, output);
	}

	static void getNeighborsInDirection(QuadTreeNode n, DIRECTION dir, ArrayList<QuadTreeNode> output) {
		if (n.parentNode == null)
			return; // ROOT

		if (!n.parentNode.isSplit) {
			return; // SOMETHING WRONG, OUR PARENT SHOULD BE SPLIT.
		}

		if (n != n.parentNode.A)
			output.add(n.parentNode.A);
		if (n != n.parentNode.B)
			output.add(n.parentNode.B);
		if (n != n.parentNode.C)
			output.add(n.parentNode.C);
		if (n != n.parentNode.D)
			output.add(n.parentNode.D);
	}

	public void searchKNN(PVector pos, int maxResults, ArrayList<QuadTreeData> output) {

	}

	public QuadTreeData searchNN(PVector position) {

		NNCandidates.clear();
		QNCandidates.clear();

		QuadTreeNode nodeUnder = this.getNodeUnder(position);
		nodeUnder.visited = true;

		if (nodeUnder.isSplit) {
			QNCandidates.add(nodeUnder.A);
			QNCandidates.add(nodeUnder.B);
			QNCandidates.add(nodeUnder.C);
			QNCandidates.add(nodeUnder.D);
		} else {
			QNCandidates.add(nodeUnder);
		}

		getNodeNeighbors(nodeUnder, QNCandidates);

		// MARK VISITED AND COLLECT DATA CANDIDATES
		for (QuadTreeNode q : QNCandidates) {
			q.visited = true;
			for (QuadTreeData d : q.data) {
				if (!NNCandidates.contains(d))
					NNCandidates.add(d);
			}
		}

		QuadTreeData result = getClosestDataInCandidates(position, NNCandidates);

		return result;
	}

	public void getAllData(ArrayList<QuadTreeData> output) {
		getAllData(this, output);
	}

	void getAllData(QuadTreeNode n, ArrayList<QuadTreeData> output) {
		if (n.isSplit) {
			getAllData(n.A, output);
			getAllData(n.B, output);
			getAllData(n.C, output);
			getAllData(n.D, output);
		} else {
			for (QuadTreeData d : n.data) {
				if (d != null)
					output.add(d);
			}
		}
	}

	// implement nearest neighbor search

	// RECT QUERY
	public void query(Bounds b, ArrayList<QuadTreeData> results) {
		query(this, b, results);
	}

	void query(QuadTreeNode n, Bounds b, ArrayList<QuadTreeData> results) {

		if (!n.bounds.Intersects(b)) {
			return;
		}

		n.visited = true;

		if (n.isSplit) {
			query(n.A, b, results);
			query(n.B, b, results);
			query(n.C, b, results);
			query(n.D, b, results);
		} else {
			for (QuadTreeData d : n.data) {
				if (d != null) {
					if (b.Contains(d.position))
						results.add(d);
				}
			}
		}
	}

	// CIRCLE QUERY
	// ConcurrentModificationException

	public Boolean insert(QuadTreeData d) {

		if (!this.bounds.Contains(d.position))
			return false;

		if (isSplit) {

			if (A.insert(d)) {
			} else if (B.insert(d)) {
			} else if (C.insert(d)) {
			} else if (D.insert(d)) {
			}

		} else {

			if (data.size() < PApplet.max(1, maxNodeDataNum)) {
				data.add(d);
			} else {
				this.split();
				return insert(d);
			}
		}

		return true;
	}

	public Boolean remove(QuadTreeData d) {
		if (!this.bounds.Contains(d.position))
			return false;

		Boolean r = false;

		for (Iterator<QuadTreeData> iterator = data.iterator(); iterator.hasNext();) {
			QuadTreeData da = iterator.next();
			if (da == d) {
				iterator.remove();
				r = true;
			}
		}

		if (r)
			collapse();

		return r;
	}

	ArrayList<QuadTreeData> collect = new ArrayList<QuadTreeData>();

	public void collapse() {
		if (this.parentNode == null) // ROOT
			return;

		collect.clear();
		this.parentNode.getAllData(collect);

		int countParentChildren = collect.size();

		if (countParentChildren <= maxNodeDataNum) {
			this.parentNode.A = null;
			this.parentNode.B = null;
			this.parentNode.C = null;
			this.parentNode.D = null;
			this.parentNode.isSplit = false;
			this.parentNode.data.addAll(collect);
			this.parentNode.collapse();
		}

	}

	public void split() {

		// CREATE ABCD and their bounds

		A = new QuadTreeNode();
		B = new QuadTreeNode();
		C = new QuadTreeNode();
		D = new QuadTreeNode();

		A.direction = DIRECTION.NW;
		B.direction = DIRECTION.NE;
		C.direction = DIRECTION.SW;
		D.direction = DIRECTION.SE;

		A.parentNode = this;
		B.parentNode = this;
		C.parentNode = this;
		D.parentNode = this;

		float w = this.bounds.size.x * 0.5f;
		float h = this.bounds.size.y * 0.5f;

		A.bounds = new Bounds(this.bounds.position.x, this.bounds.position.y, w, h);
		B.bounds = new Bounds(this.bounds.position.x + w, this.bounds.position.y, w, h);
		C.bounds = new Bounds(this.bounds.position.x, this.bounds.position.y + h, w, h);
		D.bounds = new Bounds(this.bounds.position.x + w, this.bounds.position.y + h, w, h);

		// FILL SUBDIVISION WITH DATA

		isSplit = true;

		ArrayList<QuadTreeData> splitData = new ArrayList();
		splitData.addAll(this.data);
		this.data.clear();

		for (QuadTreeData d : splitData) {
			this.insert(d);
		}

	}
}
