package tg2ly;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.herac.tuxguitar.io.base.TGFileFormatException;
import org.herac.tuxguitar.io.base.TGSongLoader;
import org.herac.tuxguitar.io.lilypond.LilypondSettings;
import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.models.TGSong;

public class LyExport {
	TGSongLoader loader = new TGSongLoader();
	TGFactory factory = new TGFactory();
	LySongExport exporter;
	String chapter = null;
	String inpath = "";
	String outpath = "";

	public void export(String inpath, String outpath, boolean force_marker) throws FileNotFoundException {
		this.inpath = inpath;
		String hash = getSongHash(Paths.get(inpath).getFileName().toString());
		this.outpath = outpath + "/" + hash + ".ly";
		
		long file_in  = (new File(this.inpath)).lastModified();
		long file_out = (new File(this.outpath)).lastModified();

		/* Only convert file if new */
		if ((force_marker == false) && (file_in != 0) && (file_out != 0) && (file_in < file_out))
		{
			System.out.println(hash);
		}
		else
		{
			try {
				FileInputStream in_stream = new FileInputStream(this.inpath);
				TGSong song = loader.load(factory, in_stream);
				outputSong(song);
				System.out.println(hash);
			}
			catch (FileNotFoundException | TGFileFormatException e)
			{
				throw new FileNotFoundException();
			}
		}
	}
	
	private String getSongHash(String str)
	{
		try {
			byte[] hash = MessageDigest.getInstance("MD5").digest(str.getBytes());
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < hash.length; ++i) {
				sb.append(Integer.toHexString((hash[i] & 0xFF) | 0x100).substring(1, 3));
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}

	private void outputSong(TGSong song) {
		try {
			File songFile = new File(this.outpath);
			if (!songFile.exists()) {
				songFile.createNewFile();
			}

			FileOutputStream songFileStream = new FileOutputStream(songFile);
			PrintWriter songWriter = new PrintWriter(songFileStream);
			
			exporter = new LySongExport(songFileStream, LilypondSettings.getDefaults());
			
			songWriter.println("\\version \"2.18.2\"\n");
			
			BufferedReader br = new BufferedReader(new InputStreamReader(LyExport.class.getResourceAsStream("/data/preembel.ly")));
			String line = null;
			while ((line = br.readLine()) != null) {
				songWriter.println(line);
			}
			br.close();
			
			songWriter.println("\\score {");
			songWriter.println("  <<");
			songWriter.println("    \\new TabStaff {");
			songWriter.println("      <<");
			songWriter.println(exporter.getLilypondTuning(song.getTrack(0)));
			songWriter.println("        \\tabFullNotation");
			songWriter.flush();
			exporter.writeSong(song);
			songWriter.println("      >>");
			songWriter.println("    }");
			songWriter.println("  >>");
			songWriter.println("}");

			songWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}



