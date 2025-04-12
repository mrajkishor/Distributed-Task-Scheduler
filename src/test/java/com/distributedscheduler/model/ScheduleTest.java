
package com.distributedscheduler.model;

import com.distributedscheduler.model.Schedule;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ScheduleTest {

    @Test
    public void testScheduleSettersAndGetters() {
        Schedule schedule = new Schedule();
        schedule.setCronExpression("0 18 * * *");
        schedule.setRecurring(true);

        assertEquals("0 18 * * *", schedule.getCronExpression());
        assertTrue(schedule.isRecurring());
    }

    @Test
    public void testScheduleConstructor() {
        Schedule schedule = new Schedule("*/10 * * * *", false);
        assertEquals("*/10 * * * *", schedule.getCronExpression());
        assertFalse(schedule.isRecurring());
    }
}
