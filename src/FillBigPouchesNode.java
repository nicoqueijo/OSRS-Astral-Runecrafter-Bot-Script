import static org.dreambot.api.methods.MethodProvider.sleep;

/**
 * "Big" pouches consist of the giant and large pouch.
 */
public class FillBigPouchesNode extends Node {

    public FillBigPouchesNode(AstralRunecrafter main) {
        super(main);
    }

    @Override
    public boolean validate() {
        boolean isBanking = main.state == AstralRunecrafter.State.BANKING;
        boolean areBigPouchesEmpty = !main.areBigPouchesFull;
        return isBanking && areBigPouchesEmpty;
    }

    @Override
    public int execute() {
        final int GIANT_POUCH_ID = 5514;
        final int LARGE_POUCH_ID = 5512;
        main.getInventory().interact(GIANT_POUCH_ID, "Fill");
        sleep(100, 150);
        main.getInventory().interact(LARGE_POUCH_ID, "Fill");
        sleep(250, 350);
        main.areBigPouchesFull = true;
        return main.sleepTime();
    }
}
