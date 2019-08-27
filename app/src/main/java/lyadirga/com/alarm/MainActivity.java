package lyadirga.com.alarm;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {

    private ListView liste;
    private Veritabani veritabani = new Veritabani(this);
    private CustomAdapter adapter;
    public static final int ALARM_SET = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        liste = (ListView) findViewById(R.id.liste);

        //Mevcut alarmlar ekranda gösterilecek
        adapter = new CustomAdapter(this, veritabani.getAlarms());
        liste.setAdapter(adapter);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startAlarmDetailsActivity(-1);
            }
        });

        //FloatingActionButton u gizlemek için
        liste.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //firstVisibleItem görünen ilk itemin indeksi
                //visibleItemCount görünen itemin sayısı
                //totalItemCount toplam item sayısı
                if (firstVisibleItem > 0) {
                    fab.hide();
                }
                else {
                    fab.show();
                }
            }
        });
    }

    public void startAlarmDetailsActivity(long id) {
        Intent intent = new Intent(this, AlarmDetayActivity.class);
        intent.putExtra("id", id);
        startActivityForResult(intent, ALARM_SET);


    }

    public void deleteAlarm(long id) {
        final long alarmId = id;

        new AlertDialog.Builder(MainActivity.this)
                .setIcon(android.R.drawable.ic_delete)
                .setTitle("Sil")
                .setMessage("Alarm silinecek, onaylıyor musunuz?")
                .setNegativeButton("Hayır", null)
                .setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AlarmManagerHelper.cancelAlarms(MainActivity.this);
                        veritabani.deleteAlarm(alarmId);
                        adapter.setAlarms(veritabani.getAlarms());
                        adapter.notifyDataSetChanged();
                        AlarmManagerHelper.setAlarms(MainActivity.this);
                    }
                }).show();

    }

    public void setAlarmEnabled(long id, boolean isEnabled) {

        AlarmManagerHelper.cancelAlarms(this);

        AlarmModel model = veritabani.getAlarm(id);
        model.isEnabled = isEnabled;
        veritabani.updateAlarm(model);

        AlarmManagerHelper.setAlarms(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode==ALARM_SET) {
            adapter.setAlarms(veritabani.getAlarms());
            adapter.notifyDataSetChanged();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
