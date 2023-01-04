import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    public static AtomicInteger threeLetterNicknameCounter = new AtomicInteger();
    public static AtomicInteger fourLetterNicknameCounter = new AtomicInteger();
    public static AtomicInteger fiveLetterNicknameCounter = new AtomicInteger();

    public static void main(String[] args) {
        Random random = new Random();
        String[] texts = new String[100_000];
        for (int i = 0; i < texts.length; i++) {
            texts[i] = generateText("abc", 3 + random.nextInt(3));
        }
        final ExecutorService threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        final Future<?> findTextWithPalindromes = threadPool.submit(
                new Thread(() -> {
                    for (String text : texts) {
                        String reversedText = new StringBuffer(text).reverse().toString();
                        if (text.equals(reversedText)) {
                            incrementCounters(text.length());
                        }
                    }
                })
        );
        final Future<?> findTextWithSameLetters = threadPool.submit(
                new Thread(() -> {
                    for (String text : texts) {
                        Pattern p = Pattern.compile("a+|b+|c+");
                        Matcher m = p.matcher(text);
                        if (m.matches()) {
                            incrementCounters(text.length());
                        }
                    }
                })
        );
        final Future<?> findTextWithLettersInAscendingOrder = threadPool.submit(
                new Thread(() -> {
                    for (String text : texts) {
                        Pattern p = Pattern.compile("a+b+c+|a+b+|a+c+|b+c+");
                        Matcher m = p.matcher(text);
                        if (m.matches()) {
                            incrementCounters(text.length());
                        }
                    }
                })
        );
        threadPool.shutdown();
        while (!threadPool.isTerminated()) {
            System.out.println("Awaiting termination...");
            try {
                threadPool.awaitTermination(100, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {

            }
        }
        System.out.println("Thread pool terminated successfully...");
        System.out.printf("%s %d %s%n", "Красивых слов с длиной 3:", threeLetterNicknameCounter.get(), "шт");
        System.out.printf("%s %d %s%n", "Красивых слов с длиной 4:", fourLetterNicknameCounter.get(), "шт");
        System.out.printf("%s %d %s%n", "Красивых слов с длиной 5:", fiveLetterNicknameCounter.get(), "шт");
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }

    public static void incrementCounters(int length) {
        switch (length) {
            case 3: {
                threeLetterNicknameCounter.getAndIncrement();
                break;
            }
            case 4: {
                fourLetterNicknameCounter.getAndIncrement();
                break;
            }
            case 5: {
                fiveLetterNicknameCounter.getAndIncrement();
                break;
            }
            default: {
                break;
            }
        }
    }
}