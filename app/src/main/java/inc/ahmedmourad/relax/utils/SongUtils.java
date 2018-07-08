package inc.ahmedmourad.relax.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.File;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public final class SongUtils {

	@Nullable
	public static Bitmap getCoverBitmap(final Context context, final String path) {

		MediaMetadataRetriever retriever = new MediaMetadataRetriever();

		retriever.setDataSource(context.getApplicationContext(), Uri.fromFile(new File(path)));
		byte[] rawArt = retriever.getEmbeddedPicture();

		if (rawArt == null)
			return null;

		return BitmapFactory.decodeByteArray(rawArt, 0, rawArt.length, new BitmapFactory.Options());
	}

	@NonNull
	public static String formatDuration(final long durationInMs) {
		return String.format(Locale.getDefault(),
				"%02d:%02d",
				TimeUnit.MILLISECONDS.toMinutes(durationInMs),
				TimeUnit.MILLISECONDS.toSeconds(durationInMs) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(durationInMs))
		);
	}
}
