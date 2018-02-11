package de.heisluft.patcher;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class IOUtils {

	public static void copyStream(InputStream is, OutputStream os) throws IOException{
		int current;
		while((current = is.read()) != -1) os.write(current);
	}
}
