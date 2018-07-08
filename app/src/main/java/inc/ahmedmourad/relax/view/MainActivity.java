package inc.ahmedmourad.relax.view;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import java.util.Set;
import java.util.TreeSet;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import inc.ahmedmourad.relax.R;
import inc.ahmedmourad.relax.adapter.SongsRecyclerAdapter;
import inc.ahmedmourad.relax.pojo.Song;
import inc.ahmedmourad.relax.utils.UiUtils;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

	private static final String STATE_RECYCLER_POSITION = "main_recycler_position";

	private static final int LOADER_ID_ = 0;

	private static final int PERMISSION_READ_EXTERNAL_STORAGE = 0;

	private static final String[] COLUMNS = {MediaStore.Audio.Media.TITLE,
			MediaStore.Audio.Media.ALBUM,
			MediaStore.Audio.Media.ARTIST,
			MediaStore.Audio.Media.DURATION,
			MediaStore.Audio.Media.DATA
	};

	private static final int ID_TITLE = 0;
	private static final int ID_ALBUM = 1;
	private static final int ID_ARTIST = 2;
	private static final int ID_DURATION = 3;
	private static final int ID_DATA = 4;

	@SuppressWarnings("WeakerAccess")
	@BindView(R.id.main_recycler_view)
	RecyclerView recyclerView;

	@SuppressWarnings("WeakerAccess")
	@BindView(R.id.main_toolbar)
	Toolbar toolbar;

	private final Set<Song> songs = new TreeSet<>();

	private SongsRecyclerAdapter adapter;

	private Unbinder unbinder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		unbinder = ButterKnife.bind(this);

		setSupportActionBar(toolbar);

		UiUtils.enableDrawingBehindStatusBar(this);

		initializeRecyclerView();

		checkPermission();
	}

	private void initializeRecyclerView() {
		adapter = new SongsRecyclerAdapter();
		recyclerView.setAdapter(adapter);
		recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
		recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
		recyclerView.setVerticalScrollBarEnabled(true);
		recyclerView.setHasFixedSize(true);
	}

	private void checkPermission() {

		int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

		if (permissionCheck != PackageManager.PERMISSION_GRANTED)
			ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_READ_EXTERNAL_STORAGE);
		else
			initLoader();
	}

	private void initLoader() {
		getSupportLoaderManager().initLoader(LOADER_ID_, null, this);
	}

	private void addSongs(final Cursor cursor) {

		if (cursor != null && !cursor.isClosed() && cursor.moveToFirst()) {

			Song song;

			do {

				song = new Song();

				song.setName(cursor.getString(ID_TITLE));
				song.setAlbum(cursor.getString(ID_ALBUM));
				song.setArtist(cursor.getString(ID_ARTIST));
				song.setDuration(cursor.getLong(ID_DURATION));
				song.setPath(cursor.getString(ID_DATA));

				songs.add(song);

			} while (cursor.moveToNext());
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);

		if (requestCode == PERMISSION_READ_EXTERNAL_STORAGE)
			if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
				initLoader();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelable(STATE_RECYCLER_POSITION, recyclerView.getLayoutManager().onSaveInstanceState());
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		recyclerView.getLayoutManager().onRestoreInstanceState(savedInstanceState.getParcelable(STATE_RECYCLER_POSITION));
	}

	@Override
	protected void onDestroy() {
		unbinder.unbind();
		super.onDestroy();
	}

	@NonNull
	@Override
	public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
		return new CursorLoader(this,
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				COLUMNS,
				MediaStore.Audio.Media.IS_MUSIC + "!= ? AND " + MediaStore.Audio.Media.DURATION + " > ?",
				new String[]{"0", "10000"},
				MediaStore.Audio.Media.TITLE + " ASC"
		);
	}

	@Override
	public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
		addSongs(cursor);
		adapter.updateSongs(songs);
	}

	@Override
	public void onLoaderReset(@NonNull Loader<Cursor> loader) {

	}
}
