package ai_answers;

import java.util.HashMap;

public class TestNine {

    void main(String[] args) {
        /*
         * Dada uma string S e uma string T,
         * encontre a menor substring contígua de S que contenha todos os caracteres de T.
         * Se não existir, retorne uma string vazia.
         * */

        var s = args[0];
        var t = args[1];
        // conta quantas vezes cada char aparece em T
        var need = new HashMap<Character, Integer>();

        for (char c : t.toCharArray()) {
            need.put(c, need.getOrDefault(c, 0) + 1);
        }


        var have = new HashMap<Character, Integer>();
        var formed = 0;          // quantos chars de T já foram satisfeitos
        var required = need.size(); // quantos chars únicos T tem

        var left = 0;
        var minLen = Integer.MAX_VALUE;
        var minLeft = 0;

        for (int right = 0; right < s.length(); right++) {
            // adiciona char da direita
            char c = s.charAt(right);
            have.put(c, have.getOrDefault(c, 0) + 1);

            // verifica se esse char satisfez a necessidade
            if (need.containsKey(c) && have.get(c).equals(need.get(c))) {
                formed++;
            }

            // tenta encolher a janela pela esquerda
            while (formed == required) {
                // atualiza o menor resultado
                if (right - left + 1 < minLen) {
                    minLen = right - left + 1;
                    minLeft = left;
                }

                // remove char da esquerda
                char leftChar = s.charAt(left);
                have.put(leftChar, have.get(leftChar) - 1);
                if (need.containsKey(leftChar) && have.get(leftChar) < need.get(leftChar)) {
                    formed--;
                }
                left++;
            }
        }

        var result = minLen == Integer.MAX_VALUE ? "" : s.substring(minLeft, minLeft + minLen);
        System.out.println(result);
    }
}
