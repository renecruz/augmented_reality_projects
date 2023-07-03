package mx.uaemex.arapp;

import android.app.ListActivity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class table extends AppCompatActivity {
    TableLayout table_layout;
    private static final String DB_NAME = "TablaPeriodica.sqlite";
    //������� ��������� �������� ������� ���� ����� �� �����������
    private static final String TABLE_NAME = "ElementoQuimico";
    private static final String FRIEND_ID = "_NoAtomico";
    private static final String FRIEND_NAME = "Elemento";
    private static final String FRIEND_SIMBOL = "SimboloAtomico";
    private static final String FRIEND_PESO = "PesoAtomico";

    private SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);

        table_layout = (TableLayout) findViewById(R.id.tableLayout1);
        ExternalDbOpenHelper dbOpenHelper = new ExternalDbOpenHelper(this, DB_NAME);
        database = dbOpenHelper.openDataBase();
        MostrarElementos();

    }

    private void MostrarElementos() {

        Cursor friendCursor = database.query(TABLE_NAME,
                new String[]
                        {FRIEND_ID, FRIEND_NAME, FRIEND_SIMBOL, FRIEND_PESO},
                null, null, null, null
                , FRIEND_ID);
        int rows = friendCursor.getCount();
        //int cols = friendCursor.getColumnCount();
        int cols = 4;
        friendCursor.moveToFirst();

        for (int i = 0; i < rows; i++) {

            TableRow row = new TableRow(this);
            row.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));

            // inner for loop
            for (int j = 0; j < cols; j++) {

                TextView tv = new TextView(this);
                //tv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                //ViewGroup.LayoutParams.WRAP_CONTENT));
                tv.setBackgroundResource(R.drawable.cell_shape);
                tv.setGravity(Gravity.CENTER);
                tv.setTextSize(10);
                tv.setPadding(0, 5, 0, 5);

                tv.setText(friendCursor.getString(j));

                row.addView(tv);

            }

            friendCursor.moveToNext();

            table_layout.addView(row);

        }

    }

}
