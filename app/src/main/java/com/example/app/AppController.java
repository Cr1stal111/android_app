package com.example.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class AppController extends AppCompatActivity {

    public LoginActivity loginActivity;

    private static final int REQUEST_CODE_PERMISSION_WRITE_EXTERNAL_STORAGE = 1;
    public static String mToken;
    public static final String HOST = "http://172.16.89.6:3000";
    public static final String USERNAME = "user14";
    public static final String PASSWORD = "14";

    static class Request {
        public URL url;
        public String method;
        public HashMap<String, String> props;
        public JSONObject data;

        public Request(URL url, String method, HashMap<String, String> props, JSONObject data) {
            this.url = url;
            this.method = method;
            this.props = props;
            this.data = data;
        }
    }

    static class NetworkTask extends AsyncTask<Request, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(Request[] objects) {
            return connectAndSend(objects[0]);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    private static JSONObject connectAndSend(Request request) {
        try {
            HttpURLConnection con = (HttpURLConnection) request.url.openConnection();
            con.setRequestMethod(request.method);
            for (Map.Entry<String, String> entry : request.props.entrySet()) {
                con.setRequestProperty(entry.getKey(), entry.getValue());
            }

            if (request.data.length() > 0) {
                OutputStream os = con.getOutputStream();
                os.write(request.data.toString().getBytes());
            }
            InputStream is = con.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuilder builder = new StringBuilder();

            String line;
            while ((line = br.readLine()) != null) {
                builder.append(line);
            }
            String result = builder.toString();
            return new JSONObject(result);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void onSignup(View view) {
        URL url = null;
        try {
            url = new URL(HOST + "/api/signup");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        NetworkTask t = new NetworkTask();

        try {
            JSONObject data = new JSONObject();
            data.put("login", USERNAME);
            data.put("password", PASSWORD);
            HashMap<String, String> props = new HashMap<>();
            props.put("Content-Type", "application/json");
            Request request = new Request(url, "POST", props, data);

            t.execute(request);

            JSONObject result = t.get();
            Log.i(MainActivity.class.getSimpleName(), "OUTPUT Result: " + result);
            if (result != null) {
                mToken = result.getString("token");
            }
            Log.i(MainActivity.class.getSimpleName(), "OUTPUT Token: " + mToken);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onLogin(View view) {
        URL url = null;
        try {
            url = new URL(HOST + "/api/login");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        NetworkTask t = new NetworkTask();

        try {
            JSONObject data = new JSONObject();
            data.put("login", USERNAME);
            data.put("password", PASSWORD);
            HashMap<String, String> props = new HashMap<>();
            props.put("Content-Type", "application/json");
            Request request = new Request(url, "POST", props, data);

            t.execute(request);

            JSONObject result = t.get();
            Log.i(MainActivity.class.getSimpleName(), "OUTPUT Result: " + result);
            if (result != null) {
                mToken = result.getString("token");
            }
            Log.i(MainActivity.class.getSimpleName(), "OUTPUT Token: " + mToken);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void onLogout(View view) {
        URL url = null;
        try {
            url = new URL(HOST + "/api/logout");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        NetworkTask t = new NetworkTask();

        try {
            JSONObject data = new JSONObject();
            // data.put("token", mToken);
            HashMap<String, String> props = new HashMap<>();
            props.put("Content-Type", "application/json");
            props.put("token", mToken);
            Request request = new Request(url, "GET", props, data);

            t.execute(request);

            JSONObject result = t.get();
            Log.i(MainActivity.class.getSimpleName(), "OUTPUT Result: " + result);
            if (result != null) {
                mToken = result.getString("token");
            }
            Log.i(MainActivity.class.getSimpleName(), "OUTPUT Token: " + mToken);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void onSelfremove(View view) {
        URL url = null;
        try {
            url = new URL(HOST + "/api/deleteaccount/" + USERNAME);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        NetworkTask t = new NetworkTask();

        try {
            JSONObject data = new JSONObject();
            // data.put("token", mToken);
            HashMap<String, String> props = new HashMap<>();
            props.put("Content-Type", "application/json");
            props.put("token", mToken);
            Request request = new Request(url, "DELETE", props, data);

            t.execute(request);

            JSONObject result = t.get();
            Log.i(MainActivity.class.getSimpleName(), "OUTPUT Result: " + result);
            if (result != null) {
                mToken = result.getString("token");
            }
            Log.i(MainActivity.class.getSimpleName(), "OUTPUT Token: " + mToken);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void onUpload(View view) {

    }

    public void onDebug1(View view) throws IOException {

        int permissionStatus = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
            // create file
            String filepath = Environment.getExternalStorageDirectory() + "/Download/test.txt";
            FileOutputStream fos = null;

            try {
                fos = new FileOutputStream(filepath);
                byte[] buffer = "It Works From Android!".getBytes();
                fos.write(buffer, 0, buffer.length);
                fos.close();
                Log.i(MainActivity.class.getSimpleName(), "OUTPUT Create file OK");
            } catch (FileNotFoundException e) {
                Log.i(MainActivity.class.getSimpleName(), "OUTPUT Create file ERR1");
                e.printStackTrace();
            } catch (IOException e) {
                Log.i(MainActivity.class.getSimpleName(), "OUTPUT Create file ERR2");
                e.printStackTrace();
            } finally {
                Log.i(MainActivity.class.getSimpleName(), "OUTPUT Create file FINALLY");
                if (fos != null) {
                    Log.i(MainActivity.class.getSimpleName(), "OUTPUT Create fos !== null");
                    fos.close();
                }
            }
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE_PERMISSION_WRITE_EXTERNAL_STORAGE);
        }


    }

    public void onDebug2(View view) {

        String uploadFilePath = Environment.getExternalStorageDirectory() + "/Download/";
        String uploadFileName = "test.txt";
        String sourceFileUri = uploadFilePath + uploadFileName;
        String upLoadServerUri = HOST + "/api/upload";


        new Thread(new Runnable() {
            public void run() {
                uploadFile(uploadFilePath, uploadFileName, sourceFileUri,
                        upLoadServerUri, "Android File", "File from android");
            }
        }).start();

    }

    public int uploadFile(String uploadFilePath, String uploadFileName,
                          String sourceFileUri, String upLoadServerUri, String title, String body) {

        String fileName = sourceFileUri;

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "qwerty";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(sourceFileUri);

        if (!sourceFile.isFile()) {

            Log.e("uploadFile", "Source File not exist :"
                    + uploadFilePath + "" + uploadFileName);

            runOnUiThread(new Runnable() {
                public void run() {
                    Log.i(MainActivity.class.getSimpleName(), "OUTPUT Upload file: " + uploadFilePath + "" + uploadFileName);
                }
            });

            return 0;

        } else {
            int serverResponseCode = 0;
            try {

                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(upLoadServerUri);

                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Accept", "*/*");
                conn.setRequestProperty("token", mToken);
                conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\"" + uploadFileName + "\"");
                dos.writeBytes(lineEnd);
                dos.writeBytes(lineEnd);

                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(lineEnd);

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"title\"");
                dos.writeBytes(lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(title);
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"body\"");
                dos.writeBytes(lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(body);
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens);
                dos.writeBytes(lineEnd);


                // Responses fro m the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                Log.i(MainActivity.class.getSimpleName(), "OUTPUT HTTP Response is : "
                        + serverResponseMessage + ": " + serverResponseCode);

                if (serverResponseCode == 201) {
                    Log.i(MainActivity.class.getSimpleName(), "OUTPUT File Upload Completed: " + uploadFileName);
                }

                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {

                ex.printStackTrace();

                Log.i(MainActivity.class.getSimpleName(), "OUTPUT File Upload error: " + ex.getMessage(), ex);
            } catch (Exception e) {

                e.printStackTrace();
                Log.i(MainActivity.class.getSimpleName(), "OUTPUT File Upload Exception : "
                        + e.getMessage(), e);
            }
            return serverResponseCode;

        } // End else block
    }

}