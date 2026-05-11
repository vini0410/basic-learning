package ai_answers;

import java.util.Stack;

public class TestTenTwo {
    void main(String[] args) {

        var array = args[0].split(",");
        var heights = new int[array.length];
        for (int i = 0; i < array.length; i++) {
            heights[i] = Integer.parseInt(array[i]);
        }

        Stack<Integer> stack = new Stack<>();
        var maxArea = 0;

        for (int i = 0; i <= heights.length; i++) {

            // altura atual (0 no final para forçar esvaziar a stack)
            int currentHeight;
            if (i == heights.length) {
                currentHeight = 0;
            } else {
                currentHeight = heights[i];
            }

            // enquanto a barra atual for menor que o topo da stack
            while (!stack.isEmpty() && currentHeight < heights[stack.peek()]) {

                // pega o índice do topo e calcula a altura dele
                int topIndex = stack.pop();
                int height = heights[topIndex];

                // calcula a largura
                int width;
                if (stack.isEmpty()) {
                    width = i; // se stack vazia, vai da borda até i
                } else {
                    width = i - stack.peek() - 1; // distância entre os índices
                }

                // calcula e compara a área
                int area = height * width;
                if (area > maxArea) {
                    maxArea = area;
                }
            }

            stack.push(i);
        }

        System.out.println("Maior área: " + maxArea);
    }
}
