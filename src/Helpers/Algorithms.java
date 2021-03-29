package Helpers;

import Entities.InputFileLine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class Algorithms {
    String folderPath;

    public Algorithms(String folderPath) {
        this.folderPath = folderPath;
    }

    public String firstOwnAlgorithm(ArrayList<InputFileLine> list) throws IOException {

        list = applyOwnAlgorithm(list);

        FileOperations.createFile(folderPath, "first_own_algorithm", list);

        return folderPath + "\\" + "first_own_algorithm.txt";
    }
//iki modul cok bagımlıysa, bırlestır.hesaplamayı karsılastır

    public ArrayList<InputFileLine> applyOwnAlgorithm(ArrayList<InputFileLine> list) {
        ArrayList<InputFileLine> clusteredList = new ArrayList<InputFileLine>();
        int selectedIndex = 0;
        int clusterCount = 0;
        String selectedLibrary = "";
        ArrayList<InputFileLine> appliedAlgorithmList = new ArrayList<InputFileLine>();
        InputFileLine line = null;

        //kendisini ve kendisi ile ilk 3 harfi aynı olanları aynı kümeye alır
        for (int i = 0; i < list.size(); i++) {
            if (!list.get(i).IsClustered) {
                clusterCount++;
                line = new InputFileLine();
                line.Name = "Contain";
                line.ClusterName = Integer.toString(clusterCount);
                line.ChildLib = list.get(i).ChildLib;
                line.IsClustered = true;
                appliedAlgorithmList.add(line);

                selectedLibrary = list.get(i).ChildLib;
                for (int j = i; j < list.size(); j++) {
                    if (selectedLibrary.equals(list.get(j).ChildLib)) {
                        list.get(j).IsClustered = true;
                    }
                }
            }
        }

        Collections.sort(appliedAlgorithmList);
        return appliedAlgorithmList;
    }

}
