///*|-----------------------------------------------------------------------------
// *|            This source code is provided under the Apache 2.0 license      --
// *|  and is provided AS IS with no warranty or guarantee of fit for purpose.  --
// *|                See the project's LICENSE.md for details.                  --
// *|           Copyright Thomson Reuters 2015. All rights reserved.            --
///*|-----------------------------------------------------------------------------

package com.thomsonreuters.upa.valueadd.reactor;

/** Identifies which type of ReactorEvent the ComponentEvent contains. */
public enum TestReactorEventTypes {
	CHANNEL_EVENT,	           /** ReactorChannelEvent */
	LOGIN_MSG,		           /** RDMLoginMsgEvent */
	DIRECTORY_MSG,	           /** RDMDirectoryMsgEvent */
	DICTIONARY_MSG,	           /** RDMDictionaryMsgEvent */
	MSG,				       /** ReactorMsgEvent */
	TUNNEL_STREAM_STATUS,      /** TunnelStreamStatusEvent */
	TUNNEL_STREAM_MSG,         /** TunnelStreamMsgEvent */
	TUNNEL_STREAM_QUEUE_MSG,   /** TunnelStreamQueueMsgEvent */
	TUNNEL_STREAM_REQUEST,     /** TunnelStreamRequestEvent */
}
