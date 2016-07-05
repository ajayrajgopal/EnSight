package in.co.entersoft.ensight;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.text.SpannableString;
import android.util.DisplayMetrics;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

//test

public class MainActivity extends ActionBarActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    int n=7;
    int k=-n;
    ArrayList<String>  result = new ArrayList<String>();
    ArrayList<String>  content = new ArrayList<String>();
    ArrayList<String>  link = new ArrayList<String>();
    ArrayList<TableRow>  row = new ArrayList<TableRow>();
    ArrayList<ImageView> iv= new ArrayList<ImageView>();
    ArrayList<Bitmap> imgs= new ArrayList<Bitmap>();
    ArrayList<String> tb= new ArrayList<String>();
    TableRow.LayoutParams lp;
    android.support.v4.widget.DrawerLayout.LayoutParams rp;
    int x=0;
    int y=0;
    int tot=8;
    int lastitr=0;
    int imgid=0;
    int flag=0,flag2=0,update=0,loading=1;
    String tag="all";
    String tag2;
    String[] res,cont,lin,thumbnail;
    TableRow[] rowarr;
    Bitmap bitmap;
    LinearLayout image;
    ImageView imgv;
    Button btn1;
    TextView tv;
    TableLayout ll;
    AlertDialog.Builder updateDialog;
    Dialog overlayDialog;
    android.support.v4.widget.DrawerLayout dl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(in.co.entersoft.ensight.R.layout.activity_main);

        FontsOverride.setDefaultFont(this, "MONOSPACE", "font.ttf");
        updateDialog = new AlertDialog.Builder(this);
        btn1=new Button(this);
        Toolbar toolbar = (Toolbar) findViewById(in.co.entersoft.ensight.R.id.toolbar);
        setSupportActionBar(toolbar);




        if(getIntent().getExtras()!=null){
            boolean sp=getIntent().getBooleanExtra("splash",true);
            if(!sp) {
                SpannableString s;
                tag2=getIntent().getStringExtra("tag");
                if(tag2==null){
                    s= new SpannableString("All News");
                }
                else if(tag2.equalsIgnoreCase("attacks")){
                    s= new SpannableString("Cyber Attacks");
                }
                else if(tag2.equalsIgnoreCase("vulnerability")){
                    s= new SpannableString("Vulnerabilities");

                }
                else{
                    s= new SpannableString("All News");
                }
                getSupportActionBar().setTitle(s);
            }
            else{
                splash();
                getSupportActionBar().setTitle("All News");
            }
        }
        else{
            splash();

            getSupportActionBar().setTitle("All News");
        }
        ConnectivityManager cm =
                (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean net = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        FloatingActionButton fab = (FloatingActionButton) findViewById(in.co.entersoft.ensight.R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                if(tag2!=null) startActivity(getIntent().putExtra("tag",tag2));
                else startActivity(getIntent().putExtra("splash",false));
            }
        });
        DrawerLayout drawer = (DrawerLayout) findViewById(in.co.entersoft.ensight.R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, in.co.entersoft.ensight.R.string.navigation_drawer_open, in.co.entersoft.ensight.R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(in.co.entersoft.ensight.R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        if(net==true){
            enableHttpResponseCache();
            getData();
        }
        else{
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

            alertDialog.setTitle("No Internet Connection");

            alertDialog.setMessage("You do not have an active Internet Connection. Please turn on your Wifi or Data and click refresh");

            alertDialog.setPositiveButton("REFRESH", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                    startActivity(getIntent());
                }
            });

            alertDialog.setNeutralButton("EXIT", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            alertDialog.show();
        }
    }
    private void enableHttpResponseCache() {
        try {
            long httpCacheSize = 10 * 1024 * 1024; // 10 MiB
            File httpCacheDir = new File(getCacheDir(), "http");
            Class.forName("android.net.http.HttpResponseCache")
                    .getMethod("install", File.class, long.class)
                    .invoke(null, httpCacheDir, httpCacheSize);
        } catch (Exception httpResponseCacheNotAvailable) {
        }
    }
    void splash(){
        overlayDialog = new Dialog(MainActivity.this, android.R.style.Theme_Panel);
        overlayDialog.setCancelable(true);
        overlayDialog.show();
        imgv=new ImageView(this);
        imgv=new ImageView(this);
        dl=(android.support.v4.widget.DrawerLayout)findViewById(in.co.entersoft.ensight.R.id.drawer_layout);
        rp = new android.support.v4.widget.DrawerLayout.LayoutParams(android.support.v4.widget.DrawerLayout.LayoutParams.MATCH_PARENT,android.support.v4.widget.DrawerLayout.LayoutParams.MATCH_PARENT);
        imgv.setLayoutParams(rp);
        imgv.setScaleType(ImageView.ScaleType.FIT_XY);
        imgv.setImageResource(in.co.entersoft.ensight.R.drawable.splash);
        dl.addView(imgv);
        flag2=1;
    }
    void getData(){
        k=k+n;
        if(k+n<=tot){

            ConnectivityManager cm =
                    (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean net = activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();
            if(net==true) {
                if (tag2 == null) {
                    new JSONTask().execute("https://ec2-54-186-180-232.us-west-2.compute.amazonaws.com/getdata.php?from=" + k + "&to=" + n + "&tag=" + tag);
                } else {
                    new JSONTask().execute("https://ec2-54-186-180-232.us-west-2.compute.amazonaws.com/getdata.php?from=" + k + "&to=" + n + "&tag=" + tag2);

                }
            }
            else{
                CharSequence text = "No Network Connection";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(getApplicationContext(), text, duration);
                toast.show();
            }
        }
        else{
            CharSequence text = "No Articles Found";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(getApplicationContext(), text, duration);
            toast.show();
        }
    }
    int i;
    void LoadData(String[] r1){
        int g = r1.length;

        ll = (TableLayout) findViewById(in.co.entersoft.ensight.R.id.table);

        for (i=lastitr+1; i <(g); i++) {

            row.add(new TableRow(this));
            rowarr= new TableRow[row.size()];
            rowarr = row.toArray(rowarr);
            lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
            iv.add(new ImageView(this));
            tv= new TextView(this);
            tv.setLayoutParams(lp);

            tv.setPadding(10, 10, 0, 0);
            tv.setTextSize(20);
            tv.setText(r1[i]);
            tv.setWidth(435);
            tv.setGravity(Gravity.LEFT | Gravity.CENTER);

            iv.get(i-1).setImageResource(in.co.entersoft.ensight.R.drawable.loading);
            (row.get(i-1)).setLayoutParams(lp);
            (iv.get(i-1)).setPadding(0, 25, 0, 25);
            (iv.get(i-1)).setLayoutParams(new TableRow.LayoutParams(200, 200));
            (iv.get(i-1)).setScaleType(ImageView.ScaleType.FIT_XY);
            new LoadImage().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, thumbnail[i]);
            addviews(i-1);
            y++;
            lastitr=i;
        }
    }
    void addviews(int a){
        (row.get(a)).setBackgroundResource(in.co.entersoft.ensight.R.drawable.separator);
        (row.get(a)).addView(iv.get(a));
        (row.get(a)).addView(tv);
        ll.addView((row.get(a)), a);
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
            iv.get(imgid).setImageBitmap(image);
            (row.get(imgid)).setLayoutParams(lp);
            (iv.get(imgid)).setPadding(0, 25, 0, 25);
            (iv.get(imgid)).setLayoutParams(new TableRow.LayoutParams(200, 200));
            (iv.get(imgid)).setScaleType(ImageView.ScaleType.FIT_XY);
            imgs.add(image);
            imgid++;
            if(imgid==(n-1)&&flag2==1){
                overlayDialog.cancel();
                dl.removeView(imgv);
                flag2=0;
            }
        }
    }
    private class LoadFirstImage extends AsyncTask<String, String, Bitmap> {
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

        protected void onPostExecute(Bitmap imag){
            imgs.add(imag);
            BitmapDrawable drawableBitmap=new BitmapDrawable(imag);
            image.setBackgroundDrawable(drawableBitmap);
        }
    }
    public class JSONTask extends AsyncTask<String, String, String[]> {
        @Override
        protected String[] doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setUseCaches(true);
                connection.addRequestProperty("Cache-Control", "max-age=600");
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();

                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                String json=buffer.toString();

                JSONObject parentObject = new JSONObject(json);
                JSONArray parentArray = parentObject.getJSONArray("articles");
                String total=parentObject.getString("total");
                if(flag==0){
                    String ver=parentObject.getString("version");
                    PackageInfo info = null;
                    PackageManager manager = getApplicationContext().getPackageManager();
                    try {
                        info = manager.getPackageInfo(
                                getApplicationContext().getPackageName(), 0);
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                    String currver = info.versionName;
                    if(!ver.equalsIgnoreCase(currver)){
                        update=1;
                    }

                }
                tot =Integer.parseInt(total);
                int i;
                if(parentObject.length()<n) {
                    for ( i= 0; i < n; i++) {
                        JSONObject finalObject = parentArray.getJSONObject(i);
                        result.add(finalObject.getString("Title"));
                        tb.add(finalObject.getString("Thumbnails"));
                        content.add(finalObject.getString("Content"));
                        link.add(finalObject.getString("Link"));
                        x++;
                    }
                    lin = new String[link.size()];
                    lin= link.toArray(lin);
                    cont = new String[content.size()];
                    cont= content.toArray(cont);
                    res= new String[result.size()];
                    res = result.toArray(res);
                    thumbnail= new String[tb.size()];
                    thumbnail= tb.toArray(thumbnail);
                    return res;
                }
                else{
                    String[] test=new String[1];
                    test[0]="No Entries";
                    return test;
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected  void onPostExecute(String[] result) {
            super.onPostExecute(result);
            loading=0;
            if(!result[0].equals("No Entries")) {
                TextView text=(TextView)findViewById(in.co.entersoft.ensight.R.id.textView);
                image=(LinearLayout)findViewById(in.co.entersoft.ensight.R.id.image);
                if(flag==0){

                    flag=1;
                    new LoadFirstImage().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, thumbnail[0]);
                    text.setText(result[0]);
                    image.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getApplicationContext(), Article.class);
                            intent.putExtra("cont", cont[0]);
                            intent.putExtra("link", lin[0]);
                            intent.putExtra("title", res[0]);
                            ByteArrayOutputStream stream1;
                            try{
                                stream1= new ByteArrayOutputStream();
                                imgs.get(0).compress(Bitmap.CompressFormat.PNG, 100, stream1);
                                byte[] byteArray = stream1.toByteArray();
                                intent.putExtra("image", byteArray);
                            }catch(IndexOutOfBoundsException e){
                                intent.putExtra("thumbnail", thumbnail[0]);
                            }
                            startActivity(intent);
                        }
                    });
                }
                if(update==1) {

                    updateDialog.setTitle("Update Available");
                    updateDialog.setMessage("There might be an update available. Please update EnSights");

                    final AlertDialog alert = updateDialog.create();

                    updateDialog.show();

                    updateDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            alert.cancel();
                        }
                    });
                }
            }


            InteractiveScrollView scrollView=(InteractiveScrollView)findViewById(in.co.entersoft.ensight.R.id.scrollView);
            LoadData(result);

            for(int i=1;i<(rowarr.length);i++){
                final int j=i;
                rowarr[i-1].setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        Intent intent= new Intent(getApplicationContext(),Article.class);
                        intent.putExtra("title",res[j]);
                        intent.putExtra("cont",cont[j]);
                        intent.putExtra("link", lin[j]);

                        try{
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            imgs.get(j).compress(Bitmap.CompressFormat.PNG, 100, stream);
                            byte[] byteArray = stream.toByteArray();
                            intent.putExtra("image", byteArray);
                        }catch(IndexOutOfBoundsException e){

                            intent.putExtra("thumbnail", thumbnail[j]);
                        }


                        startActivity(intent);
                    }
                });

            }
            scrollView.setOnBottomReachedListener(
                    new InteractiveScrollView.OnBottomReachedListener() {
                        @Override
                        public void onBottomReached() {
                            CharSequence text = "Loading...";
                            int duration = Toast.LENGTH_SHORT;
                            Toast toast = Toast.makeText(getApplicationContext(), text, duration);
                            toast.show();
                            getData();
                        }
                    }
            );
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(in.co.entersoft.ensight.R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == in.co.entersoft.ensight.R.id.all) {
            finish();
            Intent nav=new Intent(getApplicationContext(),MainActivity.class);
            nav.putExtra("tag","all");
            nav.putExtra("splash",false);
            startActivity(nav);
        }
        else if (id == in.co.entersoft.ensight.R.id.cyber) {
            finish();
            Intent nav=new Intent(getApplicationContext(),MainActivity.class);
            nav.putExtra("tag","attacks");
            nav.putExtra("splash",false);
            startActivity(nav);

        }
        else if (id == in.co.entersoft.ensight.R.id.vulnerabilities) {
            finish();
            Intent nav=new Intent(getApplicationContext(),MainActivity.class);
            nav.putExtra("tag","vulnerability");
            nav.putExtra("splash",false);
            startActivity(nav);
        }
        else if (id == in.co.entersoft.ensight.R.id.about) {
            Uri uri = Uri.parse("http://www.entersoftsecurity.com");
            Intent inte = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(inte);
        }
        else if(id == in.co.entersoft.ensight.R.id.feedback){
            final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
            String mail="Device ID: "+Settings.Secure.getString(getApplication().getContentResolver(), Settings.Secure.ANDROID_ID)+"\nDevice Name: "+android.os.Build.MODEL;
            mail=mail+"\nAndroid Version: "+ Build.VERSION.RELEASE;
            PackageManager manager = getApplicationContext().getPackageManager();
            PackageInfo info = null;
            try {
                info = manager.getPackageInfo(
                        getApplicationContext().getPackageName(), 0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            String version = info.versionName;
            mail= mail+"\nApp Version: "+ version;
            DisplayMetrics displayMetrics = getApplicationContext().getResources().getDisplayMetrics();
            int width = displayMetrics.widthPixels;
            int height = displayMetrics.heightPixels;
            mail= mail+"\nDisplay Width(in DP): "+width+"\nDisplay Height(in DP): "+height+"\n--Please do not edit anything about this line--\n";
            emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"info@entersoftsecurity.com"});
            emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "EnSight: Android App Feedback");
            emailIntent.putExtra(android.content.Intent.EXTRA_TEXT,mail );

            emailIntent.setType("message/rfc822");
            emailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(Intent.createChooser(emailIntent, ""));
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(in.co.entersoft.ensight.R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
