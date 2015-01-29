package ch.shaktipat.saraswati.internal.web.controller.peventqueue;

import java.util.List;

import ch.shaktipat.saraswati.environment.PEnvironmentFactory;
import ch.shaktipat.saraswati.internal.web.model.ListModel;
import ch.shaktipat.saraswati.internal.web.model.peventqueue.PEventQueueListRowModel;
import ch.shaktipat.saraswati.pobject.PEventQueue;
import ch.shaktipat.saraswati.pobject.manager.PEventQueueManager;

public class PEventQueueListController extends PEventQueueController
{
	@SuppressWarnings( "unchecked" )
	@Override
	protected ListModel< PEventQueueListRowModel > setupModel()
	{
		PEventQueueManager manager = PEnvironmentFactory.createLocal().getPEventQueueManager();
		List< PEventQueue > handles = manager.findByName( getSearchParameter() );		
		
		ListModel< PEventQueueListRowModel > model = new ListModel< PEventQueueListRowModel >();
		
		for ( PEventQueue handle : handles )
		{
			PEventQueueListRowModel row = new PEventQueueListRowModel(
				true,
				handle.getOID(),
				handle.getName(),
				handle.getCreateDate() );
			
			model.add( row );
		}
		
		return model;
	}
}
