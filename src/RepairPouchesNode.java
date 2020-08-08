import org.dreambot.api.methods.magic.Lunar;
import org.dreambot.api.methods.tabs.Tab;

import static org.dreambot.api.methods.Calculations.random;
import static org.dreambot.api.methods.MethodProvider.sleep;
import static org.dreambot.api.methods.MethodProvider.sleepUntil;

public class RepairPouchesNode extends Node {

    public RepairPouchesNode(AstralRunecrafter main) {
        super(main);
    }

    @Override
    public boolean validate() {
        final int BROKEN_GIANT_POUCH_ID = 5515;
        return main.getInventory().contains(BROKEN_GIANT_POUCH_ID);
    }

    @Override
    public int execute() {
        sleep(700, 850);
        main.getTabs().openWithFKey(Tab.MAGIC);
        sleepUntil(() -> main.getTabs().isOpen(Tab.MAGIC), random(1000, 1500));
        sleep(400, 500);
        main.getMagic().castSpell(Lunar.NPC_CONTACT);
        accountConsumedRunes();
        sleepUntil(() -> main.getWidgets().getWidgetChild(75, 14) != null, random(1000, 1500));
        sleep(400, 500);
        main.getWidgets().getWidgetChild(75, 14).interact();
        sleepUntil(() -> main.getDialogues().inDialogue(), random(6000, 7000));
        sleep(175, 200);
        while (main.getDialogues().inDialogue()) {
            main.getDialogues().spaceToContinue();
            sleep(200, 400);
        }
        sleep(700, 850);
        main.getTabs().openWithFKey(Tab.INVENTORY);
        sleep(700, 850);
        return main.sleepTime();
    }

    private void accountConsumedRunes() {
        main.astralRunesConsumed++;
        main.cosmicRunesConsumed++;
    }
}
