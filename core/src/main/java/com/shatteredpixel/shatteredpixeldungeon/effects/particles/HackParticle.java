
package com.shatteredpixel.shatteredpixeldungeon.effects.particles;

import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.particles.PixelParticle;
import com.watabou.utils.Random;

public class HackParticle extends PixelParticle {
	public static final Emitter.Factory FACTORY = new Emitter.Factory() {
		public void emit(Emitter emitter, int index, float x, float y) {
			emitter.recycle(HackParticle.class).reset(x, y);
		}

		public boolean lightMode() {
			return true;
		}
	};

	public HackParticle() {
		lifespan = 1.0f;
		color(Random.element(new Integer[]{13057598, 15888457, 15888457, 3641706, 3747443, 6942940, 10940521, 14718629, 4810170, 3747443, 8858454, 1654338, 4481344, 16777200, 12239830}));
		speed.polar(Random.Float(25F), Random.Float(34.0f, 64.0f));
	}

	public void reset(float x, float y) {
		revive();
		left = lifespan;
		size = 10.0f;
		this.x = x - (speed.x * lifespan);
		this.y = y - (speed.y * lifespan);
		angularSpeed = Random.Float(180.0f);
	}

	public void update() {
		float f;
		float f2;
		super.update();
		float p = left / lifespan;
		if (p < 0.5f) {
			f = p * p;
			f2 = 4.0f;
		} else {
			f = 1.0f - p;
			f2 = 2.0f;
		}
		am = f * f2;
		size(Random.Float((left / lifespan) * 6.0f));
	}
}

