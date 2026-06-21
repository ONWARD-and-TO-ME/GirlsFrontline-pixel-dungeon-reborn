package com.shatteredpixel.shatteredpixeldungeon.ui.changelist;

import com.shatteredpixel.shatteredpixeldungeon.GirlsFrontlinePixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.ColorItem;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.BlacksmithSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.HeroSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.TalentIcon;
import com.watabou.noosa.Image;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.ui.Component;

import java.util.ArrayList;
import java.util.Arrays;

public class ChangeButton extends Component {
    protected Image icon;
    protected float size = -1;
    protected int color = -1;
    protected String title;
    protected String message;
    protected MovieClip.Animation action;
    protected Boolean cycle;
    protected ArrayList<Image> pageImage;
    protected ArrayList<Float> pageSize;
    protected ArrayList<Integer> pageColor;
    protected ArrayList<MovieClip.Animation> pageAction;
    protected ArrayList<Boolean> pageCycle;
    protected ArrayList<String> pageTitle;
    protected ArrayList<String> pageMessages;
    public ChangeButton(Object... args) {
        super();

        ArrayList<Object> imgList = new ArrayList<>();
        ArrayList<Float> sizeList = new ArrayList<>();
        ArrayList<MovieClip.Animation> actionList = new ArrayList<>();
        ArrayList<Boolean> cycleList = new ArrayList<>();
        ArrayList<Integer> colorList = new ArrayList<>();
        ArrayList<ArrayList<String>> stringSlots = new ArrayList<>();

        if (args != null) {
            for (Object arg : args) {
                if (arg == null) continue;

                ArrayList<?> list = normalizeToList(arg);
                Object first = firstNonNull(list);

                if (first == null) {
                    // 约定：空占位只用数组。如果是 String 数组则作为空占位符统计，其余全 null 情况直接跳过
                    if (arg instanceof String[]) stringSlots.add(new ArrayList<>());
                    continue;
                }

                if (first instanceof Image || first instanceof Char || first instanceof Item || first instanceof Talent)
                    imgList.addAll(list);
                else if (first instanceof Float)
                    for (Object o : list) sizeList.add((Float) o);
                else if (first instanceof MovieClip.Animation)
                    for (Object o : list) actionList.add((MovieClip.Animation) o);
                else if (first instanceof Boolean)
                    for (Object o : list) cycleList.add((Boolean) o);
                else if (first instanceof Integer)
                    for (Object o : list) colorList.add((Integer) o);
                else if (first instanceof String) {
                    ArrayList<String> strSlot = new ArrayList<>();
                    // 保留 null 占位，防止页数错位
                    for (Object o : list) strSlot.add((String) o);
                    stringSlots.add(strSlot);
                }
            }
        }

        ArrayList<String> titleList = null;
        ArrayList<String> messageList = null;
        while (stringSlots.size() >= 3) {
            if (firstNonNull(stringSlots.get(0)) == null) {
                stringSlots.remove(0);
                continue;
            }
            if (firstNonNull(stringSlots.get(1)) == null) {
                stringSlots.remove(1);
                continue;
            }
            break;
        }
        if (stringSlots.size() >= 2) {
            titleList = stringSlots.get(0);
            messageList = stringSlots.get(1);
        } else if (stringSlots.size() == 1) {
            messageList = stringSlots.get(0);
        }

        int maxPage = 1;
        maxPage = Math.max(maxPage, imgList.size());
        maxPage = Math.max(maxPage, sizeList.size());
        maxPage = Math.max(maxPage, actionList.size());
        maxPage = Math.max(maxPage, cycleList.size());
        maxPage = Math.max(maxPage, colorList.size());
        if (titleList != null) maxPage = Math.max(maxPage, titleList.size());
        if (messageList != null) maxPage = Math.max(maxPage, messageList.size());

        Object[] ImageSrc = new Object[maxPage];
        Image[] img_ = new Image[maxPage];
        Object firstSrc = firstNonNull(imgList);
        if (firstSrc == null) firstSrc = new BlacksmithSprite();
        for (int i = 0; i < maxPage; i++) {
            if (i < imgList.size()){
                if (imgList.get(i) != null) ImageSrc[i] = imgList.get(i);
                else ImageSrc[i] = firstSrc;
                img_[i] = getSignalImage((ImageSrc[i]));
                continue;
            }
            ImageSrc[i] = img_[i] = new BlacksmithSprite();
        }
        Item.ignoreGuess = true;
        if (maxPage == 1){
            this.icon = buildDisplayImage(img_[0],
                    (this.size = resolveInherit(sizeList, 0, img_, -1F)),
                    (this.color = resolveInherit(colorList, 0, img_, -1)),
                    (this.action = resolveInherit(actionList, 0, img_, null)),
                    (this.cycle = resolveInherit(cycleList, 0, img_, null)));
            this.title = Messages.titleCase(getOrDef(titleList, 0, getSignalName(ImageSrc[0])));
            this.message = getOrDef(messageList, 0, getSignalInfo(ImageSrc[0]));
        }
        else {
            pageImage = new ArrayList<>();
            pageSize = new ArrayList<>();
            pageColor = new ArrayList<>();
            pageAction = new ArrayList<>();
            pageCycle = new ArrayList<>();
            pageTitle = new ArrayList<>();
            pageMessages = new ArrayList<>();
            for (int i = 0; i < maxPage; i++) {
                float curSize = resolveInherit(sizeList, i, img_, -1F);
                int curColor = resolveInherit(colorList, i, img_, -1);
                MovieClip.Animation curAction = resolveInherit(actionList, i, img_, null);
                Boolean curCycle = resolveInherit(cycleList, i, img_, null);

                String curTitle = Messages.titleCase(getOrDef(titleList, i, getSignalName(ImageSrc[i])));
                String curContent = getOrDef(messageList, i, getSignalInfo(ImageSrc[i]));

                pageImage.add(buildDisplayImage(img_[i], curSize, curColor, curAction, curCycle));
                pageSize.add(curSize);
                pageColor.add(curColor);
                pageAction.add(curAction);
                pageCycle.add(curCycle);
                pageTitle.add(curTitle);
                pageMessages.add(curContent);
            }

            this.icon = pageImage.get(0);
        }
        Item.ignoreGuess = false;
        add(this.icon);

        layout();
    }
    private static ArrayList<?> normalizeToList(Object arg) {
        if (arg instanceof ArrayList) return (ArrayList<?>) arg;
        if (arg instanceof Object[]) return new ArrayList<>(Arrays.asList((Object[]) arg));

        ArrayList<Object> result = new ArrayList<>();
        if (arg instanceof float[]) for (float f : (float[]) arg) result.add(f);
        else if (arg instanceof int[]) for (int v : (int[]) arg) result.add(v);
        else if (arg instanceof boolean[]) for (boolean b : (boolean[]) arg) result.add(b);
        else
            result.add(arg);
        return result;
    }
    private static Object firstNonNull(ArrayList<?> list) {
        for (Object o : list)
            if (o != null)
                return o;
        return null;
    }
    private static <T> T getOrDef(ArrayList<T> list, int i, T def) {
        if (list == null || i >= list.size() || list.get(i) == null)
            return def;
        return list.get(i);
    }
    private static <T> T resolveInherit(ArrayList<T> slot, int i, Image[] images, T defaultValue) {
        if (slot != null && i < slot.size() && slot.get(i) != null)
            return slot.get(i);
        if (slot != null) {
            for (int j = 0; j < images.length; j++) {
                if (j != i && images[j].getClass() == images[i].getClass()
                        && j < slot.size() && slot.get(j) != null) {
                    return slot.get(j);
                }
            }
        }
        return defaultValue;
    }
    private static Image getSignalImage(Object src) {
        if (src instanceof Image) return (Image) src;
        if (src instanceof Mob) return ((Mob) src).sprite();
        if (src instanceof Hero) return new HeroSprite(((Hero) src).heroClass, 1);
        if (src instanceof ColorItem) return Icons.Notice((ColorItem) src);
        if (src instanceof Item) return new ItemSprite((Item) src);
        if (src instanceof Talent) return new TalentIcon((Talent) src);
        return new BlacksmithSprite();
    }
    private static String getSignalName(Object src) {
        if (src instanceof Char) return ((Char) src).name();
        if (src instanceof ColorItem) return ((Item) src).trueName();
        if (src instanceof Item) return src.toString();
        if (src instanceof Talent) return ((Talent) src).title();
        return "";
    }
    private static String getSignalInfo(Object src) {
        if (src instanceof Char) return ((Char) src).info();
        if (src instanceof Item) return ((Item) src).info();
        if (src instanceof Talent) {
            String meta = ((Talent) src).desc(HeroClass.PUBLIC_1);
            if (!meta.contains("Ms:")) return ((Talent) src).desc(HeroClass.NONE) + "\n\n蜕变后：\n" + meta;
            else return ((Talent) src).desc(HeroClass.NONE);
        }
        return "";
    }
    private static Image buildDisplayImage(Image image, float size, int color, MovieClip.Animation action, Boolean cycle) {
        Image img;
        if (image instanceof CharSprite)
            img = ((CharSprite) image).newInstance();
        else
            img = new Image(image);

        if (size > 0) img.scale.set(size);
        if (img instanceof CharSprite) {
            if (action != null) ((CharSprite) img).playCycle(action, cycle);
            if (color > 0) ((CharSprite) img).aura(color);
        }
        else
            if (color > 0) img.glowing = new Image.Glow(color);
        return img;
    }

    protected void onClick() {
        if (pageMessages != null) {
            GirlsFrontlinePixelDungeon.scene().add(new ChangesWindowWithPages(
                    pageImage, pageSize, pageAction, pageCycle, pageColor, pageTitle, pageMessages, 0));
        } else {
            GirlsFrontlinePixelDungeon.scene().add(new ChangesWindow(
                    icon, size, action, cycle, color, title, message));
        }
    }
    @Override
    protected void layout() {
        super.layout();
        if (icon != null) {
            icon.x = x + (width - icon.width()) / 2f;
            icon.y = y + (height - icon.height()) / 2f;
            PixelScene.align(icon);
        }
    }

    public static class MobTitle extends Component {
        private static final int GAP = 2;
        private final CharSprite image;
        private final RenderedTextBlock title;

        public MobTitle(CharSprite mob, String title, float size, MovieClip.Animation action, int color, Boolean cycle) {
            this.title = PixelScene.renderTextBlock(title, 9);
            this.title.hardlight(0xFFFF44);
            add(this.title);

            this.image = mob.newInstance();

            if (size > 0) this.image.scale.set(size);
            if (color > 0) this.image.aura(color);
            if (action != null) this.image.playCycle(action, cycle);
            add(this.image);
        }

        @Override
        protected void layout() {
            image.x = 0;
            image.y = Math.max(0, (title.height() - image.height()) / 2);
            float w = width - image.width() - GAP;
            title.maxWidth((int) w);
            title.setPos(x + image.width() + GAP, image.height() > title.height() ? y + (image.height() - title.height()) / 2 : y);
            height = Math.max(image.height(), title.bottom());
        }
    }
}
