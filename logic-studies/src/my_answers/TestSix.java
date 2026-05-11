package my_answers;

import java.util.ArrayList;

public class TestSix {
    void main(String[] args) {
        /* Dado um array de inteiros onde cada elemento representa a altura de uma parede, 
            calcule a quantidade máxima de água que pode ser armazenada entre duas paredes quaisquer. 
            A largura entre duas paredes é a diferença entre seus índices.
            ex: nums = [3, 7, 2, 5, 1, 6, 4]
        */

        var array = args[0].split(",");
        var nums = new ArrayList<Integer>();
        for (int i = 0; i < array.length; i++) {
            nums.add(Integer.parseInt(array[i]));
        }
        var maxValue = 0;
        var resultWall1 = 0;
        var resultWall2 = 0;
        var resultGround = 0;


        for (int i = 0; i < nums.size(); i++) {
            var prox = i + 1;
            if (prox == nums.size()) {
                break;
            }
            var result = 0;
            var wall1 = nums.get(i);
            var wall2 = nums.get(prox);
            var ground = 0;

            if (wall1 > wall2) {
                ground = wall1 - wall2;
                result = calculate(wall2, ground);
            } else {
                ground = wall2 - wall1;
                result = calculate(wall1, ground);
            }

            if (result > maxValue) {
                maxValue = result;
                resultWall1 = wall1;
                resultWall2 = wall2;
                resultGround = ground;
            }
        }
        System.out.println(String.format("value: %d, w1: %d, w2: %d, g: %d", maxValue, resultWall1, resultWall2, resultGround));
    }

    Integer calculate(int smallestWall, int ground) {
        return smallestWall * ground;
    }

}
