package chrust.emploeye;

import org.junit.After;
import org.junit.Before;

import static org.junit.Assert.*;

/**
 * Created by Chrustkiran on 14/05/2018.
 */



import android.app.Activity;
import android.app.Instrumentation;
import android.media.MediaActionSound;
import android.support.test.espresso.Espresso;
import android.support.test.rule.ActivityTestRule;
import android.view.View;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.*;

    public class MainActivityTest {
        public ActivityTestRule<MainActivity> mActivityTestRule=new ActivityTestRule<MainActivity>(MainActivity.class);
        private MainActivity mActivity=null;
        Instrumentation.ActivityMonitor monitor=getInstrumentation().addMonitor(AddplaceActivity.class.getName(),null,false);

        @Before
        public void setUp() throws Exception {

            mActivity=mActivityTestRule.getActivity();
        }


        @Test
        public  void testLaunch(){

            View view=mActivity.findViewById(R.id.add_location);
            assertNotNull(view);
            Espresso.onView(withId(R.id.add_location)).perform(click());
            Activity MainActivity= getInstrumentation().waitForMonitorWithTimeout(monitor,50000);
            assertNotNull(MainActivity);
            MainActivity.finish();





        }

        @After
        public void tearDown() throws Exception {
            mActivity=null;
        }


}