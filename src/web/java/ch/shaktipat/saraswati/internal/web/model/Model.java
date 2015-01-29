package ch.shaktipat.saraswati.internal.web.model;

public abstract class Model
{
	private boolean commandSuccess;

	public Model( boolean commandSuccess )
	{
		super();
		this.commandSuccess = commandSuccess;
	}

	public boolean getCommandSuccess()
	{
		return commandSuccess;
	}

	public void setCommandSuccess( boolean commandSuccess )
	{
		this.commandSuccess = commandSuccess;
	}
}
