import static org.dreambot.api.methods.Calculations.random;
import static org.dreambot.api.methods.MethodProvider.sleep;
import static org.dreambot.api.methods.MethodProvider.sleepUntil;

public class WithdrawPureEssence extends Node {

    public WithdrawPureEssence(AstralRunecrafter main) {
        super(main);
    }

    @Override
    public boolean validate() {
        boolean isBanking = main.state == AstralRunecrafter.State.BANKING;
        boolean isInventoryFull = main.getInventory().isFull();
        return isBanking && !isInventoryFull;
    }

    @Override
    public int execute() {
        final int PURE_ESSENCE_ID = 7936;
        int startCount = main.getInventory().count(PURE_ESSENCE_ID);
        main.getBank().openClosest();
        sleepUntil(() -> main.getBank().isOpen(), random(3500, 4000));
        sleep(50, 100);
        main.getBank().withdrawAll(PURE_ESSENCE_ID);
        sleepUntil(() -> main.getInventory().isFull(), random(1000, 1500));
        main.getKeyboard().closeInterfaceWithESC();
        boolean isReadyToRunToAltar = main.getInventory().isFull() &&
                main.areBigPouchesFull && main.areSmallPouchesFull;
        if (isReadyToRunToAltar) {
            main.state = AstralRunecrafter.State.RUNNING_TO_ALTAR;
        }
        int endCount = main.getInventory().count(PURE_ESSENCE_ID);
        accountConsumedPureEssence(startCount, endCount);
        return main.sleepTime();
    }

    private void accountConsumedPureEssence(int startCount, int endCount) {
        main.pureEssenceConsumed += (endCount - startCount);
    }
}
