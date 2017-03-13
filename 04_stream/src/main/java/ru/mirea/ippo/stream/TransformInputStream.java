package ru.mirea.ippo.stream;

import java.io.IOException;
import java.io.InputStream;

final class TransformInputStream extends InputStream {
    private final Transform transform;
    private final InputStream in;

    TransformInputStream(Transform transform, InputStream stream) {
        this.transform = transform;
        this.in = stream;
    }

    @Override
    public int read() throws IOException {
        int ch = in.read();
        if (ch == -1)
            return -1;
        return transform.transform(ch);
    }

    public int read(byte b[], int off, int len) throws IOException {
        int read = in.read(b, off, len);
        for (int i = 0; i < read; ++i) {
            b[i] = (byte) transform.transform(b[i]);
        }
        return read;
    }
}
