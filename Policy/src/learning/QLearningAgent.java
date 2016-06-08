package learning;

import java.util.Random;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Max
 */
public class QLearningAgent implements Agent {
  public QLearningAgent() {
    this.alpha = 0.1;
    this.gamma = 0.9;
    this.epsilon = 0.1;
    this.rand = new Random();

  }

  public void initialize(int numOfStates, int numOfActions) {
    this.numOfStates = numOfStates;
    this.numOfActions = numOfActions;
    this.Qtable = new double[this.numOfStates][this.numOfActions];
  }

  public int chooseAction(int state) {
    /* Find possible actions for this state, and their Q values */
    int bestAction = 0;
    
    /* Epsilon-Greedy Algorithm */
    if (rand.nextInt(10000) <= 10000*this.epsilon)
        bestAction = rand.nextInt(this.numOfActions);
    
    /* Otherwise iterate through Q matrix to find best action */
    else {
        for(int i = 0; i < this.numOfActions; i++) {
            if (this.Qtable[state][i] > this.Qtable[state][bestAction]) {
                bestAction = i;
            }
        }
    }
    return bestAction;
  }

  public void updatePolicy(double reward, int action, int oldState, int newState) {
      /* Q(a,s) = (1-alpha)Q(a,s)+alpha(R(s)+gamma(max Q(a',s'))) */
      double Q_curr = this.Qtable[oldState][action];
      double Q_next_max = calcMaxQ(newState);
      
      /* Update function for Q matrix */
      Qtable[oldState][action] = Q_curr + this.alpha*(reward + this.gamma*(Q_next_max) - Q_curr);
    return;
  }

  public Policy getPolicy() {
    int[] actions = new int[this.numOfStates];
    
    /* In each state, pick best action  and populate array */
    for (int i = 0; i < this.numOfStates; i++) {
      actions[i] = chooseAction(i);
    }

    /* Create Policy object from populated actions array */
    return new Policy(actions);
  }
  
  public double calcMaxQ(int newState) {
      double maxQ = Integer.MIN_VALUE;
      for (int i = 0; i < this.numOfActions; i++) {
          if (this.Qtable[newState][i] > maxQ){
                maxQ = this.Qtable[newState][i];
          }
      }
      return maxQ;
  }
  private int numOfStates;
  private int numOfActions;
  private double alpha;
  private double gamma;
  private double epsilon;
  private double[][] Qtable;
  private Random rand;
}
