package Entities;

import java.util.ArrayList;

public class Population implements  Comparable<Population>  {
    public double DependencyCalculation;
    public int ClusterCount;
    public ArrayList<InputFileLine> ClusteredItems;

    @Override
    public int compareTo(Population population) {
        int result = new Double(this.DependencyCalculation).compareTo(population.DependencyCalculation);
        if(result != 0){
            return result;
        }else{
            return new Double(this.DependencyCalculation).compareTo(population.DependencyCalculation);
        }
    }
}
