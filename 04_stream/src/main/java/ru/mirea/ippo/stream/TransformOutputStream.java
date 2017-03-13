package ru.mirea.ippo.stream;

import java.io.IOException;
import java.io.OutputStream;

final class TransformOutputStream extends OutputStream {
    private final Transform transform;
    private final OutputStream out;

    TransformOutputStream(Transform transform, OutputStream stream) {
        this.transform = transform;
        this.out = stream;
    }

    @Override
    public void write(int b) throws IOException {
        out.write(transform.transform(b));
    }
}
