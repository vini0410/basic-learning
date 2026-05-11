package my_answers;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class TestThirteen {
    void main(String[] args) {
        /* Implemente dois métodos:
            - `serialize(root)` → converte uma árvore binária em uma string
            - `deserialize(data)` → reconstrói a árvore a partir da string
        Não há restrição no formato da string, desde que os dois métodos sejam consistentes entre si.
        1,2,4,null,null,5,null,null,3,null,null
            1
           / \
          2   3
         / \
        4   5
        */

        var string = args[0].split(",");
        Queue<String> queue = new LinkedList<>(Arrays.asList(string));

        var root = deserialize(queue);
        printTree(root);

        var ser = serialize(root);
        System.out.println("");
        System.out.println(ser);
    }

    Node deserialize(Queue<String> queue) {
        var val = queue.poll();
        if (val.equals("null")) return null;

        Node node = new Node();
        node.val = Integer.parseInt(val);
        node.left = deserialize(queue);
        node.right = deserialize(queue);
        return node;
    }

    String serialize(Node root) { // 1,2,4,null,null,5,null,null,3,null,null
        var result = "";
        if (root == null) {
            result = result + "null,";
        } else {
            result = result + root.val + ",";
            result = result + serialize(root.left);
            result = result + serialize(root.right);
        }
        return result;
    }

    void printTree(Node root) {
        if (root == null) {
            System.out.print("null/");
            return;
        } else {
            System.out.print(root.val + "/");
            printTree(root.left);
            printTree(root.right);
        }
    }

    class Node {
        int val;
        Node left;
        Node right;
    }
}


