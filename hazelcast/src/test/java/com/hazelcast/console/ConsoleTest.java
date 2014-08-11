package com.hazelcast.console;

import com.hazelcast.core.Hazelcast;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import static junit.framework.TestCase.assertEquals;

/**
 * Created by buremba <Burak Emre Kabakcı> on 07/08/14 17:26.
 */
public class ConsoleTest {
    private static final String EOL = System.getProperty("line.separator");

    @Test
    public void testQueueOfferPoll() throws Exception {
        assertResponse(new String[]{"q.offer test", "q.poll"}, "test");
    }


    @Test
    public void testQueueIterator() throws Exception {
        assertResponse(new String[]{"q.offer test0", "q.offer test1", "q.iterator"}, "test0");
    }

    @Test
    public void testQueueSize() throws Exception {
        assertResponse(new String[]{"q.offer test0", "q.poll", "q.offer test1", "q.offer test2", "q.size"}, "2");
    }

    @Test
    public void testQueueClear() throws Exception {
        assertResponse(new String[]{"q.offer test0", "q.offer test1", "q.clear", "q.size"}, "0");
    }

    @Test
    public void testSetAddRemove() throws Exception {
        assertResponse(new String[]{"s.add test", "s.add test0", "s.remove test"}, "true");
    }

    @Test
    public void testSetAddManyRemoveMany() throws Exception {
        assertResponse(new String[]{"s.addmany 5", "s.removemany 4", "s.size"}, "1");
    }

    @Test
    public void testSetSize() throws Exception {
        assertResponse(new String[]{"s.addmany 5", "s.size"}, "5");
    }

    @Test
    public void testSetIterator() throws Exception {

        assertResponse(new String[]{"s.add test", "s.add test1", "s.iterator"}, "test test1");
    }

    @Test
    public void testLocklock() throws Exception {
        ByteArrayOutputStream bytes0 = new ByteArrayOutputStream();
        String[] cmds = new String[]{"lock test"};
        ConsoleApp app0 = start(createInputStream(cmds), bytes0);

        while (app0.getProcessedCommandCount() < cmds.length) ;

        ByteArrayOutputStream bytes1 = new ByteArrayOutputStream();
        String[] inputs = {"tryLock test 5", ""};
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ConsoleApp consoleApp = start(createInputStream(inputs), bytes1);
    }


    @Test
    public void testOfferManyPollMany() throws Exception {
        assertResponse(new String[]{"q.offermany 2 test0", "q.pollmany 2"}, "test0");
    }

    @Test
    public void testAtomicLongGet() throws Exception {
        assertResponse(new String[]{"a.get"}, "0");
    }

    @Test
    public void testAtomicLong_simple() throws Exception {
        assertResponse(new String[]{"a.inc", "a.inc", "a.get"}, "2");
    }

    public void assertResponse(String[] inputs, String response) throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ConsoleApp consoleApp = start(createInputStream(inputs), new PrintStream(bytes));
        while (consoleApp.getProcessedCommandCount() < inputs.length) ;

        String actual = trimCommandPrefix(consoleApp.getCommandPrefix(), bytes.toString());
        String[] s = actual.split("\n");
        assertEquals(trimCommandPrefix(consoleApp.getCommandPrefix(), s[s.length - 1]), response);
    }

    private InputStream createInputStream(String[] inputs) {
        StringBuilder out = new StringBuilder();
        for (int x = 0; x < inputs.length; ++x) {
            out.append(inputs[x]).append(EOL);
        }
        return new ByteArrayInputStream(out.toString().getBytes());
    }


    public ConsoleApp start(final InputStream input, final OutputStream out) throws Exception {
        final ConsoleApp consoleApp = new ConsoleApp(Hazelcast.newHazelcastInstance(), System.in, System.out);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    consoleApp.start(null, true);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                synchronized (consoleApp) {
                    consoleApp.notify();
                }

            }
        }).start();
        synchronized (consoleApp) {
            consoleApp.wait();
            return consoleApp;
        }
    }

    private static String trimCommandPrefix(String commandPrefix, String text) {
        int beginIndex = 0;
        int endIndex = text.length();

        while (text.substring(beginIndex, endIndex).startsWith(commandPrefix)) {
            beginIndex += commandPrefix.length();
        }

        while (text.substring(beginIndex, endIndex).endsWith(commandPrefix)) {
            endIndex -= commandPrefix.length();
        }

        return text.substring(beginIndex, endIndex).trim();
    }
}
