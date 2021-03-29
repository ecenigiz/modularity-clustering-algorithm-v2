package Helpers;

import Entities.InputFileLine;
import Entities.Population;
import TurboMq.TurboMQ;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Integer.MAX_VALUE;

public class KMeansAlgorithm {
    String folderPath;

    public KMeansAlgorithm(String folderPath) {
        this.folderPath = folderPath;
    }

    public Population applyKMeansAlgorithm(ArrayList<InputFileLine> list, int clusterCount) throws IOException {
        Population population = preparePopulation(list, clusterCount);

        FileOperations.createFile(folderPath, "kmeans_algorithm_cluster_" + clusterCount + "_population_number_calculation_" + population.DependencyCalculation, population.ClusteredItems);
        System.out.println("k means_cluster count_"+clusterCount + "_calculation_" + population.DependencyCalculation);
        return population;

    }

    public String applyKMeansWithBinarySearchAlgorithm(ArrayList<InputFileLine> list, int clusterCount) throws IOException {
        Population selectedPopulation = preparePopulation(list, clusterCount);
        boolean flag = false;
        Population populationsBinaryNodeLeft, populationsBinaryNodeRight;

        // cluster baslangıcı= n/2, bitişi 3n/2
        List<String> col = new ArrayList<String>();
        for (int i = 0; i < list.size(); i++) {
            col.add(list.get(i).ChildLib);
        }

        int countOfDistincLine = (int) (col.stream().distinct().count());

        int power = (int) Math.round(Math.log(countOfDistincLine) / Math.log(2));
        int powerDivided2 = power / 2;

        int maxClusterCount = powerDivided2, minClusterCount=powerDivided2+power;
        //bunu 2^n e cek
        while (!flag) {
            //cluster-1 i buna atıyorum
            System.out.println("k_means_search_lineer_cluster count_"+clusterCount);

            populationsBinaryNodeLeft = preparePopulation(list, clusterCount - 1);
            //cluster+1 i buna atıyorum
            populationsBinaryNodeRight = preparePopulation(list, clusterCount + 1);

            if ((clusterCount == minClusterCount) || (clusterCount == maxClusterCount)) {
                flag = true;
            }
            else if ((selectedPopulation.DependencyCalculation >= populationsBinaryNodeLeft.DependencyCalculation)
                    && (selectedPopulation.DependencyCalculation >= populationsBinaryNodeRight.DependencyCalculation)) {
                flag = true;
            }
            //sağdakine öncelik veriyoruz. Küme sayısı artması iyi
            else if (selectedPopulation.DependencyCalculation < populationsBinaryNodeRight.DependencyCalculation) {
                selectedPopulation = populationsBinaryNodeRight;
                clusterCount = clusterCount + 1;

            } else {
                selectedPopulation = populationsBinaryNodeLeft;
                clusterCount = clusterCount - 1;
            }
        }

       // FileOperations.createFile(folderPath, "kmeans_algorithm_with_binary_search_cluster_count_" + clusterCount + "_population_number_calculation_" + selectedPopulation.DependencyCalculation, selectedPopulation.ClusteredItems);
        System.out.println("k_means_search_lineer_cluster count_"+clusterCount + "_calculation_" + selectedPopulation.DependencyCalculation);

        return folderPath + "\\" + "kmeans_algorithm.txt";

    }

    private Population preparePopulation(ArrayList<InputFileLine> list, int clusterCount) throws IOException {
        ArrayList<Population> populations = new ArrayList<Population>();
        Population tempPopulation = new Population();
        ArrayList<InputFileLine> tempLines = new ArrayList<InputFileLine>();
        InputFileLine tmpline;

        List<String> col = new ArrayList<String>();
        for (int i = 0; i < list.size(); i++) {
            col.add(list.get(i).ChildLib);
            col.add(list.get(i).ParentLib);
        }

        //1->a, 2->b. Bu sayede her class ı sayısallaştırmış oluyoruz.
        List<String> distincLine = col.stream().distinct().collect(Collectors.toList());
        int lineCount = distincLine.size();

        boolean[][] dependArray = new boolean[lineCount][lineCount];
        int x;
        int y;
        for (int i = 0; i < list.size(); i++) {
            x = distincLine.indexOf(list.get(i).ChildLib);
            y = distincLine.indexOf(list.get(i).ParentLib);
            dependArray[x][y] = true;
        }
        //depended array-> noktaların varlığı oluyor.

        //%30 tolere edebiliyor
        int clusterSize = (distincLine.size() / clusterCount) * 130 / 100;
        int[] countOfEachClusterSize = new int[clusterCount];
        int[] centroid = new int[clusterCount];//= new int[clusterCount];
        int[] centroidBefore = new int[clusterCount];//= new int[clusterCount];
        for (int i = 0; i < clusterCount; i++) {
            centroid[i] = i;
            centroidBefore[i] = i;
        }

        HashMap<String, Integer> distanceEachCluster = new HashMap<String, Integer>();
        int centroidSameCount = 0;
        int iteration = 0;

        while (centroidSameCount < clusterCount) {
            //her küme için her nokta ile arasındaki
            distanceEachCluster = new HashMap<String, Integer>();
            countOfEachClusterSize = new int[clusterCount];
            int distanceX = 0, distanceY = 0, distance = 0;
            int tempClusterName = -1, tempDistanceValue = 0;
            int minValueInCluster;

            for (int i = 0; i < lineCount; i++) {
                for (int j = 0; j < lineCount; j++) {
                    if (dependArray[i][j]) {
                        minValueInCluster = MAX_VALUE;
                        tempClusterName = -1;
                        //her c ile distance karşılastırıyoeuz, en kucuk dıstance ı secıyoruz ve o noktanın cluster ına atamam yapıyoruz
                        for (int c = 0; c < clusterCount; c++) {
                            distanceX = i - centroid[c];
                            distanceY = j - centroid[c];
                            distance = Math.abs((distanceX * distanceX) + (distanceY * distanceY));
                            if ((distance < minValueInCluster) && countOfEachClusterSize[c] <= clusterSize) {
                                minValueInCluster = distance;
                                tempClusterName = c;
                            }
                        }
                    }
                }
                distanceEachCluster.put(String.valueOf(i), tempClusterName);
                countOfEachClusterSize[tempClusterName]++;
            }

            int[] sumOfEachCluster = new int[clusterCount];
            int[] countOfEachCluster = new int[clusterCount];
            for (int k = 0; k < distanceEachCluster.size(); k++) {
                sumOfEachCluster[distanceEachCluster.get(String.valueOf(k))] += k;
                countOfEachCluster[distanceEachCluster.get(String.valueOf(k))]++;
            }

            //her sınıf ıcın ortalamayı bulduk
            int[] averageOfEachCluster = new int[clusterCount];
            for (int k = 0; k < clusterCount; k++) {
                if (countOfEachCluster[k] > 0) {
                    averageOfEachCluster[k] = sumOfEachCluster[k] / countOfEachCluster[k];
                }
            }

            centroidSameCount = 0;
            //ortalamayı atamamız lazım, before u düzenliyorum. if koşulu => eğer aynı ise sameCount artırıyor.
            for (int k = 0; k < clusterCount; k++) {
                centroid[k] = averageOfEachCluster[k];
                if (centroidBefore[k] == centroid[k]) {
                    centroidSameCount++;
                }
                centroidBefore[k] = centroid[k];
            }
            iteration++;

        }
        System.out.println("iteration:" + iteration);
        for (
                int i = 0; i < distincLine.size(); i++) {
            tmpline = new InputFileLine();
            tmpline.ChildLib = distincLine.get(i); //??
            tmpline.ClusterName = String.valueOf(distanceEachCluster.get(String.valueOf(i)));
            tmpline.IsClustered = true;
            tmpline.Name = "contain";
            tempLines.add(tmpline);
        }

        TurboMQ t = new TurboMQ();

        tempPopulation.ClusterCount = clusterCount;
        tempPopulation.ClusteredItems = tempLines;
        tempPopulation.DependencyCalculation = t.TurboMQCalculateWithList(list, tempPopulation.ClusteredItems);
        populations.add(tempPopulation);
        return populations.get(0);

    }
}
