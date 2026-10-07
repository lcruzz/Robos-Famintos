package model;

public class Rocha extends Obstaculo {

    public Rocha(String id, int x, int y) {
        super(id, x, y);
    }

    @Override
    public void bater(Robo robo, int xAnterior, int yAnterior) {
        robo.voltarPara(xAnterior, yAnterior);
    }
}
