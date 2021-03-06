package org.ser.instrumentation;

//import org.ser.instrumentation.CurEnv;
//import org.ser.instrumentation.Record;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.Iterator;


import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtBehavior;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.AttributeInfo;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.SignatureAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.ser.instrumentation.storage;
import org.ser.instrumentation.CurEnv;

public class LoggerAgent implements ClassFileTransformer {
	private static String targetmethod;
    private static String targetpackage;

    public static String GetTargetMethod() {
        return targetmethod;
    }

    public static String GetTargetPackage() { 
        return targetpackage;
    }

	public static void premain(String agentArgument,Instrumentation instrumentation) {
		ClassLoader cl = ClassLoader.getSystemClassLoader();  // 类加载器
		System.out.println("Entered PreMain");	
		if (agentArgument != null) {
			String[] args = agentArgument.split("::");
            targetpackage = args[0];
            targetmethod = args[1];
		//	Set<String> argSet = new HashSet<String>(Arrays.asList(args));

        //    for (String str: argSet) { 
        //        System.out.println(str);
        //    }    
//			if (argSet.contains("time")) {
//				System.out.println("Start at " + new Date());
//				Runtime.getRuntime().addShutdownHook(new Thread() {
//					public void run() {
//						System.out.println("Stop at " + new Date());
//					}
//				});
//			}
		}
        storage.init(); ///// 在这里调用不知道适不适合 
		instrumentation.addTransformer(new LoggerAgent());           // 回调LoggerAgent		
		System.out.println("Leaving PreMain");
	}

	public byte[] transform(ClassLoader loader, String className,
			Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
			byte[] classfileBuffer) throws IllegalClassFormatException {	// classfileBuffer是类文件加载时的原始字节码	
        //System.out.println(className + "mdzz");
        //System.out.println(className.replaceAll("/", ".") + "mdzz");

//        ClassPool pool = ClassPool.getDefault();
//        CtClass cl = pool.getCtClass(className.replaceAll("/", "."));
        String className_ = className.replaceAll("/", ".");
//        System.out.println("999999" + className);
//		if (className.contains("halfninja")) {
        if (className_.contains(targetpackage)) {
//			System.out.println("Enter" + className);
			return doClass(className_, classBeingRedefined, classfileBuffer);  // classfileBuffer被当做参数传递到doClass方法中进行处理
		}

		return classfileBuffer;
	}	
	private byte[] doClass(String name, Class<?> clazz, byte[] b) {   // b参数是类文件加载时的原始字节码		
	    CtClass cl = null;
	    try {
		    ClassPool pool = ClassPool.getDefault();
	      	cl = pool.makeClass(new java.io.ByteArrayInputStream(b));  // 构造一个类
	      	if (cl.isInterface() == false) {			   // 判断是类还是接口,如果是类,则可以进入进行操作各方法.
			CtBehavior[] methods = cl.getDeclaredBehaviors();
	        	System.out.println("Analyse " + name + "'s method...");
	        	for (int i = 0; i < methods.length; i++) {
		        	//if ("Search".equals(methods[i].getName())) {	 // 开始操作用户输入的class的method,这里操作的是BruteForce.Search
	                		doMethod(cl, methods[i]);                // doMethod()中的cl参数用于存储修改后的class
	            	//	}
	        	}
	        	b = cl.toBytecode();                          // 转化为字节码
	      }
	   } catch (Exception e) {
	    	e.printStackTrace();
	      System.err.println("Could not instrument  " + name
	          + ",  exception : " + e.getMessage());
	   } finally {
	      if (cl != null) {
	        cl.detach();
	      }
	   }
		
	   return b;
	}
	
	
	private void doMethod(CtClass cl, CtBehavior method)
		      throws NotFoundException, CannotCompileException {
		
// withGenerator
	System.out.println("Enter doMethod");
	method.insertBefore("java.lang.StackTraceElement[] trace = java.lang.Thread.currentThread().getStackTrace();if (org.ser.instrumentation.LoggerAgent.GetTargetMethod().equals(trace[1].getMethodName()) && trace[1].getClassName().contains(org.ser.instrumentation.LoggerAgent.GetTargetPackage())){org.ser.instrumentation.CurEnv.SetEnable();}if(org.ser.instrumentation.CurEnv.isEnable()  && trace[1].getClassName().contains(\"halfninja\")){org.ser.instrumentation.CurEnv.incdepth();String funcname = trace[1].getMethodName();String classname = trace[1].getClassName();String unique = classname + \".\" + funcname;int depth = org.ser.instrumentation.CurEnv.getdepth();if (depth > 1){org.ser.instrumentation.storage.addIndex(unique);System.out.println(unique);System.out.println(\"Here1\");int index = org.ser.instrumentation.storage.getindex(unique);org.ser.instrumentation.storage.addRevIndex(index, unique);String pfuncname = trace[2].getMethodName();String pclassname = trace[2].getClassName();String punique = pclassname + \".\" + pfuncname;System.out.println(punique);System.out.println(\"Here2\");int pindex = org.ser.instrumentation.storage.getindex(punique);org.ser.instrumentation.storage.updateadj(pindex, index);long stime = System.nanoTime();org.ser.instrumentation.storage.pushtime(stime);}if (depth == 1){org.ser.instrumentation.storage.addIndex(unique);int index = org.ser.instrumentation.storage.getindex(unique);org.ser.instrumentation.storage.addRevIndex(index, unique);long stime = System.nanoTime();org.ser.instrumentation.storage.pushtime(stime);}}");


	method.insertAfter("if(org.ser.instrumentation.CurEnv.isEnable()){long endtime = System.nanoTime();java.lang.StackTraceElement[] trace_ = java.lang.Thread.currentThread().getStackTrace();int depth_ = org.ser.instrumentation.CurEnv.getdepth();String funcname_ = trace_[1].getMethodName();String classname_ = trace_[1].getClassName();String pfuncname_ = trace_[2].getMethodName();String pclassname_ = trace_[2].getClassName();String unique_ = classname_ + \".\" + funcname_;String punique_ = pclassname_ + \".\" + pfuncname_;if (depth_ > 1) {System.out.println(\"depth is: \"+ depth_);int index_ = org.ser.instrumentation.storage.getindex(unique_);int pindex_ = org.ser.instrumentation.storage.getindex(punique_);System.out.println(\"current signature is\" + unique_);long deltime = endtime - org.ser.instrumentation.storage.gettoptime();org.ser.instrumentation.storage.updatetime(pindex_, index_, deltime);}if(depth_ == 1){System.out.println(\"depth is: \"+ depth_);org.ser.instrumentation.CurEnv.SetUnable();long deltime = endtime - org.ser.instrumentation.storage.gettoptime();org.ser.instrumentation.storage.updatetotaltime(deltime);System.out.println(\"rootsignature is:\" + unique_);org.ser.instrumentation.GenDotGraph.GenGraph(unique_);}org.ser.instrumentation.CurEnv.decdepth();}");

////->org.ser.instrumentation.storage.printtotaltime();
////->org.ser.instrumentation.GenDotGraph.GenGraph(unique_);

    }
}
	
