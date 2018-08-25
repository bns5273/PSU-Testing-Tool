package lab11;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 *
 * @author brett
 */
public class Util {
    static String arrayToString(String[] sa) {
        String out = "";
        for (int s = 0; s < sa.length; s++) {
            out = out + sa[s];
            if (s != sa.length - 1) {
                out = out + ", ";
            }
        }
        return out;
    }

    static String getAfterPeriod(String in) {
        return in.substring(in.lastIndexOf(".") + 1);
    }

    static String getExceptions(String in) {
        String sa[];
        sa = in.substring(in.indexOf("throws") + 15).split(",java.io.");
        return arrayToString(sa);
    }
    
    public static String skel(Class c) throws
            ClassNotFoundException,
            IllegalAccessException,
            IllegalArgumentException,
            InvocationTargetException,
            InstantiationException,
            NoSuchMethodException {
        
        String out = "package ";
        
        out = out.concat(c.getPackage().getName() + ";\n\n");
        
        String className;
        String classVars = "";
        String temp[];

        Method[] ma = c.getDeclaredMethods();
        Constructor ca[] = c.getConstructors();


        // CLASS LINE
        // TODO ma[0] not sufficent to find default constructor
        className = (c.toGenericString().substring(0, c.toGenericString().indexOf("class ")) + "class " + getAfterPeriod(ma[0].getName()));
        className = className + " {\n";
        out = out.concat(className);

        // FIELDS
        out = out.concat("// Fields\n");
        Field[] oa = c.getDeclaredFields();
        for (Field o : oa) {
            classVars = classVars.concat("    " + getAfterPeriod(o.getType().toGenericString()) + " " + o.getName() + ";\n");
        }
        out = out.concat(classVars);

        // Constructors
        out = out.concat("\n// Constructors\n");
        for (Constructor con : ca) {
            temp = con.toString().split(" ");
            out = out.concat("    " + temp[0] + " " + getAfterPeriod(con.getName() + "("));
            Parameter[] pa = con.getParameters();
            for (int p = 0; p < pa.length; p++) {
                out = out.concat(pa[p].getType() + " " + pa[p].getName());
                if (p != pa.length - 1) {
                    out = out.concat(", ");
                }
            }
            if(con.getExceptionTypes().length > 0)
                out = out.concat(") throws " + getExceptions(con.toString()) + "{ };\n");
            else
                out = out.concat(") { };\n");
        }

        // methods
        out = out.concat("\n// Methods\n");
        for (Method m : ma) {
            temp = m.toString().split(" ");
            out = out.concat("    " + temp[0] + " " + temp[1] + " " + m.getName() + "(");
            // method parameters
            Parameter[] pa = m.getParameters();
            for (int p = 0; p < pa.length; p++) {
                out = out.concat(getAfterPeriod(pa[p].getType().toGenericString()) + " " + pa[p].getName());
                if (p != pa.length - 1) {
                    out = out.concat(", ");
                }
            }
            out = out.concat(");\n");
        }

        out = out.concat("}\n");
            
            // invoking method with no arguments
//        Class[] ca = {int.class, int.class, int.class};
//        Constructor cons = c.getConstructor(ca);
//        Object[] oa = {1, 2, 3};
//        Object o = cons.newInstance(oa);
//        c.getConstructor(ca);
//        Object[] ob = {};
//        Object o2 = ma[0].invoke(o, ob); // o2 output of method
//        out = out.concat(o2\n);
            // invoke method with arguments
//        Object o = c.newInstance();
//        Object[] oa = {1, 2, 3};
//        Object o2 = ma[0].invoke(o, oa);

        return out;
    }
}
