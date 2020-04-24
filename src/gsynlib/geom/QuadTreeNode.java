package gsynlib.geom;

import java.util.*;

import gsynlib.utils.*;
import processing.core.*;
import static processing.core.PApplet.*;

public class QuadTreeNode {

	public static int maxNodeDataNum = 4;

	public QuadTreeNode parentNode = null;
	
	public int id = 0;
	
	public static enum DIRECTION {
		UNKNOWN, N, NE, E, SE, S, SW, W, NW
	}

	// A B
	// C D

	public QuadTreeNode A;
	public QuadTreeNode B;
	public QuadTreeNode C;
	public QuadTreeNode D;
	public Bounds bounds = new Bounds();
	
	public Boolean isLeaf() {
		return (A == null || B == null || C == null || D == null);
	}

	public ArrayList<QuadTreeData> data = new ArrayList<QuadTreeData>();
	public int totalDataCount = 0;

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

	public QuadTreeData getClosestDataInSelf(PVector point) {
		return getClosestDataInCandidates(point, this.data);
	}
	
	public static QuadTreeData getClosestDataInCandidates(PVector point, ArrayList<QuadTreeData> list) {
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

	static ArrayList<QuadTreeData> NNCandidates = new ArrayList<QuadTreeData>();
	static ArrayList<QuadTreeNode> QNCandidates = new ArrayList<QuadTreeNode>();

	static void getNodeNeighbors(QuadTreeNode n, ArrayList<QuadTreeNode> output) {
		output.clear();
		getNeighborsInDirection(n, DIRECTION.N, output);
		getNeighborsInDirection(n, DIRECTION.NE, output);
		getNeighborsInDirection(n, DIRECTION.E, output);
		getNeighborsInDirection(n, DIRECTION.SE, output);
		getNeighborsInDirection(n, DIRECTION.SW, output);
		getNeighborsInDirection(n, DIRECTION.W, output);
	}
	
	static void getNeighborsInDirection(QuadTreeNode n, DIRECTION dir, ArrayList<QuadTreeNode> output) {
		QuadTreeNode neighbor = getNofGreaterOrEqualSize(n,dir);
		getNofSmallerSizes(n,neighbor,dir,output);
	}
	
	public DIRECTION getDirection() {
		if(this.parentNode == null)
			return DIRECTION.UNKNOWN;
		
		if(this == this.parentNode.A)
			return DIRECTION.NW;
		if(this == this.parentNode.B)
			return DIRECTION.NE;
		if(this == this.parentNode.C)
			return DIRECTION.SW;
		if(this == this.parentNode.D)
			return DIRECTION.SE;
		
		return DIRECTION.UNKNOWN;
	}
	
	static QuadTreeNode getNofGreaterOrEqualSize(QuadTreeNode n, DIRECTION dir) {
		if(n == null)
			return n;
		
		if(dir == DIRECTION.N) {
			
			if(n.parentNode == null)
				return null;
			
			n.id = 1;

			if(n.getDirection() == DIRECTION.SW)
				return n.parentNode.A; //NW
			
			if(n.getDirection() == DIRECTION.SE)
				return n.parentNode.B; //NE
			
			QuadTreeNode node = getNofGreaterOrEqualSize(n.parentNode,dir);
			if(node == null || node.isLeaf())
				return node;
			
			//SHOULD BE NORTH
			if(n.getDirection() == DIRECTION.NW)
				return node.C;
			else // NE
				return node.D;
		}
		
		return null;
	}

	static ArrayList<QuadTreeNode> candidates = new ArrayList<QuadTreeNode>();
	static void getNofSmallerSizes(QuadTreeNode n,QuadTreeNode neighbor, DIRECTION dir, ArrayList<QuadTreeNode> output) {
		if(n == null)
			return;
		
		candidates.clear();

		if(neighbor != null)
			candidates.add(neighbor);
		
		if(dir == DIRECTION.N) {
			while(candidates.size() > 0) {
				QuadTreeNode c = candidates.get(0);
				if(c.isLeaf()) {
					c.id = 2;
					output.add(c);
				}else {
					candidates.add(c.C);
					candidates.add(c.D);
				}
				
				candidates.remove(0);
			}
			
		}
		
	}

	public QuadTreeData searchNN(PVector position) {

		NNCandidates.clear();
		QNCandidates.clear();

		QuadTreeNode nodeUnder = this.getNodeUnder(position);

		getNodeNeighbors(nodeUnder, QNCandidates);

		// MARK VISITED AND COLLECT DATA CANDIDATES
		for (QuadTreeNode q : QNCandidates) {
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
		if (!n.isLeaf()) {
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
	
	// RECT QUERY
	public void query(Bounds b, ArrayList<QuadTreeData> results) {
		query(this, b, results);
	}

	void query(QuadTreeNode n, Bounds b, ArrayList<QuadTreeData> results) {

		if (!n.bounds.Intersects(b)) {
			return;
		}

		if (!n.isLeaf()) {
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

	public Boolean insert(QuadTreeData d) {

		if (!this.bounds.Contains(d.position))
			return false;

		if (!isLeaf()) {

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
	ArrayList<QuadTreeData> splitData = new ArrayList();
	
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
			this.parentNode.data.addAll(collect);
			this.parentNode.collapse();
		}
		
		collect.clear();
	}

	public void split() {

		// CREATE ABCD and their bounds

		A = new QuadTreeNode();
		B = new QuadTreeNode();
		C = new QuadTreeNode();
		D = new QuadTreeNode();

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

		splitData.clear();
		splitData.addAll(this.data);
		this.data.clear();

		for (QuadTreeData d : splitData) {
			this.insert(d);
		}
		splitData.clear();	
	}
}
