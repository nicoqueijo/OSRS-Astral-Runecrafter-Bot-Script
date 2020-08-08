import static org.dreambot.api.methods.Calculations.random;
import static org.dreambot.api.methods.MethodProvider.sleepUntil;

public class WithdrawStaminaPotion extends Node {

    final int STAMINA_POTION_4_ID = 12625;

    public WithdrawStaminaPotion(AstralRunecrafter main) {
        super(main);
    }

    @Override
    public boolean validate() {
        final int STAMINA_POTION_1_ID = 12631;
        final int STAMINA_POTION_2_ID = 12629;
        final int STAMINA_POTION_3_ID = 12627;
        boolean isBanking = main.state == AstralRunecrafter.State.BANKING;
        boolean isMissingStaminaPotion = !main.getInventory().contains(item -> {
            int id = item.getID();
            return (id == STAMINA_POTION_1_ID || id == STAMINA_POTION_2_ID ||
                    id == STAMINA_POTION_3_ID || id == STAMINA_POTION_4_ID);
        });
        return isBanking && isMissingStaminaPotion;
    }

    @Override
    public int execute() {
        main.getBank().openClosest();
        sleepUntil(() -> main.getBank().isOpen(), random(1000, 1500));
        main.getBank().withdraw(STAMINA_POTION_4_ID);
        main.staminaPotionsConsumed++;
        sleepUntil(() -> main.getInventory().contains(STAMINA_POTION_4_ID), random(1000, 1500));
        return main.sleepTime();
    }
}
