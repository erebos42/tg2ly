package tg2ly;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.herac.tuxguitar.io.base.TGFileFormatException;
import org.herac.tuxguitar.io.base.TGSongLoader;
import org.herac.tuxguitar.io.lilypond.LilypondSettings;
import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.song.models.TGString;

public class LyBookExport {
	TGSongLoader loader = new TGSongLoader();
	TGFactory factory = new TGFactory();
	LySongExport exporter;
	String chapter = null;
	String inpath = "";
	String outpath = "";
	PrintStream tabbookWriter = null;

	public void exportBook(String inpath, String outpath) throws IOException {
		this.inpath = inpath;
		this.outpath = outpath;

		new File(this.outpath).mkdirs();
		File tabbookFile = new File(this.outpath + "/tabbook.ly");
		if (!tabbookFile.exists()) {
			tabbookFile.createNewFile();
		}
		this.tabbookWriter = new PrintStream(new FileOutputStream(tabbookFile));

		ArrayList<TGSong> songList = loadSongs(inpath);
		Collections.sort(songList, new SongNameComparator());
		Collections.sort(songList, new SongGenreComparator());

		outputPreembel();
		outputBookHeader();
		this.chapter = null;
		for (TGSong song : songList) {
			outputChapter(song);
			outputSong(song);
		}
		outputBookFooter();
	}

	private ArrayList<TGSong> loadSongs(String dirpath) {
		File root = new File(dirpath);
		if (!root.isDirectory()) {
			throw new IllegalArgumentException(
					"'dirpath' has to be a directory!");
		}

		ArrayList<TGSong> ret = new ArrayList<>();
		File[] files = root.listFiles();
		for (File file : files) {
			int i = file.getName().lastIndexOf('.');
			if (file.isFile()
					&& (i >= 0 && file.getName().substring(i).equals(".tg"))) {
				TGSong song = loadSong(file);
				if (song != null) {
					ret.add(song);
				}
			}
		}

		return ret;
	}

	class SongNameComparator implements Comparator<TGSong> {
		@Override
		public int compare(TGSong arg0, TGSong arg1) {
			return arg0.getName().compareTo(arg1.getName());
		}
	}

	class SongGenreComparator implements Comparator<TGSong> {
		@Override
		public int compare(TGSong arg0, TGSong arg1) {
			return arg0.getAlbum().compareTo(arg1.getAlbum());
		}
	}

	private TGSong loadSong(File file) {
		TGSong song = null;
		try {
			song = loader.load(factory, new FileInputStream(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (TGFileFormatException e) {
			e.printStackTrace();
		}
		return song;
	}

	private void outputPreembel() {
		tabbookWriter.println("\\version \"2.18.2\"");

		try {
			BufferedReader br = new BufferedReader(
					new InputStreamReader(
							LyBookExport.class
									.getResourceAsStream("/data/preembel.ly")));
			String line = null;
			while ((line = br.readLine()) != null) {
				tabbookWriter.println(line);
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void outputBookHeader() {
		tabbookWriter.println();
		tabbookWriter.println("\\book {");
		tabbookWriter.println("  \\paper {");
		tabbookWriter.println("    #(set-paper-size \"a4\")");
		tabbookWriter
				.println("    tocTitleMarkup = \\markup \\huge \\column {");
		tabbookWriter
				.println("      \\fill-line { \\null \"Inhaltsverzeichnis\" \\null }");
		tabbookWriter.println("      \\hspace #1");
		tabbookWriter.println("    }");
		tabbookWriter
				.println("    tocItemMarkup = \\markup \\large \\fill-line {");
		tabbookWriter
				.println("      \\fromproperty #'toc:text \\fromproperty #'toc:page");
		tabbookWriter.println("    }");
		tabbookWriter
				.println("    tocCollMarkup = \\markup { \\fill-line { \\bold \\fromproperty #'toc:text \\vspace #1 \\null } }");
		tabbookWriter
				.println("    tocPartMarkup = \\markup { \\fill-line { \\concat { \\hspace #2 \\fromproperty #'toc:text } \\vspace #0 \\fromproperty #'toc:page } }");
		tabbookWriter.println("    print-all-headers = ##t");
		tabbookWriter.println("    score-markup-spacing = #'((padding . 8))");
		tabbookWriter.println("    score-system-spacing = #'((padding . 8))");
		tabbookWriter.println("  }");
		tabbookWriter.println("  \\header {");
		tabbookWriter.println("    title       = \"Tabbook\"");
		tabbookWriter.println("    dedication  = \"\"");
		tabbookWriter.println("    subtitle    = \"\"");
		tabbookWriter.println("    subsubtitle = \"\"");
		tabbookWriter.println("    poet        = \"\"");
		tabbookWriter.println("    instrument  = \"\"");
		tabbookWriter.println("    composer    = \"\"");
		tabbookWriter.println("    meter       = \"\"");
		tabbookWriter.println("    arranger    = \"\"");
		tabbookWriter.println("    piece       = \"\"");
		tabbookWriter.println("    opus        = \"\"");
		tabbookWriter.println("    copyright   = \"\"");
		tabbookWriter.println("    tagline     = ##f");
		tabbookWriter.println("  }");
		tabbookWriter.println("  \\markuplist \\table-of-contents");
		tabbookWriter.println("  \\pageBreak");
	}

	private void outputBookFooter() {
		tabbookWriter.println("}");
	}

	private void outputChapter(TGSong song) {
		if (!song.getAlbum().equals(this.chapter)) {
			tabbookWriter.println("  #(set-toc-section! \"" + song.getAlbum()
					+ "\")");
			this.chapter = song.getAlbum();
		}
	}

	private String getRoman(int number) {

		String riman[] = { "M", "XM", "CM", "D", "XD", "CD", "C", "XC", "L",
				"XL", "X", "IX", "V", "IV", "I" };
		int arab[] = { 1000, 990, 900, 500, 490, 400, 100, 90, 50, 40, 10, 9,
				5, 4, 1 };
		StringBuilder result = new StringBuilder();
		int i = 0;
		while (number > 0 || arab.length == (i - 1)) {
			while ((number - arab[i]) >= 0) {
				number -= arab[i];
				result.append(riman[i]);
			}
			i++;
		}
		return result.toString();
	}

	private String getTuningString(TGSong song) {
		final String tuningLow[] = { "B", "A#", "A", "G#", "G", "F#", "F", "E",
				"D#", "D", "C#", "C" };
		final String tuningHigh[] = { "c", "c#", "d", "d#", "e", "f", "f#",
				"g", "g#", "a", "a#", "b" };
		int tuningSplit = 48;

		StringBuilder str = new StringBuilder();
		str.append("Tuning: ");

		@SuppressWarnings("unchecked")
		List<TGString> stringList = song.getTrack(0).getStrings();
		Collections.reverse(stringList);

		for (TGString tgString : stringList) {
			int stringValue = tgString.getValue();

			if (stringValue < tuningSplit) {
				str.append(tuningLow[(tuningSplit - stringValue - 1)
						% tuningHigh.length]);
				for (int i = 0; i < ((tuningSplit - stringValue - 1) / tuningHigh.length); i++) {
					str.append(',');
				}
			} else {
				str.append(tuningHigh[(stringValue - tuningSplit)
						% tuningLow.length]);
				for (int i = 0; i < ((stringValue - tuningSplit) / tuningLow.length); i++) {
					str.append('\'');
				}
			}
		}

		int offset = song.getTrack(0).getOffset();
		if (offset != 0) {
			str.append(" - Capo: ");
			str.append(getRoman(offset));
		}

		return str.toString();
	}

	private void outputSong(TGSong song) {
		try {
			new File(this.outpath + "/songs/").mkdirs();
			File songFile = new File(this.outpath + "/songs/"
					+ String.valueOf(song.hashCode()) + ".ly");
			if (!songFile.exists()) {
				songFile.createNewFile();
			}

			FileOutputStream songFileStream = new FileOutputStream(songFile);
			exporter = new LySongExport(songFileStream,
					LilypondSettings.getDefaults());
			PrintWriter songWriter = new PrintWriter(songFileStream);

			songWriter.println("\\piece \\markup {\"" + song.getName() + "\"}");
			songWriter.println("\\score {");
			songWriter.println("  \\header {");
			songWriter.println("    title      = \"" + song.getName() + "\"");
			songWriter.println("    dedication = \"" + song.getAlbum() + "\"");
			songWriter.println("    subtitle   = \"" + song.getArtist() + "\"");
			songWriter.println("    poet       = \"" + getTuningString(song)
					+ "\"");
			songWriter.println("    composer   = \"" + song.getAuthor() + "\"");
			songWriter.println("  }");
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

			tabbookWriter.println("  \\include \"songs/"
					+ String.valueOf(song.hashCode()) + ".ly\"");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
