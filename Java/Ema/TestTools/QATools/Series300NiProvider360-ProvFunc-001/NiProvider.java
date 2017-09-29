//APIQA this file is QATools standalone. See qa_readme.txt for details about this tool.
package com.thomsonreuters.ema.examples.training.niprovider.series300.example360__MarketPrice__ConnectionRecovery;

import com.thomsonreuters.ema.access.EmaFactory;
import com.thomsonreuters.ema.access.FieldList;
import com.thomsonreuters.ema.access.GenericMsg;
import com.thomsonreuters.ema.access.Msg;
import com.thomsonreuters.ema.access.OmmException;
import com.thomsonreuters.ema.access.OmmNiProviderConfig;
import com.thomsonreuters.ema.access.OmmProvider;
import com.thomsonreuters.ema.access.OmmProviderClient;
import com.thomsonreuters.ema.access.OmmProviderEvent;
import com.thomsonreuters.ema.access.OmmReal;
import com.thomsonreuters.ema.access.OmmState;
import com.thomsonreuters.ema.access.PostMsg;
import com.thomsonreuters.ema.access.RefreshMsg;
import com.thomsonreuters.ema.access.ReqMsg;
import com.thomsonreuters.ema.access.StatusMsg;
//APIQA
import com.thomsonreuters.ema.access.OmmConsumerConfig.OperationModel;
import com.thomsonreuters.ema.access.ElementList;
import com.thomsonreuters.ema.access.FilterEntry;
import com.thomsonreuters.ema.access.FilterList;
import com.thomsonreuters.ema.access.Map;
import com.thomsonreuters.ema.access.MapEntry;
import com.thomsonreuters.ema.access.OmmArray;
//END APIQA
import com.thomsonreuters.ema.rdm.EmaRdm;

class AppClient implements OmmProviderClient
{

	// APIQA
	static int NUMOFITEMUPDATEFORTEST = 600; // control how long the app is
											 // running
	static boolean USERDISPATCH = false; // test case for diff dispatch mode
										 // with channel set
	static boolean DIRADMINCONTROL = false; // test case admin Control with
											// channel set
	boolean _sendRefreshMsg = false;
	// END APAQA
	boolean _connectionUp;
	boolean isConnectionUp()
	{
		return _connectionUp;
	}

	public void onRefreshMsg(RefreshMsg refreshMsg, OmmProviderEvent event)
	{
		System.out.println("Received Refresh. Item Handle: " + event.handle() + " Closure: " + event.closure());

		System.out.println("Item Name: " + (refreshMsg.hasName() ? refreshMsg.name() : "<not set>"));
		System.out.println("Service Name: " + (refreshMsg.hasServiceName() ? refreshMsg.serviceName() : "<not set>"));

		System.out.println("Item State: " + refreshMsg.state());

		if (refreshMsg.state().streamState() == OmmState.StreamState.OPEN)
		{
			if (refreshMsg.state().dataState() == OmmState.DataState.OK)
			{
				_connectionUp = true;
				// APIQA
				_sendRefreshMsg = true;
				// END APIQA
			}
			else
				_connectionUp = false;
		}
		else
			_connectionUp = false;
	}

	public void onStatusMsg(StatusMsg statusMsg, OmmProviderEvent event)
	{
		System.out.println("Received Status. Item Handle: " + event.handle() + " Closure: " + event.closure());
		System.out.println("Item Name: " + (statusMsg.hasName() ? statusMsg.name() : "<not set>"));
		System.out.println("Service Name: " + (statusMsg.hasServiceName() ? statusMsg.serviceName() : "<not set>"));
		System.out.println("Item State: " + statusMsg.state());
	}

	// APIQA
	boolean sendRefreshMsg()
	{
		return _sendRefreshMsg;
	}

	void sendRefreshMsg(boolean sending)
	{
		_sendRefreshMsg = sending;
	}
	// END APIQA
	public void onGenericMsg(GenericMsg genericMsg, OmmProviderEvent event){}
	public void onAllMsg(Msg msg, OmmProviderEvent event){}
	public void onPostMsg(PostMsg postMsg, OmmProviderEvent providerEvent){}
	public void onReqMsg(ReqMsg reqMsg, OmmProviderEvent providerEvent){}
	public void onReissue(ReqMsg reqMsg, OmmProviderEvent providerEvent){}
	public void onClose(ReqMsg reqMsg, OmmProviderEvent providerEvent){}
	}

	public class NiProvider
	{
	// APIQA
	public static void sendDirRefresh(OmmProvider provider)
	{
		long sourceDirectoryHandle = 1;
		OmmArray capablities = EmaFactory.createOmmArray();
		capablities.add(EmaFactory.createOmmArrayEntry().uintValue(EmaRdm.MMT_MARKET_PRICE));
		capablities.add(EmaFactory.createOmmArrayEntry().uintValue(EmaRdm.MMT_MARKET_BY_PRICE));
		OmmArray dictionaryUsed = EmaFactory.createOmmArray();
		dictionaryUsed.add(EmaFactory.createOmmArrayEntry().ascii("RWFFld"));
		dictionaryUsed.add(EmaFactory.createOmmArrayEntry().ascii("RWFEnum"));
		ElementList serviceInfoId = EmaFactory.createElementList();
		serviceInfoId.add(EmaFactory.createElementEntry().ascii(EmaRdm.ENAME_NAME, "NI_PUB"));
		serviceInfoId.add(EmaFactory.createElementEntry().array(EmaRdm.ENAME_CAPABILITIES, capablities));
		serviceInfoId.add(EmaFactory.createElementEntry().array(EmaRdm.ENAME_DICTIONARYS_USED, dictionaryUsed));
		ElementList serviceStateId = EmaFactory.createElementList();
		serviceStateId.add(EmaFactory.createElementEntry().uintValue(EmaRdm.ENAME_SVC_STATE, EmaRdm.SERVICE_UP));
		FilterList filterList = EmaFactory.createFilterList();
		filterList.add(EmaFactory.createFilterEntry().elementList(EmaRdm.SERVICE_INFO_ID, FilterEntry.FilterAction.SET, serviceInfoId));
		filterList.add(EmaFactory.createFilterEntry().elementList(EmaRdm.SERVICE_STATE_ID, FilterEntry.FilterAction.SET, serviceStateId));
		Map map = EmaFactory.createMap();
		map.add(EmaFactory.createMapEntry().keyUInt(2, MapEntry.MapAction.ADD, filterList));

		RefreshMsg dirRefreshMsg = EmaFactory.createRefreshMsg();
		provider.submit(dirRefreshMsg.domainType(EmaRdm.MMT_DIRECTORY).filter(EmaRdm.SERVICE_INFO_FILTER | EmaRdm.SERVICE_STATE_FILTER).payload(map).complete(true), sourceDirectoryHandle);
	}

	public static void printHelp(boolean reflect)
	{
		if (!reflect)
		{
			System.out.println("\nOptions:\n" + "  -?\tShows this usage\n\n" + "  -numOfUpdatesForApp \tSend the number of item updates for the whole test [default = 600]\n"
					+ "  -userDispatch \tUse UserDispatch Operation Model [default = false]\n" + "  -dirAdminControl \tSet if user controls sending directory msg [default = false]\n" + "\n");

			System.exit(-1);
		}
		else
		{
			System.out.println("\n  Options will be used:\n" + "  -numOfUpdatesForApp \t " + AppClient.NUMOFITEMUPDATEFORTEST + "\n" + "  -userDispatch \t " + AppClient.USERDISPATCH + "\n"
					+ "  -dirAdminControl \t " + AppClient.DIRADMINCONTROL + "\n" + "\n");
		}
	}

	public static boolean readCommandlineArgs(String[] argv)
	{
		int count = argv.length;
		int idx = 0;

		while (idx < count)
		{
			if (0 == argv[idx].compareTo("-?"))
			{
				printHelp(false);
				return false;
			}
			else if (0 == argv[idx].compareToIgnoreCase("-numOfUpdatesForApp"))
			{
				if (++idx >= count)
				{
					printHelp(false);
					return false;
				}
				AppClient.NUMOFITEMUPDATEFORTEST = Integer.parseInt(argv[idx]);
				++idx;
			}
			else if (0 == argv[idx].compareToIgnoreCase("-userDispatch"))
			{
				if (++idx >= count)
				{
					printHelp(false);
					return false;
				}
				AppClient.USERDISPATCH = ((argv[idx].compareToIgnoreCase("TRUE") == 0) ? true : false);
				++idx;
			}
			else if (0 == argv[idx].compareToIgnoreCase("-dirAdminControl"))
			{
				if (++idx >= count)
				{
					printHelp(false);
					return false;
				}
				AppClient.DIRADMINCONTROL = ((argv[idx].compareToIgnoreCase("TRUE") == 0) ? true : false);
				++idx;
			}
			else
			{
				printHelp(false);
				return false;
			}
		}

		printHelp(true);
		return true;
	}

	// END APIQA
	public static void main(String[] args)
	{
		AppClient appClient = new AppClient();
		OmmProvider provider = null;
		try
		{
			if (!readCommandlineArgs(args))
				return;
			OmmNiProviderConfig config = EmaFactory.createOmmNiProviderConfig();
			// APIQA
			if (appClient.USERDISPATCH)
				config.operationModel(OperationModel.USER_DISPATCH);
			if (appClient.DIRADMINCONTROL)
				config.adminControlDirectory(OmmNiProviderConfig.AdminControl.USER_CONTROL);
			provider = EmaFactory.createOmmProvider(config.username("user"), appClient);
			if (appClient.DIRADMINCONTROL)
				sendDirRefresh(provider);
			if (appClient.USERDISPATCH)
				provider.dispatch(1000000);
			// END APIQA
			long itemHandle = 6;
			FieldList fieldList = EmaFactory.createFieldList();
			fieldList.add(EmaFactory.createFieldEntry().real(22, 14400, OmmReal.MagnitudeType.EXPONENT_NEG_2));
			fieldList.add(EmaFactory.createFieldEntry().real(25, 14700, OmmReal.MagnitudeType.EXPONENT_NEG_2));
			fieldList.add(EmaFactory.createFieldEntry().real(30, 9, OmmReal.MagnitudeType.EXPONENT_0));
			fieldList.add(EmaFactory.createFieldEntry().real(31, 19, OmmReal.MagnitudeType.EXPONENT_0));
			provider.submit(EmaFactory.createRefreshMsg().serviceName("NI_PUB").name("TRI.N")
					.state(OmmState.StreamState.OPEN, OmmState.DataState.OK, OmmState.StatusCode.NONE, "UnSolicited Refresh Completed").payload(fieldList).complete(true), itemHandle);
			// APIQA
			
			if (appClient.USERDISPATCH)
				provider.dispatch(1000000);
			appClient.sendRefreshMsg(false);
			for (int i = 0; i < appClient.NUMOFITEMUPDATEFORTEST; i++)
			// END APIQA
			{
				if (appClient.isConnectionUp())
				{
					// APIQA
					if (appClient.sendRefreshMsg())
					{
						if (appClient.USERDISPATCH)
							provider.dispatch(1000000);
						fieldList.clear();
						fieldList.add(EmaFactory.createFieldEntry().real(22, 14400 + i, OmmReal.MagnitudeType.EXPONENT_NEG_2));
						fieldList.add(EmaFactory.createFieldEntry().real(25, 14700 + i, OmmReal.MagnitudeType.EXPONENT_NEG_2));
						fieldList.add(EmaFactory.createFieldEntry().real(30, 10 + i, OmmReal.MagnitudeType.EXPONENT_0));
						fieldList.add(EmaFactory.createFieldEntry().real(31, 19 + i, OmmReal.MagnitudeType.EXPONENT_0));
						try
						{
							provider.submit(EmaFactory.createRefreshMsg().serviceName("NI_PUB").name("TRI.N")
									.state(OmmState.StreamState.OPEN, OmmState.DataState.OK, OmmState.StatusCode.NONE, "UnSolicited Refresh Completed").payload(fieldList).complete(true), itemHandle);
						}
						catch (OmmException excp)
						{
							System.out.println(excp.getMessage());
						}
						appClient.sendRefreshMsg(false);
						// END APIQA
					}
					else
					{
						fieldList.clear();
						fieldList.add(EmaFactory.createFieldEntry().real(22, 14400 + i, OmmReal.MagnitudeType.EXPONENT_NEG_2));
						fieldList.add(EmaFactory.createFieldEntry().real(30, 10 + i, OmmReal.MagnitudeType.EXPONENT_0));
						// APIQA
						try
						{
							provider.submit(EmaFactory.createUpdateMsg().serviceName("NI_PUB").name("TRI.N").payload(fieldList), itemHandle);
						}
						catch (OmmException excp)
						{
							System.out.println(excp.getMessage());
							Thread.sleep(1000);
						}
					}
				}
				try
				{
					if (appClient.USERDISPATCH)
						provider.dispatch(1000000);
				}
				catch (OmmException excp)
				{
					System.out.println(excp.getMessage());
				}
				Thread.sleep(1000);
			}
		}
		catch (InterruptedException | OmmException excp)
		// END APIQA
		{
			System.out.println(excp.getMessage());
		}
		finally
		{
			if (provider != null)
				provider.uninitialize();
		}
	}
}
