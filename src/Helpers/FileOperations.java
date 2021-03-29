package Helpers;

import Entities.InputFileLine;
import Entities.OutputFileLine;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class FileOperations {

    public static ArrayList<InputFileLine> readInputFile(String pathName) {
        ArrayList<InputFileLine> array = new ArrayList<InputFileLine>();

        try {
            File myObj = new File(pathName);

            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                String[] lineSplit = data.split(" ");

                InputFileLine line = new InputFileLine();
                line.Name = lineSplit[0];
                line.ChildLib = lineSplit[1];
                line.ParentLib = lineSplit[2];
                //line.ParentLib = lineSplit[2];
                //line.ChildLib = lineSplit[1];
                array.add(line);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        return array;
    }

    public static ArrayList<InputFileLine> readOutputFile(String pathName) {
        ArrayList<InputFileLine> array = new ArrayList<InputFileLine>();

        try {
            File myObj = new File(pathName);

            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                String[] lineSplit = data.split(" ");

                InputFileLine line = new InputFileLine();
                line.Name = lineSplit[0];
                line.ClusterName = lineSplit[1];
                line.ChildLib = lineSplit[2];
                line.IsClustered = true;
                array.add(line);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        return array;
    }

    public static void createFile(String folderName, String fileName, ArrayList<InputFileLine> list) throws IOException {
        BufferedWriter bufferedWriter = null;

        // File file = new File("output/yeni.txt");
        File file = new File(folderName + "/" + fileName + ".txt");

        if (!file.exists()) {

            file.createNewFile();
        }

        FileWriter fileWriter = new FileWriter(file);
        bufferedWriter = new BufferedWriter(fileWriter);

        for (InputFileLine fileLine : list) {
            //bufferedWriter.write(fileLine.Name + " "+ fileLine.ParentLib + " " + fileLine.ChildLib);
            bufferedWriter.write("contain " + fileLine.ClusterName + " " + fileLine.ChildLib);
            bufferedWriter.newLine();
        }
        bufferedWriter.flush();
    }

    public static String createFolder(String folderName) throws IOException {
        {
            File folder = new File("output/" + folderName);
            folder.mkdir();
            folder.createNewFile();

            return folder.getPath();
        }
    }
}
