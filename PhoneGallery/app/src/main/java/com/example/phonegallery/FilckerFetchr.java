package com.example.phonegallery;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class FilckerFetchr {
    public byte[] getUrlBytes(String urlSpec) throws IOException {
        // 创建一个url链接
        URL url = new URL(urlSpec);
        // 创建一个http url connection 虽然HttpURLConnection方法提供了一个链接 但是只有调用getInputStream的时候才
        // 如果是post方法 则是getoutputStream方法，它才真正链接到指定的url地址 才会给你反馈代码
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            // 创建一个输出字节流
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            // 输入流是链接的输入
            InputStream in = connection.getInputStream();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() + ": with" + urlSpec);
            }

            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            // 创建了url并打开网络链接后 便可循环调用read()方法读取网络数据
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }

            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

    public String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }
}
