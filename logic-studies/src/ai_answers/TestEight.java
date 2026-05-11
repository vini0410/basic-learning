package ai_answers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Stack;

public class TestEight {

    void main(String[] args) {
        /* Dada uma string contendo apenas os caracteres (, ), [, ], {, },
        determine se a string é válida.
        Uma string é válida se todo abre-parêntese é fechado na ordem correta.
        * */

        var array = args[0].split(",");
        var arrayString = new ArrayList<String>(Arrays.asList(array));
        var success = true;
        Stack<String> stack = new Stack<>();

        for (String index : arrayString) {
            if (Objects.equals(index, "(")
                    || Objects.equals(index, "[")
                    || Objects.equals(index, "{")) {
                stack.push(index);
            } else {
                if (stack.isEmpty()) {
                    success = false;
                    break;
                }
                String top = stack.peek();
                if ((Objects.equals(index, ")") && Objects.equals(top, "("))
                        || (Objects.equals(index, "]") && Objects.equals(top, "["))
                        || (Objects.equals(index, "}") && Objects.equals(top, "{"))) {
                    stack.pop();
                } else {
                    success = false;
                    break;
                }
            }
        }
        if (!stack.isEmpty()) success = false;
        System.out.println(success);
    }


}
//Stack<String> stack = new Stack<>();
//stack.push("(");   // empilha
//stack.pop();       // desempilha
//stack.peek();      // vê o topo sem remover
//stack.isEmpty();   // verifica se está vazia

