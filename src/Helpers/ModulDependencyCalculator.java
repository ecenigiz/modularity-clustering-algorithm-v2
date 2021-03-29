package Helpers;

import Entities.InputFileLine;

import java.util.ArrayList;
import java.util.Collections;

public class ModulDependencyCalculator {

    public double CalculateDependency(String fromPath, String toPath) {
        long startTime = System.currentTimeMillis();

        ArrayList<InputFileLine> firstList = FileOperations.readInputFile(fromPath);
        ArrayList<InputFileLine> clusteredList = FileOperations.readOutputFile(toPath);

        double totalSum = 0;
        double edgeWeigthIsExist = 1;
        double edgeWeigthIsNotExist = 0;
        double clusterCount = 1;
        double totalEdgeCount = firstList.size();
        double[] eachOfEdgeCount = new double[clusteredList.size()];

        //Collections.sort(clusteredList);

        //Kaç tane farklı küme var bunu buluyoruz
        for (int i = 1; i < clusteredList.size(); i++) {
            int j = 0;
            for (j = 0; j < i; j++) {
                if (clusteredList.get(i).ClusterName.equals(clusteredList.get(j).ClusterName))
                    break;
            }
            if (i == j)
                clusterCount++;
        }


        //Her kümenin eleman sayısını buluyoruz
        double[] clusterHasEdge = new double[(int) clusterCount + 1];
        int clusterIndex = 0;
        String previousClusterName = clusteredList.get(0).ClusterName;

        for (int i = 0; i < clusteredList.size(); i++) {
            if (previousClusterName.equals(clusteredList.get(i).ClusterName))
                clusterHasEdge[clusterIndex]++;
            else {
                clusterIndex++;
                clusterHasEdge[clusterIndex]++;
                previousClusterName = clusteredList.get(i).ClusterName;
            }
        }

        //1 nokta kaç yere bağlı onu buluyoruz
        for (int i = 0; i < clusteredList.size(); i++) {
            for (int j = 0; j < firstList.size(); j++) {
                if (clusteredList.get(i).ChildLib.equals(firstList.get(j).ChildLib) ||
                        (clusteredList.get(i).ChildLib.equals(firstList.get(j).ParentLib))) {
                    eachOfEdgeCount[i]++;
                }
            }
        }

        double toplamCluster = 0;
        double toplamEdge = 0;
        for (int i = 0; i < clusteredList.size(); i++) {
            for (int j = i + 1; j < clusteredList.size(); j++) {
                if (clusteredList.get(i).ClusterName.equals(clusteredList.get(j).ClusterName)) {
                    boolean flag = false;
                    for (int k = 0; k < firstList.size(); k++) {
                        boolean x1 = clusteredList.get(i).ChildLib.equals(firstList.get(k).ChildLib)
                                || clusteredList.get(i).ChildLib.equals(firstList.get(k).ParentLib);

                        boolean x2 = clusteredList.get(j).ChildLib.equals(firstList.get(k).ChildLib)
                                || clusteredList.get(j).ChildLib.equals(firstList.get(k).ParentLib);

                        flag = x1 && x2;
                    }
                    if (flag) {
                        toplamEdge = toplamEdge + (edgeWeigthIsExist - ((eachOfEdgeCount[j] * eachOfEdgeCount[j]) / (2 * totalEdgeCount)));
                    } else {
                        toplamEdge = toplamEdge + (edgeWeigthIsNotExist - ((eachOfEdgeCount[j] * eachOfEdgeCount[j]) / (2 * totalEdgeCount)));
                    }
                }
            }
        }


        //Hesaplama kısmı

     /*   int index = 0;
        for (int i = 0; i < clusterCount; i++) {
            for (int j = index; j < index + clusterHasEdge[i]; j++) {
                for (int l = j + 1; l < index + clusterHasEdge[i]; l++) {
                    boolean flag = false;
                    for (int k = 0; k < firstList.size(); k++) {
                        boolean x1 = clusteredList.get(j).ChildLib.equals(firstList.get(k).ChildLib)
                                || clusteredList.get(j).ChildLib.equals(firstList.get(k).ParentLib);

                        boolean x2 = clusteredList.get(l).ChildLib.equals(firstList.get(k).ChildLib)
                                || clusteredList.get(l).ChildLib.equals(firstList.get(k).ParentLib);

                        if (x1 && x2) flag = true;
                    }
                    if (flag) {
                        toplamEdge = toplamEdge + (edgeWeigthIsExist - ((eachOfEdgeCount[j] * eachOfEdgeCount[l]) / (2 * totalEdgeCount)));
                    } else {
                        toplamEdge = toplamEdge + (edgeWeigthIsNotExist - ((eachOfEdgeCount[j] * eachOfEdgeCount[l]) / (2 * totalEdgeCount)));
                    }
                }
            }
            index += (int) clusterHasEdge[i];
            toplamCluster += toplamEdge;
            toplamEdge = 0;
        }*/

        totalSum = toplamEdge / (2 * totalEdgeCount);
        long estimatedTime = System.currentTimeMillis() - startTime;
        System.out.println("Hesaplamada harcanan süre: " + estimatedTime);
        return totalSum;
    }
}

