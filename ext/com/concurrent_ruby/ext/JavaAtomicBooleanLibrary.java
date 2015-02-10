package com.concurrent_ruby.ext;

import java.io.IOException;
import org.jruby.Ruby;
import org.jruby.RubyClass;
import org.jruby.RubyModule;
import org.jruby.RubyObject;
import org.jruby.anno.JRubyClass;
import org.jruby.anno.JRubyMethod;
import org.jruby.runtime.ObjectAllocator;
import org.jruby.runtime.builtin.IRubyObject;
import org.jruby.runtime.load.Library;
import java.util.concurrent.atomic.AtomicBoolean;
import org.jruby.RubyBoolean;
import org.jruby.RubyNil;
import org.jruby.runtime.ThreadContext;

public class JavaAtomicBooleanLibrary implements Library {

    public void load(Ruby runtime, boolean wrap) throws IOException {
        RubyModule concurrentMod = runtime.defineModule("Concurrent");
        RubyClass atomicCls = concurrentMod.defineClassUnder("JavaAtomicBoolean", runtime.getObject(), JRUBYREFERENCE_ALLOCATOR);
        atomicCls.defineAnnotatedMethods(JavaAtomicBoolean.class);
    }

    private static final ObjectAllocator JRUBYREFERENCE_ALLOCATOR = new ObjectAllocator() {
        public IRubyObject allocate(Ruby runtime, RubyClass klazz) {
            return new JavaAtomicBoolean(runtime, klazz);
        }
    };

    @JRubyClass(name = "JavaAtomicBoolean", parent = "Object")
    public static class JavaAtomicBoolean extends RubyObject {

        private AtomicBoolean atomicBoolean;
        private ThreadContext context;

        public JavaAtomicBoolean(Ruby runtime, RubyClass metaClass) {
            super(runtime, metaClass);
        }

        @JRubyMethod
        public IRubyObject initialize(ThreadContext context, IRubyObject value) {
            atomicBoolean = new AtomicBoolean(convertRubyBooleanToJavaBoolean(value));
            this.context = context;
            return context.nil;
        }

        @JRubyMethod
        public IRubyObject initialize(ThreadContext context) {
            atomicBoolean = new AtomicBoolean();
            this.context = context;
            return context.nil;
        }

        @JRubyMethod(name = "value")
        public IRubyObject value() {
            return RubyBoolean.newBoolean(getRuntime(), atomicBoolean.get());
        }

        @JRubyMethod(name = "true?")
        public IRubyObject isAtomicTrue() {
            return RubyBoolean.newBoolean(getRuntime(), atomicBoolean.get());
        }

        @JRubyMethod(name = "false?")
        public IRubyObject isAtomicFalse() {
            return RubyBoolean.newBoolean(getRuntime(), (atomicBoolean.get() == false));
        }

        @JRubyMethod(name = "value=")
        public IRubyObject setAtomic(IRubyObject newValue) {
            atomicBoolean.set(convertRubyBooleanToJavaBoolean(newValue));
            return context.nil;
        }

        @JRubyMethod(name = "make_true")
        public IRubyObject makeTrue() {
            return RubyBoolean.newBoolean(getRuntime(), atomicBoolean.compareAndSet(false, true));
        }

        @JRubyMethod(name = "make_false")
        public IRubyObject makeFalse() {
            return RubyBoolean.newBoolean(getRuntime(), atomicBoolean.compareAndSet(true, false));
        }

        private boolean convertRubyBooleanToJavaBoolean(IRubyObject newValue) {
            if (newValue instanceof RubyBoolean.False || newValue instanceof RubyNil) {
                return false;
            } else {
                return true;
            }
        }
    }
}

