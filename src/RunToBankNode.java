import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.map.Tile;

import static org.dreambot.api.methods.MethodProvider.sleep;

public class RunToBankNode extends Node {

    public RunToBankNode(AstralRunecrafter main) {
        super(main);
    }

    @Override
    public boolean validate() {
        return main.state == AstralRunecrafter.State.RUNNING_TO_BANK;
    }

    @Override
    public int execute() {
        Tile bankBooth1 = new Tile(2099, 3919, 0);
        Tile bankBooth2 = new Tile(2098, 3919, 0);
        int randomBooth = Calculations.random(1);
        Tile bankBoothTile = randomBooth == 0 ? bankBooth1 : bankBooth2;
        main.getWalking().walk(bankBoothTile);
        sleep(3500, 4000);
        main.state = AstralRunecrafter.State.BANKING;
        return main.sleepTime();
    }
}
