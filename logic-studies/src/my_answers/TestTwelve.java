package my_answers;

import java.util.*;

public class TestTwelve {
    void main(String args[]) {
        /*
         * Dada uma lista de strings, agrupe as que são anagramas entre si.
         * A ordem dos grupos na resposta não importa.
         * eat,tea,tan,ate,nat,bat
         * */

        var input = args[0].split(",");
        var arrayString = new ArrayList<String>(Arrays.asList(input));
        Map<String, List<String>> map = new HashMap<>();

        for (String s : arrayString) {
            char[] chars = s.toCharArray();
            Arrays.sort(chars);
            String sort = new String(chars);
            var oldArr = map.get(sort);
            if (oldArr == null) {
                var newArr = new ArrayList<String>();
                newArr.add(s);
                map.put(sort, newArr);
            } else {
                oldArr.add(s);
                map.put(sort, oldArr);
            }

        }
        for (var arr : map.values()) {
            System.out.println(arr);
        }
    }
}
