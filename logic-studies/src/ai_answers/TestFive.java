package ai_answers;

import java.util.ArrayList;

public class TestFive {

    void main(String[] args) {
        /* Dado um array de inteiros positivos e negativos, 
        encontre o subarray contíguo que possui a maior soma e retorne esse valor.  
        int[] nums = {-2, 1, -3, 4, -1, 2, 1, -5, 4};
        */

        var array = args[0].split(",");
        var arrayNum = new ArrayList<Integer>();
        for (int a = 0; a < array.length; a++) {
            arrayNum.add(Integer.parseInt(array[a]));
        }

        int maxSum = arrayNum.get(0);
        int currentSum = arrayNum.get(0);

        for (int i = 1; i < arrayNum.size(); i++) {
            currentSum = Math.max(arrayNum.get(i), currentSum + arrayNum.get(i));
            maxSum = Math.max(maxSum, currentSum);
        }
        System.out.println(maxSum);
    }
}
