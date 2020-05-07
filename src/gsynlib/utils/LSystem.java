package gsynlib.utils;

import java.util.*; 

import static processing.core.PApplet.*;

public class LSystem {

  public String alphabet = ""; // Alphabet
  public String axiom = "A";
  public float varA = 0;

  public Random rand; 

  public LSystem() {
    rand = new Random();
  }

  public void setSeed(int seed) {
    rand.setSeed(seed);
  }

  int iteration = -1;
  public int getIteration() {
    return iteration;
  }

  int currentBackIndex = 0;
  public int getCurrIndex() {
    return currentBackIndex;
  }

  String backstate = "";
  String nextstate = "";

  public String getState() {
    return this.backstate;
  }

  public String getCurrentState() {
    return this.nextstate;
  }

  HashMap<String, LSRuleSet> rules = new HashMap<String, LSRuleSet>();

  //technically, one could decide to step through the full "backstate" sentence processing continuously
  //But do it all at once with the next() function
  public void step() {

    if (iteration <0) {
      reset();
      iteration = 0;
    }

    int len = backstate.length();

    if (currentBackIndex > len - 1) {
      backstate = nextstate+"";
      nextstate = "";
      currentBackIndex = 0;
      iteration++;
      return;
    }

    String c = backstate.substring(currentBackIndex, currentBackIndex + 1);
    nextstate += evaluate(c);

    currentBackIndex++;
  }

  public void process(int iter) {
    reset();
    while (iter != iteration) {
      step();
    }
  }

  //process the entire current state with the rules
  public void next() {

    if (iteration <0) {
      reset();
      iteration = 0;
    }

    int currentIteration = iteration;
    while (currentIteration == iteration) {
      step();
    }
  }


  //evaluate given 'c' input against rules
  public String evaluate(String c) {

    if (!alphabet.contains(c))
    {
      println("Bad char found '"+c+"' , make sure the alphabet is complete!");
      return c;
    }

    LSRuleSet rs = rules.get(c);
    if (rs != null) {
      LSRule r = rs.getRule();
      return r.out;
    }

    return c;
  }

  public void addRule(String in, String out) {
    addRule(in, out, 1f);
  }

  public void addRule(String in, String out, float chance) {
    LSRule r = new LSRule();
    r.out = out.trim();
    r.chance = chance;
    String i = in.trim();
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
    backstate = axiom;
    nextstate = "";
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
    for (int i = 0; i < rules.size(); ++i)
    {
      random -= rules.get(i).chance;
      if (random <= 0.0d)
      {
        randomIndex = i;
        break;
      }
    }

    return rules.get(randomIndex);
  }
}

class LSRule {
  public float chance = 1;
  public String out = "";
}
