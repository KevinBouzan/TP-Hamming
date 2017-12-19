package fr.epsi.i4;

import fr.epsi.i4.model.Entry;
import fr.epsi.i4.model.Master;

public class Main {

    public static void main(String[] args) {
        Master master = new Master();

        generateData(master);
//        generateDataBis(master);

        master.displayDistances();

        master.dispatch(3);
//        master.dispatch();

        System.out.println(master.toString());
    }

    public static void generateData(Master master) {
        master.addEntry(new Entry(0, 2, 2, 0));
        master.addEntry(new Entry(0, 1, 2, 0));
        master.addEntry(new Entry(1, 2, 2, 0));
//        master.addEntry(new Entry(0, 2, 1, 0));
        master.addEntry(new Entry(0, 2, 1, 0));
        master.addEntry(new Entry(0, 2, 2, 1));
        master.addEntry(new Entry(0, 1, 1, 1));
        master.addEntry(new Entry(1, 2, 2, 1));
        master.addEntry(new Entry(1, 1, 1, 0));
        master.addEntry(new Entry(1, 1, 1, 1));
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
