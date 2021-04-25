package luongvo.com.todolistminimal.UITests;

import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.ViewActions;
import android.support.test.rule.ActivityTestRule;
import android.view.View;

import com.wdullaer.materialdatetimepicker.date.DatePickerController;
import com.wdullaer.materialdatetimepicker.date.DayPickerView;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.Timepoint;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.lang.reflect.Field;
import java.util.Calendar;

import luongvo.com.todolistminimal.MainActivity;
import luongvo.com.todolistminimal.R;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Checks.checkNotNull;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.EasyMock2Matchers.equalTo;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;


@RunWith(JUnit4.class)
public class AddToDoItemTest {

    @Rule
    public ActivityTestRule<MainActivity> activityTestRule =
            new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setUp() {
        onView(withId(R.id.actionButton))
                .perform(ViewActions.click());
    }

    @Test
    public void todoEditTextCheck() {
        onView(withId(R.id.todoEditText))
                .check(matches(allOf(
                        isDisplayed(),
                        isClickable())));
    }

    @Test
    public void addTodoBtnCheck() {
        onView(withId(R.id.addTodoBtn))
                .check(matches(allOf(
                        isDisplayed(),
                        isClickable())));
    }

    @Test
    public void reminderSwitchCheck() {
        onView(withId(R.id.reminderSwitch))
                .check(matches(allOf(
                        isDisplayed(),
                        isClickable())));
    }

    @Test
    public void actionButtonClick() {
        onView(withId(R.id.reminderSwitch))
                .perform(ViewActions.click());
    }

    @Test
    public void buttonSetDateCheck() {
        onView(withId(R.id.reminderSwitch))
                .perform(ViewActions.click());

        onView(withId(R.id.buttonSetDate))
                .check(matches(allOf(
                        isDisplayed(),
                        isClickable())));
    }

    @Test
    public void buttonSetTimeCheck() {
        onView(withId(R.id.reminderSwitch))
                .perform(ViewActions.click());

        onView(withId(R.id.buttonSetTime))
                .check(matches(allOf(
                        isDisplayed(),
                        isClickable())));
    }

    @Test
    public void reminderTextCheck() {
        onView(withId(R.id.reminderSwitch))
                .perform(ViewActions.click());

        onView(withId(R.id.reminderText))
                .check(matches(
                        isDisplayed()));
    }

    @Test
    public void todoEditTextPrint() {
        onView(withId(R.id.todoEditText))
                .perform(typeText("Buy one apple"))
                .check(matches(withText("Buy one apple")));
    }

    @Test
    public void setDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);

        onView(withId(R.id.reminderSwitch))
                .perform(ViewActions.click());

        performSetDate(calendar);

        String month;
        if ((calendar.get(Calendar.MONTH) + 1) < 10) {
            month = "0" + (calendar.get(Calendar.MONTH) + 1);
        } else {
            month = String.valueOf(calendar.get(Calendar.MONTH) + 1);
        }

        String day;
        if (calendar.get(Calendar.DAY_OF_MONTH) < 10) {
            day = "0" + calendar.get(Calendar.DAY_OF_MONTH);
        } else {
            day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        }


        onView(withId(R.id.reminderText))
                .check(matches(withText("Reminder set at " +
                        calendar.get(Calendar.YEAR) + "-" +
                        month + "-" +
                        day + " ")));
    }

    @Test
    public void setTime() {
        onView(withId(R.id.reminderSwitch))
                .perform(ViewActions.click());
        performSetTime();
    }

    @Test
    public void createTask() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);

        onView(withId(R.id.reminderSwitch))
                .perform(ViewActions.click());

        performSetDate(calendar);
        performSetTime();

        onView(withId(R.id.todoEditText))
                .perform(typeText("Buy one apple"));

        onView(withId(R.id.addTodoBtn))
                .perform(ViewActions.click());

        onData(anything()).inAdapterView(allOf(withId(R.id.todoList), isDisplayed())).atPosition(0)
                .perform(ViewActions.click());
        onView(withId(R.id.deleteTodoBtn))
                .perform(ViewActions.click());
    }

    private void performSetTime(){
        onView(withId(R.id.buttonSetTime))
                .perform(ViewActions.click());
        onView(isAssignableFrom(RadialPickerLayout.class))
                .check(matches(isDisplayed()))
                .perform(setTime(23, 59));
        onView(withText("OK")).perform(ViewActions.click());
    }

    private void performSetDate(Calendar calendar){
        onView(withId(R.id.buttonSetDate))
                .perform(ViewActions.click());
        onView(isAssignableFrom(DayPickerView.class))
                .check(matches(isDisplayed()))
                .perform(setDate(calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)));
        onView(withText("OK")).perform(ViewActions.click());
    }

    public static ViewAction setDate(final int year, final int monthOfYear, final int dayOfMonth) {

        return new ViewAction() {
            @Override
            public void perform(UiController uiController, View view) {
                final DayPickerView dayPickerView = (DayPickerView) view;

                try {
                    Field f = null;
                    f = DayPickerView.class.getDeclaredField("mController");
                    f.setAccessible(true);
                    DatePickerController controller = (DatePickerController) f.get(dayPickerView);
                    controller.onDayOfMonthSelected(year, monthOfYear, dayOfMonth);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public String getDescription() {
                return "set date";
            }

            @Override
            public Matcher<View> getConstraints() {
                return allOf(isAssignableFrom(DayPickerView.class), isDisplayed());
            }
        };
    }



    public static ViewAction setTime(final int hours, final int minutes) {
        return new ViewAction() {

            @Override
            public void perform(UiController uiController, View view) {
                final RadialPickerLayout timePicker = (RadialPickerLayout) view;

                timePicker.setTime(new Timepoint(hours, minutes, 0));
            }

            @Override
            public String getDescription() {
                return "set time";
            }

            @Override
            public Matcher<View> getConstraints() {
                return allOf(isAssignableFrom(RadialPickerLayout.class), isDisplayed());
            }
        };
    }
}
