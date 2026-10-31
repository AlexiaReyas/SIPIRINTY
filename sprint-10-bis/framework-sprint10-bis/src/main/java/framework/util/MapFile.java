package framework.util;

import java.util.Arrays;

public final class MapFile {
    private final String fileName;
    private final String contentType;
    private final byte[] content;

    public MapFile(String fileName, String contentType, byte[] content) {
        this.fileName = fileName;
        this.contentType = contentType;
        this.content = content == null ? new byte[0] : Arrays.copyOf(content, content.length);
    }

    public String getFileName() {
        return fileName;
    }

    public String getContentType() {
        return contentType;
    }

    public byte[] getContent() {
        return Arrays.copyOf(content, content.length);
    }

    public long getSize() {
        return content.length;
    }
}