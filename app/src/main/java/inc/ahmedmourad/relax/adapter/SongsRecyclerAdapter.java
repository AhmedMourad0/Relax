package inc.ahmedmourad.relax.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import inc.ahmedmourad.relax.R;
import inc.ahmedmourad.relax.pojo.Song;
import inc.ahmedmourad.relax.utils.SongUtils;
import inc.ahmedmourad.relax.view.PlayerActivity;

public class SongsRecyclerAdapter extends RecyclerView.Adapter<SongsRecyclerAdapter.ViewHolder> {

	private final List<Song> songsList = new ArrayList<>();

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull final ViewGroup container, final int viewType) {
		return new ViewHolder(LayoutInflater.from(container.getContext()).inflate(R.layout.item_song, container, false));
	}

	@Override
	public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
		holder.bind(position, getItemAtPosition(position));
	}

	@Override
	public int getItemCount() {
		return songsList.size();
	}

	@NonNull
	private Song getItemAtPosition(final int position) {
		return songsList.get(position);
	}

	public void updateSongs(final Set<Song> songs) {
		songsList.clear();
		songsList.addAll(songs);
		notifyDataSetChanged();
	}

	class ViewHolder extends RecyclerView.ViewHolder {

		@BindView(R.id.item_song_name)
		TextView nameTextView;

		@BindView(R.id.item_song_artist)
		TextView artistTextView;

		@BindView(R.id.item_song_duration)
		TextView durationTextView;

		@BindView(R.id.item_song_cover)
		CircleImageView coverImageView;

		ViewHolder(final View view) {
			super(view);
			ButterKnife.bind(this, view);
		}

		private void bind(final int position, final Song song) {

			final Context context = itemView.getContext();

			nameTextView.setText(song.getName());
			artistTextView.setText(song.getArtist());

			durationTextView.setText(SongUtils.formatDuration(song.getDuration()));

			displaySongCover(context, song.getPath());

			itemView.setOnClickListener(v -> {

				final ArrayList<Parcelable> list = new ArrayList<>(getItemCount());

				for (int i = 0; i < getItemCount(); ++i)
					list.add(Parcels.wrap(getItemAtPosition(i)));

				final Intent intent = new Intent(context, PlayerActivity.class);
				intent.putParcelableArrayListExtra(PlayerActivity.EXTRA_SONGS, list);
				intent.putExtra(PlayerActivity.EXTRA_SONG_POSITION, position);
				context.startActivity(intent);
			});
		}

		private void displaySongCover(final Context context, final String path) {

			final Bitmap bitmap = SongUtils.getCoverBitmap(context, path);

			if (bitmap != null)
				coverImageView.setImageBitmap(bitmap);
			else
				coverImageView.setImageResource(R.drawable.placeholder);
		}
	}
}
