public abstract class Node {

    protected final AstralRunecrafter main;

    public Node(AstralRunecrafter main) {
        this.main = main;
    }

    public abstract boolean validate();

    public abstract int execute();
}
