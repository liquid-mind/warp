package ch.shaktipat.saraswati.internal.instrument;

public class NewDupInvokespecialTuple
{
	public static final int UNDEFINED_VALUE = -1;
	
	private int locationOfNewDup;
	private int locationOfInvokespecial;
	
	public NewDupInvokespecialTuple( int locationOfNewDup )
	{
		super();
		this.locationOfNewDup = locationOfNewDup;
		this.locationOfInvokespecial = UNDEFINED_VALUE;
	}

	public int getLocationOfNewDup()
	{
		return locationOfNewDup;
	}

	public void setLocationOfNewDup( int locationOfNewDup )
	{
		this.locationOfNewDup = locationOfNewDup;
	}

	public int getLocationOfInvokespecial()
	{
		return locationOfInvokespecial;
	}

	public void setLocationOfInvokespecial( int locationOfInvokespecial )
	{
		this.locationOfInvokespecial = locationOfInvokespecial;
	}

	@Override
	public String toString()
	{
		return "NewDupInvokespecialTuple [locationOfNewDup=" + locationOfNewDup + ", locationOfInvokespecial=" + locationOfInvokespecial + "]";
	}
}
