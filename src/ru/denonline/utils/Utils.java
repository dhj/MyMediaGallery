package ru.denonline.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Utils {
	public static int IO_BUFFER_SIZE = 1024;
	/**
     * Copy the content of the input stream into the output stream, using a
     * temporary byte array buffer whose size is defined by
     * {@link #IO_BUFFER_SIZE}.
     * 
     * @param in
     *            The input stream to copy from.
     * @param out
     *            The output stream to copy to.
     * 
     * @throws java.io.IOException
     *             If any error occurs during the copy.
     */
    public static void copy(InputStream in, OutputStream out)
                    throws IOException {
            byte[] b = new byte[IO_BUFFER_SIZE];
            int read;
            while ((read = in.read(b)) != -1) {
                    out.write(b, 0, read);
            }
    }

}
