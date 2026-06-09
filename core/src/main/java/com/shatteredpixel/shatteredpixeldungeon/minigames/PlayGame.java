//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.shatteredpixel.shatteredpixeldungeon.minigames;

import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class PlayGame {
    private static final ArrayList<Integer> defaultCards = new ArrayList<>(Arrays.asList(
            1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13,
            1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13,
            1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13,
            1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13));
    private static ArrayList<Integer> cards  = new ArrayList<>();
    public static ArrayList<Integer> PCcards = new ArrayList<>();
    public static ArrayList<Integer> AIcards = new ArrayList<>();
    public static int PCscore;
    public static int AIscore;
    public static int PCnum = 0;
    public static int AInum = 0;
    public static boolean PCdraw;
    public static boolean AIdraw;
    public static int turn;

    public static void resetHand() {
        cards = new ArrayList<>(defaultCards);
        if (!PCcards.isEmpty()) {
            PCcards.subList(0, PCcards.size()).clear();
        }

        PCnum = 0;
        if (!AIcards.isEmpty()) {
            AIcards.subList(0, AIcards.size()).clear();
        }

        AInum = 0;
    }

    public static int randomCard() {
        if (cards.isEmpty()) {
            cards = new ArrayList<>(defaultCards);
        }

        return cards.remove(Random.Int(cards.size()));
    }

    public static int count(ArrayList<Integer> arrayList) {
        int totalNum = 0;
        boolean haveA = false;

        for(int card : arrayList) {
            totalNum += Math.min(card, 10);
            if (card == 1) {
                haveA = true;
            }
        }

        if (totalNum > 21) {
            return 0;
        } else {
            if (totalNum <= 11 && haveA) {
                totalNum += 10;
            }

            if (totalNum == 21 && haveA) {
                ++totalNum;
            }

            return totalNum;
        }
    }

    public static void drawCard(ArrayList<Integer> arrayList) {
        int card = randomCard();
        arrayList.add(card);
    }

    public static void gameStart() {
        resetHand();
        turn = 1;
        PCdraw = false;
        AIdraw = false;
        drawCard(PCcards);
        drawCard(PCcards);
        drawCard(AIcards);
        drawCard(AIcards);
        PCnum = count(PCcards);
        AInum = count(AIcards);
    }

    public static void nextTurn() {
        ++turn;
        AI();
        if (!PCdraw && !AIdraw) {
            endGame();
        } else {
            if (PCdraw) {
                drawCard(PCcards);
            }

            if (AIdraw) {
                drawCard(AIcards);
            }

            PCnum = count(PCcards);
            AInum = count(AIcards);
            GameScene.show(new WndPlayGame(false));
        }
    }

    public static void endGame() {
        GameScene.show(new WndPlayGame(true));
    }

    public static void AI() {
        float num = 0.0F;
        int length = 0;

        HashMap<Integer, Integer> left = getLeft();
        for(int card : left.keySet()) {
            num += Math.min(card, 10) * left.get(card);
            length += left.get(card);
        }

        num /= length;
        if (AInum <= 20 && AInum != 0 && AIcards.size() != 5) {
            int PC = AISeen();
            if (PC == -1)
                AIdraw = false;
            else if (PC > AInum)
                AIdraw = true;
            else if (AInum <= 11)
                AIdraw = true;
            else {
                if (num + AInum <= 21.0F) {
                    AIdraw = Random.SB2_Float(5) > rate(AInum);
                }

                else if (num + AInum > 21.0F) {
                    AIdraw = Random.NormalFloat(10) > rate(AInum);
                }

            }
        } else {
            AIdraw = false;
        }
    }
    private static float rate(int num){
        HashMap<Integer, Integer> left = getLeft();
        int all = 0;
        int large = 0;
        int i = 1;
        while (i <= 13){
            all += left.get(i);
            if (Math.min(10, i) + num > 21)
                large += left.get(i);
            i++;
        }
        return (float) large/all;
    }
    private static int AISeen(){
        ArrayList<Integer> PCBase = new ArrayList<>(Arrays.asList(PCcards.get(0), PCcards.get(1)));
        int PC = Math.min(PCBase.get(0), 10) + Math.min(PCBase.get(1), 10);
        if (PCBase.contains(1))
            PC += 10;
        if (PCcards.size() == 2)
            return PC;
        return guessSecret(PCBase);
    }
    private static HashMap<Integer, Integer> getLeft(){
        HashMap<Integer, Integer> Seen = new HashMap<>();
        setSeen(Seen, PCcards.get(0));
        setSeen(Seen, PCcards.get(1));
        for (Integer card: AIcards){
            setSeen(Seen, card);
        }
        int i = 1;
        int time;
        HashMap<Integer, Integer> map = new HashMap<>();
        while ( i <= 13 ){
            if (Seen.containsKey(i))
                time = 4 - Seen.get(i);
            else
                time = 4;
            map.put(i, time);
            i++;
        }
        return map;
    }
    private static void setSeen(HashMap<Integer, Integer> Seen, int card){
        if (Seen.containsKey(card)){
            Seen.put(card, Seen.get(card)+1);
        }else {
            Seen.put(card, 1);
        }
    }
    private static int guessSecret( ArrayList<Integer> PCBase ){
        HashMap<Integer, Integer> map = getLeft();
        int PC = Math.min(PCBase.get(0), 10) + Math.min(PCBase.get(1), 10);
        boolean hasA = false;
        if (PCBase.contains(1)) {
            hasA = true;
            PC += 10;
        }
        int guessMin = 1;
        int left = PCcards.size() - 2;
        while ( left > 0 ){
            if (map.get(guessMin) > 0){
                PC += Math.min(10, guessMin);
                map.put(guessMin, map.get(guessMin)-1);
                left--;
            }else {
                guessMin++;
            }
            if (PC > 21){
                if (hasA) {
                    PC -= 10;
                    hasA = false;
                }
                else
                    return -1;
            }
        }
        return PC;
    }
}
