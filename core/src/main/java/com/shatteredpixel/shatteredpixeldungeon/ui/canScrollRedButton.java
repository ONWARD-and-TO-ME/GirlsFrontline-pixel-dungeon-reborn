package com.shatteredpixel.shatteredpixeldungeon.ui;

public class canScrollRedButton extends RedButton implements canScrollButton {

    public canScrollRedButton(String label) {
        super(label);
    }
    public canScrollRedButton(Enum<?> anEnum) {
        super(anEnum.name());
        this.anEnum = anEnum;
    }
    public canScrollRedButton(Enum<?> anEnum, String name) {
        super(name);
        this.anEnum = anEnum;
    }
    public int num;
    public Enum<?> anEnum;
    public canScrollRedButton(String title, int num) {
        super(title);
        this.num = num;
    }
    public canScrollRedButton(int num) {
        super(String.valueOf(num));
        this.num = num;
    }
    public boolean onClick(float x, float y){
        if(!inside(x,y)) return false;
        onClick();

        return true;
    }

    @Override
    public void onClick(){
        super.onClick();
    }

    @Override
    public void layout(){
        super.layout();
        hotArea.width = hotArea.height = 0;
    }
}