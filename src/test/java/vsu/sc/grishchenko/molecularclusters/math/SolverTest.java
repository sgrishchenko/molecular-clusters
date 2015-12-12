package vsu.sc.grishchenko.molecularclusters.math;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class SolverTest {

    @Test
    public void testSolveVerlet() throws Exception {
        DifferentialEquation equation1 = new DifferentialEquation("y1", "- 2*y1 + 4*y2", 3.);
        DifferentialEquation equation2 = new DifferentialEquation("y2", "-y1 + 3*y2", 0.);
        List<DifferentialEquation> equations = new ArrayList<>();
        equations.add(equation1);
        equations.add(equation2);

        Long time = System.currentTimeMillis();
        Map<String, List<Double>> result = Solver.solveSystem(equations, SolveMethod.RUNGE_KUTTA_4, 0., 20, 0.1);
        System.out.println(String.format("Расчет выполнялся %d мс", System.currentTimeMillis() - time));

        List<Double> y1res = new ArrayList<>();
        List<Double> y2res = new ArrayList<>();
        y1res.add(3.);
        y2res.add(0.);
        Double x = 0.1;
        for (int i = 1; i < 20; i++, x += 0.1) {
            y1res.add(4 * Math.exp(-x) - Math.exp(2 * x));
            y2res.add(Math.exp(-x) - Math.exp(2 * x));

            assertEquals(result.get("y1").get(i-1), y1res.get(i-1), 0.5);
            assertEquals(result.get("y2").get(i-1), y2res.get(i-1), 0.5);
        }
    }
}