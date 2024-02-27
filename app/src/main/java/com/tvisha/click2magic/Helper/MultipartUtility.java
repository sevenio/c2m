package com.tvisha.click2magic.Helper;

import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;


public class MultipartUtility {
    private final String boundary;
    private static final String LINE_FEED = "\r\n";
    private HttpURLConnection httpConn;
    private String charset;
    private OutputStream outputStream;
    private PrintWriter writer;

    public MultipartUtility(String requestURL, String charset)
            throws IOException {
        this.charset = charset;

        // creates a unique boundary based on time stamp
        boundary = "===" + System.currentTimeMillis() + "===";
        URL url = new URL(requestURL);
        httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setUseCaches(false);
        httpConn.setDoOutput(true); // indicates POST method
        httpConn.setDoInput(true);
        httpConn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        httpConn.setRequestProperty("User-Agent", "CodeJava Agent");
        httpConn.setRequestProperty("Test", "Bonjour");
        outputStream = httpConn.getOutputStream();
        writer = new PrintWriter(new OutputStreamWriter(outputStream, charset),true);
    }

    public void addFormField(String name, String value) {
        try {
            writer.append("--" + boundary).append(LINE_FEED);
            writer.append("Content-Disposition: form-data; name=\"" + name + "\"")
                    .append(LINE_FEED);
            writer.append("Content-Type: text/plain; charset=" + charset).append(
                    LINE_FEED);
            writer.append(LINE_FEED);
            writer.append(value).append(LINE_FEED);
            writer.flush();
        }catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    /**
     * Adds a upload file section to the request
     *
     * @param fieldName  name attribute in <input type="file" name="..." />
     * @param uploadFile a File to be uploaded
     * @throws IOException
     */
    public void addFilePart(String fieldName, File uploadFile)
            throws IOException {
        String fileName = "";
        try {
            /*String fileName = uploadFile.getName().replace("#","/");
        String[] file_split = fileName.split("/");
        fileName = file_split[file_split.length - 1];*/
        /*File file = new File(uploadFile.getAbsolutePath());


        String fileName = uploadFile.getName();*/

            fileName = uploadFile.getName();

            File file = new File(fileName);
            File file1 = new File(uploadFile.getAbsolutePath());


            writer.append("--" + boundary).append(LINE_FEED);
            writer.append(
                    "Content-Disposition: form-data; name=\"" + fieldName
                            + "\"; filename=\"" + fileName + "\"")
                    .append(LINE_FEED);
            writer.append(
                    "Content-Type: "
                            + URLConnection.guessContentTypeFromName(fileName))
                    .append(LINE_FEED);
            writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
            writer.append(LINE_FEED);
            writer.flush();
            FileInputStream inputStream = new FileInputStream(uploadFile);
            byte[] buffer = new byte[4096];
            int bytesRead = -1;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();
            inputStream.close();

            writer.append(LINE_FEED);
            writer.flush();
            return;
        }catch (StringIndexOutOfBoundsException e) {
            File newFile = null;
            String fileNames = uploadFile.getName().replace("#","/");
            String[] file_split = fileNames.split("/");
            fileNames = file_split[file_split.length - 1];
            File file = uploadFile;
            if (file.exists() && file.length()>0)
            {
               newFile = new File(Environment.getExternalStorageDirectory(),fileNames);
                if (newFile.exists() && newFile.length()>0) {
                    addFilePart(fieldName,newFile);
                }
            }
            e.printStackTrace();
        }

        return;
    }

    public void addHeaderField(String name, String value) {
        writer.append(name + ": " + value).append(LINE_FEED);
        writer.flush();
    }

    public String finish() throws IOException {
        String response=null;
        try{
            writer.append(LINE_FEED).flush();
            writer.append("--" + boundary + "--").append(LINE_FEED);
            writer.close();
            int status = httpConn.getResponseCode();
            if (status == HttpURLConnection.HTTP_OK) {
                response=InputStreamToStringBuilder.inputStreamToString(httpConn.getInputStream()).toString();
                return response;
            }else {

                throw new IOException("Server returned non-OK status: " + status+"  "+httpConn.getErrorStream());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return response;
    }
}
