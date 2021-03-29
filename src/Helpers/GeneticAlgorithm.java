package Helpers;

import Entities.InputFileLine;
import Entities.Population;
import TurboMq.TurboMQ;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;


public class GeneticAlgorithm {
    String folderPath;

    public GeneticAlgorithm(String folderPath) {
        this.folderPath = folderPath;
    }

    public String applyGeneticALgorithm(ArrayList<InputFileLine> list) throws IOException {
        int clusterCount;
        ArrayList<Population> populations = new ArrayList<Population>();
        ArrayList<Population> populationsNew = new ArrayList<Population>();
        ArrayList<InputFileLine> temp;
        Population tempPopulation;

        DecimalFormat df = new DecimalFormat("0.000");

        TurboMQ t = new TurboMQ();

        double previousClusteredPopulationMax = 0;

        double bestCalculation = -1;
        int bestCalculationClusteredCount = 0;

        //tüm kümeleri ekliyorum, distinc olarak kaç tane oldugunu bulup  2^n denklemini sağlayan n sayısı
        // cluster baslangıcı= n/2, bitişi 3n/2
        List<String> col = new ArrayList<String>();
        for (int i = 0; i < list.size(); i++) {
            col.add(list.get(i).ChildLib);
        }

        int countOfDistincLine = (int) (col.stream().distinct().count());

        int power = (int) Math.round(Math.log(countOfDistincLine) / Math.log(2));
        int powerDivided2 = power / 2;
        for (int c = powerDivided2; c < powerDivided2 + power; c++) {
            clusterCount = c;
            populations = new ArrayList<Population>();

            // Rasgele populasyon oluşturuyoruz
            int populasyonCount = 10;
            for (int i = 0; i < populasyonCount; i++) {
                temp = preparePopulation(list, clusterCount);
                Collections.sort(temp);
                Collections.reverse(temp);

                tempPopulation = new Population();
                tempPopulation.ClusterCount = clusterCount;
                tempPopulation.ClusteredItems = temp;

                populations.add(tempPopulation);
            }

            populationsNew = populations;
            int repeatCount = 1000;
            int selectedPopulationCount = 10;
            //100 kere populasyonu crossover yapıyoruz, mutasyon yapıyoruz,
            // hesaplayıp en ıyı 10 u secıyoruz
            for (int count = 0; count < repeatCount; count++) {

                for (int i = 0; i < populationsNew.size(); i++) {
                    populationsNew.get(i).DependencyCalculation = t.TurboMQCalculateWithList(list, populationsNew.get(i).ClusteredItems);
                }

                // populationsNew = new ArrayList<Population>();
                Collections.sort(populationsNew);
                Collections.reverse(populationsNew);


                //ondalık oalrak 3 basamagı aynı calculation olanları siliyorum
                for (int i = 0; i < populationsNew.size(); i++) {
                    for (int j = i + 1; j < populationsNew.size(); j++) {
                        if (df.format(populationsNew.get(i).DependencyCalculation).equals(df.format(populationsNew.get(j).DependencyCalculation))) {
                            populationsNew.remove(j);
                            j--;
                        }
                    }
                }

                // En iyi ilk elemanı seçiyoruz
                int index = populationsNew.size() - 1;
                while (populationsNew.size() >= selectedPopulationCount) {
                    populationsNew.remove(index);
                    index--;
                }

                if (count != repeatCount - 1) {
                    populations = applyCrossOverDivideHalf(populationsNew);

                    populationsNew = applyMutation(populations, clusterCount);
                }
            }
/*
            for (int i = 0; i < populationsNew.size(); i++) {
                String path = folderPath + "\\genetic_algorithm" + i + ".txt";
                FileOperations.createFile(folderPath, "genetic_algorithm_cluster_" + clusterCount + "_population_number_" + i + "_calculation_" + populationsNew.get(i).DependencyCalculation, populationsNew.get(i).ClusteredItems);
            }
*/
            if (populationsNew.get(0).DependencyCalculation > previousClusteredPopulationMax) {
                bestCalculation = populationsNew.get(0).DependencyCalculation;
                bestCalculationClusteredCount = clusterCount;
            }
            previousClusteredPopulationMax = populationsNew.get(0).DependencyCalculation;
        }

        System.out.println("best clustered count: " + bestCalculationClusteredCount);
        System.out.println("best clustered calculation: " + bestCalculation);
        return folderPath + "\\" + "genetic_algorithm.txt";

    }

    public ArrayList<InputFileLine> preparePopulation(ArrayList<InputFileLine> list, int clusterCount) {
        ArrayList<InputFileLine> clusteredList = new ArrayList<InputFileLine>();
        ArrayList<InputFileLine> clusteredDistinctList = new ArrayList<InputFileLine>();

        //String clusterName;
        Random rand = new Random();
        InputFileLine line = null;

        List<String> col = new ArrayList<String>();
        for (int i = 0; i < list.size(); i++) {
            col.add(list.get(i).ChildLib);
            col.add(list.get(i).ParentLib);
        }
        List<String> distincLine = col.stream().distinct().collect(Collectors.toList());


        for (int i = 0; i < distincLine.size(); i++) {
            line = new InputFileLine();
            line.Name = "Contain";
            String clusterName = String.valueOf(rand.nextInt(clusterCount));
            line.ClusterName = clusterName;
            line.ChildLib = distincLine.get(i);
            clusteredDistinctList.add(line);
        }

        Collections.sort(clusteredDistinctList);
        Collections.reverse(clusteredDistinctList);
        return clusteredDistinctList;
    }

    public ArrayList<Population> applyCrossOverDivideHalf(ArrayList<Population> population) {
        int populationCount = population.size();
        ArrayList<InputFileLine> itemInPopulationI;
        ArrayList<InputFileLine> itemInPopulationJ;

        ArrayList<Population> newPopulations = new ArrayList<Population>();
        Population populationInPopulationItemNew = new Population();
        ArrayList<InputFileLine> itemInPopulationNew = new ArrayList<InputFileLine>();
        InputFileLine itemInPopulationItemNew;

        for (int i = 0; i < populationCount; i++) {
            itemInPopulationI = population.get(i).ClusteredItems;

            for (int j = i + 1; j < populationCount; j++) {
                populationInPopulationItemNew = new Population();
                itemInPopulationJ = population.get(j).ClusteredItems;
                itemInPopulationNew = new ArrayList<InputFileLine>();

                for (int k = 0; k < itemInPopulationJ.size(); k++) {
                    itemInPopulationItemNew = new InputFileLine();

                    itemInPopulationItemNew.IsClustered = true;
                    itemInPopulationItemNew.ChildLib = itemInPopulationI.get(k).ChildLib;
                    itemInPopulationItemNew.Name = itemInPopulationI.get(k).Name;

                    if (k < itemInPopulationJ.size() / 2) {
                        itemInPopulationItemNew.ClusterName = itemInPopulationI.get(k).ClusterName;

                    } else {
                        itemInPopulationItemNew.ClusterName = itemInPopulationJ.get(k).ClusterName;
                    }
                    itemInPopulationNew.add(itemInPopulationItemNew);
                }
                populationInPopulationItemNew.ClusteredItems = itemInPopulationNew;
                newPopulations.add(populationInPopulationItemNew);
                //genetik yaptıktan sonra, cluster count kayboluyor. olmıyy
            }
        }

        //1-1 aynı ıse siliyoz
        boolean x1, x2;
        int flag = 0;
        for (int m = 0; m < newPopulations.size(); m++) {
            for (int l = 0; l < population.size(); l++) {
                flag = 0;
                for (int i = 0; i < population.get(l).ClusteredItems.size(); i++) {
                    x1 = newPopulations.get(m).ClusteredItems.get(i).ChildLib.equals(population.get(l).ClusteredItems.get(i).ChildLib);
                    x2 = newPopulations.get(m).ClusteredItems.get(i).ClusterName.equals(population.get(l).ClusteredItems.get(i).ClusterName);
                    if (x1 && x2)
                        flag++;
                }

                if (flag == population.get(l).ClusteredItems.size()) {
                    newPopulations.remove(m);
                    m--;
                    l = population.size();
                }
            }
        }

        newPopulations.addAll(population);
        Collections.sort(newPopulations);
        Collections.reverse(newPopulations);

        newPopulations.forEach((item) -> {
            Collections.sort(item.ClusteredItems);
            Collections.reverse(item.ClusteredItems);
        });

        return newPopulations;
    }

    public ArrayList<Population> applyMutation(ArrayList<Population> populations, int clusterCount) {

        Random rand = new Random();
        int populationCount = populations.size();
        ArrayList<InputFileLine> itemInPopulationI;
        ArrayList<InputFileLine> itemInPopulationJ;

        ArrayList<Population> newPopulations = new ArrayList<Population>();
        Population populationInPopulationItemNew = new Population();
        ArrayList<InputFileLine> itemInPopulationNew = new ArrayList<InputFileLine>();
        InputFileLine itemInPopulationItemNew;


        for (int j = 0; j < populationCount; j++) {
            populationInPopulationItemNew = new Population();
            itemInPopulationJ = populations.get(j).ClusteredItems;
            itemInPopulationNew = new ArrayList<InputFileLine>();

            for (int k = 0; k < itemInPopulationJ.size(); k++) {
                itemInPopulationItemNew = new InputFileLine();
                itemInPopulationItemNew.ClusterName = itemInPopulationJ.get(k).ClusterName;
                itemInPopulationItemNew.IsClustered = true;
                itemInPopulationItemNew.ChildLib = itemInPopulationJ.get(k).ChildLib;
                itemInPopulationItemNew.Name = itemInPopulationJ.get(k).Name;
                itemInPopulationNew.add(itemInPopulationItemNew);
            }
            populationInPopulationItemNew.ClusteredItems = itemInPopulationNew;
            populationInPopulationItemNew.ClusterCount = populations.get(j).ClusterCount;
            newPopulations.add(populationInPopulationItemNew);

            String randomClusterName = String.valueOf(rand.nextInt(clusterCount));
            int randomIndex = rand.nextInt(newPopulations.get(j).ClusteredItems.size() - 1);
            newPopulations.get(j).ClusteredItems.get(randomIndex).ClusterName = randomClusterName;
            //burda calculatıon yapabilirsin
        }

        //1-1 aynı ıse siliyoz
        boolean x1, x2;
        int flag = 0;
        for (int m = 0; m < newPopulations.size(); m++) {
            for (int l = 0; l < populations.size(); l++) {
                flag = 0;
                for (int i = 0; i < populations.get(l).ClusteredItems.size(); i++) {
                    x1 = newPopulations.get(m).ClusteredItems.get(i).ChildLib.equals(populations.get(l).ClusteredItems.get(i).ChildLib);
                    x2 = newPopulations.get(m).ClusteredItems.get(i).ClusterName.equals(populations.get(l).ClusteredItems.get(i).ClusterName);
                    if (x1 && x2)
                        flag++;
                }

                if (flag == populations.get(l).ClusteredItems.size()) {
                    newPopulations.remove(m);
                    m--;
                    l = populations.size();
                }
            }
        }

        newPopulations.addAll(populations);
        Collections.sort(newPopulations);
        Collections.reverse(newPopulations);
        newPopulations.forEach((item) -> {
            Collections.sort(item.ClusteredItems);
            Collections.reverse(item.ClusteredItems);
        });
        return newPopulations;
    }
}
