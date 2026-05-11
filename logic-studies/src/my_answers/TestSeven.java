package my_answers;

import java.util.ArrayList;

public class TestSeven {
    void main(String[] args) {
        /* Dado um array de dígitos binários e um inteiro k,
        determine o tamanho da maior janela contígua que contenha no máximo k zeros.
        array = [1, 1, 0, 1, 0, 1, 1, 1]
        k = 1
        posição:  0  1  2  3  4  5  6  7
        array:    1  1  0  1  0  1  1  1
                           ^           ^
                           i3          i7
        */
        var maxWindow = getMaxWindow(args);

        System.out.println("Maior janela: " + maxWindow);
    }

    private static int getMaxWindow(String[] args) {
        var array = args[0].split(",");
        var target = args[1];
        var arrayNum = new ArrayList<Integer>();
        var targetNum = Integer.parseInt(target);
        for (String s : array) {
            arrayNum.add(Integer.parseInt(s));
        }

        var maxWindow = 0;

        for (int a = 0; a < arrayNum.size(); a++) {
            var zeroCount = 0;
            var windowSize = 0;
            for (int b = a; b < arrayNum.size(); b++) {
                if (arrayNum.get(b) == 0) zeroCount++;
                if (zeroCount > targetNum) break;
                windowSize++;
                if (windowSize > maxWindow) maxWindow = windowSize;
            }
        }
        return maxWindow;
    }
}
