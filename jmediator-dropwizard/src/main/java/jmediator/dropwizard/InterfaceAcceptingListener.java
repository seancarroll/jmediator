package jmediator.dropwizard;

import jersey.repackaged.org.objectweb.asm.*;
import org.glassfish.jersey.internal.OsgiRegistry;
import org.glassfish.jersey.internal.util.ReflectionHelper;
import org.glassfish.jersey.server.internal.LocalizationMessages;
import org.glassfish.jersey.server.internal.scanning.ResourceProcessor;

import java.io.IOException;
import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

// https://github.com/ronmamo/reflections
// http://commons.apache.org/proper/commons-bcel/
// https://asm.ow2.io/
public class InterfaceAcceptingListener { //implements ResourceProcessor {

    private final ClassLoader classloader;

    private final Set<Class<?>> classes;

    private final Set<String> interfaces;

    private final InterfaceClassVisitor classVisitor;


    public InterfaceAcceptingListener(final ClassLoader classloader, final Class... interfaces) {
        this.classloader = classloader;
        this.classes = new LinkedHashSet<>();
        this.interfaces = getInterfacesSet(interfaces);
        this.classVisitor = new InterfaceClassVisitor();
    }

//    @Override
//    public boolean accept(String name) {
//        return !(name == null || name.isEmpty()) && name.endsWith(".class");
//    }
//
//    @Override
//    public void process(String name, InputStream in) throws IOException {
//        new ClassReader(in).accept(classVisitor, 0);
//    }

//    if (yourClass.isInterface()) {
//        //do something
//    }

//    public class MyClassVisitor extends ClassVisitor {
//        public List<MethodCallers> methodCallers;
//
//        public MyClassVisitor() {
//            super(Opcodes.ASM4);
//            this.methodCallers = new ArrayList<>();
//        }
//
//        @Override
//        public MethodVisitor visitMethod(int access, String name, String desc, String        signature, String[] exceptions) {
//            MethodCallers methodData = new MethodCallers(name, desc);
//            this.methodCallers.add(methodData);
//            return new MyMethodVisitor(methodData);
//        }
//    }


    private Set<String> getInterfacesSet(final Class... interfaces) {
        final Set<String> a = new HashSet<String>();
        for (final Class c : interfaces) {
            a.add("L" + c.getName().replaceAll("\\.", "/") + ";");
        }
        return a;
    }

    private final class InterfaceClassVisitor extends ClassVisitor {

        /**
         * The name of the visited class.
         */
        private String className;
        /**
         * True if the class has the correct scope
         */
        private boolean isScoped;
        /**
         * True if the class has the correct declared annotations
         */
        private boolean isInterface;

        private InterfaceClassVisitor() {
            super(Opcodes.ASM5);
        }

        public void visit(final int version, final int access, final String name,
                          final String signature, final String superName, final String[] interfaces) {
            className = name;
            isScoped = (access & Opcodes.ACC_PUBLIC) != 0;
            isInterface = false;
        }

        public void visitInnerClass(final String name, final String outerName,
                                    final String innerName, final int access) {
            // If the name of the class that was visited is equal
            // to the name of this visited inner class then
            // this access field needs to be used for checking the scope
            // of the inner class
            if (className.equals(name)) {
                isScoped = (access & Opcodes.ACC_PUBLIC) != 0;

                // Inner classes need to be statically scoped
                isScoped &= (access & Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC;
            }
        }

//        public void visitEnd() {
//            if (isScoped && isInterface) {
//                // Correctly scoped and annotated
//                // add to the set of matching classes.
//                classes.add(getClassForName(className.replaceAll("/", ".")));
//            }
//        }

        public void visitOuterClass(final String string, final String string0,
                                    final String string1) {
            // Do nothing
        }

        public FieldVisitor visitField(final int i, final String string,
                                       final String string0, final String string1,
                                       final Object object) {
            // Do nothing
            return null;
        }

        public void visitSource(final String string, final String string0) {
            // Do nothing
        }

        public void visitAttribute(final Attribute attribute) {
            // Do nothing
        }

        public MethodVisitor visitMethod(final int i, final String string,
                                         final String string0, final String string1,
                                         final String[] string2) {
            // Do nothing
            return null;
        }

//        private Class getClassForName(final String className) {
//            try {
//                final OsgiRegistry osgiRegistry = ReflectionHelper.getOsgiRegistryInstance();
//
//                if (osgiRegistry != null) {
//                    return osgiRegistry.classForNameWithException(className);
//                } else {
//                    return AccessController.doPrivileged(ReflectionHelper.classForNameWithExceptionPEA(className, classloader));
//                }
//            } catch (final ClassNotFoundException ex) {
//                throw new RuntimeException(LocalizationMessages.ERROR_SCANNING_CLASS_NOT_FOUND(className), ex);
//            } catch (final PrivilegedActionException pae) {
//                final Throwable cause = pae.getCause();
//                if (cause instanceof ClassNotFoundException) {
//                    throw new RuntimeException(LocalizationMessages.ERROR_SCANNING_CLASS_NOT_FOUND(className), cause);
//                } else if (cause instanceof RuntimeException) {
//                    throw (RuntimeException) cause;
//                } else {
//                    throw new RuntimeException(cause);
//                }
//            }
//        }

    }

}
