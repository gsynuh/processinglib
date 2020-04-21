package gsynlib.geom;

import java.util.*;

import processing.core.*;
import static processing.core.PApplet.*;

public class QuadTreeNode {

	public static int maxNodeDataNum = 4;
	
	public QuadTreeNode parentNode = null;
	public Boolean visited = false;
	
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
		return getClosestDataInCandidates(point,this.data);
	}
	
	static float sqrDist(PVector a, PVector b) {
		return (a.x-b.x)*(a.x-b.x) + (a.y-b.y)*(a.y-b.y);
	}
	
	public static QuadTreeData getClosestDataInCandidates(PVector point, ArrayList<QuadTreeData> list) {
		QuadTreeData result = null;
		float maxDist = Float.MAX_VALUE;
		for(QuadTreeData d : list) {
			float dist = sqrDist(d.position,point);
			if(dist < maxDist) {
				result = d;
				maxDist = dist;
			}
		}
		return result;
	}
	
	static ArrayList<QuadTreeData> NNCandidates = new ArrayList<QuadTreeData>();
	static ArrayList<QuadTreeNode> QNCandidates = new ArrayList<QuadTreeNode>();
	
	public QuadTreeData searchNN(PVector position) {
		
		NNCandidates.clear();
		QNCandidates.clear();
		
		QuadTreeNode nodeUnder = this.getNodeUnder(position);
		nodeUnder.visited = true;
		
		if(nodeUnder.isSplit) {
			QNCandidates.add(nodeUnder.A);
			QNCandidates.add(nodeUnder.B);
			QNCandidates.add(nodeUnder.C);
			QNCandidates.add(nodeUnder.D);
		}else {
			QNCandidates.add(nodeUnder);
		}
		
		
		//MARK VISITED AND COLLECT DATA CANDIDATES
		for(QuadTreeNode q : QNCandidates) {
			q.visited = true;
			for(QuadTreeData d : q.data) {
				NNCandidates.add(d);
			}
		}
		
		QuadTreeData result = getClosestDataInCandidates(position,NNCandidates);

		return result;
	}
	
	public void getAllData(ArrayList<QuadTreeData> output) {
		getAllData(this,output);
	}
	
	void getAllData(QuadTreeNode n , ArrayList<QuadTreeData> output) {
		if(n.isSplit) {
			getAllData(n.A,output);
			getAllData(n.B,output);
			getAllData(n.C,output);
			getAllData(n.D,output);
		}else {
			for(QuadTreeData d : n.data) {
				output.add(d);
			}
		}
	}

	public void insert(PVector pos, Object obj) {
		if (isSplit) {

			if (A.bounds.Contains(pos))
				A.insert(pos, obj);
			else if (B.bounds.Contains(pos))
				B.insert(pos, obj);
			else if (C.bounds.Contains(pos))
				C.insert(pos, obj);
			else if (D.bounds.Contains(pos))
				D.insert(pos, obj);

		} else {
			
			if (data.size() < PApplet.max(1, maxNodeDataNum)) {
				QuadTreeData d = new QuadTreeData();
				d.position.set(pos);
				d.object = obj;
				data.add(d);
			} else {
				this.split();
				insert(pos, obj);
			}
		}
	}

	void recalcBounds() {

		if (isSplit) {

			this.bounds.copyFrom(A.bounds);

			this.bounds.Encapsulate(A.bounds);
			this.bounds.Encapsulate(B.bounds);
			this.bounds.Encapsulate(C.bounds);
			this.bounds.Encapsulate(D.bounds);
		}
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

		int w = floor(this.bounds.size.x * 0.5f);
		int h = floor(this.bounds.size.y * 0.5f);

		A.bounds = new Bounds(this.bounds.position.x, this.bounds.position.y, w, h);
		B.bounds = new Bounds(this.bounds.position.x + w, this.bounds.position.y, w, h);
		C.bounds = new Bounds(this.bounds.position.x, this.bounds.position.y + h, w, h);
		D.bounds = new Bounds(this.bounds.position.x + w, this.bounds.position.y + h, w, h);

		A.bounds.floorValues();
		B.bounds.floorValues();
		C.bounds.floorValues();
		D.bounds.floorValues();

		recalcBounds();

		// FILL SUBDIVISION WITH DATA

		isSplit = true;

		QuadTree.dataPool.clear();
		for (QuadTreeData d : this.data) {
			QuadTree.dataPool.add(d);
		}

		this.data.clear();

		for (QuadTreeData d : QuadTree.dataPool) {
			this.insert(d.position, d);
		}

		QuadTree.dataPool.clear();

	}
}
