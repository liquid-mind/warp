package ch.shaktipat.saraswati.internal.dynproxies;

public enum ProxyType
{
	// SELF/OTHER
	// -Purpose: internal access to pobject within same pobject
	// -One proxy instance per pobject instance (proxy owned by pobject)
	// -Proxy instances held for short periods (one method call)
	// -Implements internal interface (e.g., PersistentProcess)

	// -memoryModel == SHARED
	// -May be obtained as follows:
	//   1. statically for current pobject
	//   2. dynamically for "this"
	INTERNAL_SELF,

	// -memoryModel == NON_SHARED
	// -May be obtained as follows:
	//   1. statically for specific OID
	INTERNAL_OTHER,

	// -Purpose: external access to pobject
	// -Many proxy instances per pobject instance (proxy owned by client)
	// -Proxy instances held for extended periods (many method calls)
	// -Implements external interface (e.g., PProcess)
	// -May be obtained as follows:
	//   1. statically for current process
	//   2. statically for specific OID

	// -memoryModel == SHARED
	// -May be obtained as follows:
	//   1. statically for current pobject
	EXTERNAL_SELF,
	
	// -memoryModel == NON_SHARED
	// -May be obtained as follows:
	//   2. statically for specific OID
	EXTERNAL_OTHER
}
