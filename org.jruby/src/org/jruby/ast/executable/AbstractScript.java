/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jruby.ast.executable;

import java.math.BigInteger;
import org.jcodings.Encoding;
import org.jcodings.EncodingDB;
import org.jruby.Ruby;
import org.jruby.RubyFixnum;
import org.jruby.RubyFloat;
import org.jruby.RubyModule;
import org.jruby.RubyRegexp;
import org.jruby.RubyString;
import org.jruby.RubySymbol;
import org.jruby.internal.runtime.methods.DynamicMethod;
import org.jruby.parser.StaticScope;
import org.jruby.runtime.Block;
import org.jruby.runtime.BlockBody;
import org.jruby.runtime.CompiledBlockCallback;
import org.jruby.runtime.CallSite;
import org.jruby.runtime.MethodIndex;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;
import org.jruby.util.ByteList;

/**
 *
 * @author headius
 */
public abstract class AbstractScript implements Script {
    public AbstractScript() {
    }

    public IRubyObject __file__(ThreadContext context, IRubyObject self, Block block) {
        return __file__(context, self, IRubyObject.NULL_ARRAY, block);
    }
    
    public IRubyObject __file__(ThreadContext context, IRubyObject self, IRubyObject arg, Block block) {
        return __file__(context, self, new IRubyObject[] {arg}, block);
    }
    
    public IRubyObject __file__(ThreadContext context, IRubyObject self, IRubyObject arg1, IRubyObject arg2, Block block) {
        return __file__(context, self, new IRubyObject[] {arg1, arg2}, block);
    }
    
    public IRubyObject __file__(ThreadContext context, IRubyObject self, IRubyObject arg1, IRubyObject arg2, IRubyObject arg3, Block block) {
        return __file__(context, self, new IRubyObject[] {arg1, arg2, arg3}, block);
    }
    
    @Deprecated
    public IRubyObject load(ThreadContext context, IRubyObject self, IRubyObject[] args, Block block) {
        return load(context, self, false);
    }
    
    public IRubyObject load(ThreadContext context, IRubyObject self, boolean wrap) {
        return null;
    }
    
    public IRubyObject run(ThreadContext context, IRubyObject self, IRubyObject[] args, Block block) {
        return __file__(context, self, args, block);
    }

    public RuntimeCache runtimeCache;

    public static final int NUMBERED_SCOPE_COUNT = 10;

    public final StaticScope getScope(ThreadContext context, String varNamesDescriptor, int i) {return runtimeCache.getScope(context, varNamesDescriptor, i);}
    public final StaticScope getScope0(ThreadContext context, String varNamesDescriptor) {return runtimeCache.getScope(context, varNamesDescriptor, 0);}
    public final StaticScope getScope1(ThreadContext context, String varNamesDescriptor) {return runtimeCache.getScope(context, varNamesDescriptor, 1);}
    public final StaticScope getScope2(ThreadContext context, String varNamesDescriptor) {return runtimeCache.getScope(context, varNamesDescriptor, 2);}
    public final StaticScope getScope3(ThreadContext context, String varNamesDescriptor) {return runtimeCache.getScope(context, varNamesDescriptor, 3);}
    public final StaticScope getScope4(ThreadContext context, String varNamesDescriptor) {return runtimeCache.getScope(context, varNamesDescriptor, 4);}
    public final StaticScope getScope5(ThreadContext context, String varNamesDescriptor) {return runtimeCache.getScope(context, varNamesDescriptor, 5);}
    public final StaticScope getScope6(ThreadContext context, String varNamesDescriptor) {return runtimeCache.getScope(context, varNamesDescriptor, 6);}
    public final StaticScope getScope7(ThreadContext context, String varNamesDescriptor) {return runtimeCache.getScope(context, varNamesDescriptor, 7);}
    public final StaticScope getScope8(ThreadContext context, String varNamesDescriptor) {return runtimeCache.getScope(context, varNamesDescriptor, 8);}
    public final StaticScope getScope9(ThreadContext context, String varNamesDescriptor) {return runtimeCache.getScope(context, varNamesDescriptor, 9);}

    public static final int NUMBERED_CALLSITE_COUNT = 10;

    public final CallSite getCallSite(int i) {return runtimeCache.callSites[i];}
    public final CallSite getCallSite0() {return runtimeCache.callSites[0];}
    public final CallSite getCallSite1() {return runtimeCache.callSites[1];}
    public final CallSite getCallSite2() {return runtimeCache.callSites[2];}
    public final CallSite getCallSite3() {return runtimeCache.callSites[3];}
    public final CallSite getCallSite4() {return runtimeCache.callSites[4];}
    public final CallSite getCallSite5() {return runtimeCache.callSites[5];}
    public final CallSite getCallSite6() {return runtimeCache.callSites[6];}
    public final CallSite getCallSite7() {return runtimeCache.callSites[7];}
    public final CallSite getCallSite8() {return runtimeCache.callSites[8];}
    public final CallSite getCallSite9() {return runtimeCache.callSites[9];}

    public static final int NUMBERED_BLOCKBODY_COUNT = 10;

    public final BlockBody getBlockBody(ThreadContext context, int i, String descriptor) {return runtimeCache.getBlockBody(this, context, i, descriptor);}
    public final BlockBody getBlockBody0(ThreadContext context, String descriptor) {return runtimeCache.getBlockBody(this, context, 0, descriptor);}
    public final BlockBody getBlockBody1(ThreadContext context, String descriptor) {return runtimeCache.getBlockBody(this, context, 1, descriptor);}
    public final BlockBody getBlockBody2(ThreadContext context, String descriptor) {return runtimeCache.getBlockBody(this, context, 2, descriptor);}
    public final BlockBody getBlockBody3(ThreadContext context, String descriptor) {return runtimeCache.getBlockBody(this, context, 3, descriptor);}
    public final BlockBody getBlockBody4(ThreadContext context, String descriptor) {return runtimeCache.getBlockBody(this, context, 4, descriptor);}
    public final BlockBody getBlockBody5(ThreadContext context, String descriptor) {return runtimeCache.getBlockBody(this, context, 5, descriptor);}
    public final BlockBody getBlockBody6(ThreadContext context, String descriptor) {return runtimeCache.getBlockBody(this, context, 6, descriptor);}
    public final BlockBody getBlockBody7(ThreadContext context, String descriptor) {return runtimeCache.getBlockBody(this, context, 7, descriptor);}
    public final BlockBody getBlockBody8(ThreadContext context, String descriptor) {return runtimeCache.getBlockBody(this, context, 8, descriptor);}
    public final BlockBody getBlockBody9(ThreadContext context, String descriptor) {return runtimeCache.getBlockBody(this, context, 9, descriptor);}

    public final BlockBody getBlockBody19(ThreadContext context, int i, String descriptor) {return runtimeCache.getBlockBody19(this, context, i, descriptor);}
    public final BlockBody getBlockBody190(ThreadContext context, String descriptor) {return runtimeCache.getBlockBody19(this, context, 0, descriptor);}
    public final BlockBody getBlockBody191(ThreadContext context, String descriptor) {return runtimeCache.getBlockBody19(this, context, 1, descriptor);}
    public final BlockBody getBlockBody192(ThreadContext context, String descriptor) {return runtimeCache.getBlockBody19(this, context, 2, descriptor);}
    public final BlockBody getBlockBody193(ThreadContext context, String descriptor) {return runtimeCache.getBlockBody19(this, context, 3, descriptor);}
    public final BlockBody getBlockBody194(ThreadContext context, String descriptor) {return runtimeCache.getBlockBody19(this, context, 4, descriptor);}
    public final BlockBody getBlockBody195(ThreadContext context, String descriptor) {return runtimeCache.getBlockBody19(this, context, 5, descriptor);}
    public final BlockBody getBlockBody196(ThreadContext context, String descriptor) {return runtimeCache.getBlockBody19(this, context, 6, descriptor);}
    public final BlockBody getBlockBody197(ThreadContext context, String descriptor) {return runtimeCache.getBlockBody19(this, context, 7, descriptor);}
    public final BlockBody getBlockBody198(ThreadContext context, String descriptor) {return runtimeCache.getBlockBody19(this, context, 8, descriptor);}
    public final BlockBody getBlockBody199(ThreadContext context, String descriptor) {return runtimeCache.getBlockBody19(this, context, 9, descriptor);}

    public static final int NUMBERED_BLOCKCALLBACK_COUNT = 10;

    public final CompiledBlockCallback getBlockCallback(Ruby runtime, int i, String method) {return runtimeCache.getBlockCallback(this, runtime, i, method);}
    public final CompiledBlockCallback getBlockCallback0(Ruby runtime, String method) {return runtimeCache.getBlockCallback(this, runtime, 0, method);}
    public final CompiledBlockCallback getBlockCallback1(Ruby runtime, String method) {return runtimeCache.getBlockCallback(this, runtime, 1, method);}
    public final CompiledBlockCallback getBlockCallback2(Ruby runtime, String method) {return runtimeCache.getBlockCallback(this, runtime, 2, method);}
    public final CompiledBlockCallback getBlockCallback3(Ruby runtime, String method) {return runtimeCache.getBlockCallback(this, runtime, 3, method);}
    public final CompiledBlockCallback getBlockCallback4(Ruby runtime, String method) {return runtimeCache.getBlockCallback(this, runtime, 4, method);}
    public final CompiledBlockCallback getBlockCallback5(Ruby runtime, String method) {return runtimeCache.getBlockCallback(this, runtime, 5, method);}
    public final CompiledBlockCallback getBlockCallback6(Ruby runtime, String method) {return runtimeCache.getBlockCallback(this, runtime, 6, method);}
    public final CompiledBlockCallback getBlockCallback7(Ruby runtime, String method) {return runtimeCache.getBlockCallback(this, runtime, 7, method);}
    public final CompiledBlockCallback getBlockCallback8(Ruby runtime, String method) {return runtimeCache.getBlockCallback(this, runtime, 8, method);}
    public final CompiledBlockCallback getBlockCallback9(Ruby runtime, String method) {return runtimeCache.getBlockCallback(this, runtime, 9, method);}

    public static final int NUMBERED_SYMBOL_COUNT = 10;

    public final RubySymbol getSymbol(Ruby runtime, int i, String name) {return runtimeCache.getSymbol(runtime, i, name);}
    public final RubySymbol getSymbol0(Ruby runtime, String name) {return runtimeCache.getSymbol(runtime, 0, name);}
    public final RubySymbol getSymbol1(Ruby runtime, String name) {return runtimeCache.getSymbol(runtime, 1, name);}
    public final RubySymbol getSymbol2(Ruby runtime, String name) {return runtimeCache.getSymbol(runtime, 2, name);}
    public final RubySymbol getSymbol3(Ruby runtime, String name) {return runtimeCache.getSymbol(runtime, 3, name);}
    public final RubySymbol getSymbol4(Ruby runtime, String name) {return runtimeCache.getSymbol(runtime, 4, name);}
    public final RubySymbol getSymbol5(Ruby runtime, String name) {return runtimeCache.getSymbol(runtime, 5, name);}
    public final RubySymbol getSymbol6(Ruby runtime, String name) {return runtimeCache.getSymbol(runtime, 6, name);}
    public final RubySymbol getSymbol7(Ruby runtime, String name) {return runtimeCache.getSymbol(runtime, 7, name);}
    public final RubySymbol getSymbol8(Ruby runtime, String name) {return runtimeCache.getSymbol(runtime, 8, name);}
    public final RubySymbol getSymbol9(Ruby runtime, String name) {return runtimeCache.getSymbol(runtime, 9, name);}

    public static final int NUMBERED_STRING_COUNT = 10;

    public final RubyString getString(Ruby runtime, int i, int codeRange) {return runtimeCache.getString(runtime, i, codeRange);}
    public final RubyString getString0(Ruby runtime, int codeRange) {return runtimeCache.getString(runtime, 0, codeRange);}
    public final RubyString getString1(Ruby runtime, int codeRange) {return runtimeCache.getString(runtime, 1, codeRange);}
    public final RubyString getString2(Ruby runtime, int codeRange) {return runtimeCache.getString(runtime, 2, codeRange);}
    public final RubyString getString3(Ruby runtime, int codeRange) {return runtimeCache.getString(runtime, 3, codeRange);}
    public final RubyString getString4(Ruby runtime, int codeRange) {return runtimeCache.getString(runtime, 4, codeRange);}
    public final RubyString getString5(Ruby runtime, int codeRange) {return runtimeCache.getString(runtime, 5, codeRange);}
    public final RubyString getString6(Ruby runtime, int codeRange) {return runtimeCache.getString(runtime, 6, codeRange);}
    public final RubyString getString7(Ruby runtime, int codeRange) {return runtimeCache.getString(runtime, 7, codeRange);}
    public final RubyString getString8(Ruby runtime, int codeRange) {return runtimeCache.getString(runtime, 8, codeRange);}
    public final RubyString getString9(Ruby runtime, int codeRange) {return runtimeCache.getString(runtime, 9, codeRange);}

    public final ByteList getByteList(int i) {return runtimeCache.getByteList(i);}
    public final ByteList getByteList0() {return runtimeCache.getByteList(0);}
    public final ByteList getByteList1() {return runtimeCache.getByteList(1);}
    public final ByteList getByteList2() {return runtimeCache.getByteList(2);}
    public final ByteList getByteList3() {return runtimeCache.getByteList(3);}
    public final ByteList getByteList4() {return runtimeCache.getByteList(4);}
    public final ByteList getByteList5() {return runtimeCache.getByteList(5);}
    public final ByteList getByteList6() {return runtimeCache.getByteList(6);}
    public final ByteList getByteList7() {return runtimeCache.getByteList(7);}
    public final ByteList getByteList8() {return runtimeCache.getByteList(8);}
    public final ByteList getByteList9() {return runtimeCache.getByteList(9);}

    public static final int NUMBERED_ENCODING_COUNT = 10;

    public final Encoding getEncoding(int i) {return runtimeCache.getEncoding(i);}
    public final Encoding getEncoding0() {return runtimeCache.getEncoding(0);}
    public final Encoding getEncoding1() {return runtimeCache.getEncoding(1);}
    public final Encoding getEncoding2() {return runtimeCache.getEncoding(2);}
    public final Encoding getEncoding3() {return runtimeCache.getEncoding(3);}
    public final Encoding getEncoding4() {return runtimeCache.getEncoding(4);}
    public final Encoding getEncoding5() {return runtimeCache.getEncoding(5);}
    public final Encoding getEncoding6() {return runtimeCache.getEncoding(6);}
    public final Encoding getEncoding7() {return runtimeCache.getEncoding(7);}
    public final Encoding getEncoding8() {return runtimeCache.getEncoding(8);}
    public final Encoding getEncoding9() {return runtimeCache.getEncoding(9);}

    public static final int NUMBERED_FIXNUM_COUNT = 10;

    public final RubyFixnum getFixnum(Ruby runtime, int i, int value) {return runtimeCache.getFixnum(runtime, i, value);}
    public final RubyFixnum getFixnum(Ruby runtime, int i, long value) {return runtimeCache.getFixnum(runtime, i, value);}
    public final RubyFixnum getFixnum0(Ruby runtime, int value) {return runtimeCache.getFixnum(runtime, 0, value);}
    public final RubyFixnum getFixnum1(Ruby runtime, int value) {return runtimeCache.getFixnum(runtime, 1, value);}
    public final RubyFixnum getFixnum2(Ruby runtime, int value) {return runtimeCache.getFixnum(runtime, 2, value);}
    public final RubyFixnum getFixnum3(Ruby runtime, int value) {return runtimeCache.getFixnum(runtime, 3, value);}
    public final RubyFixnum getFixnum4(Ruby runtime, int value) {return runtimeCache.getFixnum(runtime, 4, value);}
    public final RubyFixnum getFixnum5(Ruby runtime, int value) {return runtimeCache.getFixnum(runtime, 5, value);}
    public final RubyFixnum getFixnum6(Ruby runtime, int value) {return runtimeCache.getFixnum(runtime, 6, value);}
    public final RubyFixnum getFixnum7(Ruby runtime, int value) {return runtimeCache.getFixnum(runtime, 7, value);}
    public final RubyFixnum getFixnum8(Ruby runtime, int value) {return runtimeCache.getFixnum(runtime, 8, value);}
    public final RubyFixnum getFixnum9(Ruby runtime, int value) {return runtimeCache.getFixnum(runtime, 9, value);}

    public static final int NUMBERED_FLOAT_COUNT = 10;

    public final RubyFloat getFloat(Ruby runtime, int i, double value) {return runtimeCache.getFloat(runtime, i, value);}
    public final RubyFloat getFloat0(Ruby runtime, double value) {return runtimeCache.getFloat(runtime, 0, value);}
    public final RubyFloat getFloat1(Ruby runtime, double value) {return runtimeCache.getFloat(runtime, 1, value);}
    public final RubyFloat getFloat2(Ruby runtime, double value) {return runtimeCache.getFloat(runtime, 2, value);}
    public final RubyFloat getFloat3(Ruby runtime, double value) {return runtimeCache.getFloat(runtime, 3, value);}
    public final RubyFloat getFloat4(Ruby runtime, double value) {return runtimeCache.getFloat(runtime, 4, value);}
    public final RubyFloat getFloat5(Ruby runtime, double value) {return runtimeCache.getFloat(runtime, 5, value);}
    public final RubyFloat getFloat6(Ruby runtime, double value) {return runtimeCache.getFloat(runtime, 6, value);}
    public final RubyFloat getFloat7(Ruby runtime, double value) {return runtimeCache.getFloat(runtime, 7, value);}
    public final RubyFloat getFloat8(Ruby runtime, double value) {return runtimeCache.getFloat(runtime, 8, value);}
    public final RubyFloat getFloat9(Ruby runtime, double value) {return runtimeCache.getFloat(runtime, 9, value);}

    public static final int NUMBERED_REGEXP_COUNT = 10;

    public final RubyRegexp getRegexp(Ruby runtime, int i, ByteList pattern, int options) {return runtimeCache.getRegexp(runtime, i, pattern, options);}
    public final RubyRegexp getRegexp0(Ruby runtime, ByteList pattern, int options) {return runtimeCache.getRegexp(runtime, 0, pattern, options);}
    public final RubyRegexp getRegexp1(Ruby runtime, ByteList pattern, int options) {return runtimeCache.getRegexp(runtime, 1, pattern, options);}
    public final RubyRegexp getRegexp2(Ruby runtime, ByteList pattern, int options) {return runtimeCache.getRegexp(runtime, 2, pattern, options);}
    public final RubyRegexp getRegexp3(Ruby runtime, ByteList pattern, int options) {return runtimeCache.getRegexp(runtime, 3, pattern, options);}
    public final RubyRegexp getRegexp4(Ruby runtime, ByteList pattern, int options) {return runtimeCache.getRegexp(runtime, 4, pattern, options);}
    public final RubyRegexp getRegexp5(Ruby runtime, ByteList pattern, int options) {return runtimeCache.getRegexp(runtime, 5, pattern, options);}
    public final RubyRegexp getRegexp6(Ruby runtime, ByteList pattern, int options) {return runtimeCache.getRegexp(runtime, 6, pattern, options);}
    public final RubyRegexp getRegexp7(Ruby runtime, ByteList pattern, int options) {return runtimeCache.getRegexp(runtime, 7, pattern, options);}
    public final RubyRegexp getRegexp8(Ruby runtime, ByteList pattern, int options) {return runtimeCache.getRegexp(runtime, 8, pattern, options);}
    public final RubyRegexp getRegexp9(Ruby runtime, ByteList pattern, int options) {return runtimeCache.getRegexp(runtime, 9, pattern, options);}

    public static final int NUMBERED_BIGINTEGER_COUNT = 10;

    public final BigInteger getBigInteger(Ruby runtime, int i, String name) {return runtimeCache.getBigInteger(runtime, i, name);}
    public final BigInteger getBigInteger0(Ruby runtime, String name) {return runtimeCache.getBigInteger(runtime, 0, name);}
    public final BigInteger getBigInteger1(Ruby runtime, String name) {return runtimeCache.getBigInteger(runtime, 1, name);}
    public final BigInteger getBigInteger2(Ruby runtime, String name) {return runtimeCache.getBigInteger(runtime, 2, name);}
    public final BigInteger getBigInteger3(Ruby runtime, String name) {return runtimeCache.getBigInteger(runtime, 3, name);}
    public final BigInteger getBigInteger4(Ruby runtime, String name) {return runtimeCache.getBigInteger(runtime, 4, name);}
    public final BigInteger getBigInteger5(Ruby runtime, String name) {return runtimeCache.getBigInteger(runtime, 5, name);}
    public final BigInteger getBigInteger6(Ruby runtime, String name) {return runtimeCache.getBigInteger(runtime, 6, name);}
    public final BigInteger getBigInteger7(Ruby runtime, String name) {return runtimeCache.getBigInteger(runtime, 7, name);}
    public final BigInteger getBigInteger8(Ruby runtime, String name) {return runtimeCache.getBigInteger(runtime, 8, name);}
    public final BigInteger getBigInteger9(Ruby runtime, String name) {return runtimeCache.getBigInteger(runtime, 9, name);}

    public static final int NUMBERED_VARIABLEREADER_COUNT = 10;

    public final IRubyObject getVariable(Ruby runtime, int i, String name, IRubyObject object) {return runtimeCache.getVariable(runtime, i, name, object);}
    public final IRubyObject getVariable0(Ruby runtime, String name, IRubyObject object) {return runtimeCache.getVariable(runtime, 0, name, object);}
    public final IRubyObject getVariable1(Ruby runtime, String name, IRubyObject object) {return runtimeCache.getVariable(runtime, 1, name, object);}
    public final IRubyObject getVariable2(Ruby runtime, String name, IRubyObject object) {return runtimeCache.getVariable(runtime, 2, name, object);}
    public final IRubyObject getVariable3(Ruby runtime, String name, IRubyObject object) {return runtimeCache.getVariable(runtime, 3, name, object);}
    public final IRubyObject getVariable4(Ruby runtime, String name, IRubyObject object) {return runtimeCache.getVariable(runtime, 4, name, object);}
    public final IRubyObject getVariable5(Ruby runtime, String name, IRubyObject object) {return runtimeCache.getVariable(runtime, 5, name, object);}
    public final IRubyObject getVariable6(Ruby runtime, String name, IRubyObject object) {return runtimeCache.getVariable(runtime, 6, name, object);}
    public final IRubyObject getVariable7(Ruby runtime, String name, IRubyObject object) {return runtimeCache.getVariable(runtime, 7, name, object);}
    public final IRubyObject getVariable8(Ruby runtime, String name, IRubyObject object) {return runtimeCache.getVariable(runtime, 8, name, object);}
    public final IRubyObject getVariable9(Ruby runtime, String name, IRubyObject object) {return runtimeCache.getVariable(runtime, 9, name, object);}

    public static final int NUMBERED_VARIABLEWRITER_COUNT = 10;

    public final IRubyObject setVariable(Ruby runtime, int i, String name, IRubyObject object, IRubyObject value) {return runtimeCache.setVariable(runtime, i, name, object, value);}
    public final IRubyObject setVariable0(Ruby runtime, String name, IRubyObject object, IRubyObject value) {return runtimeCache.setVariable(runtime, 0, name, object, value);}
    public final IRubyObject setVariable1(Ruby runtime, String name, IRubyObject object, IRubyObject value) {return runtimeCache.setVariable(runtime, 1, name, object, value);}
    public final IRubyObject setVariable2(Ruby runtime, String name, IRubyObject object, IRubyObject value) {return runtimeCache.setVariable(runtime, 2, name, object, value);}
    public final IRubyObject setVariable3(Ruby runtime, String name, IRubyObject object, IRubyObject value) {return runtimeCache.setVariable(runtime, 3, name, object, value);}
    public final IRubyObject setVariable4(Ruby runtime, String name, IRubyObject object, IRubyObject value) {return runtimeCache.setVariable(runtime, 4, name, object, value);}
    public final IRubyObject setVariable5(Ruby runtime, String name, IRubyObject object, IRubyObject value) {return runtimeCache.setVariable(runtime, 5, name, object, value);}
    public final IRubyObject setVariable6(Ruby runtime, String name, IRubyObject object, IRubyObject value) {return runtimeCache.setVariable(runtime, 6, name, object, value);}
    public final IRubyObject setVariable7(Ruby runtime, String name, IRubyObject object, IRubyObject value) {return runtimeCache.setVariable(runtime, 7, name, object, value);}
    public final IRubyObject setVariable8(Ruby runtime, String name, IRubyObject object, IRubyObject value) {return runtimeCache.setVariable(runtime, 8, name, object, value);}
    public final IRubyObject setVariable9(Ruby runtime, String name, IRubyObject object, IRubyObject value) {return runtimeCache.setVariable(runtime, 9, name, object, value);}

    public static final int NUMBERED_CONSTANT_COUNT = 10;

    public final IRubyObject getConstant(ThreadContext context, String name, int i) {return runtimeCache.getConstant(context, name, i);}
    public final IRubyObject getConstant0(ThreadContext context, String name) {return runtimeCache.getConstant(context, name, 0);}
    public final IRubyObject getConstant1(ThreadContext context, String name) {return runtimeCache.getConstant(context, name, 1);}
    public final IRubyObject getConstant2(ThreadContext context, String name) {return runtimeCache.getConstant(context, name, 2);}
    public final IRubyObject getConstant3(ThreadContext context, String name) {return runtimeCache.getConstant(context, name, 3);}
    public final IRubyObject getConstant4(ThreadContext context, String name) {return runtimeCache.getConstant(context, name, 4);}
    public final IRubyObject getConstant5(ThreadContext context, String name) {return runtimeCache.getConstant(context, name, 5);}
    public final IRubyObject getConstant6(ThreadContext context, String name) {return runtimeCache.getConstant(context, name, 6);}
    public final IRubyObject getConstant7(ThreadContext context, String name) {return runtimeCache.getConstant(context, name, 7);}
    public final IRubyObject getConstant8(ThreadContext context, String name) {return runtimeCache.getConstant(context, name, 8);}
    public final IRubyObject getConstant9(ThreadContext context, String name) {return runtimeCache.getConstant(context, name, 9);}

    public static final int NUMBERED_CONSTANTFROM_COUNT = 10;

    public final IRubyObject getConstantFrom(RubyModule target, ThreadContext context, String name, int i) {return runtimeCache.getConstantFrom(target, context, name, i);}
    public final IRubyObject getConstantFrom0(RubyModule target, ThreadContext context, String name) {return runtimeCache.getConstantFrom(target, context, name, 0);}
    public final IRubyObject getConstantFrom1(RubyModule target, ThreadContext context, String name) {return runtimeCache.getConstantFrom(target, context, name, 1);}
    public final IRubyObject getConstantFrom2(RubyModule target, ThreadContext context, String name) {return runtimeCache.getConstantFrom(target, context, name, 2);}
    public final IRubyObject getConstantFrom3(RubyModule target, ThreadContext context, String name) {return runtimeCache.getConstantFrom(target, context, name, 3);}
    public final IRubyObject getConstantFrom4(RubyModule target, ThreadContext context, String name) {return runtimeCache.getConstantFrom(target, context, name, 4);}
    public final IRubyObject getConstantFrom5(RubyModule target, ThreadContext context, String name) {return runtimeCache.getConstantFrom(target, context, name, 5);}
    public final IRubyObject getConstantFrom6(RubyModule target, ThreadContext context, String name) {return runtimeCache.getConstantFrom(target, context, name, 6);}
    public final IRubyObject getConstantFrom7(RubyModule target, ThreadContext context, String name) {return runtimeCache.getConstantFrom(target, context, name, 7);}
    public final IRubyObject getConstantFrom8(RubyModule target, ThreadContext context, String name) {return runtimeCache.getConstantFrom(target, context, name, 8);}
    public final IRubyObject getConstantFrom9(RubyModule target, ThreadContext context, String name) {return runtimeCache.getConstantFrom(target, context, name, 9);}

    public static final int NUMBERED_METHOD_COUNT = 10;

    protected DynamicMethod getMethod(ThreadContext context, IRubyObject self, int i, String methodName) {
        return runtimeCache. getMethod(context, self, i, methodName);
    }
    protected DynamicMethod getMethod0(ThreadContext context, IRubyObject self, String methodName) {
        return runtimeCache. getMethod(context, self, 0, methodName);
    }
    protected DynamicMethod getMethod1(ThreadContext context, IRubyObject self, String methodName) {
        return runtimeCache. getMethod(context, self, 1, methodName);
    }
    protected DynamicMethod getMethod2(ThreadContext context, IRubyObject self, String methodName) {
        return runtimeCache. getMethod(context, self, 2, methodName);
    }
    protected DynamicMethod getMethod3(ThreadContext context, IRubyObject self, String methodName) {
        return runtimeCache. getMethod(context, self, 3, methodName);
    }
    protected DynamicMethod getMethod4(ThreadContext context, IRubyObject self, String methodName) {
        return runtimeCache. getMethod(context, self, 4, methodName);
    }
    protected DynamicMethod getMethod5(ThreadContext context, IRubyObject self, String methodName) {
        return runtimeCache. getMethod(context, self, 5, methodName);
    }
    protected DynamicMethod getMethod6(ThreadContext context, IRubyObject self, String methodName) {
        return runtimeCache. getMethod(context, self, 6, methodName);
    }
    protected DynamicMethod getMethod7(ThreadContext context, IRubyObject self, String methodName) {
        return runtimeCache. getMethod(context, self, 7, methodName);
    }
    protected DynamicMethod getMethod8(ThreadContext context, IRubyObject self, String methodName) {
        return runtimeCache. getMethod(context, self, 8, methodName);
    }
    protected DynamicMethod getMethod9(ThreadContext context, IRubyObject self, String methodName) {
        return runtimeCache. getMethod(context, self, 9, methodName);
    }

    public void setByteList(int index, String str, Encoding encoding) {
        // decode chars back into bytes
        char[] chars = str.toCharArray();
        byte[] bytes = new byte[chars.length];
        for (int i = 0; i < chars.length; i++) {
            bytes[i] = (byte)chars[i];
        }

        runtimeCache.byteLists[index] = new ByteList(bytes, encoding, false);
    }

    public void setEncoding(int index, String encStr) {
        runtimeCache.encodings[index] = EncodingDB.getEncodings().get(encStr.getBytes()).getEncoding();
    }

    public static CallSite[] setCallSite(CallSite[] callSites, int index, String name) {
        callSites[index] = MethodIndex.getCallSite(name);
        return callSites;
    }

    public static CallSite[] setFunctionalCallSite(CallSite[] callSites, int index, String name) {
        callSites[index] = MethodIndex.getFunctionalCallSite(name);
        return callSites;
    }

    public static CallSite[] setVariableCallSite(CallSite[] callSites, int index, String name) {
        callSites[index] = MethodIndex.getVariableCallSite(name);
        return callSites;
    }

    public static CallSite[] setSuperCallSite(CallSite[] callSites, int index) {
        callSites[index] = MethodIndex.getSuperCallSite();
        return callSites;
    }

    public final void setFilename(String filename) {
        this.filename = filename;
    }

    public final void initFromDescriptor(String descriptor) {
        runtimeCache.initFromDescriptor(descriptor);
    }

    protected String filename;
}
