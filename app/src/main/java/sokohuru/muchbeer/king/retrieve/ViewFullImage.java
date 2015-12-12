package sokohuru.muchbeer.king.retrieve;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ImageView;

import sokohuru.muchbeer.king.R;

/**
 * Created by muchbeer on 12/13/2015.
 */
public class ViewFullImage extends ActionBarActivity {

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_image_activity);

        Intent intent = getIntent();
        int i = intent.getIntExtra(MainActivity.BITMAP_ID,0);

        imageView = (ImageView) findViewById(R.id.imageViewFull);
        imageView.setImageBitmap(GetAlImages.bitmaps[i]);
    }
}
