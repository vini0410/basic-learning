package my_answers;

import java.util.ArrayList;
import java.util.Arrays;

public class TestFourteen {
    void main(String[] args) {
        /*
        *Dada uma matriz 2D de letras e uma lista de palavras, encontre a palavra mais longa da lista que pode ser formada na matriz.
        * Uma palavra pode ser formada usando letras adjacentes (horizontal, vertical ou diagonal), sem reutilizar a mesma célula na mesma palavra.

        **Exemplo:**
        ```
        matriz:
        [
          ['o','a','a','n'],
          ['e','t','a','e'],
          ['i','h','k','r'],
          ['i','f','l','v']
        ]

        palavras: ["eat","oath","tank","fate"]

        saída: "oath" (tamanho 4, encontrada na matriz)
        ```

        **Restrições:**
        - `1 <= linhas, colunas <= 12`
        - A lista pode ter até `3 * 10^4` palavras
        - Cada palavra tem entre 1 e 10 letras

        */
        var mat = args[1];
        var rows = mat.split(";");
        var matrix = new char[rows.length][];
        for (int a = 0; a < rows.length; a++) {
            String[] cols = rows[a].split(",");
            matrix[a] = new char[cols.length];
            for (int b = 0; b < cols.length; b++) {
                matrix[a][b] = cols[b].charAt(0);
            }
        }
        var maxWord = "";

        var strings = args[0].split(",");
        var words = new ArrayList<String>(Arrays.asList(strings));

        for (int l = 0; l < matrix.length; l++) {
            for (int c = 0; c < matrix[0].length; c++) {
                for (int w = 0; w < words.size(); w++) {
                    var word = words.get(w);
                    var currentWord = find(matrix, l, c, word, 0);
                    if (currentWord!= null && currentWord.length() > maxWord.length()) {
                        maxWord = currentWord;
                    }
                }
            }
        }
        System.out.println(maxWord);

    }

    String find(char[][] matrix, int l, int c, String w, int index) {
        if (index == w.length()) return w;
        if (l < 0 || c < 0 || l >= matrix.length || c >= matrix[l].length) return null;
        if (matrix[l][c] == '#') return null;
        var mc = matrix[l][c];
        var wc = w.charAt(index);
        if (mc == wc) {
            var temp = matrix[l][c]; // ✅ aqui!
            matrix[l][c] = '#';
            var r = find(matrix, l,   c+1, w, index+1);
            if (r == null) r = find(matrix, l,   c-1, w, index+1);
            if (r == null) r = find(matrix, l+1, c,   w, index+1);
            if (r == null) r = find(matrix, l-1, c,   w, index+1);
            if (r == null) r = find(matrix, l+1, c+1, w, index+1);
            if (r == null) r = find(matrix, l+1, c-1, w, index+1);
            if (r == null) r = find(matrix, l-1, c+1, w, index+1);
            if (r == null) r = find(matrix, l-1, c-1, w, index+1);
            matrix[l][c] = temp;
            return r;
        }
        return null;
    }

}