package inc.ahmedmourad.relax.pojo;

import android.support.annotation.NonNull;

import org.parceler.Parcel;

@Parcel(Parcel.Serialization.BEAN)
public class Song implements Comparable {

	private String name = "Unknown song";
	private String album = "Unknown album";
	private String artist = "Unknown artist";
	private String path = "";

	private long duration = 0L;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAlbum() {
		return album;
	}

	public void setAlbum(String album) {
		this.album = album;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {

		if (artist.equals("<unknown>"))
			artist = "Unknown artist";

		this.artist = artist;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}


	@Override
	public int compareTo(@NonNull Object o) {
		return getName().compareToIgnoreCase(((Song) o).getName());
	}
}
