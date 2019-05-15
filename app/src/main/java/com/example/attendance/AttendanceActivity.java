package com.example.attendance;
import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AttendanceActivity extends AppCompatActivity {
    private static final int STORAGE_CODE = 100;
    private List<DataUser> dataUsers;
    private ListView listView;
    private FloatingActionButton floatingActionButton;
    private String TABLE_NAME;
    private String Course_id;
    private Student_adapter student_adapter;

    private Database_helper database_helper = new Database_helper(this);

    @Override
    public void onBackPressed() {
        startActivity(new Intent(AttendanceActivity.this, MainActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);
        getSupportActionBar().setTitle("Summary Attendance");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        Initialize();
        //onclick listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(AttendanceActivity.this, Full_attendanceActivity.class);
                intent.putExtra("table_name", TABLE_NAME);
                intent.putExtra("row_id", dataUsers.get(position).getRowId());
                startActivity(intent);
            }
        });

        //onLongClickListener
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                PopupMenu popupMenu = new PopupMenu(AttendanceActivity.this, view);
                popupMenu.inflate(R.menu.student_popup_menu);
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.remove_student_id:
                                AlertDialog.Builder builder1 = new AlertDialog.Builder(AttendanceActivity.this);
                                builder1.setTitle("Are you sure?");
                                builder1.setMessage("This permanently remove the student roll from database.");
                                builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String id = dataUsers.get(position).getRowId().trim();
                                        database_helper.deleteRow(TABLE_NAME,id);
                                        finish();
                                        startActivity(getIntent());
                                    }
                                })
                                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                //Log.d("Tag", " which: "+ which);
                                                dialog.cancel();
                                            }
                                        });
                                AlertDialog dialog1 = builder1.create();
                                dialog1.show();
                                return true;
                            default:
                                return false;
                        }

                    }
                });
                return true;
            }
        });


        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // if(v.getId()==R.id.fab_add_id)
                //build alert dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(AttendanceActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog_view_design, null);
                builder.setView(mView);
                final AlertDialog dialog = builder.create();
                dialog.show();

                //process for edit text
                final EditText date = mView.findViewById(R.id.date_id);
                final EditText cycle = mView.findViewById(R.id.cycle_id);
                final EditText day = mView.findViewById(R.id.day_id);
                Button mButton = mView.findViewById(R.id.submit_button);

                //open date picker
                date.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //dialog.dismiss();
                        //code for date picker
                        Calendar calendar = Calendar.getInstance();
                        int year = calendar.get(Calendar.YEAR);
                        int month = calendar.get(Calendar.MONTH);
                        int day = calendar.get(Calendar.DAY_OF_MONTH);
                        DatePickerDialog datePickerDialog = new DatePickerDialog(AttendanceActivity.this, new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                date.setText(dayOfMonth+"/"+(month+1)+"/"+year);

                            }
                        },year, month, day);
                        datePickerDialog.show();
                    }
                });

                mButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String Date;
                        String Cycle;
                        String Day;

                        if(date.getText()!=null && cycle.getText() != null && day.getText() != null){
                            Date = date.getText().toString().trim();
                            Cycle = cycle.getText().toString().trim();
                            Day = day.getText().toString().trim();
                            String new_col = "cycle_"+Cycle+"_day_"+ Day +"_date_"+ Date;
                            //remove all spaces
                            new_col = new_col.replaceAll("\\s","");
                            Intent intent = new Intent(AttendanceActivity.this, Take_att_Activity.class);
                            intent.putExtra("table_name", TABLE_NAME);
                            intent.putExtra("new_col_name", new_col);
                            startActivity(intent);
                        } else {
                            Toast.makeText(AttendanceActivity.this,"Please fill up all !!", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

            }
        });
    }//finish of oncreate()

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.summary_attendance_option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.app_bar_search_id:
                SearchView searchView = (SearchView) item.getActionView();
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        student_adapter.getFilter().filter(newText);
                        return false;
                    }
                });
                return true;
            case R.id.review_last_class_id:
                Intent review_intent = new Intent(AttendanceActivity.this, ReviewLastClassAttendanceActivity.class);
                review_intent.putExtra("table_name", TABLE_NAME);
                startActivity(review_intent);
                return true;
            case R.id.add_new_student_id:
                AlertDialog.Builder builder = new AlertDialog.Builder(AttendanceActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.insert_roll_layout, null);
                builder.setView(mView);
                final AlertDialog dialog = builder.create();
                dialog.show();

                //process for edit text
                final EditText roll = mView.findViewById(R.id.roll_edit_id);
                Button mButton = mView.findViewById(R.id.add_button_id);
                mButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(roll.getText()!= null){
                            String rolls = roll.getText().toString().trim();
                            database_helper.AddStudent(TABLE_NAME, rolls);
                            //student_adapter.notifyDataSetChanged();
                            dialog.dismiss();
                            finish();
                            startActivity(getIntent());

                        } else {
                            Toast.makeText(AttendanceActivity.this, "Please insert roll first!....",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                //database_helper.AddStudent(TABLE_NAME);
                return true;
            case R.id.filter_by_id:
                return true;
            case R.id.highest_att_id:
                Collections.sort(dataUsers, new Comparator<DataUser>() {
                    @Override
                    public int compare(DataUser o1, DataUser o2) {
                        Double p1 = Double.parseDouble(o1.getPofattendance());
                        Double p2 = Double.parseDouble(o2.getPofattendance());
                        return p2.compareTo(p1);
                    }
                });
                student_adapter.notifyDataSetChanged();
                return true;
            case R.id.lowest_att_id:
                Collections.sort(dataUsers, new MyComparetor());
                student_adapter.notifyDataSetChanged();
                return true;
            case R.id.summary_id:
                createAlertDialog();
                return true;
            case R.id.create_table_id:
                Intent intent = new Intent(AttendanceActivity.this, HtmlActivity.class);
                intent.putExtra("table_name", TABLE_NAME);
                startActivity(intent);
                return true;
            case R.id.export_id:
                getPermission();
                //createPDF();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void Initialize() {
        InitializeAppData();
        InitializeUI();
        InitializeUIData();
    }

    private void InitializeAppData() {

    }

    private void InitializeUI() {
        listView = findViewById(R.id.list_id);
        floatingActionButton = findViewById(R.id.fab_add_id);
    }

    private void InitializeUIData() {
        String table_name = "attendance_table_";
        Bundle bundle = this.getIntent().getExtras();
        String course_id = bundle.getString("Course_ID");
        table_name = table_name + course_id;
        TABLE_NAME = table_name;
        Course_id = course_id;
        //Log.d("TAG", "table name: "+ table_name);
        dataUsers = new ArrayList<>(0);
        Cursor cursor = database_helper.AttendacneSummary(table_name);
        int latest_col = cursor.getColumnCount()-1;
        if (cursor.getCount()!=0){
            while (cursor.moveToNext()){
                String row_id = cursor.getString(cursor.getColumnIndex("id"));
                String roll = cursor.getString(cursor.getColumnIndex("roll_no"));
                String p_att =cursor.getString(cursor.getColumnIndex("p_att"));
                String marks = cursor.getString(cursor.getColumnIndex("marks"));
                StringBuilder recent = new StringBuilder();
                recent.append("R: ");
                for(int i = latest_col; i>4 && latest_col-i <= 7; i--){
                    if(cursor.getString(i).equals("1")){
                        recent.append("P");
                    }
                    else
                        recent.append("A");
                }
                String r = recent.toString();
                //add to list item
                dataUsers.add(new DataUser(row_id, roll, marks, p_att, r));
            }
        }
        student_adapter = new Student_adapter(this, R.layout.single_student_designview, dataUsers);
        listView.setAdapter(student_adapter);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case STORAGE_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED){
                    //permission granted from popup
                    createPDF();
                } else {
                    //permission denied
                    Toast.makeText(this,"permission denied ...\nplease give permission to save pdf", Toast.LENGTH_LONG).show();
                }
        }
    }

    private void getPermission(){
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                //request permission
                String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permissions, STORAGE_CODE);
            } else {
                //permission already granted
                createPDF();
            }
        } else {
            createPDF();
        }


    }

    private void createPDF() {
        Document document = new Document(PageSize.A4);
        PdfPTable pdfPTable = new PdfPTable(4);
        pdfPTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        Cursor cursor = database_helper.AttendacneSummary(TABLE_NAME);
        int total_class = cursor.getColumnCount()-5;
        pdfPTable.addCell("Roll No.");
        pdfPTable.addCell("Attendance (Out of "+total_class+")");
        pdfPTable.addCell("% of Attendance");
        pdfPTable.addCell("Obtained Marks (Out of 8)");
        //for table header
        pdfPTable.setHeaderRows(1);
        PdfPCell[] cells = pdfPTable.getRow(0).getCells();
        for(int i=0; i<cells.length; i++){
            cells[i].setBackgroundColor(BaseColor.GRAY);
        }

        if(cursor != null){
            while (cursor.moveToNext()){
                String roll = cursor.getString(cursor.getColumnIndex("roll_no"));
                String attend = cursor.getString(cursor.getColumnIndex("attend"));
                String p_att = cursor.getString(cursor.getColumnIndex("p_att"));
                String marks = cursor.getString(cursor.getColumnIndex("marks"));
                pdfPTable.addCell(roll);
                pdfPTable.addCell(attend);
                pdfPTable.addCell(p_att+"%");
                pdfPTable.addCell(marks);
            }
        }
        cursor.close();
        //construct file name
        String file_name ="";
        String course_no="";
        String dept ="";
        String series ="";
        String section ="";
        Cursor header = database_helper.showSingleCourse(Course_id);
        while (header.moveToNext()){
            dept = header.getString(header.getColumnIndex("dept"));
            series = header.getString(header.getColumnIndex("series"));
            section = header.getString(header.getColumnIndex("section"));
            course_no = header.getString(header.getColumnIndex("course_name"));
            file_name = course_no+" Series: "+dept+"'"+series+" ( Section : "+section+" ).pdf";
            //file_name = file_name.replaceAll("\\s","");
        }
        header.close();
        try {
            File folder = new File(Environment.getExternalStorageDirectory() + File.separator + "AttendanceApp");
            if(!folder.exists()){
                folder.mkdirs();
            }
            File file = new File(Environment.getExternalStorageDirectory().getPath() +"/AttendanceApp/"+file_name);
            if( !file.exists()){
                file.createNewFile();
            }
            PdfWriter.getInstance(document, new FileOutputStream(file));
        } catch (DocumentException e) {
            e.printStackTrace();
            Log.d("exception", "ex:"+e);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.d("exception", "ex:"+e);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("exception", "ex:"+e);
        }
        document.open();
        //construct heading
        Paragraph heaven = new Paragraph("Heaven's light is our guide\n", new Font(Font.FontFamily.HELVETICA,10,Font.ITALIC));
        Paragraph university = new Paragraph("Rajshahi University of Engineering & Technology\n", new Font(Font.FontFamily.HELVETICA,12,Font.BOLD));
        Paragraph course = new Paragraph("Course No.: "+ course_no+"\n", new Font(Font.FontFamily.TIMES_ROMAN,12, Font.NORMAL));
        Paragraph series_dept = new Paragraph("Series: "+dept+"'"+ series+ " (Section: "+ section+")" +"\n\n", new Font(Font.FontFamily.TIMES_ROMAN,12, Font.ITALIC));
        //set middle
        heaven.setAlignment(Element.ALIGN_CENTER);
        university.setAlignment(Element.ALIGN_CENTER);
        course.setAlignment(Element.ALIGN_CENTER);
        series_dept.setAlignment(Element.ALIGN_CENTER);
        try {
            document.add(heaven);
            document.add(university);
            document.add(course);
            document.add(series_dept);
            document.add(pdfPTable);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        document.close();
        Toast.makeText(this, "pdf created in root folder", Toast.LENGTH_LONG).show();
    }

    private void createAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(AttendanceActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.summary_layout, null);
        TextView course_view = mView.findViewById(R.id.sum_course_id);
        TextView series_view = mView.findViewById(R.id.sum_series_id);
        TextView total_class_view = mView.findViewById(R.id.sum_total_class_id);
        TextView average_view = mView.findViewById(R.id.sum_average_id);

        //get data
        Cursor course_info = database_helper.showSingleCourse(Course_id);
        while (course_info.moveToNext()){
            String dept = course_info.getString(course_info.getColumnIndex("dept"));
            String series = course_info.getString(course_info.getColumnIndex("series"));
            String section = course_info.getString(course_info.getColumnIndex("section"));
            String course_no = course_info.getString(course_info.getColumnIndex("course_name"));
            course_view.setText("Course No.: "+ course_no);
            series_view.setText("Series: "+dept+"'"+series+" ( Section: "+ section+" )");
        }
        course_info.close();
        Cursor class_info = database_helper.AttendacneSummary(TABLE_NAME);
        Integer total_class = class_info.getColumnCount()-5;
        Integer num_of_std = class_info.getCount();
        total_class_view.setText("Total number of class: "+ total_class);
        Integer sum = 0;
        if (class_info != null){
            while (class_info.moveToNext()){
                Integer attend = Integer.parseInt(class_info.getString(class_info.getColumnIndex("attend")));
                sum = sum + attend;
            }
        }
        class_info.close();
        double average = (sum * 100.0)/(total_class*num_of_std*1.0);
        average_view.setText("Average % of Attendance: "+ round(average, 2)+"%");
        builder.setView(mView);
        builder.setPositiveButton("close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }
    //for round
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    //comparator
    public class MyComparetor implements Comparator<DataUser> {
        @Override
        public int compare(DataUser o1, DataUser o2) {
            Double p1 = Double.parseDouble(o1.getPofattendance());
            Double p2 = Double.parseDouble(o2.getPofattendance());
            return p1.compareTo(p2);
        }
    }

}
