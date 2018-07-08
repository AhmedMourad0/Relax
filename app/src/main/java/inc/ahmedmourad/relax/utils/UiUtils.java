package inc.ahmedmourad.relax.utils;

import android.app.Activity;
import android.view.View;

public final class UiUtils {

	public static void enableDrawingBehindStatusBar(final Activity activity) {
		activity.getWindow()
				.getDecorView()
				.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
	}
}
