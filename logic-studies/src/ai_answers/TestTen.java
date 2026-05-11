package ai_answers;

import java.util.Stack;

public class TestTen {

    void main(String[] args) {
        /*Dado um array de inteiros representando a altura de barras de um histograma,
        onde cada barra tem largura 1,
        encontre a área do maior retângulo que pode ser formado dentro do histograma.
        *
        * */

        var array = args[0].split(",");
        var heights = new int[array.length];
        for (int i = 0; i < array.length; i++) {
            heights[i] = Integer.parseInt(array[i]);
        }

        Stack<Integer> stack = new Stack<>(); // guarda índices
        var maxArea = 0;

        for (int i = 0; i <= heights.length; i++) {
            // altura atual (0 no final para forçar esvaziar a stack)
            int currentHeight = (i == heights.length) ? 0 : heights[i];

            while (!stack.isEmpty() && currentHeight < heights[stack.peek()]) {
                int height = heights[stack.pop()];
                int width = stack.isEmpty() ? i : i - stack.peek() - 1;
                int area = height * width;
                if (area > maxArea) maxArea = area;
            }

            stack.push(i);
        }

        System.out.println("Maior área: " + maxArea);

    }
}
