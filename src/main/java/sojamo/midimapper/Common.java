package sojamo.midimapper;

import processing.data.JSONArray;
import processing.data.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.*;

class Common {

    static public Object invoke(final Object theObject, final String theMember, final Object... theParams) {

        Class[] cs = new Class[theParams.length];

        for (int i = 0; i < theParams.length; i++) {
            Class c = theParams[i].getClass();
            cs[i] = classmap.containsKey(c) ? classmap.get(c) : c;
        }
        try {
            final Field f = theObject.getClass().getDeclaredField(theMember);
            /* TODO check super */
            f.setAccessible(true);
            Object o = theParams[0];
            Class cf = o.getClass();
            if (cf.equals(Integer.class)) {
                f.setInt(theObject, i(o));
            } else if (cf.equals(Float.class)) {
                f.setFloat(theObject, f(o));
            } else if (cf.equals(Long.class)) {
                f.setLong(theObject, l(o));
            } else if (cf.equals(Double.class)) {
                f.setDouble(theObject, d(o));
            } else if (cf.equals(Boolean.class)) {
                f.setBoolean(theObject, b(o));
            } else if (cf.equals(Character.class)) {
                f.setChar(theObject, (char) i(o));
            } else {
                f.set(theObject, o);
            }
        } catch (NoSuchFieldException e1) {
            try {
                final Method m = theObject.getClass().getDeclaredMethod(theMember, cs);
                /* TODO check super */
                m.setAccessible(true);
                try {
                    return m.invoke(theObject, theParams);
                } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
                    System.err.println(e.getMessage());
                }

            } catch (SecurityException | NoSuchMethodException e) {
                System.err.println(e.getMessage());
            }
        } catch (IllegalArgumentException | IllegalAccessException e) {
            System.err.println(e);
        }
        return null;
    }

    static public int i(final Object o, final int theDefault) {
        return (o instanceof Number) ? ((Number) o).intValue() : (o instanceof String) ? i(s(o)) : theDefault;
    }

    static public String s(final Object o) {
        return (o != null) ? o.toString() : "";
    }

    static public int i(final String o, final int theDefault) {
        return isNumeric(o) ? Integer.parseInt(o) : theDefault;
    }

    static public boolean b(final Object o) {
        return (o instanceof Boolean) ? ((Boolean) o) : (o instanceof Number) ? ((Number) o).intValue() == 0 ? false : true : false;
    }

    static public long l(final Object o) {
        return (o instanceof Number) ? ((Number) o).longValue() : Long.MIN_VALUE;
    }

    static public double d(final Object o) {
        return (o instanceof Number) ? ((Number) o).doubleValue() : Double.MIN_VALUE;
    }

    static public int i(final Object o) {
        return (o instanceof Number) ? ((Number) o).intValue() : Integer.MIN_VALUE;
    }

    static public int i(final String o) {
        return isNumeric(o) ? Integer.parseInt(o) : Integer.MIN_VALUE;
    }

    static public float f(final Object o) {
        return (o instanceof Number) ? ((Number) o).floatValue() : Float.MIN_VALUE;
    }

    static public float f(final String o) {
        return isNumeric(o) ? Float.parseFloat(o) : Integer.MIN_VALUE;
    }

    static public final float mapValue(final float theValue,
                                       final float theStart0,
                                       final float theStop0,
                                       final float theStart1,
                                       final float theStop1) {
        return theStart1 + (theStop1 - theStart1) * ((theValue - theStart0) / (theStop0 - theStart0));
    }

    static public boolean isNumeric(final Object o) {
        return isNumeric(o.toString());
    }

    static public boolean isNumeric(final String str) {
        return str.matches("(-|\\+)?\\d+(\\.\\d+)?");
    }

    static public Map toMap(final Object... args) {
        Map m = new LinkedHashMap();
        if (args.length % 2 == 0) {
            for (int i = 0; i < args.length; i += 2) {
                m.put(args[i], args[i + 1]);
            }
        }
        return m;
    }

    public static Object parse(final Object o, final Object m) {
        if (o instanceof JSONObject) {
            if (m instanceof Map) {
                Set set = ((JSONObject) o).keys();
                for (Object o1 : set) {
                    Object o2 = invoke(o, "opt", o1.toString());
                    if (o2 instanceof JSONObject) {
                        Map m1 = new LinkedHashMap();
                        ((Map) m).put(o1.toString(), m1);
                        parse(o2, m1);
                    } else if (o2 instanceof JSONArray) {
                        List l1 = new ArrayList();
                        ((Map) m).put(o1.toString(), l1);
                        parse(o2, l1);
                    } else {
                        ((Map) m).put(o1.toString(), o2);
                    }
                }
            }
        } else if (o instanceof JSONArray) {
            if (m instanceof List) {
                List l = ((List) m);
                int n = 0;
                Object o3 = invoke(o, "opt", n);
                while (o3 != null) {
                    if (o3 instanceof JSONArray) {
                        List l1 = new ArrayList();
                        l.add(l1);
                        parse(o3, l1);
                    } else if (o3 instanceof JSONObject) {
                        Map l1 = new LinkedHashMap();
                        l.add(l1);
                        parse(o3, l1);
                    } else {
                        l.add(o3);
                    }
                    o3 = invoke(o, "opt", ++n);
                }
            } else {
                println("ups");
            }
        }
        return m;
    }

    static public Object json(final String theJson) {
        if (theJson.length() == 0) {
            return null;
        }

        final String json = theJson.trim();
        final char c = json.charAt(0);
        switch (c) {
            case ('['):
                try {
                    List l = new ArrayList();
                    parse(JSONArray.parse(json), l);
                    return l;
                } catch (Exception e) {
                    println("ControlPanel.json()", "parameter is not a JSON formatted String:", json);
                }
                break;
            case ('{'):
                try {
                    Map m = new LinkedHashMap();
                    parse(JSONObject.parse(json), m);
                    return m;
                } catch (Exception e) {
                    println("ControlPanel.json()", "parameter is not a JSON formatted String:", json);
                }
                break;
        }
        return null;
    }


    static public void print(final Object... strs) {
        for (Object str : strs) {
            System.out.print(toString(str) + " ");
        }
    }

    static public void println(final Object... strs) {
        print(strs);
        System.out.println();
    }

    static public String toString(final Object o) {
        if (o == null) {
            return null;
        }
        return o.toString();
    }

    static public ByteBuffer clone(final ByteBuffer original) {
        ByteBuffer clone = ByteBuffer.allocate(original.capacity());
        clone.clear();
        original.rewind();
        clone.put(original);
        original.rewind();
        clone.flip();
        return clone;
    }


    static Map<Class<?>, Class<?>> classmap = new HashMap() {
        {
            put(Integer.class, int.class);
            put(Float.class, float.class);
            put(Double.class, double.class);
            put(Boolean.class, boolean.class);
            put(Character.class, char.class);
            put(Long.class, long.class);
        }
    };

}
