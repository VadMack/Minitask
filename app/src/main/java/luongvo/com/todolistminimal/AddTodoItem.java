package luongvo.com.todolistminimal;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.rengwuxian.materialedittext.validation.RegexpValidator;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import luongvo.com.todolistminimal.Database.TodoListContract;
import luongvo.com.todolistminimal.Database.TodoListDbHelper;
import luongvo.com.todolistminimal.Utils.MyDateTimeUtils;

public class AddTodoItem extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    @BindView(R.id.todoEditText) MaterialEditText materialTextInput;
    @BindView(R.id.buttonSetDate) Button buttonSetDate;
    @BindView(R.id.buttonSetTime) Button buttonSetTime;
    @BindView(R.id.reminderSwitch) Switch reminderSwitch;
    @BindView(R.id.reminderText) TextView reminderText;
    @BindView(R.id.addTodoBtn) FloatingActionButton addTodoBtn;

    TodoListDbHelper dbHelper;

    ToDoItem toDoItem;
    MyDateTimeUtils dateTimeUtils;

    String content;
    String date;
    String time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_todo_item);
        initializeComponents();
    }

    private void initializeComponents() {
        getSupportActionBar().setTitle(R.string.add_todo_item);
        ButterKnife.bind(this);
        dbHelper = new TodoListDbHelper(this);

        dateTimeUtils = new MyDateTimeUtils();
        date ="";
        time ="";

        reminderSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    buttonSetDate.setVisibility(View.GONE);
                    buttonSetTime.setVisibility(View.GONE);
                    reminderText.setVisibility(View.GONE);
                    reminderText.setText(getString(R.string.reminder_set_at));
                    date = "";
                    time = "";
                }
                else {
                    buttonSetDate.setVisibility(View.VISIBLE);
                    buttonSetTime.setVisibility(View.VISIBLE);
                    reminderText.setVisibility(View.VISIBLE);
                }
            }
        });

        buttonSetDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        AddTodoItem.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.vibrate(true);
                dpd.dismissOnPause(true);
                dpd.show(getFragmentManager(), "DatepickerDialog");
            }
        });

        buttonSetTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                date = dateTimeUtils.fillDateIfEmpty(date);
                Calendar now = Calendar.getInstance();
                TimePickerDialog tpd = TimePickerDialog.newInstance(
                        AddTodoItem.this,
                        now.get(Calendar.HOUR_OF_DAY),
                        now.get(Calendar.MINUTE),
                        true
                );
                tpd.vibrate(true);
                tpd.dismissOnPause(true);
                tpd.show(getFragmentManager(), "TimepickerDialog" );
            }
        });

        addTodoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItemToDatabase();
            }
        });

    }

    private void addItemToDatabase() {
        if (!materialTextInput.validateWith(
                new RegexpValidator("String must not be empty", "^(?!\\s*$).+"))) {
            Toast.makeText(this, "Empty task detected! No task added!", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        String reminderDate = date + " " + time;
        content = materialTextInput.getText().toString();

        if (reminderDate.equals(" ")) {
            toDoItem = new ToDoItem(content, false, null, false);
            values.put(TodoListContract.TodoListEntries.COLUMN_NAME_CONTENT, toDoItem.getContent());
            values.put(TodoListContract.TodoListEntries.COLUMN_NAME_DONE, toDoItem.getDone());
            values.putNull(TodoListContract.TodoListEntries.COLUMN_NAME_REMINDERDATE);
        }
        else {
            toDoItem = new ToDoItem(content, false, reminderDate, true);
            values.put(TodoListContract.TodoListEntries.COLUMN_NAME_CONTENT, toDoItem.getContent());
            values.put(TodoListContract.TodoListEntries.COLUMN_NAME_DONE, toDoItem.getDone());
            values.put(TodoListContract.TodoListEntries.COLUMN_NAME_REMINDERDATE, toDoItem.getReminderDate());
        }
        long newRowId = db.insert(TodoListContract.TodoListEntries.TABLE_NAME, null, values);
        Log.d("Item :", toDoItem.toString());
    }


    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        if (dateTimeUtils.checkInvalidDate(year, monthOfYear, dayOfMonth)){
            AlertDialog alertDialog = new AlertDialog.Builder(AddTodoItem.this).create();
            alertDialog.setTitle("Date not valid!");
            alertDialog.setIcon(R.drawable.ic_warning_black_24dp);
            alertDialog.setMessage("You are selecting a time a point of time in the past!");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alertDialog.show();
            return;
        }

        date = dateTimeUtils.dateToString(year, monthOfYear, dayOfMonth);
        Log.d("Yes", date);
        reminderText.setText(getString(R.string.reminder_set_at) + " " + date + " " + time);
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        if (date.equals(dateTimeUtils.fillDateIfEmpty("")) && dateTimeUtils.checkInvalidTime(hourOfDay, minute)) {
            AlertDialog alertDialog = new AlertDialog.Builder(AddTodoItem.this).create();
            alertDialog.setTitle("Time not valid!");
            alertDialog.setMessage("You are selecting a point of time in the past!");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alertDialog.show();
            return;
        }
        time = dateTimeUtils.timeToString(hourOfDay, minute);
        Log.d("Yes", time);
        reminderText.setText(getString(R.string.reminder_set_at) + " " + date + " " + time);
    }
}
