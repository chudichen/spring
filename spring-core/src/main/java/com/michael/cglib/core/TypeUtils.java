package com.michael.cglib.core;

import com.michael.asm.Type;

import java.util.*;

/**
 * @author Michael Chu
 * @since 2019-09-25 18:01
 */
public class TypeUtils {
    private static final Map transforms = new HashMap();
    private static final Map rtransforms = new HashMap();

    private TypeUtils() {
    }

    public static Type getType(String className) {
        return Type.getType("L" + className.replace('.', '/') + ";");
    }

    public static boolean isFinal(int access) {
        return (16 & access) != 0;
    }

    public static boolean isStatic(int access) {
        return (8 & access) != 0;
    }

    public static boolean isProtected(int access) {
        return (4 & access) != 0;
    }

    public static boolean isPublic(int access) {
        return (1 & access) != 0;
    }

    public static boolean isAbstract(int access) {
        return (1024 & access) != 0;
    }

    public static boolean isInterface(int access) {
        return (512 & access) != 0;
    }

    public static boolean isPrivate(int access) {
        return (2 & access) != 0;
    }

    public static boolean isSynthetic(int access) {
        return (4096 & access) != 0;
    }

    public static boolean isBridge(int access) {
        return (64 & access) != 0;
    }

    public static String getPackageName(Type type) {
        return getPackageName(getClassName(type));
    }

    public static String getPackageName(String className) {
        int idx = className.lastIndexOf(46);
        return idx < 0 ? "" : className.substring(0, idx);
    }

    public static String upperFirst(String s) {
        return s != null && s.length() != 0 ? Character.toUpperCase(s.charAt(0)) + s.substring(1) : s;
    }

    public static String getClassName(Type type) {
        if (isPrimitive(type)) {
            return (String)rtransforms.get(type.getDescriptor());
        } else {
            return isArray(type) ? getClassName(getComponentType(type)) + "[]" : type.getClassName();
        }
    }

    public static Type[] add(Type[] types, Type extra) {
        if (types == null) {
            return new Type[]{extra};
        } else {
            List list = Arrays.asList(types);
            if (list.contains(extra)) {
                return types;
            } else {
                Type[] copy = new Type[types.length + 1];
                System.arraycopy(types, 0, copy, 0, types.length);
                copy[types.length] = extra;
                return copy;
            }
        }
    }

    public static Type[] add(Type[] t1, Type[] t2) {
        Type[] all = new Type[t1.length + t2.length];
        System.arraycopy(t1, 0, all, 0, t1.length);
        System.arraycopy(t2, 0, all, t1.length, t2.length);
        return all;
    }

    public static Type fromInternalName(String name) {
        return Type.getType("L" + name + ";");
    }

    public static Type[] fromInternalNames(String[] names) {
        if (names == null) {
            return null;
        } else {
            Type[] types = new Type[names.length];

            for(int i = 0; i < names.length; ++i) {
                types[i] = fromInternalName(names[i]);
            }

            return types;
        }
    }

    public static int getStackSize(Type[] types) {
        int size = 0;

        for(int i = 0; i < types.length; ++i) {
            size += types[i].getSize();
        }

        return size;
    }

    public static String[] toInternalNames(Type[] types) {
        if (types == null) {
            return null;
        } else {
            String[] names = new String[types.length];

            for(int i = 0; i < types.length; ++i) {
                names[i] = types[i].getInternalName();
            }

            return names;
        }
    }

    public static Signature parseSignature(String s) {
        int space = s.indexOf(32);
        int lparen = s.indexOf(40, space);
        int rparen = s.indexOf(41, lparen);
        String returnType = s.substring(0, space);
        String methodName = s.substring(space + 1, lparen);
        StringBuffer sb = new StringBuffer();
        sb.append('(');
        Iterator it = parseTypes(s, lparen + 1, rparen).iterator();

        while(it.hasNext()) {
            sb.append(it.next());
        }

        sb.append(')');
        sb.append(map(returnType));
        return new Signature(methodName, sb.toString());
    }

    public static Type parseType(String s) {
        return Type.getType(map(s));
    }

    public static Type[] parseTypes(String s) {
        List names = parseTypes(s, 0, s.length());
        Type[] types = new Type[names.size()];

        for(int i = 0; i < types.length; ++i) {
            types[i] = Type.getType((String)names.get(i));
        }

        return types;
    }

    public static Signature parseConstructor(Type[] types) {
        StringBuffer sb = new StringBuffer();
        sb.append("(");

        for(int i = 0; i < types.length; ++i) {
            sb.append(types[i].getDescriptor());
        }

        sb.append(")");
        sb.append("V");
        return new Signature("<init>", sb.toString());
    }

    public static Signature parseConstructor(String sig) {
        return parseSignature("void <init>(" + sig + ")");
    }

    private static List parseTypes(String s, int mark, int end) {
        ArrayList types = new ArrayList(5);

        while(true) {
            int next = s.indexOf(44, mark);
            if (next < 0) {
                types.add(map(s.substring(mark, end).trim()));
                return types;
            }

            types.add(map(s.substring(mark, next).trim()));
            mark = next + 1;
        }
    }

    private static String map(String type) {
        if (type.equals("")) {
            return type;
        } else {
            String t = (String)transforms.get(type);
            if (t != null) {
                return t;
            } else if (type.indexOf(46) < 0) {
                return map("java.lang." + type);
            } else {
                StringBuffer sb = new StringBuffer();
                int index = 0;

                while((index = type.indexOf("[]", index) + 1) > 0) {
                    sb.append('[');
                }

                type = type.substring(0, type.length() - sb.length() * 2);
                sb.append('L').append(type.replace('.', '/')).append(';');
                return sb.toString();
            }
        }
    }

    public static Type getBoxedType(Type type) {
        switch(type.getSort()) {
            case 1:
                return Constants.TYPE_BOOLEAN;
            case 2:
                return Constants.TYPE_CHARACTER;
            case 3:
                return Constants.TYPE_BYTE;
            case 4:
                return Constants.TYPE_SHORT;
            case 5:
                return Constants.TYPE_INTEGER;
            case 6:
                return Constants.TYPE_FLOAT;
            case 7:
                return Constants.TYPE_LONG;
            case 8:
                return Constants.TYPE_DOUBLE;
            default:
                return type;
        }
    }

    public static Type getUnboxedType(Type type) {
        if (Constants.TYPE_INTEGER.equals(type)) {
            return Type.INT_TYPE;
        } else if (Constants.TYPE_BOOLEAN.equals(type)) {
            return Type.BOOLEAN_TYPE;
        } else if (Constants.TYPE_DOUBLE.equals(type)) {
            return Type.DOUBLE_TYPE;
        } else if (Constants.TYPE_LONG.equals(type)) {
            return Type.LONG_TYPE;
        } else if (Constants.TYPE_CHARACTER.equals(type)) {
            return Type.CHAR_TYPE;
        } else if (Constants.TYPE_BYTE.equals(type)) {
            return Type.BYTE_TYPE;
        } else if (Constants.TYPE_FLOAT.equals(type)) {
            return Type.FLOAT_TYPE;
        } else {
            return Constants.TYPE_SHORT.equals(type) ? Type.SHORT_TYPE : type;
        }
    }

    public static boolean isArray(Type type) {
        return type.getSort() == 9;
    }

    public static Type getComponentType(Type type) {
        if (!isArray(type)) {
            throw new IllegalArgumentException("Type " + type + " is not an array");
        } else {
            return Type.getType(type.getDescriptor().substring(1));
        }
    }

    public static boolean isPrimitive(Type type) {
        switch(type.getSort()) {
            case 9:
            case 10:
                return false;
            default:
                return true;
        }
    }

    public static String emulateClassGetName(Type type) {
        return isArray(type) ? type.getDescriptor().replace('/', '.') : getClassName(type);
    }

    public static boolean isConstructor(MethodInfo method) {
        return method.getSignature().getName().equals("<init>");
    }

    public static Type[] getTypes(Class[] classes) {
        if (classes == null) {
            return null;
        } else {
            Type[] types = new Type[classes.length];

            for(int i = 0; i < classes.length; ++i) {
                types[i] = Type.getType(classes[i]);
            }

            return types;
        }
    }

    public static int ICONST(int value) {
        switch(value) {
            case -1:
                return 2;
            case 0:
                return 3;
            case 1:
                return 4;
            case 2:
                return 5;
            case 3:
                return 6;
            case 4:
                return 7;
            case 5:
                return 8;
            default:
                return -1;
        }
    }

    public static int LCONST(long value) {
        if (value == 0L) {
            return 9;
        } else {
            return value == 1L ? 10 : -1;
        }
    }

    public static int FCONST(float value) {
        if (value == 0.0F) {
            return 11;
        } else if (value == 1.0F) {
            return 12;
        } else {
            return value == 2.0F ? 13 : -1;
        }
    }

    public static int DCONST(double value) {
        if (value == 0.0D) {
            return 14;
        } else {
            return value == 1.0D ? 15 : -1;
        }
    }

    public static int NEWARRAY(Type type) {
        switch(type.getSort()) {
            case 1:
                return 4;
            case 2:
                return 5;
            case 3:
                return 8;
            case 4:
                return 9;
            case 5:
                return 10;
            case 6:
                return 6;
            case 7:
                return 11;
            case 8:
                return 7;
            default:
                return -1;
        }
    }

    public static String escapeType(String s) {
        StringBuffer sb = new StringBuffer();
        int i = 0;

        for(int len = s.length(); i < len; ++i) {
            char c = s.charAt(i);
            switch(c) {
                case '$':
                    sb.append("$24");
                    break;
                case '(':
                    sb.append("$28");
                    break;
                case ')':
                    sb.append("$29");
                    break;
                case '.':
                    sb.append("$2E");
                    break;
                case '/':
                    sb.append("$2F");
                    break;
                case ';':
                    sb.append("$3B");
                    break;
                case '[':
                    sb.append("$5B");
                    break;
                default:
                    sb.append(c);
            }
        }

        return sb.toString();
    }

    static {
        transforms.put("void", "V");
        transforms.put("byte", "B");
        transforms.put("char", "C");
        transforms.put("double", "D");
        transforms.put("float", "F");
        transforms.put("int", "I");
        transforms.put("long", "J");
        transforms.put("short", "S");
        transforms.put("boolean", "Z");
        CollectionUtils.reverse(transforms, rtransforms);
    }
}
