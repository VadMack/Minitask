package luongvo.com.todolistminimal;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import luongvo.com.todolistminimal.Database.TodoListContract;
import luongvo.com.todolistminimal.Database.TodoListDbHelper;
import luongvo.com.todolistminimal.Utils.MyDateTimeUtils;
import luongvo.com.todolistminimal.Utils.UpdateDatabase;

import static luongvo.com.todolistminimal.PageFragment.toDoItems;

public class AddTodoItem extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    // Bind components
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

    // Old content for edit function
    String oldContent = "";
    String oldReminder = "";
    Boolean oldHasReminder;
    Boolean oldDone;
    Boolean existingData;

    // rowID after adding into database
    private long newRowId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_todo_item);
        initializeComponents();
        existingData = loadDataIfExist();
    }

    // attach view with controllers
    private void initializeComponents() {
        getSupportActionBar().setTitle(R.string.add_todo_item);
        ButterKnife.bind(this);

        dbHelper = new TodoListDbHelper(this);

        dateTimeUtils = new MyDateTimeUtils();
        date ="";
        time ="";

        // if switch is check then reveal data and time picker
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
                // check if user chose date but didnt choose time
                if (time.equals("") && !date.equals("")) {
                    AlertDialog alertDialog = new AlertDialog.Builder(AddTodoItem.this).create();
                    alertDialog.setTitle(getString(R.string.time_error));
                    alertDialog.setMessage(getString(R.string.time_error_purpose));
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alertDialog.show();
                    return;
                }

                // get content input from user
                content = materialTextInput.getText().toString();
                // check if old content is the same as new content
                // if yes then user didn't do any action and should press back rather than add
                if ((oldContent.equals(content) || content == null) && oldReminder.equals(date + " " + time)){
                    Toast.makeText(AddTodoItem.this, "You made no change at all !?\n Press back to go back", Toast.LENGTH_SHORT).show();
                    return;
                }
                // no error found, start adding to database
                addItemToDatabase();
                // this check is used for update/edit a todoItem case
                // basically, after insert a new one, delete the old one, like trigger in database.
                if (existingData) {
                    UpdateDatabase updateDatabaseInstance = new UpdateDatabase();
                    updateDatabaseInstance.removeInDatabase(oldContent, oldReminder, AddTodoItem.this);
                    toDoItem = new ToDoItem(oldContent, oldDone, oldReminder, oldHasReminder);
                    toDoItems.remove(toDoItem);
                }
                // schedule a notification if date and time is set
                if (!(date+" "+time).equals(" "))
                    dateTimeUtils.ScheduleNotification(dateTimeUtils.getNotification(content, AddTodoItem.this),
                        AddTodoItem.this, (int)newRowId, date + " " + time);
                finish();
            }
        });

    }

    // load data passed from another activity, return success or not
    // this one is to distinguish between add and edit activity
    private Boolean loadDataIfExist() {
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras == null) return false;
        // get attributes from intent
        oldContent = extras.getString("content");
        oldReminder = extras.getString("reminder");
        oldHasReminder = extras.getBoolean("hasReminder");
        oldDone = extras.getBoolean("done");

        materialTextInput.setText(oldContent);
        // stop here if no reminder is set, else continue
        if (oldReminder.equals(" "))
            return true;
        // split string to get specific date and time
        date = oldReminder.split("\\s+")[0];
        time = oldReminder.split("\\s+")[1];
        // change text on screen and make date time picker visible
        reminderText.setText(getString(R.string.reminder_set_at) + " " + date + " " + time);
        reminderSwitch.setChecked(true);
        buttonSetDate.setVisibility(View.VISIBLE);
        buttonSetTime.setVisibility(View.VISIBLE);
        reminderText.setVisibility(View.VISIBLE);
        return true;
    }

    // this function is to add an item into database
    private void addItemToDatabase() {
        // first validate if the input is empty or not
        if (!materialTextInput.validateWith(
                new RegexpValidator("String must not be empty", "^(?!\\s*$).+"))) {
            Toast.makeText(this, "Empty task detected! No task added!", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        String reminderDate = date + " " + time;

        // when insert into database, also construct a new object for notifydatasetchanged()
        if (reminderDate.equals(" ")) { // no reminder
            toDoItem = new ToDoItem(content, false, " ", false);
            toDoItems.add(toDoItem);
            values.put(TodoListContract.TodoListEntries.COLUMN_NAME_CONTENT, toDoItem.getContent());
            values.put(TodoListContract.TodoListEntries.COLUMN_NAME_DONE, toDoItem.getDone());
            values.putNull(TodoListContract.TodoListEntries.COLUMN_NAME_REMINDERDATE);
        }
        else {  //  with reminder
            toDoItem = new ToDoItem(content, false, reminderDate, true);
            toDoItems.add(toDoItem);
            values.put(TodoListContract.TodoListEntries.COLUMN_NAME_CONTENT, toDoItem.getContent());
            values.put(TodoListContract.TodoListEntries.COLUMN_NAME_DONE, toDoItem.getDone());
            values.put(TodoListContract.TodoListEntries.COLUMN_NAME_REMINDERDATE, toDoItem.getReminderDate());
        }
        // insert into a row in database
        newRowId = db.insert(TodoListContract.TodoListEntries.TABLE_NAME, null, values);
    }


    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        // if the date is in the past tell user to choose again
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

        // set date value to the value user selected and change the text
        date = dateTimeUtils.dateToString(year, monthOfYear, dayOfMonth);
        reminderText.setText(getString(R.string.reminder_set_at) + " " + date + " " + time);
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        // if date is not chosen first but time is chosen -> make today the default date.
        // also check for valid time, must be today but not the past hour or minutes.
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

        // set time value and update text
        time = dateTimeUtils.timeToString(hourOfDay, minute);
        reminderText.setText(getString(R.string.reminder_set_at) + " " + date + " " + time);
    }
}
