package ai_answers;

import java.util.Arrays;

public class TestEleven {

    void main(String[] args) {
        /*
        * Dado uma matriz 2D contendo apenas `'1'` (terra) e `'0'` (água), conte o número de ilhas.
        * Uma ilha é formada por terra conectada horizontalmente ou verticalmente, cercada por água nas bordas.
        **Exemplo:**
        ```
        entrada:
        [
          ["1","1","0","0","0"],
          ["1","1","0","0","0"],
          ["0","0","1","0","0"],
          ["0","0","0","1","1"]
        ]
        saída: 3
        ```
        * */

//      grid[i][j]      // célula
//      grid[i].length  // colunas
//      grid.length     // linhas

        var input = args[0]; // "110/110/001"
        var rows = input.split("/");
        var grid = new char[rows.length][];
        for (int i = 0; i < rows.length; i++) {
            grid[i] = rows[i].toCharArray();
        }
        System.out.println(Arrays.deepToString(grid));
        var islandCount = 0;


        for (int l = 0; l < grid.length; l++) {
            for (int c = 0; c < grid[l].length; c++) {
                if (grid[l][c] == '1') { // terra
                    expande(grid, l,c);
                    islandCount++;
                }
            }
        }
        System.out.println(islandCount);
    }

    void expande(char[][] grid, int l, int c) {
        if (l < 0 || l >= grid.length) return;
        if (c < 0 || c >= grid[l].length) return;
        if (grid[l][c] == '0') return;

        grid[l][c] = '0';
        expande(grid, l, c+1);
        expande(grid, l, c-1);
        expande(grid, l+1, c);
        expande(grid, l-1, c);
    }
}

