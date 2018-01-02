package fr.epsi.i4;

import fr.epsi.i4.model.Entry;
import fr.epsi.i4.model.Master;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws Exception {
        Master master = new Master();

//        generateData(master);
        master.readFile();
//        generateDataBis(master);

        master.generateDistancesMatrix();
        
        master.printDistance();
        System.out.println("");
        
        master.dispatch();
        System.out.println(master.print());
        
        Scanner input = new Scanner(System.in);
        System.out.println("\nEntrer n'importe quel caractère pour quitter");
        String choice = input.next();
    }

    public static void generateData(Master master) {
        // 1
        master.addEntry(new Entry(0, 2, 2, 0));
        // 2
        master.addEntry(new Entry(0, 1, 2, 0));
        // 3
        master.addEntry(new Entry(1, 2, 2, 0));
        // 4
        master.addEntry(new Entry(0, 2, 1, 0));
        // 5
        master.addEntry(new Entry(0, 2, 2, 1));
        // 6
        master.addEntry(new Entry(0, 1, 1, 1));
        // 7
        master.addEntry(new Entry(1, 2, 2, 1));
        // 8
        master.addEntry(new Entry(1, 1, 1, 0));
        // 9
        master.addEntry(new Entry(1, 1, 1, 1));
        // 10
        master.addEntry(new Entry(1, 2, 1, 1));
    }

    public static void generateDataBis(Master master) {
        master.addEntry(new Entry(0, 2, 2, 0));
        master.addEntry(new Entry(0, 1, 2, 0));
        master.addEntry(new Entry(1, 2, 1, 1));
        master.addEntry(new Entry(0, 2, 1, 0));
        master.addEntry(new Entry(0, 2, 2, 1));
        master.addEntry(new Entry(0, 1, 1, 1));
        master.addEntry(new Entry(1, 2, 2, 1));
        master.addEntry(new Entry(1, 1, 1, 0));
        master.addEntry(new Entry(1, 1, 1, 1));
        master.addEntry(new Entry(1, 2, 1, 1));
    }
}
