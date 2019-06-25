package com.example.attendance;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class HtmlActivity extends AppCompatActivity {
    private WebView webView;
    private String html;
    private String header;
    private String table_name;

    Database_helper database_helper = new Database_helper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_html);
        getSupportActionBar().setTitle("Full Att. Sheet");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        table_name = intent.getStringExtra("table_name");

        //table code
        webView = (WebView) findViewById(R.id.my_webView_id);
        //webView.loadUrl("https://google.com");
        //webView.loadUrl("file:///android_asset/show_table.html");
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        //webView.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);
       // webView.getSettings().setUseWideViewPort(true);
        createTable();
        webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }


    }

    private void createTable() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<html> <head>" +
                "<meta name= viewport content= initial-scale=1, maximum-scale=1 >"+
                "<link rel=\"stylesheet\" type=\"text/css\"  href=\"file:///android_asset/styles.css\">"+
                "</head> <body>");
        //for header
        String row_id = table_name.substring(table_name.indexOf("table_")+6);
        Cursor currentCourse = database_helper.showSingleCourse(row_id);
        while (currentCourse.moveToNext()){
            String dept = currentCourse.getString(currentCourse.getColumnIndex("dept"));
            String series = currentCourse.getString(currentCourse.getColumnIndex("series"));
            String section = currentCourse.getString(currentCourse.getColumnIndex("section"));
            String course_name = currentCourse.getString(currentCourse.getColumnIndex("course_name"));

            stringBuilder.append("<div> <p> Rajshahi University Of Engineering & Technology " +
                    "<br> Department: "+ dept+ ", Series: "+ series +
                    "<br>Course No: "+ course_name +", Section: "+ section+ "</p></div>");
        }
        currentCourse.close();
        //create table header
        stringBuilder.append("<table>");
        Cursor cursor = database_helper.AllData(table_name);
        stringBuilder.append("<tr><th>Roll No</th>");
        Integer col = cursor.getColumnCount();
        for(int i=6; i < col; i++) {
            String col_name = cursor.getColumnName(i);
            String cycle = col_name.substring(col_name.indexOf("cycle_") + 6, col_name.indexOf("_day"));
            String day = col_name.substring(col_name.indexOf("day_") + 4, col_name.indexOf("_date"));
            stringBuilder.append("<th>"+ cycle+ day + "</th>");
        }
        stringBuilder.append("<th>Percentage</th>");
        stringBuilder.append("<th>Marks</th>");
        stringBuilder.append("</tr>");

        //now table data
        Integer row = cursor.getCount();
        while (cursor.moveToNext()){
            String roll = cursor.getString(1);
            Integer total_class = Integer.parseInt(cursor.getString(cursor.getColumnIndex("total_class")));
            stringBuilder.append("<tr><td>"+roll+"</td>");
            for(int j=6 ; j<col; j++){
                if((col - total_class) > j){
                    stringBuilder.append("<td>"+""+"</td>");
                } else {
                    String presence = cursor.getString(j);
                    stringBuilder.append("<td>"+presence+"</td>");
                }
            }
            String percentage = cursor.getString(cursor.getColumnIndex("p_att"));
            String marks = cursor.getString(cursor.getColumnIndex("marks"));
            stringBuilder.append("<td>"+ percentage+ "%</td>");
            stringBuilder.append("<td>"+ marks +"</td>");
            stringBuilder.append("</tr>");
        }
        cursor.close();
        stringBuilder.append("</table></body></html>");
        html = stringBuilder.toString();
    }
    /*

    @Override
    public void onBackPressed() {
        if(webView.canGoBack()){
            webView.goBack();
        }  else {
            super.onBackPressed();
        }
    }
    */
}
