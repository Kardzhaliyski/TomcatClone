package server.utils;

import server.Server;

import java.io.*;
import java.nio.file.Path;
import java.util.zip.GZIPOutputStream;

public class Gzip {
    public static File getCompressedVersion(File oldFile , String contentType) {
        Server server = Server.getInstance();
        boolean rtCompression = server.options.contains(CliOptions.REAL_TIME_COMPRESSION);
        boolean cachedCompression = server.options.contains(CliOptions.CACHED_COMPRESSION);
        if (!(rtCompression || cachedCompression)) {
            return oldFile;
        }

        if (isCompressible(contentType)) {
            return oldFile;
        }

        String fName = oldFile.getName();
        Path cPath = oldFile.toPath().getParent().resolve(fName + ".gz");
        File cFile = cPath.toFile();
        if (cFile.exists()) {
            if (cFile.lastModified() > oldFile.lastModified()) {
//                compressed = true;
                return cFile;
            }
        }

        if (!rtCompression) {
            return oldFile;
        }

        try (GZIPOutputStream out = new GZIPOutputStream(new FileOutputStream(cFile));
             InputStream in = new FileInputStream(oldFile)) {
            byte[] buff = new byte[4096];
            int n;
            while ((n = in.read(buff)) != -1) {
                out.write(buff, 0, n);
            }

            out.finish();
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

//        compressed = true;
        return cFile;
    }

    private static boolean isCompressible(String contentType) {
        return contentType != null &&
                (contentType.startsWith("text") ||
                        contentType.contains("json") ||
                        contentType.contains("svg") ||
                        contentType.contains("xml"));
    }
}
