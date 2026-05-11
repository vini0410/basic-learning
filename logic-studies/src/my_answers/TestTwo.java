package my_answers;

import java.util.ArrayList;

public class TestTwo {

    void main(String[] args) {
        /* Dado um array de inteiros, determine se algum valor aparece mais de uma vez.
        Retorne true se existir duplicata, false caso contrário.
            1 <= array.length <= 10⁵ | -10⁴ <= elementos <= 10⁴
        */

        var result = false;
        var array = args[0].split(",");

        var arrayNum = new ArrayList<Integer>();
        for (int a = 0; a < array.length; a++) {
            arrayNum.add(Integer.parseInt(array[a]));
        }

        for (int b = 0; b < arrayNum.size(); b++) {
            for (int c = 0; c < arrayNum.size(); c++) {
                if (b != c && arrayNum.get(b) == arrayNum.get(c)) {
                    result = true;
                    System.out.println(arrayNum.get(b) + " " + arrayNum.get(c));
                }
            }
            if (result) break;
            
        }
        System.out.println(result);
    }
    
}
