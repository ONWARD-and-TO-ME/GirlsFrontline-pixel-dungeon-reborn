/*
 * Gradient BitmapText: renders each character with a color interpolated
 * between startColor (left) and endColor (right), producing a horizontal
 * gradient effect. Used for special-date text (e.g. cake day gold->white).
 */

package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.watabou.glwrap.Vertexbuffer;
import com.watabou.noosa.BitmapText;
import com.watabou.noosa.NoosaScript;
import com.watabou.utils.ColorMath;

import java.nio.Buffer;

public class GradientBitmapText extends BitmapText {

	protected int startColor = 0xFFFFFF;
	protected int endColor = 0xFFFFFF;

	public GradientBitmapText() {
		super();
	}

	public GradientBitmapText( Font font ) {
		super( font );
	}

	public GradientBitmapText( String text, Font font ) {
		super( text, font );
	}

	public void setGradientColors( int start, int end ) {
		this.startColor = start;
		this.endColor = end;
	}

	@Override
	public void draw() {

		// Visual.draw() updates the transform matrix, but BitmapText.draw() also
		// performs the single-color draw which we want to replace. Recompute the
		// matrix directly (cheap for a small label).
		updateMatrix();

		if (dirty) {
			updateVertices();
			((Buffer) quads).limit( quads.position() );
			if (buffer == null)
				buffer = new Vertexbuffer( quads );
			else
				buffer.updateVertices( quads );
		}

		if (realLength <= 0) {
			return;
		}

		NoosaScript script = NoosaScript.get();

		font.texture.bind();

		script.camera( camera() );
		script.uModel.valueM4( matrix );

		int n = realLength;
		for (int i = 0; i < n; i++) {
			float t = n <= 1 ? 0f : (float) i / (n - 1);
			int color = ColorMath.interpolate( startColor, endColor, t );

			float r = ((color >> 16) & 0xFF) / 255f;
			float g = ((color >> 8) & 0xFF) / 255f;
			float b = (color & 0xFF) / 255f;

			// hardlight mode: ra/ga/ba = 0, rm/gm/bm = color
			script.lighting( r, g, b, am, 0, 0, 0, aa );
			script.drawQuadSet( buffer, 1, i );
		}
	}
}
/**节日          起始色（左）             结束色（右）       配色寓意 
 * 圣诞节 (XMAS) `0xFF3B3B` 红          `0x2ECC71`        绿经典圣诞红绿 
 * 万圣节 (HWEEN) `0xFF8800` 南瓜橙      `0x8E44AD` 幽紫   南瓜与鬼魅 
 * 面包节 (BREAD) `0xE0A040` 肉桂金      `0x8B5A2B` 焦棕   烘烤色泽 
 * 中秋节 (midAutumnFestival) `0x9EC9FF` 月光蓝  `0xFFFFFF` 银白   月色清辉
 * 春节 (SPRING_FESTIVAL) `0xE60012` 中国红      `0xFFD700` 鎏金    喜庆年味
 * 元宵节 (LANTERN_FESTIVAL) `0xFF4500` 灯红     `0xFFD27F` 暖黄    花灯流光
 * 清明节 (QINGMING) `0x7FBF7F` 柳绿            `0xEAF2EA` 雨雾白  踏青烟雨
 * 端午节 (DRAGON_BOAT) `0x2E8B57` 粽叶绿       `0x9DC183` 艾草青  粽艾清香
 * 七夕 (QIXI) `0xB57EDC` 鹊桥紫                `0xFFB6C1` 织女粉  星河相会
 * 重阳节 (DOUBLE_NINTH) `0xFFB90F` 菊黄        `0xD2691E` 枫橙    登高赏菊
 * 国庆节 (NATIONAL_DAY) `0xDE2910` 国旗红      `0xFFDE00` 五星金  国旗配色
 * 复活节 (EASTER) `0xFF99CC` 粉彩       `0x98FB98` 嫩绿   春日彩蛋 
 * 普通日期 `0xCACFC2` 浅灰 `0xA8AEA0` 深灰 微渐变不突兀 
 * 蛋糕节日 `0xFFD700` 金色 `0xFFFFFF` 白色 （保持原有金白渐变）
 * - 新增`applyDateColor()` ： 蛋糕日优先 （`SPDSettings.isSpecialDay()` 时使用金白渐变），否则按节日上色，再否则默认灰
- 新增`festivalColor()` ：基于`Holidays.holiday` 枚举映射颜色，并额外用`Dungeon.isXMAS()` 覆盖 12 月 17 日起的圣诞窗口
- 在`createChildren()` 创建时和`update()` 每秒刷新时都调用`applyDateColor()` ，确保跨午夜日期/节日切换时颜色及时更新 */
