package model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import org.jgap.gp.GPFitnessFunction;
import org.jgap.gp.IGPProgram;
import org.jgap.gp.terminal.Variable;

public class ClassificationFitnessFunction extends GPFitnessFunction {

    private LinkedHashMap<Integer,ArrayList<Integer>> inputsColumnMapping;    
    private LinkedHashMap<Integer,Variable> variablesColumnMapping;
    private ArrayList<Integer> _output;

    private static Object[] NO_ARGS = new Object[0];

    public ClassificationFitnessFunction(LinkedHashMap<Integer,ArrayList<Integer>> inputsColumnMapping,
    		ArrayList<Integer> output, LinkedHashMap<Integer,Variable> variablesColumnMapping) {
    	this.inputsColumnMapping 	= inputsColumnMapping;        
        this._output 				= output;
        this.variablesColumnMapping = variablesColumnMapping;        
    }
    
   
    @Override
    protected double evaluate(final IGPProgram program) {
        double result = 0.0f;

        double longResult = 0;
        int correctPredictions = 0;
        for (int i = 0; i < inputsColumnMapping.get(1).size(); i++) { //Iterating over ALL instances of Training Set
            // Set the input values
        	//Variables Map Contain 9 Attrs
        	// inputs Map Contain 9 Arrays each array mapped to variable and it contains the training set values
            for (Entry<Integer,Variable> entry:variablesColumnMapping.entrySet()) {
            	Variable variable 		= entry.getValue();
            	Integer indexOfValue	= entry.getKey();
            	variable.set(inputsColumnMapping.get(indexOfValue).get(i));
            }
            // Execute the genetically engineered algorithm
            double value =  program.execute_int(0, NO_ARGS);

            // The closer longResult gets to 0 the better the algorithm.
            if ((value<=2) && _output.get(i)==2) {correctPredictions++;}
            if ((value>2) && _output.get(i)==4) {correctPredictions++;}
//            longResult += Math.abs(value - _output.get(i));
        }

        result = (float)correctPredictions/(float)inputsColumnMapping.get(1).size();

        return result;
    }

}