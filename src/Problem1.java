import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 */
public class Problem1 {
    public static void main(String[] args) throws Exception, IOException {
        BufferedReader baca = new BufferedReader(new InputStreamReader(System.in));

        System.out.print("Masukkan jumlah baris : ");
        int rowNumber = Integer.parseInt(baca.readLine());

        System.out.print("Masukkan nilai awal untuk unobserved data : ");
        double defaultValue = Double.parseDouble(baca.readLine());

        TreeMap<String, Node> listUnsupervised = new TreeMap<>();

        double[] flu = new double[rowNumber];
        double[] allergy = new double[rowNumber];
        double[] sinus = new double[rowNumber];
        double[] headache = new double[rowNumber];
        double[] nose = new double[rowNumber];


        String input = "";

        for (int i = 0; i < rowNumber; i++) {
            System.out.print("Masukkan nilai baris ke-"+(i+1) + " : ");
            input = baca.readLine();
            flu[i] = toInt(input.charAt(0), defaultValue);
            allergy[i] = toInt(input.charAt(1), defaultValue);
            sinus[i] = toInt(input.charAt(2), defaultValue);
            headache[i] = toInt(input.charAt(3), defaultValue);
            nose[i] = toInt(input.charAt(4), defaultValue);
        }

        double probabilityFlu = probabilityTanpaParent(flu, listUnsupervised, defaultValue);
        double probabilityAllergy = probabilityTanpaParent(allergy, listUnsupervised, defaultValue);
        double probabilitySinus00 = probabilityDoubleParent(flu, allergy, sinus, 0, 0, "Sinus", listUnsupervised, defaultValue);
        double probabilitySinus01 = probabilityDoubleParent(flu, allergy, sinus, 0, 1, "Sinus", listUnsupervised, defaultValue);
        double probabilitySinus10 = probabilityDoubleParent(flu, allergy, sinus, 1, 0, "Sinus", listUnsupervised, defaultValue);
        double probabilitySinus11 = probabilityDoubleParent(flu, allergy, sinus, 1, 1, "Sinus", listUnsupervised, defaultValue);
        double probabilityHeadache0 = probabilitySingleParent(sinus, headache, 0, "Headache", listUnsupervised, defaultValue);
        double probabilityHeadache1 = probabilitySingleParent(sinus, headache, 1, "Headache", listUnsupervised, defaultValue);
        double probabilityNose0 = probabilitySingleParent(sinus, nose, 0, "Nose", listUnsupervised, defaultValue);
        double probabilityNose1 = probabilitySingleParent(sinus, nose, 1, "Nose", listUnsupervised, defaultValue);

        boolean convergen = false;
        int iteration = 0;

        while (!convergen) {
            iteration++;
            Boolean test = true;

            for (Map.Entry<String, Node> entry : listUnsupervised.entrySet()) {
                String key = entry.getKey();
                Node value = entry.getValue();

                double newValue = (value.getValue() + value.getJumlah1()) / value.getJumlahParent();

                if (value.getValue() ==newValue) {
                    test = test && true;
                } else {
                    test = test && false;
                }

                value.setValue(newValue);
            }

            convergen = test;
        }

        System.out.println("Jumlah iterasi sampai konvergen : " + iteration);
        for (Map.Entry<String, Node> entry : listUnsupervised.entrySet()) {
            String key = entry.getKey();
            Node value = entry.getValue();

            System.out.println("Nilai dari " + key + " adalah = " + value.getValue());
        }

    }

    public static double toInt(char i, double defaultValue) {
        if (i == '0')
            return 0;
        else if (i == '1')
            return 1;
        else
            return defaultValue;
    }

    public static double probabilityTanpaParent(double[] node, TreeMap<String, Node> data, double value) {
        double sum = 0;

        for (int i = 0; i < node.length; i++) {
            sum += node[i];
        }

        return sum / node.length;
    }

    public static double probabilityDoubleParent(double[] parent1, double[] parent2, double[] node, double p1, double p2, String nodeName, TreeMap<String, Node> data, double value) {
        double sum = 0;
        double jumlahParent = 0;
        double jumlah1 = 0;

        ArrayList<Integer> array = new ArrayList<>();
        for (int i = 0; i < node.length; i++) {
            if (parent1[i] == p1 && parent2[i] == p2) {
                if (node[i] != 0 && node[i] != 1) {
                    array.add(i);
                } else if (node[i] == 1) {
                    jumlah1++;
                }
                sum += node[i];
                jumlahParent++;
            }
        }

        for (int i = 0; i < array.size(); i++) {
            Node newNode = new Node(value, jumlahParent, jumlah1);
            data.put(nodeName + (array.get(i)+1), newNode);
        }

        return sum / jumlahParent;
    }

    public static double probabilitySingleParent(double[] parent, double[] node, double p, String nodeName, TreeMap<String, Node> data, double value) {
        double sum = 0;
        double jumlahParent = 0;
        double jumlah1 = 0;
        ArrayList<Integer> array = new ArrayList<>();

        for (int i = 0; i < node.length; i++) {
            if (parent[i] == p) {
                if (node[i] != 0 && node[i] != 1) {
                    array.add(i);
                } else if (node[i] == 1) {
                    jumlah1++;
                }
                sum += node[i];
                jumlahParent++;
            }
        }

        for (int i = 0; i < array.size(); i++) {
            Node newNode = new Node(value, jumlahParent, jumlah1);
            data.put(nodeName + (array.get(i) + 1), newNode);
        }

        return sum / jumlahParent;
    }

    public static void eStep(Node n, double v) {
        n.setJumlah1(v);
    }

    public static void mStep(Node n, double v) {

    }
}

class Node {
    double value;
    double jumlahParent;
    double jumlah1;

    public Node(double value, double jumlahParent, double jumlah1) {
        this.value = value;
        this.jumlahParent = jumlahParent;
        this.jumlah1 = jumlah1;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getJumlahParent() {
        return jumlahParent;
    }

    public void setJumlahParent(double jumlahParent) {
        this.jumlahParent = jumlahParent;
    }

    public double getJumlah1() {
        return jumlah1;
    }

    public void setJumlah1(double jumlah1) {
        this.jumlah1 = jumlah1;
    }
}

class Cell {
    double value;
    double probability;
    double numOfAll;
    double numOfSupervised;
    boolean isUnobserved;

    public Cell (double val, boolean bol){
        value = val;
        isUnobserved = bol;
    }
}

class Table{
    Cell [] flu;
    Cell [] allergy;
    Cell [] sinus;
    Cell [] headache;
    Cell [] nose;

    public Table(int numOfRows){
        flu = new Cell [numOfRows];
        allergy = new Cell [numOfRows];
        sinus = new Cell [numOfRows];
        headache = new Cell [numOfRows];
        nose = new Cell [numOfRows];

    }

    public void insert (String in, int index){
        flu[index] = new Cell(todouble(in.charAt(0)), in.charAt(0) != '?');
        allergy[index] =  new Cell(todouble(in.charAt(1)), in.charAt(0) != '?');
        sinus[index] =  new Cell(todouble(in.charAt(2)), in.charAt(0) != '?');
        headache[index] =  new Cell(todouble(in.charAt(3)), in.charAt(0) != '?');
        nose[index] =  new Cell(todouble(in.charAt(4)), in.charAt(0) != '?');
    }

    public  double todouble(char i) {
        if (i == '0')
            return 0.0;
        else if (i == '1')
            return 1.0;
        else
            return 0.0;
    }

}

class EM{
    Table table;
    ArrayList<Teta> unsupervised;
    Teta tetaFlue;
    Teta tetaAllergy;
    Teta tetaSinus00;
    Teta tetaSinus01;
    Teta tetaSinus10;
    Teta tetaSinus11;
    Teta tetaHeadache0;
    Teta tetaHeadache1;
    Teta tetaNose0;
    Teta tetaNose1;

    public void initTeta(){
        initNoParent(table.flu, tetaFlue);
        initNoParent(table.allergy, tetaAllergy);
        initTwoParent(table.flu, table.allergy, table.sinus, 0, 0, tetaSinus00);
        initTwoParent(table.flu, table.allergy, table.sinus, 0, 1, tetaSinus01);
        initTwoParent(table.flu, table.allergy, table.sinus, 1, 0, tetaSinus10);
        initTwoParent(table.flu, table.allergy, table.sinus, 1, 1, tetaSinus11);
        initOneParent(table.sinus, table.headache, 0, tetaHeadache0);
        initOneParent(table.sinus, table.headache, 1, tetaHeadache1);
        initOneParent(table.sinus, table.nose, 0, tetaNose0);
        initOneParent(table.sinus, table.nose, 1, tetaNose1);
    }

    public void initNoParent(Cell [] cell, Teta teta){
        boolean x = false;
        int numOfSuperfised = 0;

        for(Cell c : cell){
            if(c.isUnobserved) {
                x = x || c.isUnobserved;
                numOfSuperfised++;
            }
            teta.itsOwn.add(c);
        }

        teta.numOfUnsupervised = numOfSuperfised;
        if(x){
            unsupervised.add(teta);
        }
    }

    public void initOneParent(Cell [] parent, Cell [] cell, double val, Teta teta){
        boolean x = false;
        int numOfSuperfised = 0;
        int j = 0;

        for (int i = 0; i < cell.length; i++) {
            if (parent[i].value == val) {
                if(cell[i].isUnobserved) {
                    x = true;
                    numOfSuperfised++;
                    teta.indexUnsupervised.add(j);
                }
                teta.itsOwn.add(cell[i]);
                j++;
            }
        }

        teta.numOfUnsupervised = numOfSuperfised;
        if(x){
            unsupervised.add(teta);
        }

    }

    public void initTwoParent(Cell [] parent1, Cell [] parent2, Cell [] cell, double val1, double val2,Teta teta){
        boolean x = false;
        int numOfSuperfised = 0;
        int j = 0;

        for (int i = 0; i < cell.length; i++) {
            if (parent1[i].value == val1 && parent2[i].value == val2) {
                if(cell[i].isUnobserved) {
                    x = true;
                    numOfSuperfised++;
                    teta.indexUnsupervised.add(j);
                }
                teta.itsOwn.add(cell[i]);
                j++;
            }
        }

        teta.numOfUnsupervised = numOfSuperfised;
        if(x){
            unsupervised.add(teta);
        }
    }

    public void initProbability(){
        countProbability(tetaFlue);;
        countProbability(tetaAllergy);
        countProbability(tetaSinus00);
        countProbability(tetaSinus01) ;
        countProbability(tetaSinus10);
        countProbability(tetaSinus11);
        countProbability(tetaHeadache0);
        countProbability(tetaHeadache1);
        countProbability(tetaNose0);
        countProbability(tetaNose1);

    }

    public void eStep(double newVal){
        for (Teta c: unsupervised) {
            c.val = newVal;
        }
    }

    public double mStepHelp(Cell c, double newVal){
        return (c.numOfSupervised + newVal)/c.numOfAll;
    }

    public boolean  mStep(){
        boolean isConvergen = true;
        for (Teta c : unsupervised) {
            double newValue = mStepHelp(c, c.val);

            if (c.val == newValue) {
                isConvergen = isConvergen && true;
            } else {
                isConvergen = isConvergen && false;
            }
        }
        return isConvergen;
    }


    public void countProbability(Teta teta){
        double sum = 0;

        if(teta.itsOwn.size() == 0) return;

        for(Cell c : teta.itsOwn){
            sum += c.value;
        }

        teta.val = sum / teta.itsOwn.size();
    }

}

class Teta{
    double val;
    double numOfData;
    double numOfUnsupervised;
    boolean isUnobserved;
    ArrayList<Integer> indexUnsupervised;
    ArrayList<Cell> itsOwn;
}


