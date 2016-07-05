package in.co.entersoft.ensight;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.URL;

/**
 * Created by Ajay on 3/18/2016.
 */
public class Article extends ActionBarActivity{
    String content;
    String link;
    String title;
    String thumbnail;
    ImageView iv;
    Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(in.example.ajay.navmenu.R.layout.article);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        LayoutInflater inflator = (LayoutInflater) this .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflator.inflate(in.example.ajay.navmenu.R.layout.actionbar_article, null);


        actionBar.setCustomView(v);
        Intent in2 = getIntent();
        content = in2.getStringExtra("cont");
        //content.replace("\\\\","þ");
        //content.replace("\\","");
        //content.replaceAll("þ","\\");
        link = in2.getStringExtra("link");
        title = in2.getStringExtra("title");

        byte[] byteArray;
        iv=(ImageView) findViewById(in.example.ajay.navmenu.R.id.imageView2);
        if(in2.getByteArrayExtra("image")!=null){
            byteArray = getIntent().getByteArrayExtra("image");
            Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            iv.setImageBitmap(bmp);
        }
        else{
            thumbnail = in2.getStringExtra("thumbnail");
            new LoadImage().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, thumbnail);
        }
        TextView tv= (TextView)findViewById(in.example.ajay.navmenu.R.id.textView);
        TextView title2= (TextView) findViewById(in.example.ajay.navmenu.R.id.title);
        title2.setText(title);
        tv.setText(content);
        Button btn=(Button)findViewById(in.example.ajay.navmenu.R.id.gotolink);
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Uri uri = Uri.parse(link); // missing 'http://' will cause crashed
                Intent inte = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(inte);
            }
        });
        FloatingActionButton fab = (FloatingActionButton) findViewById(in.example.ajay.navmenu.R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareIt();
            }
        });
    }

    private class LoadImage extends AsyncTask<String, String, Bitmap> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        protected Bitmap doInBackground(String... args) {
            try {
                bitmap = BitmapFactory.decodeStream((InputStream) new URL(args[0]).getContent());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap image){
            iv.setImageBitmap(image);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void shareIt() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = title+"\n"+link+"\n  -via EnSights";
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

}