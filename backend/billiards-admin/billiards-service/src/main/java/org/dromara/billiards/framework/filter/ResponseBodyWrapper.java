package org.dromara.billiards.framework.filter;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

/**
 * 自定义响应包装器
 *
 * 用于捕获响应体内容，在过滤器中记录响应内容
 */
public class ResponseBodyWrapper extends HttpServletResponseWrapper {

    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private PrintWriter writer;

    public ResponseBodyWrapper(HttpServletResponse response) {
        super(response);
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return new ServletOutputStreamWrapper(this.outputStream, getResponse().getOutputStream());
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        if (writer == null) {
            writer = new PrintWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
        }
        return writer;
    }

    /**
     * 获取响应体内容
     */
    public byte[] getContentAsBytes() {
        if (writer != null) {
            writer.flush();
        }
        return outputStream.toByteArray();
    }

    /**
     * 获取响应体内容为字符串
     */
    public String getContentAsString() {
        if (writer != null) {
            writer.flush();
        }
        return new String(outputStream.toByteArray(), StandardCharsets.UTF_8);
    }

    /**
     * 内部类：自定义ServletOutputStream
     */
    private static class ServletOutputStreamWrapper extends ServletOutputStream {

        private final ByteArrayOutputStream outputStream;
        private final ServletOutputStream originalOutputStream;

        public ServletOutputStreamWrapper(ByteArrayOutputStream outputStream, ServletOutputStream originalOutputStream) {
            this.outputStream = outputStream;
            this.originalOutputStream = originalOutputStream;
        }

        @Override
        public void write(int b) throws IOException {
            // 同时写入到缓存和原始输出流
            outputStream.write(b);
            originalOutputStream.write(b);
        }

        @Override
        public void write(byte[] b) throws IOException {
            outputStream.write(b);
            originalOutputStream.write(b);
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            outputStream.write(b, off, len);
            originalOutputStream.write(b, off, len);
        }

        @Override
        public boolean isReady() {
            return originalOutputStream.isReady();
        }

        @Override
        public void setWriteListener(WriteListener writeListener) {
            originalOutputStream.setWriteListener(writeListener);
        }
    }
}
