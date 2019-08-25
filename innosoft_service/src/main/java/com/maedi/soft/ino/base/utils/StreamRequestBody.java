package com.maedi.soft.ino.base.utils;

import android.support.annotation.NonNull;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

public class StreamRequestBody extends RequestBody {

    private final RequestBody delegate;
    private final CommStreamRequestBody listener;

    protected StreamRequestBody(RequestBody delegate, CommStreamRequestBody listener) {
        this.delegate = delegate;
        this.listener = listener;
    }

    @Override
    public MediaType contentType() {
        return delegate.contentType();
    }

    @Override
    public long contentLength() {
        try {
            return delegate.contentLength();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public void writeTo(@NonNull BufferedSink sink) throws IOException {
        StreamSink streamSink = new StreamSink(sink);
        BufferedSink bufferedSink = Okio.buffer(streamSink);

        delegate.writeTo(bufferedSink);

        bufferedSink.flush();
    }

    final protected class StreamSink extends ForwardingSink {

        private long bytesWritten = 0;

        StreamSink(Sink delegate) {
            super(delegate);
        }

        @Override
        public void write(@NonNull Buffer source, long byteCount) throws IOException {
            super.write(source, byteCount);
            bytesWritten += byteCount;
            listener.onRequestProgress(bytesWritten, contentLength());
        }

    }

    protected interface CommStreamRequestBody {
        void onRequestProgress(long bytesWritten, long contentLength);
    }

}