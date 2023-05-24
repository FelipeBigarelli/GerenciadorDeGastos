package com.example.gerenciadordegastos;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class SobreActivity extends AppCompatActivity {

    public static void sobre(AppCompatActivity activity) {
        Intent intent = new Intent(activity, SobreActivity.class);

        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autoria_app);
    }

    private void cancelar(){
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sobre, menu);

        return true;
    }



    @Override
    public void onBackPressed() {
        cancelar();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.menuItemVoltarSobre:
                cancelar();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}