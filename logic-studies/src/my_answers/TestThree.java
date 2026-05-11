package my_answers;

import java.util.ArrayList;

public class TestThree {

    void main(String[] args) {
        /* Dado um array de dígitos binários, encontre o comprimento da maior sequência contígua de 1s.
            1 <= array.length <= 10⁵
        
        */

        var result = false;
        var array = args[0].split(",");

        var arrayString = new ArrayList<String>();
        for (int a = 0; a < array.length; a++) {
            arrayString.add(array[a]);
        }
        
        var maxCount = 0;

        for (String s : arrayString) {
            var count = 0;
            for (int j = 0; j < s.length(); j++) {
                if (s.charAt(j) == '1') {
                    count++;
                }
            }
            if (count > maxCount) {
                maxCount = count;
            }
        }
        if (maxCount > 0) {
            result = true;
        }
        System.out.println(maxCount);
        System.out.println(result);

    }    
}
