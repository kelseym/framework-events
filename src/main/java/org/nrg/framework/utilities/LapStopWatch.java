package org.nrg.framework.utilities;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class LapStopWatch extends StopWatch {
    public static LapStopWatch createStarted() {
        final LapStopWatch stopWatch = new LapStopWatch();
        stopWatch.start();
        return stopWatch;
    }

    public class Lap {
        Lap(final long lapTime, final long overallTime, final String message) {
            this.lapTime = lapTime;
            this.overallTime = overallTime;
            this.message = message;
        }

        Lap(final long lapTime, final String message) {
            this.lapTime = lapTime;
            this.overallTime = lapTime;
            this.message = message;
        }

        public long getLapTime() {
            return lapTime / NANO_2_MILLIS;
        }

        public long getLapNanoTime() {
            return lapTime;
        }

        public long getOverallTime() {
            return overallTime / NANO_2_MILLIS;
        }

        public long getOverallNanoTime() {
            return overallTime;
        }

        public String getMessage() {
            return message;
        }

        private final long lapTime, overallTime;
        private final String message;
    }

    /**
     * Overrides the {@link StopWatch#stop()} method to add the stop time to the list
     * of lap times.
     */
    @Override
    public void stop() {
        super.stop();
        lap("Stop");
    }

    /**
     * Overrides the {@link StopWatch#reset()} method to clear the list of lap times.
     */
    @Override
    public void reset() {
        super.reset();
        laps.clear();
        largestLapTime = 0;
    }

    /**
     * Adds a lap time to the list without a specific message.
     *
     * @see #lap(String)
     */
    public void lap() {
        lap("");
    }

    /**
     * Adds a lap time to the list with an accompanying message. This can be used to record,
     * e.g. which particular object or operation corresponds to a particular lap time.
     *
     * @param message The message to store with the lap time.
     *
     * @see #lap()
     */
    public void lap(final String message) {
        final long currentTime;
        if (isStopped() || isSuspended()) {
            currentTime = getNanoTime();
        } else {
            split();
            currentTime = getSplitNanoTime();
            unsplit();
        }

        log.debug("Time at lap request: {}", currentTime);

        if (laps.isEmpty()) {
            log.debug("Adding first lap with time {} ns and message \"{}\", also set to largest lap time since it's the only lap time.", currentTime, message);
            laps.add(new Lap(currentTime, message));
            largestLapTime = currentTime;
        } else {
            // Calculate the lap time: subtract the overall time at the last lap with the overall time now.
            final long lapTime = currentTime - laps.get(laps.size() - 1).getOverallTime();
            log.debug("Adding next lap with lap time {} ns and message \"{}\"", lapTime, message);
            laps.add(new Lap(lapTime, currentTime, message));
            if (lapTime > largestLapTime) {
                log.debug("Exceeded the previous largest lap time {} with {}", largestLapTime, lapTime);
                largestLapTime = lapTime;
            }
        }
    }

    /**
     * Gets the last lap time recorded in milliseconds.
     *
     * @return The last lap time recorded.
     */
    public long getLastLapTime() {
        return getLastLapNanoTime() / NANO_2_MILLIS;
    }

    /**
     * Gets the last lap time recorded in nanoseconds.
     *
     * @return The last lap time recorded.
     */
    public long getLastLapNanoTime() {
        return laps.isEmpty() ? 0 : laps.get(laps.size() - 1).getLapTime();
    }

    /**
     * Gets all of the recorded lap times in milliseconds.
     *
     * @return A list of all of the recorded lap times.
     */
    public List<Long> getLapTimes() {
        final List<Long> lapTimes = new ArrayList<>();
        for (final Lap lap : laps) {
            lapTimes.add(lap.getLapTime());
        }
        return lapTimes;
    }

    /**
     * Gets all of the recorded lap times in nanoseconds.
     *
     * @return A list of all of the recorded lap times.
     */
    public List<Long> getLapNanoTimes() {
        final List<Long> lapTimes = new ArrayList<>();
        for (final Lap lap : laps) {
            lapTimes.add(lap.getLapNanoTime());
        }
        return lapTimes;
    }

    /**
     * Gets all of the recorded lap times in nanoseconds along with
     * any accompanying messages.
     *
     * @return All of the recorded lap times with any accompanying messages.
     */
    public List<Lap> getLaps() {
        return laps;
    }

    public static void toCSV(final LapStopWatch stopWatch, final PrintStream out) {
        out.println(toCSV(stopWatch));
    }

    public static String toCSV(final LapStopWatch stopWatch) {
        final StringBuilder builder = new StringBuilder();
        builder.append(HEADER_LAP).append(",").append(HEADER_LAP_MS).append(",").append(HEADER_ELAPSED_MS).append(",").append(HEADER_MESSAGE).append("\n");

        final List<Lap> laps = stopWatch.getLaps();
        for (int index = 0; index < laps.size(); ) {
            final Lap lap = laps.get(index);
            builder.append(++index).append(",").append(lap.getLapTime()).append(",").append(lap.getOverallTime()).append(",").append(lap.getMessage()).append("\n");
        }
        return builder.toString();
    }

    public static void toTable(final LapStopWatch stopWatch, final PrintStream out) {
        out.println(toTable(stopWatch));
    }

    public static String toTable(final LapStopWatch stopWatch) {
        final List<Lap> laps = stopWatch.getLaps();
        if (laps.isEmpty()) {
            return "";
        }

        final int size = laps.size();

        // Get the size of the largest number to be formatted so that numbers with fewer digits
        final int indexPadding = Math.max(getNumberFormattedDisplaySize(size), HEADER_LAP_LENGTH);

        // Now get the size of the largest lap time and largest overall time.
        final int lapPadding     = Math.max(getNumberFormattedDisplaySize(stopWatch.largestLapTime), HEADER_LAP_TIME_MS_LENGTH);
        final int elapsedPadding = Math.max(getNumberFormattedDisplaySize(laps.get(size - 1).getOverallTime()), HEADER_ELAPSED_MS_LENGTH);

        final StringBuilder builder = new StringBuilder();
        final String        header  = String.format("%" + indexPadding + "s  |  %" + lapPadding + "s  |  %" + elapsedPadding + "s  |  %s", HEADER_LAP, HEADER_LAP_MS, HEADER_ELAPSED_MS, HEADER_MESSAGE);
        builder.append(header).append("\n");
        builder.append(StringUtils.repeat("-", header.length())).append("\n");

        final String formatString =  "%" + indexPadding + "d  |  %," + lapPadding + "d  |  %," + elapsedPadding + "d  |  %s\n";
        for (int index = 0; index < size; ) {
            final Lap lap = laps.get(index);
            builder.append(String.format(formatString, ++index, lap.getLapTime(), lap.getOverallTime(), lap.getMessage()));
        }

        return builder.toString();
    }

    /**
     * Gets the length in characters in the display string for the submitted number, including formatting characters.
     * See {@link #getNumberDisplaySize(long)} for more information about the display size. This method differs only
     * in that it adds one to the size for each full multiple of 3 for that value. For example, if the display size is
     * two, this method adds nothing and returns two, but, if the display size is four, this method returns five, since
     * there's one full multiple of three represented. This accounts for adding thousands separator characters.
     *
     * @param number The number to evaluate.
     *
     * @return The number of digits in the display string.
     */
    private static int getNumberFormattedDisplaySize(final long number) {
        final int displaySize = getNumberDisplaySize(number);
        return displaySize + (displaySize - (displaySize % 3)) / 3;
    }

    /**
     * Gets the length in characters in the display string for the submitted number. For example, submitting 52 would
     * return 2, while submitting 92638 would return 5. This looks a little bit hinky, but it's a much higher performance
     * way of determining the display length for a formatted base-10 numerical value than, e.g., converting the value to
     * a string and getting the length of that.
     *
     * @param number The number to evaluate.
     *
     * @return The number of digits in the display string.
     */
    private static int getNumberDisplaySize(final long number) {
        int  length = 0;
        long temp   = 1;
        while (temp <= number) {
            length++;
            temp = (temp << 3) + (temp << 1);
        }
        return length;
    }

    private static final Logger log                       = LoggerFactory.getLogger(LapStopWatch.class);
    private static final long   NANO_2_MILLIS             = 1000000L;
    private static final String HEADER_LAP                = "Lap";
    public static final  int    HEADER_LAP_LENGTH         = HEADER_LAP.length();
    private static final String HEADER_LAP_MS             = "Lap Time (ms)";
    private static final int    HEADER_LAP_TIME_MS_LENGTH = HEADER_LAP_MS.length();
    private static final String HEADER_ELAPSED_MS         = "Elapsed (ms)";
    private static final int    HEADER_ELAPSED_MS_LENGTH  = HEADER_ELAPSED_MS.length();
    private static final String HEADER_MESSAGE            = "Message";

    private final List<Lap> laps = new ArrayList<>();

    private long largestLapTime;
}
