import java.util.function.Supplier;
import java.util.concurrent.TimeUnit;

public class TimingUtils {
    public static <T> T time(Supplier<T> function, String operationName) {
        long startTime = System.nanoTime();
        T result = function.get();
        long endTime = System.nanoTime();
        long duration = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
        System.out.println(operationName + " execution time: " + duration + " ms");
        return result;
    }

    public static void timeVoid(Runnable function, String operationName) {
        long startTime = System.nanoTime();
        function.run();
        long endTime = System.nanoTime();
        long duration = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
        System.out.println(operationName + " execution time: " + duration + " ms");
    }

    public static <T> T timeAndReturn(Supplier<T> supplier, String operationName) {
        long startTime = System.nanoTime();
        T result = supplier.get();
        long endTime = System.nanoTime();
        long duration = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
        System.out.println(operationName + " execution time: " + duration + " ms");
        return result;
    }

}