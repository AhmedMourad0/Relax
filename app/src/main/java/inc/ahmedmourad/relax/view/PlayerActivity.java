package inc.ahmedmourad.relax.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import org.parceler.Parcels;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import inc.ahmedmourad.relax.R;
import inc.ahmedmourad.relax.pojo.Song;
import inc.ahmedmourad.relax.utils.SongUtils;
import inc.ahmedmourad.relax.utils.UiUtils;

public class PlayerActivity extends AppCompatActivity {

	public static final String EXTRA_SONG_POSITION = "player_song_position";
	public static final String EXTRA_SONGS = "player_player_songs";

	private static final String STATE_SONG_POSITION = "player_song_position";
	private static final String STATE_PROGRESS = "player_progress";
	private static final String STATE_IS_PLAYING = "player_is_playing";

	@SuppressWarnings("WeakerAccess")
	@BindView(R.id.player_cover)
	ImageView coverImageView;

	@SuppressWarnings("WeakerAccess")
	@BindView(R.id.player_artist_album)
	TextView artistTextView;

	@SuppressWarnings("WeakerAccess")
	@BindView(R.id.player_song)
	TextView nameTextView;

	@SuppressWarnings("WeakerAccess")
	@BindView(R.id.player_elapsed_time)
	TextView elapsedTimeTextView;

	@SuppressWarnings("WeakerAccess")
	@BindView(R.id.player_duration)
	TextView durationTextView;

	@SuppressWarnings("WeakerAccess")
	@BindView(R.id.player_seek_bar)
	SeekBar seekBar;

	@SuppressWarnings("WeakerAccess")
	@BindView(R.id.player_skip_previous)
	ImageButton previousControl;

	@SuppressWarnings("WeakerAccess")
	@BindView(R.id.player_rewind)
	ImageButton rewindControl;

	@SuppressWarnings("WeakerAccess")
	@BindView(R.id.player_pause)
	ImageButton pauseControl;

	@SuppressWarnings("WeakerAccess")
	@BindView(R.id.player_ffwd)
	ImageButton fastForwardControl;

	@SuppressWarnings("WeakerAccess")
	@BindView(R.id.player_next)
	ImageButton nextControl;

	@SuppressWarnings("WeakerAccess")
	@BindView(R.id.player_up)
	ImageButton upButton;

	private List<Parcelable> songsList;

	private int songPosition;

	private Song song;

	private boolean isPlaying = true;

	private Unbinder unbinder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_player);

		unbinder = ButterKnife.bind(this);

		UiUtils.enableDrawingBehindStatusBar(this);

		final Intent intent = getIntent();

		if (intent != null && intent.getExtras() != null && intent.hasExtra(EXTRA_SONGS) && intent.hasExtra(EXTRA_SONG_POSITION)) {
			songsList = intent.getParcelableArrayListExtra(EXTRA_SONGS);
			songPosition = intent.getIntExtra(EXTRA_SONG_POSITION, 0);
			song = Parcels.unwrap(songsList.get(songPosition));
		}

		if (song == null)
			finish();

		pauseControl.setOnClickListener(v -> {
			pauseControl.setImageResource(isPlaying ? R.drawable.ic_play : R.drawable.ic_pause);
			isPlaying = !isPlaying;
		});

		seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				elapsedTimeTextView.setText(SongUtils.formatDuration(song.getDuration() * progress / 100));
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}
		});

		fastForwardControl.setOnClickListener(v -> seekBar.setProgress(seekBar.getProgress() + 5));
		rewindControl.setOnClickListener(v -> seekBar.setProgress(seekBar.getProgress() - 5));

		nextControl.setOnClickListener(v -> {
			++songPosition;
			song = Parcels.unwrap(songsList.get(songPosition));
			populateUi();
		});

		previousControl.setOnClickListener(v -> {
			--songPosition;
			song = Parcels.unwrap(songsList.get(songPosition));
			populateUi();
		});

		upButton.setOnClickListener(v -> finish());

		if (savedInstanceState != null) {
			songPosition = savedInstanceState.getInt(STATE_SONG_POSITION);
			isPlaying = savedInstanceState.getBoolean(STATE_IS_PLAYING, true);
			song = Parcels.unwrap(songsList.get(songPosition));
			populateUi();
			seekBar.setProgress(savedInstanceState.getInt(STATE_PROGRESS));
		} else {
			populateUi();
		}
	}

	private void populateUi() {

		final boolean isFirstSong = songPosition == 0;
		final boolean isLastSong = songPosition == songsList.size() - 1;

		previousControl.setEnabled(!isFirstSong);
		nextControl.setEnabled(!isLastSong);
		previousControl.setAlpha(isFirstSong ? 0.3f : 1f);
		nextControl.setAlpha(isLastSong ? 0.3f : 1f);

		pauseControl.setImageResource(isPlaying ? R.drawable.ic_pause : R.drawable.ic_play);

		seekBar.setProgress(0);

		artistTextView.setText(getString(R.string.artist_album, song.getArtist(), song.getAlbum()));
		nameTextView.setText(song.getName());
		elapsedTimeTextView.setText(getString(R.string.elapsed_time_zero));
		durationTextView.setText(SongUtils.formatDuration(song.getDuration()));

		displaySongCover(this, song.getPath());
	}

	private void displaySongCover(final Context context, final String path) {

		final Bitmap bitmap = SongUtils.getCoverBitmap(context, path);

		if (bitmap != null)
			coverImageView.setImageBitmap(bitmap);
		else
			coverImageView.setImageResource(R.drawable.placeholder);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(STATE_SONG_POSITION, songPosition);
		outState.putInt(STATE_PROGRESS, seekBar.getProgress());
		outState.putBoolean(STATE_IS_PLAYING, isPlaying);
	}

	@Override
	protected void onDestroy() {
		unbinder.unbind();
		super.onDestroy();
	}
}
