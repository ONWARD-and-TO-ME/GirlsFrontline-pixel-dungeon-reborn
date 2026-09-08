package com.shatteredpixel.shatteredpixeldungeon.ui;

public class canScrollRedButton extends RedButton implements canScrollButton {

    public canScrollRedButton(String label) {
        super(label);
    }
    public canScrollRedButton(Enum<?> anEnum) {
        super(anEnum.toString());
    }
    public int num;
    public canScrollRedButton(String title, int num) {
        super(title);
        this.num = num;
    }
    public canScrollRedButton(int num) {
        super(String.valueOf(num));
        this.num = num;
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