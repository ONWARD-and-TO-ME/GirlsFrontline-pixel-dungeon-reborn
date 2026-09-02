package com.shatteredpixel.shatteredpixeldungeon.android.safeinput;

import android.content.Context;
import android.inputmethodservice.InputMethodService;
import android.text.InputType;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.backends.android.surfaceview.GLSurfaceView20;
import com.badlogic.gdx.backends.android.surfaceview.ResolutionStrategy;

/**
 * SafeGLSurfaceView20 —— 重写 onCreateInputConnection，
 * 去掉 TYPE_TEXT_VARIATION_PASSWORD / TYPE_TEXT_VARIATION_VISIBLE_PASSWORD 等密码变体 TAG，
 * 避免 Android 11+ 系统自动弹出"安全键盘"（Private Space Keyboard / 银行级安全键盘）。
 */
public class SafeGLSurfaceView20 extends GLSurfaceView20 {

    public SafeGLSurfaceView20(Context context, ResolutionStrategy resolutionStrategy) {
        super(context, resolutionStrategy);
    }

    public SafeGLSurfaceView20(Context context, ResolutionStrategy resolutionStrategy, int glVersion) {
        super(context, resolutionStrategy, glVersion);
    }

    public SafeGLSurfaceView20(Context context, boolean translucent, int depth, int stencil,
                                ResolutionStrategy resolutionStrategy) {
        super(context, translucent, depth, stencil, resolutionStrategy);
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        if (outAttrs != null) {
            // 保持 NO_EXTRACT_UI（不显示输入法提取 UI）
            outAttrs.imeOptions = outAttrs.imeOptions | EditorInfo.IME_FLAG_NO_EXTRACT_UI;
        }

        // 关键：先让父类按原始逻辑设置 inputType 和创建 InputConnection
        InputConnection ic = super.onCreateInputConnection(outAttrs);

        // 然后强制覆盖 inputType，去掉所有密码/敏感 TAG
        if (outAttrs != null) {
            // 只保留 TYPE_CLASS_TEXT，去掉所有 VARIATION_PASSWORD / VISIBLE_PASSWORD / SUGGESTIONS 等敏感标记
            // 这样 Android 输入法（包括第三方）就不会把它识别为密码输入框
            int safeInputType = InputType.TYPE_CLASS_TEXT;
            // 保留 NO_SUGGESTIONS 避免输入法的自动补全栏（可选，用户可以改成需要的）
            safeInputType |= InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS;
            // 保留多行支持（ED 项目里文本输入可能需要）
            safeInputType |= InputType.TYPE_TEXT_FLAG_MULTI_LINE;
            outAttrs.inputType = safeInputType;
        }

        return ic;
    }
}
