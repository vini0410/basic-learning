package my_answers;

import java.util.ArrayList;

public class TestOne {

    void main(String[] args) {
    /* Dado um array de inteiros e um número alvo, 
    determine se existe algum par de elementos no array cuja soma seja igual ao número alvo. Retorne true ou false.
    1 <= array.length <= 10⁴ | -10⁵ <= elementos <= 10⁵
    List<Integer> array, int target
    */

        var array = args[0].split(",");
        var target = args[1];

        var arrayNum = new ArrayList<Integer>();
        for (int a = 0; a < array.length; a++) {
            arrayNum.add(Integer.parseInt(array[a]));
        }
        var targetNum = Integer.parseInt(target);

        var result = false;
        for (int i = 0; i < arrayNum.size(); i++) {
            for (int j = 0; j < arrayNum.size(); j++) {
                if (i + j == targetNum) {
                    System.out.println(String.format("i: %d, j: %d", i, j));
                    result = true;
                }
            }
            if (result) break;
        }
        System.out.println(result);
    }
}
