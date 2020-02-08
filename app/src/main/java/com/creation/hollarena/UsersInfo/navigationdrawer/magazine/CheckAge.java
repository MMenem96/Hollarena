package com.creation.hollarena.UsersInfo.navigationdrawer.magazine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.creation.hollarena.R;
import com.creation.hollarena.UsersInfo.navigationdrawer.hollarenaalterego.HollarenaAlteregoActivity;


public class CheckAge extends AppCompatActivity {

    private RelativeLayout rlBooks,rlSports,rlfashion,rlPolitics;
    private TextView tv_fashion,tv_books,tv_sport,tv_politics;
    private Button btnNo,btnYes,btnPayStock;
    SharedPreferences categories,prefs;
    Boolean isClicked_book=true ,isClicked_fashion=true ,isClicked_politics=true, isClicked_sports=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_age);

        rlBooks=(RelativeLayout)findViewById(R.id.rlbk);
        rlfashion=(RelativeLayout)findViewById(R.id.rl_fation);
        rlSports=(RelativeLayout)findViewById(R.id.rl_sports);
        rlPolitics=(RelativeLayout)findViewById(R.id.rl_politics);

        tv_books=(TextView)findViewById(R.id.tv_cat_books);
        tv_fashion=(TextView)findViewById(R.id.tv_cat_fation);
        tv_politics=(TextView)findViewById(R.id.tv_cat_politics);
        tv_sport=(TextView)findViewById(R.id.tv_cat_sports);

        btnNo=(Button)findViewById(R.id.btn_no);
        btnYes=(Button)findViewById(R.id.btn_yes);
        btnPayStock=(Button)findViewById(R.id.btn_upgrade);

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prefs=getSharedPreferences("previouslyStarted",MODE_PRIVATE);
                SharedPreferences.Editor prefsEditor=prefs.edit();
                prefsEditor.putString("previouslyStarted","no");
                categories = PreferenceManager.getDefaultSharedPreferences(CheckAge.this);
                SharedPreferences.Editor editor=categories.edit();
                editor.clear();
                editor.apply();
                startActivity(new Intent(CheckAge.this, MagazineActivity.class));
            }
        });

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categories = getSharedPreferences("categories", MODE_PRIVATE);
                String fashion = categories.getString("fashion", "");
                String books = categories.getString("books", "");
                String politics = categories.getString("politics", "");
                String sports = categories.getString("sports", "");
                if(fashion==null&&books==null&&politics==null&& sports==null){
                    Toast.makeText(getApplicationContext(),"Choose the categories you are interested in"
                            ,Toast.LENGTH_LONG).show();
                }
                else if (fashion!=null|| books!=null|| politics!=null|| sports!=null){
                    prefs=getSharedPreferences("previouslyStarted",MODE_PRIVATE);
                    SharedPreferences.Editor prefsEditor=prefs.edit();
                    prefsEditor.putString("previouslyStarted","yes");
                    startActivity(new Intent(CheckAge.this, HollarenaAlteregoActivity.class));
                }else{
                    startActivity(new Intent(getApplicationContext(),MagazineActivity.class));
                }

            }
        });


        rlfashion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isClicked_fashion==true){
                    Toast.makeText(getApplicationContext(),"fashion is added",Toast.LENGTH_LONG).show();
                    rlfashion.setBackgroundResource(R.drawable.rounded_corner_bl);
                    tv_fashion.setTextColor(Color.parseColor("#ffffff"));
                    categories=getSharedPreferences("categories",MODE_PRIVATE);
                    SharedPreferences.Editor editor=categories.edit();
                    editor.putString("fashion","Fashion");
                    editor.commit();
                    isClicked_fashion=false;
                }else{
                    Toast.makeText(getApplicationContext(),"fashion is removed",Toast.LENGTH_LONG).show();
                    categories = PreferenceManager.getDefaultSharedPreferences(CheckAge.this);
                    SharedPreferences.Editor editor=categories.edit();
                    editor.remove("fashion");
                    editor.apply();
                    isClicked_fashion=true;
                    rlfashion.setBackgroundResource(R.drawable.rounded_corner);
                    tv_fashion.setTextColor(Color.parseColor("#000000"));
                }
            }
        });


        rlPolitics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isClicked_politics==true){
                    rlPolitics.setBackgroundResource(R.drawable.rounded_corner_bl);
                    tv_politics.setTextColor(Color.parseColor("#ffffff"));
                    categories=getSharedPreferences("categories",MODE_PRIVATE);
                    SharedPreferences.Editor editor=categories.edit();
                    editor.putString("politics","Politics");
                    editor.commit();
                    isClicked_politics=false;
                }else{
                    categories = PreferenceManager.getDefaultSharedPreferences(CheckAge.this);
                    SharedPreferences.Editor editor=categories.edit();
                    editor.remove("politics");
                    editor.apply();
                    isClicked_politics=true;
                    rlPolitics.setBackgroundResource(R.drawable.rounded_corner);
                    tv_politics.setTextColor(Color.parseColor("#000000"));
                }
            }
        });


        rlSports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isClicked_sports==true){
                    rlSports.setBackgroundResource(R.drawable.rounded_corner_bl);
                    tv_sport.setTextColor(Color.parseColor("#ffffff"));
                    categories=getSharedPreferences("categories",MODE_PRIVATE);
                    SharedPreferences.Editor editor=categories.edit();
                    editor.putString("sports","Sports");
                    editor.commit();
                    isClicked_sports=false;
                }else{
                    categories = PreferenceManager.getDefaultSharedPreferences(CheckAge.this);
                    SharedPreferences.Editor editor=categories.edit();
                    editor.remove("sports");
                    editor.apply();
                    isClicked_sports=true;
                    rlSports.setBackgroundResource(R.drawable.rounded_corner);
                    tv_sport.setTextColor(Color.parseColor("#000000"));
                }
            }
        });


        rlBooks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isClicked_book==true){
                    rlBooks.setBackgroundResource(R.drawable.rounded_corner_bl);
                    tv_books.setTextColor(Color.parseColor("#ffffff"));
                    categories=getSharedPreferences("categories",MODE_PRIVATE);
                    SharedPreferences.Editor editor=categories.edit();
                    editor.putString("books","Books");
                    editor.commit();
                    isClicked_book=false;
                }else{
                    categories = PreferenceManager.getDefaultSharedPreferences(CheckAge.this);
                    SharedPreferences.Editor editor=categories.edit();
                    editor.remove("books");
                    editor.apply();
                    isClicked_book=true;
                    rlBooks.setBackgroundResource(R.drawable.rounded_corner);
                    tv_books.setTextColor(Color.parseColor("#000000"));
                }
            }
        });

        btnPayStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CheckAge.this,Pay_stock.class));
            }
        });

    }
}
