package com.androidhive.musicplayer;

import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;

import com.echonest.api.v4.EchoNestAPI;
import com.echonest.api.v4.EchoNestException;
import com.echonest.api.v4.Song;
import com.echonest.api.v4.SongParams;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class SongsManager {
	// SDCard Path
	final String MEDIA_PATH = new String("/storage/sdcard0/Music/");

    //Map: Title - Path
	private ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();

    //Map: Title - author
    private ArrayList<HashMap<String, String>> songsEchonestList = new ArrayList<HashMap<String, String>>();

    //echonest api key
    final String echo_api_key = "JLXWJ3KITSBMFCJ2G";
	
	// Constructor
	public SongsManager(){
		
	}
	
	/**
	 * Function to read all mp3 files from sdcard
	 * and store the details in ArrayList
	 * */
	public ArrayList<HashMap<String, String>> getPlayList(){
		File home = new File(MEDIA_PATH);

		if (home.listFiles(new FileExtensionFilter()).length > 0) {
			for (File file : home.listFiles(new FileExtensionFilter())) {
				HashMap<String, String> song = new HashMap<String, String>();
				song.put("songTitle", file.getName().substring(0, (file.getName().length() - 4)));
				song.put("songPath", file.getPath());

				// Adding each song to SongList
				songsList.add(song);
			}
		}
		// return songs list array
		return songsList;
	}



    /**
     * Function to obtain an echonest playlist
     * */
    public ArrayList<HashMap<String, String>> getEchonestPlaylist() throws EchoNestException, ExecutionException, InterruptedException {
        //String s = ""+songsEchonestList.size();
        //Log.v("INITIAL size: ", s);
        String lastTitle = "";

        RetrieveEchonestPlaylist echoTask = new RetrieveEchonestPlaylist();
        echoTask.execute("INIZIO ECHONEST: Ricerca...");
        List<Song> songs = echoTask.get();

        for (Song song : songs) {
            if (!lastTitle.toLowerCase().equals(song.getTitle().toLowerCase())) {
                HashMap<String, String> songPl = new HashMap<String, String>();
                songPl.put("songTitle", song.getTitle().substring(0, (song.getTitle().length() - 4)));
                songPl.put("songArtist", song.getArtistName());

                // Adding each song to SongList
                songsEchonestList.add(songPl);
            }
            lastTitle = song.getTitle();
        }
        return songsEchonestList;
    }

/*

    public void searchSongsByLocation(String artist, int results)  throws EchoNestException {

        //LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        SongParams p = new SongParams();

        p.setMaxLongitude();
        p.setMinLongitude();
        p.setMaxLatitude();
        p.setMinLatitude();
        p.includeTracks();

        p.sortBy("song_hotttnesss", false);


        List<Song> songs = en.searchSongs(p);
        for (Song song : songs) {
            dumpSong(song);
            System.out.println();
        }
    }

    public void searchSongsByTitle(String title, int results)
            throws EchoNestException {
        Params p = new Params();
        p.add("title", title);
        p.add("results", results);
        List<Song> songs = en.searchSongs(p);
        for (Song song : songs) {
            dumpSong(song);
            System.out.println();
        }
    }

    public void dumpSong(Song song) throws EchoNestException {
        System.out.printf("%s\n", song.getTitle());
        System.out.printf("   artist: %s\n", song.getArtistName());
        System.out.printf("   dur   : %.3f\n", song.getDuration());
        System.out.printf("   BPM   : %.3f\n", song.getTempo());
        System.out.printf("   Mode  : %d\n", song.getMode());
        System.out.printf("   S hot : %.3f\n", song.getSongHotttnesss());
        System.out.printf("   A hot : %.3f\n", song.getArtistHotttnesss());
        System.out.printf("   A fam : %.3f\n", song.getArtistFamiliarity());
        System.out.printf("   A loc : %s\n", song.getArtistLocation());
    }


*/




	/**
	 * Class to filter files which are having .mp3 extension
	 * */
	class FileExtensionFilter implements FilenameFilter {
		public boolean accept(File dir, String name) {
			return (name.endsWith(".mp3") || name.endsWith(".MP3"));
		}
	}

    /*
     * Class to perform a networking operation with echonest
     */

    class RetrieveEchonestPlaylist extends AsyncTask<String, Void, List<Song>> {

        private Exception exception;

        public RetrieveEchonestPlaylist() {

        }

        protected List<Song> doInBackground(String... urls) {
            try {
                Log.v("  ", urls[0]);

                EchoNestAPI en = new EchoNestAPI(echo_api_key);
                en.setTraceSends(true);
                en.setTraceRecvs(false);

                SongParams p = new SongParams();
                p.setResults(5);
                p.sortBy("song_hotttnesss", false);
                List<Song> songs = en.searchSongs(p);


                String lastTitle = "";
                for (Song song : songs) {
                    if (!lastTitle.toLowerCase().equals(song.getTitle().toLowerCase())) {
                        Log.v(song.getTitle() + " by ", song.getArtistName());

                    }
                    lastTitle = song.getTitle();
                }
                return songs;
            } catch(Exception ee){
            Log.v("RetrieveEchonestPlaylist exception: ", ee.toString());
            }
            return null;
        }

        protected void onPostExecute(List<Song> songs) {
            // TODO: check this.exception
            // TODO: do something with the feed
            String songslength=""+songs.size();
            Log.v("ONPOSTEXECUTE: echonest playlist length --> ", songslength);
        }
    }
}
