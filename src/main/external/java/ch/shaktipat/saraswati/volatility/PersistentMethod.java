package ch.shaktipat.saraswati.volatility;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention( RetentionPolicy.RUNTIME )
public @interface PersistentMethod
{
	public Volatility volatility() default Volatility.DYNAMIC;
}
