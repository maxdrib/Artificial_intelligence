/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bayesiannets;

import java.util.Arrays;
import java.util.Random;

/**
 *
 * @author Max
 */
public class BayesianNets {
    public static boolean[] buildSample() {
        boolean[] sample = new boolean[num_vars];
        int A = 0;
        int B = 1;
        int C = 2;
        int D = 3;
        int E = 4;
        int F = 5;
        int G = 6;
        
        // A
        if(rand.nextFloat() <= .6) sample[A] = true;
        else sample[A] = false;
        
        // B
        if(rand.nextFloat() <= .3) sample[B] = true;
        else sample[B] = false;
        
        // C
        if(sample[A])
        {
            if(rand.nextFloat() <= .8) sample[C] = true;
            else sample[C] = false;
        }
        else
        {
            if(rand.nextFloat() <= .7) sample[C] = true;
            else sample[C] = false;
        }
        
        // D
        if(sample[A] && sample[B])
        {
            if(rand.nextFloat() <= .4) sample[D] = true;
            else sample[D] = false;
        }
        else if (sample[A] && !sample[B])
        {
            if(rand.nextFloat() <= .2) sample[D] = true;
            else sample[D] = false;
        }
        else if (!sample[A] && sample[B])
        {
            if(rand.nextFloat() <= .1) sample[D] = true;
            else sample[D] = false;
        }
        else
        {
            if(rand.nextFloat() <= .8) sample[D] = true;
            else sample[D] = false;
        }
        
        // E
        if(sample[C])
        {
            if(rand.nextFloat() <= .8) sample[E] = true;
            else sample[E] = false;
        }
        else
        {
            if(rand.nextFloat() <= .5) sample[E] = true;
            else sample[E] = false;
        }      
        
        // F
        if(sample[C] && sample[D])
        {
            if(rand.nextFloat() <= .7) sample[F] = true;
            else sample[F] = false;
        }
        else if (sample[C] && !sample[D])
        {
            if(rand.nextFloat() <= .6) sample[F] = true;
            else sample[F] = false;
        }
        else if (!sample[C] && sample[D])
        {
            if(rand.nextFloat() <= .3) sample[F] = true;
            else sample[F] = false;
        }
        else
        {
            if(rand.nextFloat() <= .4) sample[F] = true;
            else sample[F] = false;
        }
        
        // G
        if(sample[D])
        {
            if(rand.nextFloat() <= .3) sample[G] = true;
            else sample[G] = false;
        }
        else
        {
            if(rand.nextFloat() <= .2) sample[G] = true;
            else sample[G] = false;
        }     


        return sample;
    }

    public static float findJointProbability() 
    {
        int count = 0;
        boolean[] match = {true, false, true, false, true, true, false};
        for (int i = 0; i < sample_size; i++) {
            if(Arrays.equals(buildSample(), match)) count++;
        }
        return (float)count/sample_size;
    }
    
    
    
    
    public static float findBayesProbability(int[] conditionals, int var) 
    {
        int count = 0;
        for (int i = 0; i < sample_size; i++) {
            boolean[] sample = buildSample();
            boolean valid = true;
            
            // Check for valid permutation using conditionals
            for (int j = 0; j < num_vars; j++) {
                if(conditionals[j] == 0 && sample[j] == true) {
                    valid = false;
                } 
                if(conditionals[j] == 1 && sample[j] == false) {
                    valid = false;
                }
            }
            
            // If valid set sample and if variable we are looking for is true,
            // increment count.
            if(valid) {
                samples[i] = sample;
                if(sample[var]) count++;
            }
            // If invalid, throw sample away
            else i--;
        }
        return (float)count/sample_size;
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // Setup
        rand = new Random();
        num_trials = 10;
        sample_size = 1000;
        num_vars = 7;
        int[] conditionalsB = {-1, -1, -1, -1, -1, -1, 0}; //G=false
        int varB = 5;   // Searching for E to be true
        int[] conditionalsC = {0, 1, -1, -1, -1, -1, -1}; //A=true, B=false
        int varC = 6;   // Searching for F to be true
        
        
        // iterate through trials
        for (sample_size = 100; sample_size <= 100000; sample_size *= 10) {
            // Initialize arrays
            samples = new boolean[sample_size][num_vars];
            float[] probabilities = new float[num_trials];
            
            
            // Choose here whether we want results for question 4a, 4b, or 4c
            for (int i = 0; i < num_trials; i++) {
                probabilities[i] = findJointProbability();                      // Question 4a
//                probabilities[i] = findBayesProbability(conditionalsB, varB); // Question 4b
//                probabilities[i] = findBayesProbability(conditionalsC, varC); // Question 4c
            }


            // Calculations
            float sd = 0, average, sum = 0;
            for (int i=0; i<probabilities.length;i++) sum+=probabilities[i];

            average = sum/probabilities.length;

            for (int i=0; i<probabilities.length;i++)
            {
                sd = (float) (sd + Math.pow(probabilities[i] - average, 2));
            }
            System.out.print("Sample size: ");
            System.out.println(sample_size);
            System.out.print("Average: ");
            System.out.println(average);
            System.out.print("Standard deviation: ");
            System.out.println(sd);
            System.out.print("\n");
        }
        
        
    }
    private static int num_vars;
    private static int num_trials;
    private static int sample_size;
    private static Random rand;
    private static boolean[][] samples;
    
}
