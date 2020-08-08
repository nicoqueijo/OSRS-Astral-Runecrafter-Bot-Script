import org.dreambot.api.methods.magic.Lunar;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.tabs.Tab;

import java.awt.Rectangle;

import static org.dreambot.api.methods.Calculations.random;
import static org.dreambot.api.methods.MethodProvider.sleep;
import static org.dreambot.api.methods.MethodProvider.sleepUntil;

public class RunecraftNode extends Node {

    public RunecraftNode(AstralRunecrafter main) {
        super(main);
    }

    @Override
    public boolean validate() {
        return main.state == AstralRunecrafter.State.RUNECRAFTING;
    }

    @Override
    public int execute() {
        final int GIANT_POUCH_ID = 5514;
        final int LARGE_POUCH_ID = 5512;
        final int MEDIUM_POUCH_ID = 5510;
        final int SMALL_POUCH_ID = 5509;
        final int ALTAR_ID = 34771;
        final Rectangle TELE_GROUP_MOONCLAN_BOUNDS = new Rectangle(654, 251, 8, 8);
        final int TELE_ANIMATION = 1816;
        final int ASTRAL_RUNE_ID = 9075;

        int startCount = main.getInventory().count(ASTRAL_RUNE_ID);
        final int preEmptySlotCount1 = main.getInventory().getEmptySlots();
        main.getGameObjects().closest(ALTAR_ID).interact();
        main.getMouse().move(main.getInventory().slotBounds(0));
        sleepUntil(() -> {
            int postEmptySlotCount = main.getInventory().getEmptySlots();
            boolean isPlayerAnimating = main.getLocalPlayer().isAnimating();
            return preEmptySlotCount1 < postEmptySlotCount && !isPlayerAnimating;
        }, random(3500, 4000));

        main.getKeyboard().pressShift();
        sleep(120, 140);
        main.getInventory().get(GIANT_POUCH_ID).interact();
        sleep(120, 140);
        main.getInventory().get(LARGE_POUCH_ID).interact();
        sleep(120, 140);
        main.getKeyboard().releaseShift();
        sleep(120, 140);
        main.areBigPouchesFull = false;

        final int preEmptySlotCount2 = main.getInventory().getEmptySlots();
        main.getGameObjects().closest(ALTAR_ID).interact();
        main.getMouse().move(main.getInventory().slotBounds(1));
        sleepUntil(() -> {
            int postEmptySlotCount = main.getInventory().getEmptySlots();
            boolean isPlayerAnimating = main.getLocalPlayer().isAnimating();
            return preEmptySlotCount2 < postEmptySlotCount && !isPlayerAnimating;
        }, random(3500, 4000));

        main.getKeyboard().pressShift();
        sleep(120, 140);
        main.getInventory().get(MEDIUM_POUCH_ID).interact();
        sleep(120, 140);
        main.getInventory().get(SMALL_POUCH_ID).interact();
        sleep(120, 140);
        main.getKeyboard().releaseShift();
        sleep(120, 140);
        main.areSmallPouchesFull = false;

        final int preEmptySlotCount3 = main.getInventory().getEmptySlots();
        main.getGameObjects().closest(ALTAR_ID).interact();
        main.getTabs().openWithFKey(Tab.MAGIC);
        main.getMouse().move(TELE_GROUP_MOONCLAN_BOUNDS);

        sleepUntil(() -> {
            int postEmptySlotCount = main.getInventory().getEmptySlots();
            boolean isPlayerAnimating = main.getLocalPlayer().isAnimating();
            return preEmptySlotCount3 < postEmptySlotCount && !isPlayerAnimating;
        }, random(3500, 4000));
        int endCount = main.getInventory().count(ASTRAL_RUNE_ID);
        accountProducedAstralRunes(startCount, endCount);

        main.getMagic().castSpell(Lunar.TELE_GROUP_MOONCLAN);
        accountConsumedTeleRunes();
        sleepUntil(() -> main.getLocalPlayer().getAnimation() == TELE_ANIMATION, random(1000, 1500));
        sleep(200, 300);
        main.getTabs().openWithFKey(Tab.INVENTORY);
        sleep(200, 300);
        Rectangle bankMinimapArea = new Rectangle(588, 88, 60, 50);
        main.getMouse().move(bankMinimapArea);

        final Area MOONCLAN_TELEPORT_AREA = new Area(new Tile(2115, 3917, 0), new Tile(2106, 3912, 0));
        sleepUntil(() -> MOONCLAN_TELEPORT_AREA.
                        contains(main.getLocalPlayer().getTile()),
                random(3500, 4000));

        boolean isPlayerInTeleArea = MOONCLAN_TELEPORT_AREA.
                contains(main.getLocalPlayer().getTile());
        if (isPlayerInTeleArea) {
            main.state = AstralRunecrafter.State.RUNNING_TO_BANK;
        }
        return main.sleepTime();
    }

    private void accountConsumedTeleRunes() {
        main.astralRunesConsumed += 2;
        main.lawRunesConsumed++;
    }

    private void accountProducedAstralRunes(int startCount, int endCount) {
        main.astralRunesProduced += (endCount - startCount);
    }
}
