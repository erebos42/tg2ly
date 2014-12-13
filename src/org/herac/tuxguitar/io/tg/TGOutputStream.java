/*
 * Created on 16-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.io.tg;

import java.io.OutputStream;

import org.herac.tuxguitar.io.base.TGFileFormat;
import org.herac.tuxguitar.io.base.TGFileFormatException;
import org.herac.tuxguitar.io.base.TGOutputStreamBase;
import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.models.TGSong;

public class TGOutputStream extends TGStream implements TGOutputStreamBase {

	@Override
	public void init(TGFactory factory, OutputStream stream) {
		System.out.println("Not Implemented!");
	}

	@Override
	public boolean isSupportedExtension(String extension) {
		System.out.println("Not Implemented!");
		return false;
	}

	@Override
	public TGFileFormat getFileFormat() {
		System.out.println("Not Implemented!");
		return null;
	}

	@Override
	public void writeSong(TGSong song) throws TGFileFormatException {
		System.out.println("Not Implemented!");
	}

}
