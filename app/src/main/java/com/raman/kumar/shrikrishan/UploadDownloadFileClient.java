package com.raman.kumar.shrikrishan;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;


import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class UploadDownloadFileClient {
	private String url;
	private HttpURLConnection con;
	private OutputStream os;
	private String delimiter = "--";
	private String boundary = "SwA" + Long.toString(System.currentTimeMillis())
			+ "SwA";
	String fileName;
	public static final int DIALOG_DOWNLOAD_PROGRESS = 0;

	public ProgressListener getmProgressListener() {
		return mProgressListener;
	}

	public void setmProgressListener(ProgressListener mProgressListener) {
		this.mProgressListener = mProgressListener;
	}

	private ProgressListener mProgressListener;

	public UploadDownloadFileClient(String url) {
		this.url = url;
	}

	public byte[] downloadImage(Activity activity, String imageName,

                                AsyncTask<String, Void, byte[]> task, String svcName, String requestType) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		try {
			HttpURLConnection con = (HttpURLConnection) (new URL(url))
					.openConnection();
			con.setRequestMethod("POST");
			con.setDoInput(true);
			con.setDoOutput(true);
			con.connect();
			con.getOutputStream()
					.write(("inputFileName=" + imageName + "&method=" + "downloadFile" + "&svcName=" + svcName + "&requestType=" + requestType)
							.getBytes());
			fileName = con.getHeaderField("fileName");
			FileOutputStream fos = null;
			InputStream is = con.getInputStream();
			byte[] b = new byte[4*1024];
			while (is.read(b) != -1) {
				baos.write(b);
				try {
					// String root = "mnt/sdcard";
					String applicationDirectory = "/MTCOllect";
					File SDCardRoot = Environment.getExternalStorageDirectory();
					File myDir = new File(SDCardRoot + applicationDirectory);
					myDir.mkdirs();
					File file = new File(myDir, fileName);
					if (file.exists())
						file.delete();
					if (!file.exists()) {
						file.createNewFile();
					}
					fos = new FileOutputStream(file);
					baos.writeTo(fos);
					fos.close();
					 activity.sendBroadcast(new Intent(
					 Intent.ACTION_MEDIA_MOUNTED,
					 Uri.parse("file://"
					 + Environment.getExternalStorageDirectory())));
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}

		} catch (Throwable t) {
			t.printStackTrace();
		}
		return baos.toByteArray();
	}


	public void connectForMultipart() throws Exception {
		con = (HttpURLConnection) (new URL(url)).openConnection();
		con.setRequestMethod("POST");
		con.setDoInput(true);
		con.setDoOutput(true);
		con.setRequestProperty("Connection", "Keep-Alive");
		con.setRequestProperty("Content-Type", "multipart/form-data; boundary="
				+ boundary);
		con.connect();
		os = con.getOutputStream();
	}

	public void addFormPart(String paramName, String value) throws Exception {
		writeParamData(paramName, value);
	}

	public void addFilePart(String paramName, String fileName, byte[] data)
			throws Exception {
		os.write((delimiter + boundary + "\r\n").getBytes());
		os.write(("Content-Disposition: form-data; name=\"" + paramName
				+ "\"; filename=\"" + fileName + "\"\r\n").getBytes());
		os.write(("Content-Type: application/octet-stream\r\n").getBytes());
		os.write(("Content-Transfer-Encoding: binary\r\n").getBytes());
		os.write("\r\n".getBytes());
		os.write(data);
		os.write("\r\n".getBytes());
	}

	public void addDocumentFilePart(String paramName, String fileName, File file)
			throws Exception {
		System.out.println("File Name.." + file.getName());
		os.write((delimiter + boundary + "\r\n").getBytes());
		os.write(("Content-Disposition: form-data; name=\"" + paramName
				+ "\"; filename=\"" + fileName + "\"\r\n").getBytes());
		os.write(("Content-Type: binary/octet-stream\r\n").getBytes());
		os.write(("Content-Transfer-Encoding: binary\r\n").getBytes());
		os.write("\r\n".getBytes());
		FileInputStream in = new FileInputStream(file);
		byte[] buffer = new byte[4096];
		int length;
		while ((length = in.read(buffer)) > 0) {
			os.write(buffer, 0, length);
			if(mProgressListener!=null){
				mProgressListener.onProgressUpdate(length);
			}
		}
		in.close();
		os.write("\r\n".getBytes());
	}

	public void finishMultipart() throws Exception {
		os.write((delimiter + boundary + delimiter + "\r\n").getBytes());
	}

	public String getResponse() throws Exception {
		InputStream is = con.getInputStream();
		String line;
		BufferedReader br=new BufferedReader(new InputStreamReader(is));
		String response="";
		while ((line=br.readLine()) != null) {
			response+=line;
		}

		con.disconnect();

		return response;
	}

	private void writeParamData(String paramName, String value)
			throws Exception {
		os.write((delimiter + boundary + "\r\n").getBytes());
		os.write("Content-Type: text/plain\r\n".getBytes());
		os.write(("Content-Disposition: form-data; name=\"" + paramName + "\"\r\n")
				.getBytes());
		os.write(("\r\n" + value + "\r\n").getBytes());
		if(mProgressListener!=null){
			mProgressListener.onProgressUpdate(value.getBytes().length);
//			Thread.sleep(5000);
		}
	}



	private static String getStringFromInputStream(InputStream is) {

		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();

		String line;
		try {

			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				sb.append(line);
				System.out.println("Line...." + line);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return sb.toString();

	}

	public interface ProgressListener{
		void onProgressUpdate(int progressIncrease);
	}
}


