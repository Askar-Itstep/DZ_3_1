package com.example.dz_3_1;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private static  List<Human> humans = new ArrayList<>();;
    private static Human human;
    private static ListView lvHumans;

    private final int SIZE = 3; //для img.png - по 3 для кажд. пола
    private static Bitmap[] bmpMens;
    private static Bitmap[] bmpWomens;

    private int nmrlColor = Color.rgb(0xED, 0xE2, 0x75);
    private int slctColor = Color.rgb(0xE2, 0xA7, 0x6F);
    private int curPos = -1;
    private static View curView = null;
    private final static String TAG = "===MainActivity===";
    // для установ. даты
    private int year;
    private int month;
    private int day;
    private  static ArrayAdapter<Human> adapter;
    private static String json = "";
    private static final String FILE_NAME = "employees.txt";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lvHumans = findViewById(R.id.lvHumans);
        this.getAssetManager();

        //установ. адаптера -----------------------------------------------------------
        adapter = new ArrayAdapter<Human>(this, R.layout.person_item, R.id.tvName, humans){
            @Override
            public View getView(int position,  View convertView,  ViewGroup parent) {

                View view = (View)super.getView(position, convertView, parent);

                human = this.getItem(position); //this = adapter
                //получение пуст. виджетов
                ImageView imageViewMan = (ImageView)view.findViewById(R.id.ivMan);
                TextView tvName = view.findViewById(R.id.tvName);
                TextView tvLastName = view.findViewById(R.id.tvLastname);
                TextView tvBirthday = view.findViewById(R.id.tvBirthday);

                //--	Запись	в	виджeты
                if(human.gender)
                    imageViewMan.setImageBitmap(bmpMens[human.id % SIZE]);
                else
                    imageViewMan.setImageBitmap(bmpWomens[human.id % SIZE]);
                tvName.setText(human.firstName);
                tvLastName.setText(human.lastName);
                tvBirthday.setText(human.getBirthDayString());

                //от повтора отображ. ч/з кажд 8 полос
                if(position == curPos){
                    view.setBackgroundColor(slctColor);
                    curView = view;
                }else
                    view.setBackgroundColor(nmrlColor);
                return view;
            }
        };
//после запуска файла еще нет! его надо созд. (menu->create)
        File file = this.getFileStreamPath(FILE_NAME);  //созд-ся в текущ. папке
        if(file.exists() && !json.isEmpty()) {
            Log.d(TAG, "Файл найден");
            Toast.makeText(this, "Файл найден", Toast.LENGTH_SHORT).show();
            loadFileEmployees();

            lvHumans.setAdapter(adapter);
            //обработ. выбора элем.
            lvHumans.setOnItemClickListener((parent, view, position, id) -> {
                //от повтора отображ. ч/з кажд 8 полос
                if(curPos != -1)
                    curView.setBackgroundColor(nmrlColor);

                curPos = position;
                curView = view;
                curView.setBackgroundColor(slctColor);
            });
        }
        else {
            Log.d(TAG, "Файл не найден");
            Toast.makeText(this, "Файл не найден! Загрузка из другого источника", Toast.LENGTH_SHORT).show();
        }
    }

    private void getAssetManager() {
        AssetManager manager = this.getAssets();
        String[] filenameMen = new String[]{"men.png", "men2.png", "men3.png"};
        String [] filenameWomen = new String[]{"women.png","women2.png", "women3.png"};
        InputStream[] inputStreamMen = new InputStream[SIZE];
        InputStream[] inputStreamWomen = new InputStream[SIZE];
        bmpMens = new Bitmap[SIZE];
        bmpWomens = new Bitmap[SIZE];
        try {
            for(int i = 0; i < SIZE; i++) {
                inputStreamMen[i] = manager.open(filenameMen[i]);   //каждому свое
                inputStreamWomen[i] = manager.open(filenameWomen[i]);

                bmpMens[i] = BitmapFactory.decodeStream(inputStreamMen[i]);
                bmpWomens[i] = BitmapFactory.decodeStream(inputStreamWomen[i]);

                inputStreamMen[i].close();
                inputStreamWomen[i].close();
            }
        }catch (IOException ex) {
            Log.e(TAG, ex.getMessage());
        } catch(Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }
    private void setHumans(){
        //потом надо изменить-выборку из файла
        humans.add(new Human("Alex", "Alexin", true, Human.makeCalendar(1, 1, 2000)));
        humans.add(new Human("Bob", "Boricov", true, Human.makeCalendar(12, 2, 2001)));
        humans.add(new Human("Cindy", "Cox", false, Human.makeCalendar(23, 3, 2003)));
        humans.add(new Human("Dima", "Dubov", true, Human.makeCalendar(30, 4, 2004)));
        humans.add(new Human("Elka", "Palkova", false, Human.makeCalendar(31, 5, 2005)));
        humans.add(new Human("Foma", "Feklov", true, Human.makeCalendar(2, 6, 2006)));
        humans.add(new Human("Gena", "Gorin", true, Human.makeCalendar(3, 7, 2007)));

    }
    //меню
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
//обработка меню
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            //-------Adding--------------------------------------------------------
            case R.id.action_add: {
                AlertDialog.Builder dialogBuilder =new AlertDialog.Builder(this,
                                                            android.R.style.Theme_Holo_Light_Dialog);
                LayoutInflater inflater = getLayoutInflater();
                View view = inflater.inflate(R.layout.dialog_edit, null, false);
                //builder создает view
                dialogBuilder.setView(view);
                dialogBuilder.setTitle("New employee");
                //установ. на кнопку ОК
                dialogBuilder.setPositiveButton("Add", (dialog, which) -> {
                    EditText etName = view.findViewById(R.id.etName);
                    EditText etLastname = view.findViewById(R.id.etLastname);
                    EditText etGender = view.findViewById(R.id.etGender);
                    ImageView imageView = view.findViewById(R.id.ivHuman);
                    DatePicker picker = view.findViewById(R.id.datePicker);
                    Human candidat = null;

                    //снятие значен. из полей
                    String name = etName.getText().toString();
                    String lastname = etLastname.getText().toString();
                    String gender = etGender.getText().toString();  //ставить спиннер???
                    boolean genderBool = false;
                    Calendar birthDay = Calendar.getInstance();

                    //созд. кандидата
                    if(gender.equals("men") || gender.contains("муж")) {
                        genderBool = true;
                        imageView.setImageBitmap(bmpMens[(Human.topId+1) % SIZE ]);
                    }
                    else
                        imageView.setImageBitmap(bmpWomens[(Human.topId+1) % SIZE]);
                    if(year == 0 || month == 0 || day == 0){
                        //получ. текущ. дату
                        Calendar C = Calendar.getInstance();
                        year = C.get(Calendar.YEAR);
                        month = C.get(Calendar.MONTH);
                        day = C.get(Calendar.DAY_OF_MONTH);
                    }
                    picker.init(year, month, day, new DatePicker.OnDateChangedListener() {
                        @Override
                        public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            birthDay.set(year, month, day);
                        }
                    });
                    candidat = new Human(name, lastname, genderBool, birthDay);
                //далее надо добавить в список работников (а еще далее запись в файл)
                    if(!candidat.isFieldEmpty()) {
                        humans.add(candidat);
                        adapter.notifyDataSetChanged();
                    }
                    humans.forEach(h->Log.d(TAG, "humans1: "+h.firstName));
                });
                dialogBuilder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {  }});

                AlertDialog dialog = dialogBuilder.create();
                dialog.show();
                humans.forEach(h->Log.d(TAG, "humans2: "+h.firstName));
            }
            break;
//---------Edit----------------------------------------------------------------
            case R.id.action_edit: {
//                if (curView == null) {
                if (curPos == -1) { //тоже работает!!
                    Toast.makeText(this, "Выберите сотрудника", Toast.LENGTH_SHORT).show();
                    break;
                }
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this, android.R.style.Theme_Holo_Light_Dialog);
                LayoutInflater inflater = getLayoutInflater();
                View view = inflater.inflate(R.layout.dialog_edit, null, false);
                dialogBuilder.setView(view);
                dialogBuilder.setTitle("Edit employee");

                EditText etName = view.findViewById(R.id.etName);
                EditText etLastname = view.findViewById(R.id.etLastname);
                EditText etGender = view.findViewById(R.id.etGender);
                ImageView imageView = view.findViewById(R.id.ivHuman);
                DatePicker picker = view.findViewById(R.id.datePicker);

                Human currHuman = (Human) lvHumans.getAdapter().getItem(curPos);

                Log.d(TAG, "id=" + currHuman.id);
                etName.setText(currHuman.firstName);
                etLastname.setText(currHuman.lastName);
                if (currHuman.gender) {
                    etGender.setText("men");
                    imageView.setImageBitmap(bmpMens[id % SIZE]);
                } else {
                    etGender.setText("women");
                    imageView.setImageBitmap(bmpWomens[id % SIZE]);
                }
                dialogBuilder.setPositiveButton("Edit", (dialog, which) -> {
                    //исходн. данные из person_item в dialog_edit
                    String newName = etName.getText().toString();
                    String newLastname = etLastname.getText().toString();
                    String gender = etGender.getText().toString();
                    boolean genderBool = false;
                    Calendar birthDay = Calendar.getInstance();

                    if (year == 0 || month == 0 || day == 0) {
                        //получ. текущ. дату
                        Calendar C = Calendar.getInstance();
                        year = C.get(Calendar.YEAR);
                        month = C.get(Calendar.MONTH);
                        day = C.get(Calendar.DAY_OF_MONTH);
                    }
                    if (!newName.isEmpty())
                        currHuman.firstName = newName;
                    if (!newLastname.isEmpty())
                        currHuman.lastName = newLastname;

                    picker.init(year, month, day, new DatePicker.OnDateChangedListener() {
                        @Override
                        public void onDateChanged(DatePicker view1, int year, int monthOfYear, int dayOfMonth) {
                            birthDay.set(year, month, day);
                        }
                    });
                    currHuman.birthDay = birthDay;
                });

                dialogBuilder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                });

                AlertDialog dialog = dialogBuilder.create();
                dialog.show();
            }
                break;
//----------Delete---------------------------------------------------------
            case R.id.action_remove: {
                if (curView == null) {
                    Toast.makeText(this, "Выберите сотрудника", Toast.LENGTH_SHORT).show();
                    break;
                }
                Human currHuman = (Human) lvHumans.getAdapter().getItem(curPos);
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this, android.R.style.Theme_Holo_Light_Dialog);
                dialogBuilder.setMessage(currHuman.lastName+ " "+currHuman.firstName);

                Log.d(TAG, "delete "+currHuman.lastName);
                dialogBuilder.setTitle("Удалить данного сотрудника?");
                dialogBuilder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        humans.remove(currHuman);
                        adapter.notifyDataSetChanged();
                    }
                });
                dialogBuilder.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { }
                });

                AlertDialog dialog = dialogBuilder.create();
                dialog.show();

            }
            break;
//-------Запись в файл -----------------------------------------------------------
            case R.id.action_save_file: {
                json = new Gson().toJson(humans);
                try {
                    FileOutputStream fileOutputStream = this.openFileOutput(FILE_NAME, Context.MODE_APPEND);

                    try {
                        fileOutputStream.write(json.getBytes());
                        Toast.makeText(this, "файл сохранен", Toast.LENGTH_SHORT).show();
                        fileOutputStream.close();
                        return true;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
            break;
 // ------ Чтение из файла ---------------------------------------------------------------
            case R.id.action_read_file:
                loadFileEmployees();
                break;
//-------- загруз из объекта -----------------------------------------------------------
            case R.id.action_load_humans:{
                this.setHumans();
                Log.d(TAG, "Список загружен динамически");
                Toast.makeText(this, "Список загружен", Toast.LENGTH_SHORT).show();
                lvHumans.setAdapter(adapter);
                lvHumans.setOnItemClickListener((parent, view, position, i) -> {
                    //от повтора отображ. ч/з кажд 8 полос
                    if(curPos != -1)
                        curView.setBackgroundColor(nmrlColor);

                    curPos = position;
                    curView = view;
                    curView.setBackgroundColor(slctColor);
                });
            }
            break;
        }

        return super.onOptionsItemSelected(item);
    }

    //загрузка из фaйла в объект humans -------------------------------------------------
    private void loadFileEmployees() {
        if(json.isEmpty()){
            Log.d(TAG, "json is Empty");
            Toast.makeText(this, "Сначала сохраните файл!", Toast.LENGTH_SHORT).show();
            return;
        }
        FileInputStream fileInputStream = null;
        InputStreamReader streamReader = null;
        try {
            fileInputStream = this.openFileInput(FILE_NAME);
            streamReader  = new InputStreamReader(fileInputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Type listType = new TypeToken<ArrayList<Human>>(){}.getType();
        Log.d(TAG, json);
        humans = new Gson().fromJson(json, listType);
        try {
            streamReader.close();
            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Toast.makeText(this, "файл прочитан", Toast.LENGTH_SHORT).show();

        File file = new File(FILE_NAME);
//        Log.d(TAG, "Путь файла: "+ getFileStreamPath(FILE_NAME));
        for (String str: fileList())
            Log.d(TAG, "Файлы каталога: "+ str);
    }
}
