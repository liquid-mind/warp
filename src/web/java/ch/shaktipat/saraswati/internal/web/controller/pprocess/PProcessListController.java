package ch.shaktipat.saraswati.internal.web.controller.pprocess;

import java.util.List;

import ch.shaktipat.saraswati.environment.PEnvironmentFactory;
import ch.shaktipat.saraswati.internal.web.model.ListModel;
import ch.shaktipat.saraswati.internal.web.model.pprocess.PProcessListRowModel;
import ch.shaktipat.saraswati.pobject.PProcessHandle;
import ch.shaktipat.saraswati.pobject.manager.PProcessManager;

public class PProcessListController extends PProcessController
{
	@SuppressWarnings( "unchecked" )
	@Override
	protected ListModel< PProcessListRowModel > setupModel()
	{
		String searchParam = getSearchParameter();
		
		// TODO introduce method for searching via multiple fields at once -->
		// use instead of findByName().
		PProcessManager manager = PEnvironmentFactory.createLocal().getPProcessManager();
		List< PProcessHandle > handles = manager.findByName( searchParam );		
		
		ListModel< PProcessListRowModel > model = new ListModel< PProcessListRowModel >();
		
		for ( PProcessHandle handle : handles )
		{
			PProcessListRowModel row = new PProcessListRowModel(
				true,
				handle.getOID(),
				handle.getName(),
				handle.getCreateDate(),
				handle.getOwningPrincipal(),
				getState( handle ),
				getVolatility( handle ) );
			
			model.add( row );
		}
		
		return model;
	}
}
