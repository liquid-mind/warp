package ch.shaktipat.saraswati.internal.web.controller.psharedobject;

import java.util.List;

import ch.shaktipat.saraswati.environment.PEnvironmentFactory;
import ch.shaktipat.saraswati.internal.web.model.ListModel;
import ch.shaktipat.saraswati.internal.web.model.psharedobject.PSharedObjectListRowModel;
import ch.shaktipat.saraswati.pobject.PSharedObjectHandle;
import ch.shaktipat.saraswati.pobject.manager.PSharedObjectManager;

public class PSharedObjectListController extends PSharedObjectController
{
	@SuppressWarnings( "unchecked" )
	@Override
	protected ListModel< PSharedObjectListRowModel > setupModel()
	{
		PSharedObjectManager manager = PEnvironmentFactory.createLocal().getPSharedObjectManager();
		List< PSharedObjectHandle > handles = manager.findByName( getSearchParameter() );		
		
		ListModel< PSharedObjectListRowModel > model = new ListModel< PSharedObjectListRowModel >();
		
		for ( PSharedObjectHandle handle : handles )
		{
			PSharedObjectListRowModel row = new PSharedObjectListRowModel(
				true,
				handle.getOID(),
				handle.getName(),
				handle.getCreateDate() );
			
			model.add( row );
		}
		
		return model;
	}
}
