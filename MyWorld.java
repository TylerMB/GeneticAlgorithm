import cosc343.assig2.World;
import cosc343.assig2.Creature;
import java.util.*;

/**
 * The MyWorld extends the cosc343 assignment 2 World.  Here you can set 
 * some variables that control the simulations and override functions that
 * generate populations of creatures that the World requires for its
 * simulations.
 *
 * @author  Tyler Baker
 * @version 1.0
 * @since   2017-04-05 
 */
public class MyWorld extends World {
  
  /* Here you can specify the number of turns in each simulation
   * and the number of generations that the genetic algorithm will 
   * execute.
   */
  private final int _numTurns = 100;
  private final int _numGenerations = 200;
  int count =0;
  int [] fitnessList = new int[200];
  int [] survivalList = new int[200];

  /* Constructor.  
   * 
   Input: griSize - the size of the world
   windowWidth - the width (in pixels) of the visualisation window
   windowHeight - the height (in pixels) of the visualisation window
   repeatableMode - if set to true, every simulation in each
   generation will start from the same state
   perceptFormat - format of the percepts to use: choice of 1, 2, or 3
   */
  public MyWorld(int gridSize, int windowWidth, int windowHeight, boolean repeatableMode, int perceptFormat) {   
    // Initialise the parent class - don't remove this
    super(gridSize, windowWidth,  windowHeight, repeatableMode, perceptFormat);
    
    // Set the number of turns and generations
    this.setNumTurns(_numTurns);
    this.setNumGenerations(_numGenerations);
  }
  
  /* The main function for the MyWorld application
   * 
   */
  public static void main(String[] args) {
    // Here you can specify the grid size, window size and whether torun
    // in repeatable mode or not
    int gridSize = 24;
    int windowWidth =  1600;
    int windowHeight = 900;
    boolean repeatableMode = false;
    
    /* Here you can specify percept format to use - there are three to
     chose from: 1, 2, 3.  Refer to the Assignment2 instructions for
     explanation of the three percept formats.
     */
    int perceptFormat = 3;     
    
    // Instantiate MyWorld object.  The rest of the application is driven
    // from the window that will be displayed.
    MyWorld sim = new MyWorld(gridSize, windowWidth, windowHeight, repeatableMode, perceptFormat);
  }
  /* The MyWorld class must override this function, which is
   used to fetch a population of creatures at the beginning of the
   first simulation.  This is the place where you need to  generate
   a set of creatures with random behaviours.
   
   Input: numCreatures - this variable will tell you how many creatures
   the world is expecting
   
   Returns: An array of MyCreature objects - the World will expect numCreatures
   elements in that array     
   */  
  @Override
  public MyCreature[] firstGeneration(int numCreatures) {
    
    int numPercepts = this.expectedNumberofPercepts();
    int numActions = this.expectedNumberofActions();

    MyCreature[] population = new MyCreature[numCreatures];
    for(int i=0;i<numCreatures;i++) {
      population[i] = new MyCreature(numPercepts, numActions);     
    }
    return population;
  }
  
  /* The MyWorld class must override this function, which is
   used to fetch the next generation of the creatures.  This World will
   proivde you with the old_generation of creatures, from which you can
   extract information relating to how they did in the previous simulation...
   and use them as parents for the new generation.
   
   Input: old_population_btc - the generation of old creatures before type casting. 
   The World doesn't know about MyCreature type, only
   its parent type Creature, so you will have to
   typecast to MyCreatures.  These creatures 
   have been simulated over and their state
   can be queried to compute their fitness
   numCreatures - the number of elements in the old_population_btc
   array
   
   Returns: An array of MyCreature objects - the World will expect numCreatures
   elements in that array.  This is the new population that will be
   use for the next simulation.  
   */  
  @Override
  public MyCreature[] nextGeneration(Creature[] old_population_btc, int numCreatures) {
    // Typcast old_population of Creatures to array of MyCreatures
    MyCreature[] old_population = (MyCreature[]) old_population_btc;
    // Create a new array for the new population
    MyCreature[] new_population = new MyCreature[numCreatures];
    
    int numPercepts = this.expectedNumberofPercepts();
    int numActions = this.expectedNumberofActions();
    
    // Here is how you can get information about old creatures and how
    // well they did in the simulation
    float avgLifeTime=0f;
    int nSurvivors = 0;
    for(MyCreature creature : old_population) {
      // The energy of the creature.  This is zero if creature starved to
      // death, non-negative oterhwise.  If this number is zero, but the 
      // creature is dead, then this number gives the enrgy of the creature
      // at the time of death.
      int energy = creature.getEnergy();
      
      // This querry can tell you if the creature died during simulation
      // or not.  
      boolean dead = creature.isDead();
      
      if(dead) {
        // If the creature died during simulation, you can determine
        // its time of death (in turns)
        int timeOfDeath = creature.timeOfDeath();
        avgLifeTime += (float) timeOfDeath;
      } else {
        nSurvivors += 1;
        avgLifeTime += (float) _numTurns;
      }
    }
    avgLifeTime /= (float) numCreatures;
    System.out.println("Simulation stats:");
    System.out.println("  Survivors    : " + nSurvivors + " out of " + numCreatures);
    System.out.println("  Avg life time: " + avgLifeTime + " turns");
    
    //create an array of the fittest creatures from fitnessFunction
    MyCreature [] elitest = fitnessFunction(avgLifeTime, old_population, numCreatures, nSurvivors);

    for(int i=0;i<numCreatures;i++) {
      //call the contructor and send it the fittest parents
      new_population[i] = new MyCreature(elitest, numPercepts, numActions);     
    }
    // print the statistics of average fitness and number of survivors for data.
        if(count == 199){
      for(int i =0; i < fitnessList.length; i ++){
       System.out.println( fitnessList[i]);
      }
      for(int i =0; i < survivalList.length; i ++){
       System.out.println(survivalList[i]);
      }
    }
    //return the new population
    return new_population;
  }
  
  
  /* fitnessFunction, determines the fitness of a generation and organises it into the top two 
   * for breeding.
   * @param: avgLifeTime - generations average for stats
   * @param old_population - the generation to compute stats
   * @param numCreatures/nSurvivors - used for stats
   * 
   * @return - returns the breeding population
   */
  public MyCreature [] fitnessFunction (float avgLifeTime, MyCreature [] old_population, int numCreatures, int nSurvivors){
    
    int [] fitnessValue = new int[numCreatures];
    for(int i = 0; i < numCreatures; i++){
      
      if(!old_population[i].isDead()){
        //for creatures that lived
        fitnessValue[i] += 100;
        fitnessValue[i] += (old_population[i].getEnergy());

      } else {
        
        fitnessValue[i] += old_population[i].getEnergy()/4;
        fitnessValue[i] += old_population[i].timeOfDeath()/2;
        
      }
    }
    
    int sum = 0;
    for(int i = 0; i < fitnessValue.length; i++){
    sum += fitnessValue[i];
    }
    int fitAve = sum/numCreatures;
    System.out.println("fitness average: " + fitAve); //print fitAve
    
    //store fitAve and nSurvivors for printing
    fitnessList[count] = fitAve;
    survivalList[count] = nSurvivors;
    count++;
    
    MyCreature [] elite = new MyCreature [2];
    int first = 0;
    int second = 0;
    //the following selects the two fittest from a population
    if(fitnessValue[0] > fitnessValue[1]){
      first = 0;
      second = 1;
    }else{
      first = 1;
      second = 0;
    }
    for(int i = 2; i < numCreatures; i++){
      if(fitnessValue[i] > fitnessValue[first]){
        second = first;
        first = i;
      } else if(fitnessValue[i] < fitnessValue[first] && fitnessValue[i] > fitnessValue[second]){
        second = i;
      }
    }
    
    elite[0] = old_population[first];
    elite[1] = old_population[second];
    
    //print the elite creatures chromosomes
    for(int l =0; l < 6;l++){
      System.out.print(elite[0].getChromosomes()[l] + "\t");
    }
    System.out.print("E1\n");
    
    for(int l =0; l < 6;l++){
      System.out.print(elite[1].getChromosomes()[l] + "\t");
    }
    
    System.out.print(" E2\n");
    for(int i : elite[1].perceptMap){
    System.out.print(i);
    }
    return elite;
  }
}