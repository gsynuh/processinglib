package gsynlib.geom;

import java.util.*;

import processing.core.*;
import static processing.core.PApplet.*;

public class QuadTreeNode {

	public static int maxNodeDataNum = 4;

	public int x;
	public int y;

	// A B
	// C D

	public QuadTreeNode A;
	public QuadTreeNode B;
	public QuadTreeNode C;
	public QuadTreeNode D;
	public Bounds bounds = new Bounds();

	public Boolean isSplit = false;
	public ArrayList<QuadTreeData> data = new ArrayList<QuadTreeData>();

	public QuadTreeNode search(PVector pos) {
		return search(this, pos);
	}

	public QuadTreeNode search(QuadTreeNode n, PVector pos) {

		if (n.isSplit) {
			if (A.bounds.Contains(pos)) {
				return A.search(A, pos);
			} else if (B.bounds.Contains(pos)) {
				return B.search(B, pos);
			} else if (C.bounds.Contains(pos)) {
				return C.search(C, pos);
			} else if (D.bounds.Contains(pos)) {
				return D.search(D, pos);
			} else {
				return n;
			}

		} else {
			return n;
		}
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
