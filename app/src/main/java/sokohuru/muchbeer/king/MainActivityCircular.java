package sokohuru.muchbeer.king;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;


import java.util.logging.LogRecord;


public class MainActivityCircular extends ActionBarActivity {

    Button upload;
    private ProgressBar progressBar;
    int pStatus = 0;
TextView tx;

    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_circular);

        upload=(Button)findViewById(R.id.btnUpload);
        tx = (TextView) findViewById(R.id.textView1);
       progressBar = (ProgressBar) findViewById(R.id.circularProgressbar);

       progressBar.setVisibility(View.GONE);
       upload.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {

               //progressBar = new ProgressBar(view.getContext());

               progressBar.setVisibility(view.VISIBLE);
//               progressBar.getProgressDrawable().setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN);

               new Thread(new Runnable() {

                   @Override
                   public void run() {
                       // TODO Auto-generated method stub
                       while (pStatus < 100) {
                           pStatus += 1;

                           handler.post(new Runnable() {

                               @Override
                               public void run() {
                                   // TODO Auto-generated method stub
                                   progressBar.setProgress(pStatus);
                                   tx.setText(pStatus + "/" + progressBar.getMax());
                                //   pBar2.setProgress(pStatus);
                                 //  tv2.setText(pStatus + "%");
                               }
                           });
                           try {
                               // Sleep for 200 milliseconds.
                               // Just to display the progress slowly
                               Thread.sleep(200);
                           } catch (InterruptedException e) {
                               e.printStackTrace();
                           }
                       }
                   }
               }).start();
           }


       });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_activity_circular, menu);
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
}
