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

        int rowNumber = Integer.parseInt(baca.readLine());
        double defaultValue = Double.parseDouble(baca.readLine());

        TreeMap<String, Node> listUnsupervised = new TreeMap<>();

        double[] flu = new double[rowNumber];
        double[] allergy = new double[rowNumber];
        double[] sinus = new double[rowNumber];
        double[] headache = new double[rowNumber];
        double[] nose = new double[rowNumber];

        String input = baca.readLine();

        for (int i = 0; i < rowNumber; i++) {
            flu[i] = toInt(input.charAt(0), defaultValue);
            allergy[i] = toInt(input.charAt(1), defaultValue);
            sinus[i] = toInt(input.charAt(2), defaultValue);
            headache[i] = toInt(input.charAt(3), defaultValue);
            nose[i] = toInt(input.charAt(4), defaultValue);

            input = baca.readLine();
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

        Boolean convergen = false;

        while (!convergen) {
            Boolean test = true;

            for (Map.Entry<String, Node> entry : listUnsupervised.entrySet()) {
                String key = entry.getKey();
                Node value = entry.getValue();

                double newValue = (value.getValue() + value.getJumlah1()) / value.getJumlahParent();

                if (Math.abs(value.getValue() - newValue) < 0.005) {
                    test = test && true;
                } else {
                    test = test && false;
                }

                value.setValue(newValue);
            }

            convergen = test;
        }

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

        ArrayList<Integer> array = new ArrayList<>();
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

    public static void eStep() {

    }

    public static void mStep() {

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
    boolean isUnsupervised;
}
