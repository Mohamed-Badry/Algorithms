public class OptimizedTrie {
    private static final int R = 26;
    private Node root = new Node();

    private static class Node {
        Node[] next = new Node[R];
        boolean isWord;
    }

    public void add(String word) {
        Node node = root;
        for (char c : word.toCharArray()) {
            int index = c - 'A';
            if (node.next[index] == null) {
                node.next[index] = new Node();
            }
            node = node.next[index];
        }
        node.isWord = true;
    }

    public boolean contains(String word) {
        Node node = getNode(word);
        return node != null && node.isWord;
    }

    public boolean hasPrefix(String prefix) {
        return getNode(prefix) != null;
    }

    private Node getNode(String key) {
        Node node = root;
        for (char c : key.toCharArray()) {
            int index = c - 'A';
            if (node.next[index] == null)
                return null;
            node = node.next[index];
        }
        return node;
    }
}