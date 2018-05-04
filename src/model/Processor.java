package model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.stream.Collectors;

import javax.swing.tree.TreeNode;

import org.jgap.Configuration;
import org.jgap.InvalidConfigurationException;
import org.jgap.event.EventManager;
import org.jgap.event.GeneticEvent;
import org.jgap.event.GeneticEventListener;
import org.jgap.gp.CommandGene;
import org.jgap.gp.GPProblem;
import org.jgap.gp.IGPProgram;
import org.jgap.gp.function.Abs;
import org.jgap.gp.function.Add;
import org.jgap.gp.function.Divide;
import org.jgap.gp.function.Equals;
import org.jgap.gp.function.GreaterThan;
import org.jgap.gp.function.LesserThan;
import org.jgap.gp.function.Log;
import org.jgap.gp.function.Multiply;
import org.jgap.gp.function.Pow;
import org.jgap.gp.function.SubProgram;
import org.jgap.gp.function.Subtract;
import org.jgap.gp.function.Switch;
import org.jgap.gp.impl.DefaultGPFitnessEvaluator;
import org.jgap.gp.impl.DeltaGPFitnessEvaluator;
import org.jgap.gp.impl.GPConfiguration;
import org.jgap.gp.impl.GPGenotype;
import org.jgap.gp.impl.JGAPTreeNode;
import org.jgap.gp.impl.ProgramChromosome;
import org.jgap.gp.terminal.Constant;
import org.jgap.gp.terminal.Terminal;
import org.jgap.gp.terminal.Variable;
import org.jgap.util.NumberKit;

import application.MainController;
import javafx.application.Platform;

public class Processor  extends GPProblem {
    @SuppressWarnings("boxing")
    public static ArrayList<Double> INPUT_1 = new ArrayList<>(Arrays.asList(26.0d, 8.0d, 20.0d, 33.0d, 37.0d));

    @SuppressWarnings("boxing")
    public static  ArrayList<Double>INPUT_2 = new ArrayList<>(Arrays.asList( 35.0d, 24.0d, 1.0d, 11.0d, 16.0d));

    public static  ArrayList<Double> OUTPUT = new ArrayList<>();
    public GPConfiguration config = getGPConfiguration();
    private Variable _xVariable;
    private Variable _yVariable;
    
    // Each Feature has Instance & ArrayList [ sameName+Capital I]
    private Variable clumpThickness;
    public ArrayList<Double> clumpThicknessI			= new ArrayList<>();
    private Variable uCellSize;
    public ArrayList<Double> uCellSizeI 				= new ArrayList<>();
    private Variable uCellShape;
    public ArrayList<Double> uCellShapeI 				= new ArrayList<>();
    private Variable mAdhesion;
    public ArrayList<Double> mAdhesionI 				= new ArrayList<>();
    private Variable seCellSize;
    public ArrayList<Double> seCellSizeI 				= new ArrayList<>();
    private Variable bNuclei;
    public ArrayList<Double> bNucleiI 					= new ArrayList<>();
    private Variable bChromatin;
    public ArrayList<Double> bChromatinI 				= new ArrayList<>();
    private Variable nNuclei;
    public ArrayList<Double> nNucleiI 					= new ArrayList<>();
    private Variable mitosis;
    public ArrayList<Double> mitosisI 					= new ArrayList<>();
    
    // Training & Test Data Sets
    public ArrayList<ArrayList<Double>> trainingSet 	= new ArrayList<>();
    public ArrayList<ArrayList<Double>> testSet		= new ArrayList<>();
    //Inputs are mapped to their column in the file in the LinkedHashMap below {used in Fitness Calculation}
    public LinkedHashMap<Integer,ArrayList<Double>> inputsColumnMapping = new LinkedHashMap<>();
    //Variables are mapped to their column in the file in the LinkedHashMap below {used in Fitness Calculation}
    public LinkedHashMap<Integer,Variable> variablesColumnMapping = new LinkedHashMap<>();
    
    public GPGenotype gp;
    public MainController uiWin	;
    public int epochCounter;
    public EventManager eventManager  		= new EventManager();
    
    
    
    public Processor() throws InvalidConfigurationException {
        super(new GPConfiguration());
        Configuration.reset();
//        GPConfiguration config = getGPConfiguration();
//        _xVariable = Variable.create(config, "X", CommandGene.DoubleClass);
        

//        config.setGPFitnessEvaluator(new DeltaGPFitnessEvaluator()); // Lower Numbers from Fitness Function Is Better
        config.setGPFitnessEvaluator(new DefaultGPFitnessEvaluator()); // Higher Numbers from Fitness Function Is Better
        config.setMinInitDepth(2);
        config.setMaxInitDepth(6);
        config.setPopulationSize(1000);
        config.setMaxCrossoverDepth(6);
        
//        config.setEventManager(eventManager);
        config.setMutationProb((float) 0.1);
        config.setCrossoverProb((float)0.9);
        config.setReproductionProb((float)0.1);
        config.setNewChromsPercent((float)0.3);
        config.setPreservFittestIndividual(true);
        config.setKeepPopulationSizeConstant(true);
//        config.setProgramCreationMaxTries(20);
        config.setFitnessFunction(new ClassificationFitnessFunction(inputsColumnMapping, OUTPUT, variablesColumnMapping));
//        config.setStrictProgramCreation(true);
    }

    
	public void initialiseColumns() throws InvalidConfigurationException {
	    clumpThickness						= Variable.create(config, "clumpThickness", CommandGene.DoubleClass);
	    uCellSize							= Variable.create(config, "uCellSize", CommandGene.DoubleClass);
	    uCellShape							= Variable.create(config, "uCellShape", CommandGene.DoubleClass);
	    mAdhesion							= Variable.create(config, "mAdhesion", CommandGene.DoubleClass);
	    seCellSize							= Variable.create(config, "seCellSize", CommandGene.DoubleClass);
	    bNuclei								= Variable.create(config, "bNuclei", CommandGene.DoubleClass);
	    bChromatin							= Variable.create(config, "bChromatin", CommandGene.DoubleClass);
	    nNuclei								= Variable.create(config, "nNuclei", CommandGene.DoubleClass);
	    mitosis								=  Variable.create(config, "mitosis", CommandGene.DoubleClass);
	    //Column1
		variablesColumnMapping.put(1, clumpThickness);
		inputsColumnMapping.put(1, clumpThicknessI);
	    //Column2
		variablesColumnMapping.put(2, uCellSize);
		inputsColumnMapping.put(2, uCellSizeI);
	    //Column3
		variablesColumnMapping.put(3, uCellShape);
		inputsColumnMapping.put(3, uCellShapeI);	
	    //Column4
		variablesColumnMapping.put(4, mAdhesion);
		inputsColumnMapping.put(4, mAdhesionI);
	    //Column5
		variablesColumnMapping.put(5, seCellSize);
		inputsColumnMapping.put(5, seCellSizeI);
	    //Column6
		variablesColumnMapping.put(6, bNuclei);
		inputsColumnMapping.put(6, bNucleiI);	
	    //Column7
		variablesColumnMapping.put(7, bChromatin);
		inputsColumnMapping.put(7, bChromatinI);		
	    //Column8
		variablesColumnMapping.put(8, nNuclei);
		inputsColumnMapping.put(8, nNucleiI);		
	    //Column9
		variablesColumnMapping.put(9, mitosis);
		inputsColumnMapping.put(9, mitosisI);		
		
	}
    public String outputSolution(IGPProgram a_best) {
    	String bestIndividualRep			= "";
        if (a_best == null) {
        	bestIndividualRep				= "No best solution (null)";
          return bestIndividualRep;
        }
        double bestValue = a_best.getFitnessValue();
        if (Double.isInfinite(bestValue)) {
        	bestIndividualRep				= "No best solution (infinite)";
          return bestIndividualRep;
        }
        bestIndividualRep					+="Best solution fitness: " +NumberKit.niceDecimalNumber(bestValue, 2)+"\n";
        bestIndividualRep					+="Best solution: " + a_best.toStringNorm(0)+"\n";
        String depths = "";
        int size = a_best.size();
        for (int i = 0; i < size; i++) {
          if (i > 0) {
            depths += " / ";
          }
          depths += a_best.getChromosome(i).getDepth(0);
        }
        if (size == 1) {
        	bestIndividualRep				+= "Depth of chrom: " + depths;
        }
        else {
        	bestIndividualRep				+= "Depths of chroms: " + depths;
        }
        return bestIndividualRep;
      }
    
    
    public ArrayList<CommandGene> createTerminalsAndFunctionsFromUI(){
    	ArrayList<CommandGene> terminalsAndFunctions = null;
		try {
			terminalsAndFunctions = uiWin.createTerminalsAndFunctionsFromUI(config);
		} catch (InvalidConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return terminalsAndFunctions;
    }
    
    
    @Override
    public GPGenotype create() throws InvalidConfigurationException {
//        GPConfiguration config = getGPConfiguration();

        // The return type of the GP program.
        Class[] types = { CommandGene.DoubleClass};

        // Arguments of result-producing chromosome: none
        Class[][] argTypes = { {} };

        // Next, we define the set of available GP commands and terminals to
        // use.
        CommandGene[][] nodeSetsUi = null;
        if (uiWin!=null) {
            ArrayList<CommandGene> terminalsAndFunctions = createTerminalsAndFunctionsFromUI();
            for (Entry<Integer, Variable> entry:variablesColumnMapping.entrySet()) {
            	terminalsAndFunctions.add(entry.getValue());
            	System.out.println("ADDING INPUT "+entry.getValue().getName());
            }
            nodeSetsUi = new CommandGene[1][terminalsAndFunctions.size()]; //Create empty list
            
            for (int i=0;i<terminalsAndFunctions.size();i++) {
            	nodeSetsUi[0][i]			= terminalsAndFunctions.get(i);
            	}   	
        	}

        CommandGene[][] nodeSets = {
                {
                }
            };        
        GPGenotype result ;
        if (uiWin !=null) {
        		result 			= GPGenotype.randomInitialGenotype(config, types, argTypes,
        				nodeSetsUi, uiWin.getMaxNodes(), true);
        		System.out.println("Adding EventLister");
        		config.getEventManager().addEventListener(GeneticEvent.
        				GENOTYPE_EVOLVED_EVENT, new GeneticEventListener() {
							@Override
							public void geneticEventFired(GeneticEvent a_firedEvent) {
						    	Platform.runLater(()->uiWin.drawDataSet());
						    	Platform.runLater(()->uiWin.drawPredictedSet()); 								
							}
        			
        		});
        }else {
            	result 			= GPGenotype.randomInitialGenotype(config, types, argTypes,
                    			nodeSets, 15, true);
        	
        }
        return result;
    }
    
    public void initialisePopulation() {
        try {
			gp 					= create();
			epochCounter		= 0;
		} catch (InvalidConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        gp.setVerboseOutput(true);
    	
    }
    public void evolveForNEpochs(int epochs) {
      for (int i=0; i<epochs;i++) {
    	epochCounter++;
    	System.out.println("Epoch:"+epochCounter);
    	gp.evolve(1);
//    	generateCheckPoints();
//    	Platform.runLater(()->uiWin.drawDataSet());
//    	Platform.runLater(()->uiWin.drawPredictedSet());    	
//		uiWin.drawDataSet();
//		uiWin.drawPredictedSet();    	
    	gp.outputSolution(gp.getAllTimeBest());
    }
    	
    }
    public ArrayList<Integer> generateCheckPoints() {
        
    	IGPProgram bestP 			= gp.getAllTimeBest();
    	
    	for(IGPProgram pr :gp.getGPPopulation().getGPPrograms()) {
    		System.out.println(outputSolution(pr));
    	}
    	ArrayList<Integer> predictOutputs	= new ArrayList<>();
//    	for (Double input:INPUT_1) {
//            this.getGPConfiguration().getVariable("X").set(input);        
//            predictOutputs.add(bestP.execute_int(0,new Object[0]));
//    	}
    	return predictOutputs;
    	
    }
    public void writeTrainingAndTestSetsToFile() {
		List<String> lines 							=new ArrayList<String>();
		for (ArrayList<Double> instance:trainingSet) {			
			String listString						= instance.toString();
			listString								= listString.substring(1, listString.length()-1).replace(" ", ""); 
			lines.add(listString);
//			System.out.println("AddingLIne>>"+listString);
		}
		Path file = Paths.get("trainingData.txt");
		try {
			Files.write(file, lines, Charset.forName("UTF-8"));
		} catch (IOException e) {			
			e.printStackTrace();
		}
		lines.clear();
		for (ArrayList<Double> instance:testSet) {			
			String listString						= instance.toString();
			listString								= listString.substring(1, listString.length()-1).replace(" ", ""); 
			lines.add(listString);
//			System.out.println("AddingLIne>>"+listString);
		}
		file = Paths.get("testData.txt");
		try {
			Files.write(file, lines, Charset.forName("UTF-8"));
		} catch (IOException e) {			
			e.printStackTrace();
		}	    	
    }
    public void populateTrainingInputsAndOutput() {
    	for(ArrayList<Double> trainingInstance:trainingSet) {
    		for (int i =1;i<=9;i++) {
    			inputsColumnMapping.get(i).add(trainingInstance.get(i));
    		}
    		OUTPUT.add(trainingInstance.get(10));
    	}
		for (int i =1;i<=9;i++) {
			System.out.println(variablesColumnMapping.get(i).getName()+inputsColumnMapping.get(i).size());
		}
		System.out.println(OUTPUT.size());
    }
    public void constructTrainAndTestSets(double trainPercentage) {
    	// As we read the file if random float is less than trainPercentage then add instance 
    	// To Training Data set else add to Test Set
    	// 0.8 trainPercentage if random <0.8 then trainInstance else testInstance
    	
    	Random random 						= new Random();
		String trainingFilePath				= System.getProperty("user.dir").replace('\\', '/') + "/breast-cancer-wisconsin.data";
		File fileObj 						= new File(trainingFilePath);
		String line;
		try (FileReader fileReader = new FileReader(fileObj);				
				BufferedReader bufferedReader		= new BufferedReader(fileReader);){
				line								= bufferedReader.readLine();
				while (line!=null) {
					ArrayList<Double> instance		= new ArrayList<>();
					double randomChoice				= random.nextDouble();
					String[] lineParts				= line.split(",");
					for (String str :lineParts) {
						if (str.equals("?")) {instance.add(-1.0);continue;}
						instance.add(Double.parseDouble(str));
					}
					if (randomChoice<=trainPercentage) {
						trainingSet.add(instance);
					}else {
						testSet.add(instance);
					}
				 	line							= bufferedReader.readLine();
				}
		
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("FILE NOT FOUND !!");
			
		}		
    	System.out.println("Training Set Size="+trainingSet.size()+"\nTest Set Size="+testSet.size());
    }

	

	public static void test() throws InvalidConfigurationException {
		Processor problem = new Processor();
		problem.initialiseColumns();
		problem.constructTrainAndTestSets(0.75);		
//		System.out.println(699.0*0.1);
		problem.populateTrainingInputsAndOutput();
		problem.writeTrainingAndTestSetsToFile();
	}
    public static void main(String[] args) throws Exception {
    	test();
//        GPProblem problem = new Processor();
//        
//        GPGenotype gp = problem.create();
//        gp.setVerboseOutput(true);
////        for (int i=0; i<30;i++) {
////        	System.out.println("CYCLE:"+i);
////        	gp.evolve(1);
////        	gp.outputSolution(gp.getAllTimeBest());
////        }
//        gp.evolve(200);
//        gp.outputSolution(gp.getAllTimeBest());
//        
////        gp.outputSolution(gp.getAllTimeBest());
//        IGPProgram bestP 	= gp.getAllTimeBest();
//        
//        problem.getGPConfiguration().getVariable("X").set(2.75);        
//        System.out.println("\nExecuted 2.75= " + bestP.execute_double(0,new Object[0]));
//        problem.getGPConfiguration().getVariable("X").set(-2.0);        
//        System.out.println("\nExecuted -2= " + bestP.execute_double(0,new Object[0]));        
//        System.out.println("Settings " + gp.getGPConfiguration().getInitStrategy());
//       
    }

}