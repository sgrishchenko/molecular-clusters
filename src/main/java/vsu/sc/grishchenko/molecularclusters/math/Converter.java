package vsu.sc.grishchenko.molecularclusters.math;

import com.google.gson.Gson;
import org.apache.commons.lang3.ArrayUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Converter {
    public static Gson gson = new Gson();

    public static void main(String[] args) throws IOException {

        List<MotionEquationData> data = new ArrayList<>();

        File in = new File("44_7.txt");
        try (BufferedReader reader = new BufferedReader(new FileReader(in))) {
            reader.lines().forEach(s -> {
                String[] buffer = s.split(" ");
                data.add(new MotionEquationData("r", "0",
                        ArrayUtils.<Double>toArray(Double.parseDouble(buffer[1]),
                                Double.parseDouble(buffer[3]),
                                Double.parseDouble(buffer[2])),
                        ArrayUtils.<Double>toArray(0., 0., 0.), new Color(0.6, 0, 0, 1)));
            });
        }

        Comparator<MotionEquationData> comparatorByY = (o1, o2) -> {
            if (o1.getInitialPosition()[1] > o2.getInitialPosition()[1]) return 1;
            else return -1;
        };
        int[] i = {0};
        List<MotionEquationData> sortedData = new ArrayList<>();
        data
                .stream()
                .sorted(comparatorByY)
                .forEach(eq -> {
                    eq.setLabel(eq.getLabel() + i[0]++);
                    sortedData.add(eq);
                });

        File out = new File(in.getName().replaceAll("\\..+$", ".json"));
        try (FileWriter writer = new FileWriter(out)) {
            writer.write(gson.toJson(sortedData));
        }

    }
}
