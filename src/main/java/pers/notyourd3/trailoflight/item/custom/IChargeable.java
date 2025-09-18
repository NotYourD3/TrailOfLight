package pers.notyourd3.trailoflight.item.custom;

public interface IChargeable {
    int getMaxCharge();
    void charge(int charge);
    void decharge(int charge);

}
