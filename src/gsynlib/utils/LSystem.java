package gsynlib.utils;

import java.util.*;

import static processing.core.PApplet.*;

public class LSystem {
	
	ArrayList<Character> alphabet = new ArrayList<Character>();
	ArrayList<Character> axiom = new ArrayList<Character>();

	ArrayList<Character> backstate = new ArrayList<Character>();
	ArrayList<Character> nextstate = new ArrayList<Character>();
	
	HashMap<Character, LSRuleSet> rules = new HashMap<Character, LSRuleSet>();

	public float varA = 0;
	public float varB = 1;
	public float startAngle = 0;
	
	public int seed = 0;
	public Random rand;

	public LSystem() {
		rand = new Random();
	}

	public void setAxiom(String a) {
		axiom.clear();
		for (int i = 0; i < a.length(); i++) {
			axiom.add(a.charAt(i));
		}
	}

	public void setAlphabet(String a) {
		alphabet.clear();
		for (int i = 0; i < a.length(); i++) {
			alphabet.add(a.charAt(i));
		}
	}
	
	public int getSeed() {
		return this.seed;
	}

	public void setSeed(int seed) {
		this.seed = seed;
		rand.setSeed(seed);
		
		if(this.iteration > 0) {
			this.process(iteration);
		}
	}

	int iteration = -1;

	public int getIteration() {
		return iteration;
	}

	int currentBackIndex = 0;

	public int getCurrIndex() {
		return currentBackIndex;
	}

	public ArrayList<Character> getState() {
		if(this.nextstate.size()>0)
			return this.nextstate;
		else
			return this.backstate;
	}

	StringBuffer b = new StringBuffer();
	public String getStateString() {
		ArrayList<Character> s = getState();
		
		b.delete(0, b.length());
		int cu = 0;
		for(Character c : s) {
			
			b.append(c);
			
			cu++;
			
			if(cu > 100000)
				break;
		}
		
		return b.toString();
	}
	
	void nextToBack() {
		backstate.clear();
		backstate.addAll(nextstate);
		nextstate.clear();
	}

	// technically, one could decide to step through the full "backstate" sentence
	// processing continuously
	// But do it all at once with the next() function
	public void step() {

		if (iteration < 0) {
			reset();
			iteration = 0;
		}

		int len = backstate.size();

		if (currentBackIndex > len - 1) {
			nextToBack();
			currentBackIndex = 0;
			iteration++;
			return;
		}

		Character c = backstate.get(currentBackIndex);
		ArrayList<Character> e = evaluate(c);
		
		for(Character ec : e) {
			if(ec == null)
				continue;
			
			nextstate.add(ec);
		}

		currentBackIndex++;
	}

	public void process(int iter) {
		reset();
		while (iter != iteration) {
			step();
		}
	}

	// process the entire current state with the rules
	public void next() {

		if (iteration < 0) {
			reset();
			iteration = 0;
		}

		int currentIteration = iteration;
		while (currentIteration == iteration) {
			step();
		}
	}
	
	ArrayList<Character> results = new ArrayList<Character>();

	// evaluate given 'c' input against rules
	public ArrayList<Character> evaluate(Character c) {

		results.clear();
		results.add(c);
		
		if (!alphabet.contains(c)) {
			println("Bad char found '" + c + "' , make sure the alphabet is complete!");
			return results;
		}

		LSRuleSet rs = rules.get(c);
		if (rs != null) {
			LSRule r = rs.getRule();
			return r.out;
		}

		return results;
	}

	public void addRule(String in, String out) {
		addRule(in, out, 1f);
	}

	public void addRule(String in, String out, float chance) {
		
		if(in.length() > 1)
		{
			println("Not adding rule " + in + " because input is longer than one char.");
			return;
		}
		
		LSRule r = new LSRule();
		
		out = out.trim();
		
		r.out.clear();
		
		for(int i = 0; i < out.length(); i++) {
			r.out.add(out.charAt(i));
		}

		r.chance = chance;
		
		Character i = in.trim().charAt(0);
		
		LSRuleSet rs = rules.get(i);
		if (rs == null) {
			rs = new LSRuleSet(this);
			rules.put(i, rs);
		}
		rs.addRule(r);
	}

	public void clearRules() {
		rules.clear();
	}

	public void reset() {
		iteration = -1;
		currentBackIndex = 0;
		
		this.setSeed(this.seed);
		
		backstate.clear();
		backstate.addAll(axiom);
		nextstate.clear();
	}
}

class LSRuleSet {
	LSystem sys;
	ArrayList<LSRule> rules = new ArrayList<LSRule>();
	float totalW = 0;

	public LSRuleSet(LSystem s) {
		this.sys = s;
	}

	public void addRule(LSRule r) {
		rules.add(r);
		calcWeights();
	}

	void calcWeights() {
		totalW = 0;

		for (LSRule r : rules)
			totalW += r.chance;
	}

	public LSRule getRule() {
		if (rules.size() == 1) {
			return rules.get(0);
		}

		double random = sys.rand.nextDouble() * totalW;
		int randomIndex = 0;
		for (int i = 0; i < rules.size(); ++i) {
			random -= rules.get(i).chance;
			if (random <= 0.0d) {
				randomIndex = i;
				break;
			}
		}

		return rules.get(randomIndex);
	}
}

class LSRule {
	public float chance = 1;
	public ArrayList<Character> out = new ArrayList<Character>();
}
