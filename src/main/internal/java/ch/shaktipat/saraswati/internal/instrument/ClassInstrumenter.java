package ch.shaktipat.saraswati.internal.instrument;

import java.util.Set;

import javassist.ClassPool;
import javassist.CtBehavior;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.CtNewConstructor;
import javassist.Modifier;
import javassist.bytecode.Bytecode;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.ConstPool;
import javassist.bytecode.Descriptor;
import javassist.bytecode.MethodInfo;
import ch.shaktipat.exwrapper.javassist.ClassPoolWrapper;
import ch.shaktipat.exwrapper.javassist.CtClassWrapper;
import ch.shaktipat.exwrapper.javassist.CtConstructorWrapper;
import ch.shaktipat.exwrapper.javassist.CtMethodWrapper;
import ch.shaktipat.exwrapper.javassist.CtNewConstructorWrapper;
import ch.shaktipat.exwrapper.javassist.CtNewMethodWrapper;
import ch.shaktipat.exwrapper.javassist.bytecode.CodeAttributeWrapper;
import ch.shaktipat.exwrapper.javassist.bytecode.MethodInfoWrapper;
import ch.shaktipat.saraswati.internal.common.FieldAndMethodConstants;
import ch.shaktipat.saraswati.internal.common.SaraswatiHashSet;
import ch.shaktipat.saraswati.internal.engine.PEngine;
import ch.shaktipat.saraswati.internal.instrument.method.InstrumentationHelper;
import ch.shaktipat.saraswati.internal.pobject.PersistentProcessImpl;

public class ClassInstrumenter
{
	private CtClass ctClass = null;
	private ClassPool classPool = null;
	private ConstPool constPool = null;
	
	public ClassInstrumenter( CtClass ctClass )
	{
		super();
		this.ctClass = ctClass;
		this.classPool = ctClass.getClassPool();
		this.constPool = ctClass.getClassFile().getConstPool();
	}

	public byte[] getByteCodeForPersistentClass()
	{
		Set< CtMethod > saraswatiMethods = copyJavaToSaraswatiMethods();
		
		for ( CtMethod saraswatiMethod : saraswatiMethods )
		{
			MethodInstrumenter methodInstrumenter = new MethodInstrumenter( saraswatiMethod );
			methodInstrumenter.instrumentMethod();
		}
		
		return getByteCodeForClass( true );
	}

	public byte[] getByteCodeForNonPersistentClass()
	{
		return getByteCodeForClass( false );
	}
	
	private byte[] getByteCodeForClass( boolean instrumentStaticInitializer )
	{
		// All classes have a dummy constructor, however interfaces,
		// enums and annotations are not touched at all.
		if ( !( ctClass.isInterface() || ctClass.isEnum() || ctClass.isAnnotation() ) )
		{
			insertDummyConstructor();
			insertForceStaticInitializationMethod();
			
			if ( instrumentStaticInitializer )
				instrumentStaticInitializer();
			
			// This first output is for cases where computeMaxStack()
			// is failing --> the byte code will have an incorrect
			// max stack size but at least we can look at it.
			writeClassFileForDebugging();

			// Recompute the stack size for all methods.
			// Note that I am doing this here rather than in MethodInstrumenter
			// to ensure that the class file gets output even if it is broken.
			// This is very important for debugging.
			computeMaxStack();

			// This second output is for "normal" cases where computeMaxStack()
			// does not fail --> in this case the file from several lines
			// above will be overwritten.
			writeClassFileForDebugging();
		}
		
		byte[] bytecode = CtClassWrapper.toBytecode( ctClass );

		// Certain methods, such as CtClass.getMethodInfo(), require that the class be
		// unfrozen. Since we rely on being able to invoke some of these methods
		// subsequent to its being loaded, we simply categorically disable the freezing
		// mechanism here (toBytecode() is what causes the frozen flag to be set).
		ctClass.defrost();
		
		return bytecode;
	}
	
	private void writeClassFileForDebugging()
	{
		String instrumentedOutputDir = PEngine.getPEngine().getSaraswatiConfiguration().getDebugDir();

		if ( instrumentedOutputDir != null )
		{
			CtClassWrapper.writeFile( ctClass, instrumentedOutputDir );
			ctClass.defrost();
		}
	}
	
	private void computeMaxStack()
	{
		for ( CtMethod ctMethod : ctClass.getDeclaredMethods() )
		{
			MethodInfo methodInfo = ctMethod.getMethodInfo();

			// Skip any abstract or interface methods
			if ( !InstrumentationHelper.methodHasImplementation( methodInfo ) )
			{
				continue;
			}

			CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
			CodeAttributeWrapper.computeMaxStack( codeAttribute );
			MethodInfoWrapper.rebuildStackMap( methodInfo, classPool );
		}
	}
	
	private void instrumentStaticInitializer()
	{
		// Unfortunately, CtClass.getDeclaredConstructors() *only* returns
		// object constructors, but static constructors. However, if we
		// find the static constructor via getDeclaredBehaviors() we see
		// that it is an instance of CtConstructor. Another great feature
		// of javassist API...
		for ( CtBehavior ctBehavior : ctClass.getDeclaredBehaviors() )
		{
			if ( ctBehavior.getName().equals( FieldAndMethodConstants.CLINIT_METHOD ) )
			{
				String code = "if ( " + PersistentProcessImpl.class.getName() + "." +
					FieldAndMethodConstants.GET_CURRENT_PROCESS_OID_METHOD + "() != null ) return;";
				CtConstructorWrapper.insertBefore( (CtConstructor)ctBehavior, code );
				break;
			}
		}
	}
	
	private void insertDummyConstructor()
	{
		// Create a new constructor using the dummy type
		// to avoid any naming conflicts.
		CtClass dummyType = ClassPoolWrapper.get( classPool, __saraswati_dummy_type.class.getName() );
		CtConstructor dummyConstructor = CtNewConstructorWrapper.make( new CtClass[] { dummyType }, new CtClass[] {}, CtNewConstructor.PASS_NONE, null, null, ctClass );

		// The empty constructor we are instrumenting has the form:
		//   0: aload_0
		//   1: invokespecial #X	// Method SuperClass."<init>":()V
		//   4: return
		// We want to replace this with:
		//   0: aload_0
		//   1: aload_1
		//   2: invokespecial #Y	// Method SuperClass."<init>":(L__saraswati_dummy_type;)V
		//   5: return
		
		CtClass superClass = CtClassWrapper.getSuperclass( ctClass );
		CtClass objectType = ClassPoolWrapper.get( classPool, Object.class.getName() );
		
		// Only instrument if this class's super class is not java.lang.Object.
		if ( !superClass.equals( objectType ) )
		{
			String methodDescriptor = Descriptor.ofConstructor( new CtClass[] { dummyType } );

			Bytecode bytecode = new Bytecode( constPool, 2, 2 );
			bytecode.add( Bytecode.ALOAD_0 );
			bytecode.add( Bytecode.ALOAD_1 );
			bytecode.addInvokespecial( superClass, FieldAndMethodConstants.INIT_METHOD, methodDescriptor );
			bytecode.add( Bytecode.RETURN );
			
			dummyConstructor.getMethodInfo().setCodeAttribute( bytecode.toCodeAttribute() );
		}
		
		CtClassWrapper.addConstructor( ctClass, dummyConstructor );
	}
	
	private void insertForceStaticInitializationMethod()
	{
		CtMethod ctMethod = CtMethodWrapper.make( "public static void " + FieldAndMethodConstants.SARASWATI_FORCE_STATIC_INITIALIZATION_METHOD + "() {}", ctClass );
		CtClassWrapper.addMethod( ctClass, ctMethod );
	}
	
	private Set< CtMethod > copyJavaToSaraswatiMethods()
	{
		Set< CtMethod > saraswatiMethods = new SaraswatiHashSet< CtMethod >();
		
		for ( CtBehavior ctBehavior : ctClass.getDeclaredBehaviors() )
		{
			String saraswatiName = PEngine.getPEngine().getPClassLoader().getSymbolRegistry().getSaraswatiMethodName( ctClass, ctBehavior );
			CtMethod saraswatiMethod = null;
			
			if ( ctBehavior instanceof CtConstructor )
			{
				CtConstructor ctConstructor = (CtConstructor)ctBehavior;
				saraswatiMethod = CtConstructorWrapper.toMethod( ctConstructor, saraswatiName, ctClass );
				CodeAttribute newCodeAttribute = (CodeAttribute)ctConstructor.getMethodInfo().getCodeAttribute().copy( constPool, null );
				saraswatiMethod.getMethodInfo().setCodeAttribute( newCodeAttribute );
			}
			else if ( ctBehavior instanceof CtMethod )
			{
				CtMethod ctMethod = (CtMethod)ctBehavior;
				
				// Skip abstract methods.
				if ( ( ctMethod.getModifiers() & Modifier.ABSTRACT ) > 0 )
					continue;
				
				saraswatiMethod = CtNewMethodWrapper.copy( ctMethod, saraswatiName, ctClass, null );
			}
			else
				throw new IllegalStateException( "Unexpected type for ctBehavior: " + ctBehavior.getClass().getName() );

			CtClassWrapper.addMethod( ctClass, saraswatiMethod );
			saraswatiMethods.add( saraswatiMethod );
		}
		
		return saraswatiMethods;
	}
}
