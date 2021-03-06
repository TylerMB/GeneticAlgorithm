import cosc343.assig2.Creature;
import java.util.Random;

/**
 * The MyCreate extends the cosc343 assignment 2 Creature.  Here you implement
 * creatures chromosome and the agent function that maps creature percepts to
 * actions.  
 *
 * @author Tyler Baker 
 * @version 1.0
 * @since   2017-04-05 
 */
public class MyCreature extends Creature {
  
  // Random number generator
  Random rand = new Random();
  // Array of uninitilized chromosomes
  int [] chromosomes = new int [6];
  // A boolean to keep track whether the creature has seen a piece of food
  boolean moveToFood;
  // A map of the percepts, this tracks each percept location to figure what square the creature is on
  int [] perceptMap = new int[9];
  
  /* New Creature Constructor. creates a new MyCreature from random and fills it's chromosomes with random
   * numbers between -5 and 5, with 5 being a high affiliation to that particular percept reading
   * 
   Input: numPercept - number of percepts that creature will be receiving
   numAction - number of action output vector that creature will need
   to produce on every turn
   */
  public MyCreature(int numPercepts, int numActions) {
    
    for(int i = 0; i < chromosomes.length; i++){
      chromosomes[i] = rand.nextInt(11)-5;
    }
    System.out.print("\n");
  }
  //new population generator
  public MyCreature(MyCreature [] elite, int numPercepts, int numActions){
    
    MyCreature parent1 = elite[0];
    MyCreature parent2 = elite[1];
    int parent1Counter =0;
    int parent2Counter =0;
    
    for(int i = 0; i < chromosomes.length; i++){
      
      //while the chromosomes haven't been filled
      if(parent1Counter < 3 && parent2Counter < 3){
        
        int r = rand.nextInt(2);
        
        if(r == 1){
          chromosomes[i] = parent1.getChromosomes()[i];
          parent1Counter++;
        } else {
          chromosomes[i] = parent2.getChromosomes()[i];
          parent2Counter++;
        }
      } else {
        if(parent1Counter < 3){
          chromosomes[i] = parent1.getChromosomes()[i];
          parent1Counter++;
        }
        if(parent2Counter < 3){
          chromosomes[i] = parent2.getChromosomes()[i];
          parent2Counter++;
        }
      }
    }
//    chromosomes[0] randomChrom
//    chromosomes[1] monsterChrom
//    chromosomes[2] creatureChrom
//    chromosomes[3] foodChrom
//    chromosomes[4] greenChrom
//    chromosomes[5] redChrom
    
    //pass the parents percept map to it's children if it lived
    if(!parent1.isDead()){
      this.perceptMap = parent1.perceptMap;
    }
    
    //mutate a chromosome randomly
    if(rand.nextInt(50) == 0){
      chromosomes[rand.nextInt(4)] = rand.nextInt(11)-5;
    }
  }
  
  /* an accessor method for a creatures chromosomes
   * @return the creatures chromosome array
   */
  public int [] getChromosomes(){
    return this.chromosomes;
  }
  
  /* This function must be overridden by MyCreature, because it implements
   the AgentFunction which controls creature behavoiur.  This behaviour
   should be governed by a model (that you need to come up with) that is
   parameterise by the chromosome.  
   
   Input: percepts - an array of percepts
   numPercepts - the size of the array of percepts depend on the percept
   chosen
   numExpectedAction - this number tells you what the expected size
   of the returned array of percepts should bes
   Returns: an array of actions 
   */
  @Override
  public float[] AgentFunction(int[] percepts, int numPercepts, int numExpectedActions) {
    
    // This is where your chromosome gives rise to the model that maps
    // percepts to actions.  This function governs your creature's behaviour.
    // You need to figure out what model you want to use, and how you're going
    // to encode its parameters in a chromosome.
    
    float actions[] = new float[numExpectedActions];
    int sum = 0;
    int min;
    
    for(int perc : this.perceptMap){
      sum += perc; 
    }
    
    //if all percepts have been mapped
    if(sum == 8){
      min = this.perceptMap[0];
      for(int i = 1; i < this.perceptMap.length;i++){
        if(this.perceptMap[i] < this.perceptMap[min]){
          min = i;
        }
      }
      //if geen strawb
      if(percepts[min] == 1){
        actions[9] = chromosomes[4];
      }
      //if red strawb
      if(percepts[min] == 2){
        actions[9] = chromosomes[5];
      }
    } else  {
      //randomly select  green or redChrom if the monster has just seen food.
      //used when the creature doesn't know it's percept map
      if(moveToFood){
        if(rand.nextInt(2) == 1){
          actions[9] =  this.chromosomes[4];
          moveToFood = false;
        } else {
          actions[9] =  this.chromosomes[5];
          moveToFood = false;
        }
      }
    }
    //set random to randomChrom
    actions[10] += this.chromosomes[0];
    
    for(int i = 0; i < numPercepts;i++){
      
      if(percepts[i] == 3){
        //if food present, set to foodChrom and update map
        perceptMap[i] = 1;
        if(this.chromosomes[3] > 0){
          actions[i] += this.chromosomes[3];
          moveToFood = true;
        } else if(this.chromosomes[3] < 0){
          actions[8-i] -= this.chromosomes[3];
        }
      }
        //if there is a creature present
      if(percepts[i] == 2){
        // and the chromosome is positive, set to creatureChrom
        if(this.chromosomes[2] > 0){
          actions[i] += this.chromosomes[2];
          
        //else, increase the opposite direction
        } else if (this.chromosomes[2] < 0) {
          actions[8-i] -= this.chromosomes[2];
        }
      }
      if(percepts[i] == 1){
        //if monster present, set to monsterChrom
        if(this.chromosomes[1] > 0){
          actions[i] += this.chromosomes[1];
        } else if(this.chromosomes[1] < 0) {
          actions[8-i] -= this.chromosomes[1];
        }
      }
    }
    return actions;
  }
}