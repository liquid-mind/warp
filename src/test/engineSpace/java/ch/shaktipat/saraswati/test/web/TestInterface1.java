package ch.shaktipat.saraswati.test.web;

import java.util.Date;

public interface TestInterface1
{
	public void method1();
	public String method2( int someArg, Date[] someOtherArg );
	public void method3( String ... strings  );
	public void method4() throws RuntimeException, IllegalStateException;
}
