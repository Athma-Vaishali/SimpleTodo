package com.example.simpletodo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String KEY_ITEM_TEXT ="item_text";
    public static final String KEY_ITEM_POSITION ="item_position";
    public static final int EDIT_TEXT_CODE =20;

    List<String> items;

    Button BtnAdd;
    EditText TxtNewitem;
    RecyclerView ScrollItems;
    ItemsAdapter itemsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BtnAdd = findViewById(R.id.BtnAdd);
        TxtNewitem = findViewById(R.id.TxtNewitem);
        ScrollItems = findViewById(R.id.ScrollItems);

        loadItems();

        ItemsAdapter.OnLongClickListener OnLongClickListener=new ItemsAdapter.OnLongClickListener(){
            @Override
            public void onItemLongClicked(int position) {
                items.remove(position);
                itemsAdapter.notifyItemRemoved(position);
                Toast.makeText(getApplicationContext(),"Item removed",Toast.LENGTH_SHORT).show();
                saveItems();
            }
        };

        ItemsAdapter.OnClickListener OnClickListener = new ItemsAdapter.OnClickListener() {
            @Override
            public void onItemClicked(int position) {
                Intent i = new Intent(MainActivity.this,EditActivity.class);
                i.putExtra(KEY_ITEM_TEXT,items.get(position));
                i.putExtra(KEY_ITEM_POSITION,position);
                startActivityForResult(i,EDIT_TEXT_CODE);
            }
        };
        itemsAdapter = new ItemsAdapter(items, OnLongClickListener,OnClickListener);
        ScrollItems.setAdapter(itemsAdapter);
        ScrollItems.setLayoutManager(new LinearLayoutManager(this));

        BtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String todoItem = TxtNewitem.getText().toString();
                items.add(todoItem);
                itemsAdapter.notifyItemInserted(items.size()-1);
                TxtNewitem.setText("");
                Toast.makeText(getApplicationContext(),"New item added",Toast.LENGTH_SHORT).show();
                saveItems();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==EDIT_TEXT_CODE && resultCode==RESULT_OK){
            String text= data.getStringExtra(KEY_ITEM_TEXT);
            int position = data.getExtras().getInt(KEY_ITEM_POSITION);
            items.set(position,text);
            itemsAdapter.notifyItemChanged(position);
            saveItems();
            Toast.makeText(getApplicationContext(),"Item Updated Successfully!",Toast.LENGTH_SHORT).show();
        }else{
            Log.w("MainActivity","Unknown call to onActivityResult");
        }
    }

    private File getDataFile(){
        return new File(getFilesDir(),"data.txt");
    }

    private void loadItems(){
        try {
            items= new ArrayList<>(FileUtils.readLines(getDataFile(), Charset.defaultCharset()));
        } catch (IOException e) {
            Log.e("MainActivity","Error reading items",e);
            items = new ArrayList<>();
        }
    }

    private void saveItems(){
        try {
            FileUtils.writeLines(getDataFile(),items);
        } catch (IOException e) {
            Log.e("MainActivity","Error writing items",e);
        }
    }
}