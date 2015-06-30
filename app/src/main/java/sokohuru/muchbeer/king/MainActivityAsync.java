package sokohuru.muchbeer.king;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.GregorianCalendar;


public class MainActivityAsync extends ActionBarActivity implements View.OnClickListener{

    private String sourceFileUri;

    private TextView messageText;
    private TextView messagePercentage;
    private EditText title,desc;
    private Button uploadButton, btnselectpic;
    private ImageView imageview;
    private int serverResponseCode = 0;
    private ProgressDialog dialog = null;
    private ProgressBar progressBar;

    int bytesRead, count, bytesAvailable, bufferSize;

    int pStatus = 0;
    private String upLoadServerUri = null;
    private String imagepath=null;

    private Handler mHandler = new Handler();
    static final int DIALOG_DOWNLOAD_PROGRESS =0;
    private int percentage=0;
    private String urlParth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        uploadButton = (Button)findViewById(R.id.upLoadButton);
        btnselectpic = (Button)findViewById(R.id.btnCapturePicture);
        messageText  = (TextView)findViewById(R.id.txTitle);
        messagePercentage= (TextView) findViewById(R.id.txPercentate);
        progressBar=(ProgressBar)  findViewById(R.id.circularProgressbar);

        imageview = (ImageView)findViewById(R.id.imageViewPic);
        title=(EditText)findViewById(R.id.title);
        // desc=(EditText)findViewById(R.id.etdesc);

        btnselectpic.setOnClickListener(this);
        uploadButton.setOnClickListener(this);
        // progressBar.setVisibility(View.VISIBLE);
        upLoadServerUri = "http://sokouhuru.com/uploads.php";
        ImageView img= new ImageView(this);
    }

    @Override
    public void onClick(View view) {


        if(view==btnselectpic)
        {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Complete action using"), 1);
        }
        else if (view==uploadButton) {

            // progressBar = new ProgressBar(view.getContext());

             //    dialog.show();

            messageText.setText("uploading started.....");
                callUploadImageAsyncTask();
           // Toast.makeText(getApplicationContext(),"The total file: " + bytesAvailable, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            //Bitmap photo = (Bitmap) data.getData().getPath();
            //Uri imagename=data.getData();
            Uri selectedImageUri = data.getData();
            imagepath = getPath(selectedImageUri);
            Bitmap bitmap= BitmapFactory.decodeFile(imagepath);
            imageview.setImageBitmap(bitmap);
            messageText.setText("Uploading file path:" + imagepath);
                urlParth = imagepath;


        }

    }
    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }


public void callUploadImageAsyncTask() {
        new UploadImageAsyncTask().execute();
}
    public class UploadImageAsyncTask extends AsyncTask<String, String, String> {


        int day, month, year;
        int second, minute, hour;
        GregorianCalendar date = new GregorianCalendar();


        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";


        long total =0;

        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//showDialog(DIALOG_DOWNLOAD_PROGRESS);

        }

        @Override
        protected String doInBackground(String... strings) {

            day = date.get(Calendar.DAY_OF_MONTH);
            month = date.get(Calendar.MONTH);
            year = date.get(Calendar.YEAR);
            //sourceFileUri = imagepath;

            second = date.get(Calendar.SECOND);
            minute = date.get(Calendar.MINUTE);
            hour = date.get(Calendar.HOUR);

            String name=(hour+" hr  "+minute+" min  "+second+" sec  "+day+" day  "+(month+1)+""+year);
            String tag=name+".jpg";
            String fileName = urlParth.replace(urlParth,tag);

            final long[] total = {0};

            final byte[] buffer;
            final int maxBufferSize = 1 * 1024 * 1024;
            File sourceFile = new File(urlParth);

            if (!sourceFile.isFile()) {

                dialog.dismiss();
               // progressBar.setVisibility(View.GONE);

                Log.e("uploadFile", "Source File not exist :"+imagepath);

                runOnUiThread(new Runnable() {
                    public void run() {
                        messageText.setText("Source File not exist :"+ imagepath);
                    }
                });



            } else  {
                try {
                    // open a URL connection to the Servlet
                    final FileInputStream fileInputStream = new FileInputStream(sourceFile);
                    URL url = new URL(upLoadServerUri);
                   // int lenghtOfFile = conn.getContentLength();

                    // Open a HTTP  connection to  the URL
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true); // Allow Inputs
                    conn.setDoOutput(true); // Allow Outputs
                    conn.setUseCaches(false); // Don't use a Cached Copy
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Connection", "Keep-Alive");
                    conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                    conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                    conn.setRequestProperty("uploaded_file", fileName);


                    dos = new DataOutputStream(conn.getOutputStream());

                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                            + fileName + "\"" + lineEnd);

                    dos.writeBytes(lineEnd);




                    // create a buffer of  maximum size
                    bytesAvailable = fileInputStream.available();

                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    buffer = new byte[bufferSize];

                    // read file and write it into form...
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                //    Toast.makeText(getApplicationContext(), "Size is: " + bytesAvailable, Toast.LENGTH_LONG).show();
                    //  int lengthOfFile = conn.getContentLength();
                    final int[] updating = {0};
                       //     Toast.makeText(MainActivityAsync.this, "The total size: " + , Toast.LENGTH_SHORT).show();


                    while ((count = bytesRead) > 0 ) {


                        total[0] +=count;
                        //int top[0] +=count;

                            updating[0] +=count;
                        //   percentage = (int) (((total)/lengthOfFile)*100);

                            dos.write(buffer, 0, bufferSize);
                            bytesAvailable = fileInputStream.available();
                            bufferSize = Math.min(bytesAvailable, maxBufferSize);
                            bytesRead = fileInputStream.read(buffer, 0, bufferSize);


                        }
                  //  Toast.makeText(getApplicationContext(), bytesAvailable, Toast.LENGTH_LONG).show();
                       // messagePercentage.setText(bytesAvailable);
                        //  publishProgress(""+ (int)((total[]*100)/2100000));
                       // publishProgress(""+ total);
                        //





                    // send multipart form data necesssary after file data...
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                    // Responses from the server (code and message)
                    serverResponseCode = conn.getResponseCode();
                    String serverResponseMessage = conn.getResponseMessage();
                       // final int getImageLength = conn.getContentLength();
                    Log.i("uploadFile", "HTTP Response is : "
                            + serverResponseMessage + ": " + serverResponseCode);


                    if(serverResponseCode == 200){

                        runOnUiThread(new Runnable() {
                            public void run() {
                                int num =100;
                              //  progressBar.setVisibility(View.GONE);
                                // messageText.setText(msg);
                                String msg = "File Upload Completed.\n\n See uploaded file here : \n\n"
                                        +"http://sokouhuru.com/uploads";
                                messageText.setText(msg);
                                Toast.makeText(MainActivityAsync.this, "File Upload Complete..", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    //close the streams //
                    fileInputStream.close();
                    dos.flush();
                    dos.close();

                }
                catch (MalformedURLException ex) {

                    // dialog.dismiss();
                    ex.printStackTrace();

                    runOnUiThread(new Runnable() {
                        public void run() {

                          //  progressBar.setVisibility(View.GONE);
                            messageText.setText("MalformedURLException Exception : check script url.");
                            Toast.makeText(MainActivityAsync.this, "MalformedURLException", Toast.LENGTH_SHORT).show();
                        }
                    });

                    Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
                } catch (Exception e) {

                    // dialog.dismiss();
                    e.printStackTrace();

                    runOnUiThread(new Runnable() {
                        public void run() {

                            progressBar.setVisibility(View.GONE);
                            messageText.setText("Got Exception : Check your connection.. ");
                            Toast.makeText(MainActivityAsync.this, "Check your connection and try again ", Toast.LENGTH_SHORT).show();
                        }
                    });
                    Log.e("Upload server Exception", "Exception : " + e.getMessage(), e);
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... progress) {
            super.onProgressUpdate(progress);

            Log.d("ANDRO_ASYNC",progress[0]);
          //  dialog.setProgress(Integer.parseInt(progress[0]));

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
           // dismissDialog(DIALOG_DOWNLOAD_PROGRESS);
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }




    @Override
    protected Dialog onCreateDialog(int id) {

        switch (id) {
            case DIALOG_DOWNLOAD_PROGRESS:
                dialog = new ProgressDialog(this);
                dialog.setMessage("Downloading file...");
                dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                dialog.setCancelable(true);
                dialog.show();
                return dialog;
            default:
                return null;
        }
    }
}
