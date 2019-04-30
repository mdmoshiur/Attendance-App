package com.example.attendance;

import android.content.Intent;
import android.database.Cursor;
import android.provider.DocumentsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;

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

        Intent intent = getIntent();
        table_name = intent.getStringExtra("table_name");

        //table code
        webView = (WebView) findViewById(R.id.my_webView_id);
        //webView.loadUrl("https://google.com");
        webView.loadUrl("file:///android_asset/show_table.html");
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        //webView.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);
       // webView.getSettings().setUseWideViewPort(true);
        createTable();
        webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);


    }

    private void createTable() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<html> <head>" +
                "<meta name= viewport content= initial-scale=1, maximum-scale=1 >"+
                "<link rel=\"stylesheet\" type=\"text/css\"  href=\"file:///android_asset/styles.css\">"+
                "</head> <body> <table>");
        //create table header
        Cursor cursor = database_helper.AllData(table_name);
        stringBuilder.append("<tr><th>Roll No</th>");
        Integer col = cursor.getColumnCount();
        for(int i=5; i < col; i++) {
            String col_name = cursor.getColumnName(i);
            String cycle = col_name.substring(col_name.indexOf("cycle_") + 6, col_name.indexOf("_day"));
            String day = col_name.substring(col_name.indexOf("day_") + 4, col_name.indexOf("_date"));
            stringBuilder.append("<th>"+ cycle+ day + "</th>");
        }
        stringBuilder.append("</tr>");

        //now table data
        Integer row = cursor.getCount();
        while (cursor.moveToNext()){
            String roll = cursor.getString(1);
            stringBuilder.append("<tr><td>"+roll+"</td>");
            for(int j=5; j<col; j++){
                String presence = cursor.getString(j);
                stringBuilder.append("<td>"+presence+"</td>");
            }
            stringBuilder.append("</tr>");
        }

        stringBuilder.append("</table></body></html>");
        html = stringBuilder.toString();
    }
    /*
    private void createHtmlFile() throws IOException {
        String table = "<html><body> this is the format</body></html>";
        File file = new File("file:///android_asset/show_table3.html");
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
        bufferedWriter.write(table);
        bufferedWriter.close();
    }

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
