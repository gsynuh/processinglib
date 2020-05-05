package gsynlib.utils;

import java.lang.reflect.*;
import java.util.*;

import static processing.core.PApplet.*;

public class Grid<T extends Object> {
	
	int width = 0;
	int height = 0;
	int size = 0;
	
	public int width() {
		return width;
	}
	
	public int height() {
		return height;
	}
	
	public int size() {
		return size;
	}
	
	public Boolean loop = false;
	
	T[] grid;
	
	@SuppressWarnings("unchecked")
	public Grid(int w, int h) {
		this.width = w;
		this.height = h;
		this.size = w * h;
		
		this.grid = (T[]) Array.newInstance(Object.class, this.size );
		
		for(int i = 0; i < this.size; i++) {
			this.grid[i] = null;
		}
	}
	
	int mod(int v, int s) {
		
		if(v < 0)
			v = s + v;
		
		v = v%s;
		
		return v;
	}
	
	int toIndex(int x, int y) {
		
		if(loop) {
			x = mod(x,width);
			y = mod(y,height);
		}else {
			if(x < 0 || x > width || y < 0 || y > height) 
				return -1;
		}
		
		return x + y*width;
	}
	
	public T set(int x, int y, T data) {
		int index = toIndex(x,y);
		
		if(index < 0 || index > size)
			return null;
		
		return grid[index] = data;
	}
	
	public T set(int index, T data) {
		return grid[index] = data;
	}
	
	public T get(int x, int y) {
		int index = toIndex(x,y);
		
		if(index < 0 || index > size)
			return null;
		
		return grid[index];
	}
	
	public T get(int index) {
		
		if(loop)
			index = index % size;
		
		return grid[index];
	}
	
}
