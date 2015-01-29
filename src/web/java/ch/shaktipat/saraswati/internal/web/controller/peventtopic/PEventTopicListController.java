package ch.shaktipat.saraswati.internal.web.controller.peventtopic;

import java.util.List;

import ch.shaktipat.saraswati.environment.PEnvironmentFactory;
import ch.shaktipat.saraswati.internal.web.model.ListModel;
import ch.shaktipat.saraswati.internal.web.model.peventtopic.PEventTopicListRowModel;
import ch.shaktipat.saraswati.pobject.PEventTopic;
import ch.shaktipat.saraswati.pobject.manager.PEventTopicManager;

public class PEventTopicListController extends PEventTopicController
{
	@SuppressWarnings( "unchecked" )
	@Override
	protected ListModel< PEventTopicListRowModel > setupModel()
	{
		PEventTopicManager manager = PEnvironmentFactory.createLocal().getPEventTopicManager();
		List< PEventTopic > handles = manager.findByName( getSearchParameter() );		
		
		ListModel< PEventTopicListRowModel > model = new ListModel< PEventTopicListRowModel >();
		
		for ( PEventTopic handle : handles )
		{
			PEventTopicListRowModel row = new PEventTopicListRowModel(
				true,
				handle.getOID(),
				handle.getName(),
				handle.getCreateDate() );
			
			model.add( row );
		}
		
		return model;
	}
}
