package com.example.rentify;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import database.context.DataContext;
import database.entities.Category;
import models.TableColumns;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.example.rentify", appContext.getPackageName());
    }

    @Test
    public void CategoryTest() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        var dataContext = new DataContext(appContext); // in Activity 'this' will be passed instead of 'appContext'

        var category = new Category("TestCategory","");

        var categoryId = dataContext.Categories.Insert(category);

        var addedCategory = dataContext.Categories.GetById((int) categoryId);

        assertEquals(addedCategory.Name, "TestCategory");
    }

    @Test
    public void CategoryPartialUpdate() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        var dataContext = new DataContext(appContext); // in Activity 'this' will be passed instead of 'appContext'

        var category = new Category("TestCategory","");

        var categoryId = dataContext.Categories.Insert(category);

        var addedCategory = dataContext.Categories.GetById((int) categoryId);

        dataContext.Categories.Update(addedCategory.Id, TableColumns.Category.Name, "UpdatedNameWithPartialUpdate");

        var updatedCategory = dataContext.Categories.GetById(addedCategory.Id);

        assertEquals(updatedCategory.Name, "UpdatedNameWithPartialUpdate");
    }

    @Test
    public void CategoryFullUpdate() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        var dataContext = new DataContext(appContext); // in Activity 'this' will be passed instead of 'appContext'

        var category = new Category("TestCategory","");

        var categoryId = dataContext.Categories.Insert(category);

        var addedCategory = dataContext.Categories.GetById((int) categoryId);

        addedCategory.Name = "UpdatedNameWithFullUpdate";

        dataContext.Categories.Update(addedCategory);

        var updatedCategory = dataContext.Categories.GetById(addedCategory.Id);

        assertEquals(updatedCategory.Name, "UpdatedNameWithFullUpdate");
    }
}
