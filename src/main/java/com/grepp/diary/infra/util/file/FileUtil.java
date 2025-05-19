package com.grepp.diary.infra.util.file;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class FileUtil {
    // 파일명만 인코딩하는 함수
    public static String encodeFilenameInPath(String path) {
        if (path == null || path.isEmpty()) return path;
        int idx = path.lastIndexOf("/");
        String dir = idx >= 0 ? path.substring(0, idx + 1) : "";
        String filename = idx >= 0 ? path.substring(idx + 1) : path;
        String encodedFilename = URLEncoder
            .encode(filename, StandardCharsets.UTF_8).replace("+", "%20");
        return dir + encodedFilename;
    }
}
