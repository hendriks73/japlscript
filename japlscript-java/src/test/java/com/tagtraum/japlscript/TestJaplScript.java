package com.tagtraum.japlscript;

import com.tagtraum.japlscript.types.*;
import org.junit.Test;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.Assert.*;

/**
 * Test {@link JaplScript}.
 *
 * @author <a href="mailto:hs@tagtraum.com">Hendrik Schreiber</a>
 */
public class TestJaplScript {

    @Test
    public void testAddRemoveType() {
        // remove all types first
        final List<JaplType<?>> originalTypes = new ArrayList<>(JaplScript.getTypes());
        try {
            for (final JaplType<?> t : JaplScript.getTypes()) {
                JaplScript.removeType(t);
            }
            assertTrue(JaplScript.getTypes().isEmpty());
            // now add something
            JaplScript.addType(Text.getInstance());
            assertEquals(1, JaplScript.getTypes().size());
            assertEquals(Text.getInstance(), JaplScript.getTypes().get(0));
        } finally {
            originalTypes.forEach(JaplScript::addType);
        }
    }

    @Test
    public void testAddRemoveGlobalAspects() {
        final Aspect aspect = new Aspect() {
            @Override
            public String before(final String application, final String body) {
                return null;
            }

            @Override
            public String after(final String application, final String body) {
                return null;
            }
        };
        JaplScript.addGlobalAspect(aspect);
        assertTrue(JaplScript.getGlobalAspects().contains(aspect));
        JaplScript.removeGlobalAspect(aspect);
        assertFalse(JaplScript.getGlobalAspects().contains(aspect));
    }

    @Test
    public void testStartSession() throws ExecutionException, InterruptedException, TimeoutException {
        final Session session = JaplScript.startSession();
        // always return same session when called from the same thread
        assertSame(session, JaplScript.startSession());
        final ExecutorService executorService = Executors.newSingleThreadExecutor();
        try {
            final Session otherThreadSession = executorService.submit(new Callable<Session>() {
                @Override
                public Session call() throws Exception {
                    return JaplScript.startSession();
                }
            }).get(5L, TimeUnit.SECONDS);
            assertNotEquals(session, otherThreadSession);
        } finally {
            executorService.shutdown();
        }
    }

    @Test
    public void testGetApplication() {
        final Application app = JaplScript.getApplication(Application.class, "MyApp");
        assertEquals("application \"MyApp\"", app.getApplicationReference());
        assertNull(app.getObjectReference());
    }

    /**
     * Test application.
     */
    @com.tagtraum.japlscript.Code("capp")
    @com.tagtraum.japlscript.Name("application")
    public interface Application extends Reference {
        TypeClass CLASS = TypeClass.getInstance("application", "\u00abclass capp\u00bb", null, null);
    }

    @Test
    public void testCastNullReference() {
        final String result = JaplScript.cast(String.class, null);
        assertNull(result);
    }

    @Test
    public void testCastList() {
        final String[] result = JaplScript.cast(String[].class, new ReferenceImpl("{\"hallo\"}", null));
        assertArrayEquals(new String[]{"hallo"}, result);
    }

    @Test
    public void testCastRecord() {
        final Map<String, Object> result = JaplScript.cast(Map.class, new ReferenceImpl("{name:\"hendrik\", index:3, creation date:date \"Sunday, January 7, 2007 at 23:32:16\", icon:missing value}", null));
        assertEquals(new HashSet<>(Arrays.asList("name", "index", "creation date", "icon")), result.keySet());
    }

    @Test
    public void testCastBoolean() {
        final java.lang.Boolean trueResult = JaplScript.cast(java.lang.Boolean.TYPE, new ReferenceImpl("true", null));
        assertEquals(java.lang.Boolean.TRUE, trueResult);
        final java.lang.Boolean falseResult = JaplScript.cast(java.lang.Boolean.TYPE, new ReferenceImpl("false", null));
        assertEquals(java.lang.Boolean.FALSE, falseResult);
    }

    @Test
    public void testCastInteger() {
        final java.lang.Integer result = JaplScript.cast(java.lang.Integer.TYPE, new ReferenceImpl("-123", null));
        assertEquals(java.lang.Integer.valueOf(-123), result);
    }

    @Test
    public void testCastLong() {
        final java.lang.Long result = JaplScript.cast(java.lang.Long.TYPE, new ReferenceImpl("-123", null));
        assertEquals(java.lang.Long.valueOf(-123), result);
    }

    @Test
    public void testCastFloat() {
        final java.lang.Float result = JaplScript.cast(java.lang.Float.TYPE, new ReferenceImpl("-123.123", null));
        assertEquals(java.lang.Float.valueOf(-123.123f), result);
    }

    @Test
    public void testCastDouble() {
        final java.lang.Double result = JaplScript.cast(java.lang.Double.TYPE, new ReferenceImpl("-123.123", null));
        assertEquals(java.lang.Double.valueOf(-123.123), result);
    }

    @Test
    public void testCastDate() throws ParseException {
        final Date result = JaplScript.cast(Date.class, new ReferenceImpl("\"Friday, April 8, 2016 at 1:04:56 AM\"", null));
        assertSame(Date.class, result.getClass());
        final String dateString = "1999-05-28T11:25:27Z";
        final Date date = JaplScript.cast(Date.class, new ReferenceImpl(dateString, null));
        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        assertEquals(date, format.parse(dateString));
    }

    @Test(expected = JaplScriptException.class)
    public void testCastBadDate() {
        JaplScript.cast(Date.class, new ReferenceImpl("\"XXX, XXX 8, 2016 at 1:04:56 AM\"", null));
    }

    @Test
    public void testCastDateNullObjectReference() {
        final Date result = JaplScript.cast(Date.class, new ReferenceImpl(null, null));
        assertNull(result);
    }

    @Test
    public void testCastPictureNullObjectReference() {
        final Picture result = JaplScript.cast(Picture.class, new ReferenceImpl(null, null));
        assertNull(result);
    }

    @Test
    public void testCastPicture() {
        final String finderApplicationReference = "application \"Finder\"";
        final Picture picture = JaplScript.cast(Picture.class, new ReferenceImpl("«data ABCDBBBB»", finderApplicationReference));
        assertSame(Picture.class, picture.getClass());
        final TypeClass expected = TypeClass.getInstance("«class ABCD»", "«class ABCD»", finderApplicationReference, null);
        assertEquals(expected, picture.getTypeClass());
        assertArrayEquals(new byte[]{-69, -69}, picture.getData());
    }

    @Test
    public void testCastTdtaNullObjectReference() {
        final Tdta result = JaplScript.cast(Tdta.class, new ReferenceImpl(null, null));
        assertNull(result);
    }

    @Test
    public void testCastTdta() {
        final String finderApplicationReference = "application \"Finder\"";
        final Tdta tdta = JaplScript.cast(Tdta.class, new ReferenceImpl("«data tdtaBBBB»", finderApplicationReference));
        assertSame(Tdta.class, tdta.getClass());
        final TypeClass expected = TypeClass.getInstance("«class tdta»", "«class tdta»", finderApplicationReference, null);
        assertEquals(expected, tdta.getTypeClass());
        assertArrayEquals(new byte[]{-69, -69}, tdta.getTdta());
    }

    @Test
    public void testCastJaplScriptFileNullObjectReference() {
        final JaplScriptFile result = JaplScript.cast(JaplScriptFile.class, new ReferenceImpl(null, null));
        assertNull(result);
    }

    @Test
    public void testCastJaplScriptFile() {
        final String finderApplicationReference = "application \"Finder\"";
        final JaplScriptFile japlScriptFile = JaplScript.cast(JaplScriptFile.class, new ReferenceImpl("(POSIX file \"/Users\")", finderApplicationReference));
        assertSame(JaplScriptFile.class, japlScriptFile.getClass());
        final TypeClass expected = TypeClass.getInstance("«class furl»", "«class furl»", finderApplicationReference, null);
        assertEquals(expected, japlScriptFile.getTypeClass());
        assertEquals(Paths.get("/Users"), japlScriptFile.getPath());
    }


    @Test
    public void testCastTypeClassNullObjectReference() {
        final TypeClass result = JaplScript.cast(TypeClass.class, new ReferenceImpl(null, null));
        assertNull(result);
    }

    @Test
    public void testCastArrayNullObjectReference() {
        final String[] result = JaplScript.cast(String[].class, new ReferenceImpl(null, null));
        assertNull(result);
    }

    @Test
    public void testCastEmptyStringObjectReference() {
        final Color result = JaplScript.cast(Color.class, new ReferenceImpl("", null));
        assertNull(result);
    }

    @Test
    public void testCastDataNullObjectReference() {
        final Data result = JaplScript.cast(Data.class, new ReferenceImpl(null, null));
        assertNull(result);
    }

    @Test
    public void testCastData() {
        final String finderApplicationReference = "application \"Finder\"";
        final Data data = JaplScript.cast(Data.class, new ReferenceImpl("«data ABCDBBBB»", finderApplicationReference));
        assertSame(Data.class, data.getClass());
        final TypeClass expected = TypeClass.getInstance("«class ABCD»", "«class ABCD»", finderApplicationReference, null);
        assertEquals(expected, data.getTypeClass());
        assertArrayEquals(new byte[]{-85, -51, -69, -69}, data.getData());
    }

    @Test
    public void testCastAliasNullObjectReference() {
        final Alias result = JaplScript.cast(Alias.class, new ReferenceImpl(null, null));
        assertNull(result);
    }

    @Test
    public void testCastAlias() {
        final String finderApplicationReference = "application \"Finder\"";
        final Alias alias = JaplScript.cast(Alias.class, new ReferenceImpl("/Users", finderApplicationReference));
        assertSame(Alias.class, alias.getClass());
        final TypeClass expected = TypeClass.getInstance("«class furl»", "«class furl»", finderApplicationReference, null);
        assertEquals(expected, alias.getTypeClass());
        assertEquals(Paths.get("/Users"), alias.getPath());
    }

    @Test
    public void testSimpleQuote() {
        String quotedHallo = JaplScript.quote("Hallo");
        assertEquals("(\"Hallo\")", quotedHallo);
    }

    @Test
    public void testQuoteQuote() {
        String quotedHallo = JaplScript.quote("\"Hallo\"");
        assertEquals("(\"\\\"Hallo\\\"\")", quotedHallo);
    }

    @Test
	public void testUnicodeQuote() {
        final String s = "A\u00afB";
        System.out.println(s);
        String quotedHallo = JaplScript.quote(s);
        System.out.println(quotedHallo);
        assertEquals("(\"A\" & (\u00abdata utf8c2af\u00bb as Unicode text) & \"B\")", quotedHallo);
    }

	@Test
	public void testUnicodeQuote2() {
        final String s = "A\ubaa0B";
        System.out.println(s);
        String quotedHallo = JaplScript.quote(s);
        System.out.println(quotedHallo);
        assertEquals("(\"A\" & (\u00abdata utf8ebaaa0\u00bb as Unicode text) & \"B\")", quotedHallo);
    }

	@Test
	public void testUnicodeExtQuote() {
        final String s = "A\u00af\u00afB";
        System.out.println(s);
        String quotedHallo = JaplScript.quote(s);
        System.out.println(quotedHallo);
        assertEquals("(\"A\" & (\u00abdata utf8c2afc2af\u00bb as Unicode text) & \"B\")", quotedHallo);
    }

	@Test
	public void testTrailingUnicodeQuote() {
        final String s = "AB\u00af\u00af";
        System.out.println(s);
        String quotedHallo = JaplScript.quote(s);
        System.out.println(quotedHallo);
        assertEquals("(\"AB\" & (\u00abdata utf8c2afc2af\u00bb as Unicode text))", quotedHallo);
    }

    @Test
    public void testGetDate() throws IOException {
        final ScriptExecutor scriptExecutor = ScriptExecutor.newInstance();
        scriptExecutor.setScript("tell application \"Finder\"\n" +
                "\treturn current date\n" +
                "end tell");
        final String result = scriptExecutor.execute();
        final Date date = JaplScript.cast(Date.class, new ReferenceImpl(result, null));
        System.out.println("current date: " + date);
        assertEquals(System.currentTimeMillis(), date.getTime(), 2000);
    }

    @Test
    public void testCreateDate() throws IOException, ParseException {
        final ObjectInvocationHandler handler = new ObjectInvocationHandler(new ReferenceImpl(null, "application \"iTunes\""));
        final Date date = handler.executeAppleScript(new ReferenceImpl(null, "application \"iTunes\""), "return my createDate(1955, 5, 2, 13, 15, 22)", Date.class);
        final SimpleDateFormat format = new SimpleDateFormat("yyyy-M-d H:m:s");
        assertEquals(format.parse("1955-5-2 13:15:22"), date);
    }

}
