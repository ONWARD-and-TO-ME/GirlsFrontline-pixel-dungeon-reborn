package com.shatteredpixel.shatteredpixeldungeon.ui;

public class canScrollRedButton extends RedButton {

    public canScrollRedButton(String label) {
        super(label);
    }
    public int num;
    public canScrollRedButton(int num) {
        super(String.valueOf(num));
        this.num = num;
    }
    public canScrollRedButton( String label, int size ){
        super(label, size);
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