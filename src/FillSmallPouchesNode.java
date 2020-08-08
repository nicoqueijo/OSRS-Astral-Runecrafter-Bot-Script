import static org.dreambot.api.methods.MethodProvider.sleep;

/**
 * "Small" pouches consist of the medium and small pouch.
 */
public class FillSmallPouchesNode extends Node {

    public FillSmallPouchesNode(AstralRunecrafter main) {
        super(main);
    }

    @Override
    public boolean validate() {
        boolean isBanking = main.state == AstralRunecrafter.State.BANKING;
        boolean areSmallPouchesEmpty = !main.areSmallPouchesFull;
        return isBanking && areSmallPouchesEmpty;
    }

    @Override
    public int execute() {
        final int MEDIUM_POUCH_ID = 5510;
        final int SMALL_POUCH_ID = 5509;
        main.getInventory().interact(MEDIUM_POUCH_ID, "Fill");
        sleep(100, 150);
        main.getInventory().interact(SMALL_POUCH_ID, "Fill");
        sleep(250, 350);
        main.areSmallPouchesFull = true;
        return main.sleepTime();
    }
}
