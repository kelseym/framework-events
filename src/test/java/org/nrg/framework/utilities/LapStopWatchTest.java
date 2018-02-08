package org.nrg.framework.utilities;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class LapStopWatchTest {
    @Test
    public void testLapStopWatch() throws InterruptedException {
        final LapStopWatch stopWatch = LapStopWatch.createStarted();

        List<Long> lapTimes = stopWatch.getLapTimes();
        assertNotNull(lapTimes);
        assertEquals(0, lapTimes.size());

        Thread.sleep(100);

        stopWatch.lap();

        lapTimes = stopWatch.getLapTimes();
        assertNotNull(lapTimes);
        assertEquals(1, lapTimes.size());

        Thread.sleep(1000);

        stopWatch.lap("Just waited one second");

        lapTimes = stopWatch.getLapTimes();
        assertNotNull(lapTimes);
        assertEquals(2, lapTimes.size());

        Thread.sleep(100);
        stopWatch.stop();

        lapTimes = stopWatch.getLapTimes();
        assertNotNull(lapTimes);
        assertEquals(3, lapTimes.size());

        final long lapTime1 = lapTimes.get(0);
        final long lapTime2 = lapTimes.get(1);
        final long lapTime3 = lapTimes.get(2);
        assertTrue(lapTime1 > 0);
        assertTrue(lapTime2 > 0);
        assertTrue(lapTime3 > 0);
        assertTrue(lapTime2 > lapTime1);
        assertTrue(lapTime3 > lapTime2);
        assertEquals(stopWatch.getTime(), lapTime3);

        final List<LapStopWatch.Lap> laps = stopWatch.getLaps();
        final LapStopWatch.Lap lap1 = laps.get(0);
        final LapStopWatch.Lap lap2 = laps.get(1);
        final LapStopWatch.Lap lap3 = laps.get(2);

        assertEquals(lapTime1, lap1.getLapTime());
        assertEquals(lapTime2, lap2.getLapTime());
        assertEquals(lapTime3, lap3.getLapTime());
        assertEquals(lap1.getMessage(), "");
        assertEquals(lap2.getMessage(), "Just waited one second");
        assertEquals(lap3.getMessage(), "Stop");

        LapStopWatch.toTable(stopWatch, System.out);
        LapStopWatch.toCSV(stopWatch, System.out);

        Thread.sleep(100);
    }
}
