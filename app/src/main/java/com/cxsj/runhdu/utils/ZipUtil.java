package com.cxsj.runhdu.utils;

import android.text.TextUtils;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Created by Sail on 2017/5/6 0006.
 * 用于对字符串压缩的工具
 */

public class ZipUtil {
    /**
     * Gzip 压缩数据
     *
     * @param unGzipStr
     * @return
     */
    public static String compress(String unGzipStr) {

        if (TextUtils.isEmpty(unGzipStr)) {
            return "0";
        }
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            GZIPOutputStream gzip = new GZIPOutputStream(baos);
            gzip.write(unGzipStr.getBytes());
            gzip.close();
            byte[] encode = baos.toByteArray();
            baos.flush();
            baos.close();
            return Base64.encodeToString(encode, Base64.DEFAULT);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "0";
    }

    /**
     * Gzip解压数据
     *
     * @param gzipStr
     * @return
     */
    public static String decompress(String gzipStr) {
        if (TextUtils.isEmpty(gzipStr)) {
            return null;
        }
        byte[] t;
        try {
            t = Base64.decode(gzipStr, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        }
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ByteArrayInputStream in = new ByteArrayInputStream(t);
            GZIPInputStream gzip = new GZIPInputStream(in);
            byte[] buffer = new byte[1024];
            int n;
            while ((n = gzip.read(buffer, 0, buffer.length)) > 0) {
                out.write(buffer, 0, n);
            }
            gzip.close();
            in.close();
            out.close();
            return out.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "0";
        }
    }

    /**
     * Zip 压缩数据
     *
     * @param unZipStr
     * @return
     */
    public static String compressForZip(String unZipStr) {

        if (TextUtils.isEmpty(unZipStr)) {
            return "0";
        }
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ZipOutputStream zip = new ZipOutputStream(baos);
            zip.putNextEntry(new ZipEntry("0"));
            zip.write(unZipStr.getBytes());
            zip.closeEntry();
            zip.close();
            byte[] encode = baos.toByteArray();
            baos.flush();
            baos.close();
            return Base64.encodeToString(encode, Base64.DEFAULT);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "0";
    }

    /**
     * Zip解压数据
     *
     * @param zipStr
     * @return
     */
    public static String decompressForZip(String zipStr) {

        if (TextUtils.isEmpty(zipStr)) {
            return "0";
        }
        byte[] t = Base64.decode(zipStr, Base64.DEFAULT);
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ByteArrayInputStream in = new ByteArrayInputStream(t);
            ZipInputStream zip = new ZipInputStream(in);
            zip.getNextEntry();
            byte[] buffer = new byte[1024];
            int n = 0;
            while ((n = zip.read(buffer, 0, buffer.length)) > 0) {
                out.write(buffer, 0, n);
            }
            zip.close();
            in.close();
            out.close();
            return out.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "0";
    }
}
