import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Class Problem1
 *
 * @author Andi Fajar Nur Ismail, Kemal Amru Ramadhan, Muhhammad Izzunnaqi, Martin Novela, Wahid Nur Rohman
 */
public class Problem1 {
    public static void main(String[] args) throws Exception, IOException {
        BufferedReader baca = new BufferedReader(new InputStreamReader(System.in));

        System.out.print("Masukkan jumlah baris : ");
        int rowNumber = Integer.parseInt(baca.readLine());

        System.out.print("Masukkan nilai awal untuk unobserved data : ");
        double defaultValue = Double.parseDouble(baca.readLine());

        System.out.print("Masukkan batas toleransi konvergen : ");
        double tol = Double.parseDouble(baca.readLine());

        String input = "";
        Table table = new Table(rowNumber);

        for (int i = 0; i < rowNumber; i++) {
            System.out.print("Masukkan nilai baris ke-" + (i + 1) + " : ");
            input = baca.readLine();
            table.insert(input, i);
        }

        // Make EM class for EM operation
        EM em = new EM(tol);
        // Set the table
        em.table = table;
        // Find all Teta
        em.initTeta();
        // Do EM Algorithm!
        em.doEMDriver(defaultValue);


        // Print all output
        for (String x : em.output) {
            System.out.println(x);
        }
        System.out.println("----- Iterasi selesai ----");
        System.out.println("--------- Summary --------");
        for (String x : em.summary) {
            System.out.println(x);
        }


    }

}

/**
 * Class for represen a cell of Table
 */
class Cell {
    /**
     * Value of its cell
     */
    double value;
    /**
     * boolean for unobserved value
     */
    boolean isUnobserved;

    /**
     * Contructor
     *
     * @param val value of the cell
     * @param bol isunobserved or observed
     */
    public Cell(double val, boolean bol) {
        value = val;
        isUnobserved = bol;
    }

}

/**
 * Class for representing a table of data
 */
class Table {
    /**
     * Each column of the table
     */
    Cell[] flu;
    Cell[] allergy;
    Cell[] sinus;
    Cell[] headache;
    Cell[] nose;

    /**
     * Constrctor
     *
     * @param numOfRows number of rows
     */
    public Table(int numOfRows) {
        flu = new Cell[numOfRows];
        allergy = new Cell[numOfRows];
        sinus = new Cell[numOfRows];
        headache = new Cell[numOfRows];
        nose = new Cell[numOfRows];

    }

    /**
     * Insert each data per row
     *
     * @param in    data given
     * @param index number of row
     */
    public void insert(String in, int index) {
        flu[index] = new Cell(todouble(in.charAt(0)), in.charAt(0) == '?');
        allergy[index] = new Cell(todouble(in.charAt(1)), in.charAt(1) == '?');
        sinus[index] = new Cell(todouble(in.charAt(2)), in.charAt(2) == '?');
        headache[index] = new Cell(todouble(in.charAt(3)), in.charAt(3) == '?');
        nose[index] = new Cell(todouble(in.charAt(4)), in.charAt(4) == '?');
    }

    /**
     * Change input tp Double value
     *
     * @param i char which will be inserted
     * @return double value
     */
    public double todouble(char i) {
        if (i == '0')
            return 0.0;
        else if (i == '1')
            return 1.0;
        else
            return 0.0;
    }

}


/**
 * Class for EM Algorithm
 */
class EM {
    /**
     * Error tollerance
     */
    double tol;

    /**
     * Table for EM
     */
    Table table;

    /**
     * List of unobserved Teta (contains '?'
     */
    ArrayList<Teta> unsupervisedTeta;

    /**
     * List of result of EM Algorith,
     */
    ArrayList<Teta> converGenceTeta;

    /**
     * String output
     */
    ArrayList<String> output;
    ArrayList<String> summary;

    /**
     * All teta
     */
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

    /**
     * Constructor
     *
     * @param tol error tollerance
     */
    public EM(double tol) {
        this.tol = tol;
        unsupervisedTeta = new ArrayList<>();
        converGenceTeta = new ArrayList<>();
        output = new ArrayList<>();
        summary = new ArrayList<>();
        tetaFlue = new Teta();
        tetaAllergy = new Teta();
        tetaSinus00 = new Teta();
        tetaSinus01 = new Teta();
        tetaSinus10 = new Teta();
        tetaSinus11 = new Teta();
        tetaHeadache0 = new Teta();
        tetaHeadache1 = new Teta();
        tetaNose0 = new Teta();
        tetaNose1 = new Teta();

    }

    /**
     * Init all teta, and find unobserved teta
     */
    public void initTeta() {
        initNoParent(table.flu, tetaFlue, "F");
        initNoParent(table.allergy, tetaAllergy, "A");
        initTwoParent(table.flu, table.allergy, table.sinus, 0, 0, tetaSinus00, "S|00");
        initTwoParent(table.flu, table.allergy, table.sinus, 0, 1, tetaSinus01, "S|01");
        initTwoParent(table.flu, table.allergy, table.sinus, 1, 0, tetaSinus10, "S|10");
        initTwoParent(table.flu, table.allergy, table.sinus, 1, 1, tetaSinus11, "S|11");
        initOneParent(table.sinus, table.headache, 0, tetaHeadache0, "H|0");
        initOneParent(table.sinus, table.headache, 1, tetaHeadache1, "H|1");
        initOneParent(table.sinus, table.nose, 0, tetaNose0, "N|0");
        initOneParent(table.sinus, table.nose, 1, tetaNose1, "N|1");
    }

    /**
     * Find teta which has no parent
     *
     * @param cell cell of data
     * @param teta teta of the cell
     * @param name name of the teta
     */
    public void initNoParent(Cell[] cell, Teta teta, String name) {
        teta.name = name;
        boolean x = false;
        int numOfUnsuperfised = 0;

        for (Cell c : cell) {
            if (c.isUnobserved) {
                x = true;
                numOfUnsuperfised++;
            }
            teta.itsOwn.add(c);
            teta.sumOfSupervised += c.value;
        }

        teta.numOfUnsupervised = numOfUnsuperfised;
        if (x) {
            unsupervisedTeta.add(teta);
        }
    }

    /**
     * Find teta which has a parent
     *
     * @param parent parent of the teta
     * @param cell   cell of data
     * @param val    value of parent observed
     * @param teta   teta will be constructed
     * @param name   name of teta
     */
    public void initOneParent(Cell[] parent, Cell[] cell, double val, Teta teta, String name) {
        teta.name = name;
        boolean x = false;
        int numOfSuperfised = 0;
        int j = 0;

        for (int i = 0; i < cell.length; i++) {
            if (parent[i].value == val) {
                if (cell[i].isUnobserved) {
                    x = true;
                    numOfSuperfised++;
                    teta.indexUnsupervised.add(j);

                }
                teta.itsOwn.add(cell[i]);
                teta.sumOfSupervised += cell[i].value;
                j++;
            }
        }

        teta.numOfUnsupervised = numOfSuperfised;
        if (x) {
            unsupervisedTeta.add(teta);
        }

    }

    /**
     * Find teta which has 2 parents
     *
     * @param parent1 parent of teta
     * @param parent2 parent of teta
     * @param cell cell of data
     * @param val1 value for first parent
     * @param val2 value for second parent
     * @param teta teta will be constructed
     * @param name name of teta
     */
    public void initTwoParent(Cell[] parent1, Cell[] parent2, Cell[] cell, double val1, double val2, Teta teta, String name) {
        teta.name = name;
        boolean x = false;
        int numOfSuperfised = 0;
        int j = 0;

        for (int i = 0; i < cell.length; i++) {
            if (parent1[i].value == val1 && parent2[i].value == val2) {
                if (cell[i].isUnobserved) {
                    x = true;
                    numOfSuperfised++;
                    teta.indexUnsupervised.add(j);
                }
                teta.itsOwn.add(cell[i]);
                j++;
                teta.sumOfSupervised += cell[i].value;
            }
        }

        teta.numOfUnsupervised = numOfSuperfised;
        if (x) {
            unsupervisedTeta.add(teta);
        }
    }

    public void initProbability() {
        countProbability(tetaFlue);
        ;
        countProbability(tetaAllergy);
        countProbability(tetaSinus00);
        countProbability(tetaSinus01);
        countProbability(tetaSinus10);
        countProbability(tetaSinus11);
        countProbability(tetaHeadache0);
        countProbability(tetaHeadache1);
        countProbability(tetaNose0);
        countProbability(tetaNose1);

    }

    /**
     * E-step of EM
     * @param teta teta which will be operated of E-Step
     * @param newVal new Value whic whill be inserted
     */
    public void eStep(Teta teta, double newVal) {
        for (int i = 0; i < teta.indexUnsupervised.size(); i++) {
            output.add("-- " + teta.name + ": " + newVal);
            teta.itsOwn.get(i).value = newVal;
        }
    }

    /**
     * M-step of EM
     * @param teta teta which will be operated of M-Step
     * @param newVal new Value whic whill be inserted
     */
    public double mStep(Teta teta, double newVal) {
        double result = (teta.sumOfSupervised + newVal) / teta.itsOwn.size();
        output.add("-- " + teta.name + ": " + result);
        return result;
    }

    /**
     * Algorithm of EM (Driver)
     * @param newValue initial Value
     */
    public void doEMDriver(double newValue) {
        ArrayList<Double> initial = new ArrayList<>();
        for (int i = 0; i < unsupervisedTeta.size(); i++) {
            initial.add(newValue);
        }

        doEM(initial);
    }

    /**
     *Algorithm of EM
     *
     * @param newValue initial value
     */
    public void doEM(ArrayList<Double> newValue) {
        int numOfIteration = 0;
        while (!unsupervisedTeta.isEmpty()) {

            output.add("------- Iterasi-" + numOfIteration + " ------- ");
            output.add("- E-Step:");
            for (int i = 0; i < unsupervisedTeta.size(); i++) {
                eStep(unsupervisedTeta.get(i), newValue.get(i));
            }

            output.add("- M-Step:");

            for (int i = 0; i < unsupervisedTeta.size(); ) {

                newValue.set(i, mStep(unsupervisedTeta.get(i), newValue.get(i)));
                unsupervisedTeta.get(i).valBefore = unsupervisedTeta.get(i).val;
                unsupervisedTeta.get(i).val = newValue.get(i);
                i++;

            }

            for (int i = 0; i < unsupervisedTeta.size(); ) {
                if (Math.abs(unsupervisedTeta.get(i).valBefore - unsupervisedTeta.get(i).val) <= tol) {
                    newValue.remove(i);
                    Teta conv = unsupervisedTeta.remove(i);
                    summary.add("--> " + conv.name + " konvergen di iterasi - " + numOfIteration + " nilai: " + conv.val);
                    converGenceTeta.add(conv);
                } else {
                    i++;
                }
            }

            numOfIteration++;
        }
    }

    public void countProbability(Teta teta) {
        double sum = 0;

        if (teta.itsOwn.size() == 0) return;

        for (Cell c : teta.itsOwn) {
            sum += c.value;
        }

        teta.val = sum / teta.itsOwn.size();
    }

}

/**
 * Class for represent a teta
 */
class Teta {
    double val;
    double valBefore;
    double numOfData;
    double numOfUnsupervised;
    boolean isUnobserved;
    double sumOfSupervised;
    ArrayList<Integer> indexUnsupervised;
    ArrayList<Cell> itsOwn;
    String name;

    public Teta() {
        indexUnsupervised = new ArrayList<>();
        itsOwn = new ArrayList<>();
    }
}